package com.java.zanmessage.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.java.zanmessage.R;
import com.java.zanmessage.event.ContactChangeEvent;
import com.java.zanmessage.presenter.ContactsFgmPresenter;
import com.java.zanmessage.presenter.IntfContactsFgmPresenter;
import com.java.zanmessage.utils.Contant;
import com.java.zanmessage.view.activity.ChatActivity;
import com.java.zanmessage.view.adapter.ContactsAdapter;
import com.java.zanmessage.view.widget.ContactsLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class ContactsFragment extends BaseFragment implements ContactsInterface, SwipeRefreshLayout.OnRefreshListener, ContactsAdapter.onContactsItemClickListener {


    private ContactsLayout mContactsLayout;
    private ContactsAdapter adapter;
    private IntfContactsFgmPresenter contactsFgmPresenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mContactsLayout = (ContactsLayout) view;
        //与逻辑层抱环
        contactsFgmPresenter = new ContactsFgmPresenter(this);
        //初始化好友列表
        contactsFgmPresenter.getContacts();
        //下拉刷新
        mContactsLayout.onRefreshListener(this);
        //注册观察者/订阅者subscribe
        EventBus.getDefault().register(this);
    }

    //监听添加好友或者删除好友时更新数据
    @Subscribe(threadMode = ThreadMode.MAIN)  //回调到主线程
    public void onContactChange(ContactChangeEvent event) {
        contactsFgmPresenter.upDataContact();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //销毁fragment时要注销EventBus防止内存泄漏
        EventBus.getDefault().unregister(this);
        //静态fragment问题一大堆。
        mContactsLayout = null;
        adapter = null;
    }

    @Override
    public void initContact(List<String> contacts) {
        //展示联系人数据
        adapter = new ContactsAdapter(contacts);
        mContactsLayout.setAdapter(adapter);
        //给adapter绑定接口回调
        adapter.setOnContactsItemClickListener(this);
    }

    //网络请求联系人成功
    @Override
    public void upDataContacts() {
        adapter.notifyDataSetChanged();
        if (mContactsLayout.isRefreshing()) {
            mContactsLayout.setRefreshing(false);
            Toast.makeText(getContext(), "联系人更新成功", Toast.LENGTH_SHORT).show();
        }
    }

    //数据更新失败
    @Override
    public void upDataFailure() {
        if (mContactsLayout.isRefreshing()) {
            mContactsLayout.setRefreshing(false);
            Toast.makeText(getContext(), "联系人更新失败", Toast.LENGTH_LONG).show();
        }
    }

    //删除好友之后的回调
    @Override
    public void onDeleteContact(boolean isSuccess, String error, String contact) {
        if (isSuccess) {
            contactsFgmPresenter.upDataContact();
            Snackbar.make(mContactsLayout, "删除好友" + contact + "成功", Snackbar.LENGTH_LONG)
                    .setTextColor(getResources().getColor(R.color.black))
                    .setBackgroundTint(getResources().getColor(R.color.mianColor))
                    .show();
        } else {
            if (TextUtils.isEmpty(error)) {
                Toast.makeText(this.getContext(), error, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this.getContext(), "删除好友:" + contact + "失败", Toast.LENGTH_LONG).show();
            }

        }
    }

    @Override
    public void onRefresh() {
        //更新通讯录
        contactsFgmPresenter.upDataContact();
    }


    //adapter子条目点击回调
    @Override
    public void onContactClick(String contact) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), ChatActivity.class);
        intent.putExtra(Contant.MY_USERNAME, contact);
        startActivity(intent);
    }

    //adapter子条目长按回调
    @Override
    public void onContactLongClick(String contact) {
        Snackbar.make(mContactsLayout, "删除好友:" + contact + "?", Snackbar.LENGTH_LONG).setAction("确定", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactsFgmPresenter.deleteContact(contact);
            }
        })
                .setBackgroundTint(getResources().getColor(R.color.mianColor))
                .setTextColor(getResources().getColor(R.color.black))
                .setActionTextColor(getResources().getColor(R.color.black))
                .show();
    }
}