package com.yuan.guli.es.vo;

import com.yuan.common.constant.InfoEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用于封装所有可能传入的检索条件
 * */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchParam {

    //guli.com?keyword=小米&sort=catalog3Id&sort="排序方式"
    private String keyword;//检索参数：全文匹配关键字 (匹配skuTitle)
    private Long catalog3Id; //三级分类id

    /**
     * 排序：
     *  sort = saleCount_asc/ saleCount_desc
     *  sort = hotScore_asc/hotScore_desc  综合排序按照热度
     *  sort = skuPrice_asc/skuPrice_desc  按照价格排序
     *  sort =
     * */
    private String sort; //排序条件

    /**
     * 过滤条件：
     *      hasStock：是否有货
     *      skuPrice：价格区间1_500  _500(500以内的)  500_(500以上的)
     * **/
    private Integer hasStock; //是否有货
    private String  skuPrice; //价格区间：只需要整数，所以用String
    private  List<Long> brandId; //品牌：多个品牌id

    private List<String> attrs; //按照参数属性筛选

    private Integer pageNum = InfoEnum.CURRENTPage.getCode(); //当前页码



    private String queryString;//原生所有查询条件


}
