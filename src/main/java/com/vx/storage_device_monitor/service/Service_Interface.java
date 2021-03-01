package com.vx.storage_device_monitor.service;

import java.sql.Timestamp;

public interface Service_Interface {

    void insertNewRecord(String ip, Timestamp timestamp, float receiveBW, float transmitBW, float cpuUsage, float memoryUsage,
                               float diskUsage, int iNumber, int oNumber, float temp, float energy);
    void periodPersistence();

    String getHostInfoListOutputData();

    String getHostIpList();
}
