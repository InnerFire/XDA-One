package com.xda.one.ui;

import com.xda.one.R;
import com.xda.one.util.UIUtils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class UserProfileActivity extends BaseActivity {

    public static final String USER_ID_ARGUMENT = "user_id";

    private final String SCREEN_NAME = "ViewMessageActivity";

    public static Intent createIntent(final Context context, final String userId) {
        final Intent intent = new Intent(context, UserProfileActivity.class);
        intent.putExtra(USER_ID_ARGUMENT, userId);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
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

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getResources().getString(R.string.about));
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            final String userId = getIntent().getStringExtra(USER_ID_ARGUMENT);
            final Fragment fragment = UserProfileFragment.createInstance(userId);
            final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_activity_content, fragment).commit();
        }
    }
}