package com.example.cehuacaidan;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Scroller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 因为继承的是FrameLayout，不用重写onlayout也可以显示出来控件
 * 1、在onFinishInflate()方法里加载布局里的子view，因为不是用代码添加的所以可以获取
 * 2、在onMeasure方法获取子view的宽高
 * 3、在onlayout方法里自定义view摆放位置
 * 4、重写自定义控件的onTouchEvent方法，
 * 5、在closeMenu和openMenu中添加监听，让一次自能打开一个item
 */
public class SlideLayout extends FrameLayout {

    private View contentView;
    private View menuView;
    private int contentWidth;
    private int menuWidth;
    private int viewHeight;
    private int lastX;
    private Scroller scroller;

    public SlideLayout(@NonNull Context context) {
        super(context);
    }

    public SlideLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        scroller = new Scroller(getContext());
    }

    public SlideLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    /**
     * 当布局文件加载完成的时候回调这个方法
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        contentView = getChildAt(0);
        menuView = getChildAt(1);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        contentWidth = contentView.getMeasuredWidth();      //第一个子view的宽
        menuWidth = menuView.getMeasuredWidth();            //第二个子view的宽
        viewHeight = getMeasuredHeight();                   //自定义控件的高
    }

    /**
     *子控件摆放
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        //重新摆放了第二个子view的位置
        menuView.layout(contentWidth, 0, contentWidth + menuWidth, viewHeight);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        int eventX = (int) event.getRawX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = eventX;
                // 4-5、监听事件
                if(onStateChangeListenter != null){
                    onStateChangeListenter.onDown(this);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                int distencX = eventX - lastX;
                int toScrollX = getScrollX() - distencX;
          //--------------------------------------------
          // 4-2、不让向右滑动并且最大滑动距离不能超过第二个子控件的宽
                if (toScrollX <0){
                    toScrollX = 0;
                }else if (toScrollX > menuWidth){
                    toScrollX = menuWidth;
                }
          //--------------------------------------------

                scrollTo(toScrollX, getScrollY());      //4-1，使用scrollTo实现滑动效果

           //--------------------------------------------
          // 4-4、当item在左右滑动时，让listview无法上下滑动
                if(getScrollX()>getScrollY() && getScrollX() >3){
                    //反拦截
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
          //--------------------------------------------
               lastX = eventX;
                break;
            case MotionEvent.ACTION_UP:

          //-------------------------------------------
          //4-3、在松手的时候判断是把menu显示出来还是关闭
          //使用了Scroller类
                int totalScrollX = getScrollX();//偏移量
                if(totalScrollX < menuWidth/2){
                    //关闭Menu
                    closeMenu();
                }else{
                    //打开Menu
                    openMenu();
                }
          //----------------------------------------------
                break;
        }

        return true;
    }

    public void closeMenu() {
        int distanceX = 0 - getScrollX();   //计算要滑动多少距离
        scroller.startScroll(getScrollX(), getScrollY(), distanceX, getScrollY());
        invalidate();//强制刷新去完成computeScroll方法
        if(onStateChangeListenter != null){
            onStateChangeListenter.onClose(this);
        }
    }

    /**
     * 打开menu
     */
    public void openMenu() {
        int distanceX = menuWidth - getScrollX();
        scroller.startScroll(getScrollX(), getScrollY(), distanceX, getScrollY());
        invalidate();//强制刷新去完成computeScroll方法
        if(onStateChangeListenter != null){
            onStateChangeListenter.onOpen(this);
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if(scroller.computeScrollOffset()){
            scrollTo(scroller.getCurrX(),scroller.getCurrY());
            invalidate();
        }
    }


    /**
     * 监听SlideLayout状态的改变
     */
    public interface OnStateChangeListenter{
        void onClose(SlideLayout layout);
        void onDown(SlideLayout layout);
        void onOpen(SlideLayout layout);
    }

    private  OnStateChangeListenter onStateChangeListenter;

    /**
     * 设置SlideLayout状态的监听
     * @param onStateChangeListenter
     */
    public void setOnStateChangeListenter(OnStateChangeListenter onStateChangeListenter) {
        this.onStateChangeListenter = onStateChangeListenter;
    }

}
