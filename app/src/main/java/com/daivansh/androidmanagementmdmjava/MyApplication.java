package com.daivansh.androidmanagementmdmjava;

import android.app.Application;

import com.daivansh.androidmanagementmdmjava.utils.MyManagementAgent;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class MyApplication extends Application {

    private MyManagementAgent myManagementAgent;

    public MyManagementAgent getMyManagementAgent() {
        return myManagementAgent;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            myManagementAgent=new MyManagementAgent(MyManagementAgent.getAndroidManagementClient(getApplicationContext()));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }
}
