package com.yuan.guli.guliproduct.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuan.common.utils.PageUtils;
import com.yuan.guli.guliproduct.entity.AttrEntity;
import com.yuan.guli.guliproduct.entity.AttrGroupEntity;
import com.yuan.guli.guliproduct.vo.AttrGroupWithAttrsVo;
import com.yuan.guli.guliproduct.vo.SkuItemVo;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author lzy
 * @email 1716827691@qq.com
 * @date 2022-03-07 21:33:41
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
    使用catID检索分页状态下的分组
    * **/
    PageUtils queryPageByCid(Map<String, Object> params, Long catelogId);


    /**
     * 根据catelogId数组，查询到分类路径
     * */
   AttrGroupEntity queryByPath(Long attrGroupId);


    List<AttrGroupWithAttrsVo> getAttrGroupWithAttrsByCid(Long catelogId);

    List<SkuItemVo.SpuItemAttrGroupAttrVo> getAttrGroupWithAttrsBySpuid(Long catalogId,Long spuId);
}

