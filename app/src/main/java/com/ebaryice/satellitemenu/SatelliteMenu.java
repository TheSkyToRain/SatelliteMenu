package com.ebaryice.satellitemenu;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

/**
 * Created by Ebaryice on 2017/11/14.
 */

public class SatelliteMenu extends ViewGroup implements View.OnClickListener{


    private static final int POS_LEFT_TOP = 0;
    private static final int POS_LEFT_BOTTOM = 1;
    private static final int POS_RIGHT_TOP = 2;
    private static final int POS_RIGHT_BOTTOM = 3;

    private int toggleMenuDuration = 300;

    private Position mPosition = Position.RIGHT_BOTTOM;

    private int mRadius;

    /**
     * 菜单的默认状态
     */
    private Status mCurrentStatus = Status.CLOSE;
    /**
     * 主button
     */
    private View mCenterButton;

    private OnMenuItemClickListener onMenuItemClickListener;

    public void setOnMenuItemClickListener(OnMenuItemClickListener onMenuItemClickListener) {
        this.onMenuItemClickListener = onMenuItemClickListener;
    }


    public enum Status
    {
        OPEN,CLOSE
    }
    /**
     * 菜单的位置
     */
    public enum Position
    {
        LEFT_TOP,LEFT_BOTTOM,RIGHT_TOP,RIGHT_BOTTOM
    }

    /**
     * 点击子菜单的回调接口
     */
    public interface OnMenuItemClickListener
    {
        void onClick(View view,int pos);
    }

    public SatelliteMenu(Context context) {
        this(context,null);
    }

