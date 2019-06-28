package com.daivansh.androidmanagementmdmjava.utils;

import android.app.ProgressDialog;
import android.content.Context;

import com.daivansh.androidmanagementmdmjava.R;

public class ProgressDialogHelper {
    private static ProgressDialog progressDialog;

    public static void showProgressDialog(Context context) {
        try {
            progressDialog = new ProgressDialog(context);
            if (!progressDialog.isShowing()) {
                progressDialog.setMessage(context.getString(R.string.loading));
                progressDialog.setCancelable(false);
                progressDialog.show();
            }
        } catch (Throwable ignore) {
        }
    }

    public static void hideProgressDialog() {
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (Throwable ignore) {
        }
    }
}
