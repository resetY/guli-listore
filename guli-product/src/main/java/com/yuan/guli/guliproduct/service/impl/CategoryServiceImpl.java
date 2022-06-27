package com.yuan.guli.guliproduct.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.injector.methods.SelectList;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.yuan.common.utils.R;

import com.yuan.guli.guliproduct.dao.CategoryBrandRelationDao;
import com.yuan.guli.guliproduct.entity.BrandEntity;
import com.yuan.guli.guliproduct.entity.CategoryBrandRelationEntity;
import com.yuan.guli.guliproduct.web.vo.Catelog2Vo;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.crypto.hash.Hash;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuan.common.utils.PageUtils;
import com.yuan.common.utils.Query;

import com.yuan.guli.guliproduct.dao.CategoryDao;
import com.yuan.guli.guliproduct.entity.CategoryEntity;
import com.yuan.guli.guliproduct.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;



@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {
//    @Autowired
//    private CategoryDao categoryDao;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );
        return new PageUtils(page);
    }



    /**
     * baseMapper. = CategoryDao   一个基础了总业务类的泛型
     * */
    @Override
    public List<CategoryEntity> listWithTree() {
        //1.查询出所有分类
        List<CategoryEntity> category = baseMapper.selectList(null);//没有查询条件

        //2. 组装成父子树形结构
        //2.1找到所有的一级分类：父分类id为0
        List<CategoryEntity> level1Menus = category.stream().filter((categoryEntity) ->
      categoryEntity.getParentCid() == 0
        ).map((menu)->{
            menu.setChildren(getChildrens(menu,category)); //把当前菜单子分类放进去
            return menu;
        }).sorted((menu1,menu2)->{ //排序,前后菜单对比来进行排序
                //第一个的顺序- 第二个的顺序  =  升降序值
            return (menu1.getSort()==null?0:menu1.getSort()) - (menu2.getSort()==null?0:menu2.getSort());
        }).collect(Collectors.toList());
        return level1Menus;
    }



       /**
     * 获取某一个菜单的子菜单 和 菜单数据
     * 递归查找某一个菜单的子菜单
     * @param  root 当前菜单
     * @param  all   全部菜单
     * */
    private List<CategoryEntity> getChildrens(CategoryEntity root,
                                              List<CategoryEntity> all){

        List<CategoryEntity> children = all.stream().filter(categoryEntity -> {
            //如果：这里放进来的子类菜单数据的父id  =   当前菜单 的id 那么就是父子关系
            return categoryEntity.getParentCid().equals(root.getCatId()); //这里不可用 ==  对比
        }).map((categoryEntity) -> { //重新进行映射
            //1.找到子菜单
            categoryEntity.setChildren(getChildrens(categoryEntity, all)); //在当前菜单，调用自身找到所有子菜单
            return categoryEntity;
            //2.第二个菜单的排序
        }).sorted((menu1, menu2) -> {
            //防止空指针异常，也就是菜单序号为空
            return (menu1.getSort()==null?0:menu1.getSort()) - (menu2.getSort()==null?0:menu2.getSort());
        }).collect(Collectors.toList());//收集到所有菜单，这些子菜单
        return children;
    }




    /**
     * 批量删除菜单的方法
     * */
    @Override
    public void removeMenuByIds(List<Long>asList) {

        //TODO 待办： 1.检查当前删除的菜单，是否被引用
/**
        逻辑删除：即一个字段来判断是否被删除  物理删除：直接删除
    逻辑删除方式：
                借助mybatis-plus进行逻辑删除的配置
                    自动将删除变为修改逻辑删除字段
 **/
        int count = baseMapper.deleteBatchIds(asList);
    }


    /**
     * 修改分类菜单的时候，关联表内的数据也应该进行修改
     *
     * **/
    @Autowired
    CategoryBrandRelationDao brandRelationDao;
    @Autowired
   CategoryDao categoryDao;


    /**
     * CacheEvict：
     *      value：指定清除哪个片区的缓存
     *      key:片区里的某个缓存，注意：key的值必须要加单引号
     *      效果：修改内容后清除缓存，只有下次获取数据才会重新添加到缓存内
     *      allEntries = true:true为设置删除分区下的所有缓存数据
     * */
   //@CacheEvict(value = {"category"},allEntries = true):true为删除分区下的所有缓存数据
    @Caching(evict = {  //一个修改操作，删除两个缓存数据 ，
            @CacheEvict(value = {"category"},key = "'getLevel1Categorys'"),
            @CacheEvict(value = {"category"},key = "'getCatelogJson'")
    })
   //@CachePut   //双写模式
    @Override
    @Transactional  //加上事务，需要启动类有事务开启的支持
    public void updateBrandCategory(CategoryEntity categoryEntity) {

        //根据传过来的数据，修改brand
        int count1 = categoryDao.updateById(categoryEntity);
        if(count1 == 0){
            log.error("数据更新出现异常");
        }

        //1.根据条件查询到关联表数据，然后修改：
        QueryWrapper<CategoryBrandRelationEntity> queryWrapper = new QueryWrapper();
        queryWrapper.eq("catelog_id",categoryEntity.getCatId());
        CategoryBrandRelationEntity categoryBrandRelationEntity = brandRelationDao.selectOne(queryWrapper);

        //2. 注入修改更新的关联表内容：
        categoryBrandRelationEntity.setCatelogId(categoryEntity.getCatId());
        categoryBrandRelationEntity.setCatelogName(categoryEntity.getName());

        //3.进行修改：
        int count2 = brandRelationDao.updateById(categoryBrandRelationEntity);
        if(count2 == 0){
            log.error("数据更新出现异常");
        }
    }

    /**
     * @Cacheable({"category"})
     *     缓存中默认存储的数据，是采用java序列化后的数据进行存储到redis的，默认过期时间为-1 永远不过期
     *     且key名称为自主生成的
     *         代表该方法的结果需要缓存，
     *          如果缓存中有，方法不用调用 ，没用则调用，最后将方法结果放入缓存
     *                【缓存分区(按照业务类型进行区分)】
     *   进行改进：
     *         key:
     *              指定生成缓存的key名字 #root:可以取各种值作为key的名称，如：method.name-方法名
     *         且加上过期时间，不按照默认的设置进行:
     *              spring.cache.redis.time-to-live=3600000 一小时
     *         将数据保存为json格式：
     *               了解保存缓存原理：CacheAutoConfiguration -> RedisCacheConfiguration
     *               ->自动配置了缓存管理器 -> 初始化所有缓存 -> 每个缓存决定使用什么配置
     *              -> 如果redisCacheConfiguration有就用已经有的配置，没用就用默认配置
     *              -> 如果想要修改缓存配置，只需要容器中放入一个RedisCacheConfiguration即可
     *              -> 就会运用到RedisCacheManager的所有应用
     * **/
        //【缓存分区(按照业务类型进行区分)】
    @Cacheable(value = {"category"},key = "#root.method.name")
    @Override
    public List<CategoryEntity> getLevel1Categorys() {
        System.out.println("getLevel1");
        QueryWrapper <CategoryEntity>wrapper = new QueryWrapper();
        wrapper.eq("parent_cid",0);
        List <CategoryEntity>categoryEntities = categoryDao.selectList(wrapper);
        return  categoryEntities;
    }

