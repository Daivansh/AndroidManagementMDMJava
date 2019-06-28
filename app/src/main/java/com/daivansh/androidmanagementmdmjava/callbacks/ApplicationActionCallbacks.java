package com.daivansh.androidmanagementmdmjava.callbacks;

import com.google.api.services.androidmanagement.v1.model.ApplicationPolicy;

public interface ApplicationActionCallbacks {

    public void updateApplicationPolicy(ApplicationPolicy application);
    public void noAppicationsinList(Boolean b);
    public void deletedApplication(String deletedApplicationPackage);
}
