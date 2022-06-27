package com.yuan.guli.guliware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.yuan.common.to.OrderTo;
import com.yuan.common.to.rmq.StockLockedTo;
import com.yuan.common.utils.R;
import com.yuan.guli.guliware.entity.WareOrderTaskDetailEntity;
import com.yuan.guli.guliware.entity.WareOrderTaskEntity;
import com.yuan.guli.guliware.error.NoStockException;
import com.yuan.guli.guliware.feign.OrderFeignService;
import com.yuan.guli.guliware.feign.ProductFeign;
import com.yuan.guli.guliware.service.WareOrderTaskDetailService;
import com.yuan.guli.guliware.service.WareOrderTaskService;
import com.yuan.guli.guliware.to.OrderEntityTo;
import com.yuan.guli.guliware.to.OrderItemTo;
import com.yuan.guli.guliware.to.WareSkuLockTo;
import com.yuan.guli.guliware.vo.SkuHasStockVo;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuan.common.utils.PageUtils;
import com.yuan.common.utils.Query;

import com.yuan.guli.guliware.dao.WareSkuDao;
import com.yuan.guli.guliware.entity.WareSkuEntity;
import com.yuan.guli.guliware.service.WareSkuService;
import org.springframework.transaction.annotation.Transactional;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {


@Autowired
    OrderFeignService orderFeignService;


    /**
     * 用于进行库存的数量解锁(将数量修改到之前的状态)
     * @param  skuId 商品id
     * @param  wareId 仓库id
     * @param num  锁住的数量，则需要回滚的数量
     * **/
    @Transactional
    void unLockStock(Long skuId,Long wareId,Integer num,Long taskDid){
        //1.解锁库存
    wareSkuDao.unLockStock(skuId,wareId,num);

        //2.改变工作单详情状态为：2  解锁
            WareOrderTaskDetailEntity entity = new WareOrderTaskDetailEntity();
        entity.setId(taskDid);
        entity.setLockStatus(2); //解锁状态
        wareOrderTaskDetailService.updateById(entity);
    }


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                new QueryWrapper<WareSkuEntity>()
        );

        return new PageUtils(page);
    }

    /**
     *库存模糊检索
     * **/
    @Override
    public PageUtils querySkuWarePage(Map<String, Object> params) {

     String skuId= (String) params.get("skuId");
     String wareId  = (String) params.get("wareId");
        QueryWrapper<WareSkuEntity> wrapper = new QueryWrapper();

        if(!StringUtils.isEmpty(skuId)){
            wrapper.eq("sku_id",skuId);
        }
        if(!StringUtils.isEmpty(wareId)){
            wrapper.eq("ware_id",wareId);
        }

        IPage<WareSkuEntity> page= this.page(
                new Query<WareSkuEntity>().getPage(params),
                wrapper);
        return new PageUtils(page);
    }

    /**
     * 商品入库
     * */
    @Autowired
    ProductFeign productFeign;
    @Transactional
    @Override
    public void addStock(Long skuId, Long wareId, Integer stock) {
        QueryWrapper<WareSkuEntity> wrapper = new QueryWrapper();
        wrapper.eq("sku_id",skuId).eq("ware_id",wareId);
        List<WareSkuEntity> wareSkuEntities = baseMapper.selectList(wrapper);
        if(wareSkuEntities==null || wareSkuEntities.size()==0){ //如果没有库存记录，新增操作
            WareSkuEntity newSkuEntity = new WareSkuEntity();
            newSkuEntity.setWareId(wareId);
            newSkuEntity.setSkuId(skuId);
            newSkuEntity.setStock(stock);
            newSkuEntity.setStockLocked(0);

            //远程调用，根据skuid查询skuname
        try{    R info = productFeign.info(skuId);
            Map<String,Object> data = (Map<String, Object>) info.get("data");
            if(info.getCode()==0){
                newSkuEntity.setSkuName((String) data.get("skuName")); //注入名字
            }
        }catch (Exception e){

        }
            this.save(newSkuEntity); //新增库存
        }
        List<WareSkuEntity> collect = wareSkuEntities.stream().map(waresku -> {
            Integer oldstock = waresku.getStock();
            Integer newstock = oldstock + stock;
            waresku.setStock(newstock);
            return waresku;
        }).collect(Collectors.toList());


        this.updateBatchById(collect);//修改
    }

    /**
     * 被远程调用功能的业务层：
     * **/
    @Override
    public List<SkuHasStockVo> getHasStock(List<Long> skuIds) {
        System.out.println("skuid集合："+skuIds);
        List<SkuHasStockVo> stockVoList = skuIds.stream().map(id -> {
            //库存 =  相同skuid所有库存 - 相同skuid所有被占用(被购买的)
           Long sumstock =  baseMapper.getStockAndskuId(id);
            System.out.println("库存总数是："+sumstock);
            if(sumstock == null){
                sumstock = 0L;
            }
            SkuHasStockVo skuHasStockVo = new SkuHasStockVo();
            skuHasStockVo.setStock(sumstock>0?true:false);
            skuHasStockVo.setSkuId(id);
            return skuHasStockVo;
        }).collect(Collectors.toList());
return stockVoList;
    }

    @Autowired
    WareSkuDao wareSkuDao;
    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     *  锁库存的同时，还需要对库存工作单进行保存
     *  通过库存工作单我们可知道，哪些仓库哪些商品被锁了库存
     * **/
    @Autowired
    WareOrderTaskDetailService wareOrderTaskDetailService;

    @Autowired
    WareOrderTaskService wareOrderTaskService;
    /**
     * 锁定库存方法：
     * **/
    /***
     *1.  库存自动解锁(不使用分布式事务)
     *  情况1：订单构建成功、库存锁定成功后，出现的异常，需要对锁定的仓库的商品进行 解锁回滚
     *  情况2：库存有商品锁定失败
     * **/
    @Transactional
    @Override
    public Boolean stockLock(WareSkuLockTo to) {

        //将构建的订单数据，注入到工作单内
        WareOrderTaskEntity wareTask = new WareOrderTaskEntity();
        wareTask.setOrderSn(to.getOrderSn()); //保存订单号到工作单
        wareTask.setCreateTime(new Date());
        wareOrderTaskService.save(wareTask); //保存到数据库


        //实际业务：1.按照下单的收货地址，查找就近的仓库来锁定库存
        Boolean allStock = true;
        //1.找到每个商品在哪个仓库存在库存
        List<OrderItemTo> locks = to.getLocks();
        List<SkuWareHasStock> collect = locks.stream().map(item -> {
            Long skuId = item.getSkuId();

            SkuWareHasStock skuWareHasStock = new SkuWareHasStock();
            skuWareHasStock.setSkuId(skuId);
            skuWareHasStock.setLockNum(item.getCount());
            skuWareHasStock.setSkuName(item.getTitle());

            List<Long> wareIds = wareSkuDao.getWareIdStock(skuId); //获取仓库
            skuWareHasStock.setWareIds(wareIds); //注入可获取的仓库
            return skuWareHasStock;
        }).collect(Collectors.toList());

        //2.开始锁定库存
        for (SkuWareHasStock hasStock : collect) {
            Boolean stockSku = false;
            Long skuId = hasStock.getSkuId();
            String skuName = hasStock.getSkuName();
            List<Long> wareIds = hasStock.getWareIds();
            if(wareIds==null || wareIds.size()==0){ //仓库不存在库存
                throw new NoStockException(skuId,skuName);
            }
            /**
             *  逻辑和问题：
             *  1. 每一个商品都锁定成功，将当前构建完毕的工作单和工作单详情的id发给mq
             *  2. 锁定失败的情况：工作单信息会进行回滚，不会保存有数据
             * **/
            for (Long wareId : wareIds) {
              Integer count =  wareSkuDao.lockSkuStock(skuId,wareId,hasStock.getLockNum());
              if(count==1){
                  stockSku = true;

                  //保存到锁成功的商品的工作单详情
                  WareOrderTaskDetailEntity detailEntity = new WareOrderTaskDetailEntity();
                  detailEntity.setSkuId(skuId);
                  detailEntity.setSkuNum(hasStock.getLockNum());
                  detailEntity.setSkuName(skuName);
                  detailEntity.setLockStatus(hasStock.getLockNum()); //注入锁住的商品数量
                  detailEntity.setWareId(wareId);
                  detailEntity.setLockStatus(1); //1:锁定成功
                  detailEntity.setTaskId(wareTask.getId()); //注入工作单id
                  wareOrderTaskDetailService.save(detailEntity);

                  //TODO: 每次锁定成功，都要发消息给Rabbit消息队列 说已经锁定成功了
                  StockLockedTo stockLockedTo = new StockLockedTo();

                  stockLockedTo.setId(wareTask.getId());//注入工作单id
                  StockLockedTo.StockDetailTo stockDetailTo = new StockLockedTo.StockDetailTo();
                  BeanUtils.copyProperties(detailEntity,stockDetailTo);
                  stockLockedTo.setStockDetailTo(stockDetailTo); //注入工作单详情数据

                  //发送消息给解库存的消息队列
                  System.out.println("发送消息.............");
                  rabbitTemplate.convertAndSend("stock-event-exchange","stock.locked",stockLockedTo);
                  break;
              }else{ //当前仓库锁失败，切换下一个仓库锁
              }
            }
            if(!stockSku){
                throw new NoStockException(skuId,skuName);
            }
        }
        return allStock;

    }

    @Override
    public void unlockStock(StockLockedTo stockLockedTo) {
            System.out.println("收到解锁库存的消息");
            System.out.println("库存工作单id为："+stockLockedTo.getId());
            //通过这里工作单的仓库和skuid，再给锁失败的库存中的数据中添加回来即可，这里就算是解锁操作(回滚)
            StockLockedTo.StockDetailTo detailTo = stockLockedTo.getStockDetailTo();
            Long detailId = detailTo.getId();
            Long skuId = detailTo.getSkuId();
            Long wareId = detailTo.getWareId();
            //解锁操作：
            /**
             * 解锁操作：还需要查看是否有当前的订单
             *          如果不存在该订单：需要解锁库存(证明执行错误，已经回滚了订单)
             *          如果存在订单：
             *                  1.订单状态：如果是已取消(代表未支付成功)，则解锁库存
             * **/
            //1.查询是否有该工作单的信息：有解锁，没有无需解锁
            WareOrderTaskDetailEntity detailEntity = wareOrderTaskDetailService.getById(detailId);
            if (detailEntity!=null){ //存在该工作单，证明库存锁定成功过，需要进行解锁
                WareOrderTaskEntity task = wareOrderTaskService.getById(stockLockedTo.getId());
                //2.根据订单号远程查询订单：不存在则解锁
                R r = orderFeignService.getOrderBySn(task.getOrderSn());
                if(r.getCode()==0){
                    OrderEntityTo order = r.getData(new TypeReference<OrderEntityTo>() {});
                    //订单不存在或则订单状态为已经取消，则进行解锁操作
                    if(order==null || order.getStatus()==4){
                            if(detailEntity.getLockStatus()==1){  //工作单详情的商品状态为1，才能进行解锁
                                //解锁：调用解锁方法，把锁住的库存减回去
                                unLockStock(detailEntity.getSkuId(),detailEntity.getWareId(),
                                        detailEntity.getSkuNum(),detailId); //detailId:工作单id
                            }
                    }else{ //远程服务失败
                    throw  new RuntimeException("远程服务调用失败");
                    }
                }
            }else{
                //无需解锁
            }
    }

    /**
     * 防止订单关闭延时的解锁库存方法
     * @param order 已经关闭了的订单数据
     * **/
    @Override
    public void unlockStock(OrderTo order){
        String orderSn = order.getOrderSn();
        //解锁库存：
        //1.查询库存工作单
        WareOrderTaskEntity orderTaskEntity = wareOrderTaskService.getOrderTaskByOrderSn(orderSn);

        //2.找到状态为1：未解锁状态的库存工作单详情
        List<WareOrderTaskDetailEntity> detailList = wareOrderTaskDetailService.list(new QueryWrapper<WareOrderTaskDetailEntity>().eq("task_id",
                orderTaskEntity.getId()).eq("lock_status", 1));

        for (WareOrderTaskDetailEntity entity : detailList) {
            //解锁库存
            unLockStock(entity.getSkuId(),entity.getWareId(),entity.getSkuNum(),entity.getTaskId());
        }
    }

    @Data
    class SkuWareHasStock{
        private Long skuId;
        private String skuName;
        private List<Long>wareIds; //所有有该商品的仓库的id
        private Integer lockNum;
    }

}






