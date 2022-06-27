package com.yuan.guli.guliproduct.service.impl;

import com.yuan.guli.guliproduct.dao.BrandDao;
import com.yuan.guli.guliproduct.dao.CategoryDao;
import com.yuan.guli.guliproduct.entity.BrandEntity;
import com.yuan.guli.guliproduct.entity.CategoryEntity;
import com.yuan.guli.guliproduct.vo.BrandVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuan.common.utils.PageUtils;
import com.yuan.common.utils.Query;

import com.yuan.guli.guliproduct.dao.CategoryBrandRelationDao;
import com.yuan.guli.guliproduct.entity.CategoryBrandRelationEntity;
import com.yuan.guli.guliproduct.service.CategoryBrandRelationService;


@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryBrandRelationEntity> page = this.page(
                new Query<CategoryBrandRelationEntity>().getPage(params),
                new QueryWrapper<CategoryBrandRelationEntity>()
        );

        return new PageUtils(page);
    }


    /**
     * @param  brandId 品牌id
     * @return 返回值为分类 id和名字
     *
     * */
    @Override
    public List<CategoryBrandRelationEntity> queryCBByBid(Long brandId) {
        QueryWrapper<CategoryBrandRelationEntity> q = new QueryWrapper<>();
        q.eq("brand_id",brandId); //查询条件设置
        List<CategoryBrandRelationEntity> categoryBrandRelationEntities = baseMapper.selectList(q);
        return categoryBrandRelationEntities;
    }


    @Autowired
    CategoryDao categoryDao;
    @Autowired
    BrandDao brandDao;
    /**
     * 用于保存品牌和分类关联内容，包括前端不传进来的名字
     * */
    @Override
   public void  saveData(CategoryBrandRelationEntity categoryBrandRelationEntity){
        Long brandId = categoryBrandRelationEntity.getBrandId();
        Long catelogId = categoryBrandRelationEntity.getCatelogId();

        CategoryEntity categoryEntity = categoryDao.selectById(catelogId);
        BrandEntity brandEntity = brandDao.selectById(brandId);
       //1.查询详细名字：
        categoryBrandRelationEntity.setBrandName(brandEntity.getName());
        categoryBrandRelationEntity.setCatelogName(categoryEntity.getName());

        boolean save = this.save(categoryBrandRelationEntity);
        if(save ==  false){
            log.error("添加分类和品牌关联失败");
        }
    }

@Autowired
CategoryBrandRelationDao brandRelationDao;
    /**
     * @return 当前分类里面的所有品牌
     * */
    @Override
    public List<BrandVo> getCategoryBrand(Long catId) {

        List<CategoryBrandRelationEntity> cb = brandRelationDao.selectList(new QueryWrapper<CategoryBrandRelationEntity>().eq("catelog_id", catId));

        List<BrandVo> brands = cb.stream().map((c) -> {
            BrandVo brandVo = new BrandVo();
            brandVo.setBrandId(c.getBrandId());
            brandVo.setBrandName(c.getBrandName());
            return brandVo;
        }).collect(Collectors.toList());
        if(brands == null || brands.size()==0){
            return null;
       }
        return brands;
    }

//    /**
//     * @return 当前分类里面的所有品牌 ,这里是查询品牌的，包括未关联的这个分类下的品牌
//     * */
//    @Override
//    public List<BrandVo> getCategoryBrand(Long catId) {
//        if(catId == 0){
//            return  null;
//        }
//        List<BrandEntity> brand = brandDao.selectList(new QueryWrapper<BrandEntity>().eq("catelog_id", catId));
//        List<BrandVo> brands = brand.stream().map((c) -> {
//            BrandVo brandVo = new BrandVo();
//            brandVo.setBrandId(c.getBrandId());
//            brandVo.setBrand_name(c.getName());
//            return brandVo;
//        }).collect(Collectors.toList());
//        if(brands == null || brands.size()==0){
//            return null;
//        }
//        return brands;
//    }

}