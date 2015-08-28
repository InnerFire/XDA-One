package com.xda.one.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

import com.xda.one.R;
import com.xda.one.ui.BaseActivity;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class UIUtils {

    public static int calculateActionBarSize(final Context context) {
        final TypedValue tv = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true);
        return TypedValue.complexToDimensionPixelSize(tv.data,
                context.getResources().getDisplayMetrics());
    }

    public static void updateEmptyViewState(final View view, final RecyclerView recyclerView,
            boolean isEmpty) {
        // Find the empty view from the main view - it's using the android id for it
        final View emptyView = view.findViewById(android.R.id.empty);

        // If we don't have any threads then simply tell the user this and quit
        if (isEmpty) {
            // Toggle what's happening with the view
            showEmptyView(recyclerView, emptyView);

            // TODO - find if there's a better way to do this
            // For now simply show the empty view ...
            emptyView.setVisibility(View.VISIBLE);
            // ... and manually remove the recyler view
            recyclerView.setVisibility(View.GONE);

            return;
        }

        // TODO - find if there's a better way to do this
        // For now simply show the recycler view ...
        recyclerView.setVisibility(View.VISIBLE);
        // ... and manually remove the loading view
        emptyView.setVisibility(View.GONE);
    }

    public static void updateEmptyViewState(final View view, final RecyclerView recyclerView,
            int itemCount) {
        updateEmptyViewState(view, recyclerView, itemCount == 0);
    }

    public static void showEmptyView(final RecyclerView recyclerView, final View emptyView) {
        recyclerView.setVisibility(GONE);
        emptyView.setVisibility(VISIBLE);

        final View linearLayout = emptyView.findViewById(R.id.empty_view);
        final View progressBar = emptyView.findViewById(R.id.empty_view_progress_bar);

        linearLayout.setVisibility(VISIBLE);
        progressBar.setVisibility(GONE);
    }

    public static void showLoadingProgress(final RecyclerView recyclerView, final View emptyView) {
        recyclerView.setVisibility(GONE);
        emptyView.setVisibility(VISIBLE);

        final View linearLayout = emptyView.findViewById(R.id.empty_view);
        final View progressBar = emptyView.findViewById(R.id.empty_view_progress_bar);

        linearLayout.setVisibility(GONE);
        progressBar.setVisibility(VISIBLE);
    }

    public static ActionBar getSupportActionBar(final Activity activity) {
        return getBaseActivity(activity).getSupportActionBar();
    }

    public static BaseActivity getBaseActivity(final Activity activity) {
        return (BaseActivity) activity;
    }

    public static void setStatusBarColor(Activity activity, int color, boolean scaleColor) {
        if (!CompatUtils.hasLollipop()) {
            return;
        }
        if (!scaleColor) {
            activity.getWindow().setStatusBarColor(color);
        } else {
            activity.getWindow().setStatusBarColor(scaleColor(color, 0.8f, false));
        }
    }

    public static int scaleColor(int color, float factor, boolean scaleAlpha) {
        return Color.argb(scaleAlpha ? (Math.round(Color.alpha(color) * factor)) : Color.alpha(color),
                Math.round(Color.red(color) * factor), Math.round(Color.green(color) * factor),
                Math.round(Color.blue(color) * factor));
    }
}