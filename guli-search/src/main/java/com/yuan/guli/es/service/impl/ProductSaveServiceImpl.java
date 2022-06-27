package com.yuan.guli.es.service.impl;


import com.alibaba.fastjson.JSON;
import com.yuan.common.constant.EsConstant;
import com.yuan.common.to.es.SkuEsModel;
import com.yuan.guli.es.config.ElasticSearchConfig;
import com.yuan.guli.es.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.bulk.BulkAction;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductSaveServiceImpl  implements ProductSaveService {
/**  映射关系：
         PUT product
         {
         "mappings": {
         "properties": {
         "skuId":{
         "type": "long"
         },
         "spuId":{
         "type": "keyword"
         },
         "skuTitle":{
         "type": "text",
         "analyzer": "ik_smart"
         },
         "skuPrice":{
         "type": "keyword"
         },
         "skuImg":{
         "type": "keyword",
         "index": false,
         "doc_values": false
         },
         "saleCount":{
         "type": "long"
         },
         "hasStock":{
         "type": "boolean"
         },
         "hotScore":{
         "type": "long"
         },
         "brandId":{
         "type": "long"
         },
         "catalogId":{
         "type": "long"
         },
         "brandName":{
         "type": "keyword",
         "index": false,
         "doc_values": false
         },
         "brandImg":{
         "type": "keyword",
         "index": false,
         "doc_values": false
         },
         "catalogName":{
         "type": "keyword",
         "index": false,
         "doc_values": false
         },
         "attrs":{
         "type": "nested"
         , "properties": {
         "attrId":{
         "type":"long"
         },
         "attrName":{
         "type": "keyword",
         "index": false,
         "doc_values": false
         },
         "attrValue":{
         "type":"keyword"
         }
         }

         }
         }
         }}
**/

    //操作es
    @Autowired
    private RestHighLevelClient client;

    @Override
    public Boolean saveProduct(List<SkuEsModel> esModels) throws IOException {
        //保存到es

        //1.给es建立索引，product  建立好映射关系

        //2.构造保存请求：
        BulkRequest bulkRequest = new BulkRequest();
        for (SkuEsModel esModel : esModels) {
            IndexRequest indexRequest = new IndexRequest(EsConstant.AttrEnum.PRODUCT.getMessage());
            indexRequest.id(esModel.getSkuId().toString());
            String json_esModel = JSON.toJSONString(esModel); //把数据转化为json数据
            indexRequest.source(json_esModel, XContentType.JSON);
            bulkRequest.add(indexRequest);

        }
        //3.在es中保存这些数据：进行批量保存操作bulk()
        BulkResponse bulk = client.bulk(bulkRequest, ElasticSearchConfig.COMMOM_OPTIONS);

        boolean b = bulk.hasFailures(); //获取是否有保存失败的商品
        List<String> productError = Arrays.stream(bulk.getItems()).map(item -> {

            return item.getId();
        }).collect(Collectors.toList());
        if(b == true){  //注意：bulk.hasFailures()是判断有没有错误，如果返回结果为true，则发生错误
            log.error("商品上架错误："+productError);
        }
        log.info("商品上架完成............");
        return b;  //返回布尔类型：false则有商品上架错误
    }

}
