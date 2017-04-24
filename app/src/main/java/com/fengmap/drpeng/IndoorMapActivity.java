package com.fengmap.drpeng;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.mgwaiter.R;
import com.fengmap.android.FMMapSDK;
import com.fengmap.android.analysis.navi.FMConstraintRoad;
import com.fengmap.android.analysis.navi.FMNaviAnalyser;
import com.fengmap.android.analysis.navi.FMNaviMultiAnalyer;
import com.fengmap.android.analysis.navi.FMNaviResult;
import com.fengmap.android.map.FMGroupInfo;
import com.fengmap.android.map.FMMap;
import com.fengmap.android.map.FMMapInfo;
import com.fengmap.android.map.FMMapView;
import com.fengmap.android.map.FMViewMode;
import com.fengmap.android.map.animator.FMInterpolator;
import com.fengmap.android.map.animator.FMLinearInterpolator;
import com.fengmap.android.map.animator.FMSceneAnimator;
import com.fengmap.android.map.event.OnFMMapClickListener;
import com.fengmap.android.map.event.OnFMMapInitListener;
import com.fengmap.android.map.event.OnFMNodeListener;
import com.fengmap.android.map.geometry.FMMapCoord;
import com.fengmap.android.map.geometry.FMTotalMapCoord;
import com.fengmap.android.map.layer.FMImageLayer;
import com.fengmap.android.map.layer.FMLayerProxy;
import com.fengmap.android.map.layer.FMLineLayer;
import com.fengmap.android.map.layer.FMLocationLayer;
import com.fengmap.android.map.layer.FMModelLayer;
import com.fengmap.android.map.marker.FMImageMarker;
import com.fengmap.android.map.marker.FMLineMarker;
import com.fengmap.android.map.marker.FMLocationMarker;
import com.fengmap.android.map.marker.FMModel;
import com.fengmap.android.map.marker.FMNode;
import com.fengmap.android.map.style.FMStyle;
import com.fengmap.android.utils.FMLog;
import com.fengmap.android.utils.FMMath;
import com.fengmap.android.wrapmv.FMNaviAnalysisHelper;
import com.fengmap.android.wrapmv.Tools;
import com.fengmap.android.wrapmv.db.FMDBMapElement;
import com.fengmap.android.wrapmv.db.FMDBMapElementDAO;
import com.fengmap.android.wrapmv.db.FMDBSearchElement;
import com.fengmap.android.wrapmv.entity.FMMarkerBuilderInfo;
import com.fengmap.android.wrapmv.service.FMCallService;
import com.fengmap.android.wrapmv.service.FMLocationService;
import com.fengmap.android.wrapmv.service.FMMacService;
import com.fengmap.android.wrapmv.service.OnFMMacAddressListener;
import com.fengmap.android.wrapmv.service.OnFMReceiveLocationListener;
import com.fengmap.android.wrapmv.service.OnFMReceivePositionInCallServiceListener;
import com.fengmap.android.wrapmv.service.OnFMWifiStatusListener;
import com.fengmap.drpeng.common.NavigationUtils;
import com.fengmap.drpeng.common.StringUtils;
import com.fengmap.drpeng.entity.Floor;
import com.fengmap.drpeng.widget.CustomPopupWindow;
import com.fengmap.drpeng.widget.CustomProgressDialog;
import com.fengmap.drpeng.widget.CustomToast;
import com.fengmap.drpeng.widget.DrawableCenterTextView;
import com.fengmap.drpeng.widget.InsideModelView;
import com.fengmap.drpeng.widget.NaviProcessingView;
import com.fengmap.drpeng.widget.NaviView;
import com.fengmap.drpeng.widget.SwitchFloorView;
import com.fengmap.drpeng.widget.TopBarView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.fengmap.android.wrapmv.Tools.getFMNaviAnalyserByMapId;
import static com.fengmap.drpeng.FMAPI.TARGET_ADD_MARKER;
import static com.fengmap.drpeng.FMAPI.TARGET_CALCULATE_ROUTE;
import static com.fengmap.drpeng.FMAPI.TARGET_LOCATE;
import static com.fengmap.drpeng.FMAPI.TARGET_SELECT_POINT;
import static com.fengmap.drpeng.OutdoorMapActivity.WaiterMacAddress;

/**
 * 室内地图。
 * Created by yangbin on 16/6/29.
 */
