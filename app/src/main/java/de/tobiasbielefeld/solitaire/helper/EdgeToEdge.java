package de.tobiasbielefeld.solitaire.helper;

import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowInsets;

public class EdgeToEdge {

    private EdgeToEdge() {
    }

    public static void applyContentInsets(final Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            return;
        }

        final View decor = activity.getWindow().getDecorView();

        decor.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                View content = activity.findViewById(android.R.id.content);
                WindowInsets insets = decor.getRootWindowInsets();

                if (content == null || insets == null) {
                    return;
                }

                int left = insets.getSystemWindowInsetLeft();
                int top = insets.getSystemWindowInsetTop();
                int right = insets.getSystemWindowInsetRight();
                int bottom = insets.getSystemWindowInsetBottom();

                if (content.getPaddingLeft() != left
                        || content.getPaddingTop() != top
                        || content.getPaddingRight() != right
                        || content.getPaddingBottom() != bottom) {
                    content.setPadding(left, top, right, bottom);
                }
            }
        });
    }
}
