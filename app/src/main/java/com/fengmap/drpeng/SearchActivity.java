package com.fengmap.drpeng;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.mgwaiter.R;
import com.fengmap.android.utils.FMLog;
import com.fengmap.android.wrapmv.db.FMDBMapElement;
import com.fengmap.android.wrapmv.db.FMDBSearchElement;


/**
 * 搜索界面。
 * Created by yangbin on 16/8/14.
 */

public class SearchActivity extends FragmentActivity implements View.OnClickListener, TextWatcher {
    // top bar
    protected ImageView mBackView;
    public    EditText  mEditView;
    protected ImageView mClearView;

    protected FragmentManager      mFragmentManager;
    protected SearchFragment       mSearchFrag;
    protected SearchResultFragment mSearchResultFrag;

    protected String mMapId;
    protected int mGroupId = 0;

    // 模糊搜索
    public static final int SEARCH_NAME     = 1;
    // 分类搜索
    public static final int SEARCH_TYPENAME = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fm_activity_search);
        init();
    }


    private void init() {
        mBackView = (ImageView) findViewById(R.id.fm_search_back);
        mEditView = (EditText) findViewById(R.id.fm_search_processing);
        mClearView = (ImageView) findViewById(R.id.fm_search_delete);

        mBackView.setOnClickListener(this);
        mEditView.addTextChangedListener(this);
        mClearView.setOnClickListener(this);

        mFragmentManager = getSupportFragmentManager();
        mSearchFrag = (SearchFragment) mFragmentManager.findFragmentById(R.id.fm_fragment_search);
        mSearchResultFrag = (SearchResultFragment) mFragmentManager.findFragmentById(R.id.fm_fragment_search_result);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            dealIntentBundle(b);
        } else {
            showSearchFragment();
        }
    }


    @Override
    protected void onNewIntent(Intent intent) {
        FMLog.le("SearchActivity","SearchActivity#onNewIntent");
        Bundle b = getIntent().getExtras();
        if (b != null) {
            dealIntentBundle(b);
        } else {
            showSearchFragment();
        }
        super.onNewIntent(intent);
    }


    private void dealIntentBundle(Bundle b) {
        mMapId = b.getString(FMAPI.ACTIVITY_MAP_ID, null);
        mGroupId = b.getInt(FMAPI.ACTIVITY_MAP_GROUP_ID, 0);

        String target = b.getString(FMAPI.TARGET_FRAGMENT);
        if (SearchFragment.class.getName().equals(target)) {
            FMDBSearchElement e = (FMDBSearchElement) b.getSerializable(FMAPI.ACTIVITY_OBJ_SEARCH_HISTORY);
            if (e != null) {
                mEditView.setText(e.getHistoryname());
            }
            showSearchFragment();
        } else if (SearchResultFragment.class.getName().equals(target)) {
            FMDBMapElement e = (FMDBMapElement) b.getSerializable(FMAPI.ACTIVITY_OBJ_SEARCH_RESULT);
            if (e != null) {
                mEditView.setText(e.getHistory());
                fixedCursor(mEditView);
                mSearchResultFrag.searchByKeywords(e.getHistory());
            }
            showSearchResultFragment();
        } else {
            showSearchFragment();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fm_search_back:
                onBackPressed();
                break;
            case R.id.fm_search_processing:
                // 点击搜索框, 进入新搜索结果页面
                showSearchResultFragment();
                break;
            case R.id.fm_search_delete:
                mEditView.setText("");
                if (mSearchResultFrag.mAutoTextAdapter != null) {
                    mSearchResultFrag.mAutoTextAdapter.clearData();
                    mSearchResultFrag.mResultListView.removeFooterView(mSearchResultFrag.mMoreView);
                }
                break;

            default:
                break;
        }
    }


    @Override
    public void onBackPressed() {
        if (mSearchResultFrag.isVisible()) {
            showSearchFragment();
            return;
        }

        super.onBackPressed();
    }

    /**
     * 显示搜索结果界面。
     */
    public void showSearchResultFragment() {
        mSearchResultFrag.reset();
        mFragmentManager.beginTransaction().hide(mSearchFrag).show(mSearchResultFrag).commit();
    }

    /**
     * 显示搜索界面。
     */
    public void showSearchFragment() {
        mFragmentManager.beginTransaction().hide(mSearchResultFrag).show(mSearchFrag).commit();
    }


    public SearchFragment getSearchFragment() {
        return mSearchFrag;
    }

    public SearchResultFragment getSearchResultFragment() {
        return mSearchResultFrag;
    }

    /**
     * 修复光标的位置。
     * @param pEditText
     */
    public static void fixedCursor(EditText pEditText) {
        Editable text = pEditText.getText();
        if(text instanceof Spannable) {
            Selection.setSelection(text, text.length());
        }
    }



    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String con = mEditView.getText().toString().trim();
        if (con.equals("")) {
            return;
        }
        if (!mSearchResultFrag.isVisible()) {
            showSearchResultFragment();
        }
        mSearchResultFrag.searchByKeywords(con);
    }

}
