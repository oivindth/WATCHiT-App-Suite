package service;

import java.util.Timer;
import java.util.TimerTask;

import com.example.watchit_connect.MainActivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Message;
import android.util.Log;

public class LocalService3 extends AbstractService {
    public static final int MSG_INCREMENT = 1;
    public static final int MSG_COUNTER = 2;
	
    private NotificationManager nm;
    private Timer timer = new Timer();
    private int counter = 0, incrementby = 1;

	@Override 
	public void onStartService() {
        showNotification();
        timer.scheduleAtFixedRate(new TimerTask(){ public void run() {onTimerTick();}}, 0, 250L);
	}
	
   @Override
    public void onStopService() {
        if (timer != null) {timer.cancel();}
        counter=0;
        nm.cancel(getClass().getSimpleName().hashCode());
        Log.i("MyService", "Service Stopped.");
    }   

	@Override
	public void onReceiveMessage(Message msg) {
		if (msg.what == MSG_INCREMENT) {
			incrementby = msg.arg1;
		}
	}
   
    private void showNotification() {
     nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        
        // In this sample, we'll use the same text for the ticker and the expanded notification
        // Set the icon, scrolling text and timestamp
        // The PendingIntent to launch our activity if the user selects this notification
        // Set the info for the views that show in the notification panel.

        //String text = getString(R.string.service_started, getClass().getSimpleName());        
        //Notification notification = new Notification(R.drawable.ic_launcher, text, System.currentTimeMillis());
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
        //notification.setLatestEventInfo(this, getClass().getSimpleName(), text, contentIntent);
        
        // Send the notification.
        // We use a layout id because it is a unique number.  We use it later to cancel.

        //nm.notify(getClass().getSimpleName().hashCode(), notification);
    }

    private void onTimerTick() {
        //Log.i("TimerTick", "Timer doing work." + counter);
        
        try {
            counter += incrementby;

            // Send data as simple integer
        	send(Message.obtain(null, MSG_COUNTER, counter, 0));
        } 
        catch (Throwable t) { //you should always ultimately catch all exceptions in timer tasks.
            Log.e("TimerTick", "Timer Tick Failed.", t);            
        }
    }
}