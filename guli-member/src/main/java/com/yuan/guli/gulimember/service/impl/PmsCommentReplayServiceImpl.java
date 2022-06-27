package com.yuan.guli.gulimember.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuan.common.utils.PageUtils;
import com.yuan.common.utils.Query;

import com.yuan.guli.gulimember.dao.PmsCommentReplayDao;
import com.yuan.guli.gulimember.entity.PmsCommentReplayEntity;
import com.yuan.guli.gulimember.service.PmsCommentReplayService;


@Service("pmsCommentReplayService")
public class PmsCommentReplayServiceImpl extends ServiceImpl<PmsCommentReplayDao, PmsCommentReplayEntity> implements PmsCommentReplayService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PmsCommentReplayEntity> page = this.page(
                new Query<PmsCommentReplayEntity>().getPage(params),
                new QueryWrapper<PmsCommentReplayEntity>()
        );

        return new PageUtils(page);
    }

}