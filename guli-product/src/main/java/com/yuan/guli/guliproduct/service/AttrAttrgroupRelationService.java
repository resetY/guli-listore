package com.yuan.guli.guliproduct.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuan.common.utils.PageUtils;
import com.yuan.guli.guliproduct.entity.AttrAttrgroupRelationEntity;
import com.yuan.guli.guliproduct.vo.AttrGroupRelationVo;

import java.util.List;
import java.util.Map;

/**
 * 属性&属性分组关联
 *
 * @author lzy
 * @email 1716827691@qq.com
 * @date 2022-03-07 21:33:41
 */
public interface AttrAttrgroupRelationService extends IService<AttrAttrgroupRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);



    /**
     * 根据传输回来的arrid 和 arrgid  进行删除关联
     * */
    void deleteRelation(AttrGroupRelationVo [] relationVo);

    /**
     * 新增分组和数学关联：传过来数据：attrid 和 attrgroupid
     * */
    void saveRelation(List<AttrGroupRelationVo> relationVo);
}

