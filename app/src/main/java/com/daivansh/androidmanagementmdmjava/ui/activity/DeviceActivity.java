package com.daivansh.androidmanagementmdmjava.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.config.Configuration;
import com.daivansh.androidmanagementmdmjava.MyApplication;
import com.daivansh.androidmanagementmdmjava.R;
import com.daivansh.androidmanagementmdmjava.jobs.ListDeviceJob;
import com.daivansh.androidmanagementmdmjava.ui.adapter.DeviceListAdapter;
import com.daivansh.androidmanagementmdmjava.utils.MyManagementAgent;
import com.daivansh.androidmanagementmdmjava.utils.ProgressDialogHelper;
import com.daivansh.androidmanagementmdmjava.utils.StaticConstants;
import com.google.api.services.androidmanagement.v1.model.Device;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class DeviceActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView mRecyclerView;
    private TextView mTxtNoDeviceFound;
    private MyManagementAgent myManagementAgent;
    private Context mContext=DeviceActivity.this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        Objects.requireNonNull(getSupportActionBar()).setTitle(StaticConstants.ENTERPRISE);

        EventBus.getDefault().register(mContext);

        mTxtNoDeviceFound=findViewById(R.id.tv_no_device_found);
        mRecyclerView=findViewById(R.id.rv_device);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        findViewById(R.id.btn_list_devices).setOnClickListener(this);

        myManagementAgent=((MyApplication)getApplication()).getMyManagementAgent();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_token:
                startActivity(new Intent(mContext,EnrollmentTokenActivity.class));
                break;
            case R.id.item_policy:
                startActivity(new Intent(mContext,PolicyActivity.class));
                break;
            case R.id.item_application_policy:
                startActivity(new Intent(mContext,ApplicationPolicyActivity.class));
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard_menu, menu);
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_list_devices:
                listDevices();
                break;
        }
    }

    private void listDevices() {
        ProgressDialogHelper.showProgressDialog(mContext);
        Configuration.Builder builder=new Configuration.Builder(mContext);
        JobManager manager=new JobManager(builder.build());
        manager.addJobInBackground(new ListDeviceJob(myManagementAgent));
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(List<Device> list){
        ProgressDialogHelper.hideProgressDialog();
        if(list!=null){
            mRecyclerView.setVisibility(View.VISIBLE);
            mTxtNoDeviceFound.setVisibility(View.GONE);
            DeviceListAdapter mDeviceListAdapter = new DeviceListAdapter(mContext, (ArrayList<Device>) list);
            mRecyclerView.setAdapter(mDeviceListAdapter);
        }
        else{
            mRecyclerView.setVisibility(View.GONE);
            mTxtNoDeviceFound.setVisibility(View.VISIBLE);
            Toast.makeText(DeviceActivity.this, getString(R.string.no_device_found), Toast.LENGTH_SHORT).show();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Boolean deletionStatus){
        ProgressDialogHelper.hideProgressDialog();
        if(deletionStatus){
            Toast.makeText(mContext, getString(R.string.delete_device_success), Toast.LENGTH_SHORT).show();
            listDevices();
        }
        else{
            Toast.makeText(mContext, getString(R.string.delete_device_failed), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(mContext);
    }
}
