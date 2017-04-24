package com.fengmap.drpeng;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.mgwaiter.MainActivity;
import com.android.mgwaiter.R;
import com.fengmap.android.FMMapSDK;
import com.fengmap.android.analysis.navi.FMNaviAnalyser;
import com.fengmap.android.analysis.navi.FMNaviMultiAnalyer;
import com.fengmap.android.analysis.navi.FMNaviResult;
import com.fengmap.android.map.FMMap;
import com.fengmap.android.map.FMMapInfo;
import com.fengmap.android.map.FMPickMapCoordResult;
import com.fengmap.android.map.FMViewMode;
import com.fengmap.android.map.animator.FMInterpolator;
import com.fengmap.android.map.animator.FMLinearInterpolator;
import com.fengmap.android.map.animator.FMSceneAnimator;
import com.fengmap.android.map.event.OnFMMapClickListener;
import com.fengmap.android.map.event.OnFMMapInitListener;
import com.fengmap.android.map.event.OnFMNodeListener;
import com.fengmap.android.map.geometry.FMMapCoord;
import com.fengmap.android.map.geometry.FMTotalMapCoord;
import com.fengmap.android.map.layer.FMExternalModelLayer;
import com.fengmap.android.map.layer.FMImageLayer;
import com.fengmap.android.map.layer.FMLabelLayer;
import com.fengmap.android.map.layer.FMLayerProxy;
import com.fengmap.android.map.layer.FMLineLayer;
import com.fengmap.android.map.layer.FMLocationLayer;
import com.fengmap.android.map.layer.FMModelLayer;
import com.fengmap.android.map.marker.FMExternalModel;
import com.fengmap.android.map.marker.FMImageMarker;
import com.fengmap.android.map.marker.FMLineMarker;
import com.fengmap.android.map.marker.FMLocationMarker;
import com.fengmap.android.map.marker.FMNode;
import com.fengmap.android.map.style.FMStyle;
import com.fengmap.android.utils.FMCoordinateConvert;
import com.fengmap.android.utils.FMLog;
import com.fengmap.android.utils.FMMath;
import com.fengmap.android.wrapmv.FMMangroveMapView;
import com.fengmap.android.wrapmv.FMNaviAnalysisHelper;
import com.fengmap.android.wrapmv.Tools;
import com.fengmap.android.wrapmv.db.FMDBMapElement;
import com.fengmap.android.wrapmv.db.FMDBMapElementDAO;
import com.fengmap.android.wrapmv.db.FMDBSearchElement;
import com.fengmap.android.wrapmv.entity.FMActivityGroup;
import com.fengmap.android.wrapmv.entity.FMLineWithImageMarker;
import com.fengmap.android.wrapmv.entity.FMMarkerBuilderInfo;
import com.fengmap.android.wrapmv.entity.FMRoute;
import com.fengmap.android.wrapmv.entity.FMZone;
import com.fengmap.android.wrapmv.service.FMCallService;
import com.fengmap.android.wrapmv.service.FMLocationService;
import com.fengmap.android.wrapmv.service.FMMacService;
import com.fengmap.android.wrapmv.service.OnFMMacAddressListener;
import com.fengmap.android.wrapmv.service.OnFMReceiveLocationListener;
import com.fengmap.android.wrapmv.service.OnFMReceivePositionInCallServiceListener;
import com.fengmap.android.wrapmv.service.OnFMWifiStatusListener;
import com.fengmap.drpeng.adapter.LineAdapter;
import com.fengmap.drpeng.adapter.WorkAdapter;
import com.fengmap.drpeng.common.NavigationUtils;
import com.fengmap.drpeng.common.StringUtils;
import com.fengmap.drpeng.widget.ButtonGroup;
import com.fengmap.drpeng.widget.CustomPopupWindow;
import com.fengmap.drpeng.widget.CustomProgressDialog;
import com.fengmap.drpeng.widget.CustomToast;
import com.fengmap.drpeng.widget.DrawableCenterTextView;
import com.fengmap.drpeng.widget.ModelView;
import com.fengmap.drpeng.widget.NaviProcessingView;
import com.fengmap.drpeng.widget.NaviView;
import com.fengmap.drpeng.widget.RouteView;
import com.fengmap.drpeng.widget.TopBarView;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.fengmap.android.wrapmv.Tools.OUTSIDE_MAP_ID;
import static com.fengmap.drpeng.FMAPI.TARGET_ADD_MARKER;
import static com.fengmap.drpeng.FMAPI.TARGET_CALCULATE_ROUTE;
import static com.fengmap.drpeng.FMAPI.TARGET_LOCATE;
import static com.fengmap.drpeng.FMAPI.TARGET_SELECT_POINT;

/**
 * 室外地图。注意: 退出整个应用前,需要把查询数据库给关了FMDatabaseHelper.getDatabaseHelper().close();
 * @author yangbin
 */
