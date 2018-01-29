package com.kuro.proui;

import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import java.lang.ref.WeakReference;

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
    private boolean mIsDropdown;
    private boolean mIsCenter;
    private DialogHandler dialogHandler;

    private static class DialogHandler extends Handler {

        public static final int SHOW = 1 << 1;
        public static final int HIDE = 1 << 2;

        private WeakReference<ProDialog> proDialog;
        private WeakReference<FragmentActivity> activity;

        public DialogHandler(FragmentActivity fragmentActivity, ProDialog dialog) {
            proDialog = new WeakReference<>(dialog);
            activity = new WeakReference<>(fragmentActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            ProDialog proDialog = this.proDialog.get();
            FragmentActivity fragmentActivity = activity.get();
            if (fragmentActivity == null || proDialog == null) {
                return;
            }
            switch (msg.what) {
                case SHOW:
                    String tag = (String) msg.obj;
                    FragmentTransaction transaction = proDialog.prepareFragmentTransaction(fragmentActivity, tag);
                    proDialog.show(transaction, tag);
                    break;
                case HIDE:
                    removeCallbacksAndMessages(null);
                    if (proDialog.isShowing()) {
                        proDialog.dismiss();
                    }
                    break;
                default:
                    break;
            }
        }

    }

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
        if (!mIsDropdown) {
            if (mIsCenter) {
                x = x - view.getMeasuredWidth() / 2;
                y = y - view.getMeasuredHeight() / 2;
            }
            return;
        }
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

    public void showCenter(FragmentActivity activity, String tag) {
        mIsCenter = true;
        DisplayMetrics metrics = activity.getResources().getDisplayMetrics();
        showAtLocation(activity, tag, metrics.widthPixels / 2, metrics.heightPixels / 2);
    }

    public void showAtLocation(FragmentActivity activity, String tag, int offsetX, int offsetY) {
        mIsDropdown = false;
        if (activity == null || isShowing()) {
            return;
        }
        x = offsetX;
        y = offsetY;
        showDialog(activity, tag);
    }

    public void showAsDropDown(FragmentActivity activity, View anchorView, String tag, int gravity, int offsetX, int offsetY) {
        mGravity = gravity;
        mIsDropdown = true;
        if (activity == null || isShowing()) {
            return;
        }
        computeOffset(anchorView, offsetX, offsetY);
        showDialog(activity, tag);
    }

    public void showAsDropDown(FragmentActivity activity, View anchorView, String tag) {
        showAsDropDown(activity, anchorView, tag, Gravity.LEFT, 0, 0);
    }

    private void showDialog(FragmentActivity fragmentActivity, String tag) {
        if (dialogHandler == null) {
            dialogHandler = new DialogHandler(fragmentActivity, this);
        }
        Message message = dialogHandler.obtainMessage(DialogHandler.SHOW);
        message.obj = tag;
        dialogHandler.sendMessageDelayed(message, 500);
    }

    public void hideDialog() {
        if (dialogHandler == null) {
            return;
        }
        dialogHandler.sendEmptyMessage(DialogHandler.HIDE);
    }

    private FragmentTransaction prepareFragmentTransaction(FragmentActivity activity, String tag) {
        FragmentManager manager = activity.getSupportFragmentManager();
        FragmentTransaction mTransaction = manager.beginTransaction();
        Fragment mFragment = manager.findFragmentByTag(tag);
        if (mFragment != null) {
            //为了不重复显示dialog，在显示对话框之前移除正在显示的对话框
            mTransaction.remove(mFragment);
        }
        return mTransaction;
    }

    /**
     * 计算偏移量
     *
     * @param anchorView 弹出窗口锁定的视图
     */
    private void computeOffset(View anchorView, int offsetX, int offsetY) {
        Rect rect = new Rect();
        anchorView.getGlobalVisibleRect(rect);
        switch (mGravity) {
            case Gravity.CENTER:
                this.offsetX = rect.left + anchorView.getWidth() / 2 + offsetX;
                this.offsetY = rect.bottom + offsetY;
                anchorView.getWindowVisibleDisplayFrame(rect);
                this.offsetY -= rect.top;
                break;
            case Gravity.END:
            case Gravity.RIGHT:
                this.offsetX = rect.right + offsetX;
                this.offsetY = rect.bottom + offsetY;
                anchorView.getWindowVisibleDisplayFrame(rect);
                this.offsetY -= rect.top;
                break;
            case Gravity.START:
            case Gravity.LEFT:
            default:
                this.offsetX = rect.left + offsetX;
                this.offsetY = rect.bottom + offsetY;
                anchorView.getWindowVisibleDisplayFrame(rect);
                this.offsetY -= rect.top;
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
