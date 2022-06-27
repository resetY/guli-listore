package com.yuan.guli.guliproduct.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * spu信息介绍
 * 
 * @author lzy
 * @email 1716827691@qq.com
 * @date 2022-03-07 21:33:41
 */
@Data
@TableName("pms_spu_info_desc")
public class SpuInfoDescEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 商品id
	 */
	@TableId(type = IdType.INPUT) //这个表的spuid不是自增的，需要告知
	private Long spuId;
	/**
	 * 商品介绍
	 */
	private String decript;

}
