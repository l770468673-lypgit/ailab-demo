package com.openailab.ailab.widget;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.util.Consumer;
import androidx.fragment.app.DialogFragment;

import com.openailab.ailab.R;

/**
 * @author ZyElite
 */
public class InfoDialog extends DialogFragment {
    private Consumer<String> mConsumer;

    private CancelListener mCancelListener;

    @NonNull
    @Override

    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_info_layout, null);
        EditText phone = view.findViewById(R.id.phone);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("请输入手机号");
        builder.setView(view);
        builder.setCancelable(true);
        builder.setPositiveButton("确定", null);
        builder.setNegativeButton("取消", (dialog, which) -> {
            if (mCancelListener != null) {
                mCancelListener.onCancel();
            }
        });
        AlertDialog adialog = builder.create();

        adialog.setOnShowListener(dialog -> adialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNum = phone.getText().toString();
                if (!TextUtils.isEmpty(phoneNum) && mConsumer != null) {
                    mConsumer.accept(phoneNum);
                    adialog.dismiss();
                } else {
                    Toast.makeText(getContext(), "请输入手机号", Toast.LENGTH_SHORT).show();
                }
            }
        }));

        return adialog;

    }

    public void setEnterClickListener(Consumer<String> mConsumer) {
        this.mConsumer = mConsumer;
    }


    public void setCancelListener(CancelListener onListener) {
        mCancelListener = onListener;
    }


    public interface CancelListener {
        void onCancel();
    }
}
