package com.kuro.proui.cascade;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kuro.proui.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kuro
 * @date 2018/4/30
 */
public class CascadeAdapter extends RecyclerView.Adapter<CascadeAdapter.BaseViewHolder> {

    private Context mContext;
    private List<CascadeBean> mData;
    private OnItemClickListener mListener;
    private int currentPosition = 0;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public CascadeAdapter(Context context) {
        this.mContext = context;
        mData = new ArrayList<>();
    }

    @Override
    public int getItemViewType(int position) {
        return mData.get(position).getLevel();
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_cascade, parent, false);
        return new BaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final BaseViewHolder holder, int position) {
        TextView textView = holder.itemView.findViewById(R.id.tv_name);
        textView.setText(mData.get(position).getItemName());
        int textColor = currentPosition == position ? Color.WHITE : Color.BLACK;
        int bgColor = currentPosition != position ? Color.WHITE : Color.BLACK;
        textView.setTextColor(textColor);
        textView.setBackgroundColor(bgColor);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener == null) {
                    return;
                }
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition > RecyclerView.NO_POSITION
                        && adapterPosition < mData.size()) {
                    mListener.onItemClick(v, adapterPosition);
                    notifyItemChanged(currentPosition);
                    currentPosition = adapterPosition;
                    notifyItemChanged(currentPosition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public CascadeBean getItem(int position) {
        if (position > RecyclerView.NO_POSITION
                && position < mData.size()) {
            return mData.get(position);
        }
        return null;
    }

    public void setList(final List<? extends CascadeBean> list) {
        if (list == null) {
            return;
        }
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return mData.size();
            }

            @Override
            public int getNewListSize() {
                return list.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return mData.get(oldItemPosition).getItemName()
                        .equals(list.get(newItemPosition).getItemName());
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return mData.get(oldItemPosition).equals(list.get(newItemPosition));
            }

            @Override
            public Object getChangePayload(int oldItemPosition, int newItemPosition) {
                return "";
            }
        });
        result.dispatchUpdatesTo(this);
        mData.clear();
        mData.addAll(list);
    }

    public void setCurrentPosition(int position) {
        this.currentPosition = position;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    class BaseViewHolder extends RecyclerView.ViewHolder {

        public BaseViewHolder(View itemView) {
            super(itemView);
        }

    }

}
