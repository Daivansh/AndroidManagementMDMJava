package com.daivansh.androidmanagementmdmjava.jobs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.daivansh.androidmanagementmdmjava.utils.MyManagementAgent;
import com.google.api.services.androidmanagement.v1.model.Policy;

import org.greenrobot.eventbus.EventBus;

public class UpdatePolicyJob extends Job {
    private MyManagementAgent myManagementAgent;
    private Policy policy;

    public UpdatePolicyJob(MyManagementAgent myManagementAgent,Policy policy) {
        super(new Params(1));
        this.myManagementAgent=myManagementAgent;
        this.policy=policy;
    }

    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {
        Policy updatedPolicy=myManagementAgent.setPolicy(policy);
        EventBus.getDefault().post(updatedPolicy);
    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {

    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        return null;
    }
}
