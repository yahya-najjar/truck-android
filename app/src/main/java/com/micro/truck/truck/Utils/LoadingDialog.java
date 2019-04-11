package com.micro.truck.truck.Utils;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ProgressBar;

import com.micro.truck.truck.R;


public class LoadingDialog {

    private Dialog mDialog;

    private LoadingDialog(Activity mActivity) {
        mDialog = new Dialog(mActivity);

//        Drawable draw= mActivity.getResources().getDrawable(R.drawable.custom_progressbar);
//        ProgressBar progressBar = mActivity.findViewById(R.id.my_pg);
//        progressBar.setProgressDrawable(draw);

        mDialog.setContentView(R.layout.loading_dialog_layout);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.setCancelable(false);
    }

    public static LoadingDialog getInstance(Activity mActivity) {
        return new LoadingDialog(mActivity);
    }

    public void showDialog() {
        mDialog.show();
    }

    public void closeDialog() {
        mDialog.dismiss();
    }
}
