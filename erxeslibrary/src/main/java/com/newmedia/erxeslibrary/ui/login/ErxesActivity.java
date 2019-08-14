package com.newmedia.erxeslibrary.ui.login;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.newmedia.erxeslibrary.DataManager;
import com.newmedia.erxeslibrary.configuration.Config;
import com.newmedia.erxeslibrary.helper.Helper;
import com.newmedia.erxeslibrary.configuration.ReturnType;
import com.newmedia.erxeslibrary.configuration.ErxesRequest;
import com.newmedia.erxeslibrary.ui.conversations.ConversationListActivity;
import com.newmedia.erxeslibrary.ErxesObserver;
import com.newmedia.erxeslibrary.R;

public class ErxesActivity extends AppCompatActivity implements ErxesObserver {

    private EditText email, phone;
    private TextView sms_button, email_button;
    private LinearLayout container;
    private ImageView mailzurag, phonezurag;
    private CardView mailgroup, smsgroup;
    private Config config;
    private ErxesRequest erxesRequest;
    private DataManager dataManager;
    private LinearLayout loaderView;
    private int colorCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataManager = DataManager.getInstance(this);
        config = Config.getInstance(this);
        erxesRequest = ErxesRequest.getInstance(config);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_erxes);

        loaderView = this.findViewById(R.id.loaderView);
        email = this.findViewById(R.id.email);
        phone = this.findViewById(R.id.phone);
        container = findViewById(R.id.linearlayout);
        sms_button = this.findViewById(R.id.sms_button);
        email_button = this.findViewById(R.id.email_button);
        mailgroup = this.findViewById(R.id.mailgroup);
        smsgroup = this.findViewById(R.id.smsgroup);
        mailzurag = this.findViewById(R.id.mail_zurag);
        phonezurag = this.findViewById(R.id.phonezurag);
        colorCode = config.getState().getUiOptions().getColor();
        Helper.display_configure(this, container, "#66000000");
        this.findViewById(R.id.logout).setOnTouchListener(touchListener);
        change_color();

        boolean hasData = getIntent().getBooleanExtra("hasData",false);
        String customData = getIntent().getStringExtra("customData");
        String mEmail = getIntent().getStringExtra("mEmail");
        String mPhone = getIntent().getStringExtra("mPhone");
        if (hasData) {
            erxesRequest.setConnect(mEmail, mPhone, true, customData);
        } else {
            if (config.isLoggedIn()) {
                config.LoadDefaultValues();
                Intent a = new Intent(ErxesActivity.this, ConversationListActivity.class);
                a.putExtra("isFromLogin",false);
                ErxesActivity.this.startActivity(a);
                finish();
            } else {
                erxesRequest.getIntegration();
            }
        }
    }

    private void change_color() {
        this.findViewById(R.id.info_header).setBackgroundColor(colorCode);
        mailgroup.setCardBackgroundColor(colorCode);
        sms_button.setTextColor(colorCode);
        changeBitmapColor(phonezurag, colorCode);

        Drawable drawable = this.findViewById(R.id.selector).getBackground();
        drawable.setColorFilter(colorCode, PorterDuff.Mode.SRC_ATOP);

    }

    public void email_click(View v) {
        email.setVisibility(View.VISIBLE);
        phone.setVisibility(View.GONE);
        /////
        smsgroup.setCardBackgroundColor(Color.WHITE);
        email_button.setTextColor(Color.WHITE);
        changeBitmapColor(mailzurag, Color.WHITE);
        sms_button.setTextColor(colorCode);
        ((CardView) v).setCardBackgroundColor(colorCode);
        changeBitmapColor(phonezurag, colorCode);

    }

    public void changeBitmapColor(ImageView image, int color) {
        image.getDrawable().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
    }

    public void sms_click(View v) {

        email.setVisibility(View.GONE);
        phone.setVisibility(View.VISIBLE);

        mailgroup.setCardBackgroundColor(Color.WHITE);
        sms_button.setTextColor(Color.WHITE);
        changeBitmapColor(phonezurag, Color.WHITE);

        email_button.setTextColor(colorCode);
        ((CardView) v).setCardBackgroundColor(colorCode);
        changeBitmapColor(mailzurag, colorCode);

    }

    public void logout(View v) {
        this.finish();
    }

    public void Connect_click(View v) {
        if (config.isNetworkConnected()) {
            if (email.getVisibility() == View.GONE) {
                if (phone.getText().toString().length() > 7) {
                    dataManager.setData(DataManager.phone, phone.getText().toString());
                    erxesRequest.setConnect("", phone.getText().toString(), false,null);
                    phone.setError(null);
                } else
                    phone.setError(getResources().getString(R.string.no_correct_phone));
            } else {
                if (isValidEmail(email.getText().toString())) {
                    email.setError(null);
                    dataManager.setData(DataManager.email, email.getText().toString());
                    erxesRequest.setConnect("" + email.getText().toString(), "", false,null);
                } else
                    email.setError(getResources().getString(R.string.no_correct_mail));
            }
        } else {
            Snackbar.make(container, R.string.cantconnect, Snackbar.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        erxesRequest.remove(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        erxesRequest.add(this);
    }

    @Override
    public void notify(final int returnType, String conversationId, final String message) {

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (returnType) {
                    case ReturnType.LOGIN_SUCCESS:
                        Intent a = new Intent(ErxesActivity.this, ConversationListActivity.class);
                        a.putExtra("isFromLogin",true);
                        ErxesActivity.this.startActivity(a);
                        finish();
                        break;

                    case ReturnType.INTEGRATION_CHANGED:
                        change_color();
                        loaderView.setVisibility(View.GONE);
                        break;

                    case ReturnType.CONNECTIONFAILED:
                        Snackbar.make(container, R.string.cantconnect, Snackbar.LENGTH_SHORT).show();
                        break;

                    case ReturnType.SERVERERROR:
                        Snackbar.make(container, message, Snackbar.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        });


    }

    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(final View v, MotionEvent event) {

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                ErxesActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        v.setBackgroundResource(R.drawable.action_background);
                    }
                });
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                ErxesActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        v.setBackgroundColor(Color.parseColor("#00000000"));
                        if (v.getId() == R.id.logout)
                            logout(null);
                    }
                });
            }
            return true;
        }
    };

    public final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }
}
