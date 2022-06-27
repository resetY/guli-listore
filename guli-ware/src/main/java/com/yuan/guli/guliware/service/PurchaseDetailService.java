package com.yuan.guli.guliware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuan.common.utils.PageUtils;
import com.yuan.guli.guliware.entity.PurchaseDetailEntity;

import java.util.Map;

/**
 * 
 *
 * @author lzy
 * @email 1716827691@qq.com
 * @date 2022-03-08 10:57:57
 */
public interface PurchaseDetailService extends IService<PurchaseDetailEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPurchasedetail(Map<String, Object> params);
}

