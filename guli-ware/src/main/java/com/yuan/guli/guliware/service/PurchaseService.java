package com.yuan.guli.guliware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuan.common.utils.PageUtils;
import com.yuan.guli.guliware.entity.PurchaseEntity;
import com.yuan.guli.guliware.vo.DoneVo;
import com.yuan.guli.guliware.vo.MergeVo;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author lzy
 * @email 1716827691@qq.com
 * @date 2022-03-08 10:57:56
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryUnreceive(Map<String, Object> params);

    void saveMergo(MergeVo mergeVo);

    void getReceived(List<Long> ids);

    void Done(DoneVo doneVo);
}

