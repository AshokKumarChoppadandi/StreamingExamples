package com.bigdata.kafka.producer.edgar_logs;

public class EdgarLog {
    // ip,date,time,zone,cik,accession,extention,code,size,idx,norefer,noagent,find,crawler,browser

    public String ipAddress;
    public String date;
    public String time;
    public String zone;
    public String cik;
    public String accession;
    public String extention;
    public String code;
    public String size;
    public String idx;
    public String noRefer;
    public String noAgent;
    public String find;
    public String crawler;
    public String browser;

    public EdgarLog() {

    }

    public EdgarLog(String[] messageFields) {
        if(messageFields.length == 15) {
            this.browser = messageFields[14];
        } else {
            this.browser = "null";
        }
        this.ipAddress = messageFields[0];
        this.date = messageFields[1];
        this.time = messageFields[2];
        this.zone = messageFields[3];
        this.cik = messageFields[4];
        this.accession = messageFields[5];
        this.extention = messageFields[6];
        this.code = messageFields[7];
        this.size = messageFields[8];
        this.idx = messageFields[9];
        this.noRefer = messageFields[10];
        this.noAgent = messageFields[11];
        this.find = messageFields[12];
        this.crawler = messageFields[13];
    }

    public EdgarLog(String ipAddress, String date, String time, String zone, String cik, String accession, String extention, String code, String size, String idx, String noRefer, String noAgent, String find, String crawler, String browser) {
        this.ipAddress = ipAddress;
        this.date = date;
        this.time = time;
        this.zone = zone;
        this.cik = cik;
        this.accession = accession;
        this.extention = extention;
        this.code = code;
        this.size = size;
        this.idx = idx;
        this.noRefer = noRefer;
        this.noAgent = noAgent;
        this.find = find;
        this.crawler = crawler;
        this.browser = browser;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

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

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getCik() {
        return cik;
    }

    public void setCik(String cik) {
        this.cik = cik;
    }

    public String getAccession() {
        return accession;
    }

    public void setAccession(String accession) {
        this.accession = accession;
    }

    public String getExtention() {
        return extention;
    }

    public void setExtention(String extention) {
        this.extention = extention;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getIdx() {
        return idx;
    }

    public void setIdx(String idx) {
        this.idx = idx;
    }

    public String getNoRefer() {
        return noRefer;
    }

    public void setNoRefer(String noRefer) {
        this.noRefer = noRefer;
    }

    public String getNoAgent() {
        return noAgent;
    }

    public void setNoAgent(String noAgent) {
        this.noAgent = noAgent;
    }

    public String getFind() {
        return find;
    }

    public void setFind(String find) {
        this.find = find;
    }

    public String getCrawler() {
        return crawler;
    }

    public void setCrawler(String crawler) {
        this.crawler = crawler;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    @Override
    public String toString() {
        return "EdgarLog{" +
                "ipAddress='" + ipAddress + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", zone='" + zone + '\'' +
                ", cik='" + cik + '\'' +
                ", accession='" + accession + '\'' +
                ", extention='" + extention + '\'' +
                ", code='" + code + '\'' +
                ", size='" + size + '\'' +
                ", idx='" + idx + '\'' +
                ", noRefer='" + noRefer + '\'' +
                ", noAgent='" + noAgent + '\'' +
                ", find='" + find + '\'' +
                ", crawler='" + crawler + '\'' +
                ", browser='" + browser + '\'' +
                '}';
    }
}
