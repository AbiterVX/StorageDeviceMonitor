package com.vx.storage_device_monitor.utils;

import org.python.util.PythonInterpreter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class PythonManager {
    public void startScript(String command){
        Process proc;
        try {
            proc=Runtime.getRuntime().exec(command);
            BufferedReader in =new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line =null;
            while((line=in.readLine())!=null){
                System.out.println(line);
            }
            in.close();
            proc.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args){
        new PythonManager().startScript("python D:\\test.py");
    }
}
