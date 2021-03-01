package com.vx.storage_device_monitor.dao.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class cpuUsageRecord {
    private Timestamp timestamp;
    private BigDecimal cpuUsage;
    private float cpuUsagef;

    public cpuUsageRecord(Timestamp timestamp, BigDecimal cpuUsage) {
        this.timestamp = timestamp;
        this.cpuUsage = cpuUsage;
        this.cpuUsagef=cpuUsage.floatValue();
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public float getCpuUsagef() {
        return cpuUsagef;
    }

    @Override
    public String toString() {
        return "cpuUsageRecord{" +
                "timestamp=" + timestamp +
                ", cpuUsagef=" + cpuUsagef +
                '}';
    }
}
