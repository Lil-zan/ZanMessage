package com.java.zanmessage.view.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.BarUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.java.zanmessage.R;
import com.java.zanmessage.presenter.RegistPresenter;
import com.java.zanmessage.utils.StringUtils;

import androidx.constraintlayout.widget.ConstraintLayout;

public class RegistActivity extends BaseActivity implements View.OnClickListener, TextView.OnEditorActionListener, RegistInterface {

    private TextInputEditText newUser;
    private TextInputEditText newPassword;
    private Button newRegist;
    private RegistPresenter registPresenter;
    private ConstraintLayout myLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        newUser = (TextInputEditText) findViewById(R.id.new_user);
        newPassword = (TextInputEditText) findViewById(R.id.new_pasword);
        newRegist = (Button) findViewById(R.id.new_regist);
        myLayout = (ConstraintLayout) findViewById(R.id.layout_my);

        newUser.setOnClickListener(this);
        newPassword.setOnClickListener(this);
        newRegist.setOnClickListener(this);
        registPresenter = new RegistPresenter(this);

        BarUtils.setStatusBarColor(this, getResources().getColor(R.color.backgroudGray));
        BarUtils.setStatusBarLightMode(this, false);
        BarUtils.addMarginTopEqualStatusBarHeight(myLayout);
        BarUtils.setNavBarVisibility(this,false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.new_user:
                break;
            case R.id.new_pasword:
                newPassword.setOnEditorActionListener(this);
                break;
            case R.id.new_regist:
                regist();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (v.getId() == newPassword.getId()) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                regist();
                return true;
            }
        }
        return false;
    }

    private void regist() {
        String userName = newUser.getText().toString().trim();
        String password = newPassword.getText().toString().trim();

        if (!StringUtils.isMatch(userName, "^[a-zA-Z]\\w{2,9}$")) {
            Toast.makeText(this, "用户名不合法", Toast.LENGTH_LONG).show();
            newUser.requestFocus(View.FOCUS_RIGHT);
            return;
        } else if (!StringUtils.isMatch(password, "^\\d{3,29}$")) {
            Toast.makeText(this, "密码格式不正确", Toast.LENGTH_LONG).show();
            newPassword.requestFocus(View.FOCUS_RIGHT);
            return;
        }
        //合法注册的username password
        registPresenter.regist(userName, password);
        showWaitDialog("正在注册...");

    }

    //注册逻辑回调
    @Override
    public void onRegist(boolean isSuccess, String alert, String userName, String passWord) {
        hideWaitDialog();
        if (isSuccess) {
            //baseActivity中sp保存用户名和密码。
            saveUser(userName, passWord);
            //注册成功返回到登陆页面。
            mStartActivity(LoginAcitity.class, true);
        } else {
            //失败给出提示。
            Toast.makeText(this, alert, Toast.LENGTH_LONG).show();
        }


    }
}