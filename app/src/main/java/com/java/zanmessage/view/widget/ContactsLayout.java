package com.java.zanmessage.view.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.java.zanmessage.R;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class ContactsLayout extends RelativeLayout {

    private RecyclerView mRecycler;
    private TextView showText;
    private SlideBar slide;
    private SwipeRefreshLayout refreshLayout;

    public ContactsLayout(Context context) {
        this(context, null);
    }


    public ContactsLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs);
    }

    public ContactsLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this(context, attrs);
    }

    public ContactsLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.contacts_layout, this, true);
        initView();
        initRefreshView();
    }


    private void initView() {
        mRecycler = (RecyclerView) findViewById(R.id.contact_recycler);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        showText = (TextView) findViewById(R.id.show_text);
        slide = (SlideBar) findViewById(R.id.slide_bar);
    }

    private void initRefreshView() {
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.mainGray));
    }

    public void onRefreshListener(SwipeRefreshLayout.OnRefreshListener mRefreshListener) {
        refreshLayout.setOnRefreshListener(mRefreshListener);
    }

    public boolean isRefreshing() {
        return refreshLayout.isRefreshing();
    }

    public void setRefreshing(boolean refreshing) {
        refreshLayout.setRefreshing(refreshing);
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecycler.setAdapter(adapter);
    }
}
