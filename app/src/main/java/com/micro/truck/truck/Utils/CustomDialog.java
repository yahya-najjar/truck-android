package com.micro.truck.truck.Utils;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.micro.truck.truck.R;

public class CustomDialog {

    private Dialog mDialog;

    private CustomDialog(String title, @Nullable View.OnClickListener onConfirmClickListener, @Nullable View.OnClickListener onCancelClickListener, Activity mActivity) {
        mDialog = new Dialog(mActivity);
        mDialog.setContentView(R.layout.custom_dialog);
        Button okButton = mDialog.findViewById(R.id.dialog_ok_button);
        Button cancelButton = mDialog.findViewById(R.id.dialog_cancel_button);
        TextView titleTextView = mDialog.findViewById(R.id.error_textView);
        titleTextView.setText(title);
        if (onCancelClickListener != null) {
            cancelButton.setVisibility(View.VISIBLE);
            cancelButton.setOnClickListener(onCancelClickListener);
        }
        if (onConfirmClickListener == null) {
            okButton.setOnClickListener(view -> mDialog.dismiss());
        } else {
            okButton.setOnClickListener(onConfirmClickListener);
            cancelButton.setVisibility(View.VISIBLE);
            cancelButton.setOnClickListener(view -> mDialog.dismiss());
        }

        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public static CustomDialog getInstance(String title, View.OnClickListener onConfirmClickListener, View.OnClickListener onCancelClickListener, Activity mActivity) {
        return new CustomDialog(title, onConfirmClickListener, onCancelClickListener, mActivity);
    }

    public void showDialog() {
        mDialog.show();
    }

    public void closeDialog() {
        mDialog.dismiss();
    }
}
