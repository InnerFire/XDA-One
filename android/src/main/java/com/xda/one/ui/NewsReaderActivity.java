package com.xda.one.ui;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xda.one.R;

import com.xda.one.parser.ContentParser;
import com.xda.one.parser.TextDataStructure;
import com.xda.one.ui.widget.ObservableScrollView;
import com.xda.one.util.SectionUtils;
import com.xda.one.util.UIUtils;

public class NewsReaderActivity extends AppCompatActivity implements ObservableScrollView.Callbacks {

    private ObservableScrollView mScrollView;

    int paletteColor;
    private ImageView headerImageView;

    private TextView titleView;

    private FrameLayout mImageFrameLayout;

    private Toolbar mToolbar;

    private LayoutInflater mLayoutInflater;

    private LinearLayout mToolbarLinearLayout;

    private LinearLayout mContentLinearLayout;

    private String news_title;

    private String news_content;

    private String news_url;

    private String news_image_url;

    public NewsReaderActivity() {

    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.news_reader_activity);

        mToolbar = (Toolbar) findViewById(R.id.news_toolbar);
        setSupportActionBar(mToolbar);

        mScrollView = (ObservableScrollView) findViewById(R.id.notify_scroll_view);

        mImageFrameLayout = (FrameLayout) findViewById(R.id.image_frame_layout);
        mToolbarLinearLayout = (LinearLayout) findViewById(R.id.toolbar_linear_layout);

        mContentLinearLayout = (LinearLayout) findViewById(R.id.content_linear_layout);

        titleView = (TextView) findViewById(R.id.news_title);
        headerImageView = (ImageView) findViewById(R.id.news_image_view);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(null);
            ab.setDisplayHomeAsUpEnabled(true);
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            news_title = extras.getString("NEWS_TITLE");
            news_content = extras.getString("NEWS_CONTENT");
            news_url = extras.getString("NEWS_URL");
            news_image_url = extras.getString("NEWS_IMAGE_URL");

            UIUtils.setStatusBarColor(this, getResources().getColor(R.color.colorPrimaryDark), false);

            Picasso.with(this)
                    .load(news_image_url).fit().centerCrop()
                    .placeholder(R.drawable.ic_image_placeholder)
                    .error(R.drawable.ic_image_placeholder)
                    .into(headerImageView, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            Palette.from(((BitmapDrawable) headerImageView.getDrawable()).getBitmap()).maximumColorCount(6).generate(new Palette.PaletteAsyncListener() {
                                public void onGenerated(Palette p) {
                                    int vibrantColor = p.getVibrantColor(-1);
                                    if (vibrantColor == -1) {
                                        paletteColor = p.getMutedColor(getResources().getColor(R.color.colorPrimary));
                                    } else {
                                        paletteColor = vibrantColor;
                                    }
                                    mToolbarLinearLayout.setBackgroundColor(paletteColor);
                                    UIUtils.setStatusBarColor(NewsReaderActivity.this, paletteColor, true);
                                }
                            });
                        }
                        @Override
                        public void onError() {

                        }
                    });

            titleView.setText(news_title);

            final Spannable formattedContent = ContentParser.parseAndSmilifyBBCode(this,
                    news_content);
            final TextDataStructure structure = new TextDataStructure(formattedContent);

            mLayoutInflater = LayoutInflater.from(NewsReaderActivity.this);
            SectionUtils.setupSections(this, mLayoutInflater, mContentLinearLayout, structure,
                    null);

            setupScrollView();

            // check if the window should float
            if (getResources().getBoolean(R.bool.isTablet))
                setupFloatingWindow();
        }
    }

    private void setupFloatingWindow() {
        // configure this Activity as a floating window, dimming the background
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = getResources().getDimensionPixelSize(R.dimen.news_reader_floating_width);
        params.height = getResources().getDimensionPixelSize(R.dimen.news_reader_floating_height);
        params.alpha = 1;
        params.dimAmount = 0.6f;
        params.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        this.getWindow().setAttributes(params);
    }

    private void setupScrollView() {
        mScrollView.addCallbacks(this);

        ViewTreeObserver viewTreeObserver = mScrollView.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    // get size
                    int toolbarLinearLayoutHeight = mToolbarLinearLayout.getHeight();
                    int imageHeight = headerImageView.getHeight();

                    // adjust image frame layout height
                    ViewGroup.LayoutParams layoutParams = mImageFrameLayout.getLayoutParams();
                    if (layoutParams.height != imageHeight) {
                        layoutParams.height = imageHeight;
                        mImageFrameLayout.setLayoutParams(layoutParams);
                    }

                    // adjust top margin of content linear layout
                    ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) mContentLinearLayout.getLayoutParams();
                    if (marginLayoutParams.topMargin != toolbarLinearLayoutHeight + imageHeight) {
                        marginLayoutParams.topMargin = toolbarLinearLayoutHeight + imageHeight;
                        mContentLinearLayout.setLayoutParams(marginLayoutParams);
                    }

                    // call onScrollChange to update initial properties.
                    onScrollChanged(0, 0);
                }
            });
        }
    }

    @Override
    public void onScrollChanged(int deltaX, int deltaY)  {
        // get scroll y
        int scrollY = mScrollView.getScrollY();

        // choose appropriate y
        float newY = Math.max(headerImageView.getHeight(), scrollY);

        ViewCompat.setTranslationY(mToolbarLinearLayout, newY);
        ViewCompat.setTranslationY(mImageFrameLayout, scrollY * 0.5f);
    }

    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.news_reader_activity, container, false);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.news_reader_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.news_share:
                Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                        .setSubject(news_title)
                        .setText(news_url + "\n\n" + getResources().getText(R.string.share_subtext))
                        .setType("text/plain")
                        .getIntent();
                startActivity(shareIntent);
                return true;
            case R.id.news_open_browser:
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(news_url));
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}