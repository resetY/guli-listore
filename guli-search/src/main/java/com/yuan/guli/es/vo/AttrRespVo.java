package com.yuan.guli.es.vo;

import lombok.Data;

/**
 * 这个值对象是用来进行响应数据的
 *  响应数据 继承原有的AttrVo，获取不需要补全的字段数据
 * **/
@Data
public class AttrRespVo extends AttrVo{

    /**
     * 分类名字
     * */
    private String catelogName;
    /**
     * 分组名字
     * */
    private  String groupName;

    /**
    分组id
    * **/
    private Long attrGroupId;

    /**
     *分类 路径
     * */
    private Long[] catelogPath;


}
