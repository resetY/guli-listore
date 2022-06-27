package com.yuan.guli.guliproduct.web;

import com.yuan.common.utils.R;
import com.yuan.guli.guliproduct.entity.CategoryEntity;
import com.yuan.guli.guliproduct.service.CategoryService;
import com.yuan.guli.guliproduct.web.vo.Catelog2Vo;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Controller
public class IndexController {

    @Autowired
    CategoryService categoryService;

    @Autowired
    RedissonClient redisson;
    @RequestMapping({"/","/index.html"}) //访问/ 啥的都进行index.html的跳转

    public String  indexPage(Model model){
        //1.查询所有的一级分类
        List<CategoryEntity> categorys = categoryService.getLevel1Categorys();

        model.addAttribute("categorys",categorys);
        //跳转到index视图
        return  "index"; //不需要.html后缀，th配置中yml里面自动配置了
    }
            @ResponseBody
            @RequestMapping("/index/catelog")
            public Map<String,List<Catelog2Vo>>getcagelogJson() {
//            Map<String, List<Catelog2Vo>> map = categoryService.getcateJson();
            Map<String, List<Catelog2Vo>> map = categoryService.getCatelogJson();
            return map;
            }

            @ResponseBody
            @RequestMapping("/index/mysqlcatelog")
            public Map<String,List<Catelog2Vo>> getData(){
                Map<String, List<Catelog2Vo>> map = categoryService.getmysqlDb();
                return map;
            }


//**********************************************************************************************
    @ResponseBody
    @RequestMapping("/hello")
    public String  hello(){
        //1.获取一把锁：如果名字一样的，那么都是同一把锁
        RLock lock = redisson.getLock("myLock");

        //2.加锁:lock方法的加锁是阻塞式等待，即如果加不到锁，就会一直等
        //看门狗机制：进行锁的自动续期，如果业务时间过长，会自动进行锁销毁时间的续期(默认销毁时间为30s)
        //防止死锁：业务宕机，没解锁，那么30s也会自动释放
        lock.lock(10, TimeUnit.SECONDS);
        try{
            System.out.println("加锁成功，执行业务"+Thread.currentThread().getId());
            Thread.sleep(3000); //业务执行了3秒
        }catch (Exception e){

        }finally {
            //3.解锁,不论业务是否成功，都需要解锁

            lock.unlock();
            System.out.println("解锁成功...."+Thread.currentThread().getId());
        }
        return "hello";
    }


    @Autowired
    StringRedisTemplate redisTemplate;
/**
 * 读写锁的测试接口:写锁
 *      读写为同一把锁
 * */
    @ResponseBody
    @RequestMapping("/write")
    public String  write(){

        RReadWriteLock readWriteLock = redisson.getReadWriteLock("rw-lock");
        String uuid = UUID.randomUUID().toString();
        RLock wlock = readWriteLock.writeLock();//拿到写锁
        try{

            wlock.lock(); //加写锁
            System.out.println("加锁成功，执行业务"+Thread.currentThread().getId());

            redisTemplate.opsForValue().set("write",uuid);
        }catch (Exception e){

        }finally {
            wlock.unlock(); //释放
            System.out.println("写锁释放成功");
        }
        return uuid;
    }
    /**
     * 读写锁的测试接口:读取
     * */
    @ResponseBody
    @RequestMapping("/read")
    public String  read(){
        RReadWriteLock readWriteLock = redisson.getReadWriteLock("rw-lock");
        //加读锁
        RLock rLock = readWriteLock.readLock();

        String uuid = UUID.randomUUID().toString();
       String  write ="";
        try{
            rLock.lock();
           write = redisTemplate.opsForValue().get("write");
            System.out.println("读锁的值："+write);
            Thread.sleep(30000);
        }catch (Exception e){
        }finally {
            rLock .unlock();
            System.out.println("读锁释放成功");
        }
        return write;
    }


    /**
     * 信号量模拟：车库停车
     *      信号量：可以用作进行限流操作
     * */
    @ResponseBody
    @RequestMapping("/car")
    public String car(){
        RSemaphore semaphore = redisson.getSemaphore("car"); //name：信号量的名字

       //    semaphore.acquire();(阻塞式获取)获取一个信号，获取一个值
        boolean result = semaphore.tryAcquire();//有车位停，没车位就算了
        if(result){
            //业务代码执行
        }
        return result?"ok":"no";
    }   /**
     * 信号量模拟：车库停车
     * */
    @ResponseBody
    @RequestMapping("/go")
    public String go(){
        RSemaphore semaphore = redisson.getSemaphore("car"); //name：信号量的名字
        try{
            semaphore.release(); //释放一个信号，类似将车从车库开走
        }catch (Exception e){
        }
        return "gogogo";
    }

    /**
     *  闭锁：
     * 模拟场景：放学，只有全校5个班的学生走了，才能锁学校大门
     * */
    @ResponseBody
    @RequestMapping("/school")
    public String school(){
        RCountDownLatch door = redisson.getCountDownLatch("door");//name：信号量的名字
        try{
            door.trySetCount(5);//等待五个
            door.await();//等待闭锁完成
        }catch (Exception e){
        }
        //当闭锁完成后，锁会被自动进行删除
        return "走光了，锁门，大爷回家了";
    }
    @ResponseBody
    @RequestMapping("/class/{id}")
    public String classroom(@PathVariable("id") Integer id){
        RCountDownLatch door = redisson.getCountDownLatch("door");
            door.countDown();//计数减1
        return  id+"班的人走了";
    }

}
