package com.yuan.guli.guliware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuan.common.to.OrderTo;
import com.yuan.common.to.rmq.StockLockedTo;
import com.yuan.common.utils.PageUtils;
import com.yuan.guli.guliware.entity.WareSkuEntity;
import com.yuan.guli.guliware.to.WareSkuLockTo;
import com.yuan.guli.guliware.vo.SkuHasStockVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author lzy
 * @email 1716827691@qq.com
 * @date 2022-03-08 10:57:56
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils querySkuWarePage(Map<String, Object> params);

    //注意：多个参数一定要生成param
    void addStock(@Param("skuId") Long skuId, @Param("wareId") Long wareId, @Param("stock") Integer stock);

    List<SkuHasStockVo> getHasStock(List<Long> skuIds);

    /**
     * 锁库存，返回值为锁定成功true或者失败false
     * **/
  Boolean stockLock(WareSkuLockTo to);

  /**
   * 封装的解锁库存的方法
   * **/
    void unlockStock(StockLockedTo stockLockedTo);
   void unlockStock(OrderTo order);
}


