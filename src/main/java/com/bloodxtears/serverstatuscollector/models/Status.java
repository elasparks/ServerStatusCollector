package com.bloodxtears.serverstatuscollector.models;

public class Status {
    private String date;
    private String time;
    private double cpuUsagePercent;
    private double ramUsagePercent;
    private double hardDriveFree;
    private double dnsConnectionTime;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public double getCpuUsagePercent() {
        return cpuUsagePercent;
    }

    public void setCpuUsagePercent(double cpuUsagePercent) {
        this.cpuUsagePercent = cpuUsagePercent;
    }

    public double getRamUsagePercent() {
        return ramUsagePercent;
    }

    public void setRamUsagePercent(double ramUsagePercent) {
        this.ramUsagePercent = ramUsagePercent;
    }

    public double getHardDriveFree() {
        return hardDriveFree;
    }

    public void setHardDriveFree(double hardDriveFree) {
        this.hardDriveFree = hardDriveFree;
    }

    public double getDnsConnectionTime() {
        return dnsConnectionTime;
    }

    public void setDnsConnectionTime(double dnsConnectionTime) {
        this.dnsConnectionTime = dnsConnectionTime;
    }
}
