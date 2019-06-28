package com.daivansh.androidmanagementmdmjava.ui.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.config.Configuration;
import com.daivansh.androidmanagementmdmjava.MyApplication;
import com.daivansh.androidmanagementmdmjava.R;
import com.daivansh.androidmanagementmdmjava.jobs.DeleteDeviceJob;
import com.daivansh.androidmanagementmdmjava.ui.activity.DeviceActivity;
import com.daivansh.androidmanagementmdmjava.utils.MyManagementAgent;
import com.daivansh.androidmanagementmdmjava.utils.ProgressDialogHelper;
import com.daivansh.androidmanagementmdmjava.utils.StaticConstants;
import com.google.api.services.androidmanagement.v1.model.Device;

import java.util.ArrayList;
import java.util.List;

public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.DeviceListViewHolder> {

    private ArrayList<Device> deviceArrayList;
    private Context mContext;

    public DeviceListAdapter(Context context, ArrayList<Device> deviceArrayList) {
        this.deviceArrayList = deviceArrayList;
        this.mContext=context;
    }

    @Override
    public DeviceListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext)
                .inflate(R.layout.device_item,parent,false);
        return new DeviceListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DeviceListViewHolder holder, int position) {
        Device device=deviceArrayList.get(position);
        if(device!=null){
            holder.tvDeviceName.setText(device.getName());
        }
    }

    @Override
    public int getItemCount() {
        return deviceArrayList!=null ? deviceArrayList.size() : 0;
    }

    class DeviceListViewHolder extends RecyclerView.ViewHolder{
        TextView tvDeviceName;
        DeviceListViewHolder(final View itemView) {
            super(itemView);
            tvDeviceName =itemView.findViewById(R.id.tv_devicename);


            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    AlertDialog.Builder builder=new AlertDialog.Builder(mContext)
                            .setMessage(R.string.delete_device_alert_message)
                            .setCancelable(false)
                            .setPositiveButton(R.string.delete_device_alert_yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if(deviceArrayList.get(getLayoutPosition()).getName()!=null) {
                                        deleteDevice(deviceArrayList.get(getLayoutPosition()).getName());
                                    }
                                }
                            })
                            .setNegativeButton(R.string.delete_device_alert_no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                    AlertDialog dialog=builder.create();
                    dialog.show();
                    return true;
                }
            });
        }
    }

    private void deleteDevice(String deviceName) {
        ProgressDialogHelper.showProgressDialog(mContext);
        Configuration.Builder builder=new Configuration.Builder(mContext);
        JobManager manager=new JobManager(builder.build());
        MyManagementAgent myManagementAgent=((MyApplication)((DeviceActivity)mContext).getApplication()).getMyManagementAgent();
        List<String> wipeDataFlags=new ArrayList<>();
        wipeDataFlags.add(StaticConstants.FLAG_PRESERVE_RESET_PROTECTION_DATA);
        manager.addJobInBackground(new DeleteDeviceJob(myManagementAgent,deviceName,wipeDataFlags));
    }
}
