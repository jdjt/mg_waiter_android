package com.android.mgwaiter.bean;

/**
 * Created by hp on 2017/4/22.
 */

public class masterMessage {
    private long hotelCode;//所属酒店
    private String hotelName;//酒店名称
    private String empNo;//员工号
    private String name;//员工姓名
    private int  sex;//性别
    private String phone;//手机号
    private long depId;//部门id
    private String depName;//部门名称
    private String idNo;//身份证号
    private int resetPwdDiv;//密码重置区分
    private String imAccount;//即时通讯账号
    private String deviceId;//登录设备终端id
    private String deviceToken;//登录设备终端通讯Id
    private int deviceType;//登录设备类型
    private int workStatus;//工作状态 0-挂起(默认); 1 任务中;2-待命
    private int attendStatus;//上下班状态 0：下班 1;上班
    private String workTimeCal;//上班时长

    public long getHotelCode() {
        return hotelCode;
    }

    public String getHotelName() {
        return hotelName;
    }

    public String getEmpNo() {
        return empNo;
    }

    public String getName() {
        return name;
    }

    public int getSex() {
        return sex;
    }

    public String getPhone() {
        return phone;
    }

    public long getDepId() {
        return depId;
    }

    public String getDepName() {
        return depName;
    }

    public String getIdNo() {
        return idNo;
    }

    public int getResetPwdDiv() {
        return resetPwdDiv;
    }

    public String getImAccount() {
        return imAccount;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public int getWorkStatus() {
        return workStatus;
    }

    public int getAttendStatus() {
        return attendStatus;
    }

    public String getWorkTimeCal() {
        return workTimeCal;
    }

    public void setHotelCode(long hotelCode) {
        this.hotelCode = hotelCode;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public void setEmpNo(String empNo) {
        this.empNo = empNo;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setDepId(long depId) {
        this.depId = depId;
    }

    public void setDepName(String depName) {
        this.depName = depName;
    }

    public void setIdNo(String idNo) {
        this.idNo = idNo;
    }

    public void setResetPwdDiv(int resetPwdDiv) {
        this.resetPwdDiv = resetPwdDiv;
    }

    public void setImAccount(String imAccount) {
        this.imAccount = imAccount;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public void setWorkStatus(int workStatus) {
        this.workStatus = workStatus;
    }

    public void setAttendStatus(int attendStatus) {
        this.attendStatus = attendStatus;
    }

    public void setWorkTimeCal(String workTimeCal) {
        this.workTimeCal = workTimeCal;
    }

    @Override
    public String toString() {
        return "masterMessage{" +
                "hotelCode=" + hotelCode +
                ", hotelName='" + hotelName + '\'' +
                ", empNo='" + empNo + '\'' +
                ", name='" + name + '\'' +
                ", sex=" + sex +
                ", phone='" + phone + '\'' +
                ", depId=" + depId +
                ", depName='" + depName + '\'' +
                ", idNo='" + idNo + '\'' +
                ", resetPwdDiv=" + resetPwdDiv +
                ", imAccount='" + imAccount + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", deviceToken='" + deviceToken + '\'' +
                ", deviceType=" + deviceType +
                ", workStatus=" + workStatus +
                ", attendStatus=" + attendStatus +
                ", workTimeCal='" + workTimeCal + '\'' +
                '}';
    }

    public masterMessage() {

    }

}
