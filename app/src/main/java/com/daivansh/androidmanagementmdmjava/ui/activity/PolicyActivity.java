package com.daivansh.androidmanagementmdmjava.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.config.Configuration;
import com.daivansh.androidmanagementmdmjava.MyApplication;
import com.daivansh.androidmanagementmdmjava.R;
import com.daivansh.androidmanagementmdmjava.jobs.GetPolicyJob;
import com.daivansh.androidmanagementmdmjava.jobs.UpdatePolicyJob;
import com.daivansh.androidmanagementmdmjava.ui.adapter.PolicySettingsAdapter;
import com.daivansh.androidmanagementmdmjava.utils.MyManagementAgent;
import com.daivansh.androidmanagementmdmjava.utils.ProgressDialogHelper;
import com.google.api.services.androidmanagement.v1.model.Policy;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Objects;

public class PolicyActivity extends AppCompatActivity {

    private Context mContext= PolicyActivity.this;
    private MyManagementAgent myManagementAgent;
    private static boolean sCheckUpdatedPolicy =false;
    private PolicySettingsAdapter mPolicySettingsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policy);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.policy_activity_title);
        EventBus.getDefault().register(mContext);
        myManagementAgent=((MyApplication)getApplication()).getMyManagementAgent();
        setupPolicySettingsList();
