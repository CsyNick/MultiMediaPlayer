package com.htc.nick.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by nickchung on 2016/9/6.
 */
public class VideoService extends Service{


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
