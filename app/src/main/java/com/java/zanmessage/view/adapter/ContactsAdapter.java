package com.java.zanmessage.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.java.zanmessage.R;
import com.java.zanmessage.utils.StringUtils;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactsViewHolder> {

    private List<String> contacts;

    public ContactsAdapter(List<String> contacts) {
        this.contacts = contacts;
    }

    @NonNull
    @Override
    public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contacts_layout, parent, false);
        ContactsViewHolder viewHolder = new ContactsViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ContactsViewHolder holder, int position) {
        String contactName = contacts.get(position);
        //获取首字母
        String contactInitial = StringUtils.getInitial(contacts.get(position));
        holder.userName.setText(contactName);
        holder.initial.setText(contactInitial);
        if (position == 0) {
            holder.initial.setVisibility(View.VISIBLE);
        } else if (contactInitial.equalsIgnoreCase(StringUtils.getInitial(contacts.get(position - 1))) || StringUtils.getInitial(contacts.get(position)).isEmpty()) {
            holder.initial.setVisibility(View.GONE);
        } else {
            holder.initial.setVisibility(View.VISIBLE);
        }
        //子条目点击事件
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onContactsItemClickListener != null) {
                    onContactsItemClickListener.onContactClick(contacts.get(position));
                }
            }
        });
        //子条目长按事件
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onContactsItemClickListener != null)
                    onContactsItemClickListener.onContactLongClick(contacts.get(position));
                //不消化事件
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return contacts == null ? 0 : contacts.size();
    }


    class ContactsViewHolder extends RecyclerView.ViewHolder {

        private final TextView initial;
        private final TextView userName;

        public ContactsViewHolder(@NonNull View view) {
            super(view);
            initial = (TextView) view.findViewById(R.id.initial);
            userName = (TextView) view.findViewById(R.id.user_name);
        }
    }


    public interface onContactsItemClickListener {
        //单击联系人回调
        void onContactClick(String contact);

        //长按联系人回调
        void onContactLongClick(String contact);
    }

    //接口成员变量
    private onContactsItemClickListener onContactsItemClickListener;

    public void setOnContactsItemClickListener(ContactsAdapter.onContactsItemClickListener onContactsItemClickListener) {
        this.onContactsItemClickListener = onContactsItemClickListener;
    }

    public List<String> getContacts() {
        return contacts;
    }

}
