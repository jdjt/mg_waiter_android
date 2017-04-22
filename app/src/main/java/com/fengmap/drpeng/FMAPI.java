package com.fengmap.drpeng;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.fengmap.android.analysis.navi.FMNaviResult;
import com.fengmap.android.map.geometry.FMMapCoord;
import com.fengmap.android.map.layer.FMImageLayer;
import com.fengmap.android.map.layer.FMLineLayer;
import com.fengmap.android.map.layer.FMLocationLayer;
import com.fengmap.android.map.marker.FMImageMarker;
import com.fengmap.android.map.marker.FMLineMarker;
import com.fengmap.android.map.marker.FMLocationMarker;
import com.fengmap.android.map.marker.FMSegment;
import com.fengmap.android.map.style.FMImageMarkerStyle;
import com.fengmap.android.map.style.FMLineMarkerStyle;
import com.fengmap.android.map.style.FMLocationMarkerStyle;
import com.fengmap.android.map.style.FMStyle;
import com.fengmap.android.utils.FMMath;
import com.fengmap.android.wrapmv.FMActivityManager;
import com.fengmap.android.wrapmv.FMRouteManager;
import com.fengmap.android.wrapmv.FMZoneManager;
import com.fengmap.android.wrapmv.service.FMLocationService;
import com.fengmap.drpeng.widget.CustomToast;

import java.util.ArrayList;

/**
 * 蜂鸟业务API。
 * Created by yangbin on 16/7/2.
 */
public class FMAPI {
    private static FMAPI mAPI = null;

    // 跳转界面的标记符
    public static final String ACTIVITY_WHERE = "map_where";
    // 带mapid的跳转
    public static final String ACTIVITY_MAP_ID = "map_id";
    // 带着酒店名字跳转
    public static final String ACTIVITY_HOTEL_NAME = "hotel_name";
    // 带着groupId跳转
    public static final String ACTIVITY_MAP_GROUP_ID = "map_group_id";
    // 带着定位位置的跳转
    public static final String ACTIVITY_LOCATE_POSITION = "locate_position";
    // 带目的的跳转
    public static final String ACTIVITY_TARGET = "map_target";

    // 添加标注
    public static final String TARGET_ADD_MARKER = "target_add_marker";
    // 路径规划
    public static final String TARGET_CALCULATE_ROUTE = "target_calculate_route";
    // 地图选点
    public static final String TARGET_SELECT_POINT = "target_select_point";
    // 地图定位
    public static final String TARGET_LOCATE = "target_locate";

    public static final String TARGET_FRAGMENT = "target_fragment";

    public static final String ACTIVITY_OBJ_SEARCH_HISTORY = "search_history";
    public static final String ACTIVITY_OBJ_SEARCH_RESULT = "search_result";

    public static final int LOCATE_FAILURE = 1;  //定位失败
    public static final int LOCATE_SUCCESS_TO_ARRIVE = 2;  //定位成功->到这去
    public static final int LOCATE_SUCCESS_TO_LOCATE = 3;  //定位成功->单纯定位

    public FMRouteManager    mRouteManager;          //业态路线管理器
    public FMActivityManager mActivityManager;       //业态管理器
    public FMZoneManager     mZoneManager;           //区域管理器


    public static final float COMPLETED_MIN_DISTANCE = 30;  // 最小的完成导航的距离
    public float mInitNeedDistance = 0;       // 全程距离
    public float mInitNeedTime = 0;           // 全程时间
    public float mInitNeedCalorie = 0;        // 全程消耗卡路里
    public float mWalkedDistance = 0;         // 走过的距离

    public boolean isInLocating = false;      //是否在定位
    public static boolean mNeedMapFollowInNavigation = true;        //导航模式下地图跟随
    public static final float NAVIGATION_OFFSET_MAX_DISTANCE = 5;   // 5米


