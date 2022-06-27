package com.yuan.guli.guliorder.listener;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.yuan.guli.guliorder.config.AlipayTemplate;
import com.yuan.guli.guliorder.service.OrderService;
import com.yuan.guli.guliorder.vo.PayAsyncVo;
import com.yuan.guli.guliorder.vo.PayVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@RestController
public class OrderPayedListener {
    /**
     *  只要收到支付宝给的异步通知，告诉我们订单支付成功，返回success，支付宝也就不再通知了
     * **/


    @Autowired
    AlipayTemplate alipayTemplate;
    @Autowired
    OrderService orderService;

    @RequestMapping("/payed/notify")
    public String handlerAlipayed(PayAsyncVo payVo, HttpServletRequest req) throws UnsupportedEncodingException, AlipayApiException {

        //获取支付宝POST过来反馈信息
        Map<String,String> params = new HashMap<String,String>();
        Map<String,String[]> requestParams = req.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
            valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }

        boolean signVerified = AlipaySignature.rsaCheckV1(params, alipayTemplate.alipay_public_key, alipayTemplate.getCharset(),alipayTemplate.getSign_type()); //调用SDK验证签名

        if(signVerified){ //判断签名是否验证成功(验证成功则是通过支付宝发送的数据)
            /**
             * trade_status(支付状态):TRADE_SUCCESS(支付成功)
             * out_trade_no(订单号)：2022050922001480630505612412
             * trade_no(支付宝给予的流水号)：
             * **/
            //使用传过来的payVo数据内的订单和状态，修改订单的状态
            String result =  orderService.handlerPayResult(payVo); //返回result状态：success 或 其他
            return  "success";
        }else{
            return  "error";
        }
    }

}

//        Map<String, String[]>parameterMap = req.getParameterMap();
//        for (String key:parameterMap.keySet()){
////            System.out.println(req.getParameter(key));
//            System.out.println("参数名："+key+ "，值："+req.getParameter(key));
//        }