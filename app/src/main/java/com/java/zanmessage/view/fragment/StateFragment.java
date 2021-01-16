package com.java.zanmessage.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.java.zanmessage.R;
import com.java.zanmessage.view.activity.LoginAcitity;
import com.java.zanmessage.view.activity.MainActivity;
import com.java.zanmessage.view.adapter.ZanCallBack;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class StateFragment extends BaseFragment implements View.OnClickListener {


    private ImageView head;
    private Button loginOut;
    private CalendarView calendView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_state, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //静态fragment中引用到的资源需要释放
        loginOut = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        head = (ImageView) view.findViewById(R.id.head);
        loginOut = (Button) view.findViewById(R.id.login_out);
        calendView = (CalendarView) view.findViewById(R.id.calendar_view);
        //从环信上获取用户名
        String currentUser = EMClient.getInstance().getCurrentUser();
        loginOut.setText("退（" + currentUser + "）出");
        loginOut.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        final MainActivity activity = (MainActivity) getActivity();
        switch (v.getId()) {
            case R.id.login_out:
                EMClient.getInstance().logout(true, new ZanCallBack() {
                    @Override
                    public void onMainSuccess() {
                        Toast.makeText(activity, "退出成功", Toast.LENGTH_LONG).show();
                        activity.mStartActivity(LoginAcitity.class, true);
                    }


                    @Override
                    public void onMainError(String s) {
                        //可离线时保存本地登陆状态，监听网络状态，在网络可用状态下重新执行退出登陆
                        Toast.makeText(activity, "退出失败", Toast.LENGTH_LONG).show();
                    }
                });
                break;
        }
    }
}