package com.java.zanmessage.view.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.BarUtils;
import com.java.zanmessage.R;
import com.java.zanmessage.presenter.AddFriendPresenter;
import com.java.zanmessage.presenter.IntfAddFriendPresenter;
import com.java.zanmessage.view.adapter.AddFriendAdapter;

import java.util.List;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cn.leancloud.AVUser;

public class AddFriendActivity extends BaseActivity implements SearchView.OnQueryTextListener, AddFriendInterface, AddFriendAdapter.OnAddClickListener {

    private ConstraintLayout myLayout;
    private Toolbar toolbar;
    private TextView toolbarText;
    private ImageView nodataImage;
    private RecyclerView recycler;
    private SearchView mSearchView;
    private IntfAddFriendPresenter addFriendPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        //初始化控件
        ininViwe();
        //初始化状态栏以及导航栏
        initStatusBar();
        addFriendPresenter = new AddFriendPresenter(this);
    }

    private void ininViwe() {
        myLayout = (ConstraintLayout) findViewById(R.id.layout_my);
        toolbar = (Toolbar) findViewById(R.id.search_toolbar);
        toolbarText = (TextView) findViewById(R.id.toolbar_text);
        nodataImage = (ImageView) findViewById(R.id.nodata);
        recycler = (RecyclerView) findViewById(R.id.search_recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        //显示导航键
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //给导航键点击监听
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //状态栏与导航栏设置
    private void initStatusBar() {
        BarUtils.setStatusBarColor(this, getResources().getColor(R.color.mianColor));
        BarUtils.setStatusBarLightMode(this, true);
        BarUtils.addMarginTopEqualStatusBarHeight(myLayout);
        BarUtils.setNavBarVisibility(this, false);
    }

    //toolbar创建菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_friend_menu, menu);
        //初始化searchView
        MenuItem item = menu.findItem(R.id.search_friend);
        //获取到子条目中的控件
        mSearchView = (SearchView) item.getActionView();
        //给searchView添加hint提示
        mSearchView.setQueryHint("用户名(支持模糊匹配)");
        mSearchView.setOnQueryTextListener(this);
        return true;
    }

    //使用提交搜索
    @Override
    public boolean onQueryTextSubmit(String keyword) {
        showWaitDialog("正在搜索" + keyword + "...");
        addFriendPresenter.searchContact(keyword);
        //清除焦点,收起软键盘
        mSearchView.clearFocus();
        return true;
    }

    //不使用即时搜索
    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    //搜索结果回调
    @Override
    public void onSearchContact(boolean isSuccess, List<AVUser> avUsers, List<String> contacts) {
        hideWaitDialog();
        if (isSuccess) {
            if (avUsers != null && avUsers.size() > 0) {
                nodataImage.setVisibility(View.GONE);
                recycler.setVisibility(View.VISIBLE);
                AddFriendAdapter addAdapter = new AddFriendAdapter(avUsers, contacts);
                recycler.setAdapter(addAdapter);
                addAdapter.setOnAddClickListener(this);
            } else {
                nodataImage.setVisibility(View.VISIBLE);
                recycler.setVisibility(View.GONE);
            }
        } else {
            nodataImage.setVisibility(View.VISIBLE);
            recycler.setVisibility(View.GONE);
        }
    }

    //添加好友完成回调
    @Override
    public void onAddContacted(boolean isSuccess, String errorMsg, String usernam) {

        if (isSuccess) {
            Toast.makeText(this, "添加好友请求成功", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "添加好友请求失败:" + errorMsg, Toast.LENGTH_LONG).show();
        }
    }

    //点击按钮接口回调
    //测试没问题
    @Override
    public void addClick(String user, String addContent) {
        addFriendPresenter.addContact(user, addContent);
    }
}