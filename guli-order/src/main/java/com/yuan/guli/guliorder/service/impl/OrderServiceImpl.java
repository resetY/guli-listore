package com.yuan.guli.guliorder.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yuan.common.to.MemberRespVo;
import com.yuan.common.to.OrderTo;
import com.yuan.common.utils.R;
import com.yuan.guli.guliorder.constant.OrderConstant;
import com.yuan.guli.guliorder.dao.OrderItemDao;
import com.yuan.guli.guliorder.entity.OrderItemEntity;
import com.yuan.guli.guliorder.entity.PaymentInfoEntity;
import com.yuan.guli.guliorder.enume.OrderStatusEnum;
import com.yuan.guli.guliorder.feign.CartFeignService;
import com.yuan.guli.guliorder.feign.MemberFeignService;
import com.yuan.guli.guliorder.feign.ProductFeignService;
import com.yuan.guli.guliorder.feign.WareFeignService;
import com.yuan.guli.guliorder.interceptor.LoginUserInterceptor;
import com.yuan.guli.guliorder.service.OrderItemService;
import com.yuan.guli.guliorder.service.PaymentInfoService;
import com.yuan.guli.guliorder.to.WareSkuLockTo;
import com.yuan.guli.guliorder.vo.*;
//import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuan.common.utils.PageUtils;
import com.yuan.common.utils.Query;

import com.yuan.guli.guliorder.dao.OrderDao;
import com.yuan.guli.guliorder.entity.OrderEntity;
import com.yuan.guli.guliorder.service.OrderService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.swing.text.html.parser.Entity;
import javax.xml.crypto.Data;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    @Autowired
    RabbitTemplate rabbitTemplate;
    /**
     * 关闭订单操作
     * */
    @Override
    public void closeOrder(OrderEntity orderEntity){
        OrderEntity order = baseMapper.selectOne(new QueryWrapper<OrderEntity>().eq("order_sn", orderEntity.getOrderSn()));
        if(order.getStatus()==OrderStatusEnum.CREATE_NEW.getCode()){ //订单状态为待付款
                //1.关闭订单
            //这里最好new一个信的order进行update，因为并发场景下，上面的老order数据可能有误
            //update只修改对应的注入的数据
            OrderEntity up_order = new OrderEntity();
            up_order.setId(order.getId());
            up_order.setStatus(OrderStatusEnum.CANCLED.getCode());      //修改订单状态为关闭
            this.updateById(up_order);

            //2.修改订单信息状态成功后，最新订单信息发给mq
            OrderEntity new_order = this.getById(up_order.getId());
            OrderTo new_orderTo = new OrderTo();
            BeanUtils.copyProperties(new_order,new_orderTo);

            //todo：如何保证消息可靠性：使消息一定有发送出去

            try{
                rabbitTemplate.convertAndSend("order-event-exchange",
                        "order.release.order",
                        new_orderTo);
            }catch (Exception e){

            }
        }
    }



    /**
     * 获取支付信息
     * **/
    @Autowired
    OrderItemDao orderItemDao;
    @Override
    public PayVo getOrderPay(String orderSn) {

         //1.根据订单号查找订单：
        OrderEntity order = baseMapper.selectOne(new QueryWrapper<OrderEntity>().eq("order_sn", orderSn));
        List<OrderItemEntity> order_sn = orderItemDao.selectList(new QueryWrapper<OrderItemEntity>().eq("order_sn", order.getOrderSn()));
        OrderItemEntity orderItem = order_sn.get(0);

        //2.构建结算订单：
        PayVo payVo = new PayVo();
        //对外交易号注入：订单号
        payVo.setOut_trade_no(orderSn) ;
        payVo.setBody(orderItem.getSkuAttrsVals()); //订单备注
        payVo.setSubject("商城交易:"+orderItem.getSkuName());//订单标题:随便抽取一个订单项的标题吧
        //注入支付价格：注意：需要将价格小数修改未2位，支付宝才能识别
        //且有小数进行向上取值L；
        String pay_price = order.getPayAmount().setScale(2, BigDecimal.ROUND_UP)
                .toString();
        payVo.setTotal_amount(pay_price);

        return  payVo;
    }


