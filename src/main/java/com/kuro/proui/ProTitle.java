package com.kuro.proui;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * @author Kuro
 * @date 2018/1/14
 */
public class ProTitle extends ConstraintLayout {

    /**
     * 双击手势
     */
    private GestureDetector detector;
    /**
     * 双击监听回调
     */
    private OnDoubleClickListener doubleClickListener;

    //region 左边控件属性
    /**
     * 是否可以返回
     */
    private boolean canBack;
    /**
     * 左边按钮
     */
    private TextView leftButton;
    /**
     * 左边文本
     */
    private String leftText;
    /**
     * 左边文本字体大小
     */
    private int leftTextSize;
    /**
     * 左边文本字体颜色
     */
    private int leftTextColor;
    /**
     * 左边按钮图片
     */
    private int leftImgRes;
    //endregion

    //region title控件属性
    /**
     * 是否需要loading
     */
    private boolean needLoading;
    /**
     * loading
     */
    private ContentLoadingProgressBar loading;
    /**
     * 标题文本字体颜色
     */
    private int titleTextColor;
    /**
     * 标题文本字体大小
     */
    private int titleTextSize;
    /**
     * 标题文本
     */
    private String titleText;
    /**
     * 标题
     */
    private TextView title;
    //endregion

    //region 右边控件属性
    /**
     * 是否使用双菜单
     */
    private boolean useDoubleMenu;
    /**
     * 双菜单按钮
     */
    private ImageButton menuButton;
    /**
     * 双菜单按钮图片资源
     */
    private int menuBtnImgRes;
    /**
     * 默认菜单按钮
     */
    private TextView menu;
    /**
     * 菜单文本
     */
    private String menuText;
    /**
     * 菜单字体大小
     */
    private int menuTextSize;
    /**
     * 菜单字体颜色
     */
    private int menuTextColor;
    /**
     * 菜单图片资源
     */
    private int menuImgRes;
    //endregion


    public ProTitle(Context context) {
        this(context, null);
    }

