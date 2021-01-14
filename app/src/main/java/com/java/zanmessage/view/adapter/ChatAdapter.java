package com.java.zanmessage.view.adapter;

import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessageBody;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.util.DateUtils;
import com.java.zanmessage.R;

import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    private List<EMMessage> mMessages;

    public ChatAdapter(List<EMMessage> mMessages) {
        this.mMessages = mMessages;
    }

    @Override
    public int getItemViewType(int position) {
        //接收消息返回0，发送的消息返回1。
        EMMessage emMessage = mMessages.get(position);
        EMMessage.Direct direct = emMessage.direct();
        if (direct == EMMessage.Direct.RECEIVE) {
            return 0;
        } else {
            return 1;
        }
        /*if (position == 2 || position == 6 || position == 8) {
            return 0;
        } else {
            return 1;
        }*/
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == 0) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_receive_layout, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_send_layout, parent, false);
        }
        ChatViewHolder viewHolder = new ChatViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        EMMessage emMessage = mMessages.get(position);

        long msgTime = emMessage.getMsgTime();
        //设置消息时间显示
        holder.mDate.setText(DateUtils.getTimestampString(new Date(msgTime)));
        //第一条消息显示
        if (position == 0) {
            holder.mDate.setVisibility(View.VISIBLE);
        } else {
            //判断时间相隔
            EMMessage emMsg = mMessages.get(position - 1);
            long preTime = emMsg.getMsgTime();
            //环信提供的工具类判断信息时间是否衔接
            if (DateUtils.isCloseEnough(preTime, msgTime)) {
                holder.mDate.setVisibility(View.GONE);
            } else {
                holder.mDate.setVisibility(View.VISIBLE);
            }
        }

        //获取Body对象。body是存储消息的对象。
        EMMessageBody body = emMessage.getBody();
        if (body instanceof EMTextMessageBody) {
            EMTextMessageBody textBody = (EMTextMessageBody) body;
            String message = textBody.getMessage();
            holder.mContent.setText(message.trim());
        }

        //如果此消息是发出的消息
        if (emMessage.direct() == EMMessage.Direct.SEND) {
            //获取该信息对象中的状态。
            EMMessage.Status status = emMessage.status();
            switch (status) {
                case CREATE:
                case SUCCESS:
                    //消息发送成功
                    holder.sendFail.setVisibility(View.GONE);
                    break;
                case FAIL:
                    holder.sendFail.setVisibility(View.VISIBLE);
                    holder.sendFail.setImageResource(R.mipmap.chat_send_fail);
                    break;
                case INPROGRESS:
                    holder.sendFail.setVisibility(View.VISIBLE);
                    //发送信息加载动画
                    holder.sendFail.setImageResource(R.drawable.chat_loading_animation);
                    Drawable drawable = holder.sendFail.getDrawable();
                    if (drawable instanceof AnimationDrawable) {
                        AnimationDrawable animationDrawable = (AnimationDrawable) drawable;
                        if (!animationDrawable.isRunning())
                            animationDrawable.start();
                    }
                    break;
            }

        }
    }

    @Override
    public int getItemCount() {
        return mMessages == null ? 0 : mMessages.size();
//        return 10;
    }

    class ChatViewHolder extends RecyclerView.ViewHolder {

        private final TextView mContent;
        private final ImageView sendFail;
        private TextView mDate;
        private ImageView receiveHead;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            mDate = (TextView) itemView.findViewById(R.id.chat_date);
            receiveHead = (ImageView) itemView.findViewById(R.id.receive_head);
            mContent = (TextView) itemView.findViewById(R.id.chat_content);
            sendFail = (ImageView) itemView.findViewById(R.id.send_fail);
        }
    }
}
