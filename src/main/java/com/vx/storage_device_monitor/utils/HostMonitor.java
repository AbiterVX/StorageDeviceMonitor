package com.vx.storage_device_monitor.utils;




import com.alibaba.fastjson.JSON;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * 主机监控 -main class
 */
public class HostMonitor implements Runnable {
    //---------成员
    //字符集
    private String chartset;
    //ssh连接 List
    private Map<String,Connection> connectionMap;
    //Config配置信息
    private Config configInfo;
    //Host配置信息
    List<HostConfigInfo> hostConfigInfoList;
    //Host系统信息
    List<HostInfo> hostInfoList;
    //采样间隔，默认为2s
    public int interval_ms;
    //线程
    private Thread newThread;
    //线程开始
    public boolean threadStart;

    //---------Init
    public HostMonitor(){
        this(2000);
    }
    public HostMonitor(int _interval_ms){
        connectionMap =new HashMap<>();
        chartset = "UTF-8";
        configInfo = new Config();
        hostConfigInfoList = configInfo.getStorageDeviceHost();
        hostInfoList =new ArrayList<>();
        for(int i=0;i<hostConfigInfoList.size();i++){
            HostInfo newHostInfo = new HostInfo(hostConfigInfoList.get(i).ip);
            hostInfoList.add(newHostInfo);
        }
        interval_ms = _interval_ms;
        threadStart = false;
    }

    //---------SSH远程连接&指令调用
    //获取Session
    public Session getSession(HostConfigInfo hostConfigInfo){
        boolean sessionExist = connectionMap.containsKey(hostConfigInfo.ip);
        Connection newConnection = null;
        Session newSession = null;

        try {
            if(sessionExist){
                newConnection = connectionMap.get(hostConfigInfo.ip);
                newSession = newConnection.openSession();
            }
            else{
                newConnection = new Connection(hostConfigInfo.ip);
                newConnection.connect();
                boolean isAuthenticated = newConnection.authenticateWithPassword(hostConfigInfo.username, hostConfigInfo.password);
                if(isAuthenticated){
                    newSession = newConnection.openSession();
                    connectionMap.put(hostConfigInfo.ip,newConnection);
                }
                else{
                    //连接失败
                    return null;
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            if (newSession != null) {
                newSession.close();
            }
            if (newConnection != null) {
                newConnection.close();
            }
            if(connectionMap.containsKey(hostConfigInfo.ip)){
                connectionMap.remove(hostConfigInfo.ip);
            }
        }
        return newSession;
    }
    //执行指令
    public List<String> runCommand(String command, HostConfigInfo hostConfigInfo){
        List<String> result = new ArrayList<String>();

        //远程调用
        Session session = getSession(hostConfigInfo);
        if(session != null){
            try {
                //执行指令
                session.execCommand(command);
                //输出
                InputStream is = new StreamGobbler(session.getStdout());
                BufferedReader brs = new BufferedReader(new InputStreamReader(is, chartset));
                //逐行获取输出结果
                for (String line = brs.readLine(); line != null; line = brs.readLine()) {
                    result.add(line);
                }
                //session.waitForCondition(ChannelCondition.CLOSED | ChannelCondition.EOF | ChannelCondition.EXIT_STATUS, 1000 * 3600);
                //System.out.println("ExitCode: " + session.getExitStatus()); //得到脚本运行成功与否的标志 ：0 成功,非0 失败
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                if (session != null) {
                    session.close();
                }
            }
        }
        else{
            System.out.println("NULL");
        }
        return result;
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
    //[内存]采样
    public void sampleMemory(){
        String command = "head -n 5 /proc/meminfo";
        for(int i=0;i<hostConfigInfoList.size();i++){
            List<String> commandResult = runCommand(command, hostConfigInfoList.get(i));
            HostInfo currentHostInfo = hostInfoList.get(i);

            currentHostInfo.memTotal = Integer.parseInt(commandResult.get(0).split("\\s+")[1]);
            currentHostInfo.memAvaliable = Integer.parseInt(commandResult.get(2).split("\\s+")[1]);
        }
    }
    //[磁盘]采样
    public void sampleDisk(){
        String command = "cat /proc/diskstats";
        for(int i=0;i<hostConfigInfoList.size();i++){
            List<String> commandResult = runCommand(command, hostConfigInfoList.get(i));
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

    public String getHostIpList(){
        JSONArray jsonArray = new JSONArray();
        for(HostInfo hostInfo:hostInfoList){
            jsonArray.add(hostInfo.ip);
        }
        return jsonArray.toJSONString();
    }

    //---------多线程执行
    //多线程运行
    @Override
    public void run() {
        while(threadStart){
            //采样
            sampleAll();
            //获取
            /*
            for(HostInfo hostInfo:hostInfoList){
                System.out.println(hostInfo.getOutputData(interval_ms).toString());
            }
            */
            //等待
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

        try {
            Thread.sleep(10000);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        hostMonitor.stop();
    }

}
