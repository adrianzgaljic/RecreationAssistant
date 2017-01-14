package com.jularic.dominik.recreationassistant;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.jularic.dominik.recreationassistant.adapters.AdapterMyEvents;
import com.jularic.dominik.recreationassistant.adapters.AddListener;
import com.jularic.dominik.recreationassistant.adapters.CompleteListener;
import com.jularic.dominik.recreationassistant.adapters.Divider;
import com.jularic.dominik.recreationassistant.adapters.Filter;
import com.jularic.dominik.recreationassistant.adapters.MarkListener;
import com.jularic.dominik.recreationassistant.adapters.ResetListener;
import com.jularic.dominik.recreationassistant.adapters.SimpleTouchCallback;
import com.jularic.dominik.recreationassistant.beans.Event;
import com.jularic.dominik.recreationassistant.widgets.MyEventsRecyclerView;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;


public class MyEvents extends AppCompatActivity {

    Toolbar toolbarAddMyEvents;
    Button btnAddNewEvent;
    MyEventsRecyclerView recyclerViewMyEvents;
    Realm mRealm;
    RealmResults<Event> mResults;
    View mEmptyView;
    AdapterMyEvents mAdapter;
    private static final String TAG = "MyEvents";

    private View.OnClickListener btnAddNewEventAddListener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            showDialogAdd();
        }
    };

    private AddListener mAddListener = new AddListener() {
        @Override
        public void add() {
            showDialogAdd();;
        }
    };

    private void showDialogAdd() {
        DialogAddMyEvent dialog = new DialogAddMyEvent();
        dialog.show(getSupportFragmentManager(),"Add");
    }

    private void showDialogMark(int position){
        DialogMark dialog = new DialogMark();
        Bundle bundle = new Bundle();
        bundle.putInt("POSITION", position);
        dialog.setArguments(bundle);
        dialog.setCompleteListener(mCompleteListener);
        dialog.show(getSupportFragmentManager(), "Mark");
    }

    private MarkListener mMarkListener = new MarkListener() {
        @Override
        public void onMark(int position) {
            showDialogMark(position);
        }
    };

    private CompleteListener mCompleteListener = new CompleteListener() {
        @Override
        public void onComplete(int position) {
            mAdapter.markComplete(position);
        }
    };

    private RealmChangeListener mChangeListener = new RealmChangeListener() {
        @Override
        public void onChange(Object element) {
            Log.d(TAG, "onChange: was called");
            mAdapter.update(mResults);
        }
    };

    private ResetListener mResetListener = new ResetListener(){

        @Override
        public void onReset() {
            RecreationAssistantApp.save(MyEvents.this, Filter.NONE);
            loadResults(Filter.NONE);
        }
    };
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_events);
        mRealm = Realm.getDefaultInstance();
        toolbarAddMyEvents = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbarAddMyEvents);
        btnAddNewEvent = (Button) findViewById(R.id.button_add_my_event);
        btnAddNewEvent.setOnClickListener(btnAddNewEventAddListener);

        int filterOption = RecreationAssistantApp.load(this);
        loadResults(filterOption);
        mEmptyView = findViewById(R.id.empty_my_events);
        recyclerViewMyEvents = (MyEventsRecyclerView) findViewById(R.id.rv_my_events);
        recyclerViewMyEvents.addItemDecoration(new Divider(this, LinearLayoutManager.VERTICAL));
        recyclerViewMyEvents.setItemAnimator(new DefaultItemAnimator());
        recyclerViewMyEvents.hideIfEmpty(toolbarAddMyEvents);
        recyclerViewMyEvents.showIfEmpty(mEmptyView);
        mAdapter = new AdapterMyEvents(this, mRealm, mResults, mAddListener, mMarkListener, mResetListener);
        mAdapter.setHasStableIds(true);
        recyclerViewMyEvents.setAdapter(mAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerViewMyEvents.setLayoutManager(manager);
        //recyclerViewMyEvents.setAdapter(new AdapterMyEvents(this, mResults));


        SimpleTouchCallback callback = new SimpleTouchCallback(mAdapter);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(recyclerViewMyEvents);
        initLogoImage();
        initBackgroundImage();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        boolean handled = true;
        int filterOption = Filter.NONE;
        switch (id){
            case R.id.action_add:
                showDialogAdd();
                break;
            case R.id.action_sort_none:
                filterOption = Filter.NONE;
                break;
            case R.id.action_sort_ascending_date:
                filterOption = Filter.LEAST_TIME_LEFT;
                break;
            case R.id.action_sort_descending_dete:
                filterOption = Filter.MOST_TIME_LEFT;
                break;
            case R.id.action_show_complete:
                filterOption = Filter.COMPLETE;
                break;
            case R.id.action_show_incomplete:
                filterOption = Filter.INCOMPLETE;
                break;
            default:
                handled = false;
                break;
        }
        RecreationAssistantApp.save(this, filterOption);
        loadResults(filterOption);
        return handled;
    }

    private void loadResults (int filterOption){
        switch (filterOption){
            case Filter.NONE:
                mResults = mRealm.where(Event.class).findAllAsync();
                break;
            case Filter.LEAST_TIME_LEFT:
                mResults = mRealm.where(Event.class).findAllSortedAsync("dateOfEvent");
                break;
            case Filter.MOST_TIME_LEFT:
                mResults = mRealm.where(Event.class).findAllSortedAsync("dateOfEvent", Sort.DESCENDING);
                break;
            case Filter.COMPLETE:
                mResults = mRealm.where(Event.class).equalTo("completed",true).findAllAsync();
                break;
            case Filter.INCOMPLETE:
                mResults = mRealm.where(Event.class).equalTo("completed",false).findAllAsync();
                break;
        }
        mResults.addChangeListener(mChangeListener);
    }




    @Override
    protected void onStart() {
        super.onStart();
        mResults.addChangeListener(mChangeListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mResults.removeChangeListener(mChangeListener);
    }

    private void initBackgroundImage() {
        ImageView background = (ImageView) findViewById(R.id.iv_my_events_background);
        Glide.with(this)
                .load(R.drawable.background)
                .centerCrop()
                .into(background);
    }
    private void initLogoImage() {
        ImageView background = (ImageView) findViewById(R.id.iv_logo_my_events);
        Glide.with(this)
                .load(R.drawable.background_running_image)
                .centerCrop()
                .into(background);
    }
}
