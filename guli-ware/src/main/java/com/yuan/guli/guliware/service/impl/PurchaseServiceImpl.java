package com.yuan.guli.guliware.service.impl;

import com.yuan.common.constant.WareConstant;
import com.yuan.guli.guliware.entity.PurchaseDetailEntity;
import com.yuan.guli.guliware.service.PurchaseDetailService;
import com.yuan.guli.guliware.service.WareSkuService;
import com.yuan.guli.guliware.vo.DoneVo;
import com.yuan.guli.guliware.vo.ItemInfo;
import com.yuan.guli.guliware.vo.MergeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuan.common.utils.PageUtils;
import com.yuan.common.utils.Query;

import com.yuan.guli.guliware.dao.PurchaseDao;
import com.yuan.guli.guliware.entity.PurchaseEntity;
import com.yuan.guli.guliware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 查未领取的采购单
     * */
    @Override
    public PageUtils queryUnreceive(Map<String, Object> params) {
        QueryWrapper<PurchaseEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("status",0).or().eq("status",1); //未被查询状态
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
         wrapper
        );
        return new PageUtils(page);
    }

    @Autowired
    PurchaseDetailService pdsDao;
    @Override
    @Transactional
    public void saveMergo(MergeVo mergeVo) {
        Long purchaseId = mergeVo.getPurchaseId();

        if(purchaseId== null){  //新建的采购单:没选择采购单的情况
            PurchaseEntity newpurch = new PurchaseEntity();
            newpurch.setStatus(WareConstant.PurchaseStatus.CREATES.getCode());
            newpurch.setCreateTime(new Date());
            newpurch.setUpdateTime(new Date());
            baseMapper.insert(newpurch);
            purchaseId =newpurch.getId();  //刷新采购单的id，赋值，然后给下面
        }
        //合并采购：
            List<Long> item = mergeVo.getItems();
           Long finalpurchaseId = purchaseId;   //拿到采购单id，然后修改采购信息
        List<PurchaseDetailEntity> collect = item.stream().map(id -> {
            PurchaseDetailEntity pdEntiry = new PurchaseDetailEntity();
            if(pdEntiry.getStatus() == WareConstant.PurchaseDetailStatus.BUYING.getCode()){
                return  null;
            }
            pdEntiry.setId(id);
            pdEntiry.setPurchaseId(finalpurchaseId);
            pdEntiry.setStatus(WareConstant.PurchaseDetailStatus.ASSIGN0ED.getCode()); //采购需求状态码
            return pdEntiry;
        }).collect(Collectors.toList());
        pdsDao.updateBatchById(collect);

        //采购单变化，时间变化
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(finalpurchaseId);
        purchaseEntity.setUpdateTime(new Date());
        this.updateById(purchaseEntity);


    }

    /**
     * 领取采购单
     * */
    @Override
    @Transactional
    public void getReceived(List<Long> ids) {
        //1.采购人员领取的采购单，是新建状态的
        List<PurchaseEntity> purchase = ids.stream().map(id -> {
            PurchaseEntity purchaseEntity = baseMapper.selectById(id);
            return purchaseEntity;
        }).filter(item -> {
            if (item.getStatus() == WareConstant.PurchaseStatus.CREATES.getCode()
                    || item.getStatus() == WareConstant.PurchaseStatus.ASSIGN0ED.getCode()
            ) {
                return true; //满足过滤条件的放行
            }
            return false; //不要的
        }).map(item->{
            item.setUpdateTime(new Date());
            item.setStatus( WareConstant.PurchaseStatus.RECEIVE.getCode()); //修改状态
        return item;
        }).collect(Collectors.toList());

        //2.改变采购单状态
        this.updateBatchById(purchase);

          //3.改变采购项状态
        List<PurchaseDetailEntity> purchaseDetailEntities = purchase.stream().map(item -> {
            PurchaseDetailEntity purchaseDetailEntity = pdsDao.getOne(new QueryWrapper<PurchaseDetailEntity>().eq("purchase_id", item.getId()));
            purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatus.BUYING.getCode());
            return purchaseDetailEntity;
        }).collect(Collectors.toList());
       //也可这样
//       QueryWrapper<PurchaseDetailEntity> purchaseDetailEntityQueryWrapper = new QueryWrapper<PurchaseDetailEntity>().setEntity().in("purchase_id", ids).eq("status", 0);
//       pdsDao.update(purchaseDetailEntityQueryWrapper);
        pdsDao.updateBatchById(purchaseDetailEntities);
    }


    @Autowired
    WareSkuService skuService;
    @Transactional
    @Override
    public void Done(DoneVo doneVo) {

        //2.改变采购项状态
        boolean flag = true; //采购状态
        List<ItemInfo> items = doneVo.getItems();
        List<PurchaseDetailEntity> update = new ArrayList<>();
        for (ItemInfo item : items) {
            PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
            //分离出成功完成的采购项 和 不成功的
            if(item.getStatus() == WareConstant.PurchaseDetailStatus.HASEHERROR.getCode()){ //采购失败
                System.out.println("采购失败");
                purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatus.HASEHERROR.getCode());
                flag = false;
            }else{
                purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatus.FINISH.getCode());

                //3.将成功采购的进行入库
                PurchaseDetailEntity detailEntity = pdsDao.getById(item.getItemId());
                Long skuId = detailEntity.getSkuId();
                Long wareId = detailEntity.getWareId();
                Integer skuNum = detailEntity.getSkuNum();
                skuService.addStock(skuId,wareId,skuNum); //添加库存
            }
            //注入采购项目id
            purchaseDetailEntity.setId(item.getItemId());
            update.add(purchaseDetailEntity);
        }
        pdsDao.updateBatchById(update);

        //1.改变采购单状态:只有采购项全部成功，才能进行采购单的状态修改
        if(flag == true){
            PurchaseEntity purchaseEntity = this.getById(doneVo.getId());
            purchaseEntity.setStatus(WareConstant.PurchaseStatus.FINISH.getCode());
            purchaseEntity.setUpdateTime(new Date());
            this.updateById(purchaseEntity);
        }



    }

}