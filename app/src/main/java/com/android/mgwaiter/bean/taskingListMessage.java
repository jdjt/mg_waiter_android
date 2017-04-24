package com.android.mgwaiter.bean;

import java.util.Date;

/**
 * Created by hp on 2017/4/22.
 */

public class taskingListMessage {
private String taskContent;// 任务内容
    private int taskStatus;// 任务状态
    private long hotelCode;// 所属酒店
    private String areaCode;// 区域编码
    private String areaName;// 区域名称
    private String floorNo;// 呼叫楼层
    private String mapNo;//地图编号
    private String posionX;//坐标X
    private String positionY;// 坐标Y
    private String postionZ;// 坐标Z
    private long customerId;//发单顾客编号
    private long waiterId;//接单服务员编号
    private String wImAccount;//接单服务员Im账号
    private Date nowDate;//系统时间
    private Date waiteTime;//等候时间
    private Date waiterEndTime;//等候截至时间
    private Date acceptTime;//接单时间
    private Date finishTime;//服务员完成时间
    private Date finishEndTime;//服务员完成截至时间




}
