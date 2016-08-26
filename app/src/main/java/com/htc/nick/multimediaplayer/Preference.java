package com.htc.nick.multimediaplayer;

import org.androidannotations.annotations.sharedpreferences.DefaultInt;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

/**
 * Created by nickchung on 2016/8/26.
 */
@SharedPref(value = SharedPref.Scope.UNIQUE)
public interface Preference {


    @DefaultInt(0)
    int tabTargetPosition();
}
