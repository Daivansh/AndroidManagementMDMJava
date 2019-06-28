package com.daivansh.androidmanagementmdmjava.ui.adapter;

public class ApplicationSettingsAdapter /*extends RecyclerView.Adapter<ApplicationSettingsAdapter.ApplicationSettingsViewHolder>*/ {
/*
    private List<ApplicationPolicy> mApplicationPolicyList=new ArrayList<>();
    private List<PersistentPreferredActivity> mPersistentPreferredActivityList;
    private Policy policy;
    private Context mContext;

    public ApplicationSettingsAdapter(Context context) {
        this.mContext=context;
        policy=new Policy();
        mApplicationPolicyList.add(new ApplicationPolicy());
    }

    public void setPolicy(Policy policy) {
        this.policy = policy;
        if(policy.getApplications()!=null){
            mApplicationPolicyList=policy.getApplications();
            mApplicationPolicyList.add(new ApplicationPolicy());
            mPersistentPreferredActivityList=policy.getPersistentPreferredActivities();
            notifyDataSetChanged();
        }
    }

    public Policy getPolicy() {
        return policy;
    }

    @Override
    public ApplicationSettingsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.application_settings_item,parent,false);
        return new ApplicationSettingsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ApplicationSettingsViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mApplicationPolicyList!=null ? mApplicationPolicyList.size() : 0 ;
    }

    class ApplicationSettingsViewHolder extends RecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener {
        EditText etPackageName,etMinAppVersion;
        Spinner spinnerInstallType,spinnerDefaultPermission;
        Switch switchPersistent,switchDisable,switchLocktaskmode;
        CheckBox cbCategoryHome,cbCategoryDefault,cbCategoryLauncher,cbActionMain,cbActionView;
        ApplicationPolicy applicationPolicyConfig;

        ApplicationSettingsViewHolder(View itemView) {
            super(itemView);
            etPackageName=itemView.findViewById(R.id.et_app_package_name);
            etMinAppVersion=itemView.findViewById(R.id.et_app_minimum_version);
            spinnerDefaultPermission=itemView.findViewById(R.id.spinner_app_permission);
            spinnerInstallType=itemView.findViewById(R.id.spinner_app_install_type);
            switchDisable=itemView.findViewById(R.id.switch_app_disabled);
            switchLocktaskmode=itemView.findViewById(R.id.switch_app_locktask);
            switchPersistent=itemView.findViewById(R.id.switch_persistent);
            cbCategoryHome=itemView.findViewById(R.id.check_category_home);
            cbActionView=itemView.findViewById(R.id.check_action_view);
            cbCategoryDefault=itemView.findViewById(R.id.check_category_default);
            cbCategoryLauncher=itemView.findViewById(R.id.check_category_launcher);
            cbActionMain=itemView.findViewById(R.id.check_action_main);
        }

        void bind(int position) {
            applicationPolicyConfig=mApplicationPolicyList.get(position);

            String[] permissionSpinnerList=mContext.getResources().getStringArray(R.array.application_setting_permission_spinner);
            String[] installTypeSpinnerList=mContext.getResources().getStringArray(R.array.application_setting_installtype_spinner);

            ArrayAdapter<String> myPermissionSpinnerAdapter= new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item,permissionSpinnerList);
            ArrayAdapter<String> myInstallTypeSpinnerAdapter= new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item,installTypeSpinnerList);

            myInstallTypeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            myPermissionSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spinnerInstallType.setAdapter(myInstallTypeSpinnerAdapter);
            spinnerDefaultPermission.setAdapter(myPermissionSpinnerAdapter);

            switchPersistent.setTag(applicationPolicyConfig.getPackageName());
            switchPersistent.setOnCheckedChangeListener(this);
            updateSpinners(permissionSpinnerList,installTypeSpinnerList);
            updateSwitches();
            updateEditText();
            checkPersistency();
        }

        private void checkPersistency() {
            for(PersistentPreferredActivity persistentPreferred : mPersistentPreferredActivityList){
                if(applicationPolicyConfig.getPackageName().equalsIgnoreCase(persistentPreferred.getReceiverActivity())){
                    switchPersistent.setChecked(true);
                    List<String> intents=persistentPreferred.getCategories();
                    intents.addAll(persistentPreferred.getActions());
                    for(String intent : intents){
                        switch (intent){
                            case StaticConstants.INTENT_ACTION_MAIN:
                                cbActionMain.setChecked(true);
                                break;
                            case StaticConstants.INTENT_ACTION_VIEW:
                                cbActionView.setChecked(true);
                                break;
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
                    break;
                }
            }
        }

        private void updateEditText() {
            String val=applicationPolicyConfig.getPackageName();
            if(val!=null){
                etPackageName.setText(val);
            }
            int ver=applicationPolicyConfig.getMinimumVersionCode();
            if(ver!=0){
                etMinAppVersion.setText(String.valueOf(ver));
            }
        }

        private void updateSwitches() {
            Boolean bool=applicationPolicyConfig.getDisabled();
            if(bool!=null){
                switchDisable.setChecked(bool);
            }
            bool=applicationPolicyConfig.getLockTaskAllowed();
            if(bool!=null){
                switchLocktaskmode.setChecked(bool);
            }
        }

        private void updateSpinners(String[] permissionSpinnerList, String[] installTypeSpinnerList) {
            String value=applicationPolicyConfig.getDefaultPermissionPolicy();
            if(value!=null){
                for(int i=0;i<permissionSpinnerList.length;i++){
                    if(value.equalsIgnoreCase(permissionSpinnerList[i])){
                        spinnerDefaultPermission.setSelection(i);
                    }
                }
            }
            value=applicationPolicyConfig.getInstallType();
            if(value!=null){
                for(int i=0;i<installTypeSpinnerList.length;i++){
                    if(value.equalsIgnoreCase(installTypeSpinnerList[i])){
                        spinnerInstallType.setSelection(i);
                    }
                }
            }
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            String tag= (String) compoundButton.getTag();
            switch (compoundButton.getId()){
                case R.id.switch_persistent:
                    if (b) {
                        PersistentPreferredActivity preferredActivity=new PersistentPreferredActivity();
                        preferredActivity.setReceiverActivity(tag);
                        mPersistentPreferredActivityList.add(preferredActivity);
                        enableCheckBoxes(b);
                    } else {
                        for(PersistentPreferredActivity preferredActivity : mPersistentPreferredActivityList){
                            if(preferredActivity.getReceiverActivity().equalsIgnoreCase(tag)){
                                mPersistentPreferredActivityList.remove(preferredActivity);
                                break;
                            }
                        }
                        enableCheckBoxes(b);
                    }
                    break;
                case R.id.check_action_main:
                    if(b){
                        for(PersistentPreferredActivity preferredActivity : mPersistentPreferredActivityList){
                            if(preferredActivity.getReceiverActivity().equalsIgnoreCase(tag)){
                                preferredActivity.getCategories().add(StaticConstants)
                                break;
                            }
                        }
                    }
                    else{

                    }
                    break;
                case R.id.check_action_view:

                    break;
                case R.id.check_category_default:

                    break;
                case R.id.check_category_home:

                    break;
                case R.id.check_category_launcher:

                    break;
            }
        }

        private void enableCheckBoxes(Boolean b) {
            cbCategoryLauncher.setEnabled(b);
            cbCategoryHome.setEnabled(b);
            cbCategoryDefault.setEnabled(b);
            cbActionView.setEnabled(b);
            cbActionView.setEnabled(b);
            if(b){
                cbActionView.setOnCheckedChangeListener(this);
                cbActionMain.setOnCheckedChangeListener(this);
                cbCategoryDefault.setOnCheckedChangeListener(this);
                cbCategoryHome.setOnCheckedChangeListener(this);
                cbCategoryLauncher.setOnCheckedChangeListener(this);
            }
            else{
                cbActionView.setOnCheckedChangeListener(null);
                cbActionMain.setOnCheckedChangeListener(null);
                cbCategoryDefault.setOnCheckedChangeListener(null);
                cbCategoryHome.setOnCheckedChangeListener(null);
                cbCategoryLauncher.setOnCheckedChangeListener(null);
            }
        }

    }*/
}

