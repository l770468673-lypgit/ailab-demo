package com.openailab.ailab;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.openailab.ailab.utils.CrashHandler;


/**
 * @author ZyElite
 */
public class AiLabApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
        DaoManager.getInstance(this);
    }

}
