package com.yuan.guli.gulimember.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data //提交注册页面表单的vo
public class RegistVo {


    private String userName;

    private  String passWord;

    private  String phone;


}
