package cn.wangxiao.crm.myprizedemo;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

//import com.squareup.leakcanary.LeakCanary;


public class SysApplication extends Application {

    private static final String TAG = "JPush";

    private static Context context;
    private static int mainThreadId;
    private static Thread mainThread;
    private static Handler handler;


    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        mainThreadId = android.os.Process.myTid();
        mainThread = Thread.currentThread();
        handler = new Handler();

        //LeakCanary.install(this);

    }
/*
    public static boolean isBackground() {
        if (myActivityLifecycleCallbacks != null) {
            return !myActivityLifecycleCallbacks.isForeground();
        }
        return true;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        // you must install multiDex whatever tinker is installed!
        MultiDex.install(base);
        // 安装tinker
        Beta.installTinker();
    }*/


    public static Context getContext() {
        return context;
    }

    /**
     * 获取主线程id
     */
    public static int getMainThreadId() {
        return mainThreadId;
    }

    /**
     * 获取主线程
     */
    public static Thread getMainThread() {
        return mainThread;
    }

    public static Handler getHandler() {
        return handler;
    }

    public static List<Activity> mList = new LinkedList<Activity>();
    public static List<String> mListName = new LinkedList<String>();
    private static SysApplication instance;

    public synchronized static SysApplication getInstance() {
        if (null == instance) {
            instance = new SysApplication();
        }
        return instance;
    }

    public void addActivity(Activity activity) {
        mList.add(activity);
    }


    public void addActivity(Activity activity, String activityName) {
        mList.add(activity);
        mListName.add(activityName);
    }


    public static void exit() {
        try {
            for (Activity activity : mList) {
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }

    public void close() {
        for (Activity activity : mList) { //
            Log.e("wen", "close:" + activity.getLocalClassName());
            if (activity != null) {
                activity.finish();
            }
        }
    }

    public void removeActivity(Activity activity) {
        for (Activity ac : mList) { //
            Log.e("wen", "close:" + activity.getLocalClassName());
            if (ac.equals(activity)) {
                mList.remove(ac);
                break;
            }
        }
    }


    public void removeActivityName(String mainActivity) {
        mListName.remove(mainActivity);
    }

    public boolean isContainActivityName(String name) {
        return mListName.contains(name);
    }

    public static boolean isRunningForeground() {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcessInfos = activityManager.getRunningAppProcesses();
        // 枚举进程
        for (ActivityManager.RunningAppProcessInfo appProcessInfo : appProcessInfos) {
            if (appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                if (appProcessInfo.processName.equals(context.getApplicationInfo().processName)) {
                    return true;
                }
            }
        }
        return false;
    }

}