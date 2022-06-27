package com.yuan.guli.guliproduct.service.impl;

import com.yuan.guli.guliproduct.entity.ProductAttrValueEntity;
import com.yuan.guli.guliproduct.vo.SkuItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuan.common.utils.PageUtils;
import com.yuan.common.utils.Query;

import com.yuan.guli.guliproduct.dao.SkuSaleAttrValueDao;
import com.yuan.guli.guliproduct.entity.SkuSaleAttrValueEntity;
import com.yuan.guli.guliproduct.service.SkuSaleAttrValueService;


@Service("skuSaleAttrValueService")
public class SkuSaleAttrValueServiceImpl extends ServiceImpl<SkuSaleAttrValueDao, SkuSaleAttrValueEntity> implements SkuSaleAttrValueService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuSaleAttrValueEntity> page = this.page(
                new Query<SkuSaleAttrValueEntity>().getPage(params),
                new QueryWrapper<SkuSaleAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    @Autowired
    SkuSaleAttrValueDao sdao;
    @Override  //根据skuinfo的spuid，联合value表进行查询
    public List<SkuItemVo.SkuItemSaleAttrVo> getSaleAttrsBySpuid(Long spuId) {
        List<SkuItemVo.SkuItemSaleAttrVo>s =  sdao.getSaleAttrsBySpuid(spuId);
        return  s;
    }

    @Override
    public List<String> getSkuAttrValue(Long skuId) {

       List<String> attr =  sdao.getSkuAttrValue(skuId);
       return  attr;
    }


}