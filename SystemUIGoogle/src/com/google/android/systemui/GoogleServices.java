package com.google.android.systemui;

import android.app.AlarmManager;
import android.content.Context;
import android.os.Handler;

import com.android.systemui.res.R;
import com.android.systemui.Dumpable;
import com.android.systemui.VendorServices;
import com.android.systemui.dagger.SysUISingleton;
import com.android.systemui.dagger.qualifiers.Background;
import com.android.systemui.dagger.qualifiers.Main;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.power.domain.interactor.PowerInteractor;
import com.android.systemui.shade.NotificationShadeWindowView;
import com.android.systemui.shade.ShadeViewController;
import com.android.systemui.user.domain.interactor.SelectedUserInteractor;
import com.android.systemui.util.wakelock.WakeLockLogger;

import com.google.android.systemui.ambientmusic.AmbientIndicationContainer;
import com.google.android.systemui.ambientmusic.AmbientIndicationService;
import com.google.android.systemui.input.TouchContextService;
import com.google.android.systemui.columbus.ColumbusContext;
import com.google.android.systemui.columbus.ColumbusServiceWrapper;

import java.io.PrintWriter;
import java.util.ArrayList;

import javax.inject.Inject;

@SysUISingleton
public class GoogleServices extends VendorServices {

    private final Context mContext;
    private final ArrayList<Object> mServices;
    private final ActivityStarter mActivityStarter;
    private final AlarmManager mAlarmManager;
    private final Lazy<ColumbusServiceWrapper> mColumbusServiceLazy;
    private final Handler mBgHandler;
    private final NotificationShadeWindowView mNotificationShadeWindowView;
    private final PowerInteractor mPowerInteractor;
    private final SelectedUserInteractor mSelectedUserInteractor;
    private final ShadeViewController mShadeViewController;
    private final WakeLockLogger mWakelockLogger;

    @Inject
    public GoogleServices(
            Context context,
            ActivityStarter activityStarter,
            AlarmManager alarmManager,
            Lazy<ColumbusServiceWrapper> columbusServiceWrapperLazy,
            NotificationShadeWindowView notificationShadeWindowView,
            PowerInteractor powerInteractor,
            SelectedUserInteractor selectedUserInteractor,
            ShadeViewController shadeViewController,
            WakeLockLogger wakeLockLogger,
            @Background Handler bgHandler) {
        super();
        mContext = context;
        mActivityStarter = activityStarter;
        mServices = new ArrayList<>();
        mAlarmManager = alarmManager;
        mColumbusServiceLazy = columbusServiceWrapperLazy;
        mNotificationShadeWindowView = notificationShadeWindowView;
        mPowerInteractor = powerInteractor;
        mSelectedUserInteractor = selectedUserInteractor;
        mShadeViewController = shadeViewController;
        mWakelockLogger = wakeLockLogger;
        mBgHandler = bgHandler;
    }

    @Override
    public void start() {
        if (new ColumbusContext(mContext).isAvailable()) {
            addService(mColumbusServiceLazy.get());
        }
        if (mContext.getResources().getBoolean(R.bool.config_touch_context_enabled)) {
            addService(new TouchContextService(mContext));
        }
        AmbientIndicationContainer ambientIndicationContainer =
                (AmbientIndicationContainer)
                        mNotificationShadeWindowView.findViewById(
                                R.id.ambient_indication_container);
        ambientIndicationContainer.initializeView(
                mShadeViewController, mPowerInteractor, mActivityStarter, mWakelockLogger, mBgHandler);
        addService(
                new AmbientIndicationService(mContext, ambientIndicationContainer, mSelectedUserInteractor, mAlarmManager));
    }

    @Override
    public void dump(PrintWriter printWriter, String[] strArr) {
        for (int i = 0; i < mServices.size(); i++) {
            if (mServices.get(i) instanceof Dumpable) {
                ((Dumpable) mServices.get(i)).dump(printWriter, strArr);
            }
        }
    }

    private void addService(Object obj) {
        if (obj != null) {
            mServices.add(obj);
        }
    }
}
