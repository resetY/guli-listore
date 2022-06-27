package com.yuan.guli.guliproduct.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuan.common.utils.PageUtils;
import com.yuan.guli.guliproduct.entity.AttrEntity;
import com.yuan.guli.guliproduct.vo.AttrGroupRelationVo;
import com.yuan.guli.guliproduct.vo.AttrRespVo;
import com.yuan.guli.guliproduct.vo.AttrVo;
import com.yuan.guli.guliproduct.vo.SkuItemVo;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author lzy
 * @email 1716827691@qq.com
 * @date 2022-03-07 21:33:41
 */
public interface AttrService extends IService<AttrEntity> {



    PageUtils queryPage(Map<String, Object> params);


    void saveAttr(AttrEntity attrEntity);


    void saveAttrVo(AttrVo attrVo);

    /**
     * 模糊查询功能添加
     * */
    PageUtils queryBaseAttr(Map<String, Object> params,Long catelogId);


    /**
     * 获取信息，回显数据r
     * */
    AttrRespVo  getInfo(Long attrId);


    /**
     * 类对象修改方法
     * */
    void updateAttr(AttrVo attrVo);

    /**
     * 分类销售属性展示
     * */
    PageUtils querySaleAttr(Map<String, Object> params,Integer attrType);

    /**
     * 根据分组id查找关联基本属性
     * */
  List<AttrEntity> getattrgroupRelation(Long attrGroudId);



  PageUtils getNoRelation(Map<String, Object> params,Long attrGroupId);

  /**
   * 查询销售属性
   * */
    PageUtils saleList(Map<String, Object> params,Long catelogId);

    /**
     * 雷神版：使用网址注入方式，动态改变获取的是base  还是  sale
     * */
    PageUtils queryBaseOrSale(Map<String, Object> params, Long catelogId, String attrType);

    List<Long> selectSearchAttrIds(List<Long> attrIds);
}

