package asynctasks;

/**
 * Copyrigth 2013 ¯ivind Thorvaldsen
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
import java.util.UUID;

import no.ntnu.emergencyreflect.R;

import service.WATCHiTService;

import activities.BaseActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;

import com.example.watchit_connect.MainApplication;

/**
 * Asynctask used for bluetooth connection. Since btSocket.connect() blocks until it is connected we need it in a thread(asynctask)
 * to avoid occupiing the main guithread.
 * @author oivindth
 *
 */
public class ConnectToBluetoothTask extends AsyncTask<Void, Void, Boolean> {
	
	private BaseActivity mActivity;
	private MainApplication sApp;
	private int position;
	
	private BluetoothAdapter btAdapter;
	
	private final static UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
	
	public ConnectToBluetoothTask (BaseActivity activity, int blueToothDeviceposition) {
		mActivity = activity;
		position = blueToothDeviceposition;
		sApp = MainApplication.getInstance();
	}
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		mActivity.showProgress(mActivity.getString(R.string.bluetooth), mActivity.getString( R.string.bluetooth_connecting));
	}
	@Override
	protected Boolean doInBackground(Void... params) {
		btAdapter = BluetoothAdapter.getDefaultAdapter();
		BluetoothDevice device = MainApplication.getInstance().bluetoothDevices.get(position);
		// Two things are needed to make a connection:
		//   A MAC address, which we got above.
		//   A Service ID or UUID.  In this case we are using the
		//     UUID for SPP.
		try {
			sApp.btSocket = device.createRfcommSocketToServiceRecord(uuid);
		} catch (IOException e) {
			//errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
			e.printStackTrace();
		}

		// Discovery is resource intensive.  Make sure it isn't going on
		// when you attempt to connect and pass your message.
		btAdapter.cancelDiscovery();

		// Establish the connection.  This will block until it connects.
		try {
			sApp.btSocket.connect();
			// Create a data stream so we can talk to watchit(bluetooth)
			//Log.d("WATCHITSERVICE", "...Create Socket...");
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			//send(Message.obtain(null, this.MSG_CONNECTION_FAILED));
			try {
				sApp.btSocket.close();
			} catch (IOException e2) {
				//errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
				e2.printStackTrace();
			}
			return false;
		}

	}

	
	@Override
	protected void onPostExecute(final Boolean success) {

		mActivity.dismissProgress();
		
		if (success) {
		// connected.. tell service to start thread
			Message message = Message.obtain(null, WATCHiTService.START_CONNECT_THREAD);
			Bundle b = new Bundle();
			//b.putString("btDevice", deviceAdress);
			b.putInt("btdevicepos", position);
			message.setData(b);
			sApp.sendMessageToService(message);
			mActivity.showToast(mActivity.getString(R.string.bluetooth_ready_data));
			sApp.isWATChiTOn = true;
			sApp.broadcastWATCHiTConnectionChange(true);
		} else {
			sApp.isWATChiTOn = false;
			sApp.broadcastWATCHiTConnectionChange(false);
			mActivity.showToast(mActivity.getString(R.string.bluetooth_connection_failed));
			
			
		}
		
	}
	
}
