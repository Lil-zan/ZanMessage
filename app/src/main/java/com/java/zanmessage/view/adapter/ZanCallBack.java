package com.java.zanmessage.view.adapter;

import com.hyphenate.EMCallBack;
import com.java.zanmessage.utils.ThreadUtils;

public abstract class ZanCallBack implements EMCallBack {
    //定义登陆成功抽象方法让实现类处理逻辑
    public abstract void onMainSuccess();
    //定义登陆失败抽象方法
    public abstract void onMainError(String s);

    //登陆成功
    @Override
    public void onSuccess() {
        //把子线程逻辑发送到主线程执行
        ThreadUtils.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                //实现类看子类
                onMainSuccess();
            }
        });
    }

    @Override
    public void onError(int i,final String s) {
        ThreadUtils.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                //实现类看子类
                onMainError(s);
            }
        });
    }


    @Override
    public void onProgress(int i, String s) {

    }
}
