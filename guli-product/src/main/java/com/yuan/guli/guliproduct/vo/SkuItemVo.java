package com.yuan.guli.guliproduct.vo;

import com.yuan.guli.guliproduct.entity.SkuImagesEntity;
import com.yuan.guli.guliproduct.entity.SkuInfoEntity;
import com.yuan.guli.guliproduct.entity.SkuSaleAttrValueEntity;
import com.yuan.guli.guliproduct.entity.SpuInfoDescEntity;
import lombok.Data;

import java.util.List;

/**
 * 商品详情页数据
 *
 *         //1.获取sku基本信息 pms_sku_info
 *
 *
 *         //2.sku图片信息：`pms_sku_images`
 *
 *         //3.spu的所有sku组合信息
 *
 *         //4.商品介绍：spu介绍
 *
 *         //5.spu规格属性和包装
 * */
@Data
public class SkuItemVo {
        private SkuInfoEntity info;

        private List<SkuImagesEntity> images;

        private List<SkuItemSaleAttrVo> saleAttr; //基本信息：红色 、 黑色 ....

        private SpuInfoDescEntity  desp;//描述图片

        private List<SpuItemAttrGroupAttrVo> groups;

        private boolean hasStock; //是否有货


        @Data
        public static class SkuItemSaleAttrVo{
                private Long attrId;
                private String attrName;
                private  List<AttrValueWithSkuid> attrValues;

        }
        @Data
        public static class SpuItemAttrGroupAttrVo{
                private String groupName;
                private List<SpuBaseAttrVo> attrs;
        }
         @Data
       public  static class SpuBaseAttrVo{
                private  String attrName;
                private String attrValue;
        }

        @Data
        public static  class AttrValueWithSkuid{
            private String attrValue;
            private  String skuId;  //多个skuid的字符串
        }



}


