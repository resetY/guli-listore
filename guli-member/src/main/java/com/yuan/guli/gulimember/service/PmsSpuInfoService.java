package com.yuan.guli.gulimember.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuan.common.utils.PageUtils;
import com.yuan.guli.gulimember.entity.PmsSpuInfoEntity;

import java.util.Map;

/**
 * spu信息
 *
 * @author lzy
 * @email 1716827691@qq.com
 * @date 2022-03-08 10:30:10
 */
public interface PmsSpuInfoService extends IService<PmsSpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

