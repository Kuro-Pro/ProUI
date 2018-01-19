package com.kuro.proui;

import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

/**
 * @author kuro
 * @date 2018/1/19
 */
public abstract class ProDialog extends DialogFragment {

    private int x;
    private int y;
    private int offsetX;
    private int offsetY;
    private int mGravity;
    private int animationResId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        animationResId = getAnimation();
        if (animationResId == 0) {
            animationResId = R.style.fade_from_top_to_bottom;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(getLayoutId(), container, false);
        rootView.measure(0, 0);
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams params = window.getAttributes();
            params.gravity = Gravity.TOP | Gravity.LEFT;
            params.width = WindowManager.LayoutParams.WRAP_CONTENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            computePosition(rootView);
            params.x = x;
            params.y = y;
            if (shouldHideBackground()) {
                params.dimAmount = 0.0f;
            }
            window.setAttributes(params);
            window.setWindowAnimations(animationResId);
        }
        return rootView;
    }

    /**
     * 计算布局左上角坐标
     *
     * @param view 根布局
     */
    private void computePosition(View view) {
        switch (mGravity) {
            case Gravity.CENTER:
                x = offsetX - view.getMeasuredWidth() / 2;
                y = offsetY;
                break;
            case Gravity.END:
            case Gravity.RIGHT:
                x = offsetX - view.getMeasuredWidth();
                y = offsetY;
                break;
            case Gravity.START:
            case Gravity.LEFT:
            default:
                x = offsetX;
                y = offsetY;
                break;
        }
    }

    public void showAsDropDown(AppCompatActivity activity, View anchorView, String tag, int gravity) {
        mGravity = gravity;
        if (activity == null || isShowing()) {
            return;
        }
        FragmentManager manager = activity.getSupportFragmentManager();
        FragmentTransaction mTransaction = manager.beginTransaction();
        Fragment mFragment = manager.findFragmentByTag(tag);
        if (mFragment != null) {
            //为了不重复显示dialog，在显示对话框之前移除正在显示的对话框
            mTransaction.remove(mFragment);
        }
        computeOffset(anchorView);
        show(mTransaction, tag);
    }

    /**
     * 计算偏移量
     *
     * @param anchorView 弹出窗口锁定的视图
     */
    private void computeOffset(View anchorView) {
        Rect rect = new Rect();
        anchorView.getGlobalVisibleRect(rect);
        switch (mGravity) {
            case Gravity.CENTER:
                offsetX = rect.left + anchorView.getWidth() / 2;
                offsetY = rect.bottom;
                anchorView.getWindowVisibleDisplayFrame(rect);
                offsetY -= rect.top;
                break;
            case Gravity.END:
            case Gravity.RIGHT:
                offsetX = rect.right;
                offsetY = rect.bottom;
                anchorView.getWindowVisibleDisplayFrame(rect);
                offsetY -= rect.top;
                break;
            case Gravity.START:
            case Gravity.LEFT:
            default:
                offsetX = rect.left;
                offsetY = rect.bottom;
                anchorView.getWindowVisibleDisplayFrame(rect);
                offsetY -= rect.top;
                break;
        }
    }

    /**
     * 是否显示
     *
     * @return false:isHidden  true:isShowing
     */
    protected boolean isShowing() {
        return this.getDialog() != null && this.getDialog().isShowing();
    }

    /**
     * 获取根布局Id
     *
     * @return
     */
    abstract protected int getLayoutId();

    /**
     * 获取动画资源
     *
     * @return
     */
    abstract protected int getAnimation();

    /**
     * 是否隐藏背景
     *
     * @return
     */
    abstract protected boolean shouldHideBackground();

}
