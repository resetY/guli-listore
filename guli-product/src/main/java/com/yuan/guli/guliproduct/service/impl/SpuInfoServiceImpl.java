package com.yuan.guli.guliproduct.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.alibaba.nacos.client.utils.JSONUtils;
import com.fasterxml.jackson.databind.util.BeanUtil;
import com.yuan.common.constant.ProductConstant;
import com.yuan.common.exception.CodeEnume;
import com.yuan.common.to.SkuHasStockTo;
import com.yuan.common.to.SkuReductionTo;
import com.yuan.common.to.SpuBoundsTo;
import com.yuan.common.to.es.SkuEsModel;
import com.yuan.common.utils.R;
import com.yuan.guli.guliproduct.dao.BrandDao;
import com.yuan.guli.guliproduct.dao.CategoryDao;
import com.yuan.guli.guliproduct.dao.SkuImagesDao;
import com.yuan.guli.guliproduct.entity.*;
import com.yuan.guli.guliproduct.feign.CouponFeignService;
import com.yuan.guli.guliproduct.feign.SearchFeignService;
import com.yuan.guli.guliproduct.feign.WareFeignService;
import com.yuan.guli.guliproduct.service.*;
import com.yuan.guli.guliproduct.vo.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuan.common.utils.PageUtils;
import com.yuan.common.utils.Query;

