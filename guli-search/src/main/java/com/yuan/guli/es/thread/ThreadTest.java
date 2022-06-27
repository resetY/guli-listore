package com.yuan.guli.es.thread;

import com.zaxxer.hikari.util.UtilityElf;
import rx.exceptions.Exceptions;

import java.util.concurrent.*;

public class ThreadTest {


    public static void main(String[] args) {

        ExecutorService service = Executors.newFixedThreadPool(10);//创建10个线程

        /**
         * 线程的方式：
         *      1. 继承Thread
         *      2. 接口Runnable
         *      不能得到返回值
         *      3. Callable接口  +  FutureTash
         *      FutureTask futureTask = new FutureTask<>(new Th3());
         *      //阻塞等待整个线程执行完毕，发货结果
         *          futureTask.get();
         *       可以得到返回值，但资源损耗不好控制
         *      4. 线程池: 好处：不需要每一次执行任务都创建线程，然后销毁，这样会很耗费资源
         *              将多线程异步任务都提交给线程池
         *           一个系统中弄一两个线程池来执行任务
         *                      ExecutorService service = Executors.newFixedThreadPool(10);//创建10个线程
         *
         *                     线程池七大参数：
         *                     corePoollSize：线程池准备就绪的线程数量设置，线程池如果不销毁，会一直存在
         *                                  (除非设置了：allowCoreThreadTimeOut)
         *                      maximumPoolSize：最大线程数量，异步进来最多能被多少线程接收
         *                                  (用于控制资源)
         *                      keepAliveTime：存活时间。如果线程空闲，没有新任务，那么过这个时间线程自动销毁
         *                                               unit：时间单位
         *                      BlockingQueue<Runnable>woreQueue:阻塞队列。如果任务很多，会将多的任务放在队列里
         *                      threadFactory：线程创建工厂
         *                      handler：队列满了的话，handler按照指定拒绝策略，拒绝执行任务
         *
         * 工作顺序：
         *  1.创建线程池完毕，准备核心线程数量，线程自动进行接收任务
         *  2.线程任务满了，再进来的任务放入阻塞队列，一旦有线程释放，那么自动接收 队列的任务
         *  3.阻塞队列满的情况下，自动开启新线程进行接收任务
         *  4.如果最大线程满了，且释放了又没有任务，那么到了存活时间自动销毁
         * 5.最大线程满了，新任务过来，线程池使用拒绝策略，将发送过来的新任务拒绝
         *
         *   new LinkedBlockingDeque<>()：默认Integer最大值，我们内存不可能占的下的
         *
         * **/

                                        //自定义线程池
        ThreadPoolExecutor th = new ThreadPoolExecutor(10,
                200,
                10,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(100000),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()); //拒绝策略
      //  ThreadPoolExecutor th = new ThreadPoolExecutor(;//自定义线程池
Executors.newCachedThreadPool(); //带缓存的线程池，核心为0
        Executors.newSingleThreadExecutor(); //单线程化线程池
        Executors.newFixedThreadPool(10);//核心和max相同大小的线程池(一值存活的线程)
        Executors.newScheduledThreadPool(10); //定时任务的线程池



        service.submit(new Th1()); //可以获取到异步任务返回值
           service.execute(new Th2());               //只能执行异步任务


    }


    public static  class Th1 extends  Thread{
        @Override
        public void run() {
            System.out.println("当前线程名字为："+Thread.currentThread().getName());
            int n= 10/2;
            System.out.println("线程结束,结果："+n);

        }
    }
    public static  class Th2 implements Runnable{
        @Override
        public void run() {
            System.out.println("当前线程名字为："+Thread.currentThread().getName());
            int n= 90/2;
            System.out.println("线程结束,结果："+n);
        }
}

public static class  Th3 implements Callable {

    @Override
    public Object call() throws Exception {
        return null;
    }
}
}
