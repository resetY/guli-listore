package com.yuan.gulicart.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 整个购物车
 * 将需要计算的属性，直接在get方法重写计算即可
 * */
@Data
public class Cart {

        List<CartItem> cartItems;
        private Integer countNum; //商品数量：需要计算
        private Integer countType; //商品类型的数量：相同的商品有几种，需要计算
        private BigDecimal totalAmount; //所有商品总价格
        private BigDecimal reduce = new BigDecimal(0); //减免的总价格

        public List<CartItem> getCartItems() {
                return cartItems;
        }

        public void setCartItems(List<CartItem> cartItems) {
                this.cartItems = cartItems;
        }

        public Integer getCountNum() {
              Integer  count = 0; //没有购物项的情况
                if(cartItems!=null && cartItems.size()>0){
                        for (CartItem cartItem : cartItems) {
                                countNum+=cartItem.getCount();
                        }
                }
                return count;
        }


        public Integer getCountType() {
                return cartItems.size()==0?0:cartItems.size();
        }



        public BigDecimal getTotalAmount() {
                BigDecimal amount = new BigDecimal("0");
                if(cartItems!= null && cartItems.size()>0){
                        for (CartItem cartItem : cartItems) {
                                if(cartItem.getCheck()){ //如果被选中，才进行价格叠加
                                        BigDecimal totalPrice = cartItem.getTotalPrice();
                                        amount = amount.add(totalPrice);
                                }

                        }
                }
                //需要减去优惠价格
                BigDecimal subtract = amount.subtract(getReduce());

                return subtract;
        }



        public BigDecimal getReduce() {
                return reduce;
        }

        public void setReduce(BigDecimal reduce) {
                this.reduce = reduce;
        }
}
