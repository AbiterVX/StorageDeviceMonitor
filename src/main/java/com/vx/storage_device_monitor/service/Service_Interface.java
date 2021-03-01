package com.vx.storage_device_monitor.service;

import com.vx.storage_device_monitor.dao.entity.FieldType;

import java.sql.Timestamp;
import java.util.List;

public interface Service_Interface {

    void insertNewRecord(String ip, Timestamp timestamp, float receiveBW, float transmitBW, float cpuUsage, float memoryUsage,
                               float diskUsage, int iNumber, int oNumber, float temp, float energy);
    void periodPersistence();

    String getHostInfoListOutputData();

    String getHostIpList();

    String getSingleNewestInfoByIp(String ip);
    String getRecentInfoByIp(String ip, int numberOfDays, FieldType fieldType);
}
