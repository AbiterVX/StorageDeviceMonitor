package com.vx.storage_device_monitor.dao.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class diskUsageRecord {
    private Timestamp timestamp;
    private BigDecimal diskUsage;
    private float diskUsagef;

    public diskUsageRecord(Timestamp timestamp, BigDecimal diskUsage) {
        this.timestamp = timestamp;
        this.diskUsage = diskUsage;
        this.diskUsagef=diskUsage.floatValue();
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public float getDiskUsagef() {
        return diskUsagef;
    }

    @Override
    public String toString() {
        return "diskUsageRecord{" +
                "timestamp=" + timestamp +
                ", diskUsagef=" + diskUsagef +
                '}';
    }
}
