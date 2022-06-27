package com.yuan.guli.guliproduct.service.impl;

import com.yuan.guli.guliproduct.entity.AttrEntity;
import com.yuan.guli.guliproduct.vo.AttrGroupRelationVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuan.common.utils.PageUtils;
import com.yuan.common.utils.Query;

import com.yuan.guli.guliproduct.dao.AttrAttrgroupRelationDao;
import com.yuan.guli.guliproduct.entity.AttrAttrgroupRelationEntity;
import com.yuan.guli.guliproduct.service.AttrAttrgroupRelationService;


@Service("attrAttrgroupRelationService")
public class AttrAttrgroupRelationServiceImpl extends ServiceImpl<AttrAttrgroupRelationDao, AttrAttrgroupRelationEntity> implements AttrAttrgroupRelationService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrAttrgroupRelationEntity> page = this.page(
                new Query<AttrAttrgroupRelationEntity>().getPage(params),
                new QueryWrapper<AttrAttrgroupRelationEntity>()
        );

        return new PageUtils(page);
    }

    /***
     * 多次请求的进行批量删除方式：
     * */
//    @Autowired
//    AttrAttrgroupRelationDao attrAttrgroupRelationDao;
//    @Override
//    public void deleteRelation(AttrGroupRelationVo[] relationVo) {
//        for(int i = 0;i<relationVo.length;i++){
//            Long attrGroupId = relationVo[i].getAttrGroupId();
//            Long attrId= relationVo[i].getAttrId();
//            QueryWrapper <AttrAttrgroupRelationEntity> queryWrapper = new QueryWrapper();
//            queryWrapper.eq("attr_id",attrId).and((g)->{
//                g.eq("attr_group_id",attrGroupId);
//            });
//
//            attrAttrgroupRelationDao.delete(queryWrapper);
//        }
//
//    }


    /**
     * 单次请求批量删除
     * delete from pms_attrgroup_relation where
     * (attr_id = 1 and attr_group_id  = 1) or (attr_id = 2 and attr_group_id  = 3)
     * 一直or下去
     * */

    @Autowired
    AttrAttrgroupRelationDao attrAttrgroupRelationDao;
    @Override
    public void deleteRelation(AttrGroupRelationVo[] relationVo) {
        List<AttrAttrgroupRelationEntity> entitys = Arrays.asList(relationVo).stream().map((item) -> {
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(item, attrAttrgroupRelationEntity);
            return attrAttrgroupRelationEntity;
        }).collect(Collectors.toList());//将返回的attrREntity收集为集合
        attrAttrgroupRelationDao.deleteBatchRelation(entitys);
    }


    /**
     * 新增
     * **/
    @Override
    public void saveRelation( List<AttrGroupRelationVo> relationVo) {
        List<AttrAttrgroupRelationEntity > entities = relationVo.stream().map(r -> {
           AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(r, relationEntity);
            return relationEntity;
        }).collect(Collectors.toList());

        this.saveBatch(entities);
    }




}