package com.openailab.ailab.widget;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.openailab.ailab.R;

/**
 * @author ZyElite
 */
public class IDCardDialog extends DialogFragment {
    private String name, temperature, phone, idCard;

    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.dialog_id_card_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView mName = view.findViewById(R.id.tvName);
        TextView mTemperature = view.findViewById(R.id.tvTemperature);
        TextView mPhone = view.findViewById(R.id.tvPhone);
        TextView mIdCard = view.findViewById(R.id.tvIdCard);

        mName.setText("姓名：" + name);
        mTemperature.setText("体温：" + temperature + "℃");
        mPhone.setText("手机：" + phone);
        mIdCard.setText("身份证号码：" + idCard);
    }


    public void setInfo(String name, String temperature, String phone, String idCard) {
        this.name = name;
        this.temperature = temperature;
        this.phone = phone;
        this.idCard = idCard;
    }

}
