package com.lejia.mobile.orderking.ui;

import android.app.Activity;

import java.util.Stack;

public class ActivityStack {
    private static Stack<Activity> mActivityStack = new Stack<>();
    private static ActivityStack instance = new ActivityStack();

    private ActivityStack() {
    }

    public static ActivityStack getScreenManager() {
        return instance;
    }

    public void popActivity(Activity activity) {
        if (activity != null) {
            activity.finish();
            mActivityStack.remove(activity);
            activity = null;
        }
    }

    //
    public void pushActivity(Activity activity) {
        mActivityStack.add(activity);
    }

    //Activity
    public void popAllActivityExceptOne() {
        while (mActivityStack.size() > 0) {
            Activity activity = mActivityStack.pop();
            if (activity != null) {
                activity.finish();
            }
        }
    }

}