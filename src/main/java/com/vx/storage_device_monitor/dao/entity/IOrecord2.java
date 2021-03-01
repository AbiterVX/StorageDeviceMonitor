package com.vx.storage_device_monitor.dao.entity;

import java.sql.Timestamp;

public class IOrecord2 {
    private Integer number;
    private Timestamp timestamp;

    public IOrecord2(Integer number, Timestamp timestamp) {
        this.number = number;
        this.timestamp = timestamp;
    }

    public Integer getNumber() {
        return number;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "IOrecord2{" +
                "number=" + number +
                ", timestamp=" + timestamp +
                '}';
    }
}
