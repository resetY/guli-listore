package com.yuan.guli.guliorder.web;

import com.alipay.api.AlipayApiException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yuan.guli.guliorder.config.AlipayTemplate;
import com.yuan.guli.guliorder.entity.OrderEntity;
import com.yuan.guli.guliorder.service.OrderService;
import com.yuan.guli.guliorder.vo.PayVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 进行支付的控制层接口
 * **/
@Controller
public class PayWebCongroller {
    @Autowired
    OrderService orderService;
    @Autowired
    AlipayTemplate alipayTemplate;

    /**
     * 设置跳转到支付页面：
     *      将pay的数据返回:这个数据是字符串类型的，然后将返回的数据类型
     *      从默认json设置为text/html页面的形式
     *
     *      注意：编写pay里面的内容，设置支付成功后，跳转到订单列表页面
     * **/
    @ResponseBody
    @RequestMapping(value = "/payOrder",produces = "text/html")
    public String payOrder(@RequestParam("orderSn") String orderSn) throws AlipayApiException {
       PayVo payVo =  orderService.getOrderPay(orderSn);
        String pay = alipayTemplate.pay(payVo);
        System.out.println(pay);

        return  pay;
    }





}
