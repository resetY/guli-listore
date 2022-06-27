package com.yuan.guli.auth.feign;


import com.yuan.common.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("guli-module")
public interface ModuleFeignService {
    /**
     *远程调用发送短信功能接口
     * **/
    @RequestMapping("/sms/sendCode")
    R tools(@RequestParam("code") String code, @RequestParam("phone") String phone);


}
