package com.desarrollo.myapplication.consultor;

import android.support.constraint.ConstraintLayout;
import android.support.constraint.Constraints;
import android.support.constraint.solver.widgets.ConstraintTableLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.desarrollo.myapplication.R;

public class ConsultorPrecioActivity extends AppCompatActivity {

    ActionBar actionBar;
    ConstraintLayout constrain;
    LinearLayout linearprecio;
    ImageView imgescaner;
    boolean visible = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.consultor_precio);

        constrain = findViewById(R.id.constainlayot);
        actionBar = getSupportActionBar();
        linearprecio = findViewById(R.id.linear_precio);
        imgescaner = findViewById(R.id.img_escaner);

        constrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (visible){
                    showbarras ();
                    showlinearprecio();
                }else{
                    hidebarras ();
                    hidelinearprecio();
                }

            }
        });

        hidebarras();
    }


    void hidebarras(){
        constrain.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        if (actionBar != null) {
            actionBar.hide();
        }
        visible = true;
    }

    void showbarras (){
        constrain.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        if (actionBar != null) {
            actionBar.show();
        }
        visible = false;
    }


    void showlinearprecio(){
        linearprecio.setVisibility(View.VISIBLE);
        imgescaner.setVisibility(View.INVISIBLE);

    }
    void hidelinearprecio(){
        linearprecio.setVisibility(View.INVISIBLE);
        imgescaner.setVisibility(View.VISIBLE);
    }
}