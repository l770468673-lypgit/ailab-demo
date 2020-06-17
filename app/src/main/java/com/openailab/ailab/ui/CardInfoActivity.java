package com.openailab.ailab.ui;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.openailab.ailab.DaoManager;
import com.openailab.ailab.DaoSession;
import com.openailab.ailab.R;
import com.openailab.ailab.UserInfosDao;
import com.openailab.ailab.dao.UserInfos;
import com.openailab.facelibrary.FaceAPP;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CardInfoActivity extends AppCompatActivity {
    private String TAG = "Constraints";
    private TextView nameTextView;
    private TextView editTextphone;
    private TextView sexTextView;
    private TextView nationTextView;
    private TextView validTextView;
    private TextView idNumTextView;
    private TextView orgTextView;
    private TextView addressTextView;
    private TextView birthDayTextView;
    private TextView textView_Guest_Heat;
    private TextView textView_date_time;
    private ImageView headImageView;
    private Spinner mSpinner8;
    String[] ringlist = {"111111", "22222", "33333", "44444", "5555555"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_info);
        initUI();
        setCardInfo();
        findViewById(R.id.button_register).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                FaceAPP.getInstance().AddDB(getIntent().getFloatArrayExtra("face_feature"),
                        getIntent().getStringExtra("idnum"));
                //
                DaoSession daoSession = DaoManager.getDaoSession();

//                UserInfos idnum = daoSession.queryBuilder(UserInfos.class).where(UserInfosDao.Properties._userid.eq(getIntent().getStringExtra("idnum"))).build().unique();
//                if (idnum != null) {
//                    Log.d(TAG, "idnum != null" + idnum.get_userid().toString());
//                } else {
//                    Log.d(TAG, "idnum==== null" + idnum.get_userid().toString());
//                }
                UserInfos userInfos = new UserInfos(getIntent().getStringExtra("idnum"), "llp",
                        "123345", "男"
                        , getNowDate(), getIntent().getExtras().getFloat("guest_heat") + "");

                daoSession.insertOrReplace(userInfos);

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
        editTextphone = (TextView) findViewById(R.id.editTextphone);
        textView_date_time = (TextView) findViewById(R.id.textView_date_time);
        sexTextView = (TextView) findViewById(R.id.textView_sex);
        validTextView = (TextView) findViewById(R.id.textView_valid);
        addressTextView = (TextView) findViewById(R.id.textView_address);
        orgTextView = (TextView) findViewById(R.id.textView_org);
        idNumTextView = (TextView) findViewById(R.id.textView_id);
        nationTextView = (TextView) findViewById(R.id.textView_nation);
        birthDayTextView = (TextView) findViewById(R.id.textView_birthday);
        textView_Guest_Heat = (TextView) findViewById(R.id.textView_guest_heat);
        headImageView = (ImageView) findViewById(R.id.imageView_head);
        mSpinner8 = (Spinner) findViewById(R.id.Spinner8);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_singlechoice, ringlist);

        adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        mSpinner8.setAdapter(adapter);
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
        textView_date_time.setText(getNowDate());
        Log.d("Constraints", "guest_heat===" + getIntent().getExtras().getFloat("guest_heat") + "℃");

        textView_Guest_Heat.setText(intent.getExtras().getFloat("guest_heat") + "");
        Bitmap bitmap = base64ToBitmap(intent.getStringExtra("strheadpic"));
        headImageView.setImageBitmap(bitmap);
        getNowDate();
    }

    private String getNowDate() {
        Date date = new Date();

        String time = date.toLocaleString();

        Log.i("md", "时间time为： " + time);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年-MM月dd日-HH时mm分 E");

        String sim = dateFormat.format(date);


        Log.i("md", "时间sim为： " + sim);
        return sim;
    }


    public static Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
