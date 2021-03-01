package com.vx.storage_device_monitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class StorageDeviceMonitorApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(StorageDeviceMonitorApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder){
        return builder.sources(this.getClass());
    }
}

/*


echo "Stopping storage_device_monitor-0.0.1-SNAPSHOT"
pid=`ps -ef | grep storage_device_monitor-0.0.1-SNAPSHOT.jar | grep -v grep | awk '{print $2}'`
if [ -n "$pid" ]
then
   kill -9 $pid
fi
cp /root/.jenkins/workspace/Spirit/spirits/target/spirits-0.0.1-SNAPSHOT.jar /AbiterVX_APP/Spirits
DontKillMe nohup java -jar ./storage_device_monitor-0.0.1-SNAPSHOT.jar &
 */