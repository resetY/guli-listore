package com.yuan.guli.es.thread;

import java.util.concurrent.*;

public class ListThreadTest {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(10);//创建10个线程
        System.out.println("main......开始...........");

//        CompletableFuture<Void> f1 = CompletableFuture.runAsync(() -> { //任务一
//            //执行异步任务
//            System.out.println("当前线程名字为：" + Thread.currentThread().getName()); //pool-1-thread-1：使用默认线程名字开头为pool
//            int n = 108 / 3;
//            System.out.println("线程结束,结果：" + n);
//        }, service);


        //方法完成后的处理:
        CompletableFuture<Object> of1 = CompletableFuture.supplyAsync(() -> {//任务一
            System.out.println("当前线程名字为：" + Thread.currentThread().getName()); //pool-1-thread-1：使用默认线程名字开头为pool
            int n = 100 / 50;

            System.out.println("线程结束,结果：" + n);
            return n;
        }, service);

        //方法完成后的处理:
        CompletableFuture<Object> f2 = CompletableFuture.supplyAsync(() -> {//任务二
            System.out.println("当前线程名字为：" + Thread.currentThread().getName()); //pool-1-thread-1：使用默认线程名字开头为pool
            int n = 100 / 3;
            try {
                Thread.sleep(5000); //睡觉
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("线程结束,结果：" + n);
            return n;
        }, service).whenCompleteAsync((resp,error)->{
        },service).thenApplyAsync(r->{
            System.out.println("上个任务返回的结果是:"+r);
            return  1;
        },service);

//
//        /**
//         * runAfterBothAsync():没有返回值,不可感知1 2 状态
//         * */
//        CompletableFuture<Void> f3 = f1.runAfterBothAsync(f2, () -> {  //任务结束要执行的操作
//            System.out.println("任务3开始");
//        }, service);
//
//
//        /**
//         * runAfterBothAsync:可以组合任务,但是不能感知前面任务的返回结果
//         * */
//        f1.thenAcceptBothAsync(f2,(r1,r2)->{ //
//        System.out.println("任务三开始");
//        System.out.println(r1); //r1没有返回值的void
//        System.out.println(r2);
//        System.out.println("结束");
//    },service);
//
//        /**
//         * 可拿到结果,也可返回
//         * */
//        CompletableFuture<Integer> integerCompletableFuture = f1.thenCombineAsync(f2, (r1, r2) -> { //
//            System.out.println("任务三开始");
//            System.out.println(r1); //r1没有返回值的void
//            System.out.println(r2);
//            System.out.println("结束");
//            return 33333;
//        }, service);
//        try {
//            System.out.println(integerCompletableFuture.get());
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }
//
//        /**
//         * runAfterEither():两个任务只要有一个完成,就执行3,无返回值,不能感知
//         * */
//    f1.runAfterEitherAsync(f2,()->{
//        System.out.println("任务三开始:runAfterEither");
//        System.out.println("结束");
//    },service);
//
//
//        /**
//         * acceptEitherAsync:感知结果,不返回,需要相同类型的组合,且一个执行完毕就可执行
//         * */
//     of1.acceptEitherAsync(f2,(r)->{ //f2的结果
//         System.out.println(r+"牛逼啊");
//     },service);

        /**
         * applyToEitherAsync:感知结果,返回,需要相同类型的组合,且一个执行完毕就可执行
         * */
        CompletableFuture<String> stringCompletableFuture = of1.applyToEitherAsync(f2, (r) -> {
            return "666666666666666666";
        }, service);
        System.out.println(stringCompletableFuture.get());

        System.out.println("main......结束...........");
}

}
