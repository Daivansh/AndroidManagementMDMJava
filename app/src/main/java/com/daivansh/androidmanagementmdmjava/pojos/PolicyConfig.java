package com.daivansh.androidmanagementmdmjava.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PolicyConfig {

    @SerializedName("policyConfigItem")
    @Expose
    private List<PolicyConfigItem> policyConfigItem = null;

    public List<PolicyConfigItem> getPolicyConfigItem() {
        return policyConfigItem;
    }

    public void setPolicyConfigItem(List<PolicyConfigItem> policyConfigItem) {
        this.policyConfigItem = policyConfigItem;
    }
}