    public SatelliteMenu(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SatelliteMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                100,getResources().getDisplayMetrics());
        //获取自定义属性
        obtainStyledAttrs(attrs);

    }

    /**
     * 获取自定义属性
     * @param attrs
     */
    private void obtainStyledAttrs(AttributeSet attrs){

        TypedArray ta = getContext().obtainStyledAttributes(attrs,R.styleable.SatelliteMenu);
        //第二个参数是默认值
        int pos = ta.getInt(R.styleable.SatelliteMenu_position,POS_RIGHT_BOTTOM);
        switch (pos)
        {
            case POS_LEFT_TOP:
                mPosition = Position.LEFT_TOP;
                break;
            case POS_LEFT_BOTTOM:
                mPosition = Position.LEFT_BOTTOM;
                break;
            case POS_RIGHT_TOP:
                mPosition = Position.RIGHT_TOP;
                break;
            case POS_RIGHT_BOTTOM:
                mPosition = Position.RIGHT_BOTTOM;
                break;
        }
        mRadius = (int) ta.getDimension(R.styleable.SatelliteMenu_radius,TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                100,getResources().getDisplayMetrics()));
        Log.d("TAG","position = "+ mPosition+",radius = "+mRadius);
        ta.recycle();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int count = getChildCount();
        //child不可以小于0
        if (count<=0) throw new RuntimeException();
        for (int i=0;i<count;i++){
            //测量child
            measureChild(getChildAt(i),widthMeasureSpec,heightMeasureSpec);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int i, int i1, int i2, int i3) {
        if (changed)
        {
            layoutCenterButton();
            int count = getChildCount();
            for (int j=0;j<count-1;j++){
                View child = getChildAt(j+1);

                child.setVisibility(View.GONE);

                int cl = (int) (mRadius*Math.sin(Math.PI/2/(count-2)*j));
                int ct = (int) (mRadius*Math.cos(Math.PI/2/(count-2)*j));

                int cWidth = child.getMeasuredWidth();
                int cHeight = child.getMeasuredHeight();
                //菜单在下方
                if (mPosition == Position.LEFT_BOTTOM||mPosition == Position.RIGHT_BOTTOM){
                    ct = getMeasuredHeight() - cHeight - ct;
                }
                //菜单在右方
                if (mPosition == Position.RIGHT_TOP||mPosition == Position.RIGHT_BOTTOM){
                    cl = getMeasuredWidth() - cWidth - cl;
                }

                child.layout(cl,ct,cl+cWidth,ct+cHeight);

            }

        }
    }

    /**
     * 定位主菜单按钮
     */
    private void layoutCenterButton() {
        mCenterButton = getChildAt(0);
        mCenterButton.setOnClickListener(this);
        int l = 0;
        int t = 0;

        int width = mCenterButton.getMeasuredWidth();
        int height = mCenterButton.getMeasuredHeight();
        switch (mPosition)
        {
            case LEFT_TOP:
                l = 0;
                t = 0;
                break;
            case LEFT_BOTTOM:
                l = 0;
                t = getMeasuredHeight() - height;
                break;
            case RIGHT_TOP:
                l = getMeasuredWidth() - width;
                t = 0;
                break;
            case RIGHT_BOTTOM:
                l = getMeasuredWidth() - width;
                t = getMeasuredHeight() - height;
                break;
        }
        mCenterButton.layout(l,t,l+width,t+height);
    }

    public void setToggleMenuDuration(int duration){
        this.toggleMenuDuration = duration;
    }

    @Override
    public void onClick(View view) {
        mCenterButton = findViewById(R.id.main_button);

        rotateCenterButton(view,0f,360f,300);
        toggleMenu(toggleMenuDuration);
    }

    /**
     * 切换菜单
     */
    private void toggleMenu(int duration) {
        //为item添加动画(平移和旋转)
        int count = getChildCount();
        for (int i=0;i<count-1;i++){
            final View childView = getChildAt(i+1);

            childView.setVisibility(VISIBLE);
            int cl = (int) (mRadius*Math.sin(Math.PI/2/(count-2)*i));
            int ct = (int) (mRadius*Math.cos(Math.PI/2/(count-2)*i));

            int xflag = 1;
            int yflag = 1;
            if (mPosition == Position.LEFT_TOP||mPosition == Position.LEFT_BOTTOM)
            {
                xflag = -1;
            }
            if (mPosition == Position.LEFT_TOP||mPosition == Position.RIGHT_TOP)
            {
                yflag = -1;
            }
            AnimationSet animationSet = new AnimationSet(true);
            Animation tranAnim = null;
            if (mCurrentStatus == Status.CLOSE){
                tranAnim = new TranslateAnimation(xflag*cl,
                        0,yflag*ct,0);
                childView.setClickable(true);
                childView.setFocusable(true);
            }else{
                tranAnim = new TranslateAnimation(0,
                        xflag*cl,0,yflag*ct);
                childView.setClickable(false);
                childView.setFocusable(false);
            }
            tranAnim.setFillAfter(true);
            tranAnim.setDuration(duration);
            tranAnim.setStartOffset((i*100)/count);

            tranAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (mCurrentStatus == Status.CLOSE){
                        childView.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            //旋转动画
            RotateAnimation anim = new RotateAnimation(0,720,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setDuration(duration);
            anim.setFillAfter(true);

            animationSet.addAnimation(anim);
            animationSet.addAnimation(tranAnim);

            childView.startAnimation(animationSet);

            final int pos = i;
            childView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onMenuItemClickListener != null)
                        onMenuItemClickListener.onClick(view,pos);

                        menuItemAnim(pos);
                        changeStatus();
                }
            });

        }
        changeStatus();
    }

    /**
     * 添加menuItem的点击动画
     * @param pos
     */
    private void menuItemAnim(int pos) {

        for (int i=0;i<getChildCount()-1;i++){
            View childView = getChildAt(i+1);
            if (i==pos)
            {
                childView.startAnimation(scaleBigAnim(300));
            }else{
                childView.startAnimation(scaleSmallAnim(300));
            }
            childView.setClickable(false);
            childView.setFocusable(false);

        }
    }

    /**
     *
     * @param duration
     * @return
     */
    private Animation scaleSmallAnim(int duration) {
        AnimationSet animationSet = new AnimationSet(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f,
                0.0f,1.0f,0.0f,
                Animation.RELATIVE_TO_SELF,0.5f,
                Animation.RELATIVE_TO_SELF,0.5f);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1f,0.0f);
        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(alphaAnimation);

        animationSet.setDuration(duration);
        animationSet.setFillAfter(true);
        return animationSet;

    }

    private Animation scaleBigAnim(int duration) {
        AnimationSet animationSet = new AnimationSet(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f,
                4.0f,1.0f,4.0f,
                Animation.RELATIVE_TO_SELF,0.5f,
                Animation.RELATIVE_TO_SELF,0.5f);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1f,0.0f);
        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(alphaAnimation);

        animationSet.setDuration(duration);
        animationSet.setFillAfter(true);
        return animationSet;
    }

    /**
     * 切换菜单状态
     */
    private void changeStatus() {
       mCurrentStatus = (mCurrentStatus==Status.CLOSE?Status.OPEN:Status.CLOSE);

    }

    /**
     * 旋转view
     * @param view
     * @param start
     * @param end
     * @param duration
     */
    private void rotateCenterButton(View view, float start, float end, int duration) {
        RotateAnimation anim = new RotateAnimation(start,end,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(duration);
        anim.setFillAfter(true);
        view.startAnimation(anim);
    }
}
