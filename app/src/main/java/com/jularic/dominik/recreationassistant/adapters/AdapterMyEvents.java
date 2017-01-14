package com.jularic.dominik.recreationassistant.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jularic.dominik.recreationassistant.MyEvents;
import com.jularic.dominik.recreationassistant.R;
import com.jularic.dominik.recreationassistant.RecreationAssistantApp;
import com.jularic.dominik.recreationassistant.beans.Event;
import com.jularic.dominik.recreationassistant.extras.Util;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Dominik on 6.1.2017..
 */

public class AdapterMyEvents extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements SwipeListener {

    public static final int COUNT_FOOTER = 1;
    public static final int COUNT_NO_ITEMS = 1;
    public static final int ITEM = 0;
    public static final int NO_ITEM = 1;
    public static final int  FOOTER = 2;
    private final ResetListener mResetListener;
    private MarkListener mMarkListener;

    private LayoutInflater mInflater;
    private RealmResults<Event> mResults;
    private Realm mRealm;
    //we need this so we can manage the onClick method in the footer
    private AddListener mAddListener;
    private int mFilterOption;
    private Context mContext;


    public AdapterMyEvents(Context context, Realm realm,  RealmResults<Event> results, AddListener listener, MarkListener markListener, ResetListener resetListener){
        mContext = context;
        mInflater = LayoutInflater.from(context);
        update(results);
        mRealm = realm;
        mAddListener = listener;
        mMarkListener = markListener;
        mResetListener = resetListener;
    }


    @Override
    public long getItemId(int position) {
        if(position< mResults.size()){
            return mResults.get(position).getDateAdded();
        }
        return RecyclerView.NO_ID;
    }

    public void update(RealmResults<Event> results){
        mResults = results;
        mFilterOption = RecreationAssistantApp.load(mContext);
        notifyDataSetChanged();
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if(viewType == FOOTER){
            View view = mInflater.inflate(R.layout.rv_my_events_footer,parent, false);
            return new MyEventFooterHolder(view, mAddListener);
        }
        else if(viewType == NO_ITEM){
            View view = mInflater.inflate(R.layout.no_item, parent, false);
            return new MyEventNoItemsHolder(view);
        } else{
            View view = mInflater.inflate(R.layout.row_my_event,parent, false);
            return new MyEventHolder(view, mMarkListener);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if(holder instanceof MyEventHolder){
            MyEventHolder myEventHolder = (MyEventHolder) holder;
            Event event = mResults.get(position);
            //myEventHolder.mTextName.setText(event.getName());
            myEventHolder.setName(event.getName());
            myEventHolder.setDate(event.getDateOfEvent());
            myEventHolder.setBackground(event.isCompleted());
        }

    }

    @Override
    public int getItemViewType(int position) {
       if(!mResults.isEmpty()){
           if(position<mResults.size()){
               return ITEM;
           }else {
               return FOOTER;
           }
       } else {
           if(mFilterOption == Filter.COMPLETE || mFilterOption == Filter.INCOMPLETE){
               if(position == 0){
                   return NO_ITEM;
               }else {
                   return FOOTER;
               }
           }else {
               return ITEM;
           }

        }

    };

    //+1 is to show footer in the recyclerView
    @Override
    public int getItemCount() {

        if(!mResults.isEmpty()){
            return mResults.size() + COUNT_FOOTER;
        } else{
            if(mFilterOption == Filter.LEAST_TIME_LEFT
                    || mFilterOption == Filter.MOST_TIME_LEFT
                    || mFilterOption == Filter.NONE){
                return 0;
            } else {
                return COUNT_NO_ITEMS + COUNT_FOOTER;
            }

        }

    }

    @Override
    public void onSwipe(int position) {
        if(position < mResults.size()){
            mRealm.beginTransaction();
            //mResults.removeChangeListeners();
            //mRealm.remove();
            //mResults.remove(position);
            //mResults.get(position).remove();
            //mResults.get(position).deleteAllFromRealm()
            //mResults.get(position).deleteAllFromRealm();
            mResults.get(position).deleteFromRealm();
            //Event event = mResults.get(position);
            //event.deleteFromRealm();
            //mResults.deleteFromRealm();
            mRealm.commitTransaction();
            notifyItemRemoved(position);
        }
        resetFilterIfEmpty();

    }

    private void resetFilterIfEmpty() {
        if( mResults.isEmpty() && (mFilterOption == Filter.COMPLETE ||
                mFilterOption == Filter.INCOMPLETE)){
            mResetListener.onReset();
        }
    }

    public void markComplete(int position) {
        if(position < mResults.size()){
            mRealm.beginTransaction();
            mResults.get(position).setCompleted(true);
            mRealm.commitTransaction();
            notifyItemChanged(position);
        }

    }

    public static class MyEventHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mTextName;
        TextView mTextDateOfEvent;
        MarkListener mMarkListener;
        Context mContext;
        View mItemView;

        public MyEventHolder(View itemView, MarkListener listener) {
            super(itemView);
            mItemView = itemView;
            mContext = itemView.getContext();
            itemView.setOnClickListener(this);
            mTextName = (TextView) itemView.findViewById(R.id.tv_event_name);
            mTextDateOfEvent = (TextView) itemView.findViewById(R.id.tv_date_of_event);
            mMarkListener = listener;
        }

        public void setName(String name){
            mTextName.setText(name);
        }

        @Override
        public void onClick(View v) {
            mMarkListener.onMark(getAdapterPosition());
        }

        public void setBackground(boolean completed) {
            Drawable drawable;
            if(completed){
                drawable = ContextCompat.getDrawable(mContext, R.color.bg_event_complete);
            } else {
                drawable = ContextCompat.getDrawable(mContext, R.drawable.bg_row_my_event);
            }

            Util.setBackground(mItemView, drawable);

        }

        public void setDate(long dateOfEvent) {
            mTextDateOfEvent.setText(DateUtils.getRelativeTimeSpanString(dateOfEvent, System.currentTimeMillis(),DateUtils.DAY_IN_MILLIS,DateUtils.FORMAT_ABBREV_ALL));
        }
    }

    public static class MyEventNoItemsHolder extends RecyclerView.ViewHolder{

        public MyEventNoItemsHolder(View itemView) {
            super(itemView);
        }
    }

    public static class MyEventFooterHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        Button mBtnAdd;
        AddListener mListener;

        public MyEventFooterHolder(View itemView) {
            super(itemView);
            mBtnAdd = (Button) itemView.findViewById(R.id.btn_my_events_footer);
            mBtnAdd.setOnClickListener(this);
        }

        public MyEventFooterHolder(View itemView, AddListener listener) {
            super(itemView);
            mBtnAdd = (Button) itemView.findViewById(R.id.btn_my_events_footer);
            mBtnAdd.setOnClickListener(this);
            mListener = listener;
        }

        @Override
        public void onClick(View v) {
            mListener.add();
        }


    }

}
