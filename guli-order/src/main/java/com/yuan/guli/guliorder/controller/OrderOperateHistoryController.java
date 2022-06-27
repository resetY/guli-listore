package com.yuan.guli.guliorder.controller;

import java.util.Arrays;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yuan.guli.guliorder.entity.OrderOperateHistoryEntity;
import com.yuan.guli.guliorder.service.OrderOperateHistoryService;
import com.yuan.common.utils.PageUtils;
import com.yuan.common.utils.R;



/**
 * 订单操作历史记录
 *
 * @author lzy
 * @email 1716827691@qq.com
 * @date 2022-03-08 10:53:42
 */
@RestController
@RequestMapping("guliorder/orderoperatehistory")
public class OrderOperateHistoryController {
    @Autowired
    private OrderOperateHistoryService orderOperateHistoryService;

    /**
     * 列表
     */
    @RequestMapping("/list")
 //   @RequiresPermissions("guliorder:orderoperatehistory:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = orderOperateHistoryService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("guliorder:orderoperatehistory:info")
    public R info(@PathVariable("id") Long id){
		OrderOperateHistoryEntity orderOperateHistory = orderOperateHistoryService.getById(id);

        return R.ok().put("orderOperateHistory", orderOperateHistory);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
   // @RequiresPermissions("guliorder:orderoperatehistory:save")
    public R save(@RequestBody OrderOperateHistoryEntity orderOperateHistory){
		orderOperateHistoryService.save(orderOperateHistory);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("guliorder:orderoperatehistory:update")
    public R update(@RequestBody OrderOperateHistoryEntity orderOperateHistory){
		orderOperateHistoryService.updateById(orderOperateHistory);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
   // @RequiresPermissions("guliorder:orderoperatehistory:delete")
    public R delete(@RequestBody Long[] ids){
		orderOperateHistoryService.removeByIds(Arrays.asList(ids));
        return R.ok();
    }

}
