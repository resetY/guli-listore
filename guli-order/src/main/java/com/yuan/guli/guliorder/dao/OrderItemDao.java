package com.yuan.guli.guliorder.dao;

import com.yuan.guli.guliorder.entity.OrderItemEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单项信息
 * 
 * @author lzy
 * @email 1716827691@qq.com
 * @date 2022-03-08 10:53:42
 */
@Mapper
public interface OrderItemDao extends BaseMapper<OrderItemEntity> {
	
}
