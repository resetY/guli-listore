package com.yuan.guli.guliproduct.dao;

import com.yuan.guli.guliproduct.entity.AttrGroupEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuan.guli.guliproduct.vo.SkuItemVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


import java.util.List;

/**
 * 属性分组
 * 
 * @author lzy
 * @email 1716827691@qq.com
 * @date 2022-03-07 21:33:41
 */
@Mapper
public interface AttrGroupDao extends BaseMapper<AttrGroupEntity> {

    List<SkuItemVo.SpuItemAttrGroupAttrVo> getAttrGroupWithAttrsBySpuid(@Param("catalogId") Long catalogId,
                                                                        @Param("spuId")Long spuId);
}
