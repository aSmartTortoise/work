package com.voyah.aiwindow.common;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.voyah.aiwindow.aidlbean.AIMessage;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class PluginHelper {
    private static final String TAG = PluginHelper.class.getSimpleName();

    public static View loadPlugin(Context context, AIMessage bean) {
        Log.i(TAG, "loadPlugin begin: " + bean.toString());
        long beginTime = System.currentTimeMillis();
        View v = null;
        try {
            Context pluginContext = context.createPackageContext(bean.pkgName,
                    Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);
            Log.d(TAG, "pluginContext: " + pluginContext);

            // 如果context是activity则将宿主的activity传给插件view
            if (context instanceof Activity) {
                setActivity(pluginContext, (Activity) context);
            }

            v = reflectView(pluginContext, bean.clazz);
            Log.d(TAG, "reflectView view:" + v);
        } catch (Exception e) {
            Log.d(TAG, "Error:" + e.getLocalizedMessage());
            e.printStackTrace();
        }
        Log.i(TAG, "loadPlugin end: " + bean.pkgName + ",耗时:" + (System.currentTimeMillis() - beginTime));

        if (v != null) {
            v.setTag(bean.data);
            return v;
        }
        Toast.makeText(context, "加载插件失败", Toast.LENGTH_SHORT).show();
        return null;
    }

    private static View reflectView(Context pluginContext, String viewClazz) {
        try {
            ClassLoader pluginClassLoader = pluginContext.getClassLoader();
            Class<?> customViewClass = pluginClassLoader.loadClass(viewClazz);
            Constructor<?> constructor = customViewClass.getConstructor(Context.class);
            return (View) constructor.newInstance(pluginContext);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 反射将Activity加入到context的mOuterContext中。
     *
     * @param context  这是一个ContextImpl对象，待注入Activity
     * @param activity 这是待注入的Activity。
     */
    private static void setActivity(Context context, Activity activity) {
        try {
            Class contextImplClass = Class.forName("android.app.ContextImpl");
            Field outerContextField = contextImplClass.getDeclaredField("mOuterContext");
            outerContextField.setAccessible(true);
            outerContextField.set(context, activity);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