/**
 *分页展示订单数据
 * **/
    @Override
    public PageUtils queryOrderList(Map<String, Object> params) {
        //1.获取到session中的用户信息：
        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();

        QueryWrapper<OrderEntity> wrapper = new QueryWrapper<>();


        //2.查询当前用户的订单
        //根据用户id查询，且根据订单创建时间进行排序
        wrapper.eq("member_id",memberRespVo.getId());
        wrapper.orderByDesc("create_time");

        IPage <OrderEntity>page = this.page(
                new Query<OrderEntity>().getPage(params),
                wrapper
              );
        //获取每一个订单，查询订单项然后注入
        List<OrderEntity> collect = page.getRecords().stream().map(item -> {
            List<OrderItemEntity> orderLists =
                    orderItemService.list(new QueryWrapper<OrderItemEntity>()
                            .eq("order_sn", item.getOrderSn()));
            item.setItems(orderLists);
            return item;
        }).collect(Collectors.toList());
        //保存到分页内
        page.setRecords(collect);
        return new PageUtils(page);
    }


    /**
     * 处理支付宝的支付结果：
     * 保存交易流水
     * **/
    @Autowired
    PaymentInfoService paymentInfoService;
    @Override
    public String handlerPayResult(PayAsyncVo payVo) {
        //1.保存交易流水
        PaymentInfoEntity payinfo = new PaymentInfoEntity();
        payinfo.setAlipayTradeNo(payinfo.getAlipayTradeNo()); //支付宝交易号
        payinfo.setOrderSn(payVo.getOut_trade_no());
        payinfo.setPaymentStatus(payVo.getTrade_status()); //交易状态
        payinfo.setCallbackTime(payVo.getNotify_time()); //回调时间
        payinfo.setCreateTime(new Date());

        //注意：需要在数据库中给订单号和流水号进行唯一设置(最好这样)
        paymentInfoService.save(payinfo);

        //2.修改订单状态信息
        if(payinfo.getPaymentStatus().equals("TRADE_SUCCESS") || payinfo.getPaymentStatus().equals("TRADE_FINISHED")){
            Integer code = OrderStatusEnum.PAYED.getCode();
            //将订单状态修改为已付款状态：
            updateOrderStatus(payinfo.getOrderSn(),code);

        }
        return "success";
    }


    /**
     * 修改订单状态
     * **/
    private void updateOrderStatus(String ordrSn,Integer code){

        this.baseMapper.updateStatus(ordrSn,code);
    }

    @Autowired
  MemberFeignService memberFeignService;
    @Autowired
    CartFeignService cartFeignService;

    @Autowired
    ThreadPoolExecutor executor; //线程池构建
    @Autowired
    WareFeignService wareFeignService;
    @Autowired
    StringRedisTemplate redisTemplate;



    private ThreadLocal<OrderSubmitVo>confirmVoThreadLocal = new ThreadLocal<>();
    @Transactional
    @Override
    public OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException {

        RequestAttributes old_req = RequestContextHolder.getRequestAttributes();

        OrderConfirmVo orderConfirmVo = new OrderConfirmVo();

//        MemberRespVo user = (MemberRespVo) req.getSession().getAttribute("userLogin");


        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();//从拦截器中获取会员id
        CompletableFuture<Void> getAddress = CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(old_req); //将主线程拥有的请求头数据，放入异步线程中

            //1.远程查询用户的所有地址：
            List<OrderConfirmVo.MemberAddressVo> address = memberFeignService.getAddress(memberRespVo.getId());
            orderConfirmVo.setAddress(address);
            System.out.println("地址数据："+orderConfirmVo.getAddress());
        }, executor);


        CompletableFuture<Void> getCartItem = CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(old_req); //将主线程拥有的请求头数据，放入异步线程中
            //2.远程查询购物车所有选中的购物项
            List<OrderConfirmVo.OrderItemVo> cartItems = cartFeignService.getItems();
            //feign在远程调用之前需要构造请求，会调用很多的拦截器
            //在原本的order请求头中，存在用户cookie，但是如果是远程调用，那么会创建一个新的没有数据的请求模板
            orderConfirmVo.setItems(cartItems);
        }, executor).thenRunAsync(()->{  //接着远程查询每个商品是否有库存
            RequestContextHolder.setRequestAttributes(old_req); //将主线程拥有的请求头数据，放入异步线程中
            List<OrderConfirmVo.OrderItemVo> items = orderConfirmVo.getItems();
            List<Long> product_id = items.stream().map(item -> {
                return item.getSkuId();
            }).collect(Collectors.toList());
            R r = wareFeignService.getHasStock(product_id);
            //data:是否有库存和当前的skuid
            List<SkuHasStockVo> data = r.getData(new TypeReference<List<SkuHasStockVo>>() {
            });
            if(data!=null) { //正常方式：需要一个个进行item的库存判断和改变
                Map<Long, Boolean> stocksMap = new HashMap<>();
                for (SkuHasStockVo d : data) {
                    //思路：在vo添加一个map<Long,Boolean>的键值对集合来存储库存状态
                    //然后通过页面的skuid进行判断是否有货显示在页面上
                    stocksMap.put(d.getSkuId(),d.getStock());
                }
                orderConfirmVo.setStocks(stocksMap);
            }
        },executor);


        CompletableFuture<Void> getInt = CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(old_req); //将主线程拥有的请求头数据，放入异步线程中
            //3.优惠券信息远程查询(改为查询用户积分)
            Integer integration = memberRespVo.getIntegration();
            orderConfirmVo.setIntegeration(integration);//保存积分
        }, executor);

        //4.总价和应付价格在值对象的get里面进行重新计算

        CompletableFuture.allOf(getAddress,getCartItem,getInt).get();

        //5.todo 防重复令牌的使用
        String token = UUID.randomUUID().toString().replace("-","");
        orderConfirmVo.setOrderToken(token); //页面的token
        /**
         * 用户令牌的key:order:token:(userId),uuid   服务端的token
         *   并且设置令牌的时间为30分钟
         * **/
        redisTemplate.opsForValue().set(OrderConstant.USER_ORDER_TOKEN_PREFIX+memberRespVo.getId(),token,
                30, TimeUnit.MINUTES);

        return orderConfirmVo;
    }

    /**
     * 订单提交方法
     * **/
    //@GlobalTransactional
    @Transactional
