package com.daivansh.androidmanagementmdmjava.utils;

public interface StaticConstants {
    /** The id of the Google Cloud Platform project. */
    String PROJECT_ID = "PROJECT ID HERE";
    String ENTERPRISE = "Enterprise ID";


    /** The id of the policy for the COSU device. */
    String POLICY_ID = "POLICY ID";
    String POLICY_ABSOLUTE_PATH="POLICY ABSOLUTE PATH";
    String POLICY_RELATIVE_PATH="POLICY RELATIVE PATH";

    /** The package name of the COSU app. */
    String COSU_APP_PACKAGE_NAME =
            "com.google.android.apps.youtube.gaming";


    String COSU_APP_PACKAGE_NAMES[] =new String[]{ "com.google.android.apps.youtube.gaming"
            ,"com.android.chrome"};

    /** The OAuth scope for the Android Management API. */
    String OAUTH_SCOPE =
            "https://www.googleapis.com/auth/androidmanagement";

    /** The name of this app. */
    String APP_NAME = "Android Management";


    //Shared Prefernces files
    String PREF_FILE_TOKEN = "TOKENFILE";
    String PREF_FILE_POLICY = "POLICYFILE";


    //Enrollment Token preference keys
    String TOKEN_VALUE="token_value";
    String TOKEN_EXPIRY_TIME="expiry_time";
    String ENROLLMENT_OBJECT="enrollment_object";
    String POLICY_OBJECT="policy_object";


    //Token Time
    String EMPTY_STRING="";
    String UTC_ZULU_FORMAT="yyyy-MM-dd'T'HH:mm:ss.sss'Z'";
    String DURATION= "2592000s";

    //Policy Apps Intents
    String INTENT_CATEGORY_HOME="android.intent.category.HOME";
    String INTENT_CATEGORY_DEFAULT="android.intent.category.DEFAULT";
    String INTENT_CATEGORY_LAUNCHER="android.intent.category.LAUNCHER";
    String INTENT_ACTION_VIEW="android.intent.action.VIEW";
    String INTENT_ACTION_MAIN="android.intent.action.MAIN";


    //Policy Parameters
    String FORCE_INSTALLED="FORCE_INSTALLED";
    String GRANT="GRANT";
    String ALWAYS="ALWAYS";
    Long MAX_INTEGER_VALUE=2147484000L;

    String FLAG_PRESERVE_RESET_PROTECTION_DATA="PRESERVE_RESET_PROTECTION_DATA";


   //Library Specific Type Fields
   String LS_FIELD_SYSTEM_UPDATE="systemUpdate";

   int INITIAL_INDEX=0;

}
