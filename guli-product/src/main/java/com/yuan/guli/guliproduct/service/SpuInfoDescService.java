package com.yuan.guli.guliproduct.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuan.common.utils.PageUtils;
import com.yuan.guli.guliproduct.entity.SpuInfoDescEntity;
import com.yuan.guli.guliproduct.vo.SpuSaveVo;

import java.util.Map;

/**
 * spu信息介绍
 *
 * @author lzy
 * @email 1716827691@qq.com
 * @date 2022-03-07 21:33:41
 */
public interface SpuInfoDescService extends IService<SpuInfoDescEntity> {

    PageUtils queryPage(Map<String, Object> params);


    void saveSpuInfoDesc(SpuInfoDescEntity descEntity);
}

