package com.yuan.guli.guliware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuan.common.utils.PageUtils;
import com.yuan.guli.guliware.entity.WareInfoEntity;
import com.yuan.guli.guliware.vo.FareVo;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 仓库信息
 *
 * @author lzy
 * @email 1716827691@qq.com
 * @date 2022-03-08 10:57:57
 */
public interface WareInfoService extends IService<WareInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryInfoPages(Map<String, Object> params);

    FareVo getFare(Long addId);
}

