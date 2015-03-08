package com.thalmic.myo.main;

import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Pose;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.Vector3;
import com.thalmic.myo.enums.Arm;
import com.thalmic.myo.enums.PoseType;
import com.thalmic.myo.enums.VibrationType;
import com.thalmic.myo.enums.XDirection;

public class DataCollector extends AbstractDeviceListener {
	private Vector3 accel,gyro;
	private Pose currentPose;
	private Lumberjack l;
	private String mode;
	public DataCollector(Lumberjack main) {
		l=main;
		accel=new Vector3();
		gyro=new Vector3();
		currentPose = new Pose();
		mode="";
	}
	public void setMode(String mode)
	{
		this.mode=mode;
	}

	 @Override
	    public void onPose(Myo myo, long timestamp, Pose pose) {
			currentPose = pose;
			try {
				if (currentPose.getType() == PoseType.FIST) {
				    l.select();
				}
				else if (currentPose.getType() == PoseType.WAVE_IN || currentPose.getType() == PoseType.WAVE_OUT) {
					l.changeSelection(currentPose.getType());
				}
			}
			catch (Exception e) {
				System.err.println("Error: ");
				e.printStackTrace();
				System.exit(1);
			}
	    }
	@Override
	public void onAccelerometerData(Myo myo, long timestamp, Vector3 accel) {
		this.accel=accel;
		if(mode=="punch"&&accel.getX()>1)
			l.movement("punch",accel.getX());
	}
	@Override
	public void onGyroscopeData(Myo myo, long timestamp, Vector3 gyro) {
		this.gyro=gyro;
		if(mode=="hack"&&gyro.getY()>200&&gyro.getZ()>125)
			l.movement("hack",gyro.getZ());
		if(mode=="chop"&&gyro.getY()>300&&accel.getZ()>0.5)
			l.movement("chop",accel.getZ());
		if(mode=="slash"&&gyro.getZ()<-400)
			l.movement("slash",gyro.getZ());
	}
	@Override
	public String toString() {
		return accel.toString();
	}

	private String repeatCharacter(char character, int numOfTimes) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < numOfTimes; i++) {
			builder.append(character);
		}
		return builder.toString();
	}
}