package com.yuan.guli.guliware.dao;

import com.yuan.guli.guliware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品库存
 * 
 * @author lzy
 * @email 1716827691@qq.com
 * @date 2022-03-08 10:57:56
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {

   Long getStockAndskuId(@Param("skuId") Long skuId);

    /**
     * 修改库存被锁住的方法
     * */
    Integer lockSkuStock(@Param("skuId") Long skuId, @Param("wareId") Long wareId, @Param("lockNum") Integer lockNum);

    List<Long> getWareIdStock(@Param("skuId") Long skuId);

    void unLockStock(@Param("skuId") Long skuId, @Param("wareId") Long wareId, @Param("num") Integer num);
}
