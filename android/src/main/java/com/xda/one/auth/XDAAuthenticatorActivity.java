package com.xda.one.auth;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.xda.one.R;
import com.xda.one.util.UIUtils;

public class XDAAuthenticatorActivity extends AppCompatActivity {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frame_activity);

        UIUtils.setStatusBarColor(this, getResources().getColor(R.color.colorPrimaryDark), false);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            final String accountName = getIntent().getStringExtra(LoginFragment.ARG_ACCOUNT_NAME);
            final Fragment instance = LoginFragment.createInstance(accountName);
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_activity_content,
                    instance).commit();
        }
    }
}