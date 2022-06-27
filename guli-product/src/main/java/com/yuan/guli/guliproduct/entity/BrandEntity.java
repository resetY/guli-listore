package com.yuan.guli.guliproduct.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import com.yuan.common.valid.AddGroup;
import com.yuan.common.valid.ListValue;
import com.yuan.common.valid.UpdateGroup;
import com.yuan.common.valid.UpdateStatusGroup;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;

/**
 * 品牌
 * 
 * @author lzy
 * @email 1716827691@qq.com
 * @date 2022-03-07 21:33:41
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 品牌id
	 */
	@TableId
	@NotNull(message = "id无法被指定",groups = {UpdateGroup.class})
	@Null(message = "id已存在",groups = {AddGroup.class})
	private Long brandId;
	/**
	 * 品牌名
	 * @NotNull 不能为空
	 * @NotEntity 不能为null，且不能为空串
	 * @NotBlank 		不能为null，且不能为空串
	 */
	@NotBlank(message = "品牌名称不能为空",groups = {AddGroup.class,UpdateGroup.class})
	private String name;
	/**
	 * 品牌logo地址
	 */
	@URL(message = "logo必须是一个合法的url地址",groups = {AddGroup.class, UpdateGroup.class})
	//@NotEmpty(message = "地址不能为空",groups = {AddGroup.class})
	private String logo;
	/**
	 * 介绍
	 */
	private String descript;
	/**
	 * 显示状态[0-不显示；1-显示]
	 */
	@NotNull(groups = {AddGroup.class,UpdateStatusGroup.class})
	@ListValue(values = {0,1},message = "数值范围不正确",groups = {AddGroup.class,UpdateStatusGroup.class})
	private Integer showStatus;
	/**
	 * 检索首字母
	 */
	@Size(max = 1,message = "检索字母只能是一个数量的字母",groups = {AddGroup.class,UpdateGroup.class}) //设置最大长度,最小长度默认为0
	@Pattern(regexp = "^[a-zA-Z]$",message = "检索字母必须为英文",groups = {AddGroup.class,UpdateGroup.class}) //自定义规则注解，传入正则表达式
	//@NotBlank(message = "不能为空")
	private String firstLetter;
	/**
	 * 排序
	 */
	@Min(value = 0 ,message = "最小排序只能为大于等于0",groups = {AddGroup.class,UpdateGroup.class}) //最小排序设置为0
	//@NotNull(message = "不能为空",groups = {AddGroup.class,UpdateGroup.class})
	private Integer sort;

}
