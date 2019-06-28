package com.daivansh.androidmanagementmdmjava.ui.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.daivansh.androidmanagementmdmjava.R;
import com.daivansh.androidmanagementmdmjava.callbacks.ApplicationActionCallbacks;
import com.google.api.services.androidmanagement.v1.model.ApplicationPolicy;

import java.util.ArrayList;
import java.util.List;


public class ApplicationListAdapter extends RecyclerView.Adapter<ApplicationListAdapter.ApplicationListViewHolder> {

    private List<ApplicationPolicy> applicaitonPolicyList;
    private Context mContext;
    private ApplicationActionCallbacks applicationActionCallbacks;

    public ApplicationListAdapter(Context context, List<ApplicationPolicy> applicaitonPolicyList, ApplicationActionCallbacks applicationActionCallbacks) {
        this.applicaitonPolicyList = applicaitonPolicyList;
        this.mContext=context;
        this.applicationActionCallbacks=applicationActionCallbacks;
    }

    public List<ApplicationPolicy> getApplicaitonPolicyList() {
        return applicaitonPolicyList;
    }

    public void setApplicaitonPolicyList(List<ApplicationPolicy> applicaitonNameList) {
        this.applicaitonPolicyList = applicaitonNameList;
        notifyDataSetChanged();
    }

    /*public void addApplicaitonPolicyList(List<ApplicationPolicy> applicaitonList) {
        this.applicaitonPolicyList.addAll(applicaitonList);
        notifyDataSetChanged();
    }*/
    public void addApplicaitonPolicy(ApplicationPolicy applicationPolicy) {
        if(applicaitonPolicyList!=null) {
            applicaitonPolicyList.add(applicationPolicy);
        }
        else{
            applicaitonPolicyList=new ArrayList<>();
            applicaitonPolicyList.add(applicationPolicy);
        }
        if (applicaitonPolicyList.size() > 0) {
            applicationActionCallbacks.noAppicationsinList(false);
        }
        notifyDataSetChanged();
    }

    public void updateApplicationPolicyInList(ApplicationPolicy applicationPolicy) {
        if(applicaitonPolicyList!=null) {
            int counter = 0;
            for (ApplicationPolicy appPolicy : applicaitonPolicyList) {
                if (appPolicy.getPackageName().equalsIgnoreCase(applicationPolicy.getPackageName())) {
                    applicaitonPolicyList.remove(appPolicy);
                    applicaitonPolicyList.add(counter, applicationPolicy);
                    break;
                }
                counter++;
            }
            notifyDataSetChanged();
        }
    }

    @Override
    public ApplicationListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext)
                .inflate(R.layout.device_item,parent,false);
        return new ApplicationListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ApplicationListViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return applicaitonPolicyList != null ? applicaitonPolicyList.size() : 0;
    }

    class ApplicationListViewHolder extends RecyclerView.ViewHolder {
        TextView tvApplicationName;

        ApplicationListViewHolder(final View itemView) {
            super(itemView);
            tvApplicationName = itemView.findViewById(R.id.tv_devicename);
        }

        void bind(int position) {
            final ApplicationPolicy applicationPolicy= applicaitonPolicyList.get(position);
            if(applicationPolicy != null){
                tvApplicationName.setText(applicationPolicy.getPackageName());
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        applicationActionCallbacks.updateApplicationPolicy(applicationPolicy);
                    }
                });

                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                                .setMessage(R.string.delete_application_alert_message)
                                .setCancelable(false)
                                .setPositiveButton(R.string.delete_device_alert_yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        applicaitonPolicyList.remove(applicationPolicy);
                                        //Todo : remove persistent preferred activity of corresponding deleted application policy.
                                        if(!(applicaitonPolicyList.size() >0)){
                                            applicationActionCallbacks.noAppicationsinList(true);
                                        }
                                        else{
                                           applicationActionCallbacks.deletedApplication(applicationPolicy.getPackageName());
                                        }
                                        notifyDataSetChanged();
                                    }
                                })
                                .setNegativeButton(R.string.delete_device_alert_no, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                        return true;
                    }
                });
            }
        }
    }
}
