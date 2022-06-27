package com.yuan.guli.guliproduct.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.yuan.guli.guliproduct.dao.AttrAttrgroupRelationDao;
import com.yuan.guli.guliproduct.entity.AttrEntity;
import com.yuan.guli.guliproduct.service.AttrAttrgroupRelationService;
import com.yuan.guli.guliproduct.service.AttrService;
import com.yuan.guli.guliproduct.vo.AttrGroupRelationVo;
import com.yuan.guli.guliproduct.vo.AttrGroupWithAttrsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yuan.guli.guliproduct.entity.AttrGroupEntity;
import com.yuan.guli.guliproduct.service.AttrGroupService;
import com.yuan.common.utils.PageUtils;
import com.yuan.common.utils.R;



/**
 * 属性分组
 *
 * @author lzy
 * @email 1716827691@qq.com
 * @date 2022-03-07 21:33:41
 */
@RestController
@RequestMapping("guliproduct/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;


    /**
     * 使用catid查询分组
     */
    @RequestMapping("/list/{catelogId}") //路径变量需要  @PathVariable
    //   @RequiresPermissions("guliproduct:attrgroup:list")
    public R list(@RequestParam Map<String, Object> params,
                  @PathVariable("catelogId") Long catelogId){
        PageUtils page = attrGroupService.queryPageByCid(params,catelogId);
        return R.ok().put("page", page);
    }




    /**
     * 列表
     */
    @RequestMapping("/list")
 //   @RequiresPermissions("guliproduct:attrgroup:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = attrGroupService.queryPage(params);
        return R.ok().put("page", page);
    }


    /**
     * 获取分组属性信息
     */
    @RequestMapping("/info/{attrGroupId}")
    //@RequiresPermissions("guliproduct:attrgroup:info")
    public R info(@PathVariable("attrGroupId") Long attrGroupId){
        AttrGroupEntity attrGroup = attrGroupService.queryByPath(attrGroupId);
        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
   // @RequiresPermissions("guliproduct:attrgroup:save")
    public R save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);

        return R.ok();
    }



    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("guliproduct:attrgroup:update")
    public R update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    @Autowired
    AttrService attrService;
    /**
     * 获取所有分组和属性的关联 功能
     * */
    @RequestMapping("/{attrgroupId}/attr/relation")
    public R attrgroupRelation(@PathVariable("attrgroupId")Long attrGroupId){
        List<AttrEntity> attrEntities = attrService.getattrgroupRelation(attrGroupId);
        return R.ok().put("data", attrEntities);
    }

    /**
     删除当前分组的某个属性参数，让他们失去关联
     * **/
    @Autowired
    AttrAttrgroupRelationService deleteRelation;
    @RequestMapping("/attr/relation/delete")
    // @RequiresPermissions("guliproduct:attr:delete")
    public R deleteRelation(@RequestBody  AttrGroupRelationVo [] relationVos){
        deleteRelation.deleteRelation(relationVos);
        return R.ok();
    }


    /**
     * 查询没有被关联的属性数据，然后进行返回显示
     * */
    @RequestMapping("/{attrGroupId}/noattr/relation") //路径变量需要  @PathVariable
    //   @RequiresPermissions("guliproduct:attrgroup:list")
    public R queryNorelation(@RequestParam Map<String, Object> params,
                  @PathVariable("attrGroupId") Long attrGroupId){
        PageUtils page = attrService.getNoRelation(params, attrGroupId);
        return R.ok().put("page", page);
    }


    /**
     * 新增关联功能：
     * */
    @Autowired
    AttrAttrgroupRelationService attrAttrgroupRelationService;
    @RequestMapping("/attr/relation")
    public R saveRelation(@RequestBody List  <AttrGroupRelationVo> relationVo){
    attrAttrgroupRelationService.saveRelation(relationVo);
        return R.ok();
    }



    /**
     * 商品上传：
     *  获取分类下所有分组和关联数据
     * **/
    @RequestMapping("/{catelogId}/withattr")
    public R getGroupAndAttr(@PathVariable("catelogId")Long catelogId){
        //1. 查出所有属性分组
        //2.查询每个分组的所有属性
        List<AttrGroupWithAttrsVo> attrs = attrGroupService.getAttrGroupWithAttrsByCid(catelogId);

        return R.ok().put("data",attrs);
    }



    /**
     * 删除
     */
    @RequestMapping("/delete")
   // @RequiresPermissions("guliproduct:attrgroup:delete")
    public R delete(@RequestBody Long[] attrGroupIds){
		attrGroupService.removeByIds(Arrays.asList(attrGroupIds));
        return R.ok();
    }




}
