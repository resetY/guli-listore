package com.yuan.guli.es.thread;

import org.jcp.xml.dsig.internal.SignerOutputStream;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class YibuTest {
    public static void main(String[] args) throws ExecutionException, InterruptedException {

        ExecutorService service = Executors.newFixedThreadPool(10);//创建10个线程


        /**
         * 线程串行化
         *   thenRun:不能获取到上一步执行结果
         *   thenAcceptAsync:可以接收上一步的结果
         *   thenApplyAsync:可以接收上一步结果,可以有返回值
        **/
    CompletableFuture.runAsync(() -> { //任务一
            //执行异步任务
            System.out.println("当前线程名字为：" + Thread.currentThread().getName()); //pool-1-thread-1：使用默认线程名字开头为pool
            int n = 108 / 3;
            System.out.println("线程结束,结果：" + n);
        }, service);

        //方法完成后的处理:
        CompletableFuture<Integer> result = CompletableFuture.supplyAsync(() -> {//任务二
            System.out.println("当前线程名字为：" + Thread.currentThread().getName()); //pool-1-thread-1：使用默认线程名字开头为pool
            int n = 100 / 3;
            System.out.println("线程结束,结果：" + n);
            return n;
        }, service).whenCompleteAsync((resp,error)->{

        },service).thenApplyAsync(r->{
            System.out.println("上个任务返回的结果是:"+r);
            return  1;
        },service);
        System.out.println(result.get()); //返回值获取
        System.out.println("main.................");

    }
}
//方法完成后的感知:
//        CompletableFuture<Integer> result = CompletableFuture.supplyAsync(() -> {
////            System.out.println("当前线程名字为：" + Thread.currentThread().getName()); //pool-1-thread-1：使用默认线程名字开头为pool
////            int n = 10 / 5;
////            System.out.println("线程结束,结果：" + n);
////            return n;
////        }, service).whenComplete((res,error)->{  //类似promise
////            System.out.println("已经执行异步任务成功......."+"得到结果："+res+"异常："+error);
////        }).exceptionally(error->{ //处理异常,不摘抛出了
////            System.out.println("错误：0不能作为除数");
////            return -1;
////        });