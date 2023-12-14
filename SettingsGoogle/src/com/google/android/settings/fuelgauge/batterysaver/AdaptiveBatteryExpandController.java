/*
 * Copyright (C) 2025 the RisingOS Revived Android Project
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
package com.google.android.settings.fuelgauge.batterysaver;

import android.content.Context;
import androidx.preference.PreferenceScreen;
import androidx.preference.PreferenceGroup;
import com.android.settings.core.BasePreferenceController;

public class AdaptiveBatteryExpandController extends BasePreferenceController {
    
    public AdaptiveBatteryExpandController(Context context, String key) {
        super(context, key);
    }
    
    @Override
    public int getAvailabilityStatus() {
        return UNSUPPORTED_ON_DEVICE; // Return a status indicating this isn't supported
    }
    
    // Fix the verification error by implementing a correct displayPreference method
    // that properly handles the preference type
    @Override
    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);
        // Do nothing - this is a dummy class to satisfy verification
    }
}
