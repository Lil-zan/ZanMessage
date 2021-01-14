package com.java.zanmessage.event;

public class ContactChangeEvent {
    private String contact;
    private boolean isAdded;

    public ContactChangeEvent(String contact, boolean isAdded) {
        this.contact = contact;
        this.isAdded = isAdded;
    }

    @Override
    public String toString() {
        return "ContactChangeEvent{" +
                "contact='" + contact + '\'' +
                ", isAdded=" + isAdded +
                '}';
    }
}
