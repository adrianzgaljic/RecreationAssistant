package com.jularic.dominik.recreationassistant.adapters;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.jularic.dominik.recreationassistant.R;

/**
 * Created by Dominik on 6.1.2017..
 */

public class Divider extends RecyclerView.ItemDecoration {

    public static final String TAG = "Dominik";
    private Drawable mDivider;
    private  int mOrientation;


    public Divider(Context context, int orientation){
        mDivider = ContextCompat.getDrawable(context, R.drawable.divider );
        if(orientation != LinearLayoutManager.VERTICAL){
            throw new IllegalArgumentException("This Item Decoration can be used only with a RecyclerView that uses LinearLayoutManager with vertical orientation");

        }
        mOrientation = orientation;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if(mOrientation == LinearLayoutManager.VERTICAL){
            drawHorizontalDivider(c, parent, state);
        }
    }

    private void drawHorizontalDivider(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left, top, right, bottom;
        left = parent.getPaddingLeft();
        right = parent.getWidth() - parent.getPaddingRight();
        int count = parent.getChildCount();
        //i is position in the recycler view
        for(int i = 0; i<count; i++){
            if(AdapterMyEvents.FOOTER != parent.getAdapter().getItemViewType(i)){
                View current = parent.getChildAt(i);
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) current.getLayoutParams();
                top = current.getBottom() - params.bottomMargin;
                bottom = top + mDivider.getIntrinsicHeight();
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
                Log.d(TAG, "drawHorizontalDivider: l: "+left + "t: " + top +"r: " +right + "b: " +bottom);

            }
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if(mOrientation== LinearLayoutManager.VERTICAL){
            outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
        }
    }
}
