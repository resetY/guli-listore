package com.yuan.guli.es.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.yuan.common.constant.EsConstant;
import com.yuan.common.constant.InfoEnum;
import com.yuan.common.to.es.SkuEsModel;
import com.yuan.common.utils.R;
import com.yuan.guli.es.config.ElasticSearchConfig;
import com.yuan.guli.es.feign.ProductFeign;
import com.yuan.guli.es.service.MallSearchService;
import com.yuan.guli.es.vo.AttrRespVo;
import com.yuan.guli.es.vo.BrandVo;
import com.yuan.guli.es.vo.SearchParam;
import com.yuan.guli.es.vo.SearchResult;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MallSearchServiceImpl implements MallSearchService {

    //es??????????????????
    @Autowired
    RestHighLevelClient client;
    @Override
    public SearchResult search(SearchParam searchParam) {
        SearchResult result=null;
        SearchRequest searchR = buliderSearchRequest(searchParam);
        try {
            //3.??????????????????
            SearchResponse response = client.search(searchR, ElasticSearchConfig.COMMOM_OPTIONS);
            result = buildSearchResult(response,searchParam);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    @Autowired
    ProductFeign productFeign;
    /**
     * ??????????????????
     * **/
    private SearchResult buildSearchResult( SearchResponse  searchResponse,SearchParam searchParam) {
      SearchResult result = new SearchResult();

        //1.??????????????????????????????
        SearchHits hits = searchResponse.getHits();
        List<SkuEsModel> skuEsModelList = new ArrayList<>();

        if (hits!=null&&hits.getHits().length>0){
            SearchHit[] hitsList = hits.getHits();
            for (SearchHit searchHit : hitsList) {
                String sourceAsString = searchHit.getSourceAsString();
                SkuEsModel esModel = JSONObject.parseObject(sourceAsString, SkuEsModel.class);
                skuEsModelList.add(esModel);

                //???????????????????????????????????????
                if(!StringUtils.isEmpty(searchParam.getKeyword())){
                    HighlightField skuTitle = searchHit.getHighlightFields().get("skuTitle");
                    String string = skuTitle.getFragments()[0].string();
                    esModel.setSkuTitle(string); //???????????????????????????title
                }

            }
        }
        result.setProducts(skuEsModelList);

        //2.????????????????????????
       List< SearchResult.AttrVo> attrVos = new ArrayList<>();

        ParsedNested attr_agg = searchResponse.getAggregations().get("attr_agg");
        ParsedLongTerms attr_id_agg = attr_agg.getAggregations().get("attr_id_agg");
        List<? extends Terms.Bucket> buckets = attr_id_agg.getBuckets();
        for (Terms.Bucket b : buckets) {

            Long attr_id = b.getKeyAsNumber().longValue();
            SearchResult.AttrVo attrVo = new SearchResult.AttrVo();
            attrVo.setAttrId(attr_id);

            //??????attrname
            ParsedStringTerms attr_name_agg = b.getAggregations().get("attr_name_agg");
            String attr_name = attr_name_agg.getBuckets().get(0).getKeyAsString();
            attrVo.setAttrName(attr_name);

            //??????attr_value
            ParsedStringTerms attr_value_agg = b.getAggregations().get("attr_value_agg");
            List<? extends Terms.Bucket> value_b = attr_value_agg.getBuckets();
            List<String> values = value_b.stream().map(v -> {
                return v.getKeyAsString();
            }).collect(Collectors.toList());
            attrVo.setAttrValue(values);

        attrVos.add(attrVo);
        }

        result.setAttrs( attrVos);

        //3.?????????????????????

       ParsedLongTerms brand_agg = searchResponse.getAggregations().get("brand_agg");
       List< SearchResult.BrandVo> brandVos = new ArrayList<>();
        List<? extends Terms.Bucket> brand_aggBuckets = brand_agg.getBuckets();
        for (Terms.Bucket b : brand_aggBuckets) {
            //??????id
            String keyAsString = b.getKeyAsString();
            SearchResult.BrandVo brandVo = new SearchResult.BrandVo();
            brandVo.setBrandId(Long.parseLong(keyAsString));
            //??????name
            ParsedStringTerms brand_name_agg = b.getAggregations().get("brand_name_agg");
            String brand_name = brand_name_agg.getBuckets().get(0).getKeyAsString();
            brandVo.setBrandName(brand_name);
            //????????????
            ParsedStringTerms brand_img_agg= b.getAggregations().get("brand_img_agg");
            String brand_img = brand_img_agg.getBuckets().get(0).getKeyAsString();
            brandVo.setBrandImg(brand_img);

            brandVos.add(brandVo);
        }
        result.setBrands(brandVos);

        //4.????????????
        ParsedLongTerms catalog_agg = searchResponse.getAggregations().get("catalog_agg");
        List<? extends Terms.Bucket> catalog_aggBuckets = catalog_agg.getBuckets();

        List< SearchResult.CatalogVo> catalogVos = new ArrayList<>();
        for (Terms.Bucket b : catalog_aggBuckets) {
            SearchResult.CatalogVo catalogVo = new SearchResult.CatalogVo();
            String cataString = b.getKeyAsString();
            catalogVo.setCatalogId(Long.parseLong(cataString)); //??????id??????

            //???????????????,????????????bu?????????????????????
            Aggregations aggregations = b.getAggregations();
            ParsedStringTerms catalog_name_agg = aggregations.get("catalog_name_agg");
            String catalog_name = catalog_name_agg.getBuckets().get(0).getKeyAsString();//?????????????????????
            catalogVo.setCatalogName(catalog_name);
            System.out.println("??????????????????"+catalog_name);
            catalogVos.add(catalogVo);
        }

        result.setCatalogs(catalogVos);

        /***-------?????????????????????????????????---------****/

        //5.????????????
      Long total =   hits.getTotalHits().value;
        result.setPageNum(searchParam.getPageNum()); //??????
        //????????? = total / ?????????????????????????????????????????????+1
        //?????????toalpage = (total+page-1)/page
    Integer totalPages = Math.toIntExact(((total + InfoEnum.PAGESIZE.getCode()) - 1) / InfoEnum.PAGESIZE.getCode());
        result.setTotalPages(totalPages); //?????????
        result.setTotal(total); //???????????????

        List<Integer> pagenavs = new ArrayList();
        for (int i = 1;i<=totalPages;i++){
            pagenavs.add(i);
        }
        result.setPageNavs(pagenavs);

//        //??????????????????

//        List<SearchResult.AttrVo> attrs = result.getAttrs();
////        List<SearchResult.NavVo> navVos = attrs.stream().map(attr -> {
////                SearchResult.NavVo navVo = new SearchResult.NavVo();
////                //1.??????attrs??????????????????vo  attr = 2_5???:6???
////            List<String> values = attr.getAttrValue();
////            for (String value : values) {
////              //  String[] s = value.split("_");
////                navVo.setNavValue(value);
////            }
////            String attrName = attr.getAttrName();
////            navVo.setNavName(attrName);
////                return navVo;
////            }).collect(Collectors.toList());
////            navVos.forEach(System.out::println);
////            result.setNavs(navVos);

       if(searchParam.getAttrs()!= null && searchParam.getAttrs().size()>0){
           List<SearchResult.NavVo> navVos = searchParam.getAttrs().stream().map(attr -> {
               SearchResult.NavVo navVo = new SearchResult.NavVo();
               String[] s = attr.split("_");
               navVo.setNavValue(s[1]);
               R info = productFeign.info(Long.parseLong(s[0]));
               result.getAttrIdS().add(Long.parseLong(s[0])); //?????????????????????
               if(info.getCode()==0){
                   AttrRespVo data = info.getData("attr", new TypeReference<AttrRespVo>() {
                   });
                   navVo.setNavName(data.getAttrName());
               }else{
                   navVo.setNavName(s[0]);
               }

               //2.?????????????????????????????????
               //???????????????????????????????????????????????????????????????
               String queryString = ReplaceQueryString(searchParam,"attrs",attr);
               navVo.setLink("http://search.guli.com/list.html?"+queryString);
               return navVo;
           }).collect(Collectors.toList());
           result.setNavs(navVos);
       }
    //???????????????
if(searchParam.getBrandId()!= null && searchParam.getBrandId().size()>0){
    List<SearchResult.NavVo> navs = result.getNavs();
    SearchResult.NavVo navVo = new SearchResult.NavVo();

    //todo ????????????????????????
    R r= productFeign.infos(searchParam.getBrandId());
    StringBuffer buffer = new StringBuffer();

    if(r.getCode()==0){
        List<BrandVo> brand = r.getData("brand", new TypeReference<List<BrandVo>>() {
        });
        String brandReplace = "";
        for (BrandVo b : brand) {
           buffer.append(b.getBrandName()+";");
        brandReplace = ReplaceQueryString(searchParam,"brandId",b.getBrandId()+"");

        }
        navVo.setNavValue(buffer.toString());
        navVo.setLink("http://search.guli.com/list.html?"+brandReplace);

    }
    navs.add(navVo);
}
        //todo ??????????????????????????????


        return result;
    }

    private String ReplaceQueryString(SearchParam searchParam,String key,String value) {
        String encode = null;
        try {
             encode = URLEncoder.encode(value, "UTF-8");
            //???????????????????????????????????????java????????????????????????????????????
            encode = encode.replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return searchParam.getQueryString().replace("&"+key+"=" +  encode, "");
    }

    /**
     * ?????????????????????????????????
     * */
    private SearchRequest buliderSearchRequest(SearchParam searchParam) {

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder(); //????????????DSL??????
        //1. ??????bool
        BoolQueryBuilder querybuilder = QueryBuilders.boolQuery();
        //1.1 must ??? match
        if(!StringUtils.isEmpty(searchParam.getKeyword())){
            MatchQueryBuilder matchQuery = QueryBuilders.matchQuery("skuTitle", searchParam.getKeyword());
            querybuilder.must(matchQuery);
        }
        //1.2 ??????filter??????
        //1.2.1 cate-f
        if(searchParam.getCatalog3Id()!=null){
            querybuilder.filter( QueryBuilders.termQuery("catalogId", searchParam.getCatalog3Id()));
        }

        //1.2.2 bandid-f
        if(searchParam.getBrandId()!=null && searchParam.getBrandId().size()>0){
            querybuilder.filter(QueryBuilders.termsQuery("brandId", searchParam.getBrandId()));
        }

        //1.2.3 ????????????
        if(searchParam.getHasStock() != null){ //????????????(???????????????)???????????????????????????
            querybuilder.filter(QueryBuilders.termQuery("hasStock",searchParam.getHasStock()>0));
        }

    //1.2.4 ?????????????????????
    if(!StringUtils.isEmpty(searchParam.getSkuPrice())){
        RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("skuPrice");
        //1500_5000:1500???5000???????????????
        String[] str = searchParam.getSkuPrice().split("_");; //????????????_???????????????
        if(str.length==2 && !(str[0].equals(""))){
            //??????
            rangeQuery.gte(str[0]).lte(str[1]);  //startWith("_"):?????????_????????????true
        }else if(str.length==2 && str[0].equals("")){ //_500 ?????????2???????????????????????????
            rangeQuery.lte(str[1]);
        }else if(str.length==1){  //500_?????????1
            rangeQuery.gte(str[0]);
        }
        querybuilder.filter(rangeQuery);

        //1.2.5 ???????????????????????????????????????
        if(searchParam.getAttrs()!=null && searchParam.getAttrs().size()>0){
            //attr = 1_5???:8???   1???id  ??????????????????
            for(String attrStr :searchParam.getAttrs()){
                //??????bool???????????????
                BoolQueryBuilder bool =  QueryBuilders.boolQuery();

                String[] s = attrStr.split("_");
                String attrId = s[0];
                System.out.println("attrID:"+attrId);

                String[] attrValues = s[1].split(":");//??????????????????????????????
                bool.must(QueryBuilders.termQuery("attrs.attrId",attrId));
                bool.must(QueryBuilders.termsQuery("attrs.attrValue",attrValues));

                //nest???????????????????????????????????????????????????????????????
                NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery("attrs", bool, ScoreMode.None);//???????????????
                querybuilder.filter(nestedQuery);
            }
        }
    }
        //query:??????????????????
        searchSourceBuilder.query(querybuilder);
    /**
     * ????????????????????????
     * */
    if(!StringUtils.isEmpty(searchParam.getSort())){
        //  2.1????????????:????????????sort = hotScore(????????????)_asc/desc
        String[] sort = searchParam.getSort().split("_");
        SortOrder sortOrder = sort[1].equalsIgnoreCase("DESC")?SortOrder.DESC:SortOrder.ASC;
        searchSourceBuilder.sort(sort[0],sortOrder);

        //2.2????????????
        //?????????(?????????-1) * ????????????  =  ?????????  ??????from = (num- 1) * 16
        searchSourceBuilder.from((searchParam.getPageNum()-1)* InfoEnum.PAGESIZE.getCode());
        searchSourceBuilder.size(InfoEnum.PAGESIZE.getCode()); //???????????????16??????

        System.out.println(searchParam.getKeyword());
    }
        //2.3????????????
        if(!StringUtils.isEmpty(searchParam.getKeyword())){
            HighlightBuilder builder = new HighlightBuilder();
           builder.field("skuTitle").
                    preTags("<b style='color:red'>").
                    postTags("</b>");
            System.out.println("??????????????????");
            searchSourceBuilder.highlighter(builder);
        }
        System.out.println("?????????DSL?????????"+searchSourceBuilder.toString());

    /**
     * ????????????
     * */
    //3.1 ????????????
        TermsAggregationBuilder brand_Agg = AggregationBuilders.terms("brand_agg").field("brandId").size(50);

        //????????????????????????
        TermsAggregationBuilder brand_name_agg = AggregationBuilders.terms("brand_name_agg").field("brandName").size(1);
        brand_Agg.subAggregation(brand_name_agg);

        TermsAggregationBuilder brand_img_agg = AggregationBuilders.terms("brand_img_agg").field("brandImg").size(1);
        brand_Agg.subAggregation(brand_img_agg);

        searchSourceBuilder.aggregation(brand_Agg);

    //3.2 ????????????
        TermsAggregationBuilder catalog_agg = AggregationBuilders.terms("catalog_agg").field("catalogId").size(50);

        //???????????????
        TermsAggregationBuilder catalog_name_agg = AggregationBuilders.terms("catalog_name_agg").field("catalogName").size(1);
        catalog_agg.subAggregation(catalog_name_agg);

        searchSourceBuilder.aggregation(catalog_agg);


    //3.3 attrs????????????
        NestedAggregationBuilder attr_agg = AggregationBuilders.nested("attr_agg", "attrs");
        TermsAggregationBuilder attr_id_agg = AggregationBuilders.terms("attr_id_agg").field("attrs.attrId");
           //??????attr_id
            attr_agg.subAggregation(attr_id_agg);

        //?????????
        TermsAggregationBuilder attr_name_agg = AggregationBuilders.terms("attr_name_agg").field("attrs.attrName").size(1);
        attr_id_agg.subAggregation(attr_name_agg);

        TermsAggregationBuilder attr_value_agg = AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue").size(1);
        attr_id_agg.subAggregation( attr_value_agg );
        searchSourceBuilder.aggregation(attr_agg);

        //????????????
        SearchRequest searchRequest = new SearchRequest
                (new String[]{EsConstant.AttrEnum.PRODUCT.getMessage()},
                        searchSourceBuilder);
        return searchRequest;
    }
}
