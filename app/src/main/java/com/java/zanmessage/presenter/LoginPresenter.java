package com.java.zanmessage.presenter;


import android.util.Log;

import com.hyphenate.chat.EMClient;
import com.java.zanmessage.view.activity.LoginInterface;
import com.java.zanmessage.view.adapter.ZanCallBack;

public class LoginPresenter implements IntfLoginPresenter {
    private LoginInterface loginActivity;

    public LoginPresenter(LoginInterface loginAcitity) {
        this.loginActivity = loginAcitity;
    }

    @Override
    public void login(String userName, String passWord) {
        //重新包装环信回调方法，让逻辑代码更清晰。
        EMClient.getInstance().login(userName, passWord, new ZanCallBack() {
            @Override
            public void onMainSuccess() {
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();
                Log.d("main", "登录聊天服务器成功！");
                //登陆成功直接回调
                loginActivity.onLogin(true, "", userName, passWord);
            }

            @Override
            public void onMainError(String s) {
                loginActivity.onLogin(false, s, userName, passWord);
            }


        });
    }
}
