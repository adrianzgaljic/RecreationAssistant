package com.jularic.dominik.recreationassistant.extras;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;

import java.util.List;

/**
 * Created by Dominik on 6.1.2017..
 */

public class Util {

    public static void showViews(List<View> views){
        for (View view : views){
            view.setVisibility(View.VISIBLE);
        }
    }

    public static void hideViews(List<View> views){
        for(View view : views ){
            view.setVisibility(View.GONE);
        }
    }

    public static boolean moreThanJellyBean(){
        return Build.VERSION.SDK_INT > 15;
    }

    public static void setBackground(View mItemView, Drawable drawable) {
        if(moreThanJellyBean()){
            mItemView.setBackground(drawable);
        } else{
            mItemView.setBackgroundDrawable(drawable);
        }
    }
}
