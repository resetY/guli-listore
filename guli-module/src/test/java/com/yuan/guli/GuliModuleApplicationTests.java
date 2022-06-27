package com.yuan.guli;


import com.aliyun.oss.OSS;

import com.yuan.guli.auth.component.Sms;
import com.yuan.guli.auth.utils.HttpUtils;
import org.apache.http.HttpResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

@SpringBootTest()
@RunWith(SpringRunner.class)
public class GuliModuleApplicationTests {

    @Test
    public void contextLoads() {
    }

    @Autowired
    OSS ossClient;
    @Test
    public void Osstest() throws IOException {
        InputStream inputStream = new FileInputStream("D:\\test.jpg");
        ossClient.putObject("gulilzy","test4.jpg",inputStream);

        ossClient.shutdown();
        inputStream.close();
        System.out.println("上传完毕");
    }


//
//    @Test
//    public void Toolstest()  {
//        String host = "https://feginesms.market.alicloudapi.com";// 【1】请求地址 支持http 和 https 及 WEBSOCKET
//        String path = "/codeNotice";// 【2】后缀
//        String appcode = "f3f8917d4077454ca29e71a09c31895b"; // 【3】开通服务后 买家中心-查看AppCode
//        String sign = "175622"; // 【4】请求参数，详见文档描述
//        String skin = "1"; // 【4】请求参数，详见文档描述
//        String param = "123456"; // 【4】请求参数，详见文档描述
//        String phone = "15816703457"; // 【4】请求参数，详见文档描述
//        String urlSend = host + path + "?sign=" + sign + "&skin=" + skin+ "&param=" + param+ "&phone=" + phone; // 【5】拼接请求链接
//
//        try {
//            URL url = new URL(urlSend);
//            HttpURLConnection httpURLCon = (HttpURLConnection) url.openConnection();
//            httpURLCon.setRequestProperty("Authorization", "APPCODE " + appcode);// 格式Authorization:APPCODE
//            // (中间是英文空格)
//            int httpCode = httpURLCon.getResponseCode();
//            if (httpCode == 200) {
//                String json = read(httpURLCon.getInputStream());
//                System.out.println("正常请求计费(其他均不计费)");
//                System.out.println("获取返回的json:");
//                System.out.print(json);
//            } else {
//                Map<String, List<String>> map = httpURLCon.getHeaderFields();
//                String error = map.get("X-Ca-Error-Message").get(0);
//                System.out.println(error);
//                if (httpCode == 400 && error.equals("Invalid AppCode `not exists`")) {
//
//                    System.out.println("AppCode错误 ");
//                    System.out.println(appcode);
//                } else if (httpCode == 400 && error.equals("Invalid Url")) {
//                    System.out.println("请求的 Method、Path 或者环境错误");
//                } else if (httpCode == 400 && error.equals("Invalid Param Location")) {
//                    System.out.println("参数错误");
//                } else if (httpCode == 403 && error.equals("Unauthorized")) {
//                    System.out.println("服务未被授权（或URL和Path不正确）");
//                } else if (httpCode == 403 && error.equals("Quota Exhausted")) {
//                    System.out.println("套餐包次数用完 ");
//                } else if (httpCode == 403 && error.equals("Api Market Subscription quota exhausted")) {
//                    System.out.println("套餐包次数用完，请续购套餐");
//                } else {
//                    System.out.println("参数名错误 或 其他错误");
//                    System.out.println(error);
//                }
//            }
//
//        } catch (MalformedURLException e) {
//            System.out.println("URL格式错误");
//        } catch (UnknownHostException e) {
//            System.out.println("URL地址错误");
//        } catch (Exception e) {
//            // 打开注释查看详细报错异常信息
//            // e.printStackTrace();
//        }
//
//    }
//
//    /*
//     * 读取返回结果
//     */
//    private static String read(InputStream is) throws IOException {
//        StringBuffer sb = new StringBuffer();
//        BufferedReader br = new BufferedReader(new InputStreamReader(is));
//        String line = null;
//        while ((line = br.readLine()) != null) {
//            line = new String(line.getBytes(), "utf-8");
//            sb.append(line);
//        }
//        br.close();
//        return sb.toString();
//    }
//

    @Autowired
    Sms sms;

    @Test
    public void Tools2(){

        sms.UseTools("5201314","15816703457");
    }


    @Test
    public void Tools(){

            String host = "https://dfsns.market.alicloudapi.com";
            String path = "/data/send_sms";
            String method = "POST";
            String appcode = "f3f8917d4077454ca29e71a09c31895b";
            Map<String, String> headers = new HashMap<String, String>();
            //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
            headers.put("Authorization", "APPCODE " + appcode);
            //根据API的要求，定义相对应的Content-Type
            headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            Map<String, String> querys = new HashMap<String, String>();
            Map<String, String> bodys = new HashMap<String, String>();
            bodys.put("content", "code:5201314");
            bodys.put("phone_number", "15816703457");
            bodys.put("template_id", "TPL_0000");


            try {
                /**
                 * 重要提示如下:
                 * HttpUtils请从
                 * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java
                 * 下载
                 *
                 * 相应的依赖请参照
                 * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
                 */
             HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
                System.out.println(response.toString());
                //获取response的body
                //System.out.println(EntityUtils.toString(response.getEntity()));
            } catch (Exception e) {
                e.printStackTrace();
            }

    }


}
