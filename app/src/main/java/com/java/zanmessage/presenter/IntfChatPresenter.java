package com.java.zanmessage.presenter;

import com.hyphenate.chat.EMMessage;

public interface IntfChatPresenter {
    void initChatData(String username);
    void addMessage(EMMessage emMessage);

    void sendMessage(String mUsername, String msg);
}
