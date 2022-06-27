package com.yuan.guli.guliware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.yuan.common.utils.R;
import com.yuan.guli.guliware.feign.MemberFeignService;
import com.yuan.guli.guliware.vo.FareVo;
import com.yuan.guli.guliware.vo.MemberAddressVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuan.common.utils.PageUtils;
import com.yuan.common.utils.Query;

import com.yuan.guli.guliware.dao.WareInfoDao;
import com.yuan.guli.guliware.entity.WareInfoEntity;
import com.yuan.guli.guliware.service.WareInfoService;


@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<WareInfoEntity> page = this.page(
                new Query<WareInfoEntity>().getPage(params),
                new QueryWrapper<WareInfoEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 仓库模糊检索
     * */
    @Override
    public PageUtils queryInfoPages(Map<String, Object> params) {
    String key = (String) params.get("key");
        QueryWrapper<WareInfoEntity> wrapper= new QueryWrapper();
                if(!StringUtils.isEmpty(key)){
                   wrapper.eq("id",key).or().like("name",key)
                   .or().like("areacode",key);
                }
        IPage<WareInfoEntity> page= this.page(
                new Query<WareInfoEntity>().getPage(params),
                wrapper);
        return new PageUtils(page);
    }

    @Autowired
    MemberFeignService memberFeignService;
    @Override
    public FareVo getFare(Long addId) {
        FareVo fareVo = new FareVo();
        //运费信息由仓库-->订单地址信息获取
        R info = memberFeignService.info(addId);
        MemberAddressVo data = info.getData(new TypeReference<MemberAddressVo>() {
        });
        fareVo.setAddress(data);
        fareVo.setFarePrice(new BigDecimal("10"));
        return fareVo; //实际开发需要结合快递物流方面的接口进行调用返回，这里之间固定吧
    }

}