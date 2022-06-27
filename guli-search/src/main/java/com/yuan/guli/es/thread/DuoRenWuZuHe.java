package com.yuan.guli.es.thread;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 多任务组合：开发常见
 *      allOf:等待所有任务完成
 *      anyOf：只需要一个任务完成
 * */
public class DuoRenWuZuHe {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        //注意：如果不添加线程池进去，任务延缓执行的话，线程会直接被处理，然后丢失
        System.out.println("-----main-------");
        ExecutorService service = Executors.newFixedThreadPool(10);//创建10个线程
        CompletableFuture<String> f1 = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("查询到商品图片信息");
            return "mnv.jpg";
        }, service);
        CompletableFuture<String> f2 = CompletableFuture.supplyAsync(() -> {
            System.out.println("查询到商品属性信息");
            return "属性";
        }, service);
        CompletableFuture<String> f3 = CompletableFuture.supplyAsync(() -> {
            System.out.println("查询到商品介绍信息");
            return "介绍";
        }, service);
//        CompletableFuture<Void> all = CompletableFuture.allOf(f1,f2,f3);
        CompletableFuture<Object> all = CompletableFuture.anyOf(f1, f2, f3);
        all.get();
        System.out.println("-----main_end-------");
    }
}
