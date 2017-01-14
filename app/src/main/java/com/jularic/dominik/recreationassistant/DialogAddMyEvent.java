package com.jularic.dominik.recreationassistant;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.jularic.dominik.recreationassistant.beans.Event;

import java.util.Calendar;

import io.realm.Realm;

public class DialogAddMyEvent extends DialogFragment {

    private ImageButton imgBtnAddEventClose;
    private EditText mEtAddEvent;
    private DatePicker mDpAddEvent;
    private Button mBtnAddEvent;
    private Realm realm;

    private View.OnClickListener btnAddEventCloseClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            
            switch (id){
                case R.id.btn_add_my_event:
                    addAction();
                    break;
            }
            dismiss();
        }
    };

    //TODO process date
    private void addAction() {
        //get the value of the event
        //get the time when it was added
        String dialogData = mEtAddEvent.getText().toString();
        String date = mDpAddEvent.getDayOfMonth() + "/" + mDpAddEvent.getMonth() + "/" + mDpAddEvent.getYear();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH,mDpAddEvent.getDayOfMonth() );
        calendar.set(Calendar.MONTH,mDpAddEvent.getMonth() );
        calendar.set(Calendar.YEAR, mDpAddEvent.getYear());
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        long currentTime = System.currentTimeMillis();
        //RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().build();
        Realm realm = Realm.getDefaultInstance();
        Event event = new Event(dialogData, currentTime, calendar.getTimeInMillis(), false);
        realm.beginTransaction();
        realm.copyToRealm(event);
        realm.commitTransaction();
        realm.close();
    }

    public DialogAddMyEvent() {
    }
/*
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        realm.close();
    }
*/

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_add_my_event, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imgBtnAddEventClose = (ImageButton) view.findViewById(R.id.ib_add_my_events_close);
        mEtAddEvent = (EditText) view.findViewById(R.id.et_add_my_event);
        mDpAddEvent = (DatePicker) view.findViewById(R.id.dp_my_event);
        mBtnAddEvent = (Button) view.findViewById(R.id.btn_add_my_event);

        //using same listner for adding new event and closing because after adding new event
        // you need to close the dialog
        imgBtnAddEventClose.setOnClickListener(btnAddEventCloseClickListener);
        mBtnAddEvent.setOnClickListener(btnAddEventCloseClickListener);
    }
}
