package com.vx.storage_device_monitor.utils;




import com.alibaba.fastjson.JSON;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * 主机监控 -main class
 */
public class HostMonitor implements Runnable {
    //---------成员
    SSHManager sshManager;

    //Config配置信息
    private Config configInfo;
    //Host配置信息
    List<HostConfigInfo> hostConfigInfoList;
    //Host系统信息
    List<HostInfo> hostInfoList;
    //采样间隔，默认为2s
    public int interval_ms;

    public Thread getNewThread() {
        return newThread;
    }

    //线程
    private Thread newThread;
    //线程开始
    public boolean threadStart;
    private boolean isDataHasBeenWritten=true;




    //---------Init
    public HostMonitor(){
        //延迟时间默认为2000，单位：ms
        this(10000);
    }

    public boolean isDataHasBeenWritten() {
        return isDataHasBeenWritten;
    }

    public void setDataHasBeenWritten(boolean dataHasBeenWritten) {
        isDataHasBeenWritten = dataHasBeenWritten;
    }


    public HostMonitor(int _interval_ms){
        //配置信息
        configInfo = new Config();
        //主机配置信息
        hostConfigInfoList = configInfo.getStorageDeviceHost();
        //主机返回信息
        hostInfoList =new ArrayList<>();
        for(int i=0;i<hostConfigInfoList.size();i++){
            HostInfo newHostInfo = new HostInfo(hostConfigInfoList.get(i).ip);
            hostInfoList.add(newHostInfo);
        }
        //延迟时间 单位：ms
        interval_ms = _interval_ms;
        //采样线程开关
        threadStart = false;
        //SSH默认连接方式为JSCH。
        sshManager = new JschSSHManager();
    }

    //---------SSH远程连接&指令调用
    //执行指令
    public List<String> runCommand(String command, HostConfigInfo hostConfigInfo){
        return sshManager.runCommand(command,hostConfigInfo);
    }
    //--------- 设置连接状态（如连接中断则保证其他主机也能正常连接）
    public void setSessionConnected(int index,boolean _sessionConnected){
        HostInfo currentHostInfo = hostInfoList.get(index);
        currentHostInfo.sessionConnected = _sessionConnected;
    }