public class IndoorMapActivity extends Activity implements OnFMMapInitListener,
                                                           View.OnClickListener,
                                                           OnFMMapClickListener, CustomPopupWindow.OnWindowCloseListener, OnFMReceivePositionInCallServiceListener {
    public static IndoorMapActivity mInstance;

    private FMMapView    mMapView;
    private FMMap        mMap;
    private FMMapInfo    mMapInfo;
    private FMLayerProxy mLayerProxy;

    // 定位
    private FMLocationLayer  mLocationLayer;            //定位图层
    private FMLocationMarker mMeLocationMarker;         //我的位置Marker

    // 路径规划
    private boolean        mNeedLoadCalculatedRoute;                       //地图初始化时是否需要加载规划的路径
    private FMImageLayer   mStartMarkerLayer, mEndMarkerLayer;      //起点图层
    private FMImageMarker mStartMarker, mEndMarker;                //起点标注物
    private FMLineLayer mLineLayer;                              //线图层

    private FMLineMarker mCurrentCalculateLineMarkerLineMarker;
    private ArrayList<FMNaviResult> mPathResults = new ArrayList<>();

    private FMSceneAnimator mSceneAnimator;       //负责点击模型移动到地图屏幕中央的动画对象

    // 模型
    private FMModel mCurrentModel;
    private FMModel mLastModel;

    // 标注
    private FMImageLayer  mSpecialWorkImageLayer;
    private FMImageMarker mSpecialWorkMarker;

    // 弹窗
    private CustomPopupWindow mOpenModelInfoWindow;
    private CustomPopupWindow mOpenNaviWindow;
    private CustomPopupWindow mOpenNaviProcessWindow;

    //ui
    private TopBarView      mTopBarView;
    private SwitchFloorView mSwitchFloorView;

    private ImageView mLocationView;
    private boolean   isLoadMapCompleted = false;

    private CustomProgressDialog mProgressDialog;

    // call service
    private DrawableCenterTextView mCallView;
    private FMImageMarker          mMyMarkerInCall, mWaiterMarkerInCall;

    // other
    Random mRandom = new Random();
    private Integer[] mPassGroupIdsByCalculateRoute;  // 路线经过的楼层集合
    private int mInitGroupId = -1;
    private FMTotalMapCoord mInitLocatePosition;
    private String          mFromWhere;

    private FMDBMapElementDAO mMapElementDAO;
    private String wifiStatues = "";
    private int wifiDelayTime = 0;
    private String logText = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInstance = this;
        setContentView(R.layout.activity_fengmap_indoor);
        init();
    }

    public FMMap getMap() {
        return mMap;
    }

    private void init() {
        mTopBarView = (TopBarView) findViewById(R.id.fm_topbar);
        mSwitchFloorView = (SwitchFloorView) findViewById(R.id.indoor_switch_floor);
        mSwitchFloorView.setCallBackFloor(new SwitchFloorView.OnCallBackFloor() {
            @Override
            public void switchFloor(Floor pFloor) {
                needSwitchFloor(pFloor.getGroupId(), true ,true);
            }
        });
        mLocationView = (ImageView) findViewById(R.id.indoor_location);
        mLocationView.setOnClickListener(this);

        mCallView = (DrawableCenterTextView) findViewById(R.id.indoor_call);
        mCallView.setOnClickListener(this);

        mMapView = (FMMapView) findViewById(R.id.indoor_mapview);
        mMap = mMapView.getFMMap();

        // 搜索
        mMapElementDAO = new FMDBMapElementDAO();

        // 进度条
        mProgressDialog = new CustomProgressDialog(this, R.style.custom_dialog);
        mProgressDialog.setCustomContentView(R.layout.fm_custome_dialog);
        mProgressDialog.setInfoViewContext("加载中...");
        mProgressDialog.show();

        dealIntentBundle();
    }

    private void dealIntentBundle() {
        if (FMCallService.instance().isRunning()) {
            mCallView.setTextColor(Color.WHITE);
            mCallView.setBackgroundResource(R.drawable.fm_green_press_button);
        } else {
            mCallView.setTextColor(Color.parseColor("#70ffffff"));
            mCallView.setBackgroundResource(R.drawable.fm_green_normal_button);
        }

        Bundle b     = getIntent().getExtras();
        mFromWhere   = b.getString(FMAPI.ACTIVITY_WHERE);

        clearWalkedTemporaryValue();

        String mapId = dealWhere(b, mFromWhere);

        mMap.openMapById(mapId);  //联网下载
        mMap.setOnFMMapInitListener(this);
        mMap.setOnFMMapClickListener(this);

        mSceneAnimator = new FMSceneAnimator(mMap);

        initWindow();
        initFMLocationService();
        // 开启定位服务
        boolean openedResult = FMMapSDK.setLocateServiceState(true);
        if (!openedResult) {
            CustomToast.show(this, "请检测WIFI连接和GPS状态...");
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        dealOnNewIntent(intent);
        super.onNewIntent(intent);
    }

    private void dealOnNewIntent(Intent intent) {
        FMLog.le("IndoorMapActivity", "IndoorMapActivity#onNewIntent");

        if (FMCallService.instance().isRunning()) {
            mCallView.setTextColor(Color.WHITE);
            mCallView.setBackgroundResource(R.drawable.fm_green_press_button);
        } else {
            mCallView.setTextColor(Color.parseColor("#70ffffff"));
            mCallView.setBackgroundResource(R.drawable.fm_green_normal_button);
        }

        Bundle b     = intent.getExtras();
        mFromWhere = b.getString(FMAPI.ACTIVITY_WHERE);

        clearWalkedTemporaryValue();

        String mapId = dealWhere(b, mFromWhere);

        mMap.openMapById(mapId);  //联网下载

    }

    // 处理界面之间的传递值
    private String dealWhere(Bundle pB, String pWhere) {
        FMLog.le("IndoorMapActivity", "IndoorMapActivity#dealWhere");
        String mapId = "";
        if (SearchResultFragment.class.getName().equals(pWhere)) {
            // 从搜索结果界面而来
            FMDBMapElement e = (FMDBMapElement) pB.getSerializable(SearchResultFragment.class.getName());
            mapId = e.getMapId();
            // 创建标注
            mSpecialWorkMarker = FMAPI.instance().buildImageMarker(e.getGroupId(),
                                                                   new FMMapCoord(e.getX(), e.getY()),
                                                                   "fmr/water_marker.png",
                                                                   40,
                                                                   FMStyle.FMNodeOffsetType.FMNODE_CUSTOM_HEIGHT,
                                                                   e.getZ());

            mInitGroupId = e.getGroupId();

            mTopBarView.setTitle(String.format("%s・%s", Tools.getInsideMapName(mapId), "室内地图"));

        } else if (SearchFragment.class.getName().equals(pWhere)) {
            // 从搜索界面而来
            FMDBSearchElement e = (FMDBSearchElement) pB.getSerializable(SearchFragment.class.getName());
            mapId = e.getMapId();
            mInitGroupId = e.getGroupId();

            mTopBarView.setTitle(String.format("%s・%s", Tools.getInsideMapName(mapId), "室内地图"));

            // 目的
            String target = pB.getString(FMAPI.ACTIVITY_TARGET);
            if (TARGET_ADD_MARKER.equals(target)) {
                // 创建标注
                mSpecialWorkMarker = FMAPI.instance().buildImageMarker(e.getGroupId(),
                                                                       new FMMapCoord(e.getX(), e.getY()),
                                                                       "fmr/water_marker.png",
                                                                       40,
                                                                       FMStyle.FMNodeOffsetType.FMNODE_CUSTOM_HEIGHT,
                                                                       e.getZ());
            } else if (TARGET_CALCULATE_ROUTE.equals(target)) {
                FMTotalMapCoord myPos = FMLocationService.instance().getFirstMyLocatePosition();
                if (myPos == null) {
                    CustomToast.show(this, "定位失败...");

                    mNeedLoadCalculatedRoute = false;
                }
                FMNaviAnalysisHelper.instance().setStartNaviMultiPoint(myPos.getGroupId(),
                                                                       Tools.getFMNaviAnalyserByMapId(myPos.getMapId()),
                                                                       myPos.getMapCoord());

                // 路径规划
                FMNaviAnalysisHelper.instance().setEndNaviMultiPoint(e.getGroupId(),
                                                                     getFMNaviAnalyserByMapId(mapId),
                                                                     new FMMapCoord(e.getX(), e.getY()));

                mNeedLoadCalculatedRoute = true;

            } else if (TARGET_SELECT_POINT.equals(target)) {
                // 地图选点

            }

        } else if (OutdoorMapActivity.class.getName().equals(pWhere)) {
            // 从室外界面而来
            mapId = pB.getString(FMAPI.ACTIVITY_MAP_ID);
            String hotelName = pB.getString(FMAPI.ACTIVITY_HOTEL_NAME);
            mTopBarView.setTitle(String.format("%s・%s", hotelName, "室内地图"));

            // 目的
            String target = pB.getString(FMAPI.ACTIVITY_TARGET);
            if (TARGET_CALCULATE_ROUTE.equals(target)) {   // 路径规划

                FMLog.e("IndoorMapActivity", "IndoorMapActivity#TARGET_CALCULATE_ROUTE");
                mNeedLoadCalculatedRoute = true;
                mInitGroupId = pB.getInt(FMAPI.ACTIVITY_MAP_GROUP_ID);

            } else if (TARGET_LOCATE.equals(target)){      // 定位

                FMLog.e("IndoorMapActivity", "IndoorMapActivity#TARGET_LOCATE");

                mInitLocatePosition = (FMTotalMapCoord) pB.getSerializable(FMAPI.ACTIVITY_LOCATE_POSITION);

                if (mInitLocatePosition != null) {

                    dealAddLocateMarker(mInitLocatePosition.getGroupId(), mInitLocatePosition.getMapCoord(), mLocationLayer);
                    mInitGroupId = mInitLocatePosition.getGroupId();
                }
            }
        }
        return mapId;
    }


    /**
     * 切换地图。
     * @param mapId
     * 1. 到这去需要切换地图图;
     * 2. 定位切换地图
     */
    public void needSwitchMap(String mapId , boolean isArrive) {
        clearBeforeSwitchMap();

        if (isArrive) {     // 到这去
            mMap.openMapById(mapId);
        } else {            // 定位
            if (mInitLocatePosition != null) {  //处理定位图标
                isLoadMapCompleted = false;
                dealAddLocateMarker(mInitLocatePosition.getGroupId(), mInitLocatePosition.getMapCoord(), mLocationLayer);
                mLocationView.setBackgroundResource(R.drawable.fm_green_press_button);
                mInitGroupId = mInitLocatePosition.getGroupId();
            }
            mMap.openMapById(mapId);
        }

    }

    /**
     * 导航模式下切换室内地图。
     * @param mapId
     */
    public void needSwitchMapInNavigation(String mapId) {
        clearBeforeSwitchMap();

        mMap.openMapById(mapId);
    }

    @Override
    public void onMapInitSuccess(String mapPath) {
        mTopBarView.setTitle(String.format("%s・%s", Tools.getInsideMapName(mMap.currentMapId()), "室内地图"));

        mMap.setSceneZoomRange(1.0f, 20);
        mMap.zoom(6.0f);

        mMapInfo = mMap.getFMMapInfo();                        //获取地图配置信息

        mMap.loadThemeById("2002");

        // 图层代理
        mLayerProxy = mMap.getFMLayerProxy();

        List<Floor> floors = new ArrayList<>();
        ArrayList<FMGroupInfo> groupInfos = mMapInfo.getGroups();
        for (FMGroupInfo info : groupInfos) {
            floors.add(new Floor(info.getGroupId(), info.getGroupName().toUpperCase()));

            FMModelLayer modelLayer = mLayerProxy.getFMModelLayer(info.getGroupId());

            if (modelLayer == null) {
                continue;
            }

            modelLayer.setOnFMNodeListener(new OnFMNodeListener() {
                @Override
                public boolean onClick(FMNode pFMNode) {
//                    if (FMAPI.instance().needFilterNavigationWhenOperation(mInstance)) {
//                        return false;
//                    }
//
//                    mCurrentModel = (FMModel) pFMNode;
//
//                    if (mLastModel != null) {
//                        mLastModel.setSelected(false);
//                    }
//
//                    mCurrentModel.setSelected(true);
//
//                    InsideModelView view = (InsideModelView) mOpenModelInfoWindow.getConvertView();
//                    String          name = mCurrentModel.getName();
//                    if (name.equals("") || name == null) {
//                        name = "暂无名称";
//                    }
//                    view.setTitle(name);
//
//                    // 查询
//                    List<FMDBMapElement> elements = mMapElementDAO.queryFid(mMap.currentMapId(), mCurrentModel.getFid());
//                    String               typeName = "";
//                    String               address  = "";
//                    if (!elements.isEmpty()) {
//                        typeName = elements.get(0).getTypename();
//                        address = elements.get(0).getAddress();
//                    }
//                    elements.clear();
//                    elements = null;
//                    String viewAddress = "";
//                    if (typeName==null || typeName.equals("")) {
//                        viewAddress = address;
//                    } else {
//                        viewAddress = String.format("%s・%s", typeName, address);
//                    }
//
//                    view.setAddress(viewAddress);
//
//                    mOpenModelInfoWindow.showAsDropDown(mMapView, 0, -mMapView.getHeight());
//
//                    mLastModel = mCurrentModel;
//
//                    mSceneAnimator.animateMoveToScreenCenter(mCurrentModel.getCenterMapCoord())
//                            .setInterpolator(new FMLinearInterpolator(FMInterpolator.STAGE_INOUT))
//                            .setDurationTime(800)
//                            .start();
//
//                    mMap.updateMap();
                    return false;
                }

                @Override
                public boolean onLongPress(FMNode pFMNode) {
                    if (FMAPI.instance().needFilterNavigationWhenOperation(mInstance)) {
                        return false;
                    }
                    return false;
                }
            });

        }
        mSwitchFloorView.setFloors(floors);

        // 线图层
        mLineLayer = mLayerProxy.getFMLineLayer();
        mMap.addLayer(mLineLayer);

        // 定位
        mLocationLayer = mMap.getFMLayerProxy().getFMLocationLayer();
        mMap.addLayer(mLocationLayer);

        // 显示路网
        //mNaviAnalyser.showAllPath(mMap.currentMapPath(), mLineLayer, mMap.getFocusGroupId());

        // 处理加载任务
        dealInitLoadTask();

        if (mInitGroupId < 0) {
            mInitGroupId = groupInfos.get(0).getGroupId();
        }
        mSwitchFloorView.setSelectedGroupId(mInitGroupId);
        mMap.setMultiDisplay(new int[] { mInitGroupId });

        isLoadMapCompleted = true;

        mProgressDialog.dismiss();
    }

    // 处理地图初始化后的任务
    private void dealInitLoadTask() {
        //加载业态标注
        if (mSpecialWorkMarker != null) {
            mSpecialWorkImageLayer = mLayerProxy.createFMImageLayer(mSpecialWorkMarker.getGroupId());
            mSpecialWorkImageLayer.addMarker(mSpecialWorkMarker);
            mMap.addLayer(mSpecialWorkImageLayer);

            mSceneAnimator.animateMoveToScreenCenter(mSpecialWorkMarker.getPosition())
                    .setInterpolator(new FMLinearInterpolator(FMInterpolator.STAGE_INOUT))
                    .setDurationTime(1000)
                    .start();

            mMap.updateMap();
        }

        // 加载规划的路径
        if (mNeedLoadCalculatedRoute) {
            if (calculateAndDrawRouteAndAddStartEndMarker()) {
                mOpenNaviWindow.showAsDropDown(mMapView, 0, -mMapView.getHeight());
            }
            mNeedLoadCalculatedRoute = false;
        }

        // 加载定位标注
        if (mMeLocationMarker != null) {
            if (mMeLocationMarker.getHandle() == 0) {
                mLocationLayer.addMarker(mMeLocationMarker);
            } else {
                mMeLocationMarker.updatePosition(mInitLocatePosition.getGroupId(),
                                                 mInitLocatePosition.getMapCoord());
            }
        }

        // 处理导航模式
        if (FMLocationService.instance().isInNavigationMode()) {
            dealInitLoadedNavigationTask();
        } else {
            if (mOpenNaviProcessWindow.isShowing()) {
                mOpenNaviProcessWindow.close();
            }

            clearCalculatedPathResults();
            clearCalculateRouteLineMarker();
            clearStartAndEndMarker();
            clearWalkedTemporaryValue();
        }
    }

    // 处理导航模式下进入地图的任务    必定是终点
    private void dealInitLoadedNavigationTask() {
        if (FMNaviAnalysisHelper.instance().getEndNaviMultiPoint() == null || FMNaviAnalysisHelper.instance().getStartNaviMultiPoint() == null) {
            return;
        }

        // 如果起点在当前地图
        if (FMNaviAnalysisHelper.instance().getStartNaviMultiPoint().getNaviAnalyser().getMapId().equals(mMap.currentMapId())) {
            // 起点
            addStartMarker(FMNaviAnalysisHelper.instance().getStartNaviMultiPoint().getGroupId(),
                           FMNaviAnalysisHelper.instance().getStartNaviMultiPoint().getPosition(),
                           false);

            mPathResults = FMNaviAnalysisHelper.instance().getStartNaviMultiPoint().getNaviAnalyser().getNaviResults();
            mPassGroupIdsByCalculateRoute = getPassGroupIdsByCalculateRoute();
            mInitGroupId = mPassGroupIdsByCalculateRoute[0];

            dealAddLocateMarker(mInitGroupId,
                                FMNaviAnalysisHelper.instance().getStartNaviMultiPoint().getPosition(),
                                mLocationLayer);

            // draw
            drawFloorLine(mInitGroupId);

            animateCenterWithZoom(mInitGroupId, FMNaviAnalysisHelper.instance().getStartNaviMultiPoint().getPosition());
        } else {
            // 终点
            addEndMarker(FMNaviAnalysisHelper.instance().getEndNaviMultiPoint().getGroupId(),
                         FMNaviAnalysisHelper.instance().getEndNaviMultiPoint().getPosition(),
                         false);

            mPathResults = FMNaviAnalysisHelper.instance().getEndNaviMultiPoint().getNaviAnalyser().getNaviResults();
            mPassGroupIdsByCalculateRoute = getPassGroupIdsByCalculateRoute();
            mInitGroupId = mPassGroupIdsByCalculateRoute[0];
            // 添加定位标注物
            FMMapCoord firstPos = mPathResults.get(0).getPointList().get(0);
            dealAddLocateMarker(mInitGroupId, firstPos, mLocationLayer);
            // draw
            drawFloorLine(mInitGroupId);

            animateCenterWithZoom(mInitGroupId, firstPos);
        }

        // 显示导航窗口
        setNavigationViewContent(FMAPI.instance().mWalkedDistance);

        mOpenNaviProcessWindow.showAsDropDown(mTopBarView, 0, mTopBarView.getHeight());
    }

    // 切换地图前 清除
    private void clearBeforeSwitchMap() {
        isLoadMapCompleted = false;

        // clear
        mMap.clearDataInCallServiceLayers();
        clearCalculateRouteLineMarker();
        clearWalkedTemporaryValue();
        clearStartAndEndMarker();
        clearMeLocationMarker();
        clearSpecialMarker();
        clearCalculatedPathResults();

        mMap.removeAll();
        mLineLayer = null;
        mLocationLayer = null;
        mSpecialWorkImageLayer = null;
        mMap.updateMap();

        mPassGroupIdsByCalculateRoute = null;
        mLastLineAngle = -1;
        mLastModel = null;
    }


    @Override
    public void onMapInitFailure(String mapPath, int code) {

    }


    /**
     * 切换楼层
     * @param groupId
     * @param needChangeSwitchView
     * @param  needDrawRoute
     */
    private void needSwitchFloor(int groupId, boolean needChangeSwitchView, boolean needDrawRoute) {
        if (needChangeSwitchView) {
            mSwitchFloorView.setSelectedGroupId(groupId);
        }
        mMap.setFocusByGroupId(groupId);
        mLineLayer.removeAll();
        mMap.updateMap();
        // 是否画线
        if (!mPathResults.isEmpty() && needDrawRoute) {
            drawFloorLine(groupId);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.indoor_location:


                if (!FMLocationService.instance().checkLocationValid(this)) {
                    return;
                }

                FMTotalMapCoord myLocatePos = FMLocationService.instance().getFirstMyLocatePosition();
                if (myLocatePos == null) {
                    // 定位失败
                    needLocate(false);
                    return;
                }

                mNeedBackToMyLocation = true;


//                if (FMAPI.instance().isInLocating) { //close
//                    v.setBackgroundResource(R.drawable.fm_green_normal_button);
//
//                    clearCalculateRouteLineMarker();
//                    clearStartAndEndMarker();
//                    clearMeLocationMarker();
//
//                    mPassedGroup.clear();
//                    mPassedIndex.clear();
//
//                    FMLocationService.instance().stop();
//                } else {
//                    v.setBackgroundResource(R.drawable.fm_green_press_button);
//
//                    needLocate(false);
//                }
//
//                FMAPI.instance().isInLocating = !FMAPI.instance().isInLocating;

                break;

            case R.id.indoor_call:
                if (FMCallService.instance().isRunning()) {
                    FMMapSDK.setCallServiceState(false);

                    mCallView.setTextColor(Color.parseColor("#70ffffff"));
                    mCallView.setBackgroundResource(R.drawable.fm_green_normal_button);

                    mMap.getCallSeverLocateLayer(mMap.getFocusGroupId()).removeAll();
                    mMap.updateMap();
                } else {
                    FMMapSDK.setCallServiceState(true);

                    mCallView.setTextColor(Color.WHITE);
                    mCallView.setBackgroundResource(R.drawable.fm_green_press_button);

                    // add monitored mac address
                    FMCallService.instance().addMonitoredMacAddress(WaiterMacAddress);

                    // add waiter marker
                    addWaiterLocateInCall();

                    // add my marker
                    addMyLocateInCall();
                }
                break;

            default:
                break;
        }
    }

    private void addWaiterLocateInCall() {
        mWaiterMarkerInCall = mMap.addLocMarkerOnMap(new FMMarkerBuilderInfo(WaiterMacAddress, "fmr/fm_active_red.png"));
    }

    private void addMyLocateInCall() {
        FMMacService.instance().getMacAddress(this, new OnFMMacAddressListener() {
            @Override
            public void onMacAddress(String pMacAddr) {
                if ("".equals(pMacAddr)) {
                    FMLog.le("getMacAddress", "the mac address is invalid...");
                    return;
                }
                mMyMarkerInCall = mMap.addLocMarkerOnMap(new FMMarkerBuilderInfo(pMacAddr, "fmr/water_marker.png"));
            }
        });
    }


    @Override
    public void onBackPressed() {
        if (mProgressDialog.isShowing()) {
            return;
        }

        if (tryCloseAllWindow()) {
            return;
        }

        isLoadMapCompleted = false;

        if (mLineLayer != null) {
            mLineLayer.removeAll();
        }

        if (mSceneAnimator != null) {
            mSceneAnimator.cancel();
        }

        mMap.onDestroy();

        if (OutdoorMapActivity.class.getName().equals(mFromWhere)) {
            enterOutside();
        } else {
            super.onBackPressed();
            this.finish();
        }
    }

    //尝试关闭所有窗口 (导航模式下的窗口除外)
    private boolean tryCloseAllWindow() {
        if (mOpenNaviProcessWindow != null && mOpenNaviProcessWindow.isShowing()) {
            CustomToast.show(this, "请结束导航模式");
            return true;
        }
        if (mOpenModelInfoWindow != null && mOpenModelInfoWindow.isShowing()) {
            mOpenModelInfoWindow.close();
            return true;
        }
        if (mOpenNaviWindow != null && mOpenNaviWindow.isShowing()) {
            mOpenNaviWindow.close();
            return true;
        }
        return false;
    }

    // 关闭所有窗口
    private boolean closeAllWindow() {
        if (mOpenNaviProcessWindow != null && mOpenNaviProcessWindow.isShowing()) {
            mOpenNaviProcessWindow.close();
            return true;
        }
        if (mOpenModelInfoWindow != null && mOpenModelInfoWindow.isShowing()) {
            mOpenModelInfoWindow.close();
            return true;
        }
        if (mOpenNaviWindow != null && mOpenNaviWindow.isShowing()) {
            mOpenNaviWindow.close();
            return true;
        }
        return false;
    }


    @Override
    protected void onResume() {
        mMap.onResume();
        FMLocationService.instance().registerListener(mLocationListener);
        FMCallService.instance().registerCallServiceListener(this);

//        if (!checkIpThread.isAlive()) {
//            checkIpThread.start();
//        }

        super.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
        FMLocationService.instance().unregisterListener(mLocationListener);
        FMCallService.instance().unregisterCallServiceListener(this);

//        if (checkIpThread.isAlive()) {
//            checkIpThread.interrupt();
//        }
    }


    private void addStartMarker(int groupId, FMMapCoord position, boolean needSetCalculateStartPoint) {
        mStartMarkerLayer = mMap.getFMLayerProxy().createFMImageLayer(groupId); //创建图层
        mMap.addLayer(mStartMarkerLayer);                                       //将图层添加到地图
        mStartMarker = FMAPI.instance().addImageMarker(mStartMarkerLayer,
                                                       position,
                                                       "fmr/icon_start.png");

        if (needSetCalculateStartPoint) {
            FMNaviAnalysisHelper.instance().setStartNaviMultiPoint(groupId,
                                                                   Tools.getFMNaviAnalyserByMapId(mMap.currentMapId()),
                                                                   position);
        }

        mMap.updateMap();
    }

    private void addEndMarker(int groupId, FMMapCoord position, boolean needSetCalculateEndPoint) {
        mEndMarkerLayer = mMap.getFMLayerProxy().createFMImageLayer(groupId); //创建图层
        mMap.addLayer(mEndMarkerLayer);              //将图层添加到地图
        mEndMarker = FMAPI.instance().addImageMarker(mEndMarkerLayer,
                                                     position,
                                                     "fmr/icon_end.png");

        if (needSetCalculateEndPoint) {
            FMNaviAnalysisHelper.instance().setEndNaviMultiPoint(groupId,
                                                                 Tools.getFMNaviAnalyserByMapId(mMap.currentMapId()),
                                                                 position);
        }

        mMap.updateMap();
    }

    /**
     * 路径规划。
     */
    private boolean calculateAndDrawRouteAndAddStartEndMarker() {
        mPathResults.clear();

        int[] resultCode = FMNaviAnalysisHelper.instance().calculateRoute();

        if (resultCode[0] == FMNaviAnalysisHelper.FM_CALCULATE_ROUTE_ONE_MAP) {    // 同地图路径规划

            if (resultCode[1] == FMNaviAnalyser.FMRouteCalcuResult.ROUTE_SUCCESS) {  //成功
                // 添加起点终点
                addStartMarker(FMNaviAnalysisHelper.instance().getStartNaviMultiPoint().getGroupId(),
                               FMNaviAnalysisHelper.instance().getStartNaviMultiPoint().getPosition(),
                               false);
                addEndMarker(FMNaviAnalysisHelper.instance().getEndNaviMultiPoint().getGroupId(),
                             FMNaviAnalysisHelper.instance().getEndNaviMultiPoint().getPosition(),
                             false);

                // 结果
                mPathResults = FMNaviAnalysisHelper.instance().getNaviPathResults(FMNaviAnalysisHelper.instance().getEndNaviMultiPoint());
                mPassGroupIdsByCalculateRoute = getPassGroupIdsByCalculateRoute();
            } else {  // 失败
                CustomToast.show(this, FMNaviAnalysisHelper.instance().getCalculateRouteReturnInfo(resultCode));
                return false;
            }
        } else {        // 跨地图
            if (resultCode[1] == FMNaviMultiAnalyer.FMNaviMultiRouteResult.MULTIROUTE_SUCCESS) { //成功
                // 判断位于当前地图是起点还是终点 ?
                // 起点
                if (FMNaviAnalysisHelper.instance().getStartNaviMultiPoint().getNaviAnalyser().getMapId().equals(mMap.currentMapId())) { // 是起点
                    addStartMarker(FMNaviAnalysisHelper.instance().getStartNaviMultiPoint().getGroupId(),
                                   FMNaviAnalysisHelper.instance().getStartNaviMultiPoint().getPosition(),
                                   false);

                    mPathResults = FMNaviAnalysisHelper.instance().getNaviPathResults(FMNaviAnalysisHelper.instance().getStartNaviMultiPoint());
                    mPassGroupIdsByCalculateRoute = getPassGroupIdsByCalculateRoute();

                } else {
                    // 终点
                    addEndMarker(FMNaviAnalysisHelper.instance().getEndNaviMultiPoint().getGroupId(),
                                 FMNaviAnalysisHelper.instance().getEndNaviMultiPoint().getPosition(),
                                 false);

                    mPathResults = FMNaviAnalysisHelper.instance().getNaviPathResults(FMNaviAnalysisHelper.instance().getEndNaviMultiPoint());
                    mPassGroupIdsByCalculateRoute = getPassGroupIdsByCalculateRoute();

                }
            } else {
                CustomToast.show(this, FMNaviAnalysisHelper.instance().getCalculateRouteReturnInfo(resultCode));
                return false;
            }
        }

        // 设置导航模式下窗口的内容
        setStartOpenNavigationView(resultCode);

        drawFloorLine(mMap.getFocusGroupId());

        mMap.updateMap();

        return true;
    }

    /**
     * 路径规划。
     */
    private boolean calculateAndDrawRoute() {
        mPathResults.clear();

        int[] resultCode = FMNaviAnalysisHelper.instance().calculateRoute();

        if (resultCode[0] == FMNaviAnalysisHelper.FM_CALCULATE_ROUTE_ONE_MAP) {    // 同地图路径规划
            if (resultCode[1] == FMNaviAnalyser.FMRouteCalcuResult.ROUTE_SUCCESS) {  //成功
                mPathResults = FMNaviAnalysisHelper.instance().getNaviPathResults(FMNaviAnalysisHelper.instance().getEndNaviMultiPoint());
                mPassGroupIdsByCalculateRoute = getPassGroupIdsByCalculateRoute();
            } else {  // 失败
                CustomToast.show(this, FMNaviAnalysisHelper.instance().getCalculateRouteReturnInfo(resultCode));
                clearStartAndEndMarker();
                return false;
            }
        } else {        // 跨地图

            if (resultCode[1] == FMNaviMultiAnalyer.FMNaviMultiRouteResult.MULTIROUTE_SUCCESS) { //成功
                // 判断是所在地图谁开始点还是终止点 ?
                if (FMNaviAnalysisHelper.instance().getStartNaviMultiPoint().getNaviAnalyser().getMapId().equals(mMap.currentMapId())) { // 是起点
                    mPathResults = FMNaviAnalysisHelper.instance().getNaviPathResults(FMNaviAnalysisHelper.instance().getStartNaviMultiPoint());
                    mPassGroupIdsByCalculateRoute = getPassGroupIdsByCalculateRoute();

                } else {  // 是终点
                    mPathResults = FMNaviAnalysisHelper.instance().getNaviPathResults(FMNaviAnalysisHelper.instance().getEndNaviMultiPoint());
                    mPassGroupIdsByCalculateRoute = getPassGroupIdsByCalculateRoute();
                }
            } else {
                CustomToast.show(this, FMNaviAnalysisHelper.instance().getCalculateRouteReturnInfo(resultCode));
                clearStartAndEndMarker();
                return false;
            }
        }

        // 设置导航模式下窗口的内容
        setStartOpenNavigationView(resultCode);

        drawFloorLine(mMap.getFocusGroupId());

        mMap.updateMap();

        return true;
    }

    // Navigation Data Description
    private void setStartOpenNavigationView(int[] pResultCode) {
        NavigationUtils.NavigationDataDescription nvdd = NavigationUtils.forNavigationDataDescription(pResultCode);

        NaviView naviView = (NaviView) mOpenNaviWindow.getConvertView();
        naviView.setNaviNeedTime(nvdd.getTotalTimeDesc());
        naviView.setNaviNeedDistance(nvdd.getTotalDistanceDesc());
        naviView.setNaviNeedCalorie(nvdd.getTotalCalorieDesc());
    }

    private void initFMLocationService() {
        FMLocationService.instance().init(getApplicationContext());
        FMLocationService.instance().setOnFMWifiStatusListener(new OnFMWifiStatusListener() {
            @Override
            public void wifiMapServerError() {

            }

            @Override
            public void wifiPositionServerError() {
                wifiStatues = "位置服务断开";
            }

            @Override
            public void wifiAvailable() {
                wifiStatues = "位置服务正常";
            }

            @Override
            public void wifiDelayTime(int pDelayTime) {
                wifiDelayTime = pDelayTime;
            }

            @Override
            public void logText(String pText) {

            }
        });
    }

    @Override
    public void onMapClick(float x, float y) {
//        FMPickMapCoordResult result = mMap.pickMapCoord(x, y);
//        if (result == null) {
//          return;
//        }
    }

    // 产生随机点
    private FMMapCoord randomMapCoord() {
        int    randX = (int) (mMapInfo.getMaxX() - mMapInfo.getMinX());
        int    randY = (int) (mMapInfo.getMaxY() - mMapInfo.getMinY());
        double x     = mRandom.nextInt(randX) + mMapInfo.getMinX();
        double y     = mRandom.nextInt(randY) + mMapInfo.getMinY();

        return new FMMapCoord(x, y, 0.0);
    }

    // 画线
    private void drawFloorLine(int groupId) {
        if (mCurrentCalculateLineMarkerLineMarker != null) {
            mLineLayer.removeMarker(mCurrentCalculateLineMarkerLineMarker);
            mMap.updateMap();
        }

        mCurrentCalculateLineMarkerLineMarker = FMAPI.instance().newLineMarkers(groupId, mPathResults);
        if (mCurrentCalculateLineMarkerLineMarker == null) {
            return;
        }

        mLineLayer.addMarker(mCurrentCalculateLineMarkerLineMarker);
        mMap.updateMap();
    }

    // 跳到 室外地图
    public void enterOutside() {
        Bundle b = new Bundle();
        b.putString(FMAPI.ACTIVITY_WHERE, IndoorMapActivity.class.getName());
        b.putString(FMAPI.ACTIVITY_MAP_ID, Tools.OUTSIDE_MAP_ID);
        FMAPI.instance().gotoActivity(this, OutdoorMapActivity.class, b);
        this.finish();
    }

    // init window
    private void initWindow() {
        ///////////////////////点击模型弹窗//////////////////
        initModelInfoWindow();

        ////////////////////点击模型弹窗的去这里按钮后的弹窗///////////////////////
        initOpenNaviWindow();

        ///////////////////////点击开始导航, 显示导航处理窗口////////////////
        initNaviProcessingWindow();
    }

    // 导航模式的窗口
    private void initNaviProcessingWindow() {
        final NaviProcessingView processingView = new NaviProcessingView(this);
        mOpenNaviProcessWindow = new CustomPopupWindow(this, processingView);
        mOpenNaviProcessWindow.setOnWindowCloseListener(this);
        processingView.getStopNaviButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FMLocationService.instance().setInNavigationMode(false);
                mLocationView.setBackgroundResource(R.drawable.fm_green_normal_button);
                FMAPI.instance().isInLocating = false;

                clearCalculatedPathResults();
                clearWalkedTemporaryValue();
                clearCalculateRouteLineMarker();
                clearStartAndEndMarker();

                processingView.getStopNaviButton().setText("结束");

                mMap.setFMViewMode(FMViewMode.FMVIEW_MODE_3D);

                mOpenNaviProcessWindow.close();
            }
        });
    }

    // 路径规划的窗口
    private void initOpenNaviWindow() {
        final NaviView naviView = new NaviView(this);
        mOpenNaviWindow = new CustomPopupWindow(this, naviView);
        mOpenNaviWindow.setOnWindowCloseListener(this);
        mOpenNaviWindow.openSwipeDownGesture();
        mOpenNaviWindow.setAnimationStyle(R.style.PopupPullFromBottomAnimation);
        naviView.getNaviExchageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int        startGroupId  = mStartMarker.getGroupId();
                FMMapCoord startPosition = mStartMarker.getPosition();
                String     startStr      = startPosition.getDescription();

                int        endGroupId  = mEndMarker.getGroupId();
                FMMapCoord endPosition = mEndMarker.getPosition();
                String     endStr      = endPosition.getDescription();

                clearStartAndEndMarker();
                clearCalculateRouteLineMarker();

                naviView.setStartText(endStr);
                naviView.setEndText(startStr);

                addStartMarker(endGroupId, endPosition, true);
                addEndMarker(startGroupId, startPosition, true);

                calculateAndDrawRoute();

            }
        });
        naviView.getOpenNaviButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOpenNaviWindow.close();

                // 判断起点是否在当前地图
                String startPointMapId = FMNaviAnalysisHelper.instance().getStartNaviMultiPoint().getNaviAnalyser().getMapId();
                if (startPointMapId.equals(mMap.currentMapId())) {
                    // 切换楼层
                    int groupId = FMNaviAnalysisHelper.instance().getStartNaviMultiPoint().getGroupId();
                    if (groupId != mMap.getFocusGroupId()) {
                        needSwitchFloor(groupId, true, true);
                    }

                    NaviProcessingView processingView    = (NaviProcessingView) mOpenNaviProcessWindow.getConvertView();
                    String             remainingDistance = StringUtils.fixedRemainingDistance(FMAPI.instance().mInitNeedDistance);
                    String             remainingTime     = StringUtils.fixedInitTime(FMAPI.instance().mInitNeedTime);

                    processingView.setRemainingDistance(remainingDistance);
                    processingView.setRemainingTime(remainingTime);

                    // 添加定位图片  只能是起点
                    dealAddLocateMarker(groupId,
                                        FMNaviAnalysisHelper.instance().getStartNaviMultiPoint().getPosition(),
                                        mLocationLayer);

                    mOpenNaviProcessWindow.showAsDropDown(mTopBarView, 0, mTopBarView.getHeight());

                } else {
                    // 切地图
                    if (startPointMapId.equals(Tools.OUTSIDE_MAP_ID)) { // 室外  in->out
                        // 跳转->路径规划
                        Bundle b = new Bundle();
                        b.putString(FMAPI.ACTIVITY_WHERE, IndoorMapActivity.class.getName());
                        b.putString(FMAPI.ACTIVITY_MAP_ID, FMNaviAnalysisHelper.instance().getStartNaviMultiPoint().getNaviAnalyser().getMapId());
                        b.putInt(FMAPI.ACTIVITY_MAP_GROUP_ID, FMNaviAnalysisHelper.instance().getStartNaviMultiPoint().getGroupId());   // groupId
                        FMAPI.instance().gotoActivity(IndoorMapActivity.this, OutdoorMapActivity.class, b);
                        mInstance.finish();
                    } else {  // in->in
                        isLoadMapCompleted = false;
                        needSwitchMap(startPointMapId, true);
                    }

                }

                // 这里开始导航
                FMLocationService.instance().setInNavigationMode(true);
            }
        });
    }

    // 模型信息的窗口
    private void initModelInfoWindow() {
        InsideModelView modelView = new InsideModelView(this);
        mOpenModelInfoWindow = new CustomPopupWindow(this, modelView);
        mOpenModelInfoWindow.openSwipeDownGesture();
        mOpenModelInfoWindow.setOnWindowCloseListener(this);
        mOpenModelInfoWindow.setAnimationStyle(R.style.PopupPullFromBottomAnimation);
        modelView.getArriveButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearCalculateRouteLineMarker();
                clearStartAndEndMarker();

                // 到这去
                /**
                 * 如果未开启定位, 则开启定位, 弹出进度条;
                 */
                needLocate(true);

            }
        });
    }

    // clear
    public void clearCalculatedPathResults() {
        mPathResults.clear();
    }

    public void clearCalculateRouteLineMarker() {
        if (mCurrentCalculateLineMarkerLineMarker != null) {
            mLineLayer.removeMarker(mCurrentCalculateLineMarkerLineMarker);
            mMap.updateMap();
            mCurrentCalculateLineMarkerLineMarker = null;
        }
    }

    public void clearStartAndEndMarker() {
        if (mStartMarkerLayer != null) {
            mMap.removeLayer(mStartMarkerLayer);
            mStartMarker = null;
            mStartMarkerLayer = null;
        }
        if (mEndMarkerLayer != null) {
            mMap.removeLayer(mEndMarkerLayer);
            mEndMarker = null;
            mStartMarkerLayer = null;
        }
        mMap.updateMap();
    }

    public void clearSpecialMarker() {
        if (mSpecialWorkMarker != null && mSpecialWorkImageLayer != null) {
            mMap.removeLayer(mSpecialWorkImageLayer);
            mMap.updateMap();
            mSpecialWorkMarker = null;
            mSpecialWorkImageLayer = null;
        }
    }

    public void clearMeLocationMarker() {
        if (mMeLocationMarker != null) {
            mLocationLayer.removeMarker(mMeLocationMarker);
            mMap.updateMap();
            mMeLocationMarker = null;
        }
    }

    // 清除计算走过距离的临时变量, 便于下次计算
    public void clearWalkedTemporaryValue() {
    }


    // navigation
    public void dealMapFollow(ArrayList<FMMapCoord> points,
                              int pIndex,
                              FMTotalMapCoord currentTotalMapCoord) {
        calculateMapRotatedAngle(points, pIndex, currentTotalMapCoord.getMapCoord());
        mMap.setMapCenter(currentTotalMapCoord.getMapCoord());
    }

    private float mLastLineAngle = -1;
    // calculate angle
    private void calculateMapRotatedAngle(ArrayList<FMMapCoord> points,
                                          int pIndex,
                                          FMMapCoord pCurrCoord) {

        if (pIndex <= points.size() - 2) {
            float currLineAngle = (float) FMAPI.calcuAngle(pCurrCoord, points.get(pIndex + 1));
            if (mLastLineAngle != -1 && Math.abs(currLineAngle - mLastLineAngle)>0 ) {
                mMap.setRotate(-(float) FMMath.degreeToRad(currLineAngle));
                mMap.updateMap();
            }
            mLastLineAngle = currLineAngle;
        }
    }

    // 导航模式下窗口内容变化
    private void dealNavigationTextViewChanged(float[] naviResults,
                                               ArrayList<FMMapCoord> points,
                                               int currGroupId,
                                               FMTotalMapCoord currentTotalMapCoord) {
        float distance = naviResults[0];     // 偏离路线距离     做路径重规划的基准
        int index = (int) naviResults[1];    // 此时位置index

        if (distance > FMAPI.NAVIGATION_OFFSET_MAX_DISTANCE) {
            // 在这路径重规划
        }

        // 下面计算实时距离 和 时间
        float walkedDis = walkedDistance(points, currGroupId, index, currentTotalMapCoord);
        setNavigationViewContent(walkedDis);
    }

    // 设置导航模式的窗口内容
    private void setNavigationViewContent(float pWalkedDis) {
        NavigationUtils.NavigationDescription nvd = NavigationUtils.forNavigationDescription(pWalkedDis);

        // 导航窗口内容
        NaviProcessingView npv = (NaviProcessingView) mOpenNaviProcessWindow.getConvertView();
        npv.setRemainingDistance(nvd.getRemainedDistanceDesc());
        npv.setRemainingTime(nvd.getRemainedTimeDesc());

        // 完成导航
        if (nvd.getRemainedDistance() <= 0 || nvd.getRemainedTime() <= 0) {
            FMLocationService.instance().setInNavigationMode(false);
            npv.getStopNaviButton().setText("导航完成");
        }
    }


    // 走过的距离
    public float walkedDistance(ArrayList<FMMapCoord> points,
                                int currGroupId,
                                int index,
                                FMTotalMapCoord currentTotalMapCoord) {
        FMLog.le("walkedDistance", "index: " + index + ", currGroupId: " + currGroupId);

        if (index > points.size() - 1) {
            return FMAPI.instance().mWalkedDistance;
        }

        // 当前层当前段的距离
        float currIndexDis = (float) FMMapCoord.length(points.get(index), currentTotalMapCoord.getMapCoord());
        float segmentLen = 0;
        if (index > 0) {
            for (int i=1; i< index; ++i) {
                segmentLen += (float) FMMapCoord.length(points.get(i - 1), points.get(i));
            }
        }


        // 走过层的层
        float groupLen = 0;
        int currGroupIndex = getIndexInPassedGroups(currGroupId);
        if (currGroupIndex > 0) {
            for (int i=0; i< currGroupIndex; ++i) {
                groupLen += calculateRouteInGroupDistance(mPassGroupIdsByCalculateRoute[i]);
            }
        }

        FMAPI.instance().mWalkedDistance = groupLen + segmentLen + currIndexDis;

        CustomToast.show(this, "index: " +index+", groupIndex: "+ currGroupIndex +", walkDis: "+ FMAPI.instance().mWalkedDistance);
        return FMAPI.instance().mWalkedDistance;
    }


    // 获取当前group在pass层的下标
    private int getIndexInPassedGroups(int currGroupId) {
        int index = -1;
        for (int i=0; i< mPassGroupIdsByCalculateRoute.length; i++) {
            if (mPassGroupIdsByCalculateRoute[i] == currGroupId) {
                index = i;
                break;
            }
        }
        return index;
    }


    // 获取每一层的路径点集合
    private ArrayList<FMMapCoord> getGroupRouteCoords(int groupId) {
        for (FMNaviResult r : mPathResults) {
            if (r.getGroupId() == groupId) {
                if (r.getPointList().size()>2) {
                    return r.getPointList();
                }
            }
        }
        return new ArrayList<>(0);
    }


    // 获取每层的路径长度
    private float calculateRouteInGroupDistance(int groupId) {
        float dis = 0;
        for (FMNaviResult r : mPathResults) {
            if (r.getGroupId() == groupId) {
                if (r.getPointList().size()>2) {
                    dis = (float)Tools.getFMNaviAnalyserByMapId(mMap.currentMapId()).getSceneRouteLengthByGroupId(groupId);
                    break;
                }
            }
        }
        return dis;
    }


    @Override
    public void onClose(boolean isGestureClose, View v) {
        if (v instanceof InsideModelView) {

        } else if (isGestureClose && v instanceof NaviView) {
            clearCalculateRouteLineMarker();
            clearStartAndEndMarker();
            clearMeLocationMarker();

        } else if (v instanceof NaviProcessingView) {
            clearCalculateRouteLineMarker();
            clearStartAndEndMarker();
            clearMeLocationMarker();
            clearCalculatedPathResults();
        }
    }


    ///////////////////////定位////////////////////////////

    /**
     * 需要定位。
     */
    public void needLocate(boolean isArrive) {
        mProgressDialog.setInfoViewContext("定位中...");
        mProgressDialog.show();
        FMLocationService.instance().start();
        FMTotalMapCoord locatePosition = FMLocationService.instance().getFirstMyLocatePosition();
        if (locatePosition == null) {
            waitLocate(isArrive);
        } else {
            Message message = mHandler.obtainMessage();
            if (isArrive) {
                message.what = FMAPI.LOCATE_SUCCESS_TO_ARRIVE;
            } else {
                message.what = FMAPI.LOCATE_SUCCESS_TO_LOCATE;
            }
            message.obj = locatePosition;
            mHandler.sendMessage(message);
        }
    }

    private void waitLocate(final boolean isArrive) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean         isRun          = true;
                long            time           = System.currentTimeMillis();
                FMTotalMapCoord locatePosition = null;
                while (isRun) {
                    synchronized (FMLocationService.instance()) {
                        locatePosition = FMLocationService.instance().getFirstMyLocatePosition();
                        FMLocationService.instance().clearFirstMyLocatePosition();
                    }
                    if (locatePosition != null) {
                        isRun = false;
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException pE) {

                    }
                    if (System.currentTimeMillis() - time > FMLocationService.LOCATE_MAX_WAIT_TIME) {
                        isRun = false;
                        locatePosition = null;
                    }
                }

                Message message = mHandler.obtainMessage();
                if (locatePosition == null) { // locate failure
                    FMLocationService.instance().stop();
                    message.what = FMAPI.LOCATE_FAILURE;
                } else {
                    if (isArrive) {
                        message.what = FMAPI.LOCATE_SUCCESS_TO_ARRIVE;
                    } else {
                        message.what = FMAPI.LOCATE_SUCCESS_TO_LOCATE;
                    }
                    message.obj = locatePosition;
                }
                mHandler.sendMessage(message);

            }
        }).start();
    }

    // 事件处理
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FMAPI.LOCATE_SUCCESS_TO_ARRIVE: {  // 2     //定位成功   到这去
                    mProgressDialog.dismiss();
                    mInitLocatePosition = (FMTotalMapCoord) msg.obj;
                    dealLocateWhenSuccess(mInitLocatePosition, true);
                }   break;

                case FMAPI.LOCATE_SUCCESS_TO_LOCATE: {  //     //定位成功
                    mProgressDialog.dismiss();
                    mInitLocatePosition = (FMTotalMapCoord) msg.obj;
                    dealLocateWhenSuccess(mInitLocatePosition , false);
                }   break;


                case FMAPI.LOCATE_FAILURE:   // 1
                    mProgressDialog.dismiss();
                    clearMeLocationMarker();
                    CustomToast.show(OutdoorMapActivity.mInstance, "定位失败,请重新尝试");
                    mLocationView.setBackgroundResource(R.drawable.fm_green_normal_button);
                    break;
            }
        }
    };

    private void dealLocateWhenSuccess(FMTotalMapCoord locatePosition, boolean isArrive) {
        /**
         * 1. 等待定位, 判断是否切换地图;
         * 2. 如果切换了地图,则在其中规划路径后,打开导航模式窗口;
         * 3. 如果未切换地图,则直接路径规划,打开导航窗口。
         */
        String locateMapId = locatePosition.getMapId();
        if (locateMapId.equals(mMap.currentMapId())) {  // 室内地图
            dealLocateInCurrentMap(locatePosition, isArrive);     // 室内同地图路径规划
        } else {        // 起点在另一地图, 先路径规划,弹出路径规划窗口,点击导航后切地图
            dealLocateInOtherMap(locatePosition, isArrive);
        }
    }

    private void dealLocateInCurrentMap(FMTotalMapCoord locatePosition, boolean isArrive) {
        // 切换楼层
        int locateGroupId = locatePosition.getGroupId();
        if (locateGroupId != mMap.getFocusGroupId()) {
            needSwitchFloor(locateGroupId, true, false);
        }

        // 关闭窗口
        mOpenModelInfoWindow.close();

        if (isArrive) {
            // 起点
            FMMapCoord startPoint = locatePosition.getMapCoord();
            startPoint.setDescription("我的位置");
            addStartMarker(locatePosition.getGroupId(), startPoint, true);
            // 终点
            FMMapCoord endPoint = mCurrentModel.getCenterMapCoord();
            endPoint.setDescription(mCurrentModel.getName());
            addEndMarker(mCurrentModel.getGroupId(), endPoint, true);

            // 画线
            if (calculateAndDrawRoute()) {
                NaviView naviView = (NaviView) mOpenNaviWindow.getConvertView();
                naviView.setStartText("我的位置");
                naviView.setEndText(mCurrentModel.getName());

                mOpenNaviWindow.showAsDropDown(mMapView, 0, -mMapView.getHeight());
            }
        } else {
            dealAddLocateMarker(locateGroupId, locatePosition.getMapCoord(), mLocationLayer);
            animateCenterWithZoom(locatePosition.getGroupId(), locatePosition.getMapCoord());
        }

        mMap.updateMap();
    }

    /**
     * 设置切换地图的状态
     * @param locatePosition
     */
    private void dealLocateInOtherMap(FMTotalMapCoord locatePosition, boolean isArrive) {
        if (isArrive) {
            mOpenModelInfoWindow.close();

            // 设置终点并添加标注物
            FMMapCoord endPoint = mCurrentModel.getCenterMapCoord();
            endPoint.setDescription(mCurrentModel.getName());
            addEndMarker(mCurrentModel.getGroupId(), endPoint, true);

            // 设置起点
            FMMapCoord startPoint = locatePosition.getMapCoord();
            startPoint.setDescription("我的位置");
            FMNaviAnalysisHelper.instance().setStartNaviMultiPoint(locatePosition.getGroupId(),
                                                                   Tools.getFMNaviAnalyserByMapId(locatePosition.getMapId()),
                                                                   startPoint);
            // 路径规划
            if (calculateAndDrawRoute()) {
                NaviView naviView = (NaviView) mOpenNaviWindow.getConvertView();
                naviView.setStartText("我的位置");
                naviView.setEndText(mCurrentModel.getName());

                mOpenNaviWindow.showAsDropDown(mMapView, 0, -mMapView.getHeight());
            }

        } else {   // 定位
            // 设置路径规划的状态
            if (locatePosition.getMapId().equals(Tools.OUTSIDE_MAP_ID)) { //室外  out
                // 跳转->定位
                Bundle b = new Bundle();
                b.putString(FMAPI.ACTIVITY_WHERE, IndoorMapActivity.class.getName());
                b.putString(FMAPI.ACTIVITY_MAP_ID, locatePosition.getMapId());
                b.putSerializable(FMAPI.ACTIVITY_LOCATE_POSITION, locatePosition);
                b.putString(FMAPI.ACTIVITY_TARGET, TARGET_LOCATE);
                FMAPI.instance().gotoActivity(this, OutdoorMapActivity.class, b);
                mInstance.finish();
            } else {  // in
                isLoadMapCompleted = false;
                needSwitchMap(locatePosition.getMapId(), false);
            }
        }
    }


    // 处理添加定位图标到图层的问题
    private void dealAddLocateMarker(int groupId, FMMapCoord position, FMLocationLayer layer) {
        if (layer == null) {  // 只创建定位图标
            if (mMeLocationMarker == null) {
                mMeLocationMarker = FMAPI.instance().buildLocationMarker(groupId, position);
            } else {
                if (mMeLocationMarker.getHandle() == 0) {     // 更新位置
                    mMeLocationMarker.setGroupId(groupId);
                    mMeLocationMarker.setPosition(position);
                }
            }
        } else {
            if (mMeLocationMarker != null) {
                if (mMeLocationMarker.getHandle() != 0) {  //已存在定位图标   更新位置
                    mMeLocationMarker.updatePosition(groupId, position);
                } else {
                    mLocationLayer.addMarker(mMeLocationMarker);
                }
            } else {   // 不存在
                mMeLocationMarker = FMAPI.instance().addLocationMarker(mLocationLayer, groupId, position);
            }
        }
    }


    private void animateCenterWithZoom(int groupId, FMMapCoord initMapCoord) {
//        FMScreenCoord sc = mMap.toFMScreenCoord(groupId,
//                                                FMMapCoordZType.MAPCOORDZ_EXTENT,
//                                                initMapCoord);

        mSceneAnimator.animateMoveToScreenCenter(initMapCoord)
                .animateZoom(2)
                .setInterpolator(new FMLinearInterpolator(FMInterpolator.STAGE_INOUT))
                .setDurationTime(1000)
                .start();
    }

    private Integer[] getPassGroupIdsByCalculateRoute() {
        ArrayList<Integer> passGroups = new ArrayList<>();
        for (FMNaviResult nr : mPathResults) {
            Integer groupId = Integer.valueOf(nr.getGroupId());
            if (passGroups.contains(groupId)) {
                continue;
            } else {
                passGroups.add(groupId);
            }
        }
        Integer[] arr = new Integer[passGroups.size()];
        passGroups.toArray(arr);
        return arr;
    }

    @Override
    public void onReceivePositionInCallService(String pMacAddress, FMTotalMapCoord pFMTotalMapCoord) {
        CustomToast.show(this, "indoor macAddr: " + pMacAddress + ", " + pFMTotalMapCoord.toString());

        if (mMyMarkerInCall == null) {
            addMyLocateInCall();
        }

        if (mWaiterMarkerInCall == null) {
            addWaiterLocateInCall();
        }

        if (mMeLocationMarker != null) {
            mMeLocationMarker.setVisible(false);
        }

        // make sure see waiter's position in map
        if (pMacAddress.equals(WaiterMacAddress)) {
            String mapId = pFMTotalMapCoord.getMapId();
            if (Tools.isInsideMap(mapId)) {
                if (!mMap.currentMapId().equals(mapId)) {  // need to switch a new inside map
                    needSwitchMapInNavigation(mapId);
                    return;
                }
            } else { // waiter's position is in outside map, need to switch map
                enterOutside();
                return;
            }
        }

        // update marker position
        FMImageLayer layer = mMap.getCallSeverLocateLayer(mMap.getFocusGroupId());
        for (FMImageMarker m : layer.getAll()) {
            if (m.getDescription().equals(pMacAddress)) {
                if (pFMTotalMapCoord.getMapId().equals(mMap.currentMapId())) {  // judge marker if is in current map
                    m.setVisible(true);
                    m.updatePosition(pFMTotalMapCoord.getMapCoord());
                } else {    // hidden marker if is not in current map
                    m.setVisible(false);
                }
                break;
            }
        }
        mMap.updateMap();
    }

    private boolean                     mNeedBackToMyLocation = false;
    private FMConstraintRoad            mConstraintRoad       = new FMConstraintRoad();
    private OnFMReceiveLocationListener mLocationListener     = new OnFMReceiveLocationListener() {

        @Override
        public void onReceiveLocation(int type, FMTotalMapCoord lastLocation, FMTotalMapCoord currentLocation, final float angle) {
            String logC = "type: " + type+" ,"+ currentLocation.toString();
            if (!isLoadMapCompleted ) {
                return;
            }

            if (mLocationLayer == null) {
                return;
            }

            if (mMeLocationMarker == null) {
                mMeLocationMarker = FMAPI.instance().addLocationMarker(mLocationLayer,
                                                                       currentLocation.getGroupId(),
                                                                       currentLocation.getMapCoord());
                mMap.updateMap();
            } else if (mMeLocationMarker.getHandle() == 0) {
                return;
            }

            boolean isInNavigationMode = FMLocationService.instance().isInNavigationMode();

            if (isInNavigationMode) {
                mMap.setFMViewMode(FMViewMode.FMVIEW_MODE_2D);
                dealNavigationMode(type, lastLocation, currentLocation, angle);
                return;
            }

            // click back to my location button
            if (mNeedBackToMyLocation && !isInNavigationMode) {
                if (!mMap.currentMapId().equals(currentLocation.getMapId())) {  // switch map
                    if (Tools.isInsideMap(currentLocation.getMapId())) {
                        needSwitchMapInNavigation(currentLocation.getMapId());
                    } else {
                        enterOutside();
                    }

                    mNeedBackToMyLocation = false;
                    return;
                }

                // 切换楼层
                if (mMap.getFocusGroupId() != currentLocation.getGroupId()) {
                    needSwitchFloor(currentLocation.getGroupId(), true, false);
                }

                mNeedBackToMyLocation = false;
            }


            mRTLocateGroupId = currentLocation.getGroupId();

            // warning by open window
            if (mNeedWarningToJumpFloor && !isInNavigationMode) {
                String warnText = "";
                if (!mMap.currentMapId().equals(currentLocation.getMapId())) {  // switch map
                    if (Tools.isInsideMap(currentLocation.getMapId())) {
                        warnText = String.format("您现在在 %s，是否需要跳转？" +
                                                 "若选择 不再提示 将不会在此地图提示，" +
                                                 "需要跳转，则请点击 回到我的位置",
                                                 Tools.getInsideMapName(currentLocation.getMapId()));

                        mSwitchType = 0;
                        getAlertDialog(warnText, currentLocation.getMapId()).show();
                    } else {
                        warnText = String.format("您现在在 %s，是否需要跳转？" +
                                                 "若选择 不再提示 将不会在此地图提示，" +
                                                 "需要跳转，则请点击 回到我的位置",
                                                 Tools.OUTSIDE_MAP_NAME);

                        mSwitchType = 1;
                        getAlertDialog(warnText, currentLocation.getMapId()).show();
                    }
                } else if (mMap.getFocusGroupId() != currentLocation.getGroupId()) {  // switch floor
                    warnText = String.format("您现在在 %s，是否需要跳转？" +
                                             "若选择 不再提示 将不会在此地图提示，" +
                                             "需要跳转，则请点击 回到我的位置", getGroupName(currentLocation.getGroupId()));

                    mSwitchType = 2;
                    getAlertDialog(warnText, currentLocation.getMapId()).show();
                }
            }


            if (mMap.getFocusGroupId() != currentLocation.getGroupId()) {
                if (mMeLocationMarker.isVisible()) {
                    mMeLocationMarker.setVisible(false);
                }
            } else {
                if (!mMeLocationMarker.isVisible()) {
                    mMeLocationMarker.setVisible(true);
                }
            }
            mMeLocationMarker.updateAngleAndPosition(currentLocation.getGroupId(),
                                                     angle,
                                                     currentLocation.getMapCoord());

            mMap.updateMap();
        }
    };

    private void dealNavigationMode(int type, FMTotalMapCoord lastLocation, FMTotalMapCoord currentLocation, float angle) {

        // 在经过的地图集合中移除当前的地图,保证不会切同一个地图
        if (FMNaviAnalysisHelper.instance().getMayPassedMapIdsInNavigation().contains(mMap.currentMapId())) {
            FMNaviAnalysisHelper.instance().getMayPassedMapIdsInNavigation().remove(mMap.currentMapId());
        }

        // deal switch map in there
        String mapId = currentLocation.getMapId();
        if (!mapId.equals(mMap.currentMapId()) &&
            FMNaviAnalysisHelper.instance().getMayPassedMapIdsInNavigation().contains(mapId)) {

            if (Tools.isInsideMap(mapId)) { //
                needSwitchMapInNavigation(currentLocation.getMapId());
            } else {
                enterOutside();
            }

            return;
        }

        // the locate group is in passed groups of navigation
        if (getIndexInPassedGroups(currentLocation.getGroupId()) >= 0) {
            // deal switch floor in there
            if (mMap.getFocusGroupId() != currentLocation.getGroupId()) {
                needSwitchFloor(currentLocation.getGroupId(), true, true);
            }

            // [0] distance  [1] index
            float[] naviResults = Tools.getFMNaviAnalyserByMapId(mMap.currentMapId()).naviConstraint(getGroupRouteCoords(currentLocation.getGroupId()),
                                                               currentLocation.getGroupId(),
                                                               lastLocation.getMapCoord(),
                                                               currentLocation.getMapCoord());


            // 当前层
            int currGroupId = currentLocation.getGroupId();
            // 当前层的点集合
            ArrayList<FMMapCoord> points = getGroupRouteCoords(currGroupId);

            // 过滤 楼层
            if (points.size() > 0) {

                dealNavigationTextViewChanged(naviResults, points, currGroupId, currentLocation);

                //导航模式下地图跟随
                if (FMAPI.mNeedMapFollowInNavigation) {
                    dealMapFollow(points, (int)naviResults[1], currentLocation);
                }

            }


            if (mMap.getFocusGroupId() != currentLocation.getGroupId()) {
                mMeLocationMarker.setVisible(false);
            } else {
                mMeLocationMarker.setVisible(true);
            }
            mMeLocationMarker.updateAngleAndPosition(currentLocation.getGroupId(),
                                                     angle,
                                                     currentLocation.getMapCoord());

            mMap.updateMap();
        }
    }

    private String getGroupName(int groupId) {
        String                 name       = "";
        ArrayList<FMGroupInfo> groupInfos = mMapInfo.getGroups();
        for (FMGroupInfo info : groupInfos) {
            if (info.getGroupId() == groupId) {
                name = info.getGroupName();
                break;
            }
        }
        return name;
    }


    AlertDialog mDialog = null;
    private boolean mNeedWarningToJumpFloor = true;
    int mSwitchType = -1;
    int mRTLocateGroupId = 1;
    public AlertDialog getAlertDialog(final String pWarnText, final String pMapId) {
        if (mDialog == null) {
            mDialog = new AlertDialog.Builder(this).setTitle("提示")
            .setNegativeButton("不再提示", new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mNeedWarningToJumpFloor = false;
                    dialog.dismiss();
                }

            }).setPositiveButton("跳转", new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (mSwitchType == 2) {
                        needSwitchFloor(mRTLocateGroupId, true, false);
                    } else if (mSwitchType == 1) {
                        enterOutside();
                    } else if (mSwitchType == 0) {
                        needSwitchMapInNavigation(pMapId);
                    }
                    dialog.dismiss();
                }

            }).create();
        }


        mDialog.setMessage(pWarnText);

        return mDialog;
    }


    /**
     * @method 查询网络状态
     */
    public void ping(final String ip, final TextView tv) {
        String  result = " ";
        String command = "ping -c " + 1 + " " + ip;
        try {
            //            String ip = "www.baidu.com";// 除非百度挂了，否则用这个应该没问题~
            Process p = Runtime.getRuntime().exec(command);// ping 1次
            // 读取ping的内容，可不加。
            InputStream    input        = p.getInputStream();
            BufferedReader in           = new BufferedReader(new InputStreamReader(input));
            StringBuffer   stringBuffer = new StringBuffer();
            String         content      = "";
            while ((content = in.readLine()) != null) {
                stringBuffer.append(content);
            }
            Log.i("PingTAG", "result content : " + stringBuffer.toString());
            // PING的状态
            int status = p.waitFor();
            if (status == 0) {
                result = "successful~";
            } else {
                result = "failed~ cannot reach the IP address";
            }
        } catch (IOException e) {
            result = "failed~ IOException";
        } catch (InterruptedException e) {
            result = "failed~ InterruptedException";
        } finally {
            Log.i("PingTAG", "result = " + result);
        }
        Log.i("PingTAG", "result content : " + result.toString());
        final String msg = result;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                tv.setText(ip+"："+msg);
            }
        });
    }

    // 网络状态查询线程
//    Thread checkIpThread = new Thread(new Runnable() {
//        @Override
//        public void run() {
//            while (true){
//                try {
//                    Thread.sleep(1000);
//                    ping("www.jdjt.net", mPing1TV);
//                    ping("10.11.88.103", mPing2TV);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    });

}
