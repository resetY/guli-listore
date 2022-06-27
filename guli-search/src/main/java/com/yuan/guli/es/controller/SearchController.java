package com.yuan.guli.es.controller;

import com.sun.xml.internal.ws.api.server.Module;
import com.yuan.guli.es.service.MallSearchService;
import com.yuan.guli.es.vo.SearchParam;
import com.yuan.guli.es.vo.SearchResult;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Controller
public class SearchController {


    @Autowired
    MallSearchService mallSearchService;  //需要一个业务层接口

    @RequestMapping("/list.html")
        public String  listPage(SearchParam searchParam, Model model, HttpServletRequest req){ //传入检索条件
        String queryString = req.getQueryString();
        searchParam.setQueryString(queryString);

        SearchResult result =  mallSearchService.search(searchParam); //需要一个方法可以接受传进来的检索参数

        model.addAttribute("result",result); //将数据放入model
            return  "list"; //跳转到list页面
        }


}
