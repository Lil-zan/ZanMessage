package com.java.zanmessage.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessageBody;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.util.DateUtils;
import com.java.zanmessage.R;

import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder> {
    private List<EMConversation> list;

    public ConversationAdapter(List<EMConversation> mConversations) {
        this.list = mConversations;
    }

    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_conversation_layout, parent, false);
        ConversationViewHolder viewholder = new ConversationViewHolder(view);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationViewHolder holder, int position) {
        EMConversation emConversation = list.get(position);
        //获取最后一条消息对象
        EMMessage lastMessage = emConversation.getLastMessage();
        //获取username
        String username = lastMessage.getUserName();
        //获取最后收到的一条消息
        EMMessageBody body = lastMessage.getBody();
        if (body instanceof EMTextMessageBody) {
            EMTextMessageBody mBody = (EMTextMessageBody) body;
            holder.mMsg.setText(mBody.getMessage().trim());
        } else {
            EMMessage.Type type = lastMessage.getType();
            holder.mMsg.setText("[" + type.toString() + "]");
        }
        //获取到最后一条消息的时间
        long msgTime = lastMessage.getMsgTime();
        //获取未读信息数
        int unreadMsgCount = emConversation.getUnreadMsgCount();
        holder.user.setText(username);
        holder.date.setText(DateUtils.getTimestampString(new Date(msgTime)));
        if (unreadMsgCount > 99) {
            holder.msgNum.setVisibility(View.VISIBLE);
            holder.msgNum.setText("...");
        } else if (unreadMsgCount == 0) {
            holder.msgNum.setVisibility(View.GONE);
        } else {
            holder.msgNum.setVisibility(View.VISIBLE);
            holder.msgNum.setText(unreadMsgCount + "");
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onConversationClickListener != null) {
                    onConversationClickListener.onConversationClick(emConversation);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    class ConversationViewHolder extends RecyclerView.ViewHolder {

        private final TextView date;
        private final TextView msgNum;
        private final TextView user;
        private final TextView mMsg;
        private ImageView head;

        public ConversationViewHolder(@NonNull View view) {
            super(view);
            head = (ImageView) view.findViewById(R.id.conversation_head);
            date = (TextView) view.findViewById(R.id.conversation_date);
            msgNum = (TextView) view.findViewById(R.id.msg_num);
            user = (TextView) view.findViewById(R.id.conversation_user);
            mMsg = (TextView) view.findViewById(R.id.conversation_msg);
        }
    }

    private OnConversationClickListener onConversationClickListener;

    public interface OnConversationClickListener {
        void onConversationClick(EMConversation emConversation);
    }

    public void setOnConversationClickListener(OnConversationClickListener onConversationClickListener) {
        this.onConversationClickListener = onConversationClickListener;
    }
}
