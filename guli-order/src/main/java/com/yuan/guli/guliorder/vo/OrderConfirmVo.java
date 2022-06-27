package com.yuan.guli.guliorder.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class OrderConfirmVo {

    List<MemberAddressVo> address; //收货地址列表

    List<OrderItemVo> items; //所有选中的购物项

    //优惠券信息
    Integer integeration;//积分

  //  BigDecimal total;//订单总额

 //   BigDecimal payPrice; //应付价格

    //发票记录

    String orderToken; //防止重复提交的令牌

    private Integer totalCount; //几件商品

    private Map<Long,Boolean> stocks;

    public Integer getTotalCount() {
        Integer size = 0;
        List<OrderItemVo> items = getItems();
       if(items!=null && items.size()>0){
           for (OrderItemVo item : items) {
               size += item.getCount();
           }
       }
        return size;
    }
    public BigDecimal getTotal() {  //进行总价的计算：所有选中的订单项的价格的总和
        BigDecimal total = new BigDecimal("0");
        if(items!=null){
            for (OrderItemVo item : items) {
                 //计算出选中的订单项的总价格，然后进行逐步添加
                BigDecimal itemTotal = item.getPrice().multiply(new BigDecimal(item.getCount().toString()));
                 total =  total.add(itemTotal);
            }
        }
        return total; //返回订单总额
    }



    public BigDecimal getPayPrice() { //应付价格的计算
        BigDecimal payPrice = getTotal(); //暂时没有优惠，那么应付价格为总额
        return payPrice;
    }


    @Data
    public static class MemberAddressVo{
//MemberReceiveAddressEntity:封装这个会员的收货地址数据
            /**
             * id
             */
            private Long id;
            /**
             * member_id
             */
            private Long memberId;
            /**
             * 收货人姓名
             */
            private String name;
            /**
             * 电话
             */
            private String phone;
            /**
             * 邮政编码
             */
            private String postCode;
            /**
             * 省份/直辖市
             */
            private String province;
            /**
             * 城市
             */
            private String city;
            /**
             * 区
             */
            private String region;
            /**
             * 详细地址(街道)
             */
            private String detailAddress;
            /**
             * 省市区代码
             */
            private String areacode;
            /**
             * 是否默认
             */
            private Integer defaultStatus;
    }

    @Data
    public static class OrderItemVo{ //订单项
        private Long skuId; //商品id
        private Boolean check = true; //是否被选中
        private String title;
        private String image;
        private List<String> skuAttr; //商品套餐信息：6+128g
        private BigDecimal price;
        private Integer count;
        private BigDecimal totalPrice = new BigDecimal("0");
        private Boolean hasStock = true; //是否有货
        private BigDecimal weight; //商品重量：后续有需求再编写
    }
}
