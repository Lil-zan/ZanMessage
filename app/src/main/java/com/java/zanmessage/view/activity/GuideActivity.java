package com.java.zanmessage.view.activity;


import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.blankj.utilcode.util.BarUtils;
import com.java.zanmessage.R;
import com.java.zanmessage.presenter.GuidePresenter;
import com.java.zanmessage.presenter.IntfGuideIPresenter;
import com.java.zanmessage.view.adapter.GuideAnimatorAdapter;

public class GuideActivity extends BaseActivity implements GuideInterface {


    private ImageView launcher_iv;
    private ObjectAnimator objectAnimator;
    private IntfGuideIPresenter guideIPresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        launcher_iv = (ImageView) findViewById(R.id.launcher_iv);
        //启动时登陆判断
        initLogin();
        WindowManager.LayoutParams lp = getWindow().getAttributes();

        //全屏设置
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//        lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER;
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
//        lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT;
        }else {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        getWindow().setAttributes(lp);
        BarUtils.setStatusBarColor(this,getResources().getColor(R.color.white));
        BarUtils.setStatusBarLightMode(this,true);

    }

    private void initLogin() {
        guideIPresenter = new GuidePresenter(this);
        //把业务逻辑放置presenter层处理
        guideIPresenter.checkLogin();
    }


    @Override
    public void onCheckLogin(boolean isLogin) {
        //启动页动画
        objectAnimator = objectAnimator.ofFloat(launcher_iv, "alpha", 1, 0);
        objectAnimator.setDuration(2000);
        objectAnimator.addListener(new GuideAnimatorAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (isLogin) {
                    mStartActivity(MainActivity.class, true);
//                    mStartActivity(LoginAcitity.class, true);
                } else {
                    mStartActivity(LoginAcitity.class, true);
                }
            }
        });
        objectAnimator.start();

    }
}