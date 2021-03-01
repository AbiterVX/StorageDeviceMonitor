package com.vx.storage_device_monitor.dao.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class BWrecord2 {
    private BigDecimal BW;
    private Timestamp timestamp;
    private float BWf;

    public BWrecord2(BigDecimal BW, Timestamp timestamp) {
        this.BW = BW;
        this.timestamp = timestamp;
        this.BWf=BW.floatValue();
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public float getBWf() {
        return BWf;
    }

    @Override
    public String toString() {
        return "BWrecord2{" +
                "timestamp=" + timestamp +
                ", BWf=" + BWf +
                '}';
    }
}
