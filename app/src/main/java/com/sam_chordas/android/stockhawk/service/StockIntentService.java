package com.sam_chordas.android.stockhawk.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.TaskParams;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.ui.MyStocksActivity;

/**
 * Created by sam_chordas on 10/1/15.
 */
public class StockIntentService extends IntentService {

  public StockIntentService() {
    super(StockIntentService.class.getName());
  }

  public StockIntentService(String name) {
    super(name);
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    StockTaskService stockTaskService = new StockTaskService(this);
    Bundle args = new Bundle();
    if (intent.getStringExtra("tag").equals("add")) {
      args.putString("symbol", intent.getStringExtra("symbol"));
      try {
        stockTaskService.onRunTask(new TaskParams(intent.getStringExtra("tag"), args));
      } catch (Exception e) {
        e.printStackTrace();
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
          @Override
          public void run() {
            Toast.makeText(StockIntentService.this.getApplicationContext(), R.string.symbol_not_found, Toast.LENGTH_SHORT).show();
          }
        });
      }
    }
    else if(intent.getStringExtra("tag").equals("historical")){
      args.putString("name",intent.getStringExtra("name"));
      args.putString("currentDate",intent.getStringExtra("currentDate"));
      args.putString("weekBefore",intent.getStringExtra("weekBefore"));{
        try {
          stockTaskService.onRunTask(new TaskParams(intent.getStringExtra("tag"), args));
        } catch (Exception e) {
          e.printStackTrace();
          Handler handler = new Handler(Looper.getMainLooper());
          handler.post(new Runnable() {
            @Override
            public void run() {
              Toast.makeText(StockIntentService.this.getApplicationContext(), R.string.symbol_not_found, Toast.LENGTH_SHORT).show();
            }
          });
        }
      }

    }
    else if(intent.getStringExtra("tag").equals("init")){
      try {
        stockTaskService.onRunTask(new TaskParams(intent.getStringExtra("tag"), args));
      } catch (Exception e) {
        e.printStackTrace();
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
          @Override
          public void run() {
            Toast.makeText(StockIntentService.this.getApplicationContext(), R.string.symbol_not_found, Toast.LENGTH_SHORT).show();
          }
        });      }
    }
    // We can call OnRunTask from the intent service to force it to run immediately instead of
    // scheduling a task.
  }
}