//    @GlobalTransactional
//    @GlobalTransactional  //全局事务
    @Override
    public SubmitResponseVo submitOrder(OrderSubmitVo submitOrder) {

        SubmitResponseVo responseVo = new SubmitResponseVo();
        confirmVoThreadLocal.set(submitOrder);
        responseVo.setCode(0); //0为正常
        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();
        String orderToken = submitOrder.getOrderToken(); //前端传过来的令牌

        //服务端的token进行获取
        //1.令牌的验证【使用lua脚本来进行获取的同时删除令牌，保证原子性】
        //令牌不存在返回0，存在且删除失败也返回0，存在且删除成功返回1
        String script= "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";        //参数1：类型和脚本语句 参数2：redis的key   参数3:要对比的数据
        String redis_user_key= OrderConstant.USER_ORDER_TOKEN_PREFIX + memberRespVo.getId();

        Long result = redisTemplate.execute(new DefaultRedisScript<Long>(script,Long.class),
                Arrays.asList(redis_user_key),
                orderToken);

        System.out.println(result);
    if(result==1L){ //令牌验证成功
        //1.创建订单
        OrderCreateTo order = createOrder();
        System.out.println(order);

        //2.价格校验:差价小于0.01即可校验成功
        BigDecimal payAmount = order.getOrder().getPayAmount();
        BigDecimal payPrice = submitOrder.getPayPrice(); //页面传输过来的价格
      if(Math.abs(payAmount.subtract(payPrice).doubleValue())<0.01){
            //3.保存订单到数据库
          saveOrder(order);
          //4.库存锁定，有异常则进行订单的回滚
          WareSkuLockTo skuLockTo = new WareSkuLockTo();
          skuLockTo.setOrderSn(order.getOrder().getOrderSn());
          List<OrderConfirmVo.OrderItemVo> orderItemVoList = order.getItems().stream().map(item -> {
              OrderConfirmVo.OrderItemVo orderItemVo = new OrderConfirmVo.OrderItemVo();
              orderItemVo.setSkuId(item.getSkuId());
              orderItemVo.setCount(item.getSkuQuantity());
              orderItemVo.setTitle(item.getSkuName());
              return orderItemVo;
          }).collect(Collectors.toList());
          skuLockTo.setLocks(orderItemVoList);

          //wareFeignService.stockLock：远程操作锁定库存
          R r = wareFeignService.stockLock(skuLockTo);
          if(r.getCode()==0){ //锁库存成功
              //int i = 10/0; //库存锁成功后出现的错误，可回滚
              responseVo.setOrder(order.getOrder());

              //todo:订单创建完毕后，给rabbit发送消息
              rabbitTemplate.convertAndSend("order-event-exchange",
                      "order.create.order",
                      order.getOrder());
                return responseVo;
          }else{ //锁定失败
              responseVo.setCode(3);
          }
      }else{  //验价失败
          responseVo.setCode(2);
          return responseVo;
      }
        return responseVo;
    }else{
        responseVo.setCode(1); //令牌验证失败
        return null;
    }


    }

    @Override
    public OrderEntity getOrderBySn(String orderSn) {
        OrderEntity orderEntity = baseMapper.selectOne(new QueryWrapper<OrderEntity>().eq("order_sn", orderSn));
    return orderEntity;
    }

    @Autowired
    OrderItemService orderItemService;



    private void saveOrder(OrderCreateTo order) {
        //拦截器中获取用户
        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();


        OrderEntity orderEntity = order.getOrder();

        //注入用户id和昵称
        orderEntity.setMemberId(memberRespVo.getId());
        orderEntity.setMemberUsername(memberRespVo.getNickname());

        orderEntity.setCreateTime(new Date());
        orderEntity.setModifyTime(new Date());


        List<OrderItemEntity> items = order.getItems();
       baseMapper.insert(orderEntity); //保存订单信息
        //同时保存订单项
      // orderItemService.saveBatch(items);
        //由于seata版本为0.7，会和mybatis产生bug，无法进行批量保存，这里修改为逐个保存
        for (OrderItemEntity item : items) {
            orderItemService.save(item);
        }

    }

    /**
     * 创建订单:内部调用构建订单数据的方法build
     * */
