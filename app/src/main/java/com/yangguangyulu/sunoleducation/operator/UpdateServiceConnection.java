package com.yangguangyulu.sunoleducation.operator;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

/**
 * Description:
 * Modified By:
 */


public class UpdateServiceConnection implements ServiceConnection {
    @Override
    public void onServiceDisconnected(ComponentName name) {
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        UpdateService.UpdateBinder binder = (UpdateService.UpdateBinder) service;
        binder.start();
    }
}
