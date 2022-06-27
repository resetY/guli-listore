package com.yuan.guli.guliproduct.service.impl;

import com.yuan.guli.guliproduct.config.MyThreadConfig;
import com.yuan.guli.guliproduct.config.ThreadPoolConfig;
import com.yuan.guli.guliproduct.dao.SpuInfoDao;
import com.yuan.guli.guliproduct.dao.SpuInfoDescDao;
import com.yuan.guli.guliproduct.entity.SkuImagesEntity;
import com.yuan.guli.guliproduct.entity.SpuInfoDescEntity;
import com.yuan.guli.guliproduct.entity.SpuInfoEntity;
import com.yuan.guli.guliproduct.service.*;
import com.yuan.guli.guliproduct.vo.SkuItemVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuan.common.utils.PageUtils;
import com.yuan.common.utils.Query;

import com.yuan.guli.guliproduct.dao.SkuInfoDao;
import com.yuan.guli.guliproduct.entity.SkuInfoEntity;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 保存sku基本信息
     * **/
    @Override
    public void saveSkuInfo(SkuInfoEntity skuInfo) {
        this.save(skuInfo);
    }

    @Override
    public PageUtils querySkuInfo(Map<String, Object> params) {
        String key = (String) params.get("key");
        String brandId= (String) params.get("brandId");
        String catelogId = (String) params.get("catelogId");
        String min= (String) params.get("min");
        String max = (String) params.get("max");

        QueryWrapper<SkuInfoEntity> wrapper = new QueryWrapper();
        //句子：status=1 and (id=1 or spu_name like xxx)
        //WHERE ((id = ? OR spu_name LIKE ?) AND publish_status = ? AND brand_id = ? AND catalog_id = ?) LIMIT ?
        if(!StringUtils.isEmpty(key)){
            wrapper.and((w)->{
                w.eq("sku_id",key).or().like("sku_name",key);
            });
        }
        if(!StringUtils.isEmpty(brandId)){
            wrapper .eq("brand_id",brandId);


        } if(!StringUtils.isEmpty(catelogId)){
            wrapper .eq("catalog_id",catelogId);
        }
        if(!StringUtils.isEmpty(min)){
           // wrapper .between("price",min,max);
            if(!"0".equals(min)){
                wrapper.ge("price",min);
            }

        }  if(!StringUtils.isEmpty(max)){
            if(!"0".equals(max)){
                wrapper.ge("price",max);
            }
        }
        IPage<SkuInfoEntity> page= this.page(
                new Query<SkuInfoEntity>().getPage(params),
                wrapper
        );
        return  new PageUtils(page);
    }

    /**
     * 按照spuid查询sku数据信息
     * */
    @Override
    public List<SkuInfoEntity> getSkusBySpuId(Long spuId) {
        QueryWrapper<SkuInfoEntity> wrapper = new QueryWrapper();
        wrapper.eq("spu_id",spuId);
        List<SkuInfoEntity> skuInfoEntities = baseMapper.selectList(wrapper);
        return  skuInfoEntities;
    }

    @Autowired
    SkuImagesService skuImagesService;
    @Autowired
    SpuInfoDescDao spuInfoDescDao;
    @Autowired
    AttrService attrService;

    @Autowired
    AttrGroupService attrGroupService;

    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    SpuInfoDao spuInfoDao;
    @Autowired
    ThreadPoolExecutor poolExecutor;
    @Override
    public SkuItemVo item(Long skuId) {
        SkuItemVo skuItemVo = new SkuItemVo();

        CompletableFuture<SkuInfoEntity> info = CompletableFuture.supplyAsync(() -> {
            //1.获取sku基本信息 pms_sku_info
            SkuInfoEntity skuInfoEntity = baseMapper.selectOne(new QueryWrapper<SkuInfoEntity>().eq("sku_id", skuId));
            skuItemVo.setInfo(skuInfoEntity);
            return skuInfoEntity;
        }, poolExecutor);


        //无关系的单独开启异步任务进行处理：
        CompletableFuture<Void> image_f = CompletableFuture.runAsync(() -> {
            //2.sku图片信息：`pms_sku_images`
            List<SkuImagesEntity> images = skuImagesService.getImages(skuId);
            skuItemVo.setImages(images);
        }, poolExecutor);


        CompletableFuture<Void> skuitem_f = info.thenAcceptAsync(res -> {

            //3.spu的所有sku组合信息
            List<SkuItemVo.SkuItemSaleAttrVo> saleAttrs = skuSaleAttrValueService.getSaleAttrsBySpuid(res.getSpuId());
            skuItemVo.setSaleAttr(saleAttrs);
        }, poolExecutor);
        // 注意:不能在一个异步任务后面接着then，这样的方式会让异步任务2执行完才执行任务3，属于串行化


        CompletableFuture<Void> desp_f = info.thenAcceptAsync(res -> {
            //4.商品介绍：spu介绍
            SpuInfoDescEntity desp = spuInfoDescDao.selectOne(new QueryWrapper<SpuInfoDescEntity>().
                    eq("spu_id", res.getSpuId()));
            skuItemVo.setDesp(desp);
        }, poolExecutor);


        CompletableFuture<Void> spuitem_f = info.thenAcceptAsync(res -> {
            //5.spu规格属性和包装
            List<SkuItemVo.SpuItemAttrGroupAttrVo> groups = attrGroupService.getAttrGroupWithAttrsBySpuid(res.getCatalogId(), res.getSpuId()); //当前商品的属性值
            skuItemVo.setGroups(groups);
        }, poolExecutor);


        CompletableFuture<Void> stock_f = info.thenAcceptAsync(res -> {
            //6.判断有无货物
            SpuInfoEntity spuInfoEntity = spuInfoDao.selectOne(new QueryWrapper<SpuInfoEntity>().eq("id", res.getSpuId()));
            if (spuInfoEntity.getPublishStatus().equals(1)) {
                skuItemVo.setHasStock(true);
            } else {
                skuItemVo.setHasStock(false);
            }
        }, poolExecutor);

        /**
         * 多任务组合：开发常见
         *      allOf:等待所有任务完成
         *      anyOf：只需要一个任务完成
         * */

        //所有任务完成等待:info可以不写（不写入info可以提高一点效率），info是关联的，其他完毕info自然完成，可以不等待
        try {
            CompletableFuture.allOf(image_f,skuitem_f,desp_f,spuitem_f,stock_f).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return skuItemVo;
    }

}