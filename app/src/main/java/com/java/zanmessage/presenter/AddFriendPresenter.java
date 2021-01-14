package com.java.zanmessage.presenter;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.java.zanmessage.utils.DBUtils;
import com.java.zanmessage.utils.ThreadUtils;
import com.java.zanmessage.view.activity.AddFriendInterface;

import java.util.List;

import cn.leancloud.AVQuery;
import cn.leancloud.AVUser;

public class AddFriendPresenter implements IntfAddFriendPresenter {
    private AddFriendInterface activity;

    public AddFriendPresenter(AddFriendInterface activity) {
        this.activity = activity;
    }

    //去leanCloud云数据库中搜索好友
    @Override
    public void searchContact(String keyword) {
        String currentUser = EMClient.getInstance().getCurrentUser();
        List<String> contacts = DBUtils.getContactFromDB(currentUser);
        ThreadUtils.runOnSubThread(new Runnable() {
            @Override
            public void run() {
                AVQuery<AVUser> userQuery = AVUser.getQuery();
                userQuery.whereContains("username", keyword);
                userQuery.whereNotEqualTo("username", currentUser);
                List<AVUser> avUsers = userQuery.find();
                ThreadUtils.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        if (avUsers != null && avUsers.size() > 0) {
                            activity.onSearchContact(true, avUsers, contacts);
                        } else {
                            activity.onSearchContact(false, null, null);
                        }
                    }
                });

            }
        });
    }

    //添加好友
    @Override
    public void addContact(String user, String addContent) {
        ThreadUtils.runOnSubThread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().contactManager().addContact(user, "");
                    ThreadUtils.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            activity.onAddContacted(true, null, user);
                        }
                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    ThreadUtils.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            activity.onAddContacted(false, e.getMessage(), user);
                        }
                    });
                }
            }
        });

    }
}
