package com.java.zanmessage.presenter;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.java.zanmessage.utils.ThreadUtils;
import com.java.zanmessage.view.activity.RegistActivity;
import com.java.zanmessage.view.activity.RegistInterface;

import cn.leancloud.AVUser;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class RegistPresenter implements IntfRegistPresenter {
    private RegistInterface registActivity;

    public RegistPresenter(RegistActivity registActivity) {
        this.registActivity = registActivity;

    }

    @Override
    public void regist(String userName, String password) {
        AVUser user = new AVUser();

        // 等同于 user.put("username", "Tom")
        user.setUsername(userName);
        user.setPassword(password);

        //        可选
        //        user.setEmail("tom@leancloud.rocks");
        //        user.setMobilePhoneNumber("+8618200008888");

        //        设置其他属性的方法跟 AVObject 一样
        //        user.put("gender", "secret");

        user.signUpInBackground().subscribe(new Observer<AVUser>() {
            public void onSubscribe(Disposable disposable) {
            }

            public void onNext(final AVUser user) {
                // leanCloud注册成功
                ThreadUtils.runOnSubThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //注册环信
                            EMClient.getInstance().createAccount(userName, password);
                        } catch (HyphenateException e) {
                            e.printStackTrace();
                            //如果环信注册失败，在leanCloud把用户注册的信息删除掉。
                            user.delete();
                            //因为leancloud回调是子线程，因此需要把子线程任务发送至主线程提示。
                            ThreadUtils.runOnUIThread(new Runnable() {
                                @Override
                                public void run() {
                                    //在回调中显示错误信息
                                    registActivity.onRegist(false, "环信注册失败", userName, password);
                                }
                            });
                        }
                    }
                });
                //leanCloud和环信同时注册成功。
                ThreadUtils.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        registActivity.onRegist(true, "注册成功", userName, password);
                    }
                });


            }

            public void onError(Throwable throwable) {
                // 注册失败（通常是因为用户名已被使用）
                ThreadUtils.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        registActivity.onRegist(false, "云数据库保存失败", userName, password);
                    }
                });
            }

            //leanCloud没有过多解释。
            public void onComplete() {
            }
        });

    }
}
