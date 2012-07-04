package com.test.namespace;

import java.io.IOException;
import java.util.HashMap;



import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;



public class HATActivity extends Activity implements SensorEventListener{

	/** Called when the activity is first created. */
	
	public static int SHOW_PLANE = 0; // 0 for X, 1 for Y, 2 for Z
	MediaPlayer player;
	// Debugging
    private static final String TAG = "HATActivity";
    private static final boolean D = true;

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    // Layout Views
    private TextView mTitle;
    private ListView mConversationView;
    private EditText mOutEditText;
    private Button mSendButton;
    
    private final float[] mRotationMatrix = new float[16];

    // Name of the connected device
    private String mConnectedDeviceName = null;
    private Sensor mRotationVectorSensor;


	public static String Message;
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	

	
	    mRotationMatrix[ 0] = 1;
        mRotationMatrix[ 4] = 1;
        mRotationMatrix[ 8] = 1;
        mRotationMatrix[12] = 1;
		
		setContentView(R.layout.sensor_screen);
		tvX= (TextView)findViewById(R.id.plane_angle);
		tvInclination = (TextView)findViewById(R.id.anteversion);
		tvAnteversion = (TextView)findViewById(R.id.inclination);
		
		tvX_remote = (TextView)findViewById(R.id.plane_angle_remote);
		tvX_relative = (TextView)findViewById(R.id.plane_angle_relative);
	    mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        mSensorManager.registerListener(this, mRotationVectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
        mRotationVectorSensor = mSensorManager.getDefaultSensor(
                Sensor.TYPE_ROTATION_VECTOR);
        if(StrykerHATActivity2.misServer != true)
     		{
     			
     			tvInclination.setVisibility(View.GONE);
     			tvAnteversion.setVisibility(View.GONE);
     			tvX_relative.setVisibility(View.GONE);
     			tvX_remote.setVisibility(View.GONE);
     		}
        player = MediaPlayer.create(this,
			    Settings.System.DEFAULT_RINGTONE_URI);
        try {
			player.prepare();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
   	}



	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	 @Override
	    public synchronized void onResume() {
	        super.onResume();
	        mSensorManager.registerListener(this, mRotationVectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
	   	        	            
	 }
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		
		
		
		 if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
             // convert the rotation-vector to a 4x4 matrix. the matrix
             // is interpreted by Open GL as the inverse of the
             // rotation-vector, which is what we want.
             SensorManager.getRotationMatrixFromVector(
                     mRotationMatrix , event.values);
             float[] values = new float[4];
             SensorManager.getOrientation(mRotationMatrix,values);
             double deg = Math.toDegrees(values[1]);
             mLastX = (float)Math.round(deg);
             
             
             deg = Math.toDegrees(values[2]);
             mLastY = (float)Math.round(deg);
             
             deg = Math.toDegrees(values[0]);
             mLastZ = (float)Math.round(deg);
             
          
             
         }

		
		
		tvX.setText("0.0");

		float curr_Val = 0;
		if(StrykerHATActivity2.Reference == 0)
		{
			tvX.setText(Integer.toString((int)mLastX) + "(X)");
			tvX_relative.setText(Integer.toString((int)mLastX_remote));
			tvX_remote.setText(Integer.toString((int)mLastX - (int)mLastX_remote));
			tvX.setBackgroundColor(Color.RED);
			curr_Val = mLastX;
			
		}
		else if(StrykerHATActivity2.Reference == 1){
			tvX.setText(Integer.toString((int)mLastY)+ "(Y)");
			tvX_relative.setText(Integer.toString((int)mLastY_remote));
			tvX_remote.setText(Integer.toString((int)mLastY - (int)mLastY_remote));
			tvX.setBackgroundColor(Color.BLUE);
			curr_Val = mLastY;
		}
		else if(StrykerHATActivity2.Reference == 2)
		{
			tvX.setBackgroundColor(Color.BLACK);
			tvX.setText(Integer.toString((int)mLastZ)  + "(Z)");
			tvX_relative.setText(Integer.toString((int)mLastZ_remote));
			tvX_remote.setText(Integer.toString((int)mLastZ - (int)mLastZ_remote));
			curr_Val = mLastZ;
		}
		
		if(StrykerHATActivity2.misServer == true)
		{
			tvInclination.setText(Integer.toString((int)mLastY - (int)mLastZ_remote) + "(I)     ");
			tvAnteversion.setText(Integer.toString((int)mLastZ - (int)mLastX_remote) + "(A)");

		}
		
		
		Message = ":X:" + mLastX + ":Y:" + mLastY + ":Z:" + mLastZ;
		if(StrykerHATActivity2.misServer != true)
			{
				StrykerHATActivity2.sendMessage(Integer.toString((int) mLastX) + "," +
						Integer.toString((int) mLastY)+ ","  + Integer.toString((int) mLastZ));
				
			}
		
		if(Math.abs(curr_Val) < 5)
		{
			
				try {
					player.prepare();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//player.start();


		}
		else
		{
			if(player.isPlaying())
				player.pause();
		}
		
	}
	
	 @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        MenuInflater inflater = getMenuInflater();
	        inflater.inflate(R.menu.option_menu, menu);
	        return true;
	    }

	    @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        Intent serverIntent = null;
	        switch (item.getItemId()) {
	        case R.id.ref_x:
	        	StrykerHATActivity2.Reference = 0;
	            return true;
	            
     	case R.id.ref_y:
     		StrykerHATActivity2.Reference = 1;
	            return true;
	            
     	case R.id.ref_z:
     		StrykerHATActivity2.Reference = 2;
 			return true;
	        }
	        return true;
	    }
    
	public static void updateUI(float rel_x, float rel_y, float rel_z)
	{
		
		if(rel_tvX != null)
		rel_tvX.setText(Float.toString(mLastX - rel_x));
		if(rel_tvY != null)
		rel_tvY.setText(Float.toString(mLastY- rel_y));
		if(rel_tvZ != null)
		rel_tvZ.setText(Float.toString(mLastZ- rel_z));
	
	
	}

    private SensorManager mSensorManager;
    public static float mLastX, mLastY, mLastZ;
    public static float mLastX_remote, mLastY_remote, mLastZ_remote;
    public static float mCalibratedX, mCalibratedY, mCalibratedZ;
    private Sensor mAccelerometer;
   
    
    public static TextView tvX, tvY, tvZ, rel_tvX, rel_tvY, rel_tvZ, tvX_relative, tvX_remote;
    public static TextView tvInclination, tvAnteversion;
    //public static EditText rel_tvX,rel_tvY, rel_tvZ;
}
