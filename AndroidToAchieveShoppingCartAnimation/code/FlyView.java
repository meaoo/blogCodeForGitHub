package com.sd.meaoo.myapplication;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

/**
 * Created by meaoo on 2017/4/14.
 */

public class FlyView {
    private View fromView, toView;

    private ImageView flyView;
    private ViewGroup container;
    private Bitmap bitmap;


    //贝塞尔曲线中间过程的点的坐标
    final float[] mCurrentPosition = new float[2];
    //计算中间动画的插值坐标
    Path mPath = new Path();
    PathMeasure mPathMeasure = new PathMeasure();
    int actionBarHeight = 0;

    public FlyView(AppCompatActivity context, View from, View to, int flyImageResourceId) {
        fromView = from;
        toView = to;
        container = (ViewGroup) ((ViewGroup) context.findViewById(android.R.id.content)).getChildAt(0);
        flyView = new ImageView(context);
        flyView.setLayoutParams(new LayoutParams(100, 100));
        // flyView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        flyView.setImageResource(flyImageResourceId);

        //由于imageView无法获取宽度和高度，这里使用bitmap获取
        bitmap = BitmapFactory.decodeResource(flyView.getResources(), flyImageResourceId);

        ActionBar bar = context.getSupportActionBar();
        if (bar != null) {
            actionBarHeight = bar.getHeight();
        }
    }


    /**
     * 启动飞行
     *
     * @param duration
     * @param runnable
     */
    public void startFly(long duration, @Nullable final Runnable runnable) {
        //添加移动飞行物体到页面
        container.addView(flyView);
        //起始点坐标
        //计算起始点中心点
        final float startX = CommonUtil.getContentX(fromView) + fromView.getWidth() / 2;
        final float startY = CommonUtil.getContentY(fromView) + fromView.getHeight() / 2;

        //设置移动点起始坐标
        if (bitmap != null) {
            CommonUtil.setContentX(flyView, startX - bitmap.getWidth() / 2);
            CommonUtil.setContentY(flyView, startY - bitmap.getHeight() / 2);
        } else {
            CommonUtil.setContentX(flyView, startX);
            CommonUtil.setContentY(flyView, startY);
        }

        final float flyX = CommonUtil.getContentX(flyView);
        final float flyY = CommonUtil.getContentY(flyView);

        //结束点坐标
        //结束点坐标的Y轴不进行居中计算原因是为了实现当物体移动到结束点的时候，即刻消失的效果，
        //而不是当物体移动到结束点中心再小时
        final float toX = CommonUtil.getContentX(toView) + toView.getWidth() / 2;
        final float toY = CommonUtil.getContentY(toView);


        //贝塞尔曲线起始点
        mPath.moveTo(flyX, flyY);
        //使用二次贝塞尔曲线
        //第一个参数，值越大，横向移动距离越大
        mPath.quadTo((startX + toX) / 2, startY, toX, toY);

        //计算贝塞尔曲线的曲线长度和贝塞尔曲线中间插值的坐标
        //第二个参数如果是true,Path将会形成一个闭环
        mPathMeasure.setPath(mPath, false);

        //从0到贝塞尔曲线的长度之间进行插值计算，获取中间过程的距离值
        final ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, mPathMeasure.getLength());
        //动画执行时长，毫秒单位
        valueAnimator.setDuration(duration);

        //匀速插值器
        LinearInterpolator linearInterpolator = new LinearInterpolator();
        valueAnimator.setInterpolator(linearInterpolator);

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //当插值计算进行时，获取中间的每个值
                //这里这个值是中间过程中的曲线长度（下面根据这个值来得出中间点的坐标值）
                float value = (Float) valueAnimator.getAnimatedValue();
                // 获取当前点坐标封装到mCurrentPosition
                //传入一个距离distance(0<=distance<=getLength())，然后会计算当前距离的坐标点和切线，pos会自动填充上坐标，这个方法很重要
                mPathMeasure.getPosTan(value, mCurrentPosition, null);
                //移动（动画图片）的坐标设置为该中间点的坐标
                flyView.setTranslationX(mCurrentPosition[0] - flyView.getWidth() / 2);
                flyView.setTranslationY(mCurrentPosition[1]);

            }
        });
        //动画监听
        valueAnimator.addListener(new Animator.AnimatorListener() {
            //动画启动
            @Override
            public void onAnimationStart(Animator animator) {
            }

            //动画结束
            @Override
            public void onAnimationEnd(Animator animator) {
                if (runnable != null) {
                    runnable.run();
                }
                container.removeView(flyView);
            }

            //动画取消
            @Override
            public void onAnimationCancel(Animator animator) {
                container.removeView(flyView);
            }

            //动画重复执行
            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });

        //执行动画
        valueAnimator.start();
    }
}