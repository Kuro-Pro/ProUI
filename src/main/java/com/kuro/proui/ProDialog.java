package com.lexing360.uibase;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.kuro.proui.ScreenUtil;

/**
 * @author kuro
 * @date 2018/1/19
 */
public abstract class ProDialog extends DialogFragment {

    /**
     * dialog左上角x坐标
     */
    private int x;
    /**
     * dialog左上角y坐标
     */
    private int y;
    private int offsetX;
    private int offsetY;
    private int mGravity;
    private int mAnimationResId;
    private boolean mIsDropdown;
    private boolean mIsCenter;
    /**
     * dialog是否正在关闭
     * 避免dialog还没开始展示就已经调用
     * {@link ProDialog#hideDialog()}
     * 导致dialog无法隐藏
     */
    private boolean mIsClosing = false;
    private boolean mIsBottom;
    private int mDialogHeight;
    /**
     * 点击在dialog外是否可以隐藏
     */
    private boolean mCanCanceledOnTouchOutside;
    private int screenHeight;
    private int statusBarHeight;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        mAnimationResId = getAnimation();
        mCanCanceledOnTouchOutside = canCanceledOnTouchOutside();
        screenHeight = ScreenUtil.getScreenHeight(getContext());
        statusBarHeight = ScreenUtil.getStatusBarHeight(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View rootView = inflater.inflate(getLayoutId(), container, false);
        if (rootView == null) {
            return null;
        }
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        rootView.setLayoutParams(layoutParams);
        initView(rootView);
        final Window window = getDialog().getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = isWindowWidthMatchParent() ?
                    WindowManager.LayoutParams.MATCH_PARENT :
                    WindowManager.LayoutParams.WRAP_CONTENT;
            params.height = shouldMatchHeight() ? mDialogHeight : WindowManager.LayoutParams.WRAP_CONTENT;
            if (!mIsCenter) {
                params.gravity = Gravity.TOP | Gravity.START;
                computePosition(rootView);
                params.x = x;
                params.y = y;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    params.flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
                } else {
                    params.y -= statusBarHeight;
                }
            }
            if (mIsBottom) {
                params.gravity = Gravity.BOTTOM;
            }
            if (shouldHideBackground()) {
                params.dimAmount = 0.0f;
            }
            window.setAttributes(params);
            window.setWindowAnimations(mAnimationResId);
        }
        return rootView;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setCanceledOnTouchOutside(mCanCanceledOnTouchOutside);
        return dialog;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mIsClosing) {
            dismiss();
        }
    }

    /**
     * 计算布局左上角坐标
     *
     * @param view 根布局
     */
    private void computePosition(View view) {
        view.measure(0, 0);
        if (!mIsDropdown) {
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

    /**
     * 屏幕居中显示
     */
    public void showCenter(FragmentActivity activity, String tag) {
        mIsCenter = true;
        showDialog(activity, tag);
    }

    /**
     * 屏幕底部显示
     */
    public void showBottom(FragmentActivity activity, String tag) {
        mIsBottom = true;
        showDialog(activity, tag);
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

    /**
     * 在anchorView下方显示dialog
     *
     * @param anchorView 锚点视图
     * @param gravity    与anchorView的对齐方向
     * @param offsetX    水平偏移量
     * @param offsetY    垂直偏移量
     */
    public void showAsDropDown(FragmentActivity activity, View anchorView, String tag, int gravity, int offsetX, int offsetY) {
        mGravity = gravity;
        mIsDropdown = true;
        if (activity == null || isShowing()) {
            return;
        }
        computeOffset(anchorView, offsetX, offsetY);
        showDialog(activity, tag);
    }

    /**
     * 在anchorView下方显示dialog
     * 默认左对齐anchorView
     *
     * @param anchorView 锚点视图
     */
    public void showAsDropDown(FragmentActivity activity, View anchorView, String tag) {
        showAsDropDown(activity, anchorView, tag, Gravity.START, 0, 0);
    }

    public void showDialog(FragmentActivity fragmentActivity, String tag) {
        mIsClosing = false;
        FragmentTransaction transaction = prepareFragmentTransaction(fragmentActivity, tag);
        show(transaction, tag);
    }

    /**
     * 隐藏dialog调用此方法
     */
    public void hideDialog() {
        mIsClosing = true;
        if (isShowing()) {
            dismiss();
        }
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
        mDialogHeight = screenHeight - rect.bottom;
        switch (mGravity) {
            case Gravity.CENTER:
                this.offsetX = rect.left + anchorView.getWidth() / 2 + offsetX;
                this.offsetY = rect.bottom + offsetY;
                break;
            case Gravity.END:
            case Gravity.RIGHT:
                this.offsetX = rect.right + offsetX;
                this.offsetY = rect.bottom + offsetY;
                break;
            case Gravity.START:
            case Gravity.LEFT:
            default:
                this.offsetX = rect.left + offsetX;
                this.offsetY = rect.bottom + offsetY;
                break;
        }
    }

    protected boolean shouldMatchHeight() {
        return false;
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
     * 初始化数据
     */
    protected abstract void initData();

    /**
     * 初始化布局
     *
     * @param rootView 根布局view
     */
    protected abstract void initView(View rootView);

    /**
     * 获取根布局Id
     *
     * @return 根布局ID
     */
    @LayoutRes
    abstract protected int getLayoutId();

    /**
     * 获取动画资源
     *
     * @return 动画资源ID
     */
    @IdRes
    abstract protected int getAnimation();

    /**
     * 是否隐藏背景
     *
     * @return true代表隐藏
     */
    abstract protected boolean shouldHideBackground();

    /**
     * 点击在dialog外是否可以隐藏
     *
     * @return true代表点击外部可以隐藏
     */
    abstract protected boolean canCanceledOnTouchOutside();

    /**
     * dialog的宽度是否需要充满屏幕
     *
     * @return true代表需要
     */
    abstract protected boolean isWindowWidthMatchParent();

}