//    @Override
//    public Map<String,List<Catelog2Vo>> getCatelogJson() {
//        //1.查询出所有的一级级分类
//        List<CategoryEntity> categorys = getLevel1Categorys();
//
//        Map<String, List<Catelog2Vo>> parent_id = categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
//            //v:当前遍历的一级分类
//            List<CategoryEntity> categoryEntities = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", v.getCatId()));
//            List<Catelog2Vo> catelog2VoList = null;
//            if (categoryEntities != null) {
//                catelog2VoList = categoryEntities.stream().map(item -> {
//                    //item:每个二级分类的数据
//                    Catelog2Vo catelog2Vo = new Catelog2Vo(item.getParentCid().toString(), null, item.getCatId().toString(), item.getName());
//                    //1.寻找当前2级分类的3级分类，封装为vo
//
//                    List<CategoryEntity> category3 = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", item.getCatId()));
//                    if (category3 != null) {
//                        List<Catelog2Vo.Catelog3Vo> collect3 = category3.stream().map(item3 -> {
//                            Catelog2Vo.Catelog3Vo catelog3Vo = new Catelog2Vo.Catelog3Vo(item3.getParentCid().toString(), item3.getCatId().toString(), item3.getName());
//                            return catelog3Vo;
//                        }).collect(Collectors.toList());
//                        catelog2Vo.setCatelog3List(collect3); //注入三级菜单
//                    }
//                    return catelog2Vo;
//                }).collect(Collectors.toList());
//            }
//            return catelog2VoList;
//        }));
//
//        return  parent_id;
//    }




