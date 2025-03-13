/*
 * Copyright (C) 2022 The PixelExperience Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.systemui.reversecharging;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;

import com.android.systemui.dagger.qualifiers.Main;
import com.android.settingslib.Utils;
import com.google.android.systemui.res.R;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.dagger.SysUISingleton;
import com.android.systemui.statusbar.phone.CentralSurfaces;
import com.android.systemui.statusbar.phone.ui.StatusBarIconController;
import com.android.systemui.statusbar.policy.BatteryController;
import com.google.android.systemui.statusbar.KeyguardIndicationControllerGoogle;

import java.util.concurrent.Executor;

import javax.inject.Inject;

import dagger.Lazy;

@SysUISingleton
public class ReverseChargingViewController extends BroadcastReceiver implements LifecycleOwner, BatteryController.BatteryStateChangeCallback {
    private static final boolean DEBUG = Log.isLoggable("ReverseChargingViewCtrl", 3);
    private final BatteryController mBatteryController;
    private final BroadcastDispatcher mBroadcastDispatcher;
    private final Context mContext;
    private final KeyguardIndicationControllerGoogle mKeyguardIndicationController;
    private final LifecycleRegistry mLifecycle = new LifecycleRegistry(this);
    private final @Main Executor mMainExecutor;
    private final StatusBarIconController mStatusBarIconController;
    private final Lazy<CentralSurfaces> mCentralSurfacesLazy;
    private String mContentDescription;
    private int mLevel;
    private String mName;
    private boolean mProvidingBattery;
    private boolean mReverse;
    private String mReverseCharging;
    private String mSlotReverseCharging;

    @Inject
    public ReverseChargingViewController(
            Context context,
            BatteryController batteryController,
            Lazy<CentralSurfaces> centralSurfacesLazy,
            StatusBarIconController statusBarIconController,
            BroadcastDispatcher broadcastDispatcher,
            @Main Executor mainExecutor,
            KeyguardIndicationControllerGoogle keyguardIndicationControllerGoogle) {
        mBatteryController = batteryController;
        mStatusBarIconController = statusBarIconController;
        mCentralSurfacesLazy = centralSurfacesLazy;
        mContext = context;
        mBroadcastDispatcher = broadcastDispatcher;
        mMainExecutor = mainExecutor;
        mKeyguardIndicationController = keyguardIndicationControllerGoogle;
        loadStrings();
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return mLifecycle;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.LOCALE_CHANGED")) {
            loadStrings();
            if (DEBUG) {
                Log.d("ReverseChargingViewCtrl", "onReceive(): ACTION_LOCALE_CHANGED this=" + this);
            }
            postOnMainThreadToUpdate();
        }
    }

    @Override
    public void onBatteryLevelChanged(int i, boolean z, boolean z2) {
        mReverse = mBatteryController.isReverseOn();
        if (DEBUG) {
            Log.d("ReverseChargingViewCtrl", "onBatteryLevelChanged(): rtx=" + (mReverse ? 1 : 0) + " level=" + mLevel + " name=" + mName + " this=" + this);
        }
        postOnMainThreadToUpdate();
    }

    @Override
    public void onReverseChanged(boolean z, int i, String str) {
        mReverse = z;
        mLevel = i;
        mName = str;
        mProvidingBattery = z && i >= 0;
        if (DEBUG) {
            Log.d("ReverseChargingViewCtrl", "onReverseChanged(): rtx=" + (z ? 1 : 0) + " level=" + i + " name=" + str + " this=" + this);
        }
        postOnMainThreadToUpdate();
    }

    public void initialize() {
        mBatteryController.observe(mLifecycle, this);
        mLifecycle.markState(Lifecycle.State.RESUMED);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.LOCALE_CHANGED");
        mBroadcastDispatcher.registerReceiver(this, intentFilter);
    }

    private void postOnMainThreadToUpdate() {
        mMainExecutor.execute(() -> {
            updateReverseChargingIcon();
        });
    }

    private void updateReverseChargingIcon() {
        mStatusBarIconController.setIcon(mSlotReverseCharging, R.drawable.ic_qs_reverse_charging, mContentDescription);
        mStatusBarIconController.setIconVisibility(mSlotReverseCharging, mProvidingBattery);
    }

    private void loadStrings() {
        mReverseCharging = mContext.getString(R.string.charging_reverse_text);
        mSlotReverseCharging = mContext.getString(R.string.status_bar_google_reverse_charging);
        mContentDescription = mContext.getString(R.string.reverse_charging_on_notification_title);
    }
}
