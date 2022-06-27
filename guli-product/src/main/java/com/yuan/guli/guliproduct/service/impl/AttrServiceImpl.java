package com.yuan.guli.guliproduct.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.yuan.common.constant.ProductConstant;
import com.yuan.common.utils.Constant;
import com.yuan.guli.guliproduct.dao.AttrAttrgroupRelationDao;
import com.yuan.guli.guliproduct.dao.AttrGroupDao;
import com.yuan.guli.guliproduct.dao.CategoryDao;
import com.yuan.guli.guliproduct.entity.*;
import com.yuan.guli.guliproduct.vo.AttrGroupRelationVo;
import com.yuan.guli.guliproduct.vo.AttrRespVo;
import com.yuan.guli.guliproduct.vo.AttrVo;
import com.yuan.guli.guliproduct.vo.SkuItemVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuan.common.utils.PageUtils;
import com.yuan.common.utils.Query;

import com.yuan.guli.guliproduct.dao.AttrDao;
import com.yuan.guli.guliproduct.service.AttrService;
import org.springframework.transaction.annotation.Transactional;
import sun.plugin.dom.core.Attr;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }


    @Autowired
    AttrGroupDao attrGroupDao;
    @Autowired
    AttrAttrgroupRelationDao attrAttrgroupRelationDao;
    /**
     *   修改pms_attr的同时
     *   关联pms_attrgroup
     *   修改关联表 `pms_attr_attrgroup_relation`
     *
     * **/
    @Transactional
    @Override
    public void saveAttr(AttrEntity attrEntity) {
        //1.设置条件查询该分组是否存在
        QueryWrapper <AttrGroupEntity> queryWrapper = new QueryWrapper();
        queryWrapper.eq("catelog_id",attrEntity.getCatelogId());
        AttrGroupEntity attrGroupEntity = attrGroupDao.selectOne(queryWrapper);
        if(attrGroupEntity == null){
            log.error("该属性分组不存在");
        }
        //2.存储attr参数
        boolean save = this.save(attrEntity);
        if(save!=true){
            log.error("未知异常，数据存储失败");
        }

        //3.存储关联表:满足条件才保存关联关系
if(attrEntity.getAttrType()==ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()&&attrEntity.getAttrId()!=null){
    AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
    attrAttrgroupRelationEntity.setAttrId(attrEntity.getAttrId());
    attrAttrgroupRelationEntity.setAttrGroupId(attrGroupEntity.getAttrGroupId());
    attrAttrgroupRelationEntity.setAttrSort(attrGroupEntity.getSort());
    attrAttrgroupRelationDao.insert(attrAttrgroupRelationEntity);
}


    }



    @Override
    @Transactional
    public void saveAttrVo(AttrVo attrVo) {

        //1.给attr保存基本信息
        AttrEntity attrEntity = new AttrEntity();
    //    attrEntity.setSearchType(attrVo.getSearchType());这样麻烦

        //使用spring提供工具类:将attrvo 复制到 attr里面(前提：属性名一致)
        BeanUtils.copyProperties(attrVo,attrEntity);
        this.save(attrEntity);


        //只有有基本属性才能进行关联：
        if(attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() || attrEntity.getAttrType() ==ProductConstant.AttrEnum.ATTP_TYPE_SB.getCode() ){
            //2.保存关联表数据：
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            attrAttrgroupRelationEntity.setAttrGroupId(attrVo.getAttrGroupId());
            attrAttrgroupRelationEntity.setAttrId(attrEntity.getAttrId());
            attrAttrgroupRelationDao.insert(attrAttrgroupRelationEntity);
        }

    }


    /**
     * 根据条件进行分页查询：
     * **/

    @Autowired
    CategoryDao categoryDao;
    @Override
    public PageUtils queryBaseAttr(Map<String, Object> params, Long catelogId) {
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("attr_type",1); //限制为基本属性
        //1.先查找分类id相同的参数
        if(catelogId != 0){
            queryWrapper.eq("catelog_id",catelogId);
        }
        //2.然后再按照key进行检索
        String key = (String) params.get("key");

        if(!StringUtils.isEmpty(key)){ //如果不为空
            queryWrapper.and((wapper)->{
                wapper.eq("attr_id",key).or().like("attr_name",key);
            });
        }

        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                queryWrapper
        );

        PageUtils pageUtils = new PageUtils(page);
        List<AttrEntity> records = page.getRecords(); //1.获取到AttrEntyty数据
        List<AttrRespVo> respVos = records.stream().map((attrEntity) -> {
            //2.将attrEntity 存入到 vo数据内补全
            AttrRespVo attrRespVo = new AttrRespVo();
            BeanUtils.copyProperties(attrEntity, attrRespVo);


            //3.设置分类分组名字

            //3.1通过 当前的属性attr_id,查询到关联表信息
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = attrAttrgroupRelationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id",attrEntity.getAttrId()));

            //3.2 通过关联表的分组id，查询到分组,然后注入
            if(attrAttrgroupRelationEntity != null){
                Long attrGroupId = attrAttrgroupRelationEntity.getAttrGroupId();
                AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrGroupId);
                if (attrGroupEntity != null) {
                    attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());//注入分组名字
                }
            }
            CategoryEntity categoryEntity = categoryDao.selectById(attrRespVo.getCatelogId());

            if (categoryEntity != null) {
                attrRespVo.setCatelogName(categoryEntity.getName()); //注入分类名字
            }
            return attrRespVo;
        }).collect(Collectors.toList());

        //将处理完返回的集合，给page
        pageUtils.setList(respVos);

        return pageUtils;
    }





    @Autowired
    AttrDao attrDao;
    @Override
    public AttrRespVo getInfo(Long attrId) {
        List<Long> paths = new ArrayList();

        AttrRespVo attrRespVo =new AttrRespVo();
        AttrEntity attrEntity = attrDao.selectById(attrId);
        BeanUtils.copyProperties(attrEntity,attrRespVo);

        AttrAttrgroupRelationEntity attrRelation = attrAttrgroupRelationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId()));


    if(attrRelation!=null && attrRelation.getAttrGroupId()!=null){  //需要多添加一个判断，防止关联分组被注入null
        attrRespVo.setAttrGroupId(attrRelation.getAttrGroupId());
        AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrRelation.getAttrGroupId());
        attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
    }

        paths.add(attrEntity.getCatelogId());//先把当前层次的分类菜单id添加进去
        CategoryEntity categoryEntity = categoryDao.selectById(attrEntity.getCatelogId());
        attrRespVo.setCatelogName(categoryEntity.getName());

        //使用递归方法：将父类id都添加到数组内
        List<Long> patentsPath = findPatents(attrEntity.getCatelogId(),paths);
        //将数组反转
        Collections.reverse(patentsPath); //逆序

        //得到的List转化为数组形态，然后注入
     attrRespVo.setCatelogPath(patentsPath.toArray(new Long[0]));
        System.out.println("数据雅：：："+attrRespVo.toString());
        return  attrRespVo;
    }

    @Transactional
    @Override
    public void updateAttr(AttrVo attrVo) {
       AttrEntity attrEntity =new AttrEntity();
       BeanUtils.copyProperties(attrVo,attrEntity);
      attrDao.update(attrEntity,new QueryWrapper<AttrEntity>().eq("attr_id",attrVo.getAttrId()));
//        if(count == 0){
//                log.error("数据修改出现异常");
//        }
        //修改关联表数据
        //只有基本属性才可以关联：
        AttrAttrgroupRelationEntity attrR = new AttrAttrgroupRelationEntity();
        System.out.println("");
        if(attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() || attrEntity.getAttrType() ==ProductConstant.AttrEnum.ATTP_TYPE_SB.getCode() ){
        attrR = new AttrAttrgroupRelationEntity();
        attrR.setAttrGroupId(attrVo.getAttrGroupId());//更新分组id
        attrR.setAttrId(attrVo.getAttrId());

            Long selectCount = attrAttrgroupRelationDao.selectCount(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrVo.getAttrId()));
            //判断当前要做添加操作还是写入操作
            if(selectCount > 0){
                attrAttrgroupRelationDao.update(attrR, new UpdateWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrVo.getAttrId()));
            }else{
                attrAttrgroupRelationDao.insert(attrR);
            }
        }

    }

    @Override
    public PageUtils querySaleAttr(Map<String, Object> params,Integer attrType) {
            QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<>();

            //1.先查找分类id相同的参数

            queryWrapper.eq("attr_type",attrType);

        //2.然后再按照key进行检索
        String key = (String) params.get("key");

        if(!StringUtils.isEmpty(key)){ //如果不为空
            queryWrapper.and((wapper)->{
                wapper.eq("attr_id",key).or().like("attr_name",key);
            });
        }

        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                queryWrapper
        );

        PageUtils pageUtils = new PageUtils(page);
        List<AttrEntity> records = page.getRecords(); //1.获取到AttrEntyty数据
        List<AttrRespVo> respVos = records.stream().map((attrEntity) -> {
            //2.将attrEntity 存入到 vo数据内补全
            AttrRespVo attrRespVo = new AttrRespVo();
            BeanUtils.copyProperties(attrEntity, attrRespVo);

            //3.设置分类分组名字

            //3.1通过 当前的属性attr_id,查询到关联表信息
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = attrAttrgroupRelationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id",attrEntity.getAttrId()));

            //3.2 通过关联表的分组id，查询到分组,然后注入
            if(attrAttrgroupRelationEntity != null){
                Long attrGroupId = attrAttrgroupRelationEntity.getAttrGroupId();
                AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrGroupId);
                if (attrGroupEntity != null) {
                    attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());//注入分组名字
                }
            }
            CategoryEntity categoryEntity = categoryDao.selectById(attrRespVo.getCatelogId());

            if (categoryEntity != null) {
                attrRespVo.setCatelogName(categoryEntity.getName()); //注入分类名字
            }
            return attrRespVo;
        }).collect(Collectors.toList());

        //将处理完返回的集合，给page
        pageUtils.setList(respVos);

        return pageUtils;
    }




    /**
     * 根据分组id获取基本属性
     * **/
        @Override
        public List<AttrEntity> getattrgroupRelation(Long attrGroupId) {
            QueryWrapper<AttrAttrgroupRelationEntity> queryWrapper = new QueryWrapper();
            queryWrapper.eq("attr_group_id", attrGroupId);

            //1. 使用分组id获得对应分组
            List<AttrAttrgroupRelationEntity> attrAttrgroupRelationEntities = attrAttrgroupRelationDao.selectList(queryWrapper);

                List<Long> attrIdList = attrAttrgroupRelationEntities.stream().map((attr) -> {
                    return attr.getAttrId();
                }).collect(Collectors.toList());//将得到attrid ，收集为集合

            if(attrIdList==null || attrIdList.size()==0){
                return  null;
            }
               Collection<AttrEntity> attrEntities = this.listByIds(attrIdList); //根据attri的集合查找数据

         //2.将分组中的对应分类id，判断参数是否相同
            return (List<AttrEntity>) attrEntities;
        }

    @Override
    public PageUtils getNoRelation(Map<String, Object> params, Long attrGroupId) {

        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<>();

        //1.查询分组属于哪个分类
        AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrGroupId); //获得当前分组
        Long catelogId = attrGroupEntity.getCatelogId();//获取到了分类id

        //2.当前分组只能关联别的分组没有引用的属性
        //2.1 当前分类下的其他分组
        //判断分类id相同，且分组id不能是当前使用了的分组id
        List<AttrGroupEntity> attrGroupList = attrGroupDao.selectList(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));

        //获取到其他分组内所关联的分组id(不包含当前传过来的分组)
        List<Long> groupIds = attrGroupList.stream().map((attrG) -> {
            return attrG.getAttrGroupId();
        }).collect(Collectors.toList());

       if(groupIds==null || groupIds.size()==0){
           return  null;
       }
        //2.2这些分组关联的属性
        //判断其他的分组id是否 被关联  将被关联的数据筛选出来
        List<AttrAttrgroupRelationEntity> attrR = attrAttrgroupRelationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().in("attr_group_id", groupIds));

        List<Long> attrIds = attrR.stream().map((attr_id) -> {
            return attr_id.getAttrId();
        }).collect(Collectors.toList());
        //2.3 从当前分类的所有属性中移除这些属性:attR.getattrId（被关联的属性）
        //分类id要一致，且不能存在被关联的attrid
        QueryWrapper<AttrEntity>  wrapper =  new QueryWrapper<AttrEntity>().eq("catelog_id",catelogId).eq("attr_type", ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode());
        if(attrIds!=null || attrIds.size()>0){
        wrapper.notIn("attr_id", attrIds);
        }

        List<AttrEntity> attr = attrDao.selectList(wrapper);

        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)){ //如果key存在,则增加模糊查询条件
               wrapper.and((w)->{
            w.eq("attr_id",key).or().like("attr_name",key);
               });
        }
        IPage<AttrEntity> page = this.page(  //params:分页参数
                new Query<AttrEntity>().getPage(params),
                wrapper
        );

        PageUtils pageUtils = new PageUtils(page);

        return pageUtils;
    }

    @Override
    public PageUtils saleList(Map<String, Object> params, Long catelogId) {

            QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper();
        //1.通过分类id 和 类型 查找 到销售属性的数据
        queryWrapper.eq("catelog_id",catelogId).eq("attr_type",ProductConstant.AttrEnum.ATTP_TYPE_SALE);
        List<AttrEntity> attrEntities = attrDao.selectList(queryWrapper);

        //2.然后再按照key进行检索
        String key = (String) params.get("key");

        if(!StringUtils.isEmpty(key)){ //如果不为空
            queryWrapper.and((wapper)->{
                wapper.eq("attr_id",key).or().like("attr_name",key);
            });
        }

        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                queryWrapper
        );

        PageUtils pageUtils = new PageUtils(page);
        List<AttrEntity> records = page.getRecords(); //1.获取到AttrEntyty数据

        return  pageUtils;
    }



    @Override
    public PageUtils queryBaseOrSale(Map<String, Object> params, Long catelogId, String attrType) {
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("attr_type","base".equalsIgnoreCase(attrType)?1:0);

        //1.先查找分类id相同的参数
        if(catelogId != 0){
            queryWrapper.eq("catelog_id",catelogId);
        }
        //2.然后再按照key进行检索
        String key = (String) params.get("key");

        if(!StringUtils.isEmpty(key)){ //如果不为空
            queryWrapper.and((wapper)->{
                wapper.eq("attr_id",key).or().like("attr_name",key);
            });
        }

        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                queryWrapper
        );

        PageUtils pageUtils = new PageUtils(page);
        List<AttrEntity> records = page.getRecords(); //1.获取到AttrEntyty数据
        List<AttrRespVo> respVos = records.stream().map((attrEntity) -> {
            //2.将attrEntity 存入到 vo数据内补全
            AttrRespVo attrRespVo = new AttrRespVo();
            BeanUtils.copyProperties(attrEntity, attrRespVo);


            //3.设置分类分组名字

            //3.1通过 当前的属性attr_id,查询到关联表信息
          if("base".equalsIgnoreCase(attrType)){ //只有基本属性，才设置分组关联信息
              AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = attrAttrgroupRelationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id",attrEntity.getAttrId()));

              //3.2 通过关联表的分组id，查询到分组,然后注入
                  Long attrGroupId = attrAttrgroupRelationEntity.getAttrGroupId();
                  AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrGroupId);
                      attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());//注入分组名字


          }
            CategoryEntity categoryEntity = categoryDao.selectById(attrRespVo.getCatelogId());

            if (categoryEntity != null) {
                attrRespVo.setCatelogName(categoryEntity.getName()); //注入分类名字
            }
            return attrRespVo;
        }).collect(Collectors.toList());

        //将处理完返回的集合，给page
        pageUtils.setList(respVos);

        return pageUtils;
    }

//    @Override
//    public List<Long> selectSearchAttrIds(List<Long> attrIds) {
//        List<Long> searchIds = new ArrayList<>();
//        List<AttrEntity> searchAttrs = attrIds.stream().map(attrid -> {
//            AttrEntity attr = this.getById(attrid);
//            return attr;
//        }).filter(attr -> {
//            return attr.getSearchType() == 0;
//        }).collect(Collectors.toList());
//
//        for(AttrEntity attrEntity:searchAttrs){
//            searchIds.add(attrEntity.getAttrId());
//        }
//        return  searchIds;
//    }
    @Override
    public List<Long> selectSearchAttrIds(List<Long> attrIds) {
            //在Dao层编写，只获取id
    List<Long> searchAttrIds = baseMapper.selectSearchAttrIds(attrIds);
        return  searchAttrIds;
    }

    private List<Long> findPatents(Long catelogId,List<Long> paths){

        CategoryEntity category = categoryDao.selectById(catelogId);
        if(category.getParentCid() != 0){ //查看有无父类id
            paths.add(category.getParentCid());
            findPatents(category.getParentCid(),paths);
        }
        return paths;
    }




}