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
    private HostMonitor hostMonitor;
    public MainController(){
        hostMonitor = new HostMonitor();
    }

    @RequestMapping(value ="/", method = RequestMethod.GET)
    public String get_HomePage(){
        return "html/HomePage.html";
    }

    @RequestMapping(value ="Test", method = RequestMethod.GET)
    public String Test(){
        return "html/Test.html";
    }

    @ResponseBody
    @RequestMapping(value = "/Command", method = RequestMethod.POST)
    public Map<String, String> post_RunCommand(@RequestBody Map<String, String> map){
        //配置信息
        HostConfigInfo hostConfigInfo =new HostConfigInfo("39.105.123.116","root","576412173");
        //指令
        String command=map.get("command");
        //返回结果
        Map<String, String> resultMap=new HashMap<>();
        //执行指令
        //hostMonitor.getStorageDeviceInfoList();

        List<String> result = hostMonitor.runCommand(command, hostConfigInfo);

        //resultMap.put("result",result.toString());
        return resultMap;
    }
}
