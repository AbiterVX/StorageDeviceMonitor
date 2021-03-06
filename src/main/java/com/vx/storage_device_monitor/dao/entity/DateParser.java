package com.vx.storage_device_monitor.dao.entity;

import java.util.Calendar;
import java.util.StringTokenizer;

public class DateParser implements Comparable<DateParser>{
    private String originalFormat;
    private Calendar calendarFormat;
    private String[] index={"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
    public DateParser(String originalFormat) {
        this.originalFormat = originalFormat;
        this.calendarFormat=Calendar.getInstance();
        String[] tokens=getToken();
        for(int i=0;i<12;i++){
            if(index[i].equals(tokens[0])){
                calendarFormat.set(Calendar.MONTH,i);
            }
        }
        calendarFormat.set(Calendar.DAY_OF_MONTH,Integer.parseInt(tokens[1]));
        calendarFormat.set(Calendar.HOUR_OF_DAY,Integer.parseInt(tokens[2]));
        calendarFormat.set(Calendar.MINUTE,Integer.parseInt(tokens[3]));
        calendarFormat.set(Calendar.SECOND,Integer.parseInt(tokens[4]));
    }
    public String[] getToken(){
        return originalFormat.split("\\s+|:");
    }

    public String getOriginalFormat() {
        return originalFormat;
    }

    public Calendar getCalendarFormat() {
        return calendarFormat;
    }

    @Override
    public int compareTo(DateParser o) {
        return this.getCalendarFormat().compareTo(o.getCalendarFormat());
    }
    public static void main(String[] args){
        for(String string:new DateParser("Mar  1 05:50:26").getToken()){
            System.out.println(string);
        }
    }
}
