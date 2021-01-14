package com.java.zanmessage.utils;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ThreadUtils {

    private static Handler handler = new Handler(Looper.getMainLooper());
    //创建单线程线程池
    //由单线程线程池，改为多线程线程池
    private static Executor executor = Executors.newCachedThreadPool();

    //在子线程中执行
    public static void runOnSubThread(Runnable runnable) {
        executor.execute(runnable);
    }

    //返回到ui线程中执行任务
    public static void runOnUIThread(Runnable runnable) {
        handler.post(runnable);
    }

    //判断当前是否是主线程
    public static boolean isMainThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }

}
