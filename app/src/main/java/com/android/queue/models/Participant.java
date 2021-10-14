package com.android.queue.models;

public class Participant {
    public String waiterPhone;
    public String waiterName;
    public Long waiterNumber;
    public String waiterState;

    public Participant(String waiterPhone, String waiterName, String waiterState) {
        this.waiterPhone = waiterPhone;
        this.waiterName = waiterName;
        this.waiterState = waiterState;
    }

    public Participant(String waiterPhone, String waiterName, Long waiterNumber, String waiterState) {
        this.waiterPhone = waiterPhone;
        this.waiterName = waiterName;
        this.waiterState = waiterState;
        this.waiterNumber = waiterNumber;
    }

    public Participant() {}
}
