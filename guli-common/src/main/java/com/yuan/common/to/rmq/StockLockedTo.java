package com.yuan.common.to.rmq;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.List;

@Data
public class StockLockedTo {

    private Long id; //库存工作单id
    private StockDetailTo stockDetailTo; //工作单详情,用于发送给消息队列

@Data
  public static class StockDetailTo{

        /**
         * id
         */
        private Long id;
        /**
         * sku_id
         */
        private Long skuId;
        /**
         * sku_name
         */
        private String skuName;
        /**
         * 购买个数
         */
        private Integer skuNum;
        /**
         * 工作单id
         */
        private Long taskId;
        /**
         * 仓库id
         */
        private Long wareId;
        /**
         * 1-已锁定  2-已解锁  3-扣减
         */
        private Integer lockStatus;

    }
}
