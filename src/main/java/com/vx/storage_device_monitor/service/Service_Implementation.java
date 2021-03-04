package com.vx.storage_device_monitor.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.vx.storage_device_monitor.dao.Dao_disk;
import com.vx.storage_device_monitor.dao.Dao_record;
import com.vx.storage_device_monitor.dao.entity.*;
import com.vx.storage_device_monitor.utils.HostMonitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@Order(value=1)
public class Service_Implementation implements Service_Interface, ApplicationRunner {
    @Autowired
    private Dao_record dao_record;
    @Autowired
    private Dao_disk dao_disk;
    private HostMonitor hostMonitor=new HostMonitor();
    private Thread pThread=null;
    @Override
    public void insertNewRecord(String ip, Timestamp timestamp,float receiveBW,float transmitBW,float cpuUsage,float memoryUsage,
                                      float diskUsage,int iNumber,int oNumber,float temp,float energy) {
        dao_record.insertNewRecord(ip,timestamp,receiveBW,transmitBW,cpuUsage,memoryUsage,diskUsage,iNumber,oNumber,temp,energy);
    }

    @Override
    public void run(ApplicationArguments args) {
        startMonitor();
        periodPersistence();
        try {
            hostMonitor.getNewThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void periodPersistence() {
        if(pThread==null) {
            pThread = new Thread(new PersistenceThread(), "The thread to persist");
            pThread.start();

        }
    }

    @Override
    public String getSingleNewestInfoByIp(String ip) {
        return JSON.toJSONString(newestData(ip));

    }

    @Override
    public String getHostInfoListOutputData() {
        return hostMonitor.getHostInfoListOutputData();
    }
    public String getHostHardwareInfoListOutputData(){
        return hostMonitor.getHostHardWareInfoListOutputData();
    }
    @Override
    public String getHostIpList() {
        return hostMonitor.getHostIpList();
    }

    private void startMonitor() {
        hostMonitor.start();
    }
    public String getHostIp(int index){
        return hostMonitor.getHostIp(index);
    }


    private class PersistenceThread implements Runnable{

        private int interval_ms;
        public PersistenceThread(){
            interval_ms=hostMonitor.interval_ms;
        }
        @Override
        public void run() {
            while(hostMonitor.threadStart){
//                try {
//                    Thread.sleep(2000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                //采样
                while(hostMonitor.isDataHasBeenWritten());
                List<Map<String,Object>> listForWritten=hostMonitor.getOriginalHostInfoListOutputData();
                for(Map<String,Object> iterable:listForWritten){
                    dao_record.insertNewRecord((String)iterable.get("ip"),(Timestamp) iterable.get("timestamp"),(float)iterable.get("receiveBW"),(float)iterable.get("transmitBW"),
                            (float)iterable.get("cpuUsage"), (float)iterable.get("memoryUsage"),(float)iterable.get("diskUsage"),250,250,36.0f,600.0f);
                }
                //等待
                hostMonitor.setDataHasBeenWritten(true);
                try {
                    Thread.sleep(interval_ms);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Map<String,Object> newestData(String ip){
        List<Map<String,Object>> temp=hostMonitor.getOriginalHostInfoListOutputData();
        for(Map<String,Object> iterable:temp){
            if(iterable.get("ip").equals(ip)){
                return iterable;
            }
        }
        return null;
    }
    public List<BWrecord> getBWInTimeRange(String ip,int numberOfDays){
        return dao_record.getBWWithTimestamp(new Timestamp(System.currentTimeMillis()-numberOfDays*86400000),new Timestamp(System.currentTimeMillis()),ip);
    }
    public List<IOrecord> getIOInTimeRange(String ip,int numberOfDays){
        return dao_record.getIOWithTimestamp(new Timestamp(System.currentTimeMillis()-numberOfDays*86400000),new Timestamp(System.currentTimeMillis()),ip);
    }
    @Override
    public String getRecentInfoByIp(String ip, int numberOfDays, FieldType fieldType) {
        String result;
        switch(fieldType){
            case CPUUSAGE:
                result=JSON.toJSONString(dao_record.getCpuUsageWithTimestamp(new Timestamp(System.currentTimeMillis()-numberOfDays*86400000),
                        new Timestamp(System.currentTimeMillis()),ip));
                break;
            case MEMORYUSAGE:
                result=JSON.toJSONString(dao_record.getMemoryUsageWithTimestamp(new Timestamp(System.currentTimeMillis()-numberOfDays*86400000),
                        new Timestamp(System.currentTimeMillis()),ip));
                break;
            case DISKUSAGE:
                result=JSON.toJSONString(dao_record.getDiskUsageWithTimestamp(new Timestamp(System.currentTimeMillis()-numberOfDays*86400000),
                        new Timestamp(System.currentTimeMillis()),ip));
                break;
            case RECEIVEBANDWIDTH:
                result=JSON.toJSONString(dao_record.getReceiveBWWithTimestamp(new Timestamp(System.currentTimeMillis()-numberOfDays*86400000),
                        new Timestamp(System.currentTimeMillis()),ip));
                break;
            case TRANSMITBANDWIDTH:
                result=JSON.toJSONString(dao_record.getTransmitBWWithTimestamp(new Timestamp(System.currentTimeMillis()-numberOfDays*86400000),
                        new Timestamp(System.currentTimeMillis()),ip));
                break;
            case INPUTNUMBER:
                result=JSON.toJSONString(dao_record.getInumberWithTimestamp(new Timestamp(System.currentTimeMillis()-numberOfDays*86400000),
                        new Timestamp(System.currentTimeMillis()),ip));
                break;
            case OUTPUTNUMBER:
                result=JSON.toJSONString(dao_record.getOnumberWithTimestamp(new Timestamp(System.currentTimeMillis()-numberOfDays*86400000),
                        new Timestamp(System.currentTimeMillis()),ip));
                break;
            case TEMPERATURE:
                result=JSON.toJSONString(dao_record.getTempWithTimestamp(new Timestamp(System.currentTimeMillis()-numberOfDays*86400000),
                        new Timestamp(System.currentTimeMillis()),ip));
                break;
            case ENERGY:
                result=JSON.toJSONString(dao_record.getEnergyWithTimestamp(new Timestamp(System.currentTimeMillis()-numberOfDays*86400000),
                        new Timestamp(System.currentTimeMillis()),ip));
                break;
            default:
                result=JSON.toJSONString(dao_record.recordQueryWithTimestamp(new Timestamp(System.currentTimeMillis()-numberOfDays*86400000),
                        new Timestamp(System.currentTimeMillis()),ip));
                break;
        }
        return result;
    }
    public String getFullRecordsByIP(String ip, int numberOfDays){
        int numberOfEntry= dao_record.recordNumberQueryWithTimestamp(new Timestamp(System.currentTimeMillis()-numberOfDays*86400000),
                new Timestamp(System.currentTimeMillis()),ip);
        List<Record> tempResult=dao_record.recordQueryWithTimestamp(new Timestamp(System.currentTimeMillis()-numberOfDays*86400000),
                new Timestamp(System.currentTimeMillis()),ip);
        Float[] temp=new Float[numberOfEntry];
        Float[] cpuUsage=new Float[numberOfEntry];
        Float[] diskUsage=new Float[numberOfEntry];
        Float[] memoryUsage=new Float[numberOfEntry];
        Float[] receiveBW=new Float[numberOfEntry];
        Float[] transmitBW=new Float[numberOfEntry];
        Timestamp[] timestamp=new Timestamp[numberOfEntry];
        int i=0;
        for(Record record:tempResult){
            temp[i]=record.getTempf();
            cpuUsage[i]=record.getCpuUsagef();
            diskUsage[i]=record.getDiskUsagef();
            memoryUsage[i]=record.getMemoryUsagef();
            receiveBW[i]=record.getReceiveBWf();
            transmitBW[i]=record.getTransmitBWf();
            timestamp[i]=record.getTimestamp();
            i++;
        }
        JSONObject result=new JSONObject();
        result.put("temp",temp);
        result.put("cpuUsage",cpuUsage);
        result.put("diskUsage",diskUsage);
        result.put("memoryUsage",memoryUsage);
        result.put("receiveBW",receiveBW);
        result.put("transmitBW",transmitBW);
        result.put("timestamp",timestamp);
        return result.toJSONString();
    }


    public String getDiskFailureList(String ip) {
        List<String> timestampList=dao_disk.getDiskFailureTimestamp(ip);
        String resultDate=findLatest(timestampList);
        List<String> diskFailureInfo=dao_disk.getDiskFailureInfo(ip,resultDate);
        StringBuffer temp=new StringBuffer("");
        for(String string:diskFailureInfo){
            temp.append(string);
            temp.append("\n");
        }
        JSONObject resultObject=new JSONObject();
        resultObject.put("ip",ip);
        resultObject.put("date",resultDate);
        resultObject.put("Information",temp.toString());
        return resultObject.toJSONString();
    }

    private String findLatest(List<String> timestampList) {
        ArrayList<DateParser> list=new ArrayList<>();
        for(String string:timestampList){
            list.add(new DateParser(string));
        }
        Collections.sort(list);
        return list.get(list.size()-1).getOriginalFormat();

    }


    public String getAllDeviceCpuUsage(int numberOfDays) {
        //for(String ipitem:hostMonitor.getHostIpList()et)

        int numberOfEntry= dao_record.allRecordNumberQueryWithTimestamp(new Timestamp(System.currentTimeMillis()-numberOfDays*86400000),
                new Timestamp(System.currentTimeMillis()));
        List<Record> tempResult=dao_record.allCpuUsageQueryWithTimestamp(new Timestamp(System.currentTimeMillis()-numberOfDays*86400000),
                new Timestamp(System.currentTimeMillis()));
        Float[] temp=new Float[numberOfEntry];
        Float[] cpuUsage=new Float[numberOfEntry];
        Float[] diskUsage=new Float[numberOfEntry];
        Float[] memoryUsage=new Float[numberOfEntry];
        Float[] receiveBW=new Float[numberOfEntry];
        Float[] transmitBW=new Float[numberOfEntry];
        int i=0;
        for(Record record:tempResult){
            temp[i]=record.getTempf();
            cpuUsage[i]=record.getCpuUsagef();
            diskUsage[i]=record.getDiskUsagef();
            memoryUsage[i]=record.getMemoryUsagef();
            receiveBW[i]=record.getReceiveBWf();
            transmitBW[i]=record.getTransmitBWf();
            i++;
        }
        JSONObject result=new JSONObject();
        result.put("temp",temp);
        result.put("cpuUsage",cpuUsage);
        result.put("diskUsage",diskUsage);
        result.put("memoryUsage",memoryUsage);
        result.put("receiveBW",receiveBW);
        result.put("transmitBW",transmitBW);
        return result.toJSONString();
    }

}
