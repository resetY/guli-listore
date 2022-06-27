package com.yuan.common.valid;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

/**
 * 自定义的校验器
 * */
public class ListValueConstraintValidator implements ConstraintValidator<ListValue,Integer> {

    private Set<Integer> set = new HashSet<>();
    @Override  //初始化方法
    public void initialize(ListValue constraintAnnotation) {
        int[] values = constraintAnnotation.values();
        for (int value : values) {
            set.add(value);
        }

    }

  /**
   *  判断是否校验成功
   * @param  value 需要校验的值
   * @param  context 上下文信息
   * **/
    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {

        return set.contains(value); //set包不包含value，不包含返回false
    }
}
