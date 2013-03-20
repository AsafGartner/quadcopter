package com.asafg.quadcopter;

import ioio.lib.api.PwmOutput;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOActivity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class Main extends IOIOActivity implements SensorEventListener {
	private SeekBar sb1;
	private Button b1;
	private Button b2;
	private Button b3;
	private Button b4;
	private Button b5;
	
	private Button turnLeftButton;
	private Button turnRightButton;
	
	private ProgressBar pbm1;
	private ProgressBar pbm2;
	private ProgressBar pbm3;
	private ProgressBar pbm4;
	
	private int tiltAmount = 100;
	private int turnAmount = 50;
	
	private boolean turningRight = false;
	private boolean turningLeft = false;
	
	private int prog1 = 0;
	private int motor1 = 0;
	private int motor2 = 0;
	private int motor3 = 0;
	private int motor4 = 0;
	
	private SensorManager sensorManager;
	private Sensor accelerometer;
	
	private float[] tiltPos = {0, 0, 0};
	private ProgressBar pb1;
	private RelativeLayout rl1;
	private TextView tvX;
	private TextView tvY;
	private TextView tvZ;
	
	private int sensorRate = SensorManager.SENSOR_DELAY_UI;

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, sensorRate);
        pb1 = (ProgressBar)findViewById(R.id.progressBar1);
        rl1 = (RelativeLayout)findViewById(R.id.relativeLayout1);
        tvX = (TextView)findViewById(R.id.textView1);
        tvY = (TextView)findViewById(R.id.textView2);
        tvZ = (TextView)findViewById(R.id.textView3);
        
        pbm1 = (ProgressBar)findViewById(R.id.progressBar2);
        pbm2 = (ProgressBar)findViewById(R.id.progressBar3);
        pbm3 = (ProgressBar)findViewById(R.id.progressBar4);
        pbm4 = (ProgressBar)findViewById(R.id.progressBar5);
        
        sb1 = (SeekBar)findViewById(R.id.seekBar1);
        b1 = (Button)findViewById(R.id.button1);
        b2 = (Button)findViewById(R.id.button2);
        b3 = (Button)findViewById(R.id.button3);
        b4 = (Button)findViewById(R.id.button4);
        b5 = (Button)findViewById(R.id.button5);
        
        turnRightButton = (Button)findViewById(R.id.turnRightButton);
        turnLeftButton = (Button)findViewById(R.id.turnLeftButton);
        
        sb1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override public void onStopTrackingTouch(SeekBar seekBar) {}
			@Override public void onStartTrackingTouch(SeekBar seekBar) {}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				prog1 = progress;
				updateMotors();
			}
		});
        
        b1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sb1.setProgress(0);
			}
		});
        
        b2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sb1.setProgress(500);
			}
		});
        
        b3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sb1.setProgress(1000);
			}
		});
        
        b4.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sb1.setProgress(sb1.getProgress()+1);
			}
		});
        
        b5.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sb1.setProgress(sb1.getProgress()-1);
			}
		});
        
        turnRightButton.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					turningRight = false;
				} else if (event.getAction() == MotionEvent.ACTION_DOWN){
					turningRight = true;
				}
				return false;
			}
		});
        
        turnLeftButton.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					turningLeft = false;
				} else if (event.getAction() == MotionEvent.ACTION_DOWN){
					turningLeft = true;
				}
				return false;
			}
		});
        
        enableUi(false);
    }
	
	class Looper extends BaseIOIOLooper {
		private PwmOutput pwmOutput1;
		private PwmOutput pwmOutput2;
		private PwmOutput pwmOutput3;
		private PwmOutput pwmOutput4;

		
		@Override
		public void setup() throws ConnectionLostException {
			try {
				pwmOutput1 = ioio_.openPwmOutput(3, 500);
				pwmOutput2 = ioio_.openPwmOutput(4, 500);
				pwmOutput3 = ioio_.openPwmOutput(5, 500);
				pwmOutput4 = ioio_.openPwmOutput(6, 500);
				enableUi(true);
			} catch (ConnectionLostException e) {
				enableUi(false);
				throw e;
			}
		}
		
		@Override
		public void loop() throws ConnectionLostException {
			try {
				pwmOutput1.setDutyCycle(((float)motor4)/1000.0f);
				pwmOutput2.setDutyCycle(((float)motor3)/1000.0f);
				pwmOutput3.setDutyCycle(((float)motor2)/1000.0f);
				pwmOutput4.setDutyCycle(((float)motor1)/1000.0f);
				Thread.sleep(10);
			} catch (InterruptedException e) {
				ioio_.disconnect();
			} catch (ConnectionLostException e) {
				enableUi(false);
				throw e;
			}
		}
	}

	@Override
	protected IOIOLooper createIOIOLooper() {
		return new Looper();
	}

	private void enableUi(final boolean enable) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
			}
		});
	}
	
	protected void onResume() {
		super.onResume();
		sensorManager.registerListener(this, accelerometer, sensorRate);
	}
	
	protected void onPause() {
		super.onPause();
		sensorManager.unregisterListener(this);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		float[] vector = {event.values[0], event.values[1], event.values[2]};
		float[] normal = {0, 0, -1.0f};
		float dot = vector[0] * normal[0] + vector[1] * normal[1] + vector[2] * normal[2];
		float[] scaled_normal = {normal[0]*dot, normal[1]*dot, normal[2]*dot};
		tiltPos[0] = vector[0] - scaled_normal[0];
		tiltPos[1] = vector[1] - scaled_normal[1];
		tiltPos[2] = vector[2] - scaled_normal[2];
		
		tiltPos[0] /= 10.0;
		tiltPos[1] /= 10.0;
		tiltPos[2] /= 10.0;
		
		tvX.setText(Float.toString(tiltPos[0]));
		tvY.setText(Float.toString(tiltPos[1]));
		tvZ.setText(Float.toString(tiltPos[2]));
		
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(16, 16);
		int halfWidth = rl1.getWidth()/2;
		int halfHeight = rl1.getHeight()/2;
		params.leftMargin = (int)(-tiltPos[1] * halfWidth + halfWidth - 8);
		params.topMargin = (int)(-tiltPos[0] * halfHeight + halfHeight - 8);
		pb1.setLayoutParams(params);
		
		updateMotors();
	}
	
	private void updateMotors() {
		motor1 = motor2 = motor3 = motor4 = prog1;
		
		if (prog1 >= 500) {
			motor1 += tiltAmount * tiltPos[1];
			motor2 += tiltAmount * tiltPos[0];
			motor3 -= tiltAmount * tiltPos[1];
			motor4 -= tiltAmount * tiltPos[0];
			

			if (turningLeft) {
				motor1 -= turnAmount;
				motor2 += turnAmount;
				motor3 -= turnAmount;
				motor4 += turnAmount;
			}
			
			if (turningRight) {
				motor1 += turnAmount;
				motor2 -= turnAmount;
				motor3 += turnAmount;
				motor4 -= turnAmount;
			}
			
			motor1 = Math.min(Math.max(500, motor1), 1000);
			motor2 = Math.min(Math.max(500, motor2), 1000);
			motor3 = Math.min(Math.max(500, motor3), 1000);
			motor4 = Math.min(Math.max(500, motor4), 1000);
		}
		
		pbm1.setProgress((motor1 - 500)*2);
		pbm2.setProgress((motor2 - 500)*2);
		pbm3.setProgress((motor3 - 500)*2);
		pbm4.setProgress((motor4 - 500)*2);
	}
}
