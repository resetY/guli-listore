package com.yuan.gulicart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.yuan.common.utils.R;
import com.yuan.gulicart.feign.ProductFeignService;
import com.yuan.gulicart.interceptor.CartInterceptor;
import com.yuan.gulicart.service.CartService;
import com.yuan.gulicart.vo.Cart;
import com.yuan.gulicart.vo.CartItem;
import com.yuan.gulicart.vo.SkuInfoVo;
import com.yuan.gulicart.vo.UserInfoTo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    StringRedisTemplate redisTemplate; //操作redis客户端

    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    ThreadPoolExecutor executor;
    private  final String CART_PREFIX = "guli:cart:"; //redis中的购物车前缀
    @Override
    public CartItem addCart(Long skuId, Integer num) throws ExecutionException, InterruptedException {


        BoundHashOperations<String, Object, Object> ops = getCartOps();

        String value = (String) ops.get(skuId.toString());//判断是否存在该key


        if(StringUtils.isEmpty(value)){
            CartItem cartItem = new CartItem();
            //添加购物车未存在的商品
            CompletableFuture<Void> F1 = CompletableFuture.runAsync(() -> { //任务一：远程查询sku信息
                //1.远程查询当前要添加的商品的信息：根据skuid查询
                R info = productFeignService.info(skuId);
                SkuInfoVo skuInfo = info.getData("skuInfo", new TypeReference<SkuInfoVo>() {
                });
                cartItem.setCheck(true); //默认选中状态
                cartItem.setSkuId(skuInfo.getSkuId());
                cartItem.setTitle(skuInfo.getSkuTitle());
                cartItem.setSkuId(skuInfo.getSkuId());
                cartItem.setCount(num);
                cartItem.setImage(skuInfo.getSkuDefaultImg());
                cartItem.setPrice(skuInfo.getPrice());
            }, executor);

            //2.远程查询sku相应的attr组合信息
            CompletableFuture<Void> F2 = CompletableFuture.runAsync(() -> {
                List<String> skuAttrValue = productFeignService.getSkuAttrValue(skuId);
                cartItem.setSkuAttr(skuAttrValue);
            });

            CompletableFuture.allOf(F1,F2).get(); //需要进行全部完成

            String json_cartItem = JSON.toJSONString(cartItem);
            ops.put(skuId.toString(),json_cartItem);
            return cartItem;
        }else { //存在该商品,则只需要修改数量
            CartItem cartValue = JSON.parseObject(value, CartItem.class);
            Integer old_count = cartValue.getCount();
            cartValue.setCount(old_count+num);
            String json_cartItem = JSON.toJSONString(cartValue);
            ops.put(skuId.toString(),json_cartItem);
            return  cartValue;
        }

    }

    //获取购物项：
    @Override
    public CartItem getCartItem(Long skuId) {
        BoundHashOperations<String, Object, Object> ops = getCartOps();
        String cartitem_json = (String) ops.get(skuId.toString());
        CartItem cartItem = JSON.parseObject(cartitem_json, CartItem.class);
        return  cartItem;
    }

    @Override
    public Cart getCart() throws ExecutionException, InterruptedException {

        Cart cart = new Cart();

        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
       if(userInfoTo.getUserId()!=null){ //登录用户
           String userKey = CART_PREFIX+userInfoTo.getUserId();
           BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(userKey); //登陆后的购物车操作
        //2.如果临时购物车数据还存在，需要进行合并
           List<CartItem> cartItems = getCartItems(CART_PREFIX+userInfoTo.getUserKey());
           if(cartItems!=null && cartItems.size()>0){ //如果临时有数据，进行合并
               for (CartItem cartItem : cartItems) {
                        addCart(cartItem.getSkuId(),cartItem.getCount());  //将临时购物车数据添加到用户购物车
               }
           }
         cleanCart(CART_PREFIX+userInfoTo.getUserKey()); //清空临时购物车
           //3.获取登陆后的购物车数据
           List<CartItem> cartItems_log = getCartItems(userKey); //包含合并过来的临时购物车数据
            cart.setCartItems(cartItems_log);
       }else{  //未登录
        String userKey = CART_PREFIX+userInfoTo.getUserKey();
//           BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(userKey);
////           List<Object> values = ops.values();//拿取所有购物项
////            if(values!=null && values.size()>0){
////                List<CartItem> cartItemList = values.stream().map(obj -> {
////                    String obj_str = (String) obj;
////                    CartItem cartItem = JSON.parseObject(obj_str, CartItem.class);
////                    return cartItem;
////                }).collect(Collectors.toList());
////                cart.setCartItems(cartItemList);
////            }

           List<CartItem> cartItems = getCartItems(userKey);
           cart.setCartItems(cartItems);
       }

        return cart;
    }

    private List<CartItem> getCartItems(String userKey) {
        BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(userKey);
        List<Object> values = ops.values();//拿取所有购物项
        if (values != null && values.size() > 0) {
            List<CartItem> cartItemList = values.stream().map(obj -> {
                String obj_str = (String) obj;
                CartItem cartItem = JSON.parseObject(obj_str, CartItem.class);
                return cartItem;
            }).collect(Collectors.toList());
            return cartItemList;
        }
        return  null;
    }
    private  BoundHashOperations<String, Object, Object>  getCartOps(){
        //1.判断购物车使用临时还是对应登录用户的
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        //redis的key：
        // cart:几号购物车  如果是临时 则cart:uuid
        String cartKey = "";
        if(userInfoTo.getUserId()!=null){ //登录情况
            cartKey = CART_PREFIX+userInfoTo.getUserId();
        }else{
            cartKey = CART_PREFIX+userInfoTo.getUserKey(); //临时用户
        }
        //注意：购物车有的商品，添加数量即可，没有的就新增购物项

        //op：封装操作，可以使用这个对象完成对购物车数据的增删除改查
        BoundHashOperations<String, Object, Object> op = redisTemplate.boundHashOps(cartKey);
        return op;
    }


    /**
     * 清空购物车方法
     * **/
    @Override
    public void cleanCart(String cartKey){
     redisTemplate.delete(cartKey); //直接把key整个删除
    }

    @Override
    public void checkItem(Long skuId, Integer check) {
        BoundHashOperations<String, Object, Object> ops = getCartOps();
        CartItem cartItem = getCartItem(skuId);//得到当前购物项
        cartItem.setCheck(check==1?true:false);
        String json_item = JSON.toJSONString(cartItem);
        ops.put(skuId.toString(),json_item);
    }

    /**
     * 修改购物车展示的商品数量
     * **/
    @Override
    public void changeCount(Long skuId, Integer num) {
        BoundHashOperations<String, Object, Object> ops = getCartOps();
        CartItem cartItem = getCartItem(skuId);//得到当前购物项
        cartItem.setCount(num);
        String json_item= JSON.toJSONString(cartItem);
        ops.put(skuId.toString(),json_item);
    }

    /**
     * 删除购物项
     * */
    @Override
    public void delItem(Long skuId) {
        BoundHashOperations<String, Object, Object> ops = getCartOps();
        ops.delete(skuId.toString());
    }


    @Override
    public List<CartItem> getItems() {
        System.out.println("--------------------------------------");
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        if(userInfoTo.getUserId()==null){
            return null;
        }else{
            String cartKey = CART_PREFIX+userInfoTo.getUserId();
            List<CartItem> cartItems = getCartItems(cartKey);
            //收集 被选中的购物项
            List<CartItem> collect = cartItems.stream().filter(item ->item.getCheck()).map(item->{
                //远程调用获取商品的最新价格，然后进行改变
                //注意：购物项的价格需要去实时查询
                BigDecimal price = productFeignService.getPrice(item.getSkuId());
                item.setPrice(price); //更新为最新价格
                return item;
            }).collect(Collectors.toList());
            return collect;
        }

    }
}
