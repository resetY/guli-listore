package com.yuan.gulicart.vo;

import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 购物项
 * */
@Data
public class CartItem {
        private Long skuId; //商品id
        private Boolean check = true; //是否被选中
        private String title;
        private String image;
        private List<String> skuAttr; //商品套餐信息：6+128g
        private BigDecimal price;
        private Integer count;
        private BigDecimal totalPrice = new BigDecimal("0");

    public BigDecimal getTotalPrice() { //购物项总价进行get中的修改计算 ，i；multiply：乘法
        return this.price.multiply(new BigDecimal(""+this.count));
}

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public Boolean getCheck() {
        return check;
    }

    public void setCheck(Boolean check) {
        this.check = check;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<String> getSkuAttr() {
        return skuAttr;
    }

    public void setSkuAttr(List<String> skuAttr) {
        this.skuAttr = skuAttr;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }


}
