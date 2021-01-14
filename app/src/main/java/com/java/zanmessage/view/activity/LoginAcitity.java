package com.java.zanmessage.view.activity;

import android.Manifest;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.BarUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.java.zanmessage.R;
import com.java.zanmessage.presenter.LoginPresenter;
import com.java.zanmessage.utils.StringUtils;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

public class LoginAcitity extends BaseActivity implements View.OnClickListener, LoginInterface, TextView.OnEditorActionListener {

    private ImageView headImage;
    private TextInputEditText userText;
    private TextInputEditText psdText;
    private Button loginButton;
    private TextView newRegist;
    private LoginPresenter loginPresenter;
    private String userName;
    private String passWord;
    private ConstraintLayout myLayout;
    private int REQUESTCODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_acitity);
        headImage = (ImageView) findViewById(R.id.head);
        userText = (TextInputEditText) findViewById(R.id.user);
        psdText = (TextInputEditText) findViewById(R.id.pasword);
        loginButton = (Button) findViewById(R.id.login);
        myLayout = (ConstraintLayout) findViewById(R.id.layout_my);
        newRegist = (TextView) findViewById(R.id.regist);

        headImage.setOnClickListener(this);
        userText.setOnClickListener(this);
        psdText.setOnClickListener(this);
        loginButton.setOnClickListener(this);
        newRegist.setOnClickListener(this);
        //创建p层
        loginPresenter = new LoginPresenter(this);
//        immersion();
        BarUtils.setStatusBarColor(this, getResources().getColor(R.color.backgroudGray));
        BarUtils.setStatusBarLightMode(this, false);
        BarUtils.addMarginTopEqualStatusBarHeight(myLayout);
        BarUtils.setNavBarVisibility(this, false);
    }


    //重新new此Activity时会重新走此方法而不是onCreate
//    @Override
////    protected void onNewIntent(Intent intent) {
////        super.onNewIntent(intent);
////    }

    @Override
    protected void onStart() {
        super.onStart();
        initUser();
    }

    //当用户注册返回登陆页面时的数据回显。
    private void initUser() {
        userName = getUserName();
        passWord = getPassWord();
        userText.setText(userName);
        psdText.setText(passWord);

        psdText.setOnEditorActionListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //点击头像
            case R.id.head:
                break;
            case R.id.user:
                break;
            case R.id.pasword:
                break;
            case R.id.login:
                login();
                break;
            //新用户
            case R.id.regist:
                //跳转注册界面
                mStartActivity(RegistActivity.class, false);
                break;
            default:
                break;
        }
    }


    private void login() {
        userName = userText.getText().toString().trim();
        passWord = psdText.getText().toString().trim();

        if (!StringUtils.isMatch(userName, "^[a-zA-Z]\\w{2,9}$")) {
            Toast.makeText(this, "用户名不合法", Toast.LENGTH_LONG).show();
            //用户名不合法，光标焦点回到输入框右侧
            userText.requestFocus(View.FOCUS_RIGHT);
            return;
        } else if (!StringUtils.isMatch(passWord, "^\\d{3,29}$")) {
            Toast.makeText(this, "密码格式不正确", Toast.LENGTH_LONG).show();
            //密码不合法，光标焦点回到输入框右侧
            psdText.requestFocus(View.FOCUS_RIGHT);
            return;
        }
        //由于环信获取读写数据库权限报错，登陆前动态获取sd卡权限
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUESTCODE);
            return;
        }else{
            //登陆业务逻辑交给presenter层处理
            loginPresenter.login(userName, passWord);
            showWaitDialog("正在登陆...");
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUESTCODE) {
            if (grantResults[0] == PermissionChecker.PERMISSION_GRANTED) {
                //登陆业务逻辑交给presenter层处理
                loginPresenter.login(userName, passWord);
                showWaitDialog("正在登陆...");
            } else {
                Toast.makeText(this, "没有获取读写SD卡权限，部分功能无法使用", Toast.LENGTH_LONG).show();
            }
        }
    }

    //登陆回调
    @Override
    public void onLogin(boolean isSuccess, String msg, String username, String password) {
        //隐藏对话框
        hideWaitDialog();
        if (isSuccess) {
            saveUser(username, password);
            mStartActivity(MainActivity.class, true);
        } else {
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        }

    }

    //密码输入框对回车键监听。
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (v.getId() == psdText.getId()) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                login();
                //记得消化掉事件。
                return true;
            }
        }
        return false;
    }
}