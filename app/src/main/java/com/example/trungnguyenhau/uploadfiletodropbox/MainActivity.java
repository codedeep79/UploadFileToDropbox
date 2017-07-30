package com.example.trungnguyenhau.uploadfiletodropbox;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session;
import com.dropbox.client2.session.TokenPair;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnLogin, btnUpload, btnListFile;
    private boolean isUserLogin = false;
    private DropboxAPI dropboxAPI;
    private LinearLayout linearLayout;

    private final static String DROPBOX_FILE_DIR = "/DropboxDemo/";
    private final static String DROPBOX_NAME = "dropbox_prefs";
    private final static String ACCESS_KEY = "z5nj0wdflqqxtvp";
    private final static String ACCESS_SECRET = "xin9ccbin8j8qdx";

    private final static Session.AccessType ACCESS_TYPE = Session.AccessType.DROPBOX;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addControls();
        addEvents();
    }

    private void addControls() {
        btnLogin    = (Button) findViewById(R.id.button_login);
        btnLogin.setOnClickListener(this);
        btnUpload   = (Button) findViewById(R.id.button_uploadfile);
        btnUpload.setOnClickListener(this);
        btnListFile = (Button) findViewById(R.id.button_listfile);
        btnListFile.setOnClickListener(this);

        linearLayout = (LinearLayout) findViewById(R.id.linearlayout_container_file);
        loggedIn(false);

        AppKeyPair appKeyPair = new AppKeyPair(ACCESS_KEY, ACCESS_SECRET);
        AndroidAuthSession session;

        SharedPreferences prefs = getSharedPreferences(DROPBOX_NAME, 0);
        String key = prefs.getString(ACCESS_KEY, null);
        String secret = prefs.getString(ACCESS_SECRET, null);

        if(key != null && secret != null) {
            AccessTokenPair token = new AccessTokenPair(key, secret);
            session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE, token);
        } else {
            session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE);
        }

        dropboxAPI = new DropboxAPI(session);

    }

    // Xác thực đăng nhập dropbox
    @Override
    protected void onResume() {
        super.onResume();
        AndroidAuthSession session  = (AndroidAuthSession) dropboxAPI.getSession();
        if (session.authenticationSuccessful())
        {
            try{
                // Đăng nhập thành công thì kết thúc xác thực
                session.finishAuthentication();

                TokenPair tokens = session.getAccessTokenPair();
                SharedPreferences prefs = getSharedPreferences(DROPBOX_NAME, 0);
                Editor editor = prefs.edit();
                editor.putString(ACCESS_KEY, tokens.key);
                editor.putString(ACCESS_SECRET, tokens.secret);
                editor.commit();

                loggedIn(true);
            }catch (IllegalStateException e)
            {
                Toast.makeText(MainActivity.this, "Error during dropbox auth", Toast.LENGTH_LONG).show();
            }
        }

    }



    private void addEvents() {

    }


    private void loggedIn(boolean userLoggedIn) {
        isUserLogin = userLoggedIn;
        btnUpload.setEnabled(userLoggedIn);
        btnUpload.setBackgroundColor(userLoggedIn ? Color.BLUE : Color.GREEN);
        btnListFile.setEnabled(userLoggedIn);
        btnListFile.setBackgroundColor(userLoggedIn ? Color.BLUE : Color.GREEN);
        btnLogin.setText(userLoggedIn ? "Logout" : "Log in");
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.button_login:
                if (isUserLogin)
                {
                    // Nếu đã có user đăng nhập thì xóa session. Nó sẽ nhận user mới
                    dropboxAPI.getSession().unlink();
                    loggedIn(false);
                }
                else
                {
                    ((AndroidAuthSession)dropboxAPI.getSession())
                            .startAuthentication(MainActivity.this);
                }
                break;

            case R.id.button_uploadfile:
                UploadFile uploadFile = new UploadFile(this, dropboxAPI, DROPBOX_FILE_DIR);
                uploadFile.execute();
                break;

            case R.id.button_listfile:
                ListFile listFile = new ListFile(dropboxAPI, DROPBOX_FILE_DIR, handler);
                listFile.execute();
                break;
            default:
                break;
        }
    }

    private final Handler handler = new Handler(){
        // Ồ đã nhận Message => vậy là Thread đã thực thi xong
        @Override
        public void handleMessage(Message msg) {
            ArrayList<String> result = msg.getData().getStringArrayList("data");
            for (String filename: result)
            {
                TextView txtView = new TextView(MainActivity.this);
                txtView.setText(filename);
                linearLayout.addView(txtView);
            }

        }
    };
}
