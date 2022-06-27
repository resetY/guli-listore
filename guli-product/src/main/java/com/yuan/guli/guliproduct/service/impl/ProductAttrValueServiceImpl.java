package com.yuan.guli.guliproduct.service.impl;

import com.yuan.guli.guliproduct.entity.SkuSaleAttrValueEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuan.common.utils.PageUtils;
import com.yuan.common.utils.Query;

import com.yuan.guli.guliproduct.dao.ProductAttrValueDao;
import com.yuan.guli.guliproduct.entity.ProductAttrValueEntity;
import com.yuan.guli.guliproduct.service.ProductAttrValueService;
import org.springframework.transaction.annotation.Transactional;


@Service("productAttrValueService")
public class ProductAttrValueServiceImpl extends ServiceImpl<ProductAttrValueDao, ProductAttrValueEntity> implements ProductAttrValueService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<ProductAttrValueEntity> page = this.page(
                new Query<ProductAttrValueEntity>().getPage(params),
                new QueryWrapper<ProductAttrValueEntity>()
        );
        return new PageUtils(page);
    }

    @Override
    public void saveAttrValue(List<ProductAttrValueEntity> attrValueEntityList) {
        this.saveBatch(attrValueEntityList);
    }



    /**
     * 根据spuid查找规格参数所有内容
     * */
    @Override
    public List<ProductAttrValueEntity> listforSpu(Long spuId) {

        QueryWrapper<ProductAttrValueEntity> wrapper = new QueryWrapper();
        wrapper.eq("spu_id",spuId);
        List<ProductAttrValueEntity> productAttrValueEntities = baseMapper.selectList(wrapper);
    return  productAttrValueEntities;
    }

    /**
     * 商品维护：spu管理
     * */
    @Transactional
    @Override
    public void updateAttrValue(Long spuId, List<ProductAttrValueEntity> valueEntities) {
        QueryWrapper<ProductAttrValueEntity> wrapper = new QueryWrapper();
        wrapper.eq("spu_id", spuId);
        baseMapper.delete(wrapper); //删除这个spu——id的所有属性，然后再进行添加
        List<ProductAttrValueEntity> attrValueEntityList = valueEntities.stream().map(item -> {
            item.setSpuId(spuId);
          //  item.setAttrSort();不需要顺序
            return item;
        }).collect(Collectors.toList());
        this.saveBatch(attrValueEntityList);
    }

    @Override
    public List<ProductAttrValueEntity> getAttrsByskuId(Long spuId) {

        QueryWrapper<ProductAttrValueEntity> wrapper = new QueryWrapper();
        wrapper.eq("spu_id",spuId);
        List <ProductAttrValueEntity> productAttrValueEntities = baseMapper.selectList(wrapper);
        return productAttrValueEntities ;

    }
}