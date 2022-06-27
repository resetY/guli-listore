package com.yuan.guli.auth.feign;

import com.yuan.common.to.SocailUser;
import com.yuan.common.utils.R;
import com.yuan.guli.auth.vo.LoginVo;
import com.yuan.guli.auth.vo.RegistVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("guli-member")
public interface MemberFeignService {
    @RequestMapping("/gulimember/member/regist")
    R regist(@RequestBody RegistVo registVo);


    @RequestMapping("/gulimember/member/login")
    R login(@RequestBody LoginVo loginVo);

    @RequestMapping("/gulimember/member/oauth2/login")
   R oauthLogin(@RequestBody SocailUser socailUser);
}