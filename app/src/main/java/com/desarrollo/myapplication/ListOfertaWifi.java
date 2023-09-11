package com.desarrollo.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import honeywell.connection.ConnectionBase;
import honeywell.connection.Connection_TCP;

public class ListOfertaWifi extends AppCompatActivity implements Runnable {
    ConnectionBase conn = null;
    private String ip;
    private int port;
    byte[] printdatascpos = null;

    private Button btnprinttest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_oferta_wifi);

        btnprinttest = findViewById(R.id.btn_print_wifi);

        btnprinttest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(ListOfertaWifi.this, "PrintingTask").start();
            }
        });

    }

    public void run() {

        try
        {
            conn = null;
            conn = Connection_TCP.createClient(ip,port , false);
            if(!conn.getIsOpen()) {
                conn.open();
            }
            int bytesWritten = 0;
            int bytesToWrite = 1024;
            int totalBytes = printdatascpos.length;
            int remainingBytes = totalBytes;
            while (bytesWritten < totalBytes)
            {
                if (remainingBytes < bytesToWrite)
                    bytesToWrite = remainingBytes;

                //Send data, 1024 bytes at a time until all data sent
                conn.write(printdatascpos, bytesWritten, bytesToWrite);
                bytesWritten += bytesToWrite;
                remainingBytes = remainingBytes - bytesToWrite;
                Thread.sleep(100);
            }

            //signals to close connection
            conn.close();
        } catch (Exception e) {
            if(conn != null)
                conn.close();
            e.printStackTrace();
        }
    }

}