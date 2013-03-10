package service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import com.example.watchit_connect.MainApplication;
import com.example.watchit_connect.R;
import activities.MainDashBoardActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

/**
 * Service class for communicating with WATCHiT. Runs a thread so it can always receieve data when paired and connected to WATCHiT device.
 * Should be easy to extend so that it works with other bluetooth devices.
 * @author oivindth
 *
 */
public class WATCHiTService extends AbstractService {

	private NotificationManager mNM;

	private BluetoothSocket btSocket;

	private BluetoothAdapter btAdapter = null;

	StringBuilder sb = new StringBuilder();
	private ConnectedThread mConnectedThread;
	private final static UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //Standard SerialPortService ID
	// Unique Identification Number for the Notification.
	// We use it on Notification start, and to cancel it.
	private int NOTIFICATION = R.string.watchit_service_started;

	public static final int MSG_SET_INT_VALUE = 9993;
	public static final int MSG_SET_STRING_VALUE =9994;
	public static final int MSG_SET_STRING_VALUE_TO_ACTIVITY = 9995;
	public static final int MSG_CONNECTION_ESTABLISHED = 9996;
	public static final int MSG_CONNECTION_LOST = 9997;
	public static final int MSG_DEVICE_NAME = 9998;

	@Override
	public void onStartService() {
		Log.i("WATCHiTService", "We are now in service");
		mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		// Display a notification about us starting.  We put an icon in the status bar.
		showNotification();
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
	/**
	 * Receieve a message from Activity.
	 * case: set string value: write data to watchit.
	 * case: msg_device_name: device paired. Create, open sockets and connect.
	 */
	public void onReceiveMessage(Message msg) {
		Log.d("onreceievemessage in localservice", "correct place");
		if (msg.what == MSG_SET_STRING_VALUE) {
			mConnectedThread.write(msg.getData().getString("watchitdata"));
		}
		if (msg.what == MSG_DEVICE_NAME) {
			msg.getData().getString("btDevice");
			int pos = msg.getData().getInt("btdevicepos");
			btAdapter = BluetoothAdapter.getDefaultAdapter();
			//checkBTState();
			Log.d("WATCHITSERVICE", "...onResume - try connect...");

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
			Log.d("WATCHITSERVICE ","...Connecting...");
			try {
				btSocket.connect();

				Log.d("WATCHITSERVICE", "....Connection ok...");

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

			// Create a data stream so we can talk to watchit(bluetooth)
			Log.d("WATCHITSERVICE", "...Create Socket...");
			mConnectedThread = new ConnectedThread(btSocket);
			mConnectedThread.start();

		}
	}

	/**
	 *  Call this from the main activity to shutdown the connection 
	 *  
	 */
	public void cancel() {
		try {
			btSocket.close();
		} catch (IOException e) { }
	} 

	/**
	 * Thread used for constantly listening to data receieved from bluetooth(WATCHiT).
	 * @author oivindth
	 *
	 */
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
					Log.d("Service.Thread.Run(): ", " bytes:  " + bytes);
					
					String strIncom = new String(buffer, 0, bytes);                 // create string from bytes array
					sb.append(strIncom); 
					//textView.setText(sb.toString());
					//sb.delete(0, sb.length());
					int endOfLineIndex = sb.indexOf("\r\n");                            // determine the end-of-line
					if (endOfLineIndex > 0) {                                            // if end-of-line,
						String sbprint = sb.substring(0, endOfLineIndex);               // extract string
						sb.delete(0, sb.length());  //clear
						Log.d("low level:  ", sbprint);
						
						//Send data to Activity
						Bundle b = new Bundle();
						b.putString("watchitdata", sbprint);
						Message messageToActivity = Message.obtain(null, MSG_SET_STRING_VALUE_TO_ACTIVITY);
						messageToActivity.setData(b);
						send(messageToActivity);

					}

				} catch (Exception e) {
					e.printStackTrace();
					Log.d("thread RUN", "error:  " + e);
					break;
				}
			}
		}
		/**
		 *  Send data to WATChiT
		 **/
		public void write(String message) {
			Log.d("TAG", "...Data to send: " + message + "...");
			byte[] msgBuffer = message.getBytes();
			try {
				mmOutStream.write(msgBuffer);
			} catch (IOException e) {
				Log.d("TAG", "...Error data send: " + e.getMessage() + "...");     
			}
		}

		/** Shut down thread *
		 * */
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
				new Intent(this, MainDashBoardActivity.class), 0);

		// Set the info for the views that show in the notification panel.
		notification.setLatestEventInfo(this, "WATCHiT",
				text, contentIntent);

		// Send the notification.
		mNM.notify(NOTIFICATION, notification);
	}

}
