package com.yuan.guli.gulicoupon.service.impl;

import com.sun.org.apache.bcel.internal.generic.NEW;
import com.yuan.common.to.MemberPrice;
import com.yuan.common.to.SkuReductionTo;
import com.yuan.guli.gulicoupon.entity.MemberPriceEntity;
import com.yuan.guli.gulicoupon.entity.SkuLadderEntity;
import com.yuan.guli.gulicoupon.service.MemberPriceService;
import com.yuan.guli.gulicoupon.service.SkuLadderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuan.common.utils.PageUtils;
import com.yuan.common.utils.Query;

import com.yuan.guli.gulicoupon.dao.SkuFullReductionDao;
import com.yuan.guli.gulicoupon.entity.SkuFullReductionEntity;
import com.yuan.guli.gulicoupon.service.SkuFullReductionService;
import org.springframework.transaction.annotation.Transactional;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }

    @Autowired
    SkuLadderService skuLadderService;



    @Autowired
    MemberPriceService memberPriceService;
    /**
     * 被远程调用的接口的业务层实现
     * */
    @Override
    public void saveSkuInfo(SkuReductionTo skuReductionTo) {
            //1. 保存满减打折、会员价格
        //sms_sku_ladder
        SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
        skuLadderEntity.setSkuId(skuReductionTo.getSkuId());
        skuLadderEntity.setFullCount(skuReductionTo.getFullCount()); //满几件
        skuLadderEntity.setDiscount(skuReductionTo.getDiscount());//打几折
        skuLadderEntity.setAddOther(skuReductionTo.getCountStatus()); //优惠
        //改进：对满减大于0的进行默认折扣
       if(skuReductionTo.getFullCount()>0){
           // skuLadderEntity.setPrice(1L); //折后价格：可现在计算也可也等结算
           skuLadderService.save(skuLadderEntity);
        }

        //2. sms_sku_full_reduction
        SkuFullReductionEntity fullReductionEntity = new SkuFullReductionEntity();
       fullReductionEntity.setAddOther(skuReductionTo.getPriceStatus());
       fullReductionEntity.setFullPrice(skuReductionTo.getFullPrice());
       fullReductionEntity.setReducePrice(skuReductionTo.getReducePrice());
       fullReductionEntity.setSkuId(skuReductionTo.getSkuId());
        //价格到位了，再执行满减
        if(fullReductionEntity.getFullPrice().compareTo(new BigDecimal("0"))==1){
          this.save(fullReductionEntity);
        }



        //3.会员价格：
        List<MemberPrice> memberPrice = skuReductionTo.getMemberPrice();
        List<MemberPriceEntity> MP = memberPrice.stream().map(mp -> {
            MemberPriceEntity memberPriceEntity = new MemberPriceEntity();
            memberPriceEntity.setSkuId(skuReductionTo.getSkuId());
                memberPriceEntity.setMemberPrice(mp.getPrice());
                memberPriceEntity.setMemberLevelName(mp.getName());
                memberPriceEntity.setMemberLevelId(mp.getId());
                memberPriceEntity.setAddOther(1);
            return memberPriceEntity;
        }).filter(emtity->{  //会员价格有，蔡进行折扣注入
            return  emtity.getMemberPrice().compareTo(new BigDecimal("0"))==1;
        }).collect(Collectors.toList());
        memberPriceService.saveBatch(MP);

    }

}