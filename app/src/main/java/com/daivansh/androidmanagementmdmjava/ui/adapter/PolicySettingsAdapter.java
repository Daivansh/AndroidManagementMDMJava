package com.daivansh.androidmanagementmdmjava.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.daivansh.androidmanagementmdmjava.R;
import com.daivansh.androidmanagementmdmjava.pojos.PolicyConfig;
import com.daivansh.androidmanagementmdmjava.pojos.PolicyConfigItem;
import com.daivansh.androidmanagementmdmjava.utils.StaticConstants;
import com.google.api.services.androidmanagement.v1.model.Policy;
import com.google.api.services.androidmanagement.v1.model.SystemUpdate;
import com.google.api.services.androidmanagement.v1.model.UserFacingMessage;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.util.List;

public class PolicySettingsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<PolicyConfigItem> mPolicyConfigItemList;
    private Policy policy;
    private Context mContext;

    public PolicySettingsAdapter(Context context) {
        this.mContext=context;
        policy=new Policy();
        setupPolicyConfigList();
    }

    private void setupPolicyConfigList() {
        InputStream raw =  mContext.getResources().openRawResource(R.raw.mypolicyconfigjson);
        Reader rd = new BufferedReader(new InputStreamReader(raw));
        Gson gson = new Gson();
        PolicyConfig config = gson.fromJson(rd, PolicyConfig.class);

        mPolicyConfigItemList=config.getPolicyConfigItem();
    }

    public void setPolicy(Policy policy) {
        this.policy = policy;
        notifyDataSetChanged();
    }

    public Policy getPolicy() {
        return policy;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        switch (viewType){
            case R.layout.policy_settings_switch_item:
                v= LayoutInflater.from(parent.getContext()).inflate(R.layout.policy_settings_switch_item,parent,false);
                return new PolicySettingsSwitchViewHolder(v);

            case R.layout.policy_settings_spinner_item:
                v= LayoutInflater.from(parent.getContext()).inflate(R.layout.policy_settings_spinner_item,parent,false);
                return new PolicySettingsSpinnerViewHolder(v);

            case R.layout.policy_settings_textinput_item:
                v= LayoutInflater.from(parent.getContext()).inflate(R.layout.policy_settings_textinput_item,parent,false);
                return new PolicySettingsInputViewHolder(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()){

            case R.layout.policy_settings_switch_item:
                ((PolicySettingsSwitchViewHolder)holder).bindSwitchHolder(position);
                break;
            case R.layout.policy_settings_spinner_item:
                ((PolicySettingsSpinnerViewHolder)holder).bindSpinnerHolder(position);
                break;
            case R.layout.policy_settings_textinput_item:
                ((PolicySettingsInputViewHolder)holder).bindTextHolder(position);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(mPolicyConfigItemList.get(position)!=null){
            switch (mPolicyConfigItemList.get(position).getType()){
                case PolicyConfigItem.VALUE_TYPE_BOOLEAN:
                    return R.layout.policy_settings_switch_item;

                case  PolicyConfigItem.VALUE_TYPE_LIBRARY_SPECIFIC:
                case PolicyConfigItem.VALUE_TYPE_LIST:
                    return R.layout.policy_settings_spinner_item;

                case  PolicyConfigItem.VALUE_TYPE_LONG:
                case PolicyConfigItem.VALUE_TYPE_STRING_MESSAGE:
                    return R.layout.policy_settings_textinput_item;
            }
        }
        return 0;
    }

    @Override
    public int getItemCount() {
        return mPolicyConfigItemList!=null ? mPolicyConfigItemList.size() : 0 ;
    }

    class PolicySettingsSwitchViewHolder extends RecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener{
        TextView tvSettingsName;
        Switch switchSettings;

        PolicySettingsSwitchViewHolder(View itemView) {
            super(itemView);
            this.setIsRecyclable(false);
            tvSettingsName=itemView.findViewById(R.id.tv_settings_name);
            switchSettings=itemView.findViewById(R.id.switch_settings);
        }

        void bindSwitchHolder(int position){
            if(mPolicyConfigItemList.get(position)!=null){
                PolicyConfigItem policyConfigItem=mPolicyConfigItemList.get(position);
                tvSettingsName.setText(policyConfigItem.getName());
                switchSettings.setOnCheckedChangeListener(this);
                String fieldName=policyConfigItem.getFieldName();
                switchSettings.setTag(fieldName);
                updateSwitch(fieldName);
            }
        }

        private void updateSwitch(String fieldName) {
            try {
                if(policy!=null) {
                    Field field = policy.getClass().getDeclaredField(fieldName);
                    if (field != null) {
                        field.setAccessible(true);
                        Boolean var = (Boolean) field.get(policy);
                        if (var != null)
                            switchSettings.setChecked(var);
                    }
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            String fieldName = (String) compoundButton.getTag();
            try {
                if(policy!=null) {
                    Field field = policy.getClass().getDeclaredField(fieldName);
                    if (field != null) {
                        field.setAccessible(true);
                        field.set(policy, b);
                    }
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

    }


    class PolicySettingsSpinnerViewHolder extends RecyclerView.ViewHolder implements AdapterView.OnItemSelectedListener {
        TextView tvSettingsName;
        Spinner spinnerSettings;

        PolicySettingsSpinnerViewHolder(View itemView) {
            super(itemView);
            this.setIsRecyclable(false);
            tvSettingsName=itemView.findViewById(R.id.tv_spinner_settings_name);
            spinnerSettings=itemView.findViewById(R.id.spinner_settings);
        }

        void bindSpinnerHolder(int position){
            if(mPolicyConfigItemList.get(position)!=null) {
                PolicyConfigItem policyConfigItem = mPolicyConfigItemList.get(getAdapterPosition());

                tvSettingsName.setText(policyConfigItem.getName());

                if(policyConfigItem.getSubObject()!=null){
                    String fieldName=policyConfigItem.getFieldName();
                    List<String> spinnerList=  policyConfigItem.getSubObject();

                    ArrayAdapter<String> mySpinnerAdapter= new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item,spinnerList);
                    mySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    spinnerSettings.setAdapter(mySpinnerAdapter);
                    spinnerSettings.setTag(fieldName);
                    spinnerSettings.setOnItemSelectedListener(this);
                    updateSpinner(fieldName,spinnerList,policyConfigItem.getType());
                }
            }
        }

        private void updateSpinner(String fieldName, List<String> spinnerList, int type) {
            try {
                if (policy != null) {
                    Field field = policy.getClass().getDeclaredField(fieldName);
                    if (field != null) {
                        field.setAccessible(true);
                        if(type == PolicyConfigItem.VALUE_TYPE_LIBRARY_SPECIFIC){
                            if(fieldName.equalsIgnoreCase(StaticConstants.LS_FIELD_SYSTEM_UPDATE)){
                                SystemUpdate systemUpdate= (SystemUpdate) field.get(policy);
                                if(systemUpdate!=null){
                                    String systemUpdateType = systemUpdate.getType();
                                    for (int i=0;i<spinnerList.size();i++) {
                                        if(spinnerList.get(i).equalsIgnoreCase(systemUpdateType)){
                                            spinnerSettings.setSelection(i);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        else{
                            String var = (String) field.get(policy);
                            if (var != null) {
                                for (int i=0;i<spinnerList.size();i++) {
                                    if(spinnerList.get(i).equalsIgnoreCase(var)){
                                        spinnerSettings.setSelection(i);
                                        break;
                                    }
                                }
                            }
                        }

                    }
                }
            } catch (IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            String fieldName = (String) adapterView.getTag();
            try {
                if(policy!=null && fieldName!=null) {
                    Field field = policy.getClass().getDeclaredField(fieldName);
                    if (field != null) {
                        field.setAccessible(true);
                        String value= (String) adapterView.getItemAtPosition(i);
                        if (fieldName.equalsIgnoreCase(StaticConstants.LS_FIELD_SYSTEM_UPDATE)) {
                            SystemUpdate update=null;
                            if(i!=0) {
                                update = new SystemUpdate();
                                update.setType(value);
                            }
                            field.set(policy, update);
                        }
                        else{
                            field.set(policy, value);
                        }

                    }
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            // TODO Auto-generated method
        }
    }



    class PolicySettingsInputViewHolder extends RecyclerView.ViewHolder implements TextWatcher {
        TextView tvSettingsName;
        EditText editTextSettings;

        PolicySettingsInputViewHolder(View itemView) {
            super(itemView);
            this.setIsRecyclable(false);
            tvSettingsName=itemView.findViewById(R.id.tv_text_settings_name);
            editTextSettings=itemView.findViewById(R.id.edit_text_settings);

        }

        void bindTextHolder(int position){
            if(mPolicyConfigItemList.get(position)!=null) {
                PolicyConfigItem policyConfigItem = mPolicyConfigItemList.get(getAdapterPosition());
                tvSettingsName.setText(policyConfigItem.getName());
                editTextSettings.addTextChangedListener(this);
                String fieldName=policyConfigItem.getFieldName();
                updateEditText(fieldName,policyConfigItem.getType());
            }
        }

        private void updateEditText(String fieldName, int type) {
            try {
                if(policy!=null) {
                    Field field = policy.getClass().getDeclaredField(fieldName);
                    if (field != null) {
                        field.setAccessible(true);
                        if(type == PolicyConfigItem.VALUE_TYPE_STRING_MESSAGE){
                            editTextSettings.setHint(mContext.getString(R.string.edit_text_message));
                            UserFacingMessage message= (UserFacingMessage) field.get(policy);
                            if(message!=null){
                                editTextSettings.setText(message.getDefaultMessage());
                            }
                            else{
                                editTextSettings.setText("");
                            }
                        }
                        else if(type == PolicyConfigItem.VALUE_TYPE_LONG){
                            Long number= (Long) field.get(policy);
                            editTextSettings.setHint(mContext.getString(R.string.edit_text_number));
                            editTextSettings.setInputType(InputType.TYPE_CLASS_NUMBER);
                            if(number!=null){
                                editTextSettings.setText(String.valueOf(number));
                            }
                            else{
                                editTextSettings.setText(mContext.getString(R.string.default_long_value));
                            }
                        }
                    }
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            try {
                if(policy!=null) {
                    PolicyConfigItem policyConfigItem=mPolicyConfigItemList.get(getAdapterPosition());
                    String fieldName=policyConfigItem.getFieldName();
                    int type=policyConfigItem.getType();

                    Field field = policy.getClass().getDeclaredField(fieldName);
                    if (field != null) {
                        field.setAccessible(true);
                        if(type == PolicyConfigItem.VALUE_TYPE_STRING_MESSAGE){
                            if(!(TextUtils.isEmpty(charSequence))){
                                UserFacingMessage message=new UserFacingMessage();
                                message.setDefaultMessage(String.valueOf(charSequence));
                                field.set(policy,message);
                            }else{
                                field.set(policy,null);
                            }

                        }
                        else if(type == PolicyConfigItem.VALUE_TYPE_LONG){
                            if(!(TextUtils.isEmpty(charSequence))){
                                Long number= Long.valueOf(String.valueOf(charSequence));
                                field.set(policy,number);
                            }else{
                                field.set(policy,null);
                            }

                        }
                    }
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }

}

