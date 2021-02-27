package com.vx.storage_device_monitor.utils;

import com.jcraft.jsch.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SSHManager 的 Jsch实现方式
 * 与Ethz相比具有更强的稳定性
 */
public class JschSSHManager implements SSHManager{
    //JSCH
    JSch mainJSCH;
    //JSCH ssh Session List
    private Map<String, Session> sessionMap;

    public JschSSHManager(){
        mainJSCH = new JSch();
        sessionMap= new HashMap<>();
    }

    //获得Session
    private Session getJSCHSession(HostConfigInfo hostConfigInfo){
        boolean sessionExist = sessionMap.containsKey(hostConfigInfo.ip);
        com.jcraft.jsch.Session currentSession = null;
        try {
            if(sessionExist){
                currentSession = sessionMap.get(hostConfigInfo.ip);
            }
            else{
                //创建session并且打开连接，因为创建session之后要主动打开连接
                currentSession = mainJSCH.getSession(hostConfigInfo.username, hostConfigInfo.ip, 22);
                currentSession.setPassword(hostConfigInfo.password);
                //session.setUserInfo(userInfo);
                java.util.Properties config = new java.util.Properties();
                config.put("StrictHostKeyChecking", "no");
                currentSession.setConfig(config);
                currentSession.connect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return currentSession;
    }

    //执行JSCH指令
    @Override
    public List<String> runCommand(String command, HostConfigInfo hostConfigInfo){
        List<String> result = new ArrayList<String>();

        int returnCode = 0;
        com.jcraft.jsch.Session session = getJSCHSession(hostConfigInfo);
        try {
            //打开通道，设置通道类型，和执行的命令
            Channel channel = session.openChannel("exec");
            ChannelExec channelExec = (ChannelExec)channel;
            channelExec.setCommand(command);

            channelExec.setInputStream(null);
            BufferedReader input = new BufferedReader(new InputStreamReader(channelExec.getInputStream()));
            channelExec.connect();

            //接收远程服务器执行命令的结果
            String line;
            while ((line = input.readLine()) != null) {
                result.add(line);
            }
            input.close();

            // 得到returnCode
            if (channelExec.isClosed()) {
                returnCode = channelExec.getExitStatus();
            }

            // 关闭通道
            channelExec.disconnect();
            //关闭session
            //session.disconnect();

        } catch (JSchException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