    public ProTitle(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProTitle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        detector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener());
        detector.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (doubleClickListener != null) {
                    doubleClickListener.onDoubleClick();
                }
                return false;
            }

            @Override
            public boolean onDoubleTapEvent(MotionEvent e) {
                return false;
            }
        });
        initView(attrs);
    }

    private void initView(AttributeSet attrs) {
        Context context = getContext();
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ProTitle, 0, 0);

        canBack = ta.getBoolean(R.styleable.ProTitle_canBack, false);
        leftText = ta.getString(R.styleable.ProTitle_leftText);
        leftTextSize = getSpTextSize(ta, R.styleable.ProTitle_leftTextSize, 14);
        leftTextColor = ta.getColor(R.styleable.ProTitle_leftTextColor, ContextCompat.getColor(context, R.color.white));
        leftImgRes = ta.getResourceId(R.styleable.ProTitle_leftImgRes, R.mipmap.ic_back);

        needLoading = ta.getBoolean(R.styleable.ProTitle_needLoading, false);
        titleText = ta.getString(R.styleable.ProTitle_titleText);
        titleTextSize = getSpTextSize(ta, R.styleable.ProTitle_titleTextSize, 16);
        titleTextColor = ta.getColor(R.styleable.ProTitle_titleColor, ContextCompat.getColor(context, R.color.white));

        useDoubleMenu = ta.getBoolean(R.styleable.ProTitle_useDoubleMenu, false);
        menuBtnImgRes = ta.getResourceId(R.styleable.ProTitle_menuBtnImgRes, R.mipmap.ic_add);
        menuText = ta.getString(R.styleable.ProTitle_menuText);
        menuTextSize = getSpTextSize(ta, R.styleable.ProTitle_menuTextSize, 14);
        menuTextColor = ta.getColor(R.styleable.ProTitle_menuTextColor, ContextCompat.getColor(context, R.color.white));
        menuImgRes = ta.getResourceId(R.styleable.ProTitle_menuImgRes, R.mipmap.ic_more);
        ta.recycle();
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.pro_title, this);
        rootView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    v.performClick();
                }
                detector.onTouchEvent(event);
                return true;
            }
        });
        rootView.setBackgroundColor(0xFF0000FF);
        initLeftButton();
        initTitle();
        initMenuButton();
    }

    /**
     * 初始化左边控件
     */
    private void initLeftButton() {
        leftButton = findViewById(R.id.back);
        leftButton.setText(leftText);
        leftButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, leftTextSize);
        leftButton.setTextColor(leftTextColor);
        setLeftText(leftText);
        setCanBack(canBack);
        setLeftImgRes(leftImgRes);
    }

    /**
     * 初始化title
     */
    private void initTitle() {
        loading = findViewById(R.id.progress);
        setNeedLoading(needLoading);
        showLoading();
        title = findViewById(R.id.title);
        title.setText(titleText);
        title.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleTextSize);
        title.setTextColor(titleTextColor);
    }

    /**
     * 初始化菜单控件
     */
    private void initMenuButton() {
        menuButton = findViewById(R.id.button);
        setUseDoubleMenu(useDoubleMenu);
        setMenuBtnImgRes(menuBtnImgRes);
        menu = findViewById(R.id.menu);
        menu.setVisibility(TextUtils.isEmpty(menuText) && menuImgRes == 0 ? GONE : VISIBLE);
        menu.setText(menuText);
        menu.setTextSize(TypedValue.COMPLEX_UNIT_PX, menuTextSize);
        menu.setTextColor(menuTextColor);
        setMenuImgRes(menuImgRes);
    }

    private int getSpTextSize(TypedArray typedArray, int index, int defaultValue) {
        return typedArray.getDimensionPixelSize(index,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                        defaultValue, getResources().getDisplayMetrics()));
    }

    //公共方法

    public void setLeftBtnOnClickListener(OnClickListener listener) {
        leftButton.setOnClickListener(listener);
    }

    public void showLoading() {
        if (loading == null || !needLoading) {
            return;
        }
        if (!loading.isShown()) {
            loading.show();
        }
    }

    public void hideLoading() {
        if (loading == null) {
            return;
        }
        if (loading.isShown()) {
            loading.hide();
        }
    }

    public void setCanBack(boolean canBack) {
        this.canBack = canBack;
        OnClickListener listener = !this.canBack ? null : new OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = getContext();
                if (context instanceof Activity) {
                    ((Activity) context).finish();
                }
            }
        };
        leftButton.setOnClickListener(listener);
    }

    public boolean isCanBack() {
        return canBack;
    }

    public void setLeftText(CharSequence text) {
        leftButton.setText(text);
    }

    public void setLeftTextSize(int leftTextSize) {
        leftButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, leftTextSize);
    }

    public void setLeftTextColor(int leftTextColor) {
        leftButton.setTextColor(leftTextColor);
    }

    public void setLeftImgRes(int leftImgRes) {
        Drawable drawable = ContextCompat.getDrawable(getContext(), leftImgRes);
        if (drawable != null) {
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        }
        leftButton.setCompoundDrawables(drawable, null, null, null);
    }

    public void setNeedLoading(boolean needLoading) {
        this.needLoading = needLoading;
        loading.setVisibility(needLoading ? VISIBLE : GONE);
    }

    public boolean isNeedLoading() {
        return needLoading;
    }

    public void setTitle(CharSequence text) {
        title.setText(text);
    }

    public void setTitleTextColor(int titleTextColor) {
        title.setTextColor(titleTextColor);
    }

    public void setTitleTextSize(int titleTextSize) {
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, titleTextSize);
    }

    public void setUseDoubleMenu(boolean useDoubleMenu) {
        this.useDoubleMenu = useDoubleMenu;
        menuButton.setVisibility(useDoubleMenu ? VISIBLE : GONE);
    }

    public boolean isUseDoubleMenu() {
        return useDoubleMenu;
    }

    public void setMenuBtnImgRes(int menuBtnImgRes) {
        if (menuBtnImgRes == 0) {
            return;
        }
        menuButton.setImageResource(menuBtnImgRes);
    }

    public void setMenuText(CharSequence text) {
        menu.setText(text);
    }

    public void setMenuTextSize(int menuTextSize) {
        menu.setTextSize(TypedValue.COMPLEX_UNIT_SP, menuTextSize);
    }

    public void setMenuTextColor(int menuTextColor) {
        menu.setTextColor(menuTextColor);
    }

    public void setMenuImgRes(int menuImgRes) {
        Drawable drawable = ContextCompat.getDrawable(getContext(), menuImgRes);
        if (drawable != null) {
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        }
        menu.setCompoundDrawables(null, null, drawable, null);
    }

    /**
     * 设置title双击事件
     *
     * @param listener OnDoubleClickListener
     */
    public void setOnDoubleClickListener(OnDoubleClickListener listener) {
        this.doubleClickListener = listener;
    }

    public interface OnDoubleClickListener {
        /**
         * title双击时间
         */
        void onDoubleClick();
    }

}
