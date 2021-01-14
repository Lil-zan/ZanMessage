package com.java.zanmessage.view.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hyphenate.util.DensityUtil;
import com.java.zanmessage.R;
import com.java.zanmessage.utils.StringUtils;
import com.java.zanmessage.view.adapter.ContactsAdapter;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class SlideBar extends View {
    private static final String[] SECTIONS = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#"};
    private int mX = 0;
    private int mY = 0;
    private Paint mPaint;
    private RecyclerView mRecyclerView;
    private TextView mFloatText;

    public SlideBar(Context context) {
        this(context, null);
    }

    public SlideBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs);
    }

    public SlideBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this(context, attrs);
    }

    public SlideBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        //Paint.ANTI_ALIAS_FLAG属性抗锯齿
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.parseColor("#ffc900"));
        //环信工具类里把像素转换成dp
        int sp2px = DensityUtil.sp2px(context, 10);
        mPaint.setFakeBoldText(true);
        mPaint.setTextSize(sp2px);
        //把文字设置在空间居中
        mPaint.setTextAlign(Paint.Align.CENTER);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //获取自身的宽度
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
        mX = measuredWidth >> 1;
        mY = measuredHeight / SECTIONS.length;


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //绘制字母
        for (int i = 0; i < SECTIONS.length; i++) {
            canvas.drawText(SECTIONS[i], mX, mY + mY * i, mPaint);
        }

    }

    //监听触摸事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                setBackgroundResource(R.drawable.slidebar_bg);
                setFloatViewScroll(event.getY());
                break;
            case MotionEvent.ACTION_UP:
                setBackgroundColor(Color.TRANSPARENT);
                if (mFloatText != null) mFloatText.setVisibility(GONE);
                break;

        }
        return true;
    }

    private void setFloatViewScroll(float y) {
        //由于在触摸事件中触发，不需要每次都消耗内存findViewById，做空判断。
        if (mFloatText == null && mRecyclerView == null) {
            //此处需要获取到父控件
            ViewGroup parent = (ViewGroup) getParent();//目前没有想到更好的拓展方法。只能强转ViewGroup写死。
            //强转父控件后可findViewById找到需要用到的子控件.
            mRecyclerView = parent.findViewById(R.id.contact_recycler);//注意对比子控件id。
            mFloatText = parent.findViewById(R.id.show_text);
        }
        mFloatText.setVisibility(VISIBLE);
        //根据触摸高度，计算字母索引
        int index = (int) (y / mY);
        //触摸控件外部出现越界异常，添加指针限定范围判断。
        if (index < 0) {
            index = 0;
        } else if (index > SECTIONS.length - 1) {
            index = SECTIONS.length - 1;
        }
        String section = SECTIONS[index];
        mFloatText.setText(section);

        //recyclerView根据触摸字母做出滑动操作。

        ContactsAdapter adapter = (ContactsAdapter) mRecyclerView.getAdapter();

        List<String> contacts = adapter.getContacts();
        for (int i = 0; i < contacts.size(); i++) {
            if (StringUtils.getInitial(contacts.get(i)).equalsIgnoreCase(section)) {
                mRecyclerView.smoothScrollToPosition(i);
                return;
            }
        }

    }

}