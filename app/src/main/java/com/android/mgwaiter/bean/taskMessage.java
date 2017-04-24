package com.android.mgwaiter.bean;

import java.util.Date;

/**
 * Created by hp on 2017/4/24.
 */

public class taskMessage {
    private String taskContent;//任务内容
    private int taskStatus;//任务状态
    private long hotelCode;//所属酒店
    private String areaCode;//区域编码
    private String areaName;//区域名称
    private String floorNo;// 呼叫楼层
    private String mapNo;//地图编号
    private String posionX;//坐标X
    private String positionY;// 坐标Y
    private String postionZ;// 坐标Z
    private long customerId;//发单顾客编号
    private String cImAccount;//发单顾客Im账号
    private long waiterId;//接单服务员编号
    private String wImAccount;//接单服务员Im账号
    private Date nowDate;//系统时间
    private Date waiteTime;//等候时间
    private Date waiterEndTime;//等候截至时间
    private Date acceptTime;//接单时间
    private Date finishTime;//服务员完成时间
    private Date finishEndTime;//服务员完成截至时间
    private long taskCode; //任务编号
    public String getTaskContent() {
        return taskContent;
    }

    public long getTaskCode() {
        return taskCode;
    }

    public void setTaskCode(long taskCode) {
        this.taskCode = taskCode;
    }

    public void setTaskContent(String taskContent) {
        this.taskContent = taskContent;
    }

    public int getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(int taskStatus) {
        this.taskStatus = taskStatus;
    }

    public long getHotelCode() {
        return hotelCode;
    }

    public void setHotelCode(long hotelCode) {
        this.hotelCode = hotelCode;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getFloorNo() {
        return floorNo;
    }

    public void setFloorNo(String floorNo) {
        this.floorNo = floorNo;
    }

    public String getMapNo() {
        return mapNo;
    }

    public void setMapNo(String mapNo) {
        this.mapNo = mapNo;
    }

    public String getPosionX() {
        return posionX;
    }

    public void setPosionX(String posionX) {
        this.posionX = posionX;
    }

    public String getPositionY() {
        return positionY;
    }

    public void setPositionY(String positionY) {
        this.positionY = positionY;
    }

    public String getPostionZ() {
        return postionZ;
    }

    public void setPostionZ(String postionZ) {
        this.postionZ = postionZ;
    }

    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public String getcImAccount() {
        return cImAccount;
    }

    public void setcImAccount(String cImAccount) {
        this.cImAccount = cImAccount;
    }

    public long getWaiterId() {
        return waiterId;
    }

    public void setWaiterId(long waiterId) {
        this.waiterId = waiterId;
    }

    public String getwImAccount() {
        return wImAccount;
    }

    public void setwImAccount(String wImAccount) {
        this.wImAccount = wImAccount;
    }

    public Date getNowDate() {
        return nowDate;
    }

    public void setNowDate(Date nowDate) {
        this.nowDate = nowDate;
    }

    public Date getWaiteTime() {
        return waiteTime;
    }

    public void setWaiteTime(Date waiteTime) {
        this.waiteTime = waiteTime;
    }

    public Date getWaiterEndTime() {
        return waiterEndTime;
    }

    public void setWaiterEndTime(Date waiterEndTime) {
        this.waiterEndTime = waiterEndTime;
    }

    public Date getAcceptTime() {
        return acceptTime;
    }

    public void setAcceptTime(Date acceptTime) {
        this.acceptTime = acceptTime;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    public Date getFinishEndTime() {
        return finishEndTime;
    }

    public void setFinishEndTime(Date finishEndTime) {
        this.finishEndTime = finishEndTime;
    }


}
