package com.yuan.guli;


import com.alibaba.fastjson.JSON;
import com.yuan.guli.es.config.ElasticSearchConfig;
import lombok.Data;
import lombok.ToString;
import org.apache.lucene.util.QueryBuilder;
import org.assertj.core.error.ShouldBeToday;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.ElasticsearchClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.ml.PostDataRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.*;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.json.JSONString;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.ws.rs.GET;
import java.io.IOException;
import java.util.List;

@RunWith(SpringRunner.class) //spring驱动
@SpringBootTest
public class GuliSearchApplicationTests {
    @ToString
    @Data
   static class Account {  //使用_source生成的账户javabean

        private int account_number;
        private int balance;
        private String firstname;
        private String lastname;
        private int age;
        private String gender;
        private String address;
        private String employer;
        private String email;
        private String city;
        private String state;

    }
    //测试es的使用
    @Autowired
    private RestHighLevelClient client;


    /**
     * 复杂检索
     * */
    @Test
    public void fuzatest() throws IOException {
        //1.构造检索请求：
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("bank"); //检索谁
        //2.构建语句
        SearchSourceBuilder builder = new SearchSourceBuilder();

        //3.match_all检索
//        builder.query(QueryBuilders.matchAllQuery());
//        builder.from(); //分页
//        builder.size();
//        builder.aggregations(); //聚合

        //带s的为工具类
     //   builder.query(QueryBuilders.matchQuery("address","mill"));
        builder.query(QueryBuilders.matchAllQuery());

        //聚合：按照年龄聚合
        TermsAggregationBuilder agg = AggregationBuilders.terms("ageAgg").field("age").size(50);
        builder.aggregation(agg); //放入条件
        //聚合2:平均薪资聚合
        AvgAggregationBuilder avgs = AggregationBuilders.avg("balanceAvg").field("balance");
        builder.aggregation(avgs);

        //检索条件指定
        searchRequest.source(builder);

        System.out.println("检索条件："+builder.toString()); //输出检索条件

        //4.执行检索：获得响应
        SearchResponse searchResponse = client.search(searchRequest, ElasticSearchConfig.COMMOM_OPTIONS);
        //5.分析结果:获得的response响应可以获取许多数据，包括状态、时长、检索数据
        SearchHits hits = searchResponse.getHits();
        System.out.println();

        SearchHit[] hits1 = hits.getHits();
        for (SearchHit searchHit:hits1){
         //   System.out.println("数据获取："+searchHit.toString());
//            System.out.println(searchHit.getSourceAsMap());
            String sourceAsString = searchHit.getSourceAsString();
           Account account =  JSON.parseObject(sourceAsString,Account.class);
            System.out.println(account);
        }


        //聚合数据获取
        Aggregations aggs = searchResponse.getAggregations();
        Terms ageAgg = aggs.get("ageAgg");
        List<? extends Terms.Bucket> buckets = ageAgg.getBuckets();
        for (Terms.Bucket b:buckets){
            System.out.println("年龄："+b.getKeyAsString());
        }


    }


    @Test
   public  void contextLoads() {
        System.out.println(client);
    }

    /**
     * 测试：给es存储数据
     * */
    @Test
    public  void indexData() throws IOException {
       //1.给指定为user下存储数据:     CreateIndexRequest
        IndexRequest req = new IndexRequest("users");
        req.id("2"); //添加id
        //第一种添加数据方式：
   //    req.source("username","杨佳灿","age",18,"gender","男");

        //2.第二种方式：给请求保存数据 XContentType.JSON:类型Json
        User user = new User("郑凯婷", 18, "女");
        String json_user = JSON.toJSONString(user);
        req.source(json_user,XContentType.JSON);

        //3.进行索引保存：传入请求 和 请求的设置项
        IndexResponse index = client.index(req, ElasticSearchConfig.COMMOM_OPTIONS);

        System.out.println(index); //获得有用的响应数据
        //结果：IndexResponse[index=users,type=_doc,id=1,version=1,result=created,seqNo=0,primaryTerm=1,shards={"total":2,"successful":1,"failed":0}]
    }
    @Data
    class User {
        private String username;
        private Integer age;
        private String gender;

        public User() {
        }

        public User(String username, Integer age, String gender) {
            this.username = username;
            this.age = age;
            this.gender = gender;
        }
    }



/**
 *
 *  (空间复杂度 和 时间复杂度 的检索)
 * ①存储方式1：方便检索  节省了时间，浪费了空间
 * {
 *     skuid:1
 *     spuid；11
 *     skuTitle:华为
 *     price：998
 *     saleCount：99
 *     attrs{
 *         尺寸
 *         cpu
 *         分辨率
 *     }
 *     一个spuid的数据，会存在多个sku数据进行关联
 *      假设：100万商品，每个商品sku数据有20条
 *     亢余： 100万 * 20 * 2kb = 2gb
 *
 *  ② 存储方式2
 *  {
 *      skuid：1
 *      spuid：11
 *      sku基本信息
 *
 *}
 * attr索引{
 *     spuid:11
 *     spu数据(atts){
 *         信息数据
 *     }
 * }
 *  第二种方式的缺点：数据量大，那么很难承受  节省了空间，浪费了时间
              注意：检索某条数据名称，它的属性是动态生成

              假如搜索：小米 -- 小米包括的：电器、手机、粮食等类型商品
              假如搜索小米，查出手机相关 10000条数据
               分步：这数据有4000个相关的spu对应的所有可能属性
               es：检索传入的id[4000个spuid]，如果每个spuid8子节，一个请求就发送了32kb

             如果同时在线用户10万人，每个人都搜索，然后发送 32kb * 10万的请求 (要疯)
 *
 *
 *
 *
 * **/





}