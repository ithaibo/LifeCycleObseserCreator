package com.howbuy.loginstateobsever;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.howbuy.login.LoginManagerImpl;
import com.howbuy.login.LoginObserver;
import com.howbuy.login.LogoutObserver;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LoginManagerImpl.getInstance()
                .addLoginObserver(
                        new LoginObserver() {
                            @Override
                            public void onLogin() {
                                Toast.makeText(getApplication(), "login", Toast.LENGTH_SHORT).show();
                            }
                        }, MainActivity.this);

        LoginManagerImpl.getInstance()
                .addLogoutObserver(new LogoutObserver() {
                    @Override
                    public void onLogout() {
                        Toast.makeText(getApplication(), "logout", Toast.LENGTH_SHORT).show();
                    }
                }, MainActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LoginManagerImpl.getInstance().publishLoginState(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LoginManagerImpl.getInstance().publishLoginState(false);
    }
}
