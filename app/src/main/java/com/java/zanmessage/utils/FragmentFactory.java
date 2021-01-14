package com.java.zanmessage.utils;

import com.java.zanmessage.view.fragment.BaseFragment;
import com.java.zanmessage.view.fragment.ContactsFragment;
import com.java.zanmessage.view.fragment.MsgFragment;
import com.java.zanmessage.view.fragment.StateFragment;

public class FragmentFactory {

    public static MsgFragment msgFragment;
    public static ContactsFragment contactsFragment;
    public static StateFragment stateFragment;

    public static BaseFragment getFragment(int position) {
        switch (position) {
            case 0:
                if (msgFragment == null) msgFragment = new MsgFragment();
                return msgFragment;
            case 1:
                if (contactsFragment == null) contactsFragment = new ContactsFragment();
                return contactsFragment;
            case 2:
                if (stateFragment == null) stateFragment = new StateFragment();
                return stateFragment;
            default:
                break;
        }
        return null;
    }
}
