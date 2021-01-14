package com.java.zanmessage.view.activity;

import java.util.List;

import cn.leancloud.AVUser;

public interface AddFriendInterface {
    void onSearchContact(boolean isSuccess, List<AVUser> avUsers, List<String> contacts);

    void onAddContacted(boolean isSuccess, String errorMsg, String usernam);
}
