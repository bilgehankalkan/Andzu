package com.canakkoca.andzu.base;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.canakkoca.andzu.R;
import com.canakkoca.andzu.activities.MainAndzuActivity;
import com.canakkoca.andzu.bubbles.BubbleLayout;
import com.canakkoca.andzu.bubbles.BubblesManager;
import com.canakkoca.andzu.bubbles.OnInitializedCallback;
import com.canakkoca.andzu.utils.Logger;

/**
 * Created by can.akkoca on 4/12/2017.
 */

public class AndzuApp {

    private DaoSession daoSession;

    private static AndzuApp andzuApp;

    private static BubblesManager bubblesManager;
    private static BubbleLayout bubbleView;

    private static boolean isAndzuEnabled;

    private static boolean isAndzuActivated;

    private Application application;

    public static AndzuApp init(Application application, DaoSession daoSession) {
        return new AndzuApp(application, daoSession);
    }

    private AndzuApp(Application application, DaoSession daoSession) {
        andzuApp = this;
        this.application = application;
        this.daoSession = daoSession;

        Logger.init(this);

        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityStarted(Activity activity) {
                ActiveActivitiesTracker.activityStarted();
            }

            @Override
            public void onActivityResumed(Activity activity) {
                if (activity instanceof BaseActivity) {
                    isAndzuActivated = true;
                    disableAndzu();
                }
            }

            @Override
            public void onActivityPaused(Activity activity) {
                if (activity instanceof BaseActivity) {
                    isAndzuActivated = false;
                    enableAndzu();
                }
            }

            @Override
            public void onActivityStopped(Activity activity) {
                ActiveActivitiesTracker.activityStopped();
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }

    public static void onTerminate() {
        try {
            if (isAndzuEnabled) {
                bubblesManager.recycle();
            }
        } catch (Exception ignored) { }
    }

    public void initAndzu(){
        if(!isAndzuEnabled) {
            bubblesManager = new BubblesManager.Builder(application.getApplicationContext())
                    .setInitializationCallback(new OnInitializedCallback() {
                        @Override
                        public void onInitialized() {
                            isAndzuEnabled = true;
                            addNewBubble();
                        }
                    })
                    .build();
            bubblesManager.initialize();
        }
    }

    private void addNewBubble() {
        bubbleView = (BubbleLayout) LayoutInflater.from(application).inflate(R.layout.bubble_layout, null);
        bubbleView.setOnBubbleClickListener(new BubbleLayout.OnBubbleClickListener() {

            @Override
            public void onBubbleClick(BubbleLayout bubble) {
                if(!isAndzuActivated) {
                    isAndzuActivated = true;
                    Intent i = new Intent(application.getApplicationContext(), MainAndzuActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    application.startActivity(i);
                }
            }
        });
        bubbleView.setShouldStickToWall(false);
        bubblesManager.addBubble(bubbleView, 60, 20);
    }

    public DaoSession getDaoSession() {
       return daoSession;
    }

    public void enableAndzu() {
        if(andzuApp == null)
            throw new AndzuNotInitializedException("You must call first to use initAndzu method");
        if(isAndzuEnabled && bubbleView != null){
            isAndzuActivated = false;
            bubbleView.setVisibility(View.VISIBLE);
        }
    }

    public void disableAndzu() {
        if(andzuApp == null)
            throw new AndzuNotInitializedException("You must call first to use initAndzu method");
        if(isAndzuEnabled && bubbleView != null){
            isAndzuActivated = true;
            bubbleView.setVisibility(View.GONE);
        }
    }

    public static AndzuApp getAndzuApp() {
        return andzuApp;
    }

    public Application getApplication() {
        return application;
    }

    private static class ActiveActivitiesTracker {
        private static int sActiveActivities = 0;

        public static void activityStarted() {
            if (sActiveActivities == 0 || sActiveActivities == 1) {
                if (isAndzuEnabled) {
                    bubbleView.setVisibility(View.VISIBLE);
                }
            }
            sActiveActivities++;
        }

        public static void activityStopped() {
            sActiveActivities--;
            if (sActiveActivities == 0 ) {
                try {
                    if (isAndzuEnabled) {
                        bubbleView.setVisibility(View.GONE);
                    }
                } catch (Exception ignored) { }
            }
        }
    }
}
