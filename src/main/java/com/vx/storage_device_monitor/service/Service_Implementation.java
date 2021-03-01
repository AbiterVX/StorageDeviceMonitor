package com.vx.storage_device_monitor.service;

import com.vx.storage_device_monitor.dao.Dao_record;
import com.vx.storage_device_monitor.dao.entity.BWrecord;
import com.vx.storage_device_monitor.dao.entity.IOrecord;
import com.vx.storage_device_monitor.utils.HostMonitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Service
@Order(value=1)
public class Service_Implementation implements Service_Interface, ApplicationRunner {
    @Autowired
    private Dao_record dao_record;
    private HostMonitor hostMonitor=new HostMonitor();
    private Thread pThread=null;
    @Override
    public void insertNewRecord(String ip, Timestamp timestamp,float receiveBW,float transmitBW,float cpuUsage,float memoryUsage,
                                      float diskUsage,int iNumber,int oNumber,float temp,float energy) {
        dao_record.insertNewRecord(ip,timestamp,receiveBW,transmitBW,cpuUsage,memoryUsage,diskUsage,iNumber,oNumber,temp,energy);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        startMonitor();
        periodPersistence();
    }

    @Override
    public void periodPersistence() {
        if(pThread==null) {
            pThread = new Thread(new PersistenceThread(), "The thread to persist");
            pThread.start();
        }
    }

    @Override
    public String getHostInfoListOutputData() {
        return hostMonitor.getHostInfoListOutputData();
    }

    @Override
    public String getHostIpList() {
        return hostMonitor.getHostIpList();
    }

    private void startMonitor() {
        hostMonitor.start();
    }
    private class PersistenceThread implements Runnable{

        private int interval_ms;
        public PersistenceThread(){
            interval_ms=hostMonitor.interval_ms;
        }
        @Override
        public void run() {
            while(hostMonitor.threadStart){
                //采样
                while(hostMonitor.isDataHasBeenWritten());
                List<Map<String,Object>> listForWritten=hostMonitor.getOriginalHostInfoListOutputData();
                for(Map<String,Object> iterable:listForWritten){
                    dao_record.insertNewRecord((String)iterable.get("ip"),new Timestamp(System.currentTimeMillis()),(float)iterable.get("receiveBW"),(float)iterable.get("transmitBW"),
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

    public Map<String,Object> newestData(String ip){
        List<Map<String,Object>> temp=hostMonitor.getOriginalHostInfoListOutputData();
        for(Map<String,Object> iterable:temp){
            if(iterable.get("ip").equals(ip)){
                return iterable;
            }
        }
        return null;
    }
    public List<BWrecord> getBWInTimeRange(Timestamp lowbound,Timestamp highbound,String ip){
        return dao_record.getBWWithTimestamp(lowbound,highbound,ip);
    }
    public List<IOrecord> getIOInTimeRange(Timestamp lowbound, Timestamp highbound, String ip){
        return dao_record.getIOWithTimestamp(lowbound,highbound,ip);
    }
}