import com.yuan.guli.guliproduct.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 商品上架的数据保存
     * */
    @Autowired
    SpuInfoDescService descService;
    @Autowired
    SpuImagesService spuImagesService;

    @Autowired
    AttrService attrService;

    @Autowired
    ProductAttrValueService attrValueService;

    @Autowired
    SkuInfoService skuInfoService;

    @Autowired
  SkuImagesService skuImagesService;

    @Autowired
    SkuSaleAttrValueService SaleAttrValueService;

    @Autowired
    CouponFeignService couponFeignService;
    @Transactional
    @Override
    public void saveSpuInfo(SpuSaveVo vo) {
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        //1.保存spu基本信息：spu_info
        BeanUtils.copyProperties(vo,spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        this.saveBaseSpuInfo(spuInfoEntity); //封装

        //2.保存spu的描述图片:spu_info_desc
        SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
        spuInfoDescEntity.setSpuId(spuInfoEntity.getId());
        List<String> decript = vo.getDecript();
        spuInfoDescEntity.setDecript(String.join(",",decript)); //将所有数据用逗号隔开，返回字符串
      descService.saveSpuInfoDesc(spuInfoDescEntity);

        //3.保存spu的展示图片集:spu_images
        SpuImagesEntity SpuImages = new SpuImagesEntity();
        List<String> images = vo.getImages();
        SpuImages.setSpuId(spuInfoEntity.getId());
        BeanUtils.copyProperties(vo,SpuImages);

        //spuid：用于判断是哪个商品，imagews：图片路径

        spuImagesService.saveImages(spuInfoEntity.getId(),images); //保存图片路径和spuid

        //4.保存spu规格参数：pms_product_attr_value 表
        List<BaseAttrs> baseAttrs = vo.getBaseAttrs();
        List<ProductAttrValueEntity> attrValueEntityList = baseAttrs.stream().map((base) -> {
            ProductAttrValueEntity attrValueEntity = new ProductAttrValueEntity();
            attrValueEntity.setAttrId(base.getAttrId());
            AttrEntity attr = attrService.getById(base.getAttrId()); //找attr，保存attrName
            attrValueEntity.setAttrValue(base.getAttrValues());
            attrValueEntity.setAttrName(attr.getAttrName());
            attrValueEntity.setQuickShow(base.getShowDesc());
            attrValueEntity.setSpuId(spuInfoEntity.getId());
            return attrValueEntity;
        }).collect(Collectors.toList());

        attrValueService.saveAttrValue(attrValueEntityList);//批量保存


        //6.保存spu的积分信息：sms_spu_bounds (需要先保存积分信息，才能对后面数据进行保存关联)
        Bounds bounds = vo.getBounds();//成长积分和购物积分信息
        SpuBoundsTo spuBoundsTo = new SpuBoundsTo();
        BeanUtils.copyProperties(bounds,spuBoundsTo);
        spuBoundsTo.setSpuId(spuInfoEntity.getId());
        R r1 = couponFeignService.saveSpuBounds(spuBoundsTo);//调用远程调用
       //修改r，使其可用获得code状态，从这个我们运行可以知道远程服务调用是否成功
        if(r1.getCode() != 0){
            log.error("远程服务：保存积分信息失败！！！");
        }

        //5.保存当前spu对对应sku的信息:
        List<Skus> skus = vo.getSkus();
        if(skus!= null && skus.size()>0){
            skus.forEach(System.out::println); //输出结果无问题
            vo.getSkus().forEach(item->{  //
            System.out.println("数据啊啊啊啊："+item.toString());
            String defaultImg = "";
            System.out.println("默认照片："+defaultImg);
            for (Images image : item.getImages()) {
                if(image.getDefaultImg() == 1){ //如果是默认图片的，则进行存储
                    defaultImg = image.getImgUrl(); //注入默认图片，给下面使用
                }
            }
            //5.1.保存sku基本信息：sku_info
            SkuInfoEntity skuInfo = new SkuInfoEntity();
            BeanUtils.copyProperties(item,skuInfo);
            skuInfo.setBrandId(spuInfoEntity.getBrandId());
            skuInfo.setCatalogId(spuInfoEntity.getCatalogId());
            skuInfo.setSaleCount(0L); //销量
            skuInfo.setSpuId(spuInfoEntity.getId());
            skuInfo.setSkuDefaultImg(defaultImg);

            skuInfoService.saveSkuInfo(skuInfo); //保存sku基本信息-----

            Long skuId = skuInfo.getSkuId();//保存完成后，可用获取到每个skuid
                System.out.println("skuid:"+skuId);

            //5.2.保存sku图片集：sku_images
            //给sku_images,注入他们的sku_id
            //skuImag 用 sku的数据注入改变：
            List<SkuImagesEntity> imagesEntities = item.getImages().stream().map(img -> {
                SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                skuImagesEntity.setSkuId(skuId);
                skuImagesEntity.setImgUrl(img.getImgUrl()); //图片是当前正在遍历的图片
                skuImagesEntity.setDefaultImg(img.getDefaultImg());
                return skuImagesEntity;
            }).filter(entity->{  //过滤器：过滤掉空路径图片,非空就返回
               return StringUtils.isEmpty(entity.getImgUrl());
            }).collect(Collectors.toList());
            //保存：
         skuImagesService.saveBatch(imagesEntities);

            //5.3.sku销售信息：sku_sale_attr_value
            List<Attr> attrs = item.getAttr(); //获得attrs集合
            List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = attrs.stream().map(attr -> {
                SkuSaleAttrValueEntity skuSaleAttr = new SkuSaleAttrValueEntity();
                BeanUtils.copyProperties(attr, skuSaleAttr);
                skuSaleAttr.setSkuId(skuId);
                return skuSaleAttr;
            }).collect(Collectors.toList());
            SaleAttrValueService.saveBatch(skuSaleAttrValueEntities); //保存


            //7.sku的优惠满减等信息：sms_sku_ladder sms_sku_full_reladtion  sms_
            //需要进行远程操作，跨服务操作才能进行保存
            SkuReductionTo skuReductionTo = new SkuReductionTo();
            BeanUtils.copyProperties(item,skuReductionTo);
            skuReductionTo.setSkuId(skuId);
            //满减数量和满减价格如果大于0，才远程调用服务进行添加,BigDecimel的比较需要用方法，返回1则比结果大
            if(skuReductionTo.getFullCount()>0 || skuReductionTo.getFullPrice().compareTo(new BigDecimal("0"))==1){
                R r2 = couponFeignService.saveSkuReduction(skuReductionTo); //调用远程服务
                if(r2.getCode() != 0){
                    log.error("远程服务：保存spu优惠信息失败！！！");
                }
            }

        });
        }

    }

    @Override
    public void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity) {
        //this.baseMapper:直接拿到Dao
                this.save(spuInfoEntity);

    }

    @Override
    public PageUtils querySpuinfoPage(Map<String, Object> params) {
        String key = (String) params.get("key");
        String brandId= (String) params.get("brandId");
        String catelogId = (String) params.get("catelogId");
        String status = (String) params.get("status");
        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper();

        //句子：status=1 and (id=1 or spu_name like xxx)
        //WHERE ((id = ? OR spu_name LIKE ?) AND publish_status = ? AND brand_id = ? AND catalog_id = ?) LIMIT ?
        if(!StringUtils.isEmpty(key)){
                wrapper.and((w)->{
                   w.eq("id",key).or().like("spu_name",key);
                });
        }
        if(!StringUtils.isEmpty(status)){
            wrapper.eq("publish_status",status);

        }
        if(!StringUtils.isEmpty(brandId) && !catelogId.equals("0")){
            wrapper .eq("brand_id",brandId);


        } if(!StringUtils.isEmpty(catelogId) &&  !catelogId.equals("0")){
            wrapper .eq("catalog_id",catelogId);
        }
                IPage<SpuInfoEntity> page= this.page(
                        new Query<SpuInfoEntity>().getPage(params),
                        wrapper
                );
            return  new PageUtils(page);
    }

    @Autowired
    ProductAttrValueService productAttrValueService;
    @Autowired
    BrandDao brandDao;
    @Autowired
   CategoryDao categoryDao;
    @Autowired
    WareFeignService wareFeignService;
    @Autowired
    SearchFeignService searchFeignService;


    /**
     * 商品上架商城业务：
     * **/
    @Transactional
    @Override
    public void upProduct(Long spuId) {

        //2.查询所有sku信息，信息包含品牌名字
       List<SkuInfoEntity> skus =  skuInfoService.getSkusBySpuId(spuId);

        List<Long> skuIds = skus.stream().map(item -> {
            return item.getSkuId();
        }).collect(Collectors.toList());



        //4.TODO:查询当前sku的所有可以被检索的规格属性attr
        List<ProductAttrValueEntity> attrsByskuId = productAttrValueService.getAttrsByskuId(spuId);
        List<Long> attrIds = attrsByskuId.stream().map((attr) -> {
            return attr.getAttrId();
        }).collect(Collectors.toList());

        //查询出可以被检索的id
       List<Long> searchAttrIds =  attrService.selectSearchAttrIds(attrIds);

       //判断是否包含：注意：这里优化为HashSet进行判断，因为可以降低时间复杂度
                //list判断：0n的时间   hashset：01的时间复杂度
        Set<Long> searchSet = new HashSet<>(searchAttrIds);

        List<SkuEsModel.Attrs> attrsList = attrsByskuId.stream().filter(item -> {
            return searchSet.contains(item.getAttrId());
        }).map(attr -> {
            SkuEsModel.Attrs attrs = new SkuEsModel.Attrs();
            BeanUtils.copyProperties(attr, attrs);
            return attrs;
        }).collect(Collectors.toList());


        //TODO：1.通过调用远程功能给库存系统，查询是否有库存 通过skuid查询库存
        /**
         * 设置如果远程调用网络失败，默认为没有库存
         *      如果远程查询出现异常，那么数据map，默认为空
         * **/
        Map<Long, Boolean> stockMap = null;
        try{
            R hasStock = wareFeignService.getHasStock(skuIds);
            //将数据的两个值，分布转化为键值对形式：
    //将泛型传入为我们需要的数据，而这个泛型的构造器受到保护，需要使用内部类方式注入类型： new TypeReference<List<SkuHasStockTo>>(){};
            TypeReference<List<SkuHasStockTo>> typeReference = new TypeReference<List<SkuHasStockTo>>() {
            };
            stockMap = hasStock.getData(typeReference).stream().collect(Collectors.toMap(SkuHasStockTo::getSkuId,
                    item -> item.getStock()));
        }catch (Exception e){
            log.error("库存服务出现异常，查询失败"+e);

        }


        //3.封装信息
        Map<Long, Boolean> finalStockMap = stockMap;
        List<SkuEsModel> upProducts = skus.stream().map((sku) -> {//需要上架的商品

            SkuEsModel esModel = new SkuEsModel();
           BeanUtils.copyProperties(sku,esModel);
           //需要额外处理的属性：skuPrice，skuImg，hasStock，hotScore
            //brandName  brandImg  catalogName
           esModel.setSkuPrice(sku.getPrice());
           esModel.setSkuImg(sku.getSkuDefaultImg());

            CategoryEntity category = categoryDao.selectById(esModel.getCatalogId());
            esModel.setCatalogName(category.getName());


            //根据skuid注入库存数据，是否有库存
            if(finalStockMap == null){
                    esModel.setHasStock(false);
            }else{
                esModel.setHasStock(finalStockMap.get(sku.getSkuId()));
            }



            //TODO:热度评分呢
            esModel.setHotScore(0L);

            //TODO:注入品牌名字和分类名字等信息
            BrandEntity barand = brandDao.selectById(esModel.getBrandId());
            esModel.setBrandName(barand.getName());
            esModel.setBrandImg(barand.getLogo());

            esModel.setAttrs(attrsList);


            return esModel;
        }).collect(Collectors.toList());


        //TODO：5. 将数据发送给es保存 使用检索模块:guli-search
        R r = searchFeignService.UpProduct(upProducts);
       if( r.getCode()== CodeEnume.ES_EXCEPTION.getCode()){ //调用成功
           //远程调用失败
           //TODO:7.重复调用?
           /**
            * Fegin的调用过程：
            *   1. 构造请求数据，将对象转化为json数据传输
            *   2. 发送请求进行执行(执行成功会解码响应数据)
            *       excuteAndDecode(template)
            *   3.执行请求有重试机制，如果重试机制有开启，那么默认执行失败的时候重试五次
            *           while(true){
            *               try{
            *                   执行代码
            *               }catch{
            *                   抛出异常代码，然后重试
            *               }
            *           }
            * **/
       }else {
           this.baseMapper.updateStatus(spuId,ProductConstant.SpuStatusEnum.UP_SPU1.getCode()); //为上架的spu修改状态

       }
    }

    /**
     * 根据skuid查询spu的方法
     * **/
    @Override
    public SpuInfoEntity getSpuInfoByskuId(Long skuId) {
        SkuInfoEntity sku = skuInfoService.getById(skuId);
        SpuInfoEntity spuInfoEntity = baseMapper.selectById(sku.getSpuId());
        return spuInfoEntity;
    }

}