    private FMAPI() {
        mActivityManager = FMActivityManager.getInstance();
        mRouteManager    = FMRouteManager.getInstance();
        mZoneManager     = FMZoneManager.getInstance();
    }


    public static synchronized FMAPI instance() {
        if (mAPI == null) {
            synchronized (FMAPI.class) {
                mAPI = new FMAPI();
            }
        }
        return mAPI;
    }


    /**
     * 处于导航模式时,过滤其他操作。
     * @param pContext
     * @return
     */
    public boolean needFilterNavigationWhenOperation(Context pContext) {
        if (FMLocationService.instance().isInNavigationMode()) {
            CustomToast.show(pContext, "请先结束导航");
            return true;
        }
        return false;
    }

    /**
     * 创建一个图片标注物。
     * @param groupId
     * @param position
     * @param picName
     * @param markerSize
     * @param offsetType
     * @param customHeight
     * @return
     */
    public FMImageMarker buildImageMarker(int groupId,
                                          FMMapCoord position,
                                          String picName,
                                          int markerSize,
                                          FMStyle.FMNodeOffsetType offsetType,
                                          float customHeight) {
        FMImageMarker m = new FMImageMarker(position);
        m.setGroupId(groupId);

        FMImageMarkerStyle s = new FMImageMarkerStyle();
        s.setFMNodeOffsetType(offsetType);
        s.setImageFromAssets(picName);
        s.setWidth(markerSize);
        s.setHeight(markerSize);
        if (offsetType == FMStyle.FMNodeOffsetType.FMNODE_CUSTOM_HEIGHT) {
            s.setCustomHeightOffset(customHeight);
        }
        m.setStyle(s);
        return m;
    }

    /**
     * 创建一个图片标注物。
     * @param groupId   层id
     * @param position  位置
     * @param picName   标注物图片名字
     * @return
     */
    public FMImageMarker buildImageMarker(int groupId,
                                          FMMapCoord position,
                                          String picName) {
        FMImageMarker m = new FMImageMarker(position);
        m.setGroupId(groupId);

        FMImageMarkerStyle s = new FMImageMarkerStyle();
        s.setFMNodeOffsetType(FMStyle.FMNodeOffsetType.FMNODE_MODEL_ABOVE);
        s.setImageFromAssets(picName);
        s.setWidth(50);
        s.setHeight(50);
        m.setStyle(s);
        return m;
    }

    /**
     * 添加图片标注物。
     * @param layer     图层
     * @param position  位置
     * @param picName   标注物图片名字
     * @return 标注物
     */
    public FMImageMarker addImageMarker(FMImageLayer layer,
                                        FMMapCoord position,
                                        String picName) {
        FMImageMarker      m = new FMImageMarker(position);
        FMImageMarkerStyle s = new FMImageMarkerStyle();
        s.setFMNodeOffsetType(FMStyle.FMNodeOffsetType.FMNODE_MODEL_ABOVE);
        s.setImageFromAssets(picName);
        s.setWidth(40);
        s.setHeight(40);
        m.setStyle(s);
        layer.addMarker(m);
        return m;
    }

    /**
     * 批量添加图片标注物，标注物的图片资源、尺寸大小一样。
     * @param layer      图层
     * @param positions  位置集合
     * @param picName    图片名称
     * @param size       大小
     */
    public void addImageMarkers(FMImageLayer layer,
                                FMMapCoord[] positions,
                                String picName,
                                int size) {
        ArrayList<FMImageMarker> markers = new ArrayList<>(positions.length);
        FMImageMarkerStyle       s       = new FMImageMarkerStyle();
        s.setImageFromAssets(picName);
        if (size<=0) {
            size = 40;
        }
        s.setWidth(size);
        s.setHeight(size);

        for (FMMapCoord c : positions) {
            FMImageMarker m = new FMImageMarker(c);
            m.setStyle(s);
            markers.add(m);
        }

        layer.addMarker(markers);
    }


