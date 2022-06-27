package com.yuan.guli.es.vo;

import com.yuan.common.to.es.SkuEsModel;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/***
 * 检索到后，返回到页面的响应数据模型
 *
 */
@Data
public class SearchResult {

    //所有商品信息
    private List<SkuEsModel> products;

    /**
     * 分页信息
     * */
    //当前页码
    private Integer pageNum;
    //总记录数
    private Long total;
    //总页数
    private Integer totalPages;
    private  List<Integer> pageNavs; //导航页码

    /**
     * 搜索相关的展示信息
     * **/
    private List<BrandVo> brands;

    /**
     * 参数属性的检索
     * **/
    private List<AttrVo> attrs;
    private  List<CatalogVo> catalogs;//分类id和名字集合


    //面包屑导航数据：
    private List<NavVo> navs= new ArrayList<>();
    private List<Long> attrIdS = new ArrayList<>();



    @Data
    public static class NavVo{
        private  String navName;
        private String navValue;
        private  String link; //跳转的地址
    }


    @Data
    public static class CatalogVo{
        private Long catalogId;
        private String catalogName;
    }
    @Data
    public static class AttrVo{
        private  Long attrId;
        private  String attrName;
        private List<String> attrValue;
    }
    @Data
    public  static class BrandVo{
        private Long brandId;
        private String brandName;
        private String brandImg;
    }
}
