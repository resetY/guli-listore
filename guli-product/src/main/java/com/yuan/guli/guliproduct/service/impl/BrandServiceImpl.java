package com.yuan.guli.guliproduct.service.impl;

import com.yuan.guli.guliproduct.dao.CategoryBrandRelationDao;
import com.yuan.guli.guliproduct.entity.CategoryBrandRelationEntity;
import com.yuan.guli.guliproduct.vo.BrandVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.BatchUpdateException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuan.common.utils.PageUtils;
import com.yuan.common.utils.Query;

import com.yuan.guli.guliproduct.dao.BrandDao;
import com.yuan.guli.guliproduct.entity.BrandEntity;
import com.yuan.guli.guliproduct.service.BrandService;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        //1. 获取key
        String key = (String) params.get("key");

        QueryWrapper<BrandEntity> queryWrapper = new QueryWrapper<>();

        if(!StringUtils.isEmpty(key)){ //如果不为空
            queryWrapper.eq("brand_id",key).or().like("name",key);
        }
        IPage<BrandEntity> page = this.page(
                new Query<BrandEntity>().getPage(params),
               queryWrapper
        );
        return new PageUtils(page);
    }


    /**
     * 修改品牌的时候，关联表内的数据也应该进行修改
     * **/
    @Autowired
    CategoryBrandRelationDao brandRelationDao;
    @Autowired
    BrandDao brandDao;
    @Override
    public void updateBrandCategory(BrandEntity brandEntity) {

        //根据传过来的数据，修改brand
        int count1 = brandDao.updateById(brandEntity);
        if(count1 == 0){
            log.error("数据更新出现异常");
        }

        //1.根据条件查询到关联表数据，然后修改：
        QueryWrapper<CategoryBrandRelationEntity> queryWrapper = new QueryWrapper();
        queryWrapper.eq("brand_id",brandEntity.getBrandId());
        CategoryBrandRelationEntity categoryBrandRelationEntity = brandRelationDao.selectOne(queryWrapper);

        //2. 注入修改更新的关联表内容：
        categoryBrandRelationEntity.setBrandId(brandEntity.getBrandId());
        categoryBrandRelationEntity.setBrandName(brandEntity.getName());

        //3.进行修改：
        int count2 = brandRelationDao.updateById(categoryBrandRelationEntity);
        if(count2 == 0){
            log.error("数据更新出现异常");
        }
    }

    @Override
    public List<BrandEntity> getBrandByBrands(List<Long> brandId) {
       QueryWrapper<BrandEntity> queryWrapper  =new QueryWrapper();
       queryWrapper.in("brand_id",brandId);
        List<BrandEntity> brandEntities = baseMapper.selectList(queryWrapper);
        return  brandEntities;
    }


}