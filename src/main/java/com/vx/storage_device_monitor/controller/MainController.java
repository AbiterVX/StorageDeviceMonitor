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

    //资源利用界面
    @RequestMapping(value ="/CheckHostInfo", method = RequestMethod.GET)
    public String getCheckHostInfoPage(){
        return "html/CheckHostInfo.html";
    }

    //磁盘故障界面
    @RequestMapping(value ="/CheckDiskFailureInfo", method = RequestMethod.GET)
    public String getCheckDiskFailureInfoPage(){
        return "html/CheckDiskFailureInfo.html";
    }



    //测试界面
    @RequestMapping(value ="/Test", method = RequestMethod.GET)
    public String Test(){
        return "html/Test.html";
    }

    //资源利用率对比界面
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
    public String postGetRecentHostInfoList(@RequestBody Map<String,String> map){
        int index=Integer.parseInt(map.get("index"));
        String result = service_implementation.getFullRecordsByIP(service_implementation.getHostIp(index),2);
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
        String result=service_implementation.getAllDeviceCpuUsage(2);
        System.out.println(result);
        return result;
    }
    @ResponseBody
    @RequestMapping(value="/getFieldInfoList",method=RequestMethod.POST)
    public String postGetFieldInfoList(@RequestBody Map<String,String> map){
        int index=Integer.parseInt(map.get("index"));
        String field=map.get("field");
        String result=service_implementation.getRecentInfoByIp(service_implementation.getHostIp(index),2,FieldType.fromString(field));
        System.out.println(result);
        return result;
    }
}
