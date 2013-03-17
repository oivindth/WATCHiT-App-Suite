package asynctasks;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.xbill.DNS.MXRecord;

import service.WATCHiTService;

import activities.BaseActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.example.watchit_connect.MainApplication;

import de.imc.mirror.sdk.OfflineModeHandler.Mode;

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
		mActivity.showProgress("Bluetooth", "establishing connection...");
	}
	
	@Override
	protected Boolean doInBackground(Void... params) {
		btAdapter = BluetoothAdapter.getDefaultAdapter();
		//checkBTState();
		Log.d("WATCHITSERVICE", ".... try connect...");

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
		Log.d("WATCHITSERVICE ","...Connecting...");
		try {
			sApp.btSocket.connect();
			// Create a data stream so we can talk to watchit(bluetooth)
			Log.d("WATCHITSERVICE", "...Create Socket...");
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
			mActivity.showToast("Now waiting for bluetooth data");
			sApp.isWATChiTOn = true;
			sApp.broadcastConnectionChange(true);
		} else {
			sApp.isWATChiTOn = false;
			sApp.broadcastConnectionChange(false);
			mActivity.showToast("Failed to connect...");
			
			
		}
		
	}
	
}
