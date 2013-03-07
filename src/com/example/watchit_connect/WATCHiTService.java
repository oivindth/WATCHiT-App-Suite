package com.example.watchit_connect;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;


import service.AbstractService;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class WATCHiTService extends AbstractService {
	
	private NotificationManager mNM;
	
	private BluetoothSocket btSocket;
    
	final int RECIEVE_MESSAGE = 1;        // Status  for Handler
	private BluetoothAdapter btAdapter = null;
	private StringBuilder sb = new StringBuilder();
	private ConnectedThread mConnectedThread;
	private Handler h;
	private final static UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //Standard SerialPortService ID
	private String deviceAdress;
	

	// Unique Identification Number for the Notification.
    // We use it on Notification start, and to cancel it.
    private int NOTIFICATION = R.string.watchit_service_started;
  
    static final int MSG_SET_INT_VALUE = 9993;
    static final int MSG_SET_STRING_VALUE =9994;
    static final int MSG_SET_STRING_VALUE_TO_ACTIVITY = 9995;
    static final int MSG_CONNECTION_ESTABLISHED = 9996;
    static final int MSG_CONNECTION_LOST = 9997;
    static final int MSG_DEVICE_NAME = 9998;
    
    @Override
    public void onStartService() {
    	Log.i("WATCHiTService", "We are now in service");
    	mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

    	// Display a notification about us starting.  We put an icon in the status bar.
    	showNotification(); // no icon atm.

    	h = new Handler() {
    		public void handleMessage(android.os.Message msg) {
    			Log.d("LocalService", "Message in handler: " + msg.what);
    			switch (msg.what) {
    			case RECIEVE_MESSAGE:                                                   // if receive massage
    				byte[] readBuf = (byte[]) msg.obj;
    				String strIncom = new String(readBuf, 0, msg.arg1);                 // create string from bytes array
    				sb.append(strIncom); 
    				//textView.setText(sb.toString());
    				//sb.delete(0, sb.length());
    				int endOfLineIndex = sb.indexOf("\r\n");                            // determine the end-of-line
    				if (endOfLineIndex > 0) {                                            // if end-of-line,
    					String sbprint = sb.substring(0, endOfLineIndex);               // extract string
    					sb.delete(0, sb.length());  //clear
    					
    	                //Send data to Activity
    	                Bundle b = new Bundle();
    	                b.putString("watchitdata", sbprint);
    	                Message messageToActivity = Message.obtain(null, MSG_SET_STRING_VALUE_TO_ACTIVITY);
    	                messageToActivity.setData(b);
    	                send(messageToActivity);

    				}
    				Log.d("Recieved from arduino: ", "...String:"+ sb.toString() +  "  Byte:" + msg.arg1 + "...");
    				break;

    			}
    		};
    	};
    	
    	
    }

    
	@Override
	public void onStopService() {
		Log.d("TAG", "...In onPause()...");
	    try     {
	      btSocket.close();
	    } catch (Exception e2) {
	      //errorExit("Fatal Error", "In onPause() and failed to close socket." + e2.getMessage() + ".");
	    	e2.printStackTrace();
	    }	
	}

	@Override
	public void onReceiveMessage(Message msg) {
		Log.d("onreceievemessage in localservice", "correct place");
		if (msg.what == MSG_SET_STRING_VALUE) {
		      mConnectedThread.write(msg.getData().getString("watchitdata"));
		    }
		if (msg.what == MSG_DEVICE_NAME) {
			deviceAdress = msg.getData().getString("btDevice");
			int pos = msg.getData().getInt("btdevicepos");
			btAdapter = BluetoothAdapter.getDefaultAdapter();
	    	//checkBTState();
	    	Log.d("TAG", "...onResume - try connect...");

	    	BluetoothDevice device = MainApplication.getInstance().bluetoothDevices.get(pos);
	    	
	
	    	// Two things are needed to make a connection:
	    	//   A MAC address, which we got above.
	    	//   A Service ID or UUID.  In this case we are using the
	    	//     UUID for SPP.
	    	try {
	    		btSocket = device.createRfcommSocketToServiceRecord(uuid);
	    	} catch (IOException e) {
	    		//errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
	    		e.printStackTrace();
	    	}

	    	// Discovery is resource intensive.  Make sure it isn't going on
	    	// when you attempt to connect and pass your message.
	    	btAdapter.cancelDiscovery();

	    	// Establish the connection.  This will block until it connects.
	    	Log.d("TAG ","...Connecting...");
	    	try {
	    		btSocket.connect();
	    		
	    		Log.d("TAG", "....Connection ok...");
	    		
	    		send(Message.obtain(null, this.MSG_CONNECTION_ESTABLISHED)); //Let acitvity know that a connection to watchit has been made.
	    	} catch (IOException e) {
	    		e.printStackTrace();
	    		try {
	    			btSocket.close();
	    		} catch (IOException e2) {
	    			//errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
	    			e2.printStackTrace();
	    		}
	    	}

	    	// Create a data stream so we can talk to server.
	    	Log.d("TAG", "...Create Socket...");
	    	//send(Message.obtain(null, MSG_SET_INT_VALUE));
	    	mConnectedThread = new ConnectedThread(btSocket);
	    	mConnectedThread.start();
			
		}
	}
        
	/*
	  private BluetoothDevice getDevice(String deviceAdress) {
	       	BluetoothDevice mDevice = null;
	   		Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
	   		if(pairedDevices.size() > 0)
	           {
	               for(BluetoothDevice device : pairedDevices)
	               {
	                   if(device.getAddress().equals(deviceAdress)) mDevice = device;
	               }
	           }
	   		return mDevice;
	       }
	       */


		    /* Call this from the main activity to shutdown the connection */
		    public void cancel() {
		        try {
		            btSocket.close();
		        } catch (IOException e) { }
		    } 
		    
		    private class ConnectedThread extends Thread {
		        private final BluetoothSocket mmSocket;
		        private final InputStream mmInStream;
		        private final OutputStream mmOutStream;
		      
		        public ConnectedThread(BluetoothSocket socket) {
		        	Log.d("ConnectTHREAD", "In thread");
		            mmSocket = socket;
		            InputStream tmpIn = null;
		            OutputStream tmpOut = null;
		            // Get the input and output streams, using temp objects because
		            // member streams are final
		            try {
		                tmpIn = socket.getInputStream();
		                tmpOut = socket.getOutputStream();
		            } catch (IOException e) { }
		      
		            mmInStream = tmpIn;
		            mmOutStream = tmpOut;
		        }
		      
		        public void run() {
		        	
		            byte[] buffer = new byte[1024];  // buffer store for the stream
		            int bytes; // bytes returned from read()
		 
		            // Keep listening to the InputStream until an exception occurs
		            while (true) {
		            	
		            	Log.d("in loop", "waiting for data");
		                try {
		                    // Read from the InputStream
		                    bytes = mmInStream.read(buffer);        // Get number of bytes and message in "buffer"
		                    h.obtainMessage(RECIEVE_MESSAGE, bytes, -1, buffer).sendToTarget();     // Send to message queue Handler
		                    Log.d("recieve", "b " + bytes);
		                } catch (Exception e) {
		                	e.printStackTrace();
		                	Log.d("thread RUN", "error:  " + e);
		                    break;
		                }
		            }
		        }
		      
		        /* Call this from the main activity to send data to the remote device */
		        public void write(String message) {
		            Log.d("TAG", "...Data to send: " + message + "...");
		            byte[] msgBuffer = message.getBytes();
		            try {
		                mmOutStream.write(msgBuffer);
		            } catch (IOException e) {
		                Log.d("TAG", "...Error data send: " + e.getMessage() + "...");     
		              }
		        }
		        
		        
		        /* Call this from the main activity to shutdown the thread connection */
		        public void cancel() {
		            try {
		                mmSocket.close();
		            } catch (IOException e) { }
		        } 
		    }
		    

    /**
     * Show a notification while this service is running.
     */
    private void showNotification() {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = getText(R.string.watchit_service_started);

        // Set the icon, scrolling text and timestamp
        Notification notification = new Notification(R.drawable.androidavatar , text,
               System.currentTimeMillis());

        //The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        // Set the info for the views that show in the notification panel.
        notification.setLatestEventInfo(this, "WATCHiT",
                       text, contentIntent);

        // Send the notification.
        mNM.notify(NOTIFICATION, notification);
    }

}
