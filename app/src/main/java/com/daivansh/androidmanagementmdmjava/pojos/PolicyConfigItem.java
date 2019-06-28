package com.daivansh.androidmanagementmdmjava.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PolicyConfigItem<T> {

    public static final int VALUE_TYPE_BOOLEAN=1;
    public static final int VALUE_TYPE_LIST=2;
    public static final int VALUE_TYPE_LONG=3;
    public static final int VALUE_TYPE_STRING_MESSAGE=4;
    public static final int VALUE_TYPE_LIBRARY_SPECIFIC=5;

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("fieldName")
    @Expose
    private String fieldName;
    @SerializedName("type")
    @Expose
    private int type;
    @SerializedName("subObject")
    @Expose
    private List<T> subObject = null;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public List<?> getSubObject() {
        if(type==VALUE_TYPE_LIST || type==VALUE_TYPE_LIBRARY_SPECIFIC)
            return subObject;
        else
            return null;
    }

    public void setSubObject(List<T> subObject) {
        if(type==VALUE_TYPE_LIST || type==VALUE_TYPE_LIBRARY_SPECIFIC)
            this.subObject = subObject;
    }

}
