package com.vx.storage_device_monitor.controller;

import com.alibaba.fastjson.JSON;
import com.vx.storage_device_monitor.dao.entity.FieldType;
import com.vx.storage_device_monitor.service.Service_Implementation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TestController {
    @Autowired
    Service_Implementation service_implementation;

    @ResponseBody
    @RequestMapping(value = "/test",method = RequestMethod.GET)
    public String test1(String ip){
        return service_implementation.getRecentInfoByIp("39.105.123.116",1, FieldType.CPUUSAGE)+
                service_implementation.getRecentInfoByIp("39.105.123.116",1, FieldType.DISKUSAGE)+
                service_implementation.getRecentInfoByIp("39.105.123.116",1, FieldType.MEMORYUSAGE)+
                service_implementation.getRecentInfoByIp("39.105.123.116",1, FieldType.ENERGY)+
        service_implementation.getRecentInfoByIp("39.105.123.116",1, FieldType.INPUTNUMBER)+
        service_implementation.getRecentInfoByIp("39.105.123.116",1, FieldType.OUTPUTNUMBER)+
        service_implementation.getRecentInfoByIp("39.105.123.116",1, FieldType.TEMPERATURE)+
        service_implementation.getRecentInfoByIp("39.105.123.116",1, FieldType.RECEIVEBANDWIDTH)+
        service_implementation.getRecentInfoByIp("39.105.123.116",1, FieldType.TRANSMITBANDWIDTH)+
                service_implementation.getRecentInfoByIp("39.105.123.116",1, FieldType.ALLFIELDS);
    }

    @ResponseBody
    @RequestMapping(value = "/test2",method = RequestMethod.GET)
    public String test2(String ip){
        return JSON.toJSONString(service_implementation.getBWInTimeRange("39.105.123.116",1))+
                JSON.toJSONString(service_implementation.getIOInTimeRange("39.105.123.116",1))+
                service_implementation.getSingleNewestInfoByIp("223.4.179.227");
    }

    @ResponseBody
    @RequestMapping(value = "/test3",method = RequestMethod.GET)
    public String test3(String ip){
        return service_implementation.getFullRecordsByIP("39.105.123.116",2);
    }

    @ResponseBody
    @RequestMapping(value = "/test4",method = RequestMethod.GET)
    public String test4(String ip){
        String result=service_implementation.getDiskFailureList("192.168.0.0");
        System.out.println(result);
        return result;
    }
}
