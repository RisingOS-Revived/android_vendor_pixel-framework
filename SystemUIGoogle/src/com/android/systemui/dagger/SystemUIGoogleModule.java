/*
 * Copyright (C) 2022 The Android Open Source Project
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

package com.android.systemui.dagger;

import android.app.AlarmManager;
import android.content.Context;
import android.os.Handler;

import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.dagger.qualifiers.Application;
import com.android.systemui.dagger.qualifiers.Background;
import com.android.systemui.dagger.qualifiers.Main;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.log.LogBufferFactory;
import com.android.systemui.media.NotificationMediaManager;
import com.android.systemui.plugins.BcSmartspaceDataPlugin;
import com.android.systemui.settings.UserTracker;
import com.android.systemui.smartspace.dagger.SmartspaceModule;
import com.android.systemui.statusbar.policy.ZenModeController;
import com.android.systemui.statusbar.policy.domain.interactor.ZenModeInteractor;
import com.android.systemui.util.concurrency.DelayableExecutor;

import com.google.android.systemui.smartspace.AlarmAppSearchController;
import com.google.android.systemui.smartspace.BcSmartspaceDataProvider;
import com.google.android.systemui.smartspace.DateSmartspaceDataProvider;
import com.google.android.systemui.smartspace.KeyguardMediaViewController;
import com.google.android.systemui.smartspace.KeyguardZenAlarmViewController;
import com.google.android.systemui.smartspace.NextClockAlarmController;
import com.google.android.systemui.smartspace.WeatherSmartspaceDataProvider;
import com.google.android.systemui.smartspace.log.NextClockAlarmControllerLogger;

import dagger.BindsOptionalOf;
import dagger.Module;
import dagger.Provides;

import kotlinx.coroutines.CoroutineDispatcher;
import kotlinx.coroutines.CoroutineScope;

import java.util.concurrent.Executor;

import javax.inject.Named;

/**
 * Dagger module for providing Google-specific SystemUI components.
 */
@Module
public abstract class SystemUIGoogleModule {

    @BindsOptionalOf
    @Named(SmartspaceModule.GLANCEABLE_HUB_SMARTSPACE_DATA_PLUGIN)
    abstract BcSmartspaceDataPlugin optionalGlanceableHubSmartspaceDataPlugin();

    /**
     * Provides the KeyguardZenAlarmViewController for displaying DND and alarm info.
     */
    @SysUISingleton
    @Provides
    static KeyguardZenAlarmViewController provideKeyguardZenAlarmViewController(
            Context context,
            @Named(SmartspaceModule.DATE_SMARTSPACE_DATA_PLUGIN) BcSmartspaceDataPlugin datePlugin,
            ZenModeController zenModeController,
            ZenModeInteractor zenModeInteractor,
            AlarmManager alarmManager,
            NextClockAlarmController nextClockAlarmController,
            @Main Handler handler,
            @Background CoroutineScope applicationScope) {
        return new KeyguardZenAlarmViewController(
                context,
                datePlugin,
                zenModeController,
                zenModeInteractor,
                alarmManager,
                nextClockAlarmController,
                handler,
                applicationScope);
    }

    /**
     * Provides the KeyguardMediaViewController for displaying media info on lockscreen.
     */
    @SysUISingleton
    @Provides
    static KeyguardMediaViewController provideKeyguardMediaViewController(
            Context context,
            UserTracker userTracker,
            @Named(SmartspaceModule.GLANCEABLE_HUB_SMARTSPACE_DATA_PLUGIN)
                    BcSmartspaceDataPlugin plugin,
            @Main DelayableExecutor uiExecutor,
            NotificationMediaManager mediaManager) {
        return new KeyguardMediaViewController(
                context, userTracker, plugin, uiExecutor, mediaManager);
    }

    /**
     * Provides the base BcSmartspaceDataPlugin.
     */
    @SysUISingleton
    @Provides
    static BcSmartspaceDataPlugin provideBcSmartspaceDataPlugin() {
        return new BcSmartspaceDataProvider();
    }

    /**
     * Provides the date smartspace data plugin.
     */
    @SysUISingleton
    @Provides
    @Named(SmartspaceModule.DATE_SMARTSPACE_DATA_PLUGIN)
    static BcSmartspaceDataPlugin provideDateSmartspaceDataPlugin() {
        return new DateSmartspaceDataProvider();
    }

    /**
     * Provides the glanceable hub smartspace data plugin.
     */
    @SysUISingleton
    @Provides
    @Named(SmartspaceModule.GLANCEABLE_HUB_SMARTSPACE_DATA_PLUGIN)
    static BcSmartspaceDataPlugin provideGlanceableHubSmartspaceDataPlugin() {
        return new BcSmartspaceDataProvider();
    }

    /**
     * Provides the weather smartspace data plugin.
     */
    @SysUISingleton
    @Provides
    @Named(SmartspaceModule.WEATHER_SMARTSPACE_DATA_PLUGIN)
    static BcSmartspaceDataPlugin provideWeatherSmartspaceDataPlugin() {
        return new WeatherSmartspaceDataProvider();
    }

    /**
     * Provides the DateSmartspaceDataProvider.
     */
    @SysUISingleton
    @Provides
    static DateSmartspaceDataProvider provideDateSmartspaceDataProvider() {
        return new DateSmartspaceDataProvider();
    }

    /**
     * Provides the WeatherSmartspaceDataProvider.
     */
    @SysUISingleton
    @Provides
    static WeatherSmartspaceDataProvider provideWeatherSmartspaceDataProvider() {
        return new WeatherSmartspaceDataProvider();
    }

    /**
     * Provides the AlarmAppSearchController for searching alarm apps.
     */
    @SysUISingleton
    @Provides
    static AlarmAppSearchController provideAlarmAppSearchController(
            @Main Executor mainExecutor, @Background CoroutineDispatcher bgDispatcher) {
        return new AlarmAppSearchController(mainExecutor, bgDispatcher);
    }

    /**
     * Provides the NextClockAlarmController for managing next alarm information.
     */
    @SysUISingleton
    @Provides
    static NextClockAlarmController provideNextClockAlarmController(
            UserTracker userTracker,
            BroadcastDispatcher broadcastDispatcher,
            DumpManager dumpManager,
            AlarmAppSearchController alarmAppSearchController,
            @Main Executor mainExecutor,
            @Application CoroutineScope applicationScope,
            @Background CoroutineScope backgroundScope) {
        return new NextClockAlarmController(
                userTracker,
                broadcastDispatcher,
                dumpManager,
                alarmAppSearchController,
                mainExecutor,
                applicationScope,
                backgroundScope);
    }

    /**
     * Provides the NextClockAlarmControllerLogger for logging.
     */
    @SysUISingleton
    @Provides
    static NextClockAlarmControllerLogger provideNextClockAlarmControllerLogger(
            LogBufferFactory factory) {
        return new NextClockAlarmControllerLogger(
                factory.create("NextClockAlarmControllerLog", 100));
    }
}
