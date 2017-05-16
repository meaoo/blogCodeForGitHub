package com.sd.meaoo.myapplication;

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
