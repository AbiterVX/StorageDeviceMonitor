package com.vx.storage_device_monitor.dao.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class memoryUsageRecord {
    private Timestamp timestamp;
    private BigDecimal memoryUsage;
    private float memoryUsagef;

    public memoryUsageRecord(Timestamp timestamp, BigDecimal memoryUsage) {
        this.timestamp = timestamp;
        this.memoryUsage = memoryUsage;
        this.memoryUsagef=memoryUsage.floatValue();
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public float getMemoryUsagef() {
        return memoryUsagef;
    }

    @Override
    public String toString() {
        return "memoryUsageRecord{" +
                "timestamp=" + timestamp +
                ", memoryUsagef=" + memoryUsagef +
                '}';
    }
}
