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

import com.android.systemui.statusbar.NotificationInsetsModule;
import com.android.systemui.statusbar.QsFrameTranslateModule;

import com.google.android.systemui.smartspace.dagger.SmartspaceStartableModule;

import dagger.Subcomponent;

/**
 * Dagger Subcomponent for Google's SystemUI variant.
 */
@SysUISingleton
@Subcomponent(modules = {
        DefaultComponentBinder.class,
        DependencyProvider.class,
        NotificationInsetsModule.class,
        QsFrameTranslateModule.class,
        SystemUIModule.class,
        SystemUIGoogleModule.class,
        SystemUICoreStartableModule.class,
        ReferenceSystemUIModule.class,
        SmartspaceStartableModule.class})
public interface SysUIGoogleComponent extends SysUIComponent {

    /**
     * Builder for SysUIGoogleComponent.
     */
    @SysUISingleton
    @Subcomponent.Builder
    interface Builder extends SysUIComponent.Builder {
        SysUIGoogleComponent build();
    }
}
