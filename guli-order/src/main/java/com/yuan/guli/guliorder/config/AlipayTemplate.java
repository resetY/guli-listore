package com.yuan.guli.guliorder.config;


import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.yuan.guli.guliorder.vo.PayVo;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "alipay")
@Component
@Data
public class AlipayTemplate {

    //在支付宝创建的应用的id
    public static String app_id = "2021000119689138";

    // 商户私钥，您的PKCS8格式RSA2私钥
    public static String merchant_private_key = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDn/mA2Idm6qhLQOd+EMHdgVQpuDy+dT6Z3j7dc0fJE/wJ3PwfEIr2rNNeux7NpO4OivpM4kr/qrrpfJCMYiVDYONBNILdAe0jfGOOdxV+ztWU+p3jWc4GiZf/tZf0GPfMHPCX4m0bqryT1eESDgpvVqbRFn0k6nlmNwlqziwFXqMo11PNv0Cs5O2dzCUReBUjGyF0BR2gmkFgQIYx2QeiCFGvOlRBqmqaJEO087EGBV+g2/40HUXMO910NsXRTsW3T8MAQfIwx6qsl9QotB1jXkNjL0PE9hGe9VHAgcermfLSvCJI0c+MQ6Sk0vxxNBf7SbDhYNxpDVvw3UYHV+6GrAgMBAAECggEAKKqOcu68G9x0L8v1Qla8lkZR7bj7v7eWlp66paqnrTCGXiDOW7M7TCeXJywaZvfnuz9szx3qRlkaklLjVAm+AijzWpHBjawOugzC2YUfoYDKnpD8vZrBvpXiptqZ3948G6iPf2S4qarJ772FRQhACNQWpZ2mWIApco//FjnnolW+oyKw+INAkXotw5P+DoHuDbplMIGiKcueJhhdXbtbxzQRf0Xg4BxGnwUcDkp+K7ZeCDbJmlNW5Gn+jDDhHxUwT1cgF8RcXVSRzEUMonax3s/BvAjxSYUtcTszrzYLFIqOfXDJ4VY1IBG3EkMcG3uBymxXv4FVe0sQmpWLXHpAAQKBgQD61di1rNVqCoDKTJCIMW3lAL4eNT8o5wwzxg8wYmv/0LmCmDAWOodaDE3G3duHiwB5AB0wTA+YWQmnnx3lJCrncxArOMQEGm46q/UhzVrQ4fkW/vKYdwMnh6L8yK9fBT0SeH2CFdmWh/Q71iTykSzsGVv6q8cbsBluQm6QBZgsqwKBgQDsxTb61n7Ur+pobnRrigG3kcvI5Nagr+Cqq0N8YyaiCiNz1ZsrQfdWk1HgxOQ8rSYDkbIwI2/SVlq+67yfOyagqXJr3VOJevEMn/9aTQ8yn0W9Qkpwm+6OJoaONVCUM3wtzQWNOEWnUHhVZG/FOBzgrKA8skvfRjbfH4j2Z3BfAQKBgHiqfQjQrw3HJbOr52+ND8e1dsADb3HWzDjv04i8ICY9G7DmU2OuHkTEXHoJaj0nMkADPQdp36qzUoCve+sqXfVfleyk7y7mJBbXeCIdrs2Dl5dnZ/7OadcPWenvBOZaF8oV6pK0viq7FlPerYF/MzQU8vmAv1TjfyhajuN7vXvnAoGBAJ2oaIa4gzWuecDWI9Fj0ObiPzbjIgNXeZtLdSGDiDsH0zmTd8JjJ4ZPjIrFsFCy3wgrYOJrBk+hzsUoha3lnkAG9LjcZH68DXxhoZAIleEYa0CtvpGjU0L2ZkyVNbWpXrvEtDinysGxdJ8vxLknNxtn9z7TXnAq+eiyv+mLUSkBAoGBAJSjYLPQoo/wImgSpOoxSOhW3R5m5pSf05lMbWKnfX7HDMam9UdDQkMmHmdmxpYxOYIt05wiUyDGnxDNelI/6gZHri7tkA8uXaEoORkGHgYXLCt4PpDXKXRjyBYIDAAo8UUaoUF/XQ2pZFoKGo7R9hGrpAeJIio/szrjax2eahRM";
    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    public static String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjTFJ5Zth+fMBkcU8PLnj01VXEUuLCWhkMzx8BVL1DElJWtzIc5OALLS12TrNzOwGIXqhhjqrSoLUPrJbTsZm3FdIT3Z6nQ9vYAX7cfUxuBCb5Vmf/8cfycwkTrbxwrRVjPiQQArz/IOhJNeSSF4ikastGO/67RwZRYpJ/8Z45CcddGuj32g4d/tDD3MpA7w8BzcjFAme4FI/EmHYv+taWtqdcv4UObG/0oUpApIKVMTcwveoeDQrZj454RLqrxqqXt6VVDK86jvXcep9M8YqlT/4RiB/JY97cCP+Eo/NOYAoyYTIlKeeGRJVLGD1ZiOyf/rwzaEXCy+ufYEimtzwQwIDAQAB";

    // 服务器[异步通知]页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    // 支付宝会悄悄的给我们发送一个请求，告诉我们支付成功的信息
    public static String notify_url = "http://nzjgjq.natappfree.cc/payed/notify";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    //同步通知，支付成功，一般跳转到成功页
    //这里我们设置：跳转到用户的订单列表页面
    public static String return_url = "http://member.guli.com/memberOrder.html";

    // 签名方式
    private  String sign_type = "RSA2";

    // 字符编码格式
    private  String charset = "utf-8";

    // 支付宝网关； https://openapi.alipaydev.com/gateway.do
    public static String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    public  String pay(PayVo vo) throws AlipayApiException {

        //AlipayClient alipayClient = new DefaultAlipayClient(AlipayTemplate.gatewayUrl, AlipayTemplate.app_id, AlipayTemplate.merchant_private_key, "json", AlipayTemplate.charset, AlipayTemplate.alipay_public_key, AlipayTemplate.sign_type);
        //1、根据支付宝的配置生成一个支付客户端
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
                app_id, merchant_private_key, "json",
                charset, alipay_public_key, sign_type);

        //2、创建一个支付请求 //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(return_url);
        alipayRequest.setNotifyUrl(notify_url);

        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = vo.getOut_trade_no();
        //付款金额，必填
        String total_amount = vo.getTotal_amount();
        //订单名称，必填
        String subject = vo.getSubject();
        //商品描述，可空
        String body = vo.getBody();

        String timeoutExpress = "1m"; //设置支付超时时间

        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"total_amount\":\""+ total_amount +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"timeout_express\":\""+ timeoutExpress +"\","
                + "\"body\":\""+ body +"\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        String result = alipayClient.pageExecute(alipayRequest).getBody();

        //会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
        System.out.println("支付宝的响应："+result);




        return result;

    }
}
