
package com.iresearch.android.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;
import java.util.HashMap;
import java.util.Map;

/**
 * Memo'ized typefaces loaded from the assets directory
 */
public class TypefaceUtils {

    private static final Map<String, Typeface> TYPEFACES = new HashMap<String, Typeface>(4);

    /**
     * Get typeface with name
     * 
     * @param name
     * @param view
     * @return typeface, either cached or loaded from the assets
     */
    public static Typeface getTypeface(final String name, final View view) {
        return getTypeface(name, view.getContext());
    }

    /**
     * Get typeface with name
     * 
     * @param name
     * @param context
     * @return typeface, either cached or loaded from the assets
     */
    public static Typeface getTypeface(final String name, final Context context) {
        Typeface typeface = TYPEFACES.get(name);
        if (typeface == null) {
            typeface = Typeface.createFromAsset(context.getAssets(), name);
            TYPEFACES.put(name, typeface);
        }
        return typeface;
    }

    /**
     * Set typeface with name on given text view
     * 
     * @param name
     * @param view
     * @return view
     */
    public static <V extends TextView> V setTypeface(final String name, final V view) {
        if (view != null)
            view.setTypeface(getTypeface(name, view));
        return view;
    }

    /**
     * Set typeface with name on given text views
     * 
     * @param name
     * @param views
     */
    public static void setTypeface(final String name, final TextView... views) {
        if (views == null || views.length == 0)
            return;

        Typeface typeface = getTypeface(name, views[0]);
        for (TextView view : views)
            view.setTypeface(typeface);
    }
}
