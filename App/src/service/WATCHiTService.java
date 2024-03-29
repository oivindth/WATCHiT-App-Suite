package service;
/**
 * Copyrigth 2013 �ivind Thorvaldsen
 * This file is part of Reflection App

    Reflection App is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Foobar is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */
/*
 * 
 * Thanks to Matt Bell and A.V. Koltykov for inspiration and code examples when 
 * creating the android to arduino bluetooth communication moduels.
 * 
 * Matt Bell
 * ANDROID AND ARDUINO BLUETOOTH COMMUNICATION
 * url:  http://bellcode.wordpress.com/2012/01/02/android-and-arduino-bluetooth-communication/

 * Koltykov A.V.
 * Data transfer between Android and Arduino via Bluetooth 
 * url http://english.cxem.net/arduino/arduino5.php"
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import com.example.watchit_connect.MainApplication;
import no.ntnu.emergencyreflect.R;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Message;

/**
 * Service class for communicating with WATCHiT. Runs a thread so it can always receieve data when paired and connected to WATCHiT device.
 * Should be easy to extend so that it works with other bluetooth devices.
 * @author oivindth
 *
 */
public class WATCHiTService extends AbstractService {
	//TODO: Refactor bluetooth connection out and make it an asynctask.
	

	//private BluetoothSocket btSocket;

	//private BluetoothAdapter btAdapter = null;

	StringBuilder sb = new StringBuilder();
	private ConnectedThread mConnectedThread;
	//TODO: from random string instead of hardcoded uuid?
	private final static UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //Standard SerialPortService ID

	MainApplication sApp;
	
	public static final int MSG_SET_INT_VALUE = 9993;
	public static final int MSG_SET_STRING_VALUE =9994;
	public static final int MSG_SET_STRING_VALUE_TO_ACTIVITY = 9995;
	public static final int MSG_CONNECTION_ESTABLISHED = 9996;
	public static final int MSG_CONNECTION_LOST = 9997;
	public static final int MSG_CONNECTION_FAILED = 9999;
	public static final int MSG_DEVICE_NAME = 9998;
	public static final int START_CONNECT_THREAD = 10000;

	@Override
	public void onStartService() {
		sApp = MainApplication.getInstance();
	}

	@Override
	public void onStopService() {
		//Log.d("TAG", "...In onPause()...");
		try     {
			sApp.btSocket.close();
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
		if (msg.what == MSG_SET_STRING_VALUE) {
			//Log.d("WATCHiTService", " writing data to watchit. Data: " + msg.getData().getString("watchitdata"));
			mConnectedThread.write(msg.getData().getString("watchitdata"));
		}
		
		if (msg.what == START_CONNECT_THREAD) {
			mConnectedThread = new ConnectedThread(sApp.btSocket);
			mConnectedThread.start();
			//Log.d("WATCHITSERVICE", "Thread started.");
		}
		
	}

	/**
	 *  Call this from the main activity to shutdown the connection 
	 *  
	 */
	public void cancel() {
		try {
			sApp.btSocket.close();
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
			//Log.d("ConnectTHREAD", "In thread");
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
				//Log.d("in loop", "waiting for data");
				try {
					// Read from the InputStream
					bytes = mmInStream.read(buffer);        // Get number of bytes and message in "buffer"
					
					String strIncom = new String(buffer, 0, bytes);                 // create string from bytes array
					sb.append(strIncom); 
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

				} catch (Exception e) {
					e.printStackTrace();
					Message messageToActivity = Message.obtain(null, MSG_CONNECTION_LOST);
					send(messageToActivity);
					//Log.d("thread RUN", "error:  " + e);
					break;
				}
			}
		}
		/**
		 *  Send data to WATChiT
		 **/
		public void write(String message) {
			//Log.d("TAG", "...Data to send: " + message + "...");
			byte[] msgBuffer = message.getBytes();
			try {
				mmOutStream.write(msgBuffer);
			} catch (IOException e) {
				//Log.d("TAG", "...Error data send: " + e.getMessage() + "...");     
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



}
