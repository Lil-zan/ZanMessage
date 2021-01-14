package com.java.zanmessage.presenter;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.java.zanmessage.view.activity.ChatInterface;
import com.java.zanmessage.view.adapter.ZanCallBack;

import java.util.ArrayList;
import java.util.List;

public class ChatPresenter implements IntfChatPresenter {
    private ChatInterface chatInterface;
    //存放历史消息对象的集合
    private List<EMMessage> mMessages = new ArrayList<>();
    private EMConversation conversation;

    public ChatPresenter(ChatInterface chatInterface) {
        this.chatInterface = chatInterface;
    }

    //获取环信SDK存放在本地数据库的历史聊天数据
    @Override
    public void initChatData(String username) {
        conversation = EMClient.getInstance().chatManager().getConversation(username);
        mMessages.clear();
        if (conversation != null) {
            //获取最后一条消息的对象
            EMMessage lastMessage = conversation.getLastMessage();
            //根据最后一条消息的id，往前获取19条历史消息不包含最后一条
            List<EMMessage> emMessages = conversation.loadMoreMsgFromDB(lastMessage.getMsgId(), 19);
            mMessages.clear();
            //除最后一条外的19条历史消息存放到集合
            mMessages.addAll(emMessages);
            //再把最后一条历史消息存放到集合里
            mMessages.add(lastMessage);
            //此处把已读的消息标记为已读。
            conversation.markAllMessagesAsRead();
        }

        chatInterface.onInitChatData(mMessages);
    }

    //收到消息之后的逻辑。
    @Override
    public void addMessage(EMMessage emMessage) {
        //新添加进来的消息也要标记为已读
        conversation.markMessageAsRead(emMessage.getMsgId());
        //这行代码只是在本地信息中标记已读。
        //emMessage.setUnread(false);
        mMessages.add(emMessage);
        chatInterface.onUpData();
    }

    @Override
    public void sendMessage(String mUsername, String msg) {
        //创建一条文本消息，content为消息文字内容，toChatUsername为对方用户或者群聊的id，后文皆是如此
        EMMessage message = EMMessage.createTxtSendMessage(msg, mUsername);
        //如果是群聊，设置chattype，默认是单聊
        //if (chatType == CHATTYPE_GROUP)
        //    message.setChatType(ChatType.GroupChat);
        //发送消息
        message.setMessageStatusCallback(new ZanCallBack() {
            @Override
            public void onMainSuccess() {
                chatInterface.onUpData();
            }

            @Override
            public void onMainError(String s) {
                chatInterface.onUpData();
            }
        });
        EMClient.getInstance().chatManager().sendMessage(message);
        mMessages.add(message);
        chatInterface.onUpData();
    }
}