    /**
     * 添加定位图标。
     * @param layer      图层
     * @param groupId    定位在哪一层
     * @param position   位置
     * @return 定位图标
     */
    public FMLocationMarker addLocationMarker(FMLocationLayer layer,
                                              int groupId,
                                              FMMapCoord position) {
        FMLocationMarkerStyle locStyle = new FMLocationMarkerStyle();
        locStyle.setActiveImageFromAssets("fmr/pointer.png");
        locStyle.setStaticImageFromAssets("fmr/dome.png");
        locStyle.setWidth(45);
        locStyle.setHeight(45);
        FMLocationMarker marker = new FMLocationMarker(groupId, position, locStyle);
        layer.addMarker(marker);
        return marker;
    }


    /**
     * 创建定位图标。
     * @param groupId    定位在哪一层
     * @param position   位置
     * @return 定位图标
     */
    public FMLocationMarker buildLocationMarker(int groupId,
                                                FMMapCoord position) {
        FMLocationMarkerStyle locStyle = new FMLocationMarkerStyle();
        locStyle.setActiveImageFromAssets("fmr/pointer.png");
        locStyle.setStaticImageFromAssets("fmr/dome.png");
        locStyle.setWidth(45);
        locStyle.setHeight(45);
        FMLocationMarker marker = new FMLocationMarker(groupId, position, locStyle);
        return marker;
    }


    /**
     * 画一层的路径线。
     * @param layer         图层
     * @param lineWidth     线宽
     * @param lineArrowPic  线上的箭头图片名称
     * @param lineColor     线的颜色
     * @param naviResult    一层的数据结果
     * @return 线对象
     */
    public FMLineMarker drawFloorLine(FMLineLayer layer,
                                      float lineWidth,
                                      String lineArrowPic,
                                      int lineColor,
                                      FMNaviResult naviResult) {

        FMLineMarkerStyle lineStyle = new FMLineMarkerStyle();
        lineStyle.setFillColor(lineColor);
        lineStyle.setLineWidth(lineWidth);
        lineStyle.setImageFromAssets(lineArrowPic);
        lineStyle.setLineMode(FMLineMarker.LineMode.FMLINE_PLANE);
        lineStyle.setLineType(FMLineMarker.LineType.FMLINE_TEXTURE_MIX);


        FMLineMarker floorLine = new FMLineMarker();
        floorLine.setStyle(lineStyle);

        FMSegment s = new FMSegment(naviResult.getGroupId(), naviResult.getPointList());
        floorLine.addSegment(s);

        floorLine.setFMLineDrawType(FMLineMarker.FMLINE_FLOOR);

        layer.addMarker(floorLine);

        return floorLine;
    }

    /**
     * 画跨层线。
     * @param layer 图层
     * @param lineWidth 线宽
     * @param lineArrowPic 线上箭头图片的名字
     * @param start 起点结果
     * @param end   终点结果
     * @return 线对象
     */
    FMLineMarker drawLineBetweenFloors(FMLineLayer layer,
                                       float lineWidth,
                                       String lineArrowPic,
                                       FMNaviResult start,
                                       FMNaviResult end) {

        FMLineMarkerStyle lineStyle = new FMLineMarkerStyle();
        lineStyle.setLineWidth(lineWidth);
        lineStyle.setImageFromAssets(lineArrowPic);

        lineStyle.setLineMode(FMLineMarker.LineMode.FMLINE_PLANE);
        lineStyle.setLineType(FMLineMarker.LineType.FMLINE_TEXTURE_MIX);  //箭头线型

        FMLineMarker betweenLine = new FMLineMarker();
        betweenLine.setStyle(lineStyle);

        ArrayList<FMMapCoord> coords0 = new ArrayList<>();
        int                   size0   = start.getPointList().size();
        coords0.add(start.getPointList().get(size0 - 1));
        FMSegment             s0      = new FMSegment(start.getGroupId(), coords0);
        ArrayList<FMMapCoord> coords1 = new ArrayList<>();
        int                   size1   = end.getPointList().size();
        coords1.add(end.getPointList().get(0));
        FMSegment s1 = new FMSegment(end.getGroupId(), coords1);
        betweenLine.addSegment(s0);
        betweenLine.addSegment(s1);

        betweenLine.setFMLineDrawType(FMLineMarker.FMLINE_BETWEEN_FLOORS);
        layer.addMarker(betweenLine);

        return betweenLine;
    }


