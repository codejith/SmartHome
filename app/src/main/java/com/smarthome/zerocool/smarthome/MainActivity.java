package com.smarthome.zerocool.smarthome;

import android.os.Messenger;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.os.Message;
import android.os.RemoteException;
import android.content.IntentFilter;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.content.ServiceConnection;
import android.content.ComponentName;
import android.content.Context;
import android.os.Handler;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


public class MainActivity extends AppCompatActivity {
    private Messenger service = null;
   private final Messenger serviceHandler = new Messenger(new ServiceHandler());
   private PushReceiver pushReceiver;
    private IntentFilter intentFilter = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.MQTT.PushReceived");
        pushReceiver = new PushReceiver();
        registerReceiver(pushReceiver, intentFilter, null, null);

        startService(new Intent(this, MQTTservice.class));
        System.out.println("WORKING TILL");

        buttonListener();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        bindService(new Intent(this, MQTTservice.class), serviceConnection, 0);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        unbindService(serviceConnection);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        registerReceiver(pushReceiver, intentFilter);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        unregisterReceiver(pushReceiver);
    }

    public class PushReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent i)
        {
           // String topic = i.getStringExtra(MQTTservice.TOPIC);
            String message = i.getStringExtra(MQTTservice.MESSAGE);
            //System.out.println(message);
            Toast.makeText(context, "Push message received - " + "Temperature" + ":" + message, Toast.LENGTH_LONG).show();
        }
    }





    private ServiceConnection serviceConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder binder)
        {
            service = new Messenger(binder);
            Bundle data = new Bundle();
            //data.putSerializable(MQTTservice.CLASSNAME, MainActivity.class);
            data.putCharSequence(MQTTservice.INTENTNAME, "com.example.MQTT.PushReceived");
            Message msg = Message.obtain(null, MQTTservice.REGISTER);
            msg.setData(data);
            msg.replyTo = serviceHandler;
            try
            {
                Thread.sleep(100);
                service.send(msg);
            }
            catch (RemoteException | InterruptedException e)
            {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0)
        {
        }
    };

    class ServiceHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case MQTTservice.SUBSCRIBE: 	break;
                case MQTTservice.PUBLISH:		break;
                case MQTTservice.REGISTER:		break;
                default:
                    super.handleMessage(msg);
                    return;
            }

            // code here
        }
    }

 public void buttonListener() {

    // Button lgt1 = (Button)findViewById(R.id.light1);
      Button temp = (Button)findViewById(R.id.light2);

     ToggleButton tg = (ToggleButton)findViewById(R.id.light1);

     tg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
         @Override
         public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
             if (isChecked) {
                 Bundle data = new Bundle();
                 //data.putCharSequence(MQTTservice.TOPIC, "Light");
                 data.putCharSequence(MQTTservice.TOPIC, "Light2");
                 data.putCharSequence(MQTTservice.MESSAGE, "a");
                 Message msg = Message.obtain(null, MQTTservice.PUBLISH);
                 msg.setData(data);
                 msg.replyTo = serviceHandler;
                 try {
                     service.send(msg);
                 } catch (RemoteException e) {
                     e.printStackTrace();

                 }
             } else {
                 Bundle data = new Bundle();
                 data.putCharSequence(MQTTservice.TOPIC, "Light2");
                 data.putCharSequence(MQTTservice.MESSAGE, "b");
                 Message msg = Message.obtain(null, MQTTservice.PUBLISH);
                 msg.setData(data);
                 msg.replyTo = serviceHandler;
                 try {
                     service.send(msg);
                 } catch (RemoteException e) {
                     e.printStackTrace();
                 }
             }
         }
     });


     temp.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Bundle data = new Bundle();
             //data.putCharSequence(MQTTservice.TOPIC, "Light");
             data.putCharSequence(MQTTservice.TOPIC, "tempTopic");

             Message msg = Message.obtain(null, MQTTservice.SUBSCRIBE);
             msg.setData(data);
             msg.replyTo = serviceHandler;
             try {
                 service.send(msg);
             } catch (RemoteException e) {
               //  Toast.makeText(context, "No data available", Toast.LENGTH_LONG).show();
                  e.printStackTrace();
                 // result.setText("Subscribe failed with exception:" + e.getMessage());
             }

         }
     });

     /*lgt2.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {

             Bundle data = new Bundle();
             data.putCharSequence(MQTTservice.TOPIC, "Light2");
             data.putCharSequence(MQTTservice.MESSAGE, "b");
             Message msg = Message.obtain(null, MQTTservice.PUBLISH);
             msg.setData(data);
             msg.replyTo = serviceHandler;
             try {
                 service.send(msg);
             } catch (RemoteException e) {
                 e.printStackTrace();
                 //.setText("Publish failed with exception:" + e.getMessage());
             }
         }
     });*/
 }


}