    //--------- 采样
    //全部采样
    public void sampleAll(){
        sampleNetBindWidth();
        sampleCpuUsage();
        sampleMemory();
        sampleDisk();
    }
    //[网络带宽]采样
    public void sampleNetBindWidth(){
        String command = "cat /proc/net/dev";
        for(int i=0;i<hostConfigInfoList.size();i++){
            List<String> commandResult = runCommand(command, hostConfigInfoList.get(i));
            //设置连接状态
            setSessionConnected(i, commandResult.size() != 0);

            for(int j=0;j<commandResult.size();j++){
                String currentLine = commandResult.get(j);
                if(currentLine.contains("eth0")){
                    String[] datas = currentLine.split("\\s+");
                    //数据设置
                    HostInfo currentHostInfo = hostInfoList.get(i);
                    currentHostInfo.receiveBytes[0] = currentHostInfo.receiveBytes[1];
                    currentHostInfo.transmitBytes[0] = currentHostInfo.transmitBytes[1];
                    currentHostInfo.receiveBytes[1] = Integer.parseInt(datas[2]);
                    currentHostInfo.transmitBytes[1] = Integer.parseInt(datas[10]);
                    System.out.println("In function sampleNetBinWidth of HostMonitor"+currentHostInfo.receiveBytes);
                    break;
                }
            }
        }


    }
    //[CPU利用率]采样
    public void sampleCpuUsage(){
        String command = "cat /proc/stat";
        for(int i=0;i<hostConfigInfoList.size();i++){
            List<String> commandResult = runCommand(command, hostConfigInfoList.get(i));
            //设置连接状态
            setSessionConnected(i, commandResult.size() != 0);

            if(commandResult.size()!= 0 ){
                String currentLine = commandResult.get(0);
                if(currentLine.contains("cpu")){
                    String[] datas = currentLine.split("\\s+");
                    int totalTime = 0;
                    for(int j=1;j<=7;j++){
                        totalTime += Integer.parseInt(datas[j]);
                    }

                    HostInfo currentHostInfo = hostInfoList.get(i);
                    currentHostInfo.cpuTotalTime[0] = currentHostInfo.cpuTotalTime[1];
                    currentHostInfo.cpuTotalTime[1] = totalTime;
                    currentHostInfo.cpuIdleTime[0] = currentHostInfo.cpuIdleTime[1];
                    currentHostInfo.cpuIdleTime[1] = Integer.parseInt(datas[4]);
                }
            }
        }
    }
    //[内存]采样
    public void sampleMemory(){
        String command = "head -n 5 /proc/meminfo";
        for(int i=0;i<hostConfigInfoList.size();i++){
            List<String> commandResult = runCommand(command, hostConfigInfoList.get(i));
            //设置连接状态
            setSessionConnected(i, commandResult.size() != 0);

            if(commandResult.size()!= 0 ){
                HostInfo currentHostInfo = hostInfoList.get(i);
                currentHostInfo.memTotal = Integer.parseInt(commandResult.get(0).split("\\s+")[1]);
                currentHostInfo.memAvaliable = Integer.parseInt(commandResult.get(2).split("\\s+")[1]);
            }
        }
    }
    //[磁盘]采样
    public void sampleDisk(){
        String command = "cat /proc/diskstats";
        for(int i=0;i<hostConfigInfoList.size();i++){
            List<String> commandResult = runCommand(command, hostConfigInfoList.get(i));
            //设置连接状态
            setSessionConnected(i, commandResult.size() != 0);

            for(String resultLine:commandResult){
                if(resultLine.contains("vda")){
                    HostInfo currentHostInfo = hostInfoList.get(i);

                    currentHostInfo.ioTimeSpent[0] = currentHostInfo.ioTimeSpent[1];
                    String[] datas = resultLine.split("\\s+");

                    currentHostInfo.ioTimeSpent[1]= Integer.parseInt(datas[13]);

                    break;
                }
            }
        }

    }
    //---------获得Host设备信息
    public String getHostInfoListOutputData(){
        JSONArray jsonArray = new JSONArray();
        for(HostInfo hostInfo:hostInfoList){
            Map<String, Object> result = hostInfo.getOutputData(interval_ms);
            JSONObject jsonObject = new JSONObject(result);
            jsonArray.add(jsonObject);
        }
        return jsonArray.toJSONString();
    }
    public String getHostIp(int index){
        return hostConfigInfoList.get(index).ip;
    }
    public Map<String, Object> getHostHardWareInfo(String ip){
        Map<String, Object> result = new HashMap<>();
        result.put("CPU",ip);
        result.put("Memory",ip);
        result.put("Disk",ip);
        result.put("GPU",ip);
        return result;
    }
    public String getHostHardWareInfoListOutputData(){
        JSONArray jsonArray = new JSONArray();
        for(HostInfo hostInfo:hostInfoList){
            Map<String, Object> result = getHostHardWareInfo(hostInfo.ip);
            JSONObject jsonObject = new JSONObject(result);
            jsonArray.add(jsonObject);
        }

        return jsonArray.toJSONString();
    }

    //获取Host的IP
    public String getHostIpList(){
        JSONArray jsonArray = new JSONArray();
        for(HostInfo hostInfo:hostInfoList){
            jsonArray.add(hostInfo.ip);
        }
        return jsonArray.toJSONString();
    }
    public List<Map<String,Object>> getOriginalHostInfoListOutputData(){
        ArrayList<Map<String,Object>> mapList=new ArrayList<>();
        for(HostInfo hostInfo:hostInfoList){
            Map<String, Object> result = hostInfo.getOutputData(interval_ms);
            mapList.add(result);
        }
        return mapList;
    }
    //---------多线程执行
    //多线程运行
    @Override
    public void run() {
        while(threadStart){
            System.out.println("Check:"+isDataHasBeenWritten);
            while(!isDataHasBeenWritten);
            //采样
            sampleAll();
            //获取

            for(HostInfo hostInfo:hostInfoList){
                System.out.println("In function run() of HostMonitor"+hostInfo.getOutputData(interval_ms).toString());
            }

            //等待
            setDataHasBeenWritten(false);
            try {
                Thread.sleep(interval_ms);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
    //开始线程
    public void start(){
        if(newThread == null){
            for(HostInfo hostInfo:hostInfoList){
                hostInfo.initValue();
            }
            threadStart = true;
            newThread = new Thread(this,"HostMonitor—thread");
            newThread.start();
        }
    }
    //终止线程
    public void stop(){
        threadStart = false;
        try {
            Thread.sleep(interval_ms*2);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(newThread != null){
            newThread.interrupt();
            newThread = null;
        }
    }
    //main-用于测试
    public static void main(String[] args) {
        HostMonitor hostMonitor = new HostMonitor();
        hostMonitor.start();
        System.out.println(hostMonitor.getHostInfoListOutputData());

//        try {
//            Thread.sleep(30000);
//        }
//        catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        hostMonitor.stop();
    }

}
