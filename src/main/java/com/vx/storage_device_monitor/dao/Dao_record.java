package com.vx.storage_device_monitor.dao;


import com.vx.storage_device_monitor.dao.entity.BWrecord;
import com.vx.storage_device_monitor.dao.entity.IOrecord;
import com.vx.storage_device_monitor.dao.entity.record;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.sql.Timestamp;
import java.util.List;

@Mapper
public interface Dao_record {
    @Insert("insert into NodeRecord values\n"+
            "(#{ip},#{timestamp},#{receiveBW},#{transmitBW},#{cpuUsage},#{memoryUsage},#{diskUsage},#{iNumber},#{oNumber},#{temp},#{energy})"
            )
    void insertNewRecord(@Param("ip") String ip,
                         @Param("timestamp")Timestamp timestamp,
                         @Param("receiveBW")float receiveBW,
                         @Param("transmitBW")float transmitBW,
                         @Param("cpuUsage")float cpuUsage,
                         @Param("memoryUsage")float memoryUsage,
                         @Param("diskUsage")float diskUsage,
                         @Param("iNumber")int iNumber,
                         @Param("oNumber")int oNumber,
                         @Param("temp")float temp,
                         @Param("energy")float energy
                         );
    @Select("select * from NodeRecord where ip=#{ip}")
    List<record> recordQueryWithIP(@Param("ip") String ip);

    @Select("select * from NodeRecord " +
            "where ip=#{ip} and timestamp > #{lowbound} and timestamp < #{highbound} " +
            "order by timestamp " )
    List<record> recordQueryWithTimestamp(@Param("lowbound")Timestamp lowbound,@Param("highbound") Timestamp highbound,@Param("ip") String ip);

    @Select("select receiveBW,transmitBW,timestamp from NodeRecord " +
            "where ip=#{ip} and timestamp>#{lowbound} and timestamp<#{highbound}" +
            "order by timestamp")
    List<BWrecord> getBWWithTimestamp(@Param("lowbound")Timestamp lowbound, @Param("highbound") Timestamp highbound, @Param("ip") String ip);

    @Select("select transmitBW from NodeRecord " +
            "where ip=#{ip} and timestamp > #{lowbound} and timestamp < #{highbound} " +
            "order by timestamp"  )
    List<Float> getTransmitBWWithTimestamp(@Param("lowbound")Timestamp lowbound,@Param("highbound") Timestamp highbound,@Param("ip") String ip);

    @Select("select memoryUsage from NodeRecord " +
            "where ip=#{ip} and timestamp > #{lowbound} and timestamp < #{highbound} " +
            "order by timestamp" )
    List<Float> getMemoryUsageWithTimestamp(@Param("lowbound")Timestamp lowbound,@Param("highbound") Timestamp highbound,@Param("ip") String ip);

    @Select("select diskUsage from NodeRecord " +
            "where ip=#{ip} and timestamp > #{lowbound} and timestamp < #{highbound} " +
            "order by timestamp" )
    List<Float> getDiskUsageWithTimestamp(@Param("lowbound")Timestamp lowbound,@Param("highbound") Timestamp highbound,@Param("ip") String ip);

    @Select("select temp from NodeRecord " +
            "where ip=#{ip} and timestamp > #{lowbound} and timestamp < #{highbound} " +
            "order by timestamp" )
    List<Float> getTempWithTimestamp(@Param("lowbound")Timestamp lowbound,@Param("highbound") Timestamp highbound,@Param("ip") String ip);

    @Select("select cpuUsage from NodeRecord " +
            "where ip=#{ip} and timestamp > #{lowbound} and timestamp < #{highbound} " +
            "order by timestamp" )
    List<Float> getCpuUsageWithTimestamp(@Param("lowbound")Timestamp lowbound,@Param("highbound") Timestamp highbound,@Param("ip") String ip);

    @Select("select energy from NodeRecord " +
            "where ip=#{ip} and timestamp > #{lowbound} and timestamp < #{highbound} " +
            "order by timestamp" )
    List<Float> getEnergyWithTimestamp(@Param("lowbound")Timestamp lowbound,@Param("highbound") Timestamp highbound,@Param("ip") String ip);

    @Select("select iNumber,oNumber,timestamp from NodeRecord " +
            "where ip=#{ip} and timestamp>#{lowbound} and timestamp<#{highbound}" +
            "order by timestamp")
    List<IOrecord> getIOWithTimestamp(@Param("lowbound")Timestamp lowbound,@Param("highbound") Timestamp highbound,@Param("ip") String ip);





}
