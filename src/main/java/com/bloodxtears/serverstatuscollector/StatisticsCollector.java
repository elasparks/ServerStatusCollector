package com.bloodxtears.serverstatuscollector;

import com.bloodxtears.serverstatuscollector.dao.StatisticsDAO;
import com.bloodxtears.serverstatuscollector.models.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

@Component
public class StatisticsCollector extends TimerTask {
    final private static byte dnsPort = 53;
    final private static short timeout = 5000;
    final private static long HOUR = 1000 * 60 * 60;
    private final Timer timer;
    private final SystemInfo systemInfo;
    private final SimpleDateFormat dateFormat;
    private final SimpleDateFormat timeFormat;
    private final StatisticsDAO statisticsDAO;

    @Autowired
    public StatisticsCollector(StatisticsDAO statisticsDAO) {
        this.statisticsDAO = statisticsDAO;
        this.systemInfo = new SystemInfo();
        this.timer = new Timer(true);
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        timeFormat = new SimpleDateFormat("HH:mm:ss");
        timer.scheduleAtFixedRate(this, 0, HOUR);
    }

    @Override
    public void run() {
        Date date = new Date();
        Status status = new Status();
        status.setDate(dateFormat.format(date));
        status.setTime(timeFormat.format(date));
        status.setCpuUsagePercent(getCpuUsage());
        status.setRamUsagePercent(getRamUsage());
        status.setHardDriveFree(getHardDriveFreeSpace());
        status.setDnsConnectionTime(getAverageDNSConnectionTime());
    }

    private double getCpuUsage() {
        CentralProcessor cpu = systemInfo.getHardware().getProcessor();
        long[] prevTicks = cpu.getSystemCpuLoadTicks();
        try {
            Thread.sleep(500);
        } catch (InterruptedException ignored) {
        }
        return cpu.getSystemCpuLoadBetweenTicks(prevTicks) * 100;
    }

    private double getRamUsage(){
        GlobalMemory ram = systemInfo.getHardware().getMemory();
        long totalRam = ram.getTotal();
        long availableRam = ram.getTotal();
        long usedRam = totalRam - availableRam;
        return 100.0 / totalRam * usedRam;
    }

    private double getHardDriveFreeSpace(){
        var partition = systemInfo.getOperatingSystem().getFileSystem().getFileStores().get(0);
        return partition.getFreeSpace() / 1024.0 / 1024.0;
    }

    private double getAverageDNSConnectionTime() {
        Properties prop = new Properties();
        String dnsIP = "8.8.8.8";
        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("application.properties")) {
            prop.load(inputStream);
            dnsIP = prop.getProperty("dns", "8.8.8.8");
        } catch (IOException e) {
            e.printStackTrace(System.out);
            System.exit(1);
        }

        byte requests = 10;
        double allTime = 0;
        for (byte i = 0; i < requests; ++i) {
            try (Socket socket = new Socket()) {
                double time = System.currentTimeMillis();
                socket.connect(new InetSocketAddress(dnsIP, dnsPort), timeout);
                time = (System.currentTimeMillis() - time);
                allTime += time;
            } catch (IOException e) {
                return -1;
            }
        }
        return allTime / requests;
    }
}
