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
import com.fengmap.android.wrapmv.db.FMDBSearchElement;
import com.fengmap.android.wrapmv.db.FMDBSearchElementDAO;
import com.fengmap.android.wrapmv.db.FMDatabaseDefine;
import com.fengmap.drpeng.adapter.HistoryTextAdapter;

import java.util.List;


/**
 * 搜索视图。
 * Created by yangbin on 16/9/8.
 */

public class SearchFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {
    private TextView mFoodView, mHotelView, mShoppingView, mRelaxView;
    private TextView mServeView, mWCView, mATMView, mEleView;

    private ListView mHistoryListView;
    private TextView mClearHistoryView;

    private FMDBSearchElementDAO mSearchElementDAO;
    private HistoryTextAdapter   mHistoryTextAdapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fm_map_search, container, false);
        init(view);
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        setHistoryTextDataSource();
        super.onResume();
    }

    private void init(View pView) {
        mSearchElementDAO = new FMDBSearchElementDAO();

        mFoodView = (TextView) pView.findViewById(R.id.fm_search_food);
        mHotelView = (TextView) pView.findViewById(R.id.fm_search_hotel);
        mShoppingView = (TextView) pView.findViewById(R.id.fm_search_shopping);
        mRelaxView = (TextView) pView.findViewById(R.id.fm_search_park);
        mFoodView.setOnClickListener(this);
        mHotelView.setOnClickListener(this);
        mShoppingView.setOnClickListener(this);
        mRelaxView.setOnClickListener(this);

        mServeView = (TextView) pView.findViewById(R.id.fm_search_serve);
        mWCView = (TextView) pView.findViewById(R.id.fm_search_wc);
        mATMView = (TextView) pView.findViewById(R.id.fm_search_atm);
        mEleView = (TextView) pView.findViewById(R.id.fm_search_ele);
        mServeView.setOnClickListener(this);
        mWCView.setOnClickListener(this);
        mATMView.setOnClickListener(this);
        mEleView.setOnClickListener(this);

        mHistoryListView = (ListView) pView.findViewById(R.id.fm_search_history_list);
        mHistoryListView.setOnItemClickListener(this);

        mClearHistoryView = new TextView(getContext());
        mClearHistoryView.setText("清除历史记录");
        mClearHistoryView.setTextSize(14);
        mClearHistoryView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                                                                       (int) (50 * FMDevice.getDeviceDensity())));
        mClearHistoryView.setTextColor(Color.parseColor("#90C98C"));
        mClearHistoryView.setGravity(Gravity.CENTER);
        mHistoryListView.addFooterView(mClearHistoryView);

    }


    //设置历史记录数据源
    public void setHistoryTextDataSource() {
        List<FMDBSearchElement> rs = mSearchElementDAO.queryAll();
        if (rs.isEmpty()) { //数据为空, 移除 "清除历史记录"
            mClearHistoryView.setVisibility(View.GONE);
        } else {
            mClearHistoryView.setVisibility(View.VISIBLE);
        }

        if (mHistoryTextAdapter == null) {
            mHistoryTextAdapter= new HistoryTextAdapter(getActivity(), rs, R.layout.view_item_search);
            mHistoryListView.setAdapter(mHistoryTextAdapter);
            mHistoryTextAdapter.notifyDataSetChanged();
        } else {
            mHistoryTextAdapter.updateData(rs);
        }
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fm_search_food:
            {    //美食
                ((SearchActivity)getActivity()).mEditView.setText(FMDatabaseDefine.TYPENAME_FOOD);
                ((SearchActivity)getActivity()).mSearchResultFrag.searchByTypename(FMDatabaseDefine.TYPENAME_FOOD);
                gotoSearch();
            }
            break;
            case R.id.fm_search_hotel:
            {
                ((SearchActivity)getActivity()).mEditView.setText(FMDatabaseDefine.TYPENAME_HOTEL);
                ((SearchActivity)getActivity()).mSearchResultFrag.searchByTypename(FMDatabaseDefine.TYPENAME_HOTEL);
                gotoSearch();
            }
            break;
            case R.id.fm_search_shopping:
            {
                ((SearchActivity)getActivity()).mEditView.setText(FMDatabaseDefine.TYPENAME_SHOP);
                ((SearchActivity)getActivity()).mSearchResultFrag.searchByTypename(FMDatabaseDefine.TYPENAME_SHOP);
                gotoSearch();
            }
            break;
            case R.id.fm_search_park:
            {
                ((SearchActivity)getActivity()).mEditView.setText(FMDatabaseDefine.TYPENAME_RELAX);
                ((SearchActivity)getActivity()).mSearchResultFrag.searchByTypename(FMDatabaseDefine.TYPENAME_RELAX);
                gotoSearch();
            }
            break;

            case R.id.fm_search_serve:
            {
                ((SearchActivity)getActivity()).mEditView.setText(FMDatabaseDefine.TYPENAME_SERVER);
                ((SearchActivity)getActivity()).mSearchResultFrag.searchByTypename(FMDatabaseDefine.TYPENAME_SERVER);
                gotoSearch();
            }
            break;
            case R.id.fm_search_wc:
            {
                ((SearchActivity)getActivity()).mEditView.setText(FMDatabaseDefine.TYPENAME_WC);
                ((SearchActivity)getActivity()).mSearchResultFrag.searchByTypename(FMDatabaseDefine.TYPENAME_WC);
                gotoSearch();
            }
            break;
            case R.id.fm_search_atm:
            {
                ((SearchActivity)getActivity()).mEditView.setText(FMDatabaseDefine.TYPENAME_ATM);
                ((SearchActivity)getActivity()).mSearchResultFrag.searchByTypename(FMDatabaseDefine.TYPENAME_ATM);
                gotoSearch();
            }
            break;
            case R.id.fm_search_ele:
            {
                ((SearchActivity)getActivity()).mEditView.setText(FMDatabaseDefine.TYPENAME_ELEVATOR);
                ((SearchActivity)getActivity()).mSearchResultFrag.searchByTypename(FMDatabaseDefine.TYPENAME_ELEVATOR);
                gotoSearch();
            }
        break;

            default:
            break;
        }
    }


    private void gotoSearch() {
        SearchActivity.fixedCursor(((SearchActivity)getActivity()).mEditView);
        ((SearchActivity)getActivity()).showSearchResultFragment();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position >= mHistoryTextAdapter.getCount()) {  //清除历史记录
            mHistoryTextAdapter.clearData();
            mClearHistoryView.setVisibility(View.GONE);
            mSearchElementDAO.clear();
            return;
        }
    }
}
