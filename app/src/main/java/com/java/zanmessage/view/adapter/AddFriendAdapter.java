package com.java.zanmessage.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.util.DateUtils;
import com.java.zanmessage.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import cn.leancloud.AVUser;

public class AddFriendAdapter extends RecyclerView.Adapter<AddFriendAdapter.ViewHolder> {

    public List<AVUser> avUsers;
    public List<String> mContacts;

    public AddFriendAdapter(List<AVUser> avUsers, List<String> mContacts) {
        this.avUsers = avUsers;
        this.mContacts = mContacts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_friend_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String user = avUsers.get(position).getUsername();
        holder.name.setText(user);
        holder.date.setText(DateUtils.getTimestampString(avUsers.get(position).getCreatedAt()));

        if (mContacts != null || mContacts.size() > 0) {
            if (mContacts.contains(user)) {
                holder.addButton.setEnabled(false);
                holder.addButton.setText("已添加");
            } else {
                holder.addButton.setEnabled(true);
                holder.addButton.setText("添加");

            }

        } else {
            holder.addButton.setEnabled(true);
            holder.addButton.setText("添加");
        }
        holder.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onAddClickListener != null)
                    onAddClickListener.addClick(user, "");
            }
        });

    }


    @Override
    public int getItemCount() {
        return avUsers == null ? 0 : avUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView headImage;
        private final TextView name;
        private final TextView date;
        private final Button addButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            headImage = (ImageView) itemView.findViewById(R.id.head_iamge);
            name = (TextView) itemView.findViewById(R.id.name_text);
            date = (TextView) itemView.findViewById(R.id.date_text);
            addButton = (Button) itemView.findViewById(R.id.add_button);
        }
    }

    public interface OnAddClickListener {
        void addClick(String user,String addContent);
    }

    public void setOnAddClickListener(OnAddClickListener onAddClickListener) {
        this.onAddClickListener = onAddClickListener;
    }

    private OnAddClickListener onAddClickListener;
}
