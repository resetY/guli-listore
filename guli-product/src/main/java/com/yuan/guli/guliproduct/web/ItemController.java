package com.yuan.guli.guliproduct.web;

import com.yuan.guli.guliproduct.service.SkuInfoService;
import com.yuan.guli.guliproduct.vo.SkuItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ItemController {

    @Autowired
    SkuInfoService skuInfoService;
    @GetMapping("/{skuId}.html") //使用商品id，来根据进行跳转到的商品页
    public String  SkuItem(@PathVariable("skuId") Long skuId, Model model){ //传入检索条件

        SkuItemVo  skuItemVo = skuInfoService.item(skuId);
        model.addAttribute("item",skuItemVo);
        System.out.println("商品跳转");
        return "item"; //跳转到item页面
    }
}