private OrderCreateTo createOrder(){
OrderCreateTo orderCreateTo = new OrderCreateTo();
//1.生成订单号，然后注入订单号和地址信息等数据
    String timeId = IdWorker.getTimeId();//mybatisplus生成一个时间id，可用来代表订单号
    OrderEntity order = buildOrder(timeId);

    //2.所有订单项获取和注入
    List<OrderItemEntity> orderItems = buildOrderItems(timeId);
    System.out.println(orderItems);
    //3.最终价格注入
    //思路：计算叠加每个订单项的价格，最后注入到订单内
    computePrice(order,orderItems);


    order.setCreateTime(new Date());

    orderCreateTo.setOrder(order);//保存
    orderCreateTo.setItems(orderItems);
    return orderCreateTo;
}

/**
 * 价格校验注入
 * @param  order 订单对象
 * @param  orderItems 所有订单项
 *
 * **/
    private void computePrice(OrderEntity order, List<OrderItemEntity> orderItems) {
       BigDecimal total = new BigDecimal("0.0");

        BigDecimal promotion = new BigDecimal("0.0");
        BigDecimal intergration = new BigDecimal("0.0");
        BigDecimal coupon = new BigDecimal("0.0");

        //积分注入
        int gift  = 0; //积分
        int growth = 0; //成长值

        for (OrderItemEntity Item : orderItems) {
            intergration = intergration.add(Item.getIntegrationAmount());
          coupon = coupon.add(Item.getCouponAmount());
           promotion = promotion.add(Item.getPromotionAmount()); //每个订单项的优惠金额

       total = total.add(Item.getRealAmount());

          //叠加每个订单项的积分和成长值信息
           growth= growth+ Item.getGiftGrowth();
           gift = gift+ Item.getGiftIntegration();

        }
            order.setTotalAmount(total);//订单总额注入
        BigDecimal payPrice = order.getTotalAmount().add(order.getFreightAmount());
        order.setPayAmount(payPrice);//应付总而注入：订单 -  运费

        //优惠信息注入完善
        order.setPromotionAmount(promotion); //注入优惠价格总额
        order.setIntegrationAmount(intergration);
        order.setCouponAmount(coupon);

        //注入订单总成长值和积分信息
        order.setGrowth(growth);
        order.setIntegration(gift);

    }

    /**
 * 构建所有订单项
 * @param orderSn 订单号保存，订单项目的订单号跟着订单的一样
 * **/
    private List<OrderItemEntity> buildOrderItems(String orderSn) {
        //最后确定每个购物项的价格
        List<OrderConfirmVo.OrderItemVo> items = cartFeignService.getItems();
        if(items!=null && items.size()>0){
            List<OrderItemEntity> collect = items.stream().map(item -> {

                OrderItemEntity orderItem = bulidOrderItem(item); //使用构建订单项数据方法
                orderItem.setOrderSn(orderSn);
                return orderItem;
            }).collect(Collectors.toList());
            System.out.println(collect);
            return collect;
        }else{
            return null;
        }
    }

    /**
 * 构建订单
 * @param timeId 传入的要保存的订单id
 * **/
    private OrderEntity buildOrder(String timeId) {
        OrderEntity order = new OrderEntity();
        order.setOrderSn(timeId);

        OrderSubmitVo orderSubmitVo = confirmVoThreadLocal.get();
        R r= wareFeignService.getFare(orderSubmitVo.getAddId()); //传入收货地址id

        //收货地址信息和运费获取，然后注入
        FareVo fareVo = r.getData(new TypeReference<FareVo>() {});
        order.setFreightAmount(fareVo.getFarePrice()); //保存运费信息

        OrderConfirmVo.MemberAddressVo fareAddress = fareVo.getAddress(); //地址信息
        order.setReceiverName(fareAddress.getName());
        order.setReceiverPhone(fareAddress.getPhone());
        order.setReceiverProvince(fareAddress.getProvince()); //省-城市-区-详细地址
        order.setReceiverCity(fareAddress.getCity());
        order.setReceiverRegion(fareAddress.getRegion());
        order.setReceiverDetailAddress(fareAddress.getDetailAddress());
        order.setReceiverPostCode(fareAddress.getPostCode()); //邮编

        order.setStatus(OrderStatusEnum.CREATE_NEW.getCode()); //订单状态：未付款
        return order;
    }

    @Autowired
    ProductFeignService productFeignService;
    /**
 * 构建订单项数据
 * **/
