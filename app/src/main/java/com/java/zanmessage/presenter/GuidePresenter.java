package com.java.zanmessage.presenter;

import com.hyphenate.chat.EMClient;
import com.java.zanmessage.view.activity.GuideActivity;
import com.java.zanmessage.view.activity.GuideInterface;

public class GuidePresenter implements IntfGuideIPresenter {
    private GuideInterface guideActivity;

    public GuidePresenter(GuideActivity guideActivity) {
        this.guideActivity = guideActivity;

    }

    @Override
    public void checkLogin() {
        if (EMClient.getInstance().isLoggedInBefore() && EMClient.getInstance().isConnected()) {
            guideActivity.onCheckLogin(true);
        } else {
            guideActivity.onCheckLogin(false);
        }

    }
}
