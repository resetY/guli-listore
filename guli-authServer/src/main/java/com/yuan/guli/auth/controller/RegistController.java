package com.yuan.guli.auth.controller;

import com.yuan.common.utils.R;
import com.yuan.guli.auth.feign.MemberFeignService;
import com.yuan.guli.auth.feign.ModuleFeignService;
import com.yuan.guli.auth.vo.RegistVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Controller
public class RegistController {
 /**
   @RequestMapping("regist.html") //访问/ 啥的都进行index.html的跳转
    public String login(){
        return  "regist";
        **/



 @Autowired
 ModuleFeignService moduleFeign;

 //  //设置随机验证码
//  String code  = "";
//  for (int i =0;i<=4;i++) {
//   Integer num  = random.nextInt(10)%(0+1) +0;
//   code+=num.toString();
//  } 或

 public static String SMSCODE="sms:code:";  //key前缀

 @Autowired
 StringRedisTemplate redisTemplate;

 public RegistController() {
 }

 @Transactional
 @ResponseBody
 @RequestMapping("/sms/sendCode")
 public R sendCode(@RequestParam("phone") String phone){ //接收发送过来的手机号码，然后远程调用进行发送短信服务
  //Todo 1.接口防刷：

 //2.将验证码存储在缓存中:手机号为：key  按照手机号查找到code验证码
  String redisCode = (String) redisTemplate.opsForValue().get(SMSCODE+phone); //
  System.out.println("内容为："+redisCode);
 if(!StringUtils.isEmpty(redisCode)){
  long l = Long.parseLong(redisCode.split("_")[0]);//进行分割，然后获取第一部分的时间信息

  //获取的时间 - 缓存中存在的时间
  if(System.currentTimeMillis() - l<60000){//在60秒内防止再次发送验证码
  return  R.error(10002,"验证码获取频繁，请稍后再试");
  }
 }

  //code加上系统时间：
  String code = UUID.randomUUID().toString().substring(0, 5)+"_"+System.currentTimeMillis();
  code = code.split("_")[0];

  redisTemplate.opsForValue().set(SMSCODE+phone,code,120, TimeUnit.SECONDS); //两分钟有效的验证缓存

  System.out.println(code);
  moduleFeign.tools(code,phone);
  return R.ok();
 }



 //TODO 注意：分布式下的session使用是会出现问题的，需要进行解决
 /**
  * 点击注册成功后，进行跳转
  * */
 @Autowired
 MemberFeignService memberFeignService;
 @RequestMapping("/regist") //@Valid:开启校验，BindResult：校验结果的存储
 //重定向进行数据共享方式：RedirecAttributes
 public String regist(@Valid RegistVo registVo, BindingResult bindResult,
                      RedirectAttributes redirectAttributes,
                      HttpSession session) {

  if (bindResult.hasErrors()) {
   Map<String, String> error = bindResult.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, DefaultMessageSourceResolvable::getDefaultMessage));
   redirectAttributes.addFlashAttribute("errors", error);
   return "redirect:http://auth.guli.com/regist.html";
  }
  //如果验证码相同，才能注册
  String redisCode = redisTemplate.opsForValue().get(SMSCODE + registVo.getPhone()); //
  if (!StringUtils.isEmpty(redisCode)) { //redis获取的验证码存在
   if (registVo.getCode().equals(redisCode.split("_")[0])) {
    System.out.println("验证码输入正确");
    redisTemplate.delete(SMSCODE + registVo.getPhone()); //删除缓存的验证码
    //进行远程调用，注册
    R r = memberFeignService.regist(registVo);
    if (r.getCode() == 0) { //注册成功
     return "redirect:http://auth.guli.com/login.html";  //请求重定向
    } else {
     Map<String, String> errors = new HashMap<>();
     errors.put("code", "验证码错误");
     redirectAttributes.addFlashAttribute("errors", errors);
     return "redirect:http://auth.guli.com/regist.html";
    }
   } else {
    Map<String, String> errors = new HashMap<>();
    errors.put("code", "验证码错误");
    redirectAttributes.addFlashAttribute("errors", errors);
    return "redirect:http://auth.guli.com/regist.html";
   }

  }
  return "redirect:http://auth.guli.com/regist.html\"";  //请求重定向

 }
 }