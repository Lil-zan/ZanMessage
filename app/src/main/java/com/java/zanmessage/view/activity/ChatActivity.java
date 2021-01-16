package com.java.zanmessage.view.activity;

import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.BarUtils;
import com.hyphenate.chat.EMMessage;
import com.java.zanmessage.R;
import com.java.zanmessage.presenter.ChatPresenter;
import com.java.zanmessage.utils.Contant;
import com.java.zanmessage.view.adapter.ChatAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ChatActivity extends BaseActivity implements View.OnClickListener, ChatInterface, TextWatcher {

    private LinearLayout mLayout;
    private Toolbar mToolBar;
    private TextView tbText;
    private RecyclerView mRecyclerView;
    private LinearLayout msgLayout;
    private EditText msgEdit;
    private Button sendButton;
    private String mUsername;
    private ChatPresenter chatPresenter;
    private ChatAdapter chatAdapter;
    private NotificationManager mNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        //初始化控件
        initView();
        //初始化本地数据
        initData();
        //注册EventBus的订阅者,注册同时在onDestroy注销掉。防止遗漏。
        EventBus.getDefault().register(this);

        //修改状态栏的设置
        BarUtils.setStatusBarColor(this, getResources().getColor(R.color.beigeGray));
        BarUtils.setStatusBarLightMode(this, true);
        BarUtils.addMarginTopEqualStatusBarHeight(mLayout);
        BarUtils.setNavBarVisibility(this, false);//这句代码有问题。当软键盘弹出后底部导航栏会一直出现

        mNotification = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotification.cancel(Contant.MSG_NOTIFICATION_ID);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    //接收EventBus传递的数据
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EMMessage emMessage) {
        //获取信息来源
        String from = emMessage.getFrom();
        //判断如果消息来源是当前聊天对象
        if (from.equals(mUsername)) {
            //则把消息添加到当前聊天对象的消息队列中。
            chatPresenter.addMessage(emMessage);
        }
    }

    private void initView() {
        mLayout = (LinearLayout) findViewById(R.id.my_layout);
        mToolBar = (Toolbar) findViewById(R.id.chat_toolbar);
        tbText = (TextView) findViewById(R.id.chat_toolbar_text);
        mRecyclerView = (RecyclerView) findViewById(R.id.chat_recycler);
        msgLayout = (LinearLayout) findViewById(R.id.chat_msg_layout);
        msgEdit = (EditText) findViewById(R.id.chat_msg);
        sendButton = (Button) findViewById(R.id.chat_send);
        mToolBar.setTitle("");
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        sendButton.setOnClickListener(this);
        //监听EditText状态是否改变
        msgEdit.addTextChangedListener(this);
    }

    private void initData() {
        Intent intent = getIntent();
        mUsername = intent.getStringExtra(Contant.MY_USERNAME);
        if (TextUtils.isEmpty(mUsername)) {
            finish();
            return;
        }
        tbText.setText(mUsername);

        //历史数据展示
        chatPresenter = new ChatPresenter(this);
        //把业务交给逻辑层处理
        chatPresenter.initChatData(mUsername);
        if (TextUtils.isEmpty(msgEdit.getText().toString().trim())) {
            sendButton.setEnabled(false);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chat_send:
                //TODO 发送按钮
                sendMsg();
                break;
        }

    }

    private void sendMsg() {
        String msg = msgEdit.getText().toString().trim();
        if (TextUtils.isEmpty(msg)) {
            Toast.makeText(this, "不能发送空内容", Toast.LENGTH_SHORT).show();
            return;
        }
        msgEdit.getText().clear();
        chatPresenter.sendMessage(mUsername, msg);
    }

    //逻辑层整理完数据，回调给ui层进行展示
    @Override
    public void onInitChatData(List<EMMessage> mMessages) {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatAdapter = new ChatAdapter(mMessages);
        mRecyclerView.setAdapter(chatAdapter);
        //历史聊天信息更新后记得滑动显示最后一条。
        mRecyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
    }

    //更新回调
    @Override
    public void onUpData() {
        chatAdapter.notifyDataSetChanged();
        //数据更新，别忘记消息滑动到最后一条
        mRecyclerView.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
    }


    //监听EditText状态
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String msg = s.toString().trim();
        if (TextUtils.isEmpty(msg)) {
            sendButton.setEnabled(false);
        } else {
            sendButton.setEnabled(true);
        }
    }
}