public class OutdoorMapActivity extends Activity implements View.OnClickListener,
                                                            ButtonGroup.OnButtonGroupListener,
                                                            OnFMMapInitListener,
                                                            OnFMMapClickListener,
                                                            CustomPopupWindow.OnWindowCloseListener, OnFMReceivePositionInCallServiceListener {
    public static OutdoorMapActivity mInstance = null;

    private FMMangroveMapView mMapView;
    private FMMap             mMap;
    private FMMapInfo         mMapInfo;
    private FMLayerProxy      mLayerProxy;

    // 路径规划
    private boolean        mNeedLoadCalculatedRoute;                        //地图初始化时是否需要加载规划的路径
    private FMImageLayer   mStartMarkerLayer, mEndMarkerLayer;      //起点图层
    private FMImageMarker mStartMarker, mEndMarker;                //起点标注物
    private FMLineLayer  mLineLayer;             //线图层
    private FMLineMarker mCalculateLineMarker;   // 路径规划的线marker

    private ArrayList<FMNaviResult> mPathResults = new ArrayList<>();

    // 定位
    private FMLocationLayer  mLocationLayer;            //定位图层
    private FMLocationMarker mMeLocationMarker;         //我的位置Marker

    private FMSceneAnimator mSceneAnimator;       //负责点击模型移动到地图屏幕中央的动画对象

    private FMExternalModel mCurrentModel;
    private FMExternalModel mLastModel;

    // 标注
    private FMImageLayer  mSpecialWorkImageLayer;
    private FMImageMarker mSpecialWorkMarker;

    // 业态线路
    private FMImageLayer mRouteImageLayer;
    private int mLastSelectedWorkMarkerIndex = -1;    //记录上一次选中的业态索引
    private int mLastSelectedRouteIndex = -1;         //记录上一次选中的路线索引

    // 弹窗
    private CustomPopupWindow mOpenModelInfoWindow;
    private CustomPopupWindow mOpenNaviWindow;
    private CustomPopupWindow mOpenRouteWindow;
    private CustomPopupWindow mOpenNaviProcessWindow;

    // 业态组
    private FMActivityGroup mFacilityActGroup;
    private FMActivityGroup mFoodActGroup;
    private FMActivityGroup mShopActGroup;

    // UI
    private TopBarView    mTopBarView;
    private ButtonGroup   mButtonGroup;

    private ImageView mLocationView;

    private DrawableCenterTextView mShowRouteView;
    private boolean                isShowRoute;

    private CustomProgressDialog mProgressDialog;
    //other
    private Random mRandom = new Random();
    private boolean isMapLoadCompleted;

    // call service
    private DrawableCenterTextView mCallView;
    private FMImageMarker          mMyMarkerInCall, mWaiterMarkerInCall;

    // 室外API
    private FMDBMapElementDAO mMapElementDAO;
    private FMTotalMapCoord   mInitLocatePosition;
    private FMDBSearchElement mSearchElement;
    private FMDBMapElement    mMapElement;
    private String            mFromWhere;
    private boolean isLocateSuccess = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInstance = this;
        setContentView(R.layout.activity_fengmap);
        init();
    }

    public FMMap getMap() {
        return mMap;
    }

    private void init() {
        mTopBarView = (TopBarView) findViewById(R.id.fm_topbar);
        mTopBarView.setTitle(String.format("%s・%s", "三亚", "三亚湾"));

        mButtonGroup = (ButtonGroup) findViewById(R.id.fm_btgroup);
        mButtonGroup.setOnButtonGroupListener(this);

        mLocationView = (ImageView) findViewById(R.id.fm_map_img_location);
        mLocationView.setOnClickListener(this);

        mShowRouteView = (DrawableCenterTextView) findViewById(R.id.fm_bt_route);
        mShowRouteView.setOnClickListener(this);

        mCallView = (DrawableCenterTextView) findViewById(R.id.fm_bt_call);
        mCallView.setOnClickListener(this);
        mMapView = (FMMangroveMapView) findViewById(R.id.mapview);
        mMap = mMapView.getFMMap();

        // 初始化定位服务
        initFMLocationService();
        // 开启定位服务
        boolean openedResult = FMMapSDK.setLocateServiceState(true);
        if (!openedResult) {
            CustomToast.show(this, "请检测WIFI连接和GPS状态...");
        }

        // 进度条
        mProgressDialog = new CustomProgressDialog(this, R.style.custom_dialog);
        mProgressDialog.setCustomContentView(R.layout.fm_custome_dialog);
        mProgressDialog.setInfoViewContext("加载中...");
        mProgressDialog.show();

        // 搜索
        mMapElementDAO = new FMDBMapElementDAO();

        // 初始化数据
        initJsonData();

        // 初始化弹窗
        initWindow();

        // 处理bundle
        dealIntentBundle();

        mMapView.setManager(FMAPI.instance().mActivityManager,
                            FMAPI.instance().mRouteManager,
                            FMAPI.instance().mZoneManager);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        FMLog.le("OutdoorMapActivity", "OutdoorMapActivity#onNewIntent");

        isMapLoadCompleted = false;
        dealOnNewIntent(intent);
        isMapLoadCompleted = true;

        super.onNewIntent(intent);
    }


    private void dealIntentBundle() {
        String mapId = OUTSIDE_MAP_ID;
        Bundle b = getIntent().getExtras();
        mFromWhere = b.getString(FMAPI.ACTIVITY_WHERE);

        dealWhere(b, mFromWhere);

        isMapLoadCompleted = false;

        mMap.openMapById(mapId);

        mSceneAnimator = new FMSceneAnimator(mMap);

        clearWalkedTemporaryValue();

        mMap.setOnFMMapInitListener(this);
        mMap.setOnFMMapClickListener(this);

    }


    private void dealOnNewIntent(Intent intent) {
        Bundle b = intent.getExtras();
        mFromWhere = b.getString(FMAPI.ACTIVITY_WHERE);

        if (FMCallService.instance().isRunning()) {
            mCallView.setTextColor(Color.WHITE);
            mCallView.setBackgroundResource(R.drawable.fm_green_press_button);
        } else {
            mCallView.setTextColor(Color.parseColor("#70ffffff"));
            mCallView.setBackgroundResource(R.drawable.fm_green_normal_button);
        }


        if (mLocationLayer != null) {
            mLocationLayer.removeAll();
            mMeLocationMarker = null;
        }

        dealWhere(b, mFromWhere);

        clearWalkedTemporaryValue();

        dealInitLoadTask();

        mMap.updateMap();
    }

    private void dealWhere(Bundle pB, String pWhere) {
        if (SearchResultFragment.class.getName().equals(pWhere)) {
            // 从搜索结果界面而来
            mMapElement = (FMDBMapElement) pB.getSerializable(SearchResultFragment.class.getName());

            mSpecialWorkMarker = FMAPI.instance().buildImageMarker(mMapElement.getGroupId(),
                                                                   new FMMapCoord(mMapElement.getX(), mMapElement.getY()),
                                                                   "fmr/water_marker.png",
                                                                   40,
                                                                   FMStyle.FMNodeOffsetType.FMNODE_CUSTOM_HEIGHT,
                                                                   mMapElement.getZ());
        } else if (SearchFragment.class.getName().equals(pWhere)) {
            // 从搜索界面而来
            mSearchElement = (FMDBSearchElement) pB.getSerializable(SearchFragment.class.getName());
            String  target = pB.getString(FMAPI.ACTIVITY_TARGET);
            if (TARGET_ADD_MARKER.equals(target)) {
                // 创建标注
                mSpecialWorkMarker = FMAPI.instance().buildImageMarker(mSearchElement.getGroupId(),
                                                                       new FMMapCoord(mSearchElement.getX(), mSearchElement.getY()),
                                                                       "fmr/water_marker.png",
                                                                       40,
                                                                       FMStyle.FMNodeOffsetType.FMNODE_CUSTOM_HEIGHT,
                                                                       mSearchElement.getZ());
            } else if (TARGET_CALCULATE_ROUTE.equals(target)){
                FMTotalMapCoord myPos = FMLocationService.instance().getFirstMyLocatePosition();
                if (myPos == null) {
                    CustomToast.show(this, "定位失败...");
                    mNeedLoadCalculatedRoute = false;
                }
                FMNaviAnalysisHelper.instance().setStartNaviMultiPoint(myPos.getGroupId(),
                                                                       Tools.getFMNaviAnalyserByMapId(myPos.getMapId()),
                                                                       myPos.getMapCoord());

                // 路径规划
                FMNaviAnalysisHelper.instance().setEndNaviMultiPoint(mSearchElement.getGroupId(),
                                                                     Tools.getFMNaviAnalyserByMapId(mSearchElement.getMapId()),
                                                                     new FMMapCoord(mSearchElement.getX(), mSearchElement.getY()));
                mNeedLoadCalculatedRoute = true;

            } else if (TARGET_SELECT_POINT.equals(target)) {
                // 地图选点

            }
        } else if (MainActivity.class.getName().equals(pWhere)) {
            // 正常进入
        } else if (IndoorMapActivity.class.getName().equals(pWhere)) {
            // 从室内地图界面而来
            // 目的
            String target = pB.getString(FMAPI.ACTIVITY_TARGET);
            if (TARGET_CALCULATE_ROUTE.equals(target)) {  // 路径规划
                closeAllWindow();
                mNeedLoadCalculatedRoute = true;

            } else if (TARGET_LOCATE.equals(target)) {    // 定位
                closeAllWindow();
                mInitLocatePosition = (FMTotalMapCoord) pB.getSerializable(FMAPI.ACTIVITY_LOCATE_POSITION);
                if (mInitLocatePosition != null) {
                    dealAddLocateMarker(mInitLocatePosition.getGroupId(), mInitLocatePosition.getMapCoord(), mLocationLayer);
                }
                mLocationView.setBackgroundResource(R.drawable.fm_green_press_button);
            }
        }

    }

    // 初始化窗口
    private void initWindow() {
        ///////////////////////点击模型弹窗//////////////////
        initModelViewWindow();

        ////////////////////点击模型弹窗的去这里按钮后的弹窗///////////////////////
        initNaviViewWindow();

        ///////////////////////点击开始导航, 显示导航处理窗口////////////////
        initNaviProcessingViewWindow();

    }

    private void initRoutViewWindow() {
        if (mOpenRouteWindow != null) {
            return;
        }
        // 业态内容视图
        RouteView routeView = new RouteView(this);
        routeView.setManager(FMAPI.instance().mRouteManager, FMAPI.instance().mActivityManager);
        FMRoute   route  = FMAPI.instance().mRouteManager.createRoute("data/route1.json");
        FMRoute[] routes = {route };
        routeView.setData(routes);

        // 业态弹窗
        mOpenRouteWindow = new CustomPopupWindow(this, routeView);
        mOpenRouteWindow.setOnWindowCloseListener(this);
        mOpenRouteWindow.openSwipeDownGesture();
        mOpenRouteWindow.setAnimationStyle(R.style.PopupPullFromBottomAnimation);

        // 业态线标注物
        final LineAdapter la = (LineAdapter) routeView.getLineView().getAdapter();
        la.setSelectedPosition(0);
        la.setOnLineItemSelectedListener(new LineAdapter.OnLineItemSelectedListener() {
            @Override
            public void onItemSelected(int position) {
                if (mLastSelectedRouteIndex>=0) {
                    FMRoute lastRoute = la.getRoute(mLastSelectedRouteIndex);
                    mMapView.hiddenRouteOnMap(lastRoute);
                }
                FMRoute currentRoute = la.getRoute(position);
                mMapView.showRouteOnMap(currentRoute);
            }
        });

        // 业态图片标注物
        WorkAdapter wa = (WorkAdapter) routeView.getWorkView().getAdapter();
        wa.setOnWorkerItemSelectedListener(new WorkAdapter.OnWorkerItemSelectedListener() {
            @Override
            public void onWorkerItemSelected(int routePosition, int workerPosition) {
                FMRoute               route  = la.getRoute(routePosition);
                FMLineWithImageMarker marker = FMAPI.instance().mRouteManager.getMarker(route.getRouteCode());
                if (marker == null) {
                    return;
                }

                if (mLastSelectedWorkMarkerIndex >=0) {
                    marker.getImageMarkers()[mLastSelectedWorkMarkerIndex].updateSize(30, 30);
                }

                mLastSelectedWorkMarkerIndex = workerPosition;

                // 找到当前对应的marker
                FMImageMarker currentWorkerMarker = marker.getImageMarkers()[workerPosition];
                if (currentWorkerMarker == null) {
                    return;
                }

                currentWorkerMarker.updateSize(36, 36);
                mMap.updateMap();
            }
        });
    }

    // 导航模式下的窗口
    private void initNaviProcessingViewWindow() {
        final NaviProcessingView processingView = new NaviProcessingView(this);
        mOpenNaviProcessWindow = new CustomPopupWindow(this, processingView);
        mOpenNaviProcessWindow.setOnWindowCloseListener(this);
        processingView.getStopNaviButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FMLocationService.instance().setInNavigationMode(false);
                FMAPI.instance().isInLocating = false;
                mLocationView.setBackgroundResource(R.drawable.fm_green_normal_button);

                clearWalkedTemporaryValue();
                clearCalculatedPathResults();
                clearStartAndEndMarker();
                clearCalculateRouteLineMarker();
                dealViewChangedWhenOverNavigationMode();

                mMap.setFMViewMode(FMViewMode.FMVIEW_MODE_3D);

                processingView.getStopNaviButton().setText("结束");

                mOpenNaviProcessWindow.close();
            }
        });
    }

    // 路径规划窗口
    private void initNaviViewWindow() {
        final NaviView naviView = new NaviView(this);
        mOpenNaviWindow = new CustomPopupWindow(this, naviView);
        mOpenNaviWindow.openSwipeDownGesture();  //开启下滑关闭手势
        mOpenModelInfoWindow.setAnimationStyle(R.style.PopupPullFromBottomAnimation);
        mOpenNaviWindow.setOnWindowCloseListener(this);
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
                // 设置导航
                FMLocationService.instance().setInNavigationMode(true);

                // 判断起点是否在当前地图
                if (FMNaviAnalysisHelper.instance().getStartNaviMultiPoint().getNaviAnalyser().getMapId().equals(OUTSIDE_MAP_ID)) {
                    NaviProcessingView processingView = (NaviProcessingView) mOpenNaviProcessWindow.getConvertView();

                    String remainingDistance = StringUtils.fixedRemainingDistance(FMAPI.instance().mInitNeedDistance);
                    String remainingTime = StringUtils.fixedInitTime(FMAPI.instance().mInitNeedTime);

                    processingView.setRemainingDistance(remainingDistance);
                    processingView.setRemainingTime(remainingTime);

                    // 添加定位图片   只能是起点
                    dealAddLocateMarker(FMNaviAnalysisHelper.instance().getStartNaviMultiPoint().getGroupId(),
                                        FMNaviAnalysisHelper.instance().getStartNaviMultiPoint().getPosition(),
                                        mLocationLayer);


                    dealViewChangedWhenOpenNavigationMode();
                    mOpenNaviProcessWindow.showAsDropDown(mMapView, 0, -mMapView.getHeight());
                } else {
                    // 切地图
                    Bundle b = new Bundle();
                    String mapID = FMNaviAnalysisHelper.instance().getStartNaviMultiPoint().getNaviAnalyser().getMapId();
                    b.putString(FMAPI.ACTIVITY_WHERE, OutdoorMapActivity.class.getName());
                    b.putString(FMAPI.ACTIVITY_MAP_ID, mapID);
                    b.putInt(FMAPI.ACTIVITY_MAP_GROUP_ID, FMNaviAnalysisHelper.instance().getStartNaviMultiPoint().getGroupId());

                    b.putString(FMAPI.ACTIVITY_HOTEL_NAME, Tools.getInsideMapName(mapID));

                    // 关闭窗口
                    mOpenNaviWindow.close();

                    FMAPI.instance().gotoActivity(OutdoorMapActivity.this, IndoorMapActivity.class, b);
                }
            }
        });
    }

    // 模型信息窗口
    private void initModelViewWindow() {
        ModelView modelView = new ModelView(this);
        mOpenModelInfoWindow = new CustomPopupWindow(this, modelView);
        mOpenModelInfoWindow.setAnimationStyle(R.style.PopupPullFromBottomAnimation);
        mOpenModelInfoWindow.openSwipeDownGesture();  //开启下滑关闭手势
        // 到这里去
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

    ArrayList<FMMapCoord> GeoMapCoords = new ArrayList<>();
    // 初始化数据
    private void initJsonData() {
        FMAPI.instance().mActivityManager.setDataResources("data/acts.json", false);

        mFacilityActGroup = FMAPI.instance().mActivityManager.createGroup("data/facility.json");
        mFoodActGroup = FMAPI.instance().mActivityManager.createGroup("data/food.json");
        mShopActGroup = FMAPI.instance().mActivityManager.createGroup("data/shop.json");
    }
    
    private void prepareTestData() {
        byte[] buffer = FMMapSDK.getFMResourceManager().readAssetsFile("data/geo.json");
        if (buffer != null) {
            String          content = new String(buffer);
            CoordCollection array   = new Gson().fromJson(content, CoordCollection.class);
            for (int i=0; i<array.coordinates.length; i++) {
                double[] temp = FMCoordinateConvert.wgs2WebMercator(array.coordinates[i][0],
                                                                    array.coordinates[i][1]);
                GeoMapCoords.add(new FMMapCoord(temp[0], temp[1]));

            }
            array.coordinates = new double[0][0];
            array = null;
            content = null;
            buffer = null;
        }
    }

    static class CoordCollection {
        double[][] coordinates;
    }

    private void openUploadData() {
        FMMapSDK.setLocateServiceState(false);
        FMMapSDK.setCallServiceState(true);


        new Thread(new Runnable() {
            int index = 0;
            @Override
            public void run() {
                while (true) {
                    if (index>=GeoMapCoords.size()-1) {
                        index = 0;
                    }
                    FMMapCoord mc = GeoMapCoords.get(index);
                    FMTotalMapCoord temp = new FMTotalMapCoord();
                    temp.setMapCoord(mc);
                    temp.setGroupId(1);
                    FMCallService.instance().uploadPosition("",
                                                            temp);
                    index++;

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }


    @Override
    public void onBackPressed() {
        if (mProgressDialog.isShowing()) {
            return;
        }

        if (tryCloseAllWindow()) {
            return;
        }

        if (mSceneAnimator != null) {
            mSceneAnimator.cancel();
        }

        clearWalkedTemporaryValue();
        clearCalculatedPathResults();
        clearCalculateRouteLineMarker();
        clearStartAndEndMarker();
        clearMeLocationMarker();

        if (SearchFragment.class.getName().equals(mFromWhere)) {
            Bundle b = new Bundle();
            b.putString(FMAPI.TARGET_FRAGMENT, SearchFragment.class.getName());
            b.putSerializable(FMAPI.ACTIVITY_OBJ_SEARCH_HISTORY, mSearchElement);
            FMAPI.instance().gotoActivity(this, SearchActivity.class, b);
            mFromWhere = null;
        } else if (SearchResultFragment.class.getName().equals(mFromWhere)) {
            Bundle b = new Bundle();
            b.putString(FMAPI.TARGET_FRAGMENT, SearchResultFragment.class.getName());
            b.putSerializable(FMAPI.ACTIVITY_OBJ_SEARCH_RESULT, mMapElement);
            FMAPI.instance().gotoActivity(this, SearchActivity.class, b);
            mFromWhere = null;
        } else {
            super.onBackPressed();
        }
    }

    private boolean tryCloseAllWindow() {
        if (mOpenNaviProcessWindow !=null && mOpenNaviProcessWindow.isShowing()) {
            CustomToast.show(this, "请结束导航模式");
            return true;
        }
        if (mOpenModelInfoWindow!=null && mOpenModelInfoWindow.isShowing()) {
            mOpenModelInfoWindow.close();
            return true;
        }
        if (mOpenNaviWindow !=null && mOpenNaviWindow.isShowing()) {
            mOpenNaviWindow.close();
            return true;
        }
        if (mOpenRouteWindow != null && mOpenRouteWindow.isShowing()) {
            mOpenRouteWindow.close();
            return true;
        }
        return false;
    }

    // 关闭所有窗口
    private void closeAllWindow() {
        if (mOpenNaviProcessWindow!=null && mOpenNaviProcessWindow.isShowing()) {
            mOpenNaviProcessWindow.close();
        }
        if (mOpenModelInfoWindow!=null &&mOpenModelInfoWindow.isShowing()) {
            mOpenModelInfoWindow.close();
        }
        if (mOpenNaviWindow!=null && mOpenNaviWindow.isShowing()) {
            mOpenNaviWindow.close();
        }
        if (mOpenRouteWindow != null && mOpenRouteWindow.isShowing()) {
            mOpenRouteWindow.close();
        }
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
        FMLocationService.instance().unregisterListener(mLocationListener);
        FMCallService.instance().unregisterCallServiceListener(this);

//        if (checkIpThread.isAlive()) {
//            checkIpThread.interrupt();
//        }

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        FMLog.le("OutdoorMapActivity", "OutdoorMapActivity#onDestroy");
        FMCallService.instance().stop();
        FMLocationService.instance().stop();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fm_bt_route:    //打开路线
                if (!isMapLoadCompleted) {
                    CustomToast.show(this, "正在加载地图");
                    return;
                }

                LineAdapter a =((LineAdapter)((RouteView)mOpenRouteWindow.getConvertView()).getLineView().getAdapter());
                FMRoute defaultRoute = a.getRoute(a.getSelectedPosition());

                if (isShowRoute) {  //close
                    mOpenRouteWindow.close();
                    mShowRouteView.setTextColor(Color.parseColor("#70ffffff"));
                    mShowRouteView.setBackgroundResource(R.drawable.fm_green_normal_button);

                    // 移除路线
                    mMapView.hiddenRouteOnMap(defaultRoute);
                } else {
                    mOpenRouteWindow.showAtLocation(mShowRouteView, Gravity.BOTTOM, 0, 0);
                    mShowRouteView.setTextColor(Color.WHITE);
                    mShowRouteView.setBackgroundResource(R.drawable.fm_green_press_button);

                    // 显示的线路
                    mMapView.showRouteOnMap(defaultRoute);
                }

                isShowRoute = !isShowRoute;
                break;


            case R.id.fm_map_img_location:
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
                break;

            case R.id.fm_bt_call:
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
                }
                break;

            default:
                break;
        }
    }

    public static String WaiterMacAddress = "1C:77:F6:64:49:0C";
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

    private void addStartMarker(int groupId, FMMapCoord position, boolean needSetCalculateStartPoint) {

        mStartMarkerLayer = mMap.getFMLayerProxy().createFMImageLayer(groupId); //创建图层
        mMap.addLayer(mStartMarkerLayer);              //将图层添加到地图
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
        mMap.addLayer(mEndMarkerLayer);         //将图层添加到地图
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
        clearCalculatedPathResults();

        int[] resultCode = FMNaviAnalysisHelper.instance().calculateRoute();

        if (resultCode[0] == FMNaviAnalysisHelper.FM_CALCULATE_ROUTE_ONE_MAP) {  // 同地图路径规划
            if (resultCode[1] == FMNaviAnalyser.FMRouteCalcuResult.ROUTE_SUCCESS) { //成功
                // 添加起点终点
                addStartMarker(FMNaviAnalysisHelper.instance().getStartNaviMultiPoint().getGroupId(),
                               FMNaviAnalysisHelper.instance().getStartNaviMultiPoint().getPosition(),
                               false);
                addEndMarker(FMNaviAnalysisHelper.instance().getEndNaviMultiPoint().getGroupId(),
                             FMNaviAnalysisHelper.instance().getEndNaviMultiPoint().getPosition(),
                             false);
                // 结果
                mPathResults = FMNaviAnalysisHelper.instance().getNaviPathResults(FMNaviAnalysisHelper.instance().getEndNaviMultiPoint());
            } else {
                CustomToast.show(this, FMNaviAnalysisHelper.instance().getCalculateRouteReturnInfo(resultCode));
                clearStartAndEndMarker();
                return false;
            }
        } else {    // 跨地图
            if (resultCode[1] == FMNaviMultiAnalyer.FMNaviMultiRouteResult.MULTIROUTE_SUCCESS) { //成功

                if (resultCode[0] == FMNaviAnalysisHelper.FM_CALCULATE_ROUTE_IN_IN_MAP) {
                    mPathResults = FMNaviAnalysisHelper.instance().getNaviPathResults(FMNaviAnalysisHelper.instance().getOutsideNaviMultiPoint());
                } else {
                    // 判断起点还是终点
                    if (FMNaviAnalysisHelper.instance().getStartNaviMultiPoint().getNaviAnalyser().getMapId().equals(mMap.currentMapId())) {
                        addStartMarker(FMNaviAnalysisHelper.instance().getStartNaviMultiPoint().getGroupId(),
                                       FMNaviAnalysisHelper.instance().getStartNaviMultiPoint().getPosition(),
                                       false);

                        mPathResults = FMNaviAnalysisHelper.instance().getNaviPathResults(FMNaviAnalysisHelper.instance().getStartNaviMultiPoint());
                    } else {
                        addEndMarker(FMNaviAnalysisHelper.instance().getEndNaviMultiPoint().getGroupId(),
                                     FMNaviAnalysisHelper.instance().getEndNaviMultiPoint().getPosition(),
                                     false);

                        mPathResults = FMNaviAnalysisHelper.instance().getNaviPathResults(FMNaviAnalysisHelper.instance().getEndNaviMultiPoint());
                    }
                }
            } else {
                CustomToast.show(this, FMNaviAnalysisHelper.instance().getCalculateRouteReturnInfo(resultCode));
                clearStartAndEndMarker();
                return false;
            }
        }

        // 设置导航模式下窗口的内容
        setStartOpenNavigationView(resultCode);

        mCalculateLineMarker = FMAPI.instance().drawFloorLine( mLineLayer,
                                                               4.0f,
                                                               "fmr/full_arrow.png",
                                                               Color.BLUE,
                                                               mPathResults.get(0) );

        mMap.updateMap();
        return true;
    }

    /**
     * 路径规划。
     */
    private boolean calculateAndDrawRoute() {
        clearCalculatedPathResults();

        int[] resultCode = FMNaviAnalysisHelper.instance().calculateRoute();

        if (resultCode[0] == FMNaviAnalysisHelper.FM_CALCULATE_ROUTE_ONE_MAP) {  // 同地图路径规划
            if (resultCode[1] == FMNaviAnalyser.FMRouteCalcuResult.ROUTE_SUCCESS) { //成功
                mPathResults = FMNaviAnalysisHelper.instance().getNaviPathResults(FMNaviAnalysisHelper.instance().getEndNaviMultiPoint());
            } else {
                CustomToast.show(this, FMNaviAnalysisHelper.instance().getCalculateRouteReturnInfo(resultCode));
                clearStartAndEndMarker();
                return false;
            }
        } else {    // 跨地图
            if (resultCode[1] == FMNaviMultiAnalyer.FMNaviMultiRouteResult.MULTIROUTE_SUCCESS) { //成功

                if (resultCode[0] == FMNaviAnalysisHelper.FM_CALCULATE_ROUTE_IN_IN_MAP) {
                    mPathResults = FMNaviAnalysisHelper.instance().getNaviPathResults(FMNaviAnalysisHelper.instance().getOutsideNaviMultiPoint());
                } else {
                    // 判断起点还是终点
                    if (FMNaviAnalysisHelper.instance().getStartNaviMultiPoint().getNaviAnalyser().getMapId().equals(mMap.currentMapId())) {
                        mPathResults = FMNaviAnalysisHelper.instance().getNaviPathResults(FMNaviAnalysisHelper.instance().getStartNaviMultiPoint());
                    } else {
                        mPathResults = FMNaviAnalysisHelper.instance().getNaviPathResults(FMNaviAnalysisHelper.instance().getEndNaviMultiPoint());
                    }
                }

            } else {
                CustomToast.show(this, FMNaviAnalysisHelper.instance().getCalculateRouteReturnInfo(resultCode));
                clearStartAndEndMarker();
                return false;
            }
        }

        // 设置导航模式下窗口的内容
        setStartOpenNavigationView(resultCode);

        mCalculateLineMarker = FMAPI.instance().drawFloorLine( mLineLayer,
                                                               2.0f,
                                                               "fmr/full_arrow.png",
                                                               Color.BLUE,
                                                               mPathResults.get(0) );


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
        FMLocationService.instance().setOnFMWifiStatusListener(new OnFMWifiStatusListener() {
            @Override
            public void wifiMapServerError() {
                wifiStatues = "位置地图数据服务断开";
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

    // 进室内
    private void enterInsideWhenInNavigation(String mapId) {
        clearCalculatedPathResults();
        clearStartAndEndMarker();
        clearCalculateRouteLineMarker();

        Bundle b = new Bundle();
        b.putString(FMAPI.ACTIVITY_WHERE, OutdoorMapActivity.class.getName());
        b.putString(FMAPI.ACTIVITY_MAP_ID, mapId);
        b.putString(FMAPI.ACTIVITY_HOTEL_NAME, Tools.getInsideMapName(mapId));

        // 定位 or 到达
        if (FMLocationService.instance().isInNavigationMode()) {
            b.putInt(TARGET_CALCULATE_ROUTE, FMLocationService.instance().getFirstMyLocatePosition().getGroupId());
        } else {
            b.putInt(TARGET_LOCATE, FMLocationService.instance().getFirstMyLocatePosition().getGroupId());
        }


        FMAPI.instance().gotoActivity(this, IndoorMapActivity.class, b);
    }


    @Override
    public void onItemClick(ButtonGroup.ButtonType type, boolean isSelected) {
        // effect
        if (isSelected) {
            mButtonGroup.unSelected(type);
            executeButtonType(mButtonGroup.getCurrentType());
        } else {
            mButtonGroup.selected(type);
            executeButtonType(type);
        }
        mMap.updateMap();
    }

    private void executeButtonType(ButtonGroup.ButtonType type) {
        // business
        if (type == ButtonGroup.ButtonType.FACILITY) {
            mMapView.hiddenActivityGroupOnMap(mFoodActGroup);
            mMapView.hiddenActivityGroupOnMap(mShopActGroup);

            mMapView.showActivityGroupOnMap(mFacilityActGroup);
        } else if (type == ButtonGroup.ButtonType.SHOP) {
            mMapView.hiddenActivityGroupOnMap(mFacilityActGroup);
            mMapView.hiddenActivityGroupOnMap(mShopActGroup);

            mMapView.showActivityGroupOnMap(mFoodActGroup);
        } else if (type == ButtonGroup.ButtonType.FOOD){
            mMapView.hiddenActivityGroupOnMap(mFacilityActGroup);
            mMapView.hiddenActivityGroupOnMap(mFoodActGroup);

            mMapView.showActivityGroupOnMap(mShopActGroup);
        } else {   // none
            mMapView.showAllOnMap();
        }
        mMap.updateMap();
    }

    @Override
    public void onMapInitSuccess(String mapId) {
        mMap.setSceneZoomRange(1.0f, 20);
        mMapInfo = mMap.getFMMapInfo();
        mMap.zoom(1.6f);
        mMap.setTiltAngle((float) FMMath.degreeToRad(50));
        mMap.setRotate((float) FMMath.degreeToRad(45));
        mMap.setMapCenter(new FMMapCoord(1.2188300E7, 2071220.0, 0.0));

        // 图层代理
        mLayerProxy = mMap.getFMLayerProxy();

        FMModelLayer modelLayer = mLayerProxy.getFMModelLayer(mMap.getDisplayGroupIds()[0]);
        if ( modelLayer!= null ) {
            modelLayer.setVisible(false);
        }

        // 3D模型图层
        FMExternalModelLayer eml = mLayerProxy.getFMExternalModelLayer(mMap.getDisplayGroupIds()[0]);

        eml.setOnFMNodeListener(new OnFMNodeListener() {
            @Override
            public boolean onClick(FMNode pFMNode) {
                //禁地图点击事件
//                if (FMAPI.instance().needFilterNavigationWhenOperation(mInstance)) {
//                    return false;
//                }
//
//                mCurrentModel = (FMExternalModel)pFMNode;
//
//                if (mCurrentModel.getDataType()==100000 ||
//                    mCurrentModel.getFid().equals("999800171") ||
//                    mCurrentModel.getFid().equals("999800170")) {
//                    return false;
//                }
//
//                if (mLastModel != null) {
//                    mMapView.setHighlight(mLastModel, false);
//                }
//
//                mMapView.setHighlight(mCurrentModel, true);
//
//                mLastModel = mCurrentModel;
//
//                mMap.updateMap();
//
//                ModelView view = (ModelView) mOpenModelInfoWindow.getConvertView();
//                String name = mCurrentModel.getName();
//                if ("".equals(name) || name == null) {
//                    name = "暂无名称";
//                }
//                view.setTitle(name);
//                // 查询
//                List<FMDBMapElement> elements = mMapElementDAO.queryFid(mMap.currentMapId(), mCurrentModel.getFid());
//                String               typeName = "";
//                String               address  = "";
//                if (!elements.isEmpty()) {
//                    typeName = elements.get(0).getTypename();
//                    address = elements.get(0).getAddress();
//                }
//                elements.clear();
//                elements = null;
//                String viewAddress = "";
//                if (typeName==null || typeName.equals("")) {
//                    viewAddress = address;
//                } else {
//                    viewAddress = String.format("%s・%s", typeName, address);
//                }
//                view.setAddress(viewAddress);
//                view.setEnterMapIdByModelFid(mCurrentModel.getFid());
//
//                mOpenModelInfoWindow.showAsDropDown(mMapView, 0, -mMapView.getHeight());
//
//                mSceneAnimator.animateMoveToScreenCenter(mCurrentModel.getCenterMapCoord())
//                        .setInterpolator(new FMLinearInterpolator(FMInterpolator.STAGE_INOUT))
//                        .setDurationTime(800)
//                        .start();
//
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

        // 线图层
        mLineLayer = mLayerProxy.getFMLineLayer();
        mMap.addLayer(mLineLayer);

        // 定位
        mLocationLayer = mMap.getFMLayerProxy().getFMLocationLayer();
        mMap.addLayer(mLocationLayer);
//        mMeLocationMarker = FMAPI.instance().addLocationMarker(mLocationLayer, 1, GeoMapCoords.get(0));

        // 显示所有路径
        //mNaviAnalyser.showAllPath(mMap.currentMapPath(), mLineLayer, mMap.getDisplayGroupIds()[0]);

        // 线路上图片图层
        mRouteImageLayer = mLayerProxy.createFMImageLayer(mMap.getDisplayGroupIds()[0]);
        mMap.addLayer(mRouteImageLayer);

        // 设置内部搜需要的图层
        mMapView.setRouteLayers(mLineLayer, mRouteImageLayer);

        dealInitLoadTask();

        initRoutViewWindow();

        FMLabelLayer labelLayer = mMap.getFMLayerProxy().getFMLabelLayer(1);
        ArrayList    arrayList  = labelLayer.getAll();

        isMapLoadCompleted = true;

        mProgressDialog.dismiss();

//        needLocate(false);

//        new Thread(new Runnable() {
//            int index = 0;
//            @Override
//            public void run() {
//                FMZone[] zones = FMAPI.instance().mZoneManager.getAllZones();
//
//                while (true) {
//                    FMMapCoord c = randomMapCoord();
//                    mMeLocationMarker.updatePosition(1, c);
//                    for (FMZone zone : zones) {
//                        FMLog.le("zoneContain", "test current zone: " + zone.getZoneName());
//                        if (FMMap.zoneContain(zone.getZoneCoords(), c)) {
//                            FMLog.le("zone", "find zone out: "+zone.getZoneName());
//                            break;
//                        }
//                        FMLog.le("zoneContain", "size: " + zone.getZoneCoords().length);
//                    }
//
//                    mMap.updateMap();
//
//                    try {
//                        Thread.sleep(800);
//                    } catch (InterruptedException pE) {
//                        pE.printStackTrace();
//                    }
//                }
//            }
//        }).start();
    }

    @Override
    public void onMapInitFailure(String mapPath, int code) {

    }

    @Override
    public void onMapClick(float x, float y) {
        FMPickMapCoordResult result = mMap.pickMapCoord(x, y);
        if (result != null) {
            FMLog.e("onMapClick", result.getMapCoord().toString());

            // TODO: 2016/12/10  test zone
            FMTotalMapCoord totalCoord = new FMTotalMapCoord();
            totalCoord.setMapCoord(result.getMapCoord());
            totalCoord.setGroupId(1);
            totalCoord.setMapId(Tools.OUTSIDE_MAP_ID);
            FMZone zone = FMAPI.instance().mZoneManager.getCurrentZone(totalCoord);
            FMLog.i("zone ", "zone: " + zone.toString());
        }
    }

    // 处理地图初始化后的任务
    private void dealInitLoadTask() {
        if (mSpecialWorkMarker != null) {  //加载业态标注
            mSpecialWorkImageLayer = mLayerProxy.createFMImageLayer(mSpecialWorkMarker.getGroupId());
            mSpecialWorkImageLayer.addMarker(mSpecialWorkMarker);
            mMap.addLayer(mSpecialWorkImageLayer);

            mSceneAnimator.animateMoveToScreenCenter(mSpecialWorkMarker.getPosition())
                          .setInterpolator(new FMLinearInterpolator(FMInterpolator.STAGE_INOUT))
                          .setDurationTime(1000)
                          .start();
        }

        if (mNeedLoadCalculatedRoute) {
            if (calculateAndDrawRouteAndAddStartEndMarker()) {
                mOpenNaviWindow.showAsDropDown(mMapView, 0, -mMapView.getHeight());
            }
            mNeedLoadCalculatedRoute = false;
        }

        if (mMeLocationMarker != null) {
            if (mMeLocationMarker.getHandle() == 0) {
                mLocationLayer.addMarker(mMeLocationMarker);
            } else {
                mMeLocationMarker.updatePosition(mInitLocatePosition.getGroupId(),
                                                 mInitLocatePosition.getMapCoord());
            }
        }

        if (FMLocationService.instance().isInNavigationMode()) {   // 处理导航模式
            closeAllWindow();
            dealViewChangedWhenOpenNavigationMode();
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

    // 处理导航模式下进入地图的任务    必定是终点或者是中途点
    private void dealInitLoadedNavigationTask() {
        if (FMNaviAnalysisHelper.instance().getEndNaviMultiPoint() == null || FMNaviAnalysisHelper.instance().getStartNaviMultiPoint() == null) {
            return;
        }

        // 室内室内
        if (FMNaviAnalysisHelper.instance().getCalculatorRouteType() == FMNaviAnalysisHelper.FM_CALCULATE_ROUTE_IN_IN_MAP) {
            // 没有起点终点
            mPathResults = FMNaviAnalysisHelper.instance().getNaviPathResults(FMNaviAnalysisHelper.instance().getOutsideNaviMultiPoint());

        } else if (FMNaviAnalysisHelper.instance().getCalculatorRouteType() == FMNaviAnalysisHelper.FM_CALCULATE_ROUTE_IN_OUT_MAP){
            if (FMNaviAnalysisHelper.instance().getStartNaviMultiPoint().getNaviAnalyser().getMapId().equals(mMap.currentMapId())) {
                addStartMarker(FMNaviAnalysisHelper.instance().getStartNaviMultiPoint().getGroupId(),
                               FMNaviAnalysisHelper.instance().getStartNaviMultiPoint().getPosition(),
                               false);
                mPathResults = FMNaviAnalysisHelper.instance().getNaviPathResults(FMNaviAnalysisHelper.instance().getStartNaviMultiPoint());
            } else {
                // 终点
                addEndMarker(FMNaviAnalysisHelper.instance().getEndNaviMultiPoint().getGroupId(),
                             FMNaviAnalysisHelper.instance().getEndNaviMultiPoint().getPosition(),
                             false);
                mPathResults = FMNaviAnalysisHelper.instance().getNaviPathResults(FMNaviAnalysisHelper.instance().getEndNaviMultiPoint());
            }

        }

        // 添加定位标注物
        FMMapCoord startCoord = mPathResults.get(0).getPointList().get(0);
        dealAddLocateMarker(mMap.getFocusGroupId(), startCoord, mLocationLayer);


        // 画路线
        mCalculateLineMarker = FMAPI.instance().drawFloorLine(mLineLayer,
                                                              2.0f,
                                                              "fmr/full_arrow.png",
                                                              Color.BLUE,
                                                              mPathResults.get(0));

        animateCenterWithZoom(mMap.getFocusGroupId(), startCoord);
        mMap.updateMap();

        // 显示导航窗口
        setNavigationViewContent(FMAPI.instance().mWalkedDistance);

        mOpenNaviProcessWindow.showAsDropDown(mMapView, 0, -mMapView.getHeight());
    }


    @Override
    public void onClose(boolean isGestureClose, View v) {
        if (v instanceof ModelView) {  // 模型信息

        } else if (isGestureClose && v instanceof NaviView) {  // 开始导航,  手势结束路径规划
            clearCalculateRouteLineMarker();
            clearStartAndEndMarker();
        } else if (v instanceof NaviProcessingView) { // 导航模式   这里结束导航
            clearCalculatedPathResults();
            clearCalculateRouteLineMarker();
            clearStartAndEndMarker();
            clearWalkedTemporaryValue();
            dealViewChangedWhenOverNavigationMode();
        }
//        else if (v instanceof RouteView) {  // 线路模式
//            mShowRouteView.setTextColor(Color.parseColor("#70ffffff"));
//            mShowRouteView.setBackgroundResource(R.drawable.fm_green_normal_button);
//            isShowRoute = !isShowRoute;
//            RouteView rv = (RouteView) mOpenRouteWindow.getConvertView();
//            ((LineAdapter)rv.getLineView().getAdapter()).getCurrentMarker().setVisible(false);
//            mMap.updateMap();
//        }
    }

    // clear
    public void clearCalculatedPathResults() {
        mPathResults.clear();
    }

    public void clearCalculateRouteLineMarker() {
        if (mCalculateLineMarker != null) {
            mLineLayer.removeMarker(mCalculateLineMarker);
            mMap.updateMap();
            mCalculateLineMarker = null;
        }
    }

    public void clearStartAndEndMarker() {
        if (mStartMarkerLayer != null) {
            mStartMarkerLayer.removeAll();
            mMap.removeLayer(mStartMarkerLayer);
            mStartMarker = null;
            mStartMarkerLayer = null;
        }
        if (mEndMarkerLayer != null) {
            mEndMarkerLayer.removeAll();
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
    private void dealViewChangedWhenOpenNavigationMode() {
        mButtonGroup.setVisibility(View.INVISIBLE);        // 隐藏ButtonGroup
//        mShowRouteView.setVisibility(View.INVISIBLE);    //
        mLocationView.setVisibility(View.INVISIBLE);
    }

    private void dealViewChangedWhenOverNavigationMode() {
        mButtonGroup.setVisibility(View.VISIBLE);         // 显示ButtonGroup
//        mShowRouteView.setVisibility(View.VISIBLE);       //
        mLocationView.setVisibility(View.VISIBLE);
    }

    private void dealMapFollow(FMTotalMapCoord currentTotalMapCoord, int index) {
        calculateMapRotatedAngle(index, currentTotalMapCoord.getMapCoord());
        mMap.setMapCenter(currentTotalMapCoord.getMapCoord());
    }

    private void dealNavigationTextViewChanged(float[] naviResults, FMTotalMapCoord currentTotalMapCoord) {
        float distance = naviResults[0];     // 偏离路线距离     做路径重规划的基准
        int index = (int) naviResults[1];    // 此时位置index

        FMLog.e("dealNavigationTextViewChanged", "offset distance: " + distance + " index: " + index);

        if (distance > FMAPI.NAVIGATION_OFFSET_MAX_DISTANCE) {
            // 路径重规划
        }

        // 下面计算实时距离 和 时间
        float walkedDis = walkedDistance(index, currentTotalMapCoord);

        setNavigationViewContent(walkedDis);
    }

    // 设置导航模式下弹窗的内容
    private void setNavigationViewContent(float pWalkedDis) {
        NavigationUtils.NavigationDescription nvd = NavigationUtils.forNavigationDescription(pWalkedDis);
        // 导航窗口内容
        NaviProcessingView npv = (NaviProcessingView) mOpenNaviProcessWindow.getConvertView();
        npv.setRemainingDistance(nvd.getRemainedDistanceDesc());
        npv.setRemainingTime(nvd.getRemainedTimeDesc());

        // 完成导航
        if (nvd.getRemainedDistance() <= 0 || nvd.getRemainedTime() <=0) {
            FMLocationService.instance().setInNavigationMode(false);
            npv.getStopNaviButton().setText("导航完成");
        }
    }


    private float walkedDistance(int index, FMTotalMapCoord currentTotalMapCoord) {
        ArrayList<FMMapCoord> points = mPathResults.get(0).getPointList();

        // 当前段的距离
        float currIndexDis = (float) FMMapCoord.length(points.get(index), currentTotalMapCoord.getMapCoord());
        float len = 0;
        if (index > 0) {
            for (int i=1; i< index; ++i) {
                len += (float) FMMapCoord.length(points.get(i - 1), points.get(i));
            }
        }

        FMAPI.instance().mWalkedDistance = len + currIndexDis;


        CustomToast.show(this, "index: " +index+" , walkDis: "+ FMAPI.instance().mWalkedDistance);
        return FMAPI.instance().mWalkedDistance;
    }

    // calculate angle
    private float mLastLineAngle = -1;
    private void calculateMapRotatedAngle(int pIndex, FMMapCoord pCurrCoord) {
        // pIndex < point size - 1
        ArrayList<FMMapCoord> points = mPathResults.get(0).getPointList();
        if (pIndex <= points.size() - 2) {
            float currLineAngle = (float) FMAPI.calcuAngle(pCurrCoord, points.get(pIndex + 1));
            if (mLastLineAngle != -1 && Math.abs(currLineAngle - mLastLineAngle)>0 ) {
                mMap.setRotate(-(float) FMMath.degreeToRad(currLineAngle));
                mMap.updateMap();
            }
            mLastLineAngle = currLineAngle;
        }

    }

    ///////////////////////////定位/////////////////////////////
    private FMMapCoord randomMapCoord() {
        int    randX = (int) (mMapInfo.getMaxX() - mMapInfo.getMinX());
        int    randY = (int) (mMapInfo.getMaxY() - mMapInfo.getMinY());
        double x     = mRandom.nextInt(randX) + mMapInfo.getMinX();
        double y     = mRandom.nextInt(randY) + mMapInfo.getMinY();

        return new FMMapCoord(x, y, 0.0);
    }

    /**
     * 需要定位。
     */
    public void needLocate(boolean isArrive) {
        isLocateSuccess = false;
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
                    locatePosition = FMLocationService.instance().getFirstMyLocatePosition();
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

    public final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FMAPI.LOCATE_SUCCESS_TO_ARRIVE: { // 2     //定位成功  到这去
                    mProgressDialog.dismiss();
                    FMAPI.instance().isInLocating = true;
                    isLocateSuccess = true;
                    FMTotalMapCoord meLocatePosition = (FMTotalMapCoord) msg.obj;
                    dealLocateWhenSuccess(meLocatePosition, true);
                }   break;

                case FMAPI.LOCATE_SUCCESS_TO_LOCATE: {  // 定位
                    mProgressDialog.dismiss();
                    FMAPI.instance().isInLocating = true;
                    isLocateSuccess = true;
                    FMTotalMapCoord meLocatePosition = (FMTotalMapCoord) msg.obj;
                    dealLocateWhenSuccess(meLocatePosition, false);
                }   break;

                case FMAPI.LOCATE_FAILURE:   // 1
                    mProgressDialog.dismiss();
                    FMAPI.instance().isInLocating = false;
                    clearMeLocationMarker();
                    CustomToast.show(OutdoorMapActivity.mInstance, "定位失败,请重新尝试");
                    mLocationView.setBackgroundResource(R.drawable.fm_green_normal_button);

                    // 关闭定位
                    FMLocationService.instance().stop();
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
        if (locateMapId.equals(Tools.OUTSIDE_MAP_ID)) {  //室外地图
            dealLocateInCurrentMap(locatePosition, isArrive);   // 室外同地图路径规划
        } else {        // 起点在另一地图, 先路径规划,弹出路径规划窗口,点击导航后切地图
            dealLocateInOtherMap(locatePosition, isArrive);
        }
    }

    private void dealLocateInCurrentMap(FMTotalMapCoord locatePosition, boolean isArrive) {
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
            // 添加定位标注
            dealAddLocateMarker(locatePosition.getGroupId(), locatePosition.getMapCoord(), mLocationLayer);
            animateCenterWithZoom(locatePosition.getGroupId(), locatePosition.getMapCoord());
        }

        mMap.updateMap();
    }


    /**
     * 设置切换地图的状态。
     * @param locatePosition
     */
    private void dealLocateInOtherMap(FMTotalMapCoord locatePosition, boolean isArrive) {
        if (isArrive) {            // 到这去

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

                // 打开窗口
                mOpenNaviWindow.showAsDropDown(mMapView, 0, -mMapView.getHeight());
            }

        } else {                     // 我的位置

            // 跳转
            Bundle b = new Bundle();
            b.putString(FMAPI.ACTIVITY_WHERE, OutdoorMapActivity.class.getName());
            b.putString(FMAPI.ACTIVITY_MAP_ID, locatePosition.getMapId());
            b.putInt(FMAPI.ACTIVITY_MAP_GROUP_ID, locatePosition.getGroupId());

            // 定位
            b.putString(FMAPI.ACTIVITY_TARGET, TARGET_LOCATE);
            b.putSerializable(FMAPI.ACTIVITY_LOCATE_POSITION, locatePosition);

            b.putString(FMAPI.ACTIVITY_HOTEL_NAME, Tools.getInsideMapName(locatePosition.getMapId()));

            FMAPI.instance().gotoActivity(this, IndoorMapActivity.class, b);
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
        mSceneAnimator.animateMoveToScreenCenter(initMapCoord)
                .animateZoom(1.5)
                .setInterpolator(new FMLinearInterpolator(FMInterpolator.STAGE_INOUT))
                .setDurationTime(1000)
                .start();
    }

    @Override
    public void onReceivePositionInCallService(String pMacAddress, FMTotalMapCoord pFMTotalMapCoord) {
        CustomToast.show(this, "outdoor macAddr: " + pMacAddress + ", " + pFMTotalMapCoord.toString());

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
            if (Tools.isInsideMap(mapId)) {  // waiter's position is in inside map, need to switch map
                enterInsideWhenInNavigation(mapId);
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
                } else {    // hidden
                    m.setVisible(false);
                }
                break;
            }
        }
        mMap.updateMap();
    }

    private boolean mNeedBackToMyLocation = false;
    private String wifiStatues = "";
    private int wifiDelayTime = 0;
    private String logText = "";

    private OnFMReceiveLocationListener mLocationListener = new OnFMReceiveLocationListener() {
        @Override
        public void onReceiveLocation(int type, FMTotalMapCoord lastLocation, FMTotalMapCoord currentLocation, final float angle) {
            String logC = "type: " + type+" ,"+ currentLocation.toString();

            if (!isMapLoadCompleted) {
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
            } else if (mMeLocationMarker.getHandle() == 0){
                return;
            }

            boolean isInNavigationMode = FMLocationService.instance().isInNavigationMode();

            // deal in navigation mode
            if (isInNavigationMode) {
                mMap.setFMViewMode(FMViewMode.FMVIEW_MODE_2D);
                dealNavigationMode(type, lastLocation, currentLocation, angle);
                return;
            }

            // click back to my location
            if (mNeedBackToMyLocation && !isInNavigationMode) {
                if (!mMap.currentMapId().equals(currentLocation.getMapId())) {
                    enterInsideWhenInNavigation(currentLocation.getMapId());
                }
                mNeedBackToMyLocation = false;
            }

            // warning by open window
            if (mNeedWarningToJumpMap && !isInNavigationMode) {
                if (!mMap.currentMapId().equals(currentLocation.getMapId())) {
                    getAlertDialog(currentLocation).show();
                }
            }

            if (!mMeLocationMarker.isVisible()) {
                mMeLocationMarker.setVisible(true);
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

        String mapId = currentLocation.getMapId();

        if (!mapId.equals(mMap.currentMapId()) &&
            FMNaviAnalysisHelper.instance().getMayPassedMapIdsInNavigation().contains(mapId)) {
            enterInsideWhenInNavigation(mapId);
            return;
        }

        // [0] distance      [1] index
        float[] naviResults = Tools.getFMNaviAnalyserByMapId(Tools.OUTSIDE_MAP_ID).naviConstraint(mPathResults.get(0).getPointList(),
                                                           currentLocation.getGroupId(),
                                                           lastLocation.getMapCoord(),
                                                           currentLocation.getMapCoord());

        if (FMAPI.mNeedMapFollowInNavigation) {  // update map rotation way that is different with before
            dealMapFollow(currentLocation, (int) naviResults[1]);
        }

        dealNavigationTextViewChanged(naviResults, currentLocation);

        if (!mMeLocationMarker.isVisible()) {
            mMeLocationMarker.setVisible(true);
        }
        mMeLocationMarker.updateAngleAndPosition(currentLocation.getGroupId(),
                                                 angle,
                                                 currentLocation.getMapCoord());

        mMap.updateMap();
    }

    AlertDialog mDialog = null;
    private boolean mNeedWarningToJumpMap = true;
    public AlertDialog getAlertDialog(final FMTotalMapCoord myPosition) {

        String warnText = String.format("您现在在 %s，是否需要跳转？" +
                                        "若选择 不再提示 将不会在再提示，" +
                                        "需要跳转，则请点击 回到我的位置", Tools.getInsideMapName(myPosition.getMapId()));

        if (mDialog == null) {
            mDialog = new AlertDialog.Builder(this).setTitle("提示")
                    .setNegativeButton("不再提示", new DialogInterface.OnClickListener(){

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mNeedWarningToJumpMap = false;
                            dialog.dismiss();
                        }

                    }).setPositiveButton("跳转", new DialogInterface.OnClickListener(){

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            enterInsideWhenInNavigation(myPosition.getMapId());
                            dialog.dismiss();
                        }

                    }).create();
        }

        mDialog.setMessage(warnText);
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
