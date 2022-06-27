package com.yuan.guli.guliproduct.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuan.common.utils.PageUtils;
import com.yuan.guli.guliproduct.entity.SpuInfoDescEntity;
import com.yuan.guli.guliproduct.entity.SpuInfoEntity;
import com.yuan.guli.guliproduct.vo.SpuSaveVo;

import java.util.Map;

/**
 * spu信息
 *
 * @author lzy
 * @email 1716827691@qq.com
 * @date 2022-03-07 21:33:41
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 商品上架保存数据
     *
     */
    void saveSpuInfo(SpuSaveVo vo);

    /**
     * Spu保存基本信息
     * */
    void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity);


    PageUtils querySpuinfoPage(Map<String, Object> params);

    void upProduct(Long spuId);

    SpuInfoEntity getSpuInfoByskuId(Long skuId);
}

