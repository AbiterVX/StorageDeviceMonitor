package com.vx.storage_device_monitor.utils;

import java.util.Arrays;
import java.util.List;

/**
 * No Use now
 */
public class HostInfo {
    public HostInfo(String _ip){
        ip = _ip;
        receiveBytes = new int[2];
        Arrays.fill(receiveBytes,0);
        transmitBytes = new int[2];
        Arrays.fill(transmitBytes,0);

        cpuTotalTime = new int[2];
        Arrays.fill(cpuTotalTime,0);
        cpuIdleTime = new int[2];
        Arrays.fill(cpuIdleTime,0);

        memTotal = 0;
        memAvaliable = 0;

        ioTimeSpent = new int[2];
        Arrays.fill(ioTimeSpent,0);
    }
    //ip
    public String ip;

    //[流量统计]接受字节数量
    public int[] receiveBytes;
    //[流量统计]发送字节数量
    public int[] transmitBytes;

    //[CPU使用率]总时间
    public int[] cpuTotalTime;

    //[CPU使用率]空闲时间
    public int[] cpuIdleTime;

    //[内存]内存总量
    public int memTotal;
    //[内存]内存可用
    public int memAvaliable;

    //[磁盘]输入/输出操作花费的时间
    public int[] ioTimeSpent;


    //CPU使用率
    //内存使用率
    //硬盘使用率
    //带宽,单位k
    //温度
    //IO数量
    //计算能力-CPU型号
    //能耗
    //磁盘故障信息

    //[网络带宽]输出（格式KB/s）
    public void printNetBindWidth(){
        float reciveBW = 0;
        float transmitBW = 0;
        if(receiveBytes[0] != 0 && receiveBytes[0] != transmitBytes[1]){
            //两次采样差值，并由原bytes转为KB/s格式
            reciveBW = (receiveBytes[1] - receiveBytes[0])*8f/60f/1024f ;
            transmitBW = (transmitBytes[1] - transmitBytes[0])*8f/60f/1024f;
            //保留2位小数
            reciveBW=( float )(Math.round(reciveBW* 100f ) / 100f );
            transmitBW=( float )(Math.round(transmitBW* 100f )/ 100f );
        }
        System.out.println("IP: "+ip+" ,reciveBW: "+reciveBW + "KB/s ,transmitBW: "+transmitBW +" KB/s");
    }

    public void printCpuUsage(){
        float cpuUsage = 0;
        if(cpuTotalTime[0] != 0 && cpuTotalTime[0] != cpuTotalTime[1]){
            cpuUsage = (1f-  (float) (cpuIdleTime[1] -cpuIdleTime[0]) / (float)(cpuTotalTime[1] - cpuTotalTime[0]) ) * 100f ;
            cpuUsage=( float )(Math.round(cpuUsage* 100f ) / 100f );
        }
        System.out.println("IP: "+ip+" ,cpuUsage: "+cpuUsage + " %");
    }

    public void printMemoryUsage(){
        float memoryUsage = 0;
        memoryUsage = (float) (memTotal - memAvaliable ) / (float)memTotal * 100f ;
        memoryUsage = ( float )(Math.round(memoryUsage* 100f ) / 100f );
        System.out.println("IP: "+ip+" ,memoryUsage: "+memoryUsage + " %");
    }

    public void printDiskUsage(int interval_ms){
        float diskUsage = 0;
        if(ioTimeSpent[0] != 0 && ioTimeSpent[0] != ioTimeSpent[1]){
            diskUsage = (float) (ioTimeSpent[1] - ioTimeSpent[0]) / (float)interval_ms * 100f;
            diskUsage=( float )(Math.round(diskUsage* 100f ) / 100f );
        }
        System.out.println("IP: "+ip+" ,diskUsage: "+diskUsage + " %");
    }
}
