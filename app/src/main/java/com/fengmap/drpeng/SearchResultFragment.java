package com.fengmap.drpeng;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.mgwaiter.R;
import com.fengmap.android.FMDevice;
import com.fengmap.android.utils.FMLog;
import com.fengmap.android.wrapmv.Tools;
import com.fengmap.android.wrapmv.db.FMDBMapElement;
import com.fengmap.android.wrapmv.db.FMDBMapElementDAO;
import com.fengmap.android.wrapmv.db.FMDBSearchElement;
import com.fengmap.android.wrapmv.db.FMDBSearchElementDAO;
import com.fengmap.android.wrapmv.db.FMDatabaseDefine;
import com.fengmap.drpeng.adapter.AutoTextAdapter;

import java.util.List;

import static com.fengmap.drpeng.SearchActivity.SEARCH_NAME;
import static com.fengmap.drpeng.SearchActivity.SEARCH_TYPENAME;


/**
 * Created by yangbin on 16/9/8.
 */

public class SearchResultFragment extends Fragment implements AdapterView.OnItemClickListener {
    protected ListView        mResultListView;
    protected AutoTextAdapter mAutoTextAdapter;
    protected TextView        mMoreView;

    private FMDBMapElementDAO    mElementDAO;
    private FMDBSearchElementDAO mSearchElementDAO;
    protected int mCurrentIndex = 0;          // 分页下标
    private int mCurrentSearchWay = 1;      // 当前搜索方式
    private String mContent;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fm_search_result, container, false);
        init(view);

        if (savedInstanceState != null) {
            mCurrentSearchWay = savedInstanceState.getInt("SearchWay");
            mContent = savedInstanceState.getString("Content");
            setAutoCompleteTextDataSource(mContent);
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        FMLog.le("SearchResultFragment", "SearchResultFragment#onDestroyView");
        super.onDestroyView();
    }

    private void init(View pView) {
        mElementDAO = new FMDBMapElementDAO();
        mSearchElementDAO = new FMDBSearchElementDAO();

        mResultListView = (ListView) pView.findViewById(R.id.fm_search_result_list);
        mResultListView.setOnItemClickListener(this);

        mMoreView = new TextView(getContext());
        mMoreView.setText("查看更多搜索结果");
        mMoreView.setTextSize(14);
        mMoreView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                                                               (int) (50 * FMDevice.getDeviceDensity())));
        mMoreView.setTextColor(Color.parseColor("#90C98C"));
        mMoreView.setGravity(Gravity.CENTER);
        mResultListView.addFooterView(mMoreView);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("SearchWay", mCurrentIndex);
        outState.putString("Content", mContent);
    }


    /**
     * 类型搜索。
     * @param typename
     */
    public void searchByTypename(String typename) {
        mCurrentSearchWay = SEARCH_TYPENAME;
        setAutoCompleteTextDataSource(typename);
    }

    /**
     * 模糊搜索。
     * @param keywords
     */
    public void searchByKeywords(String keywords) {
        mCurrentSearchWay = SEARCH_NAME;
        if (keywords.equals("")) {
            return;
        }
        setAutoCompleteTextDataSource(keywords);
    }


    //每次查询数据库,设置数据源
    private void setAutoCompleteTextDataSource(String content) {
        if (content==null || content.equals("")) {
            return;
        }

        mContent = content;

        List<FMDBMapElement> rs = null;

        if (FMDatabaseDefine.TYPENAME_ATM.equals(content) ||
            FMDatabaseDefine.TYPENAME_ELEVATOR.equals(content) ||
            FMDatabaseDefine.TYPENAME_FOOD.equals(content) ||
            FMDatabaseDefine.TYPENAME_HOTEL.equals(content) ||
            FMDatabaseDefine.TYPENAME_RELAX.equals(content) ||
            FMDatabaseDefine.TYPENAME_SERVER.equals(content) ||
            FMDatabaseDefine.TYPENAME_SHOP.equals(content) ||
            FMDatabaseDefine.TYPENAME_WC.equals(content)) {

            rs = mElementDAO.queryPriorityTypename(((SearchActivity)getActivity()).mMapId, ((SearchActivity)getActivity()).mGroupId,
                                                   content, mCurrentIndex * FMDatabaseDefine.MAX_COUNT);
        } else {
            if (mCurrentSearchWay == SEARCH_NAME) {
                rs = mElementDAO.queryPriorityName(((SearchActivity)getActivity()).mMapId, ((SearchActivity)getActivity()).mGroupId,
                                                   content, mCurrentIndex * FMDatabaseDefine.MAX_COUNT);
            } else if (mCurrentSearchWay == SEARCH_TYPENAME) {
                rs = mElementDAO.queryPriorityTypename(((SearchActivity)getActivity()).mMapId, ((SearchActivity)getActivity()).mGroupId,
                                                       content, mCurrentIndex * FMDatabaseDefine.MAX_COUNT);
            }
        }
        if (rs.isEmpty()) { //数据为空, 移除 "加载更多"
            mMoreView.setVisibility(View.GONE);
        } else {
            mMoreView.setVisibility(View.VISIBLE);
        }

        if (mAutoTextAdapter == null) {
            mAutoTextAdapter= new AutoTextAdapter(getContext(), rs, R.layout.view_item_search);
            mResultListView.setAdapter(mAutoTextAdapter);
            mAutoTextAdapter.notifyDataSetChanged();
        } else {
            mAutoTextAdapter.updateData(rs);
        }
    }

    private boolean isLoadCompleted = false;
    private void loadMore() {
        ++mCurrentIndex;   // 分页

        String content = ((SearchActivity)getActivity()).mEditView.getText().toString().trim();
        if (content==null || content.equals("")) {
            return;
        }

        List<FMDBMapElement> rs = null;
        if (mCurrentSearchWay == SEARCH_NAME) {
            rs = mElementDAO.queryName(null, -1, content, mCurrentIndex * FMDatabaseDefine.MAX_COUNT);
        } else if (mCurrentSearchWay == SEARCH_TYPENAME) {
            rs = mElementDAO.queryTypename(null, -1, content, mCurrentIndex * FMDatabaseDefine.MAX_COUNT);
        }

        if (rs.isEmpty()) { //数据为空, 移除 "加载更多"
            isLoadCompleted = true;
            mMoreView.setText("没有更多数据了");
            mMoreView.setTextColor(Color.parseColor("#999999"));
        } else {
            isLoadCompleted = false;
            mMoreView.setVisibility(View.VISIBLE);
            mAutoTextAdapter.addData(rs);
        }

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // 加载更多
        if (position >= mAutoTextAdapter.getCount()) {
            if (isLoadCompleted) {
                return;
            }
            loadMore();
            return;
        }

        FMDBMapElement element = mAutoTextAdapter.getItem(position);
        // 加入历史记录
        String historyName = ((SearchActivity)getActivity()).mEditView.getText().toString().trim();
        if (historyName!=null && !historyName.equals("")) {
            // 判断是否存在该搜索记录, 没有则写入
            if (!mSearchElementDAO.isSearchHistoryExist(historyName)) {
                FMDBSearchElement e = new FMDBSearchElement(element.getId(), historyName);
                mSearchElementDAO.insert(e);
            }
            // 判断是否存在该历史记录, 没有则写入
            if (!mSearchElementDAO.isHistoryExist(element)) {
                FMDBSearchElement e = new FMDBSearchElement(element);
                mSearchElementDAO.insert(e);
            }
        }

        element.setHistory(historyName);
        // 跳转界面
        gotoMap(element);
    }

    private void gotoMap(FMDBMapElement e) {
        String mapId = e.getMapId();
        boolean isInside = Tools.isInsideMap(mapId);
        Bundle b = new Bundle();
        String className = SearchResultFragment.class.getName();
        b.putSerializable(className, e);
        b.putString(FMAPI.ACTIVITY_WHERE, className);
        if (isInside) {
            FMAPI.instance().gotoActivity(getActivity(),
                                          IndoorMapActivity.class,
                                          b);
        } else {
            FMAPI.instance().gotoActivity(getActivity(),
                                          OutdoorMapActivity.class,
                                          b);
        }
    }

    public void reset() {
        isLoadCompleted = false;
        mCurrentIndex = 0;
    }
}