    public FMLineMarker drawMultiFloorLine(FMLineLayer layer,
                                           float lineWidth,
                                           String lineArrowPic, int color,
                                           ArrayList<FMNaviResult> pNaviResults) {
        FMLineMarkerStyle lineStyle = new FMLineMarkerStyle();
        lineStyle.setLineWidth(lineWidth);
        lineStyle.setImageFromAssets(lineArrowPic);

        lineStyle.setLineMode(FMLineMarker.LineMode.FMLINE_PLANE);
        lineStyle.setLineType(FMLineMarker.LineType.FMLINE_TEXTURE_MIX);  //箭头线型

        FMLineMarker lineMarker = new FMLineMarker();
        lineMarker.setStyle(lineStyle);

        for (int i=0; i<pNaviResults.size(); i++) {
            FMNaviResult nr      = pNaviResults.get(i);
            FMSegment    segment = new FMSegment(nr.getGroupId(), nr.getPointList());
            lineMarker.addSegment(segment);
        }

        if (pNaviResults.size() < 2) {
            lineMarker.setFMLineDrawType(FMLineMarker.FMLINE_FLOOR);
        } else {
            lineMarker.setFMLineDrawType(FMLineMarker.FMLINE_BETWEEN_FLOORS);
        }

        layer.addMarker(lineMarker);

        return lineMarker;
    }




    public FMLineMarker newLineMarkers(int groupId,
                                       ArrayList<FMNaviResult> pNaviResults) {

        FMLineMarkerStyle lineStyle = new FMLineMarkerStyle();
        lineStyle.setFillColor(Color.BLUE);
        lineStyle.setLineWidth(2.0f);
        lineStyle.setImageFromAssets("fmr/full_arrow.png");

        lineStyle.setLineMode(FMLineMarker.LineMode.FMLINE_PLANE);
        lineStyle.setLineType(FMLineMarker.LineType.FMLINE_TEXTURE_MIX);  //箭头线型


        for (int i=0; i<pNaviResults.size(); i++) {
            FMNaviResult nr = pNaviResults.get(i);
            if (nr.getGroupId() == groupId) {
                ArrayList<FMMapCoord> coords = nr.getPointList();
                if (coords.size() > 2) {   // Floor line
                    FMSegment    segment    = new FMSegment(groupId, coords);
                    FMLineMarker lineMarker = new FMLineMarker();
                    lineMarker.addSegment(segment);
                    lineMarker.setStyle(lineStyle);
                    return lineMarker;
                } else {
                    // 跨层点
                    //Nothing To Do
                }
            }
        }

        return null;
    }


    /**
     * 跳转界面。
     * @param start
     * @param target
     * @param b
     */
    public void gotoActivity(Activity start, Class target, Bundle b) {
        Intent intent = new Intent(start, target);
        if (b!=null) {
            intent.putExtras(b);
        }
        start.startActivity(intent);
    }


    public static FMMapCoord YVector = new FMMapCoord(0,1,0);

    public static double calcuAngle(FMMapCoord pCurrCoord, FMMapCoord pNextCoord) {
        FMMapCoord vector = FMMapCoord.subtract(pNextCoord, pCurrCoord);
        double rad = Math.acos(FMMapCoord.dot(YVector, vector));
        if (vector.x > 0) {
            rad =  Math.PI * 2 - rad;
        }
        return FMMath.radToDegree(rad);
    }

}
