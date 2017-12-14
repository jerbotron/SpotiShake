package com.jerbotron_mac.spotishake.activities.home.custom;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class ShakeDetector implements SensorEventListener {

    private static final float SHAKE_THRESHOLD_GRAVITY = 2.7F;

    private OnShakeListener onShakeListener;

    private final SensorManager sensorManager;
    private final Sensor sensor;

    public ShakeDetector(SensorManager sensorManager) {
        this.sensorManager = sensorManager;
        this.sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (onShakeListener == null) {
            unregisterListener();
        }

        float gX = event.values[0] / SensorManager.GRAVITY_EARTH;
        float gY = event.values[1] / SensorManager.GRAVITY_EARTH;
        float gZ = event.values[2] / SensorManager.GRAVITY_EARTH;
        double gForce = Math.sqrt(gX*gX + gY*gY + gZ*gZ);
        if (gForce > SHAKE_THRESHOLD_GRAVITY) {
            onShakeListener.onShake();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void registerShakeListener(OnShakeListener onShakeListener) {
        if (sensorManager == null || sensor == null || onShakeListener == null) {
            return;
        }
        this.onShakeListener = onShakeListener;
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
    }

    public void unregisterListener() {
        this.onShakeListener = null;
        if (sensorManager != null && sensor != null) {
            sensorManager.unregisterListener(this, sensor);
        }
    }

    public interface OnShakeListener {
        void onShake();
    }
}
