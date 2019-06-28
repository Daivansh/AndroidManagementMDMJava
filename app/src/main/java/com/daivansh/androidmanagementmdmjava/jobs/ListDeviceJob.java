package com.daivansh.androidmanagementmdmjava.jobs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.daivansh.androidmanagementmdmjava.utils.MyManagementAgent;
import com.google.api.services.androidmanagement.v1.model.Device;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;


public class ListDeviceJob extends Job {
    private MyManagementAgent myManagementAgent;
    private List<Device> mDeviceList;
    public ListDeviceJob(MyManagementAgent myManagementAgent) {
        super(new Params(1));
        this.myManagementAgent=myManagementAgent;
    }

    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {
        mDeviceList=myManagementAgent.listDevices();
        EventBus.getDefault().post(mDeviceList!=null ? mDeviceList : new ArrayList<Device>());
    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {

    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        return null;
    }
}
