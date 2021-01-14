package com.java.zanmessage.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.java.zanmessage.R;
import com.java.zanmessage.view.activity.ChatActivity;
import com.java.zanmessage.view.adapter.ConversationAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MsgFragment extends BaseFragment implements MsgInterface, ConversationAdapter.OnConversationClickListener {

    private RecyclerView mRecyclerView;
    private MsgPresenter msgPresenter;
    private ConversationAdapter conversationAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_msg, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.conversation_recycler);
        msgPresenter = new MsgPresenter(this);
        //绑定EventBus订阅者
        EventBus.getDefault().register(this);
        msgPresenter.initConversation();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EMMessage emMessage) {
        //重新更新一次会话就好
        msgPresenter.initConversation();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void oninitConversation(List<EMConversation> mConversations) {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        conversationAdapter = new ConversationAdapter(mConversations);
        conversationAdapter.setOnConversationClickListener(this);
        mRecyclerView.setAdapter(conversationAdapter);

    }

    @Override
    public void onConversationClick(EMConversation emConversation) {
        String username = emConversation.getLastMessage().getUserName();
        Intent intent = new Intent(getContext(), ChatActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }

    //chatactivity返回之后。不仅activity要通知更新界面,fragment也需要更新一下
    @Override
    public void onResume() {
        super.onResume();
        msgPresenter.initConversation();
    }
}