//        findViewById(R.id.btn_set_policy).setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        getCurrentPolicy();
//        getPolicyConfigFromPref();
    }

    private void setupPolicySettingsList() {
        RecyclerView rvPolicySettings = findViewById(R.id.rv_policy_settings);
        mPolicySettingsAdapter=new PolicySettingsAdapter(mContext);
        rvPolicySettings.setLayoutManager(new LinearLayoutManager(mContext));
        rvPolicySettings.setAdapter(mPolicySettingsAdapter);
    }

    private void getCurrentPolicy() {
        ProgressDialogHelper.showProgressDialog(mContext);
        Configuration.Builder builder=new Configuration.Builder(mContext);
        JobManager manager=new JobManager(builder.build());
        manager.addJobInBackground(new GetPolicyJob(myManagementAgent));
    }

    private void updatePolicy(){
        Policy policy=null;
        if(mPolicySettingsAdapter!=null) {
            policy = mPolicySettingsAdapter.getPolicy();
        }
        if(policy!=null){
            sCheckUpdatedPolicy =true;
            ProgressDialogHelper.showProgressDialog(mContext);
            Configuration.Builder builder=new Configuration.Builder(mContext);
            JobManager manager=new JobManager(builder.build());
            manager.addJobInBackground(new UpdatePolicyJob(myManagementAgent,policy));
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_update_policy:
                updatePolicy();
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.policy_menu, menu);
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Policy policy){
        ProgressDialogHelper.hideProgressDialog();
        if(policy!=null){
            mPolicySettingsAdapter.setPolicy(policy);
            if(sCheckUpdatedPolicy){
                Toast.makeText(mContext, getString(R.string.policy_updated_successfully), Toast.LENGTH_SHORT).show();
                sCheckUpdatedPolicy =false;
            }
//            savePolicyInPreferences(policy);
        }
        else if(sCheckUpdatedPolicy){
            Toast.makeText(mContext, getString(R.string.policy_not_applied), Toast.LENGTH_SHORT).show();
            sCheckUpdatedPolicy =false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(mContext);
    }


    /*   private void savePolicyInPreferences(Policy policy){
        ObjectMapper objectMapper = new ObjectMapper();
        String json=null;
        policy.setVersion(policy.getVersion()+StaticConstants.MAX_INTEGER_VALUE);
        try {
            json = objectMapper.writeValueAsString(policy);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
//        Gson gson = new Gson();
//        String json = gson.toJson(policy,Policy.class);
        if(json!=null) {
            SharedPreferenceHelper.setSharedPreferenceString(mContext, StaticConstants.PREF_FILE_POLICY,
                    StaticConstants.POLICY_OBJECT, json);
            updateUI();
        }
    }*/

  /*  private void getPolicyConfigFromPref()  {
        String policyConfigJSON = SharedPreferenceHelper.getSharedPreferenceString(mContext, StaticConstants.PREF_FILE_POLICY,
                StaticConstants.POLICY_OBJECT, StaticConstants.EMPTY_STRING);
        if (policyConfigJSON == null || policyConfigJSON.equalsIgnoreCase(StaticConstants.EMPTY_STRING)) {
            setDefaultPolicyConfig();
        } else {
//            policyConfigJSON=convertVersionToLong(policyConfigJSON);
//            Gson gson = new Gson();
//            policy=gson.fromJson(policyConfigJSON,Policy.class);
            try {
                ObjectMapper mapper=new ObjectMapper();
                policy=mapper.readValue(policyConfigJSON,Policy.class);
                if(policy.getVersion()!=null){
                    policy.setVersion(policy.getVersion()-StaticConstants.MAX_INTEGER_VALUE);
                }

                updateUI();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }*/

    /*  private void setDefaultPolicyConfig() {
        policy.setInstallUnknownSourcesAllowed(false);
        policy.setModifyAccountsDisabled(false);
        policy.setStatusBarDisabled(false);
        policy.setBluetoothConfigDisabled(false);
        updateUI();
//        savePolicyInPreferences(policy);
    }*/

    /*private String convertVersionToLong(String policyConfigJSON) {
        JSONObject jsonObject=null;
        try{
            jsonObject=new JSONObject(policyConfigJSON);
            if(jsonObject.has("version")) {
                long version = jsonObject.getInt("version");
                jsonObject.remove("version");
                jsonObject.put("version", version + 1L);

            }
        }
        catch (JSONException ex){
            ex.printStackTrace();
        }
        return jsonObject.toString();
    }*/

     /*  private void createPolicyAndApply() {
        ProgressDialogHelper.showProgressDialog(mContext);
        Configuration.Builder builder=new Configuration.Builder(mContext);
        JobManager manager=new JobManager(builder.build());
        if(manager!=null)
            manager.addJobInBackground(new UpdatePolicyJob(myManagementAgent,createSingleTaskModePolicy()));
    }*/


   /* private Policy createSingleTaskModePolicy() {
        List<String> categories = new ArrayList<>();
        categories.add(StaticConstants.INTENT_CATEGORY_HOME);
        categories.add(StaticConstants.INTENT_CATEGORY_DEFAULT);
        categories.add(StaticConstants.INTENT_CATEGORY_LAUNCHER);

        List<String> actions= new ArrayList<>();
        actions.add(StaticConstants.INTENT_ACTION_VIEW);
        actions.add(StaticConstants.INTENT_ACTION_MAIN);


        List<ApplicationPolicy> applicationPolicies=new ArrayList<>();
        for(String app : StaticConstants.COSU_APP_PACKAGE_NAMES){
            applicationPolicies.add( new ApplicationPolicy()
                    .setPackageName(app)
                    .setInstallType(StaticConstants.FORCE_INSTALLED)
                    .setDefaultPermissionPolicy("")
                    .setLockTaskAllowed(true));
        }

        return new Policy()
                .setApplications(
                      applicationPolicies)
                .setPersistentPreferredActivities(
                        Collections.singletonList(
                                new PersistentPreferredActivity()
                                        .setReceiverActivity(StaticConstants.COSU_APP_PACKAGE_NAMES[0])
                                        .setActions(actions)
                                        .setCategories(categories)))
                .setCameraDisabled(true)
                .setDefaultPermissionPolicy(StaticConstants.GRANT)
                .setInstallUnknownSourcesAllowed(true)
                .setDebuggingFeaturesAllowed(true)
                .setAppAutoUpdatePolicy(StaticConstants.ALWAYS)
                .setStatusBarDisabled(true);
    }
*/
   /* private Policy createSimplePolicy() {
        return new Policy()
                .setCameraDisabled(true);
    }*/

  /*  @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_set_policy:
                createPolicyAndApply();
                break;

        }
    }*/

}
