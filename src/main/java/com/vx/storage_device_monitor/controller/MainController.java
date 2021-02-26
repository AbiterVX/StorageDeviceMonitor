package com.vx.storage_device_monitor.controller;


import com.vx.storage_device_monitor.utils.HostConfigInfo;
import com.vx.storage_device_monitor.utils.HostMonitor;
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
    private HostMonitor hostMonitor;
    public MainController(){
        hostMonitor = new HostMonitor(2000);
        //后台开启线程：用于数据采样
        hostMonitor.start();
    }

    //index界面/主界面
    @RequestMapping(value ="/", method = RequestMethod.GET)
    public String get_HomePage(){
        return "html/HomePage.html";
    }

    //测试界面
    @RequestMapping(value ="Test", method = RequestMethod.GET)
    public String Test(){
        return "html/Test.html";
    }

    //主机数据获取
    @ResponseBody
    @RequestMapping(value = "/getHostInfoList", method = RequestMethod.POST)
    public String postGetHostInfoList(){
        String result = hostMonitor.getHostInfoListOutputData();
        System.out.println(result);
        return result;
    }

    //主机IP获取
    @ResponseBody
    @RequestMapping(value = "/getHostIpList", method = RequestMethod.POST)
    public String postGetHostIpList(){
        String result = hostMonitor.getHostIpList();
        System.out.println(result);
        return result;
    }


}
