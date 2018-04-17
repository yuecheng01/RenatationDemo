package com.yeucheng.renatationdemo.inject;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Administrator on 2018/1/30.
 */

public class ViewUtils {

    public static void inject(Activity activity) {
        inject(new ViewFinder(activity), activity);
    }

    public static void inject(View view) {
        inject(new ViewFinder(view), view);
    }

    private static void inject(ViewFinder viewFinder, Object object) {
        injectField(viewFinder, object);
        injectEvent(viewFinder, object);
    }

    private static void injectField(ViewFinder viewFinder, Object object) {
        //获取类中的所用属性
        Class<?> clazz = object.getClass();
        //获取所有属性包括共有和私有
        Field[] fields = clazz.getDeclaredFields();
        //获取findView中的value值
        for (Field field : fields
                ) {
            FindView findView = field.getAnnotation(FindView.class);
            if (findView != null) {
                //获取注解里的id值
                int value = findView.value();
                //findViewById找到view
                View view = viewFinder.findViewById(value);
                if (view != null) {
                    //能够修饰所有修饰符
                    field.setAccessible(true);
                    //动态注入找到View
                    try {
                        field.set(object, view);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private static void injectEvent(ViewFinder viewFinder, Object object) {
        //获取类中所有的方法
        Class<?> clazz = object.getClass();
        Method[] method = clazz.getDeclaredMethods();
        //获取Click里的value值
        for (Method m : method
                ) {
            Click click = m.getAnnotation(Click.class);
            if (click != null) {
                int[] viewIds = click.value();
                for (int viewId : viewIds
                        ) {
                    View view = viewFinder.findViewById(viewId);
                    //检测网络
                    CheckNet checkNet = m.getAnnotation(CheckNet.class);
                    boolean isCheckNet = checkNet != null;
                    if (view != null) {
                        view.setOnClickListener(new DeclareOnClickListener(m, object, isCheckNet));
                    }
                }
            }
        }
    }

    private static class DeclareOnClickListener implements View.OnClickListener {
        private Object mObject;
        private Method mMethod;
        private boolean mIsCheckNet;

        public DeclareOnClickListener(Method method, Object o, boolean isCheckNet) {
            super();
            this.mMethod = method;
            this.mObject = o;
            this.mIsCheckNet = isCheckNet;
        }

        @Override
        public void onClick(View view) {
            //需不需要检测网络
            if (mIsCheckNet) {
                //判断是否有网络
                if (!isNetworkConnected(view.getContext())) {
                    //打印toast
                    Toast.makeText(view.getContext(), "亲,网络异常哟,请检查网络连接!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            //所有方法都可以
            mMethod.setAccessible(true);
            try {
                mMethod.invoke(mObject, view);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 检测网络是否可用
     *
     * @return
     */
    private static boolean isNetworkConnected(Context context) {
        try {
            ConnectivityManager contextSystemService = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = contextSystemService.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
