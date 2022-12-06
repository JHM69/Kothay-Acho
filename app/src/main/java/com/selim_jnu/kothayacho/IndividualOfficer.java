package com.selim_jnu.kothayacho;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.selim_jnu.kothayacho.adapter.TabAdapter;
import com.selim_jnu.kothayacho.fragment.StatusFragment;
import com.selim_jnu.kothayacho.fragment.TrackFragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

public class IndividualOfficer extends AppCompatActivity {

    private TabAdapter adapter;
    private TabLayout tabLayout;

    Toolbar toolbar;
    String myFormat="d MMM";
    List<Address> addresses = new ArrayList<>();
    TextView dates;
    User user;
    View progressBar;
    ViewPager viewPager;


    final Calendar myCalendar= Calendar.getInstance();

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indivisual_officer);

        toolbar = findViewById(R.id.toolbar4);
        progressBar = findViewById(R.id.progressBar3);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.view_pager);
        dates = findViewById(R.id.text67View29);

        DatePickerDialog.OnDateSetListener date = (view, year, month, day) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH,month);
            myCalendar.set(Calendar.DAY_OF_MONTH,day);
            updateLabel();
        };

        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat(myFormat);
        String todate= dateFormat.format(currentdate());
        dates.setText(todate);
        dates.setOnClickListener(view -> new DatePickerDialog(IndividualOfficer.this,date,myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
        ).show());
        long time = System.currentTimeMillis();
        long end = time - (time%86400000) + 86400000;
        long start = end-86400000;

        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");
        String number = intent.getStringExtra("number");

        Log.d("number", "onCreate: "+ number);
        if(user==null){
            FirebaseDatabase.getInstance().getReference().child("Users").equalTo(number).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    user = snapshot.getValue(User.class);
                    toolbar.setSubtitle(Objects.requireNonNull(user).getNumber());
                    toolbar.setTitle(user.getName());
                    getData(start, end);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else{
            toolbar.setSubtitle(user.getNumber());
            toolbar.setTitle(user.getName());
            getData(start, end);
        }



    }


    @Override
    public void onResume() {
        super.onResume();
    }


    private void highLightCurrentTab(int position) {
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            assert tab != null;
            tab.setCustomView(null);
            tab.setCustomView(adapter.getTabView(i));
        }
        TabLayout.Tab tab = tabLayout.getTabAt(position);
        Objects.requireNonNull(tab).setCustomView(null);
        tab.setCustomView(adapter.getSelectedTabView(position, false));
    }



    private void updateLabel(){
        long time = myCalendar.getTime().getTime();
        long end = time - (time%86400000) + 86400000;
        long start = end-86400000;
//        Log.d("ttime", "date current : "+ System.currentTimeMillis());
//        Log.d("ttime", "date selectet : "+ start);
//        Log.d("ttime", "date end : "+ end);
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.US);
        dates.setText(dateFormat.format(myCalendar.getTime()));
        getData(start, end);
    }

    void getData(long start, long end){
        addresses.clear();
        progressBar.setVisibility(View.VISIBLE);
        Query place = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getNumber()).child("Places").orderByChild("key").equalTo(user.getKey()).limitToLast(100);
        adapter = new TabAdapter(getSupportFragmentManager(), getApplicationContext());
        place.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot s:snapshot.getChildren()){
                    Address a = s.getValue(Address.class);
                    addresses.add(a);
//                    if(!contains(Objects.requireNonNull(a).address)){
//                        addresses.add(a);
//                    }
                }
                progressBar.setVisibility(View.GONE);
                adapter.addFragment(new StatusFragment(addresses), "Location", R.drawable.ic_outline_location_on_24);
                adapter.addFragment(new TrackFragment(addresses, user.getId()), "Tracking", R.drawable.ic_outline_map_24);

                viewPager.setAdapter(adapter);
                for (int i = 0; i < tabLayout.getTabCount(); i++) {
                    TabLayout.Tab tab = tabLayout.getTabAt(i);
                    Objects.requireNonNull(tab).setCustomView(null);
                    tab.setCustomView(adapter.getTabView(i));
                }
                tabLayout.setupWithViewPager(viewPager);

                highLightCurrentTab(0);
                try {
                    viewPager.setOffscreenPageLimit(adapter.getCount());
                } catch (Exception ignored) {

                }


                viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    }

                    @Override
                    public void onPageSelected(int position) {
                        highLightCurrentTab(position);
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    boolean contains(String address){
        for(Address a:addresses){
            if(a.getAddress().equals(address)) return true;
        }
        return false;
    }

    private Date currentdate() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 0);
        return cal.getTime();
    }
}
