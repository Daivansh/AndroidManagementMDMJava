package com.daivansh.androidmanagementmdmjava.ui.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.config.Configuration;
import com.daivansh.androidmanagementmdmjava.MyApplication;
import com.daivansh.androidmanagementmdmjava.R;
import com.daivansh.androidmanagementmdmjava.callbacks.ApplicationActionCallbacks;
import com.daivansh.androidmanagementmdmjava.jobs.GetPolicyJob;
import com.daivansh.androidmanagementmdmjava.jobs.UpdatePolicyJob;
import com.daivansh.androidmanagementmdmjava.ui.adapter.ApplicationListAdapter;
import com.daivansh.androidmanagementmdmjava.utils.MyManagementAgent;
import com.daivansh.androidmanagementmdmjava.utils.ProgressDialogHelper;
import com.daivansh.androidmanagementmdmjava.utils.StaticConstants;
import com.google.api.services.androidmanagement.v1.model.ApplicationPolicy;
import com.google.api.services.androidmanagement.v1.model.PersistentPreferredActivity;
import com.google.api.services.androidmanagement.v1.model.Policy;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ApplicationPolicyActivity extends AppCompatActivity implements ApplicationActionCallbacks,View.OnClickListener, AdapterView.OnItemSelectedListener, CompoundButton.OnCheckedChangeListener {

    private Context mContext= ApplicationPolicyActivity.this;
    private MyManagementAgent myManagementAgent;
    private static boolean sCheckUpdatedPolicy =false;
    ApplicationActionCallbacks applicationActionCallbacks;
    private Policy mPolicy;
    private ApplicationPolicy mApplicationPolicy=null;
    LinearLayout linearLayout,linearLayoutCheckbox;
    private PersistentPreferredActivity mPersistentPreferredActivity=null;
    private Button buttonAddApplication;
    TextView tvNoApplicationsFound;
    EditText etPackageName;
    Spinner spinnerInstallType,spinnerDefaultPermission;
    Switch switchPersistent,switchDisable,switchLocktaskmode;
    CheckBox cbCategoryHome,cbCategoryDefault,cbCategoryLauncher,cbActionMain,cbActionView;
    private ApplicationListAdapter mApplicationListAdapter;
    RecyclerView rvPolicySettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.application_settings_item);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.applications_activity_title);
        EventBus.getDefault().register(mContext);
        myManagementAgent=((MyApplication)getApplication()).getMyManagementAgent();
        setupApplicationsList();
        setupViewIDs();
    }

    private void setupViewIDs() {
        linearLayout=findViewById(R.id.linear_layout_below);
        linearLayoutCheckbox=findViewById(R.id.linear_layout_checkbox);
        tvNoApplicationsFound=findViewById(R.id.tv_no_applications);
        buttonAddApplication=findViewById(R.id.btn_add_application);
        etPackageName=findViewById(R.id.et_app_package_name);
        spinnerDefaultPermission=findViewById(R.id.spinner_app_permission);
        spinnerInstallType=findViewById(R.id.spinner_app_install_type);
        switchDisable=findViewById(R.id.switch_app_disabled);
        switchLocktaskmode=findViewById(R.id.switch_app_locktask);
        switchPersistent=findViewById(R.id.switch_persistent);
        cbCategoryHome=findViewById(R.id.check_category_home);
        cbActionView=findViewById(R.id.check_action_view);
        cbCategoryDefault=findViewById(R.id.check_category_default);
        cbCategoryLauncher=findViewById(R.id.check_category_launcher);
        cbActionMain=findViewById(R.id.check_action_main);
        setupSpinners();
        setListeners();
    }

    private void setupSpinners() {
        String[] permissionSpinnerList=mContext.getResources().getStringArray(R.array.application_setting_permission_spinner);
        String[] installTypeSpinnerList=mContext.getResources().getStringArray(R.array.application_setting_installtype_spinner);

        ArrayAdapter<String> myPermissionSpinnerAdapter= new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item,permissionSpinnerList);
        ArrayAdapter<String> myInstallTypeSpinnerAdapter= new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item,installTypeSpinnerList);

        myInstallTypeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        myPermissionSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerInstallType.setAdapter(myInstallTypeSpinnerAdapter);
        spinnerDefaultPermission.setAdapter(myPermissionSpinnerAdapter);
    }

    private void setListeners() {
        buttonAddApplication.setOnClickListener(this);
//        etPackageName.addTextChangedListener(packageTextWatcher);
        spinnerDefaultPermission.setOnItemSelectedListener(this);
        spinnerInstallType.setOnItemSelectedListener(this);
        switchDisable.setOnCheckedChangeListener(this);
        switchLocktaskmode.setOnCheckedChangeListener(this);
        switchPersistent.setOnCheckedChangeListener(this);
        updateApplicationPolicy(mApplicationPolicy);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getCurrentPolicy();
    }


    private void setupApplicationsList() {
        applicationActionCallbacks=this;
        rvPolicySettings = findViewById(R.id.rv_application_list);
        mApplicationListAdapter=new ApplicationListAdapter(mContext,null,applicationActionCallbacks);
        rvPolicySettings.setLayoutManager(new LinearLayoutManager(mContext,LinearLayout.HORIZONTAL,false));
        rvPolicySettings.setAdapter(mApplicationListAdapter);
    }

    private void getCurrentPolicy() {
        ProgressDialogHelper.showProgressDialog(mContext);
        Configuration.Builder builder=new Configuration.Builder(mContext);
        JobManager manager=new JobManager(builder.build());
        manager.addJobInBackground(new GetPolicyJob(myManagementAgent));
    }

    private void updatePolicy(){
        List<ApplicationPolicy> applicationPolicies;
        if(mPolicy!=null && mApplicationListAdapter!=null){
            applicationPolicies = mApplicationListAdapter.getApplicaitonPolicyList();
            if(applicationPolicies!=null){
                mPolicy.setApplications(applicationPolicies);
            }
            updatePersistentPreferredActivityInPolicy();
            sCheckUpdatedPolicy =true;
            ProgressDialogHelper.showProgressDialog(mContext);
            Configuration.Builder builder=new Configuration.Builder(mContext);
            JobManager manager=new JobManager(builder.build());
            manager.addJobInBackground(new UpdatePolicyJob(myManagementAgent,mPolicy));
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
            this.mPolicy=policy;
            List<ApplicationPolicy> applicationPolicyList=policy.getApplications();
            if(applicationPolicyList!=null){
                rvPolicySettings.setVisibility(View.VISIBLE);
                tvNoApplicationsFound.setVisibility(View.GONE);
                mApplicationListAdapter.setApplicaitonPolicyList(applicationPolicyList);
                mApplicationPolicy =applicationPolicyList.get(0);
                updatePersistentPreferredActivityVariable();
                updateApplicationPolicy(mApplicationPolicy);
                noAppicationsinList(false);
            }
            else{
                noAppicationsinList(true);
                mApplicationListAdapter.setApplicaitonPolicyList(null);
                mApplicationPolicy =null;
                mPersistentPreferredActivity =null;
            }
            if(sCheckUpdatedPolicy){
                Toast.makeText(mContext, getString(R.string.applications_updated_successfully), Toast.LENGTH_SHORT).show();
                sCheckUpdatedPolicy =false;
            }
        }
        else if(sCheckUpdatedPolicy){
            Toast.makeText(mContext, getString(R.string.policy_not_applied), Toast.LENGTH_SHORT).show();
            sCheckUpdatedPolicy =false;
        }
    }

    private void updatePersistentPreferredActivityVariable() {
        List<PersistentPreferredActivity> persistentPreferredList=mPolicy.getPersistentPreferredActivities();
        if(persistentPreferredList!=null && mApplicationPolicy !=null){
            boolean check=true;
            for(PersistentPreferredActivity persistentPreferred : persistentPreferredList) {
                if(persistentPreferred!=null){
                    String recieverActivity =persistentPreferred.getReceiverActivity();
                    if (mApplicationPolicy.getPackageName().equalsIgnoreCase(recieverActivity)) {
                        mPersistentPreferredActivity=persistentPreferred;
                        check=false;
                        break;
                    }
                }
            }
            if(check){
                mPersistentPreferredActivity=null;
            }
        }
        checkPersistency();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(mContext);
    }

    @Override
    public void updateApplicationPolicy(ApplicationPolicy application) {
        if(mPolicy!=null){
            updatePersistentPreferredActivityInPolicy();
            if(mApplicationPolicy!=null){
                mApplicationListAdapter.updateApplicationPolicyInList(mApplicationPolicy);
            }
        }
        mApplicationPolicy=application;
        setupData();
    }

    private void setupData() {
        if(mApplicationPolicy!=null){
            updatePersistentPreferredActivityVariable();
            switchPersistent.setTag(mApplicationPolicy.getPackageName());
            updateSpinners(mApplicationPolicy);
            updateSwitches(mApplicationPolicy);
            updateEditText(mApplicationPolicy);
        }
    }

    @Override
    public void noAppicationsinList(Boolean b) {
        if(b){
            rvPolicySettings.setVisibility(View.GONE);
            tvNoApplicationsFound.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.GONE);
            enableCheckBoxes(false);
            mPolicy.setPersistentPreferredActivities(null);
            mPersistentPreferredActivity=null;
        }
        else{
            if(mPersistentPreferredActivity!=null){
                enableCheckBoxes(true);
            }
            rvPolicySettings.setVisibility(View.VISIBLE);
            tvNoApplicationsFound.setVisibility(View.GONE);
            linearLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void deletedApplication(String deletedApplicationPackage) {
        if(mApplicationPolicy!=null){
            if(deletedApplicationPackage.equalsIgnoreCase(mApplicationPolicy.getPackageName())){
                mApplicationPolicy=mApplicationListAdapter.getApplicaitonPolicyList().get(StaticConstants.INITIAL_INDEX);
                setupData();
            }
        }
        List<PersistentPreferredActivity> persistentPreferredActivities=mPolicy.getPersistentPreferredActivities();
        if(persistentPreferredActivities!=null) {
            for (int i = 0; i < persistentPreferredActivities.size(); i++) {
                PersistentPreferredActivity preferredActivity = persistentPreferredActivities.get(i);
                if (preferredActivity.getReceiverActivity().equalsIgnoreCase(deletedApplicationPackage)) {
                    mPolicy.getPersistentPreferredActivities().remove(preferredActivity);
                }
            }
        }
    }


    private void updatePersistentPreferredActivityInPolicy() {
        List<PersistentPreferredActivity> preferredActivities = mPolicy.getPersistentPreferredActivities();
        if(preferredActivities!=null){
            for(int i=0;i<preferredActivities.size();i++){
                PersistentPreferredActivity preferredActivity=preferredActivities.get(i);
                if(preferredActivity.getReceiverActivity()
                        .equalsIgnoreCase(mApplicationPolicy.getPackageName())){
                    preferredActivities.remove(preferredActivity);
                    break;
                }
            }
            if(mPersistentPreferredActivity!=null)
                preferredActivities.add(mPersistentPreferredActivity);
        }
        else {
            if(mPersistentPreferredActivity!=null) {
                preferredActivities = new ArrayList<>();
                preferredActivities.add(mPersistentPreferredActivity);
            }
        }
        mPolicy.setPersistentPreferredActivities(preferredActivities);
    }

    private void checkPersistency() {
        if(mPersistentPreferredActivity!=null && mApplicationPolicy!=null) {
            switchPersistent.setChecked(true);
            enableCheckBoxes(true);
            List<String> intents = mPersistentPreferredActivity.getActions();
            if (intents != null){
                for (String intent : intents) {
                    switch (intent) {
                        case StaticConstants.INTENT_ACTION_MAIN:
                            cbActionMain.setChecked(true);
                            break;
                        case StaticConstants.INTENT_ACTION_VIEW:
                            cbActionView.setChecked(true);
                            break;
                    }
                }
        }
            intents=mPersistentPreferredActivity.getCategories();
            if(intents!=null) {
                for (String intent : intents) {
                    switch (intent) {
                        case StaticConstants.INTENT_CATEGORY_DEFAULT:
                            cbCategoryDefault.setChecked(true);
                            break;
                        case StaticConstants.INTENT_CATEGORY_HOME:
                            cbCategoryHome.setChecked(true);
                            break;
                        case StaticConstants.INTENT_CATEGORY_LAUNCHER:
                            cbCategoryLauncher.setChecked(true);
                            break;
                    }
                }
            }
        }
        else{
            switchPersistent.setChecked(false);
            enableCheckBoxes(false);
        }
    }

    private void enableCheckBoxes(Boolean b) {
      /*  cbCategoryLauncher.setEnabled(b);
        cbCategoryHome.setEnabled(b);
        cbCategoryDefault.setEnabled(b);
        cbActionView.setEnabled(b);
        cbActionMain.setEnabled(b);*/
        if(b){
            linearLayoutCheckbox.setVisibility(View.VISIBLE);
            cbActionView.setOnCheckedChangeListener(this);
            cbActionMain.setOnCheckedChangeListener(this);
            cbCategoryDefault.setOnCheckedChangeListener(this);
            cbCategoryHome.setOnCheckedChangeListener(this);
            cbCategoryLauncher.setOnCheckedChangeListener(this);
        }
        else{
            linearLayoutCheckbox.setVisibility(View.GONE);
            cbActionView.setOnCheckedChangeListener(null);
            cbActionMain.setOnCheckedChangeListener(null);
            cbCategoryDefault.setOnCheckedChangeListener(null);
            cbCategoryHome.setOnCheckedChangeListener(null);
            cbCategoryLauncher.setOnCheckedChangeListener(null);
            cbActionMain.setChecked(b);
            cbActionView.setChecked(b);
            cbCategoryDefault.setChecked(b);
            cbCategoryHome.setChecked(b);
            cbCategoryLauncher.setChecked(b);
        }
    }

    private void updateEditText(ApplicationPolicy appPolicy) {
        String val=appPolicy.getPackageName();
        if(val!=null){
            etPackageName.setText(val);
        }
    }

    private void updateSwitches(ApplicationPolicy appPolicy) {
        Boolean bool=appPolicy.getDisabled();
        if(bool!=null){
            switchDisable.setChecked(bool);
        }
        else{
            switchDisable.setChecked(false);
        }
        bool=appPolicy.getLockTaskAllowed();
        if(bool!=null){
            switchLocktaskmode.setChecked(bool);
        }
        else{
            switchLocktaskmode.setChecked(false);
        }
    }

    private void updateSpinners(ApplicationPolicy appPolicy) {
        String[] permissionSpinnerList=mContext.getResources().getStringArray(R.array.application_setting_permission_spinner);
        String[] installTypeSpinnerList=mContext.getResources().getStringArray(R.array.application_setting_installtype_spinner);

        String value=appPolicy.getDefaultPermissionPolicy();
        if(value!=null){
            for(int i=0;i<permissionSpinnerList.length;i++){
                if(value.equalsIgnoreCase(permissionSpinnerList[i])){
                    spinnerDefaultPermission.setSelection(i);
                }
            }
        }
        else{
            spinnerDefaultPermission.setSelection(0);
        }
        value=appPolicy.getInstallType();
        if(value!=null){
            for(int i=0;i<installTypeSpinnerList.length;i++){
                if(value.equalsIgnoreCase(installTypeSpinnerList[i])){
                    spinnerInstallType.setSelection(i);
                }
            }
        }
        else{
            spinnerInstallType.setSelection(0);
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.btn_add_application){
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

            final EditText input = new EditText(mContext);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            input.setLayoutParams(lp);
            builder.setView(input)
                    .setMessage(R.string.add_application_alert_message)
                    .setCancelable(false)
                    .setPositiveButton(R.string.add_application_alert_add, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String packageName = input.getText().toString().trim();
                            if(packageName.equalsIgnoreCase(StaticConstants.EMPTY_STRING)){
                                input.setError(getString(R.string.empty_package_name));
                            }
                            else{
                                ApplicationPolicy applicationPolicy=new ApplicationPolicy();
                                applicationPolicy.setPackageName(packageName);
                                mApplicationListAdapter.addApplicaitonPolicy(applicationPolicy);
                                mApplicationPolicy=applicationPolicy;
                                setupData();

                            }
                        }
                    })
                    .setNegativeButton(R.string.add_application_alert_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });



            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    /*TextWatcher packageTextWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            // TODO Auto-generated method
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if(TextUtils.isEmpty(charSequence)){
                mApplicationPolicy.setPackageName(StaticConstants.EMPTY_STRING);
            }
            else{
                mApplicationPolicy.setPackageName(String.valueOf(charSequence));
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // TODO Auto-generated method
        }
    };*/

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String[] permissionSpinnerList=mContext.getResources().getStringArray(R.array.application_setting_permission_spinner);
        String[] installTypeSpinnerList=mContext.getResources().getStringArray(R.array.application_setting_installtype_spinner);
        if(mApplicationPolicy!=null) {
            switch (adapterView.getId()) {
                case R.id.spinner_app_install_type:
                    mApplicationPolicy.setInstallType(installTypeSpinnerList[i]);
                    break;
                case R.id.spinner_app_permission:
                    mApplicationPolicy.setDefaultPermissionPolicy(permissionSpinnerList[i]);
                    break;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // TODO Auto-generated method
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.switch_persistent:
                if (b) {
                    if(mApplicationPolicy!=null && mPersistentPreferredActivity==null){
                        mPersistentPreferredActivity=new PersistentPreferredActivity();
                        mPersistentPreferredActivity.setReceiverActivity(mApplicationPolicy.getPackageName());
                    }
                } else {
                    mPersistentPreferredActivity=null;
                }
                enableCheckBoxes(b);
                break;
            case R.id.switch_app_disabled:
                if(mApplicationPolicy!=null){
                    mApplicationPolicy.setDisabled(b);
                }
                break;
            case R.id.switch_app_locktask:
                if(mApplicationPolicy!=null){
                    if(b){
                        mApplicationPolicy.setLockTaskAllowed(true);
                    }
                    else{
                        mApplicationPolicy.setLockTaskAllowed(false);
                    }

                }
                break;

            case R.id.check_action_main:
                if(mPersistentPreferredActivity!=null){
                    List<String> actions=mPersistentPreferredActivity.getActions();
                    if(b){
                        if(actions!=null){
                            if(!(actions.contains(StaticConstants.INTENT_ACTION_MAIN))){
                                actions.add(StaticConstants.INTENT_ACTION_MAIN);
                            }
                            mPersistentPreferredActivity.setActions(actions);
                        }
                        else{
                            actions=new ArrayList<>();
                            actions.add(StaticConstants.INTENT_ACTION_MAIN);
                            mPersistentPreferredActivity.setActions(actions);
                        }
                    }
                    else{
                        if(actions!=null){
                            actions.remove(StaticConstants.INTENT_ACTION_MAIN);
                            mPersistentPreferredActivity.setActions(actions);
                        }
                        else{
                            Toast.makeText(mContext, "Intent Already Null", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
            case R.id.check_action_view:
                if(mPersistentPreferredActivity!=null){
                    List<String> actions=mPersistentPreferredActivity.getActions();
                    if(b){
                        if(actions!=null){
                            if(!(actions.contains(StaticConstants.INTENT_ACTION_VIEW))){
                                actions.add(StaticConstants.INTENT_ACTION_VIEW);
                            }
                            mPersistentPreferredActivity.setActions(actions);
                        }
                        else{
                            actions=new ArrayList<>();
                            actions.add(StaticConstants.INTENT_ACTION_VIEW);
                            mPersistentPreferredActivity.setActions(actions);
                        }
                    }
                    else{
                        if(actions!=null){
                            actions.remove(StaticConstants.INTENT_ACTION_VIEW);
                            mPersistentPreferredActivity.setActions(actions);
                        }
                        else{
                            Toast.makeText(mContext, "Intent Already Null", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
            case R.id.check_category_default:
                if(mPersistentPreferredActivity!=null){
                    List<String> categories=mPersistentPreferredActivity.getCategories();
                    if(b){
                        if(categories!=null){
                            if(!(categories.contains(StaticConstants.INTENT_CATEGORY_DEFAULT))){
                                categories.add(StaticConstants.INTENT_CATEGORY_DEFAULT);
                            }
                            mPersistentPreferredActivity.setCategories(categories);
                        }
                        else{
                            categories=new ArrayList<>();
                            categories.add(StaticConstants.INTENT_CATEGORY_DEFAULT);
                            mPersistentPreferredActivity.setCategories(categories);
                        }
                    }
                    else{
                        if(categories!=null){
                            categories.remove(StaticConstants.INTENT_CATEGORY_DEFAULT);
                            mPersistentPreferredActivity.setCategories(categories);
                        }
                        else{
                            Toast.makeText(mContext, "Intent Already Null", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
            case R.id.check_category_home:
                if(mPersistentPreferredActivity!=null){
                    List<String> categories=mPersistentPreferredActivity.getCategories();
                    if(b){
                        if(categories!=null){
                            if(!(categories.contains(StaticConstants.INTENT_CATEGORY_HOME))){
                                categories.add(StaticConstants.INTENT_CATEGORY_HOME);
                            }
                            mPersistentPreferredActivity.setCategories(categories);
                        }
                        else{
                            categories=new ArrayList<>();
                            categories.add(StaticConstants.INTENT_CATEGORY_HOME);
                            mPersistentPreferredActivity.setCategories(categories);
                        }
                    }
                    else{
                        if(categories!=null){
                            categories.remove(StaticConstants.INTENT_CATEGORY_HOME);
                            mPersistentPreferredActivity.setCategories(categories);
                        }
                        else{
                            Toast.makeText(mContext, "Intent Already Null", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
            case R.id.check_category_launcher:
                if(mPersistentPreferredActivity!=null){
                    List<String> categories=mPersistentPreferredActivity.getCategories();
                    if(b){
                        if(categories!=null){
                            if(!(categories.contains(StaticConstants.INTENT_CATEGORY_LAUNCHER))){
                                categories.add(StaticConstants.INTENT_CATEGORY_LAUNCHER);
                            }
                            mPersistentPreferredActivity.setCategories(categories);
                        }
                        else{
                            categories=new ArrayList<>();
                            categories.add(StaticConstants.INTENT_CATEGORY_LAUNCHER);
                            mPersistentPreferredActivity.setCategories(categories);
                        }
                    }
                    else{
                        if(categories!=null){
                            categories.remove(StaticConstants.INTENT_CATEGORY_LAUNCHER);
                            mPersistentPreferredActivity.setCategories(categories);
                        }
                        else{
                            Toast.makeText(mContext, "Intent Already Null", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;

        }
    }
}
