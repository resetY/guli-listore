package com.yuan.guli.guliproduct.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yuan.guli.guliproduct.dao.*;
import com.yuan.guli.guliproduct.entity.*;
import com.yuan.guli.guliproduct.service.AttrAttrgroupRelationService;
import com.yuan.guli.guliproduct.service.AttrService;
import com.yuan.guli.guliproduct.service.ProductAttrValueService;
import com.yuan.guli.guliproduct.vo.AttrGroupWithAttrsVo;
import com.yuan.guli.guliproduct.vo.SkuItemVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuan.common.utils.PageUtils;
import com.yuan.common.utils.Query;

import com.yuan.guli.guliproduct.service.AttrGroupService;
import org.w3c.dom.Attr;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {



    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 利用cid检索分组
     * */
    @Override
    public PageUtils queryPageByCid(Map<String, Object> params, Long catelogId) {
        String key = (String) params.get("key"); //如果有key，那么key也是检索条件
        QueryWrapper<AttrGroupEntity> attr = new QueryWrapper<>();
       if(catelogId==0) {
           //查询所有：
           if(!StringUtils.isEmpty(key)){ //如果key不为空，那么开始检索
               attr.and((obj)->{
                   //判断cid是否等于key ，或 名字是否like
                   obj.eq("attr_group_id",key).or().like("attr_group_name",key);
               });
           }
           IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params),attr);

           return  new PageUtils(page); //pageUtils：传入page可以进行分页解析
       }else{ //catelog_id进行查询
           //先匹配分组id
         attr = new QueryWrapper<AttrGroupEntity>().eq("catelog_id",catelogId);
           if(!StringUtils.isEmpty(key)){ //如果key不为空，那么开始检索
                attr.and((obj)->{
                    //判断cid是否等于key ，或 名字是否like
                    obj.eq("attr_group_id",key).or().like("attr_group_name",key);
                });
           }
           //注入条件attr和数据
           IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params),attr);
           return new PageUtils(page);
       }

    }


    @Autowired
    private CategoryDao categoryDao;
    @Override
    public AttrGroupEntity queryByPath(Long attrGroupId) {
        List<Long> paths = new  ArrayList();

        AttrGroupEntity attrgroup = baseMapper.selectById(attrGroupId);

        paths.add(attrgroup.getCatelogId());//先把当前层次的分类菜单id添加进去

        //使用方法：将父类id都添加到数组内
        List<Long> patentsPath = findPatents(attrgroup.getCatelogId(),paths);
        //将数组反转
        Collections.reverse(patentsPath); //逆序


        //得到的List转化为数组形态，然后注入
        attrgroup.setCatelogPath(patentsPath.toArray(new Long[0]));

        return  attrgroup;
    }

    @Autowired
    AttrGroupDao attrGroupDao;
    @Autowired
    AttrService attrService;
    @Override
    public List<AttrGroupWithAttrsVo> getAttrGroupWithAttrsByCid(Long catelogId) {
        //所有分组
        List<AttrGroupEntity> attrGroups = this.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));
     attrGroups.forEach(System.out::println);
        List<AttrGroupWithAttrsVo> attrVos = attrGroups.stream().map((group) -> {
            AttrGroupWithAttrsVo vo = new AttrGroupWithAttrsVo();
            BeanUtils.copyProperties(group,vo);
            //调用业务层的获取分组id和属性关联的 attr 数据：
            List<AttrEntity> attrs = attrService.getattrgroupRelation(vo.getAttrGroupId());
                vo.setAttrs(attrs);
            return vo;
        }).collect(Collectors.toList());
        if(attrVos == null || attrVos.size()==0){
            return  null;
        }
        return attrVos;
    }


    @Override
    public List<SkuItemVo.SpuItemAttrGroupAttrVo> getAttrGroupWithAttrsBySpuid(Long catalogId,Long spuId) {

        //1.当前spu内有多少对应的属性分组:使用三表联合查询
        List<SkuItemVo.SpuItemAttrGroupAttrVo> vos =  attrGroupDao.getAttrGroupWithAttrsBySpuid(catalogId,spuId);
        return vos;
    }


    private List<Long> findPatents(Long catelogId,List<Long> paths){
        CategoryEntity category = categoryDao.selectById(catelogId);
        if(category.getParentCid() != 0){ //查看有无父类id
            paths.add(category.getParentCid());
            findPatents(category.getParentCid(),paths);
        }
        return paths;
    }


}