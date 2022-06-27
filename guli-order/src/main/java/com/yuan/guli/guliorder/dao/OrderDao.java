package com.yuan.guli.guliorder.dao;

import com.yuan.guli.guliorder.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 订单
 * 
 * @author lzy
 * @email 1716827691@qq.com
 * @date 2022-03-08 10:53:42
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {

    void updateStatus(@Param("ordrSn") String ordrSn, @Param("code") Integer code);
}
