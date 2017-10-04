package com.huangbryant.weather.utils;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.annotation.NonNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * 作者：  黄双
 * 事件 2017/9/4 0004.
 * 邮箱： 15378412400@163.com
 */

public class ActivityManager {
    private ActivityManager() {

    }

    private static Stack<Activity> sActivityStack = new Stack<>();

    private static ActivityManager sInstance = new ActivityManager();

    // 采用弱引用持有 Activity ，避免造成 内存泄露
    private WeakReference<Activity> sCurrentActivityWeakRef;

    public static ActivityManager getInstance() {
        return sInstance;
    }

    public Activity getCurrentActivity() {
        Activity currentActivity = null;
        if (sCurrentActivityWeakRef != null) {
            currentActivity = sCurrentActivityWeakRef.get();
        }
        return currentActivity;
    }

    public void setCurrentActivity(Activity activity) {
        sCurrentActivityWeakRef = new WeakReference<Activity>(activity);
    }

    /**
     * 添加Activity到堆栈
     */
    public synchronized static void addActivity(Activity activity) {
        synchronized (sActivityStack) {
            sActivityStack.add(activity);
        }
    }

    public synchronized static void removeActivity(Activity activity) {
        if (activity != null) {
            synchronized (sActivityStack) {
                sActivityStack.remove(activity);
            }
        }
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public synchronized static Activity currentActivity() {
        synchronized (sActivityStack) {
            return sActivityStack.isEmpty() ? null : sActivityStack.lastElement();
        }
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public synchronized static void finishActivity() {
        Activity activity;

        synchronized (sActivityStack) {
            activity = sActivityStack.lastElement();
        }

        if (activity != null) {
            activity.finish();
            activity = null;
        }
    }

    /**
     * 结束指定的Activity
     */
    public synchronized static void finishActivity(Activity activity) {
        if (activity != null) {
            synchronized (sActivityStack) {
                sActivityStack.remove(activity);
            }

            activity.finish();
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public synchronized static void finishActivity(Class<?> cls) {
        List<Activity> activities = new ArrayList<>();

        synchronized (sActivityStack) {
            for (Activity activity : sActivityStack) {
                if (activity.getClass().equals(cls)) {
                    activities.add(activity);
                }
            }
        }

        for (Activity activity : activities) {
            finishActivity(activity);
        }

        activities.clear();
    }

    /**
     * 结束所有Activity
     */
    public synchronized static void finishAllActivity() {
        synchronized (sActivityStack) {
            for (Activity mActivity : sActivityStack) {
                if (mActivity != null) {
                    mActivity.finish();
                }
            }

            sActivityStack.clear();
        }
    }

    /**
     * 结束除指定Activity外的其他所有Activity
     *
     * @param activity 不需要结束的Activity
     */
    public synchronized static void finishAllActivityExcept(Activity activity) {
        synchronized (sActivityStack) {
            for (Activity mActivity : sActivityStack) {
                if (mActivity != activity && mActivity != null) {
                    mActivity.finish();
                }
            }

            sActivityStack.clear();

            if (activity != null) {
                sActivityStack.add(activity);
            }
        }
    }

    /**
     * 结束除指定Activity外的其他所有Activity
     *
     * @param clazz 不需要结束的Activity
     */
    public synchronized static void finishAllActivityExcept(Class<?> clazz) {
        synchronized (sActivityStack) {
            Activity exceptActivity = null;

            for (Activity activity : sActivityStack) {
                if (activity != null) {
                    if (activity.getClass() == clazz) {
                        exceptActivity = activity;
                    } else {
                        activity.finish();
                    }
                }
            }

            sActivityStack.clear();

            if (exceptActivity != null) {
                sActivityStack.add(exceptActivity);
            }
        }
    }

    /**
     * 退出应用程序
     */
    public static void appExit() {
        finishAllActivity();
    }

    public static void addFragmentToActivity(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment, int containerId, String stackName) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(containerId, fragment);

        if (stackName != null) {
            transaction.addToBackStack(stackName);
        }

        transaction.commit();
    }

    public static void addFragmentToActivity(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment, int containerId) {
        addFragmentToActivity(fragmentManager, fragment, containerId, null);
    }
}
