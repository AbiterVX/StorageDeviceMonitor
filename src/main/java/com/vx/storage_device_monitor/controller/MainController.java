package com.vx.storage_device_monitor.controller;


import com.vx.storage_device_monitor.dao.entity.FieldType;
import com.vx.storage_device_monitor.dao.entity.Record;
import com.vx.storage_device_monitor.service.Service_Implementation;
import com.vx.storage_device_monitor.utils.HostConfigInfo;
import com.vx.storage_device_monitor.utils.HostMonitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

@Controller
public class MainController {
    //主机监控类
    //private HostMonitor hostMonitor;
    @Autowired
    Service_Implementation service_implementation;
    public MainController(){
        //hostMonitor = new HostMonitor(5000);
        //后台开启线程：用于数据采样
        //hostMonitor.start();
    }

    //index界面/主界面
    @RequestMapping(value ="/", method = RequestMethod.GET)
    public String get_HomePage(){
        return "html/HomePage.html";
    }

    //index界面/主界面
    @RequestMapping(value ="/HomePage", method = RequestMethod.GET)
    public String getHomePage(){
        return "html/HomePage.html";
    }

    //[界面]主机信息
    @RequestMapping(value ="/HostInfo", method = RequestMethod.GET)
    public String getHostInfoPage(){
        return "html/HostInfo.html";
    }

    //[界面]进程IO信息
    @RequestMapping(value ="/ProcessIOInfo", method = RequestMethod.GET)
    public String getProcessIOInfoPage(){
        return "html/ProcessIOInfo.html";
    }

    //[界面]磁盘故障预测
    @RequestMapping(value ="/DiskFailurePredict", method = RequestMethod.GET)
    public String getDiskFailurePredictPage(){
        return "html/DiskFailurePredict.html";
    }

    //[界面]IO测试
    @RequestMapping(value ="/IOTest", method = RequestMethod.GET)
    public String getIOTestPage(){
        return "html/IOTest.html";
    }


    //测试界面
    @RequestMapping(value ="/Test", method = RequestMethod.GET)
    public String Test(){
        return "html/Test.html";
    }

    //[废弃]资源利用率对比界面
    @RequestMapping(value ="/CheckHostInfoComparison", method = RequestMethod.GET)
    public String getCheckHostInfoComparison(){
        return "html/CheckHostInfoComparison.html";
    }





    //主机数据获取
    @ResponseBody
    @RequestMapping(value = "/getHostInfoList", method = RequestMethod.POST)
    public String postGetHostInfoList(){
        String result = service_implementation.getHostInfoListOutputData();
        System.out.println(result);
        return result;
    }

    //主机IP获取
    @ResponseBody
    @RequestMapping(value = "/getHostIpList", method = RequestMethod.POST)
    public String postGetHostIpList(){
        String result = service_implementation.getHostIpList();
        System.out.println(result);
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/getHostHardWareList", method = RequestMethod.POST)
    public String postGetHostHardwareList(){
        String result = service_implementation.getHostHardwareInfoListOutputData();
        System.out.println(result);
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/getRecentHostInfoList", method = RequestMethod.POST)
    public String postGetRecentHostInfoList(@RequestBody Map<String,Integer> map){
        int index=map.get("index");
        int minute=map.get("dateInterval");
        String result = service_implementation.getFullRecordsByIP(service_implementation.getHostIp(index),minute);
        return result;
    }
    @ResponseBody
    @RequestMapping(value="/getDiskFailureList", method=RequestMethod.POST)
    public String postGetDiskFailure(@RequestBody Map<String,String> map){
        int index=Integer.parseInt(map.get("index"));
        String result=service_implementation.getDiskFailureList(service_implementation.getHostIp(index));
        System.out.println(result);
        return result;
    }
    @ResponseBody
    @RequestMapping(value="/getAllDeviceInfo",method=RequestMethod.POST)
    public String postGetAllDeviceCpuUsage(@RequestBody Map<String,String> map){
        int hour=Integer.parseInt(map.get("dateInterval"));
        String result=service_implementation.getAllDeviceUsage(hour);
        System.out.println(result);
        return result;
    }

    @ResponseBody
    @RequestMapping(value="/getFieldInfoList",method=RequestMethod.POST)
    public String postGetFieldInfoList(@RequestBody Map<String,String> map) {
        int index = Integer.parseInt(map.get("index"));
        int hour=Integer.parseInt(map.get("dateInterval"));
        String field = map.get("field");
        String result = service_implementation.getRecentInfoByIp(service_implementation.getHostIp(index), hour, FieldType.fromString(field));
        return result;
    }

    @ResponseBody
    @RequestMapping(value="/getHostState",method=RequestMethod.POST)
    public String postGetHostState(){
        String result=service_implementation.getHostState();
        System.out.println(result);
        return result;
    }
}
