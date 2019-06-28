package com.daivansh.androidmanagementmdmjava.jobs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.daivansh.androidmanagementmdmjava.utils.MyManagementAgent;
import com.daivansh.androidmanagementmdmjava.utils.StaticConstants;
import com.google.api.services.androidmanagement.v1.model.EnrollmentToken;

import org.greenrobot.eventbus.EventBus;

public class CreateEnrollmentTokenJob extends Job {
    private MyManagementAgent myManagementAgent;
    private EnrollmentToken enrollmentToken;
    public CreateEnrollmentTokenJob(MyManagementAgent myManagementAgent) {
        super(new Params(1));
        this.myManagementAgent=myManagementAgent;
    }

    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {
        enrollmentToken=myManagementAgent.createEnrollmentToken(StaticConstants.DURATION);
        EventBus.getDefault().post(enrollmentToken);
    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {

    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        return null;
    }
}
