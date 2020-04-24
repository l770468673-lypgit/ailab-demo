package com.openailab.ailab.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.openailab.ailab.R;
import com.openailab.facelibrary.FaceAPP;

public class CardInfoActivity extends AppCompatActivity {
    private TextView nameTextView;
    private TextView sexTextView;
    private TextView nationTextView;
    private TextView validTextView;
    private TextView idNumTextView;
    private TextView orgTextView;
    private TextView addressTextView;
    private TextView birthDayTextView;
    private TextView textView_Guest_Heat;
    private ImageView headImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_info);
        initUI();
        setCardInfo();
        findViewById(R.id.button_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FaceAPP.getInstance().AddDB(getIntent().getFloatArrayExtra("face_feature"),
                        getIntent().getStringExtra("idnum"));
                finish();
            }
        });
        findViewById(R.id.button_cancel_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initUI() {
        nameTextView = (TextView) findViewById(R.id.textView_name);
        sexTextView = (TextView) findViewById(R.id.textView_sex);
        validTextView = (TextView) findViewById(R.id.textView_valid);
        addressTextView = (TextView) findViewById(R.id.textView_address);
        orgTextView = (TextView) findViewById(R.id.textView_org);
        idNumTextView = (TextView) findViewById(R.id.textView_id);
        nationTextView = (TextView) findViewById(R.id.textView_nation);
        birthDayTextView = (TextView) findViewById(R.id.textView_birthday);
        textView_Guest_Heat = (TextView) findViewById(R.id.textView_guest_heat);
        headImageView = (ImageView) findViewById(R.id.imageView_head);


    }

    private void setCardInfo() {
        Intent intent = getIntent();
        nameTextView.setText(intent.getStringExtra("uname"));
        sexTextView.setText(intent.getStringExtra("sex"));
        nationTextView.setText(intent.getStringExtra("nation"));
        addressTextView.setText(intent.getStringExtra("address"));
        validTextView.setText(intent.getStringExtra("beginTime") + " - " +
                intent.getStringExtra("endTime"));
        orgTextView.setText(intent.getStringExtra("signingOrganization"));
        idNumTextView.setText(intent.getStringExtra("idnum"));
        birthDayTextView.setText(intent.getStringExtra("birthDate"));
        Log.d("Constraints","guest_heat==="+getIntent().getExtras().getFloat("guest_heat"));

        textView_Guest_Heat.setText(intent.getExtras().getFloat("guest_heat") + "");
        Bitmap bitmap = base64ToBitmap(intent.getStringExtra("strheadpic"));
        headImageView.setImageBitmap(bitmap);
    }

    public static Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
