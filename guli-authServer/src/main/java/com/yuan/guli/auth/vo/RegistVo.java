package com.yuan.guli.auth.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data //提交注册页面表单的vo
public class RegistVo {
    @NotEmpty(message = "用户名必须提交")
    @Length(min = 6,max = 18,message = "用户名必须为6-18为字符")
    private String userName;
    @NotEmpty(message = "密码不能为空")
    @Length(min = 6,max = 18,message = "密码必须为6-18位字符")
    private  String passWord;
    @NotEmpty(message = "手机号码不能为空")
    @Pattern(regexp = "^[1]([3-9])[0-9]{9}$",message = "手机号码格式不正确")
    private  String phone;
    @NotEmpty(message = "验证码不能为空")
    private  String code;

}
