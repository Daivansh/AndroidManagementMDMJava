package com.daivansh.androidmanagementmdmjava.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.config.Configuration;
import com.daivansh.androidmanagementmdmjava.MyApplication;
import com.daivansh.androidmanagementmdmjava.R;
import com.daivansh.androidmanagementmdmjava.jobs.CreateEnrollmentTokenJob;
import com.daivansh.androidmanagementmdmjava.utils.MyManagementAgent;
import com.daivansh.androidmanagementmdmjava.utils.ProgressDialogHelper;
import com.daivansh.androidmanagementmdmjava.utils.SharedPreferenceHelper;
import com.daivansh.androidmanagementmdmjava.utils.StaticConstants;
import com.google.api.services.androidmanagement.v1.model.EnrollmentToken;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class EnrollmentTokenActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext= EnrollmentTokenActivity.this;
    private MyManagementAgent myManagementAgent;
    private TextView tvToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enrollment_token);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.enrollment_activity_title);
        EventBus.getDefault().register(mContext);
        myManagementAgent=((MyApplication)getApplication()).getMyManagementAgent();
        tvToken=findViewById(R.id.tv_token);
        findViewById(R.id.btn_refresh_token).setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkIfATokenIsPresent();
    }

  /*  private void checkIfATokenIsPresent() {
        String tokenvalue=SharedPreferenceHelper.getSharedPreferenceString(mContext, StaticConstants.PREF_FILE_TOKEN,
                StaticConstants.TOKEN_VALUE,StaticConstants.EMPTY_STRING);
        if(tokenvalue==null || tokenvalue.equalsIgnoreCase(StaticConstants.EMPTY_STRING)){
            createNewToken();
        }
        else{
            String expiryTime=SharedPreferenceHelper.getSharedPreferenceString(mContext, StaticConstants.PREF_FILE_TOKEN,
                    StaticConstants.TOKEN_EXPIRY_TIME,StaticConstants.EMPTY_STRING);
            if(expiryTime==null || expiryTime.equalsIgnoreCase(StaticConstants.EMPTY_STRING)){
                createNewToken();
            }
            else{
                if(checkTokenExpiryDate(expiryTime)){
                    createNewToken();
                }
                else{
                    tvToken.setText(String.format("%s \n %s",getString(R.string.emrollment_display),tokenvalue));
                }
            }
        }
    }*/

    private void checkIfATokenIsPresent() {
       String enrollmentTokenJSON= SharedPreferenceHelper.getSharedPreferenceString(mContext, StaticConstants.PREF_FILE_TOKEN,
               StaticConstants.ENROLLMENT_OBJECT,StaticConstants.EMPTY_STRING);
        if(enrollmentTokenJSON==null || enrollmentTokenJSON.equalsIgnoreCase(StaticConstants.EMPTY_STRING)) {
            createNewToken();
        }
        else{
            Gson gson = new Gson();
            EnrollmentToken token= gson.fromJson(enrollmentTokenJSON, EnrollmentToken.class);
            if (token.getValue() == null || token.getValue().equalsIgnoreCase(StaticConstants.EMPTY_STRING)) {
                createNewToken();
            } else {
                String expiryTime = token.getExpirationTimestamp();
                if (expiryTime == null || expiryTime.equalsIgnoreCase(StaticConstants.EMPTY_STRING)) {
                    createNewToken();
                } else {
                    if (checkTokenExpiryDate(expiryTime)) {
                        createNewToken();
                    } else {
                        tvToken.setText(String.format("%s \n %s", getString(R.string.emrollment_display), token.getValue()));
                    }
                }
            }
        }
    }

        private boolean checkTokenExpiryDate(String expiryTime) {
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat sdf = new SimpleDateFormat(StaticConstants.UTC_ZULU_FORMAT);
            Date date = null;
            try {
                date= sdf.parse(expiryTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return Objects.requireNonNull(date).before(new Date());
        }

    private void createNewToken() {
        ProgressDialogHelper.showProgressDialog(mContext);
        tvToken.setText("");
        Configuration.Builder builder=new Configuration.Builder(mContext);
        JobManager manager=new JobManager(builder.build());
        manager.addJobInBackground(new CreateEnrollmentTokenJob(myManagementAgent));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(mContext);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EnrollmentToken token){
        ProgressDialogHelper.hideProgressDialog();
        if(token!=null){
            tvToken.setText(String.format("%s \n %s",getString(R.string.emrollment_display),token.getValue()));
            saveTokenInPreferences(token);
               /* SharedPreferenceHelper.setSharedPreferenceString(mContext, StaticConstants.PREF_FILE_TOKEN,
                    StaticConstants.TOKEN_VALUE,token.getValue());
            SharedPreferenceHelper.setSharedPreferenceString(mContext, StaticConstants.PREF_FILE_TOKEN,
                    StaticConstants.TOKEN_EXPIRY_TIME,token.getExpirationTimestamp());*/
        }
        else{
            tvToken.setText(getString(R.string.no_token));
            Toast.makeText(mContext, getString(R.string.token_creation_failed), Toast.LENGTH_SHORT).show();
        }
    }

    private void saveTokenInPreferences(EnrollmentToken token){
        Gson gson = new Gson();
        String json = gson.toJson(token);
        SharedPreferenceHelper.setSharedPreferenceString(mContext, StaticConstants.PREF_FILE_TOKEN,
                StaticConstants.ENROLLMENT_OBJECT,json);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_refresh_token:
                checkIfATokenIsPresent();
                break;
        }
    }
}
