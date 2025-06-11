package com.example.homestray;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;

public class SearchActivity extends MainActivity {
    SeekBar rangeSeekBar;
    TextView range;
    CheckBox cbBaby, cbYoung, cbAdult, cbOld, cbCrippled, cbNeutered, cbSick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //Declaration
        drawerLayout = findViewById(R.id.drawerLayout_search);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        Spinner spinnerType = findViewById(R.id.spinnerType);
        rangeSeekBar = findViewById(R.id.rangeSeekBar);
        range = findViewById(R.id.rangeText);
        cbBaby = findViewById(R.id.cbBaby);
        cbYoung = findViewById(R.id.cbYoung);
        cbAdult = findViewById(R.id.cbAdult);
        cbOld = findViewById(R.id.ctOld);

        //Toolbar
        setSupportActionBar(toolbar);

        //Setting Spinner Adapter
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.animal_type,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);

        //Spinner on selected
        final String[] selectedType = {""};
        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedType[0] = parent.getItemAtPosition(position).toString();
                Toast.makeText(SearchActivity.this, selectedType[0], Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        //Seekbar Range of search
        rangeSeekBar.setProgress(10);
        rangeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                range.setText(progress + " km");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        //Navigation Drawer Menu
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        //Set so that user won't exist app when press back button
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if(drawerLayout.isDrawerOpen(GravityCompat.START)){
                    drawerLayout.closeDrawer(GravityCompat.START);
                }else{
                    Intent intent = new Intent(SearchActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        ActivityStateHelper.saveLastActivity(this, this.getClass().getName());
    }
}