/**
 * 优化查询分类的业务
 *
 * **/

    @Override
        public Map<String,List<Catelog2Vo>> getcateJsonFormDb() {
        /**
         *   1.空结果也进行缓存-解决缓存穿透
         *  2.将空结果设置宿随机值的过期时间-缓存雪崩
         *  3.缓存击穿-分布式锁
         *          ① 加锁方式1：synchronized (this){代码}，springboot的所有组件(对象)的容器
         *                  都是单例的，那么如果是单例的，那么这个锁就可行。
         *
         * */
        synchronized (this) {
            return getDataForDB();

        }
    }

    //将查询数据库和拿取缓存数据的代码进行封装，方便调用
    private Map<String, List<Catelog2Vo>> getDataForDB() {
        //得到锁后，需要进入缓存中进行查询，如果有数据，那么直接返回数据结果
        String catalogJson = redisTemplate.opsForValue().get("catalogJson");
        if(!StringUtils.isEmpty(catalogJson)){
            Map<String, List<Catelog2Vo>> catelogs = JSONObject.parseObject(catalogJson, new TypeReference<Map<String, List<Catelog2Vo>>>() {
            });
            System.out.println("进行本地锁后，拿取了缓存数据");
            return catelogs;
        }
        //会发现一个问题，数据库被查询了两次，原因是查询完数据，数据还没放进缓存，锁就被释放，导致又查询了一次
        System.out.println("开始查询了数据库!!!!!!!");

        //1. 查询所有分类菜单
                    List<CategoryEntity> categoryList = baseMapper.selectList(null);

                    //1.查询出所有的一级级分类
                    //  List<CategoryEntity> categoryEntities = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", v.getCatId()));
                    List<CategoryEntity> categorys = getParent_cid(categoryList, 0L);

                    Map<String, List<Catelog2Vo>> parent_id = categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
                        //v:当前遍历的一级分类:抽取为方法
                        List<CategoryEntity> categoryEntities = getParent_cid(categoryList, v.getCatId());
                        List<Catelog2Vo> catelog2VoList = null;
                        if (categoryEntities != null) {
                            catelog2VoList = categoryEntities.stream().map(item -> {
                                //item:每个二级分类的数据
                                Catelog2Vo catelog2Vo = new Catelog2Vo(item.getParentCid().toString(), null, item.getCatId().toString(), item.getName());
                                //1.寻找当前2级分类的3级分类，封装为vo

                                List<CategoryEntity> category3 = getParent_cid(categoryList, item.getCatId());
                                if (category3 != null) {
                                    List<Catelog2Vo.Catelog3Vo> collect3 = category3.stream().map(item3 -> {
                                        Catelog2Vo.Catelog3Vo catelog3Vo = new Catelog2Vo.Catelog3Vo(item3.getParentCid().toString(), item3.getCatId().toString(), item3.getName());
                                        return catelog3Vo;
                                    }).collect(Collectors.toList());
                                    catelog2Vo.setCatalog3List(collect3); //注入三级菜单
                                }
                                return catelog2Vo;
                            }).collect(Collectors.toList());
                        }
                        return catelog2VoList;

        }));
        //查询完数据后，立即保存在缓存中，防止发生锁时序问题
        String json_catelogs = JSON.toJSONString(parent_id);
        redisTemplate.opsForValue().set("catalogJson", json_catelogs, 1, TimeUnit.DAYS); //过期时间为1天
        return parent_id;
    }

    //被抽取出来的查询parent_id的方法
    private List<CategoryEntity> getParent_cid( List<CategoryEntity> categoryList,Long parentCid) {
        List<CategoryEntity> collect = categoryList.stream().filter(item -> {
            return item.getParentCid().equals(parentCid);
        }).collect(Collectors.toList());

        return  collect;  //返回一些parentid相同的
    }


    /**
     * 加上分布式锁RedisLock
     * 防止死锁：给锁设置自动过期时间
     * **/
    public Map<String,List<Catelog2Vo>> getcateJsonFormDbWithRedisLock(){
        String uuid = "zkt"+UUID.randomUUID().toString();
        //1.占分布式锁，去redis占坑  setIfAbsent = setNX  设置key的过期时间为30s
        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock",uuid,300,TimeUnit.SECONDS);
        //让所有服务都去占坑，假如lock数据，如果占坑成功返回true
        if(lock){ //加锁成功
            System.out.println("分布式锁获取成功");
            Map<String, List<Catelog2Vo>> dataForDB;
       try {
         dataForDB = getDataForDB();
       }finally {
           //  redisTemplate.delete("lock"); //业务执行完毕，需要把锁删除，让别的线程可进入
//            String lock_value = redisTemplate.opsForValue().get("lock");
//            if(lock_value.equals(uuid)){
//                redisTemplate.delete("lock"); //删除自己服务的锁
//            }
           //删除锁:
           //获取uuid对比 + 对比成功删除 = 原子操作（同一时间操作）
           //脚本保证原子性操作： 获取的值不存在返回 或者 存在的时候删除失败：0  存在且删除：返回1
           String script= "if redis.call('get',KEYS[1] == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
           redisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class),
           Arrays.asList("lock"), uuid);
       }
            return dataForDB;  //加锁成功的，执行业务
        }else{ //加锁失败，需要重试
            System.out.println("分布式锁占领失败，重新加入分布式锁");
            try {
                Thread.sleep(200);
            }catch (Exception e){

            }
            return getcateJsonFormDbWithRedisLock();//自旋，重新加入锁
        }

        }



    /**
     * 从redis数据库获取缓存的方法,
     *      注：缓存中我们只存储json数据，json跨语言也能通，所以json好用，可存
     * */
    @Autowired
    StringRedisTemplate redisTemplate;
    public Map<String,List<Catelog2Vo>> getcateJson(){

            //1.如果缓存中存在数据，那么直接拿取缓存的数据，如果没有，那么去数据库查询然后在存储在缓存当中
            String catalogJson = redisTemplate.opsForValue().get("catalogJson");
            if (StringUtils.isEmpty(catalogJson)) {  //如果为空，查询数据库数据,查询完
                Map<String, List<Catelog2Vo>> catelogs = getcateJsonFormDbWithRedisson();
                //保存到缓存中
//                String json_catelogs = JSON.toJSONString(catelogs);
//                redisTemplate.opsForValue().set("catalogJson", json_catelogs, 1, TimeUnit.DAYS); //过期时间为1天
                return catelogs;
            }
         System.out.println("缓存命中，直接返回");
            //2.进行反序列化：将缓存中的json数据进行反序列化，复杂类型采用泛型TypeRefernce
            Map<String, List<Catelog2Vo>> CatelogListMap = JSONObject.parseObject(catalogJson, new TypeReference<Map<String, List<Catelog2Vo>>>() {
            });
            return CatelogListMap; //返回数据
        }

    @Autowired
    RedissonClient redisson;

    /**
     * 使用框架进行分布式锁的使用：
     * 防止死锁：给锁设置自动过期时间
     *      缓存数据如何和数据库保存一致(一致性问题)： 双写模式  和   失效模式
     * **/
    public Map<String,List<Catelog2Vo>> getcateJsonFormDbWithRedisson(){

        //1.注入redis的锁,锁的名字牵扯到锁的粒度，粒度越细，资源少，运行块
        //注意一个问题：锁的名字最好不要为lock，不然很容易被其他服务使用到
        RLock lock = redisson.getLock("catalogJson-lock");
        lock.lock(30,TimeUnit.SECONDS);

        System.out.println("分布式锁获取成功");
            Map<String, List<Catelog2Vo>> dataForDB;
            try {
                dataForDB = getDataForDB();
            }finally {
                lock.unlock();
            }
            return dataForDB;  //加锁成功的，执行业务
        }



        //使用注解缓存的方式：
       @Override
      @Cacheable(value = {"category"},key = "#root.method.name",sync = true)
    public Map<String,List<Catelog2Vo>> getCatelogJson() {
        //1.查询出所有的一级级分类
        List<CategoryEntity> categorys = getLevel1Categorys();
        Map<String, List<Catelog2Vo>> parent_id = categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            //v:当前遍历的一级分类
            List<CategoryEntity> categoryEntities = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", v.getCatId()));
            List<Catelog2Vo> catelog2VoList = null;
            if (categoryEntities != null) {
                catelog2VoList = categoryEntities.stream().map(item -> {
                    //item:每个二级分类的数据
                    Catelog2Vo catelog2Vo = new Catelog2Vo(item.getParentCid().toString(), null, item.getCatId().toString(), item.getName());
                    //1.寻找当前2级分类的3级分类，封装为vo
                    List<CategoryEntity> category3 = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", item.getCatId()));
                    if (category3 != null) {
                        List<Catelog2Vo.Catelog3Vo> collect3 = category3.stream().map(item3 -> {
                            Catelog2Vo.Catelog3Vo catelog3Vo = new Catelog2Vo.Catelog3Vo(item3.getParentCid().toString(), item3.getCatId().toString(), item3.getName());
                            return catelog3Vo;
                        }).collect(Collectors.toList());
                        catelog2Vo.setCatalog3List(collect3); //注入三级菜单
                    }
                    return catelog2Vo;
                }).collect(Collectors.toList());
            }
            return catelog2VoList;
        }));
        return  parent_id;
    }

    @Override
    public Map<String, List<Catelog2Vo>> getmysqlDb() {
        return null;
    }


}



