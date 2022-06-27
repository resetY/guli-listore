package com.yuan.guli.es.service;

import com.yuan.guli.es.vo.SearchParam;
import com.yuan.guli.es.vo.SearchResult;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;

public interface MallSearchService {

    /**
     * @param searchParam 检索的所有参数
     * @return  检索出来需要展示的商品数据
     * */
    SearchResult search(SearchParam searchParam);
}
