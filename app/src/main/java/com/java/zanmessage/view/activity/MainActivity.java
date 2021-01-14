package com.java.zanmessage.view.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.ashokvarma.bottomnavigation.TextBadgeItem;
import com.blankj.utilcode.util.BarUtils;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.java.zanmessage.R;
import com.java.zanmessage.utils.FragmentFactory;
import com.java.zanmessage.view.fragment.BaseFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends BaseActivity {

    private Toolbar mtoolBar;
    private TextView barText;
    private FrameLayout mFrLayout;
    private BottomNavigationBar bottomBar;
    private String[] titleText = {"消息", "联系人", "动态"};
    private ConstraintLayout layout1;
    private TextBadgeItem textBadgeItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        //初始化Fragment
        initFragment();
        //初始化bottomNavigationBar
        initBottomNavigation();
        //修改状态栏的设置
        BarUtils.setStatusBarColor(this, getResources().getColor(R.color.mianColor));
        BarUtils.setStatusBarLightMode(this, true);
        BarUtils.addMarginTopEqualStatusBarHeight(layout1);
        BarUtils.setNavBarVisibility(this, false);

        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EMMessage emMessage) {
        upDataUnReadCount();
    }

    private void upDataUnReadCount() {
        //环信提供了未读消息总数量，这里直接获取
        int unreadMessageCount = EMClient.getInstance().chatManager().getUnreadMessageCount();
        if (unreadMessageCount == 0) {
            textBadgeItem.hide(true);
        } else if (unreadMessageCount > 99) {
            textBadgeItem.show(true);
            textBadgeItem.setText("...");
        } else {
            textBadgeItem.show(true);
            textBadgeItem.setText(unreadMessageCount + "");
        }
    }

    //在进入chatactivity返回之后。消息已是已读状态，需要更新界面中的未读消息badges
    @Override
    protected void onResume() {
        super.onResume();
        upDataUnReadCount();
    }

    private void initFragment() {
        //开发时热部署或者，应用程序出现bug闪退之后，activity可能会存在保存状态。从而导致之前启动的fragment与新建的fragment同时显示产生重影
        //解决重影方案:判断当前activity是否有老的fragment，有则移除;
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        //遍历fragment相应长度标题数组
        for (int i = 0; i < titleText.length; i++) {
            //获取相应fragment的tag标签
            Fragment fragmentByTag = supportFragmentManager.findFragmentByTag("" + i);
            //判断该标签的fragment不为空的话，则移除
            if (fragmentByTag != null)
                supportFragmentManager.beginTransaction().remove(fragmentByTag).commit();
        }

        BaseFragment fragment = FragmentFactory.getFragment(0);
        getSupportFragmentManager().beginTransaction().add(R.id.frame_layout, fragment, "0").show(fragment).commit();
    }

    private void initBottomNavigation() {
        BottomNavigationItem msgItem = new BottomNavigationItem(R.mipmap.ic_launcher, "消息");
        BottomNavigationItem contactItem = new BottomNavigationItem(R.mipmap.ic_launcher, "联系人");
        BottomNavigationItem otherItem = new BottomNavigationItem(R.mipmap.ic_launcher, "动态");

        //给msgitem设置badges信息数量角标
        textBadgeItem = new TextBadgeItem()
                .setBorderWidth(2)
                .setBackgroundColorResource(R.color.badgesRed)
                .setHideOnSelect(false);//TODO 是否点击隐藏badges,目前先不隐藏吧
        msgItem.setBadgeItem(textBadgeItem);
        //给badges设置是否有未读消息数量
        upDataUnReadCount();

        bottomBar.addItem(msgItem)
                .addItem(contactItem)
                .addItem(otherItem)
                .setFirstSelectedPosition(0)
                .setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(int position) {
                        barText.setText(titleText[position]);
                        BaseFragment fragment = FragmentFactory.getFragment(position);
                        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        //判断如果没有添加过的fragment就先添加
                        if (!fragment.isAdded())
                            fragmentTransaction.add(R.id.frame_layout, fragment, position + "");
                        //添加过的fragment直接show
                        fragmentTransaction.show(fragment).commit();
                    }

                    @Override
                    public void onTabUnselected(int position) {
                        getSupportFragmentManager().beginTransaction().hide(FragmentFactory.getFragment(position)).commit();
                    }

                    @Override
                    public void onTabReselected(int position) {

                    }
                })
                .initialise();
    }

    //初始化控件
    private void initView() {
        mtoolBar = findViewById(R.id.toolbar);
        barText = (TextView) findViewById(R.id.bar_text);
        mFrLayout = (FrameLayout) findViewById(R.id.frame_layout);
        bottomBar = (BottomNavigationBar) findViewById(R.id.bottom_bar);
        layout1 = (ConstraintLayout) findViewById(R.id.layout_1);
        mtoolBar.setTitle("");
        barText.setText(titleText[0]);
        setSupportActionBar(mtoolBar);
    }

    //重写菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //给菜单按钮添加布局文件
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //显示图标
        MenuBuilder menuBuilder = (MenuBuilder) menu;
        menuBuilder.setOptionalIconsVisible(true);
        return true;
    }

    //菜单按钮点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_menu_add_friend:
                //添加好友
                mStartActivity(AddFriendActivity.class, false);
                break;
            case R.id.main_menu_share_friend:
                //分享好友

                break;
            case R.id.main_menu_about_me:
                //关于我

                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}