package com.kuro.proui;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ContentLoadingProgressBar;
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
     * 控件背景资源
     */
    private int backgroundResource;
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
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ProTitle, 0, 0);
        backgroundResource = ta.getResourceId(R.styleable.ProTitle_backgroundRes, R.color.blue);

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
        initView();
    }

    private void initView() {
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
        rootView.setBackgroundResource(backgroundResource);
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
        Drawable drawable = ContextCompat.getDrawable(getContext(), leftImgRes);
        if (drawable != null) {
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        }
        leftButton.setCompoundDrawables(drawable, null, null, null);
        leftButton.setVisibility(canBack ? VISIBLE : INVISIBLE);
        if (canBack) {
            leftButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = getContext();
                    if (context instanceof Activity) {
                        ((Activity) context).finish();
                    }
                }
            });
        }
    }

    /**
     * 初始化title
     */
    private void initTitle() {
        loading = findViewById(R.id.progress);
        loading.setVisibility(needLoading ? VISIBLE : GONE);
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
        menuButton.setVisibility(useDoubleMenu ? VISIBLE : GONE);
        menuButton.setImageResource(menuBtnImgRes);
        menu = findViewById(R.id.menu);
        menu.setText(menuText);
        menu.setTextSize(TypedValue.COMPLEX_UNIT_PX, menuTextSize);
        menu.setTextColor(menuTextColor);
        Drawable drawable = ContextCompat.getDrawable(getContext(), menuImgRes);
        if (drawable != null) {
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        }
        menu.setCompoundDrawables(null, null, drawable, null);
    }

    private int getSpTextSize(TypedArray typedArray, int index, int defaultValue) {
        return typedArray.getDimensionPixelSize(index,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                        defaultValue, getResources().getDisplayMetrics()));
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
