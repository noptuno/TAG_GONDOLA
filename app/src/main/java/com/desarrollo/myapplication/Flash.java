package com.desarrollo.myapplication;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.desarrollo.myapplication.consultor.ConsultorPrecioActivity;
import com.desarrollo.myapplication.consultor.fullscream.FullscreenActivity;

public class Flash extends AppCompatActivity {
    MediaPlayer bienvenidosound;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash);

        bienvenidosound = MediaPlayer.create(this,R.raw.bienvenido);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {


                Intent mainIntent = new Intent(Flash.this, MenuPrincipal.class);
                Flash.this.startActivity(mainIntent);
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                Flash.this.finish();

            }


        }, 2000);

    }
}
