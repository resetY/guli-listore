package com.yuan.common.valid;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotNull;
import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

 /**
     ListValue： 自定义校验注解
        校验注解需要三个默认有的属性
  @Constrain ： 指定自定义注解使用的是哪个校验器进行的
 * **/
 @Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
 @Retention(RUNTIME)
 @Documented
 @Constraint(validatedBy = {ListValueConstraintValidator.class})
 public @interface ListValue {

  //异常内容 ：且默认从异常注解中取值
  String message() default "{com.yuan.common.valid.ListValue.message}";

  //
  Class<?>[] groups() default { }; //分组

  //
  Class<? extends Payload>[] payload() default { };

  //指定注解的values内容，默认为空
  int [] values() default{};
 }

