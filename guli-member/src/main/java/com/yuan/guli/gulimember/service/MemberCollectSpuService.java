package com.yuan.guli.gulimember.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuan.common.utils.PageUtils;
import com.yuan.guli.gulimember.entity.MemberCollectSpuEntity;

import java.util.Map;

/**
 * 会员收藏的商品
 *
 * @author lzy
 * @email 1716827691@qq.com
 * @date 2022-03-08 10:30:09
 */
public interface MemberCollectSpuService extends IService<MemberCollectSpuEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

