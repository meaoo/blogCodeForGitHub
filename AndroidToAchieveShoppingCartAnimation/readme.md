> 需求：点击触发事件，使一个圆点或者其他的什么东西，移动到另外一个元素上。由于是使用坐标进行移动，所以，会出现起始点位置，中途的路径，落点位置的点不是中心点，而是做小X轴和Y轴焦点。当然这个案例会考虑这个问题，并解决这个问题。

### 示例图

![Android实现购物车动画示例图](http://upload-images.jianshu.io/upload_images/4678351-61cf22540e5c09e4.gif?imageMogr2/auto-orient/strip)

### `activity_main.xml`代码
```
<?xml version="1.0" encoding="utf-8"?>
<android.support.constraint.ConstraintLayout
        xmlns:android="http://schemas.android.com/apk/res/android"
        xmlns:app="http://schemas.android.com/apk/res-auto"
        xmlns:tools="http://schemas.android.com/tools"
        android:layout_width="match_parent"
        android:layout_height="match_parent" android:id="@+id/activity_main">


    <Button android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="测试" android:id="@+id/btn"
            app:layout_constraintTop_toTopOf="parent"
            android:layout_marginTop="8dp"
            android:layout_marginRight="8dp"
            app:layout_constraintRight_toRightOf="parent"/>
    <Button android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="盒子"
            android:id="@+id/box"
            app:layout_constraintBottom_toBottomOf="parent"
            android:layout_marginBottom="8dp"
            android:layout_marginLeft="8dp"
            app:layout_constraintLeft_toLeftOf="parent"/>
</android.support.constraint.ConstraintLayout>
```
### `MainActivity`代码
> 这个方法就没啥说的了.初始化调用,以及动画效果在这里定义.

```
package cn.meaoo.myapplication;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
/**
 * 物体抛物线移动
 * Created by meaoo on 2017/4/14.
 */
public class MainActivity extends AppCompatActivity {
    private View startView, toView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_dot);
        //起始点元素
        startView = findViewById(R.id.btn);
        //结束点元素
        toView = findViewById(R.id.box);

        startView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickK();
            }
        });
    }

    private void clickK() {
        FlyView fly = new FlyView(this, startView, toView, R.drawable.ic_action_name);
        fly.startFly(3000, new Runnable() {
            @Override
            public void run() {
                //结束点元素动画变化
                toView.animate().scaleX(1.5f);
                toView.animate().scaleY(1.3f);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        toView.animate().scaleX(1f);
                        toView.animate().scaleY(1f);
                    }
                }, 200);
            }
        });
    }
}
```


### `FlyView`代码
> 使物体进行点与点之间移动方法,或者叫两点之间飞行,并使用贝塞尔曲线,进行一个比较漂亮的飞行姿态.并处理动画效果

```
package cn.meaoo.myapplication;

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
```

### `CommonUtil`代码
> 由于系统提供方法无法准确获取元素准确坐标.所以使用该方法辅助`FlyView`计算开始点和结束点以及移动物体在屏幕中的真实坐标

```
package cn.meaoo.myapplication;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by meaoo on 2017/4/14.
 */

public class CommonUtil {

    /**
     * 获取view在全局中的X轴坐标点
     *
     * @param view
     * @return
     */
    public static float getContentX(View view) {
        int x = 0;
        Activity context = (Activity) view.getContext();
        View content = ((ViewGroup) context.findViewById(android.R.id.content)).getChildAt(0);
        x += view.getX();
        ViewGroup parent = (ViewGroup) view.getParent();

        while (parent != content) {
            x += parent.getX();
            parent = (ViewGroup) parent.getParent();
        }
        return x;
    }

    /**
     * 获取view在全局中的Y轴坐标点
     *
     * @param view
     * @return
     */
    public static float getContentY(View view) {
        int y = 0;
        Activity context = (Activity) view.getContext();
        View content = ((ViewGroup) context.findViewById(android.R.id.content)).getChildAt(0);
        y += view.getY();
        ViewGroup parent = (ViewGroup) view.getParent();

        while (parent != content) {
            y += parent.getY();
            parent = (ViewGroup) parent.getParent();
        }
        return y;
    }

    /**
     * 设置view在全局中的X轴坐标点
     *
     * @param view
     * @param x
     */
    public static void setContentX(View view, float x) {
        Activity context = (Activity) view.getContext();
        View content = ((ViewGroup) context.findViewById(android.R.id.content)).getChildAt(0);
        ViewGroup parent = (ViewGroup) view.getParent();
        while (parent != content) {
            x -= parent.getX();
            parent = (ViewGroup) parent.getParent();
        }
        view.setX(x);
    }

    /**
     * 设置view在全局中的Y轴坐标点
     *
     * @param view
     * @param y
     */
    public static void setContentY(View view, float y) {
        Activity context = (Activity) view.getContext();
        View content = ((ViewGroup) context.findViewById(android.R.id.content)).getChildAt(0);
        ViewGroup parent = (ViewGroup) view.getParent();
        while (parent != content) {
            y -= parent.getY();
            parent = (ViewGroup) parent.getParent();
        }
        view.setY(y);
    }

}

```

### 结尾
如果对上面的操作有任何疑问或者问题，无法得到解决，请与我联系
```
//发送邮件或者添加QQ，请说明什么问题，以及文章链接，这样方便对您的问题进行更详尽的回答
E-mail：m@meaoo.cn
QQ : 774540069
```