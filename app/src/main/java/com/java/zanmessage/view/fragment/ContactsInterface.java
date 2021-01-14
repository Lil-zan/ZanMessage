package com.java.zanmessage.view.fragment;

import java.util.List;

public interface ContactsInterface {
    void initContact(List<String> contacts);

    void upDataContacts();

    void upDataFailure();

    void onDeleteContact(boolean isSuccess, String error, String contact);
}
