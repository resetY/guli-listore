package com.yuan.guli.guliware.service.impl;

import com.yuan.guli.guliware.entity.WareSkuEntity;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuan.common.utils.PageUtils;
import com.yuan.common.utils.Query;

import com.yuan.guli.guliware.dao.PurchaseDetailDao;
import com.yuan.guli.guliware.entity.PurchaseDetailEntity;
import com.yuan.guli.guliware.service.PurchaseDetailService;


@Service("purchaseDetailService")
public class PurchaseDetailServiceImpl extends ServiceImpl<PurchaseDetailDao, PurchaseDetailEntity> implements PurchaseDetailService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseDetailEntity> page = this.page(
                new Query<PurchaseDetailEntity>().getPage(params),
                new QueryWrapper<PurchaseDetailEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 采购的检索
     * */
    @Override
    public PageUtils queryPurchasedetail(Map<String, Object> params) {
        String  key = (String) params.get("key");
        String  status = (String) params.get("status");
        String  wareId = (String) params.get("wareId");
        QueryWrapper<PurchaseDetailEntity> wrapper =new QueryWrapper();
        if( !StringUtils.isEmpty(key)){
            wrapper.and(w->{ //进行整体拼接
                w.eq("purchase_id",key).or().eq("sku_id",key);
            });
        }
     if( !StringUtils.isEmpty(status)){
            wrapper.eq("status",status);
        } if( !StringUtils.isEmpty(status)){
            wrapper.eq("ware_id",wareId);
        }


        IPage<PurchaseDetailEntity> page= this.page(
                new Query<PurchaseDetailEntity>().getPage(params),
                wrapper);
        return new PageUtils(page);
    }

}