private OrderItemEntity bulidOrderItem(OrderConfirmVo.OrderItemVo orderItemVo){
        OrderItemEntity orderItemEntity = new OrderItemEntity();
        //1.订单信息：订单号
    //2.商品sku信息
    orderItemEntity.setSkuId(orderItemVo.getSkuId());
    orderItemEntity.setSkuName(orderItemVo.getTitle());
    orderItemEntity.setSkuPic(orderItemVo.getImage()); //插入图片信息
    orderItemEntity.setSkuPrice(orderItemVo.getPrice());
    orderItemEntity.setSkuQuantity(orderItemVo.getCount()); //商品购买的数量

    List<String> attr = orderItemVo.getSkuAttr();
    String str_attr = StringUtils.collectionToDelimitedString(attr, ";");//每一属性使用分号隔开
    orderItemEntity.setSkuAttrsVals(str_attr);

    //3.商品spu信息
    R r = productFeignService.getSpuInfoByskuId(orderItemEntity.getSkuId());
    SpuInfoTo spuinfodata = r.getData(new TypeReference<SpuInfoTo>() {
    });

    orderItemEntity.setSpuId(spuinfodata.getId()); //需要通过skuid查询的skuinfo查询到spuid
    orderItemEntity.setSpuName(spuinfodata.getSpuName());
    orderItemEntity.setCategoryId(spuinfodata.getCatalogId());
    orderItemEntity.setSpuBrand(spuinfodata.getBrandId().toString());//通过spuinfo表的品牌id查询品牌名称 或者 直接存入品牌id


    //4.优惠信息：优惠模块不存在

    //5.每个订单项得到的会员成长积分数据：思路：后续可使用价格判断，来对应注入相应的固定积分
    Double s = 50 + Math.random() * (100 - 50 + 1); //50到100的随机积分
    Integer jifen = s.intValue();
    orderItemEntity.setGiftGrowth(jifen); //注入积分，这里先价格决定积分
        orderItemEntity.setGiftIntegration(jifen);//购物积分

    //6.订单项价格优惠信息：暂时都为0
    orderItemEntity.setIntegrationAmount(new BigDecimal("0"));
    orderItemEntity.setCouponAmount(new BigDecimal("0"));
    orderItemEntity.setPromotionAmount(new BigDecimal("0"));

    BigDecimal realPrice = orderItemEntity.getSkuPrice().multiply(new BigDecimal(orderItemEntity.getSkuQuantity().toString()));
    //用价格减去优惠信息，获取最终价格
    BigDecimal youhui = orderItemEntity.getIntegrationAmount().add(orderItemEntity.getCouponAmount().add(orderItemEntity.getPromotionAmount()));
    realPrice.subtract(youhui);
    orderItemEntity.setRealAmount(realPrice); //当前项的实际总价格

    System.out.println(orderItemEntity);
        return orderItemEntity; //订单项数据构建完毕
}
}