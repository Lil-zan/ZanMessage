package com.java.zanmessage.view.activity;

import com.hyphenate.chat.EMMessage;

import java.util.List;

public interface ChatInterface {
    void onInitChatData(List<EMMessage> mMessages);

    void onUpData();
}
