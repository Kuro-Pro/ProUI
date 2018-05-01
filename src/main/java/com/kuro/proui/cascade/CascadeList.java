package com.kuro.proui.cascade;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.kuro.proui.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kuro
 * @date 2018/4/30
 */
public class CascadeList extends LinearLayout {

    private static final int MAX_LIST_COUNT = 3;
    private int listCount;
    private Context mContext;
    private List<RecyclerView> mLists;
    private List<CascadeAdapter> mAdapters;
    private OnItemSelectedListener mListener;

    public interface OnItemSelectedListener {

        void onItemSelectedListener(View view, int level, int position);

    }

    public CascadeList(Context context) {
        this(context, null);
    }

    public CascadeList(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CascadeList(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;

        TypedArray ta = mContext.obtainStyledAttributes(attrs, R.styleable.CascadeList, 0, 0);
        listCount = ta.getInteger(R.styleable.CascadeList_listCount, MAX_LIST_COUNT);
        ta.recycle();

        initView();
    }

    private void initView() {
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.cascade_list, this);
        mLists = new ArrayList<>(listCount);
        mAdapters = new ArrayList<>(listCount);
        for (int i = 0; i < listCount; i++) {
            if (CascadeBean.PRIMARY == i) {
                RecyclerView recyclerView = rootView.findViewById(R.id.rv_primary);
                mLists.add(recyclerView);
                continue;
            }
            if (CascadeBean.MINOR == i) {
                RecyclerView recyclerView = rootView.findViewById(R.id.rv_minor);
                mLists.add(recyclerView);
                continue;
            }
            if (CascadeBean.LOWEST == i) {
                RecyclerView recyclerView = rootView.findViewById(R.id.rv_lowest);
                mLists.add(recyclerView);
            }
        }
        initCascade();
    }

    private void initCascade() {
        RecyclerView recyclerView;
        for (int i = 0; i < mLists.size(); i++) {
            recyclerView = mLists.get(i);
            recyclerView.setVisibility(VISIBLE);
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            CascadeAdapter adapter = new CascadeAdapter(mContext);
            final int finalI = i;
            adapter.setOnItemClickListener(new CascadeAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    int next = finalI + 1;
                    if (next >= 0 && next < listCount) {
                        setData(next, mAdapters.get(finalI).getItem(position).next());
                    }
                    if (mListener != null) {
                        mListener.onItemSelectedListener(view, finalI, position);
                    }
                }
            });
            recyclerView.setAdapter(adapter);
            mAdapters.add(adapter);
        }
    }

    public void setPrimaryData(List<? extends CascadeBean> data) {
        setData(CascadeBean.PRIMARY, data);
    }

    public void setMinorData(List<? extends CascadeBean> data) {
        setData(CascadeBean.MINOR, data);
    }

    public void setLowestData(List<? extends CascadeBean> data) {
        setData(CascadeBean.LOWEST, data);
    }

    private void setData(int adapter, List<? extends CascadeBean> data) {
        if (data == null || data.isEmpty()) {
            return;
        }
        mAdapters.get(adapter).setList(data);
        mAdapters.get(adapter).setCurrentPosition(0);
        if (adapter >= listCount) {
            return;
        }
        setData(adapter + 1, data.get(0).next());
    }

    public CascadeBean getItem(int level, int position) {
        return mAdapters.get(level).getItem(position);
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        this.mListener = listener;
    }
}
