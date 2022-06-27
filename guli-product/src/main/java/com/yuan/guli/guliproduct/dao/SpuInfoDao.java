package com.yuan.guli.guliproduct.dao;

import com.yuan.common.constant.ProductConstant;
import com.yuan.guli.guliproduct.entity.SpuInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * spu信息
 * 
 * @author lzy
 * @email 1716827691@qq.com
 * @date 2022-03-07 21:33:41
 */
@Mapper
public interface SpuInfoDao extends BaseMapper<SpuInfoEntity> {


    void updateStatus(@Param("spuId") Long spuId, @Param("status") int code);
}
