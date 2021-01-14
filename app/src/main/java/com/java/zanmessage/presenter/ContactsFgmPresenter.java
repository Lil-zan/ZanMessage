package com.java.zanmessage.presenter;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.java.zanmessage.utils.DBUtils;
import com.java.zanmessage.utils.ThreadUtils;
import com.java.zanmessage.view.fragment.ContactsInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

public class ContactsFgmPresenter implements IntfContactsFgmPresenter {
    private ContactsInterface contactsFragment;
    private List<String> mContacts = new ArrayList<>();

    public ContactsFgmPresenter(ContactsInterface contactsFragment) {
        this.contactsFragment = contactsFragment;
    }

    @Override
    public void getContacts() {
        //先在本地获取缓存
        final String currentUser = EMClient.getInstance().getCurrentUser();
        List<String> contactsFromDB = DBUtils.getContactFromDB(currentUser);
        //该方法被调用两次时，会重复加载数据，因此需要在添加数据前清空集合。
        mContacts.clear();
        mContacts.addAll(contactsFromDB);
        //把缓存数据返回给view显示
        contactsFragment.initContact(mContacts);
        //网络获取最新通讯录
        upDateFromServer(currentUser);
    }

    @Override
    public void upDataContact() {
        upDateFromServer(EMClient.getInstance().getCurrentUser());
    }


    //删除指定联系人
    @Override
    public void deleteContact(String contact) {
        //子线程操作请求网络
        ThreadUtils.runOnSubThread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().contactManager().deleteContact(contact);
                    //回到主线程回调
                    ThreadUtils.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            //删除完后回调
                            contactsFragment.onDeleteContact(true, null, contact);
                        }
                    });

                } catch (HyphenateException e) {
                    e.printStackTrace();
                    //返回主线程提示删除失败
                    ThreadUtils.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            contactsFragment.onDeleteContact(true, e.getMessage(), contact);
                        }
                    });

                }
            }
        });

    }

    private void upDateFromServer(final String currentUser) {
        ThreadUtils.runOnSubThread(new Runnable() {
            @Override
            public void run() {
                //让环信网络请求放置在子线程执行
                try {
                    List<String> EMCcontacts = EMClient.getInstance().contactManager().getAllContactsFromServer();
                    //出现联系人重复bug，无法重现，只能在此处做去重处理
                    HashSet<String> hashSet = new HashSet<String>(EMCcontacts);
                    List<String> newContacts = new ArrayList<>(hashSet);
                    //给联系人排序
                    Collections.sort(newContacts, new Comparator<String>() {
                        @Override
                        public int compare(String o1, String o2) {
                            return o1.compareToIgnoreCase(o2);
                        }
                    });
                    //把网络获取的最新通讯录缓存到本地
                    DBUtils.updateContact(currentUser, newContacts);

                    //把最新数据返回给view层展示
                    mContacts.clear();
                    mContacts.addAll(newContacts);
                    //子线程中不能更新ui数据,记得返回主线程中执行刷新数据
                    ThreadUtils.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            //通过接口回调通知recyclerView刷新数据
                            contactsFragment.upDataContacts();
                        }
                    });

                } catch (HyphenateException e) {
                    e.printStackTrace();
                    contactsFragment.upDataFailure();
                }
            }
        });
    }
}
