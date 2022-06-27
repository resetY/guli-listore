package com.yuan.guli.guliorder.web;


import com.yuan.guli.guliorder.entity.OrderEntity;
import com.yuan.guli.guliorder.service.OrderService;
import com.yuan.guli.guliorder.vo.OrderConfirmVo;
import com.yuan.guli.guliorder.vo.OrderSubmitVo;
import com.yuan.guli.guliorder.vo.SubmitResponseVo;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Controller
public class OrderWebController {
    @Autowired
    OrderService orderService;


    /**
     * 提交订单的方法
     *      提交订单会以表单方式进行提交
     * **/
    @RequestMapping("/submitOrder")
    public String submitOrder(OrderSubmitVo submitOrder, Model model,
                              RedirectAttributes redirectAttributes){ //前端传输过来需要进行提交的数据
        System.out.println("订单提交的数据："+submitOrder);
        SubmitResponseVo responseVo = orderService.submitOrder(submitOrder);
        System.out.println("状态码："+responseVo.getCode());
        if(responseVo.getCode()==0){
            model.addAttribute("orderResponse",responseVo);
            return "pay";
        }else{
            String msg = "下单失败";
            switch (responseVo.getCode()){
                case 1: msg+="登录信息过期，请重新登录"; break;
                case 2: msg+="订单商品价格发生变化，请确认后再次进行订单提交"; break;
                case 3: msg+="库存锁定失败，商品库存不足，订单构建失败"; break;
            }
            redirectAttributes.addFlashAttribute("msg",msg);
            return "redirect:http://order.guli.com/toTrade";
        }

    }


    /**
     * 跳转到订单页面的
     * **/
    @RequestMapping("/list.html")
    public String OrderListPage(){
        return "list";
    }

    /**
     * 跳转到订单页面的
     * **/
    @RequestMapping("/confirm.html")
    public String OrderConfirmPage(){

       return "confirm";
    }
    /**
     *订单确认页，需要展示所有结算的商品数据,返回需要的数据
     * **/
    @RequestMapping("/toTrade")
    public String toTrade(Model model) throws ExecutionException, InterruptedException {
        OrderConfirmVo orderConfirmVo =  orderService.confirmOrder();
        System.out.println("数据："+orderConfirmVo);
        model.addAttribute("confirmData",orderConfirmVo);
        return "confirm";
    }

    /**
     * 跳转到订单页面的
     * **/
    @RequestMapping("/pay.html")
    public String OrderPayPage(){
        return "pay";
    }


    /**
     * 跳转到订单页面的
     * **/
    @RequestMapping("/detail.html")
    public String OrderDetailage(){
        return "detail";
    }



    @Autowired
    RabbitTemplate rabbitTemplate;

    @ResponseBody
    //测试订单生成，检测mq
    @RequestMapping("/test/createOrder")
    public String createOrderTest(){
        //订单下单成功
        OrderEntity entity = new OrderEntity();
        entity.setOrderSn(UUID.randomUUID().toString());
        rabbitTemplate.convertAndSend("order-event-exchange","order.create.order",entity);

return "ok";
    }



}
