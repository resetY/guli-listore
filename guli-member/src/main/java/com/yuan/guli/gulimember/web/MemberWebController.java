//package com.yuan.guli.gulimember.web;
//
//import com.alibaba.fastjson.TypeReference;
//import com.yuan.common.utils.R;
//import com.yuan.guli.gulimember.feign.OrderFeignService;
//import com.yuan.guli.gulimember.service.MemberService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Controller
//public class MemberWebController {
//
//@Autowired
//    OrderFeignService orderFeignService;
//    @RequestMapping("/memberOrder.html")
//    public String memberOrder(@RequestParam(value = "pageNum",defaultValue = "1")
//                                          Integer pageNum, Model model){
//
//
//
//        Map <String,Object> page =  new HashMap<>();
//        page.put("page",pageNum);
//        R r = orderFeignService.queryPageWithItem(page);
//
//        model.addAttribute("orderList",r);
//        System.out.println("订单列表数据："+r);
//        return  "list";
//    }
//}
