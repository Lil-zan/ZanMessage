package com.java.zanmessage.view.fragment;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.java.zanmessage.presenter.IntfMsgPresenter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;


public class MsgPresenter implements IntfMsgPresenter {
    private MsgInterface msgView;
    private List<EMConversation> mConversations = new ArrayList<>();


    public MsgPresenter(MsgInterface msgView) {
        this.msgView = msgView;
    }

    //初始化聊天
    @Override
    public void initConversation() {
        //获取所有回话
        //返回值是username和消息对象的Map
        Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
        //消息对象中可获取到username，所以没必要整个Map
        Collection<EMConversation> values = conversations.values();
        mConversations.clear();
        mConversations.addAll(values);
        //展示时是根据消息发送或者接收时间进行倒序排序
        Collections.sort(mConversations, new Comparator<EMConversation>() {
            @Override
            public int compare(EMConversation o1, EMConversation o2) {
                return (int) (o2.getLastMessage().getMsgTime() - o1.getLastMessage().getMsgTime());
            }
        });
        msgView.oninitConversation(mConversations);
    }
}
