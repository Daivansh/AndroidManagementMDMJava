package com.daivansh.androidmanagementmdmjava.utils;

import java.util.ArrayList;
import java.util.HashMap;

public class PolicySettings {
    private  static  HashMap<String, String> mapPolicyConfig;
    private  static  ArrayList<ArrayList<String>> spinnerArrayLists;

    static {
        mapPolicyConfig=new HashMap<>();
        mapPolicyConfig.put("ADD USER DISABLED","addUserDisabled");
        mapPolicyConfig.put("ADJUST VOLUME DISABLED","adjustVolumeDisabled");
        mapPolicyConfig.put("AUTO TIME REQUIRED","autoTimeRequired");
        mapPolicyConfig.put("BLOCK APPLICATIONS ENABLED","blockApplicationsEnabled");
        mapPolicyConfig.put("BLUETOOTH CONFIG DISABLED","bluetoothConfigDisabled");
        mapPolicyConfig.put("BLUETOOTH CONTACT SHARING DISABLED","bluetoothContactSharingDisabled");
        mapPolicyConfig.put("BLUETOOTH DISABLED","bluetoothDisabled");
        mapPolicyConfig.put("CAMERA DISABLED","cameraDisabled");
        mapPolicyConfig.put("CELL BROADCASTS CONFIG DISABLED","cellBroadcastsConfigDisabled");
        mapPolicyConfig.put("CREATE WINDOWS DISABLED","createWindowsDisabled");
        mapPolicyConfig.put("CREDENTIALS CONFIG DISABLED","credentialsConfigDisabled");
        mapPolicyConfig.put("DATA ROAMING DISABLED","dataRoamingDisabled");
        mapPolicyConfig.put("DEBUGGING FEATURES ALLOWED","debuggingFeaturesAllowed");
        mapPolicyConfig.put("ENSURE VERIFY APPS ENABLED","ensureVerifyAppsEnabled");
        mapPolicyConfig.put("FACTORY RESET DISABLED","factoryResetDisabled");
        mapPolicyConfig.put("FUN DISABLED","funDisabled");
        mapPolicyConfig.put("INSTALL APPS DISABLED","installAppsDisabled");
        mapPolicyConfig.put("INSTALL UNKNOWN SOURCES ALLOWED","installUnknownSourcesAllowed");
        mapPolicyConfig.put("KEYGUARD DISABLED","keyguardDisabled");
        mapPolicyConfig.put("KIOSK CUSTOM LAUNCHER ENABLED","kioskCustomLauncherEnabled");
        mapPolicyConfig.put("MOBILE NETWORKS CONFIG DISABLED","mobileNetworksConfigDisabled");
        mapPolicyConfig.put("MODIFY ACCOUNTS DISABLED","modifyAccountsDisabled");
        mapPolicyConfig.put("MOUNT PHYSICAL MEDIA DISABLED","mountPhysicalMediaDisabled");
        mapPolicyConfig.put("NETWORK ESCAPE HATCH ENABLED","networkEscapeHatchEnabled");
        mapPolicyConfig.put("NETWORK RESET DISABLED","networkResetDisabled");
        mapPolicyConfig.put("OUTGOING BEAM DISABLED","outgoingBeamDisabled");
        mapPolicyConfig.put("OUTGOING CALLS DISABLED","outgoingCallsDisabled");
        mapPolicyConfig.put("PRIVATE KEY SELECTION ENABLED","privateKeySelectionEnabled");
        mapPolicyConfig.put("REMOVE USER DISABLED","removeUserDisabled");
        mapPolicyConfig.put("SAFE BOOT DISABLED","safeBootDisabled");
        mapPolicyConfig.put("SCREEN CAPTURE DISABLED","screenCaptureDisabled");
        mapPolicyConfig.put("SET USER ICON DISABLED","setUserIconDisabled");
        mapPolicyConfig.put("SET WALLPAPER DISABLED","setWallpaperDisabled");
        mapPolicyConfig.put("SHARE LOCATION DISABLED","shareLocationDisabled");
        mapPolicyConfig.put("SKIP FIRST USE HINTS ENABLED","skipFirstUseHintsEnabled");
        mapPolicyConfig.put("SMS DISABLED","smsDisabled");
        mapPolicyConfig.put("STATUS BAR DISABLED","statusBarDisabled");
        mapPolicyConfig.put("TETHERING CONFIG DISABLED","tetheringConfigDisabled");
        mapPolicyConfig.put("UNINSTALL APPS DISABLED","uninstallAppsDisabled");
        mapPolicyConfig.put("UNMUTE MICROPHONE DISABLED","unmuteMicrophoneDisabled");
        mapPolicyConfig.put("USB FILE TRANSFER DISABLED","usbFileTransferDisabled");
        mapPolicyConfig.put("USB MASS STORAGE ENABLED","usbMassStorageEnabled");
        mapPolicyConfig.put("VPN CONFIG DISABLED","vpnConfigDisabled");
        mapPolicyConfig.put("WIFI CONFIG DISABLED","wifiConfigDisabled");
        mapPolicyConfig.put("DEFAULT_PERMISSION_POLICY","defaultPermissionPolicy");
        mapPolicyConfig.put("APP_AUTO_UPDATE_POLICY","appAutoUpdatePolicy");
        mapPolicyConfig.put("PLAY_STORE_MODE","playStoreMode");
        mapPolicyConfig.put("SYSTEM_UPDATE","systemUpdate");


        spinnerArrayLists=new ArrayList<>();
        ArrayList<String> list=new ArrayList<>();

        //Default Permission Policy
        list.add("PERMISSION_POLICY_UNSPECIFIED");
        list.add("PROMPT");
        list.add("GRANT");
        list.add("DENY");
        spinnerArrayLists.add(list);

        ArrayList<String> list2=new ArrayList<>();
        //Auto Update Policy
        list2.add("APP_AUTO_UPDATE_POLICY_UNSPECIFIED");
        list2.add("CHOICE_TO_THE_USER");
        list2.add("NEVER");
        list2.add("WIFI_ONLY");
        list2.add("ALWAYS");
        spinnerArrayLists.add(list2);

        ArrayList<String> list3=new ArrayList<>();

        //Play Store Mode
        list3.add("PLAY_STORE_MODE_UNSPECIFIED");
        list3.add("WHITELIST");
        list3.add("BLACKLIST");
        spinnerArrayLists.add(list3);

        ArrayList<String> list4=new ArrayList<>();
        //System Update Type
        list4.add("SYSTEM_UPDATE_TYPE_UNSPECIFIED");
        list4.add("AUTOMATIC");
        list4.add("WINDOWED");
        list4.add("POSTPONE");
        spinnerArrayLists.add(list4);
    }

    public static HashMap<String, String> getMapPolicyConfig() {
        return mapPolicyConfig;
    }

    public static ArrayList<ArrayList<String>> getSpinnerArrayLists() {
        return spinnerArrayLists;
    }
}
