package com.daivansh.androidmanagementmdmjava.jobs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.daivansh.androidmanagementmdmjava.utils.MyManagementAgent;
import com.google.api.services.androidmanagement.v1.model.Empty;

import org.greenrobot.eventbus.EventBus;

import java.util.List;


public class DeleteDeviceJob extends Job {
    private MyManagementAgent myManagementAgent;
    private String mDeviceName;
    private List<String> mWipeDataFlags;

    public DeleteDeviceJob(MyManagementAgent myManagementAgent, String deviceName, List<String> wipeDataFlags) {
        super(new Params(1));
        this.myManagementAgent=myManagementAgent;
        this.mDeviceName=deviceName;
        this.mWipeDataFlags=wipeDataFlags;
    }

    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {
        Empty empty=myManagementAgent.deleteDevice(mDeviceName,mWipeDataFlags);
       empty.size();
        EventBus.getDefault().post( empty.keySet().size()>0 ? false : true );
    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {

    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        return null;
    }
}
