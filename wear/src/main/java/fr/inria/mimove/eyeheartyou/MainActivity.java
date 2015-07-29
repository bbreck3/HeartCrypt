package fr.inria.mimove.eyeheartyou;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.TextView;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.wearable.*;
import com.google.android.gms.common.api.ResultCallback;
import java.util.List;

public class MainActivity extends Activity implements SensorEventListener , MessageApi.MessageListener, GoogleApiClient.ConnectionCallbacks{

    private TextView mTextView;

    private Sensor mHeartRateSensor;
    private SensorManager mSensorManager;

    private static final String TAG = "EyeHeartYou";

    public MainActivity(){

    }


    private GoogleApiClient client;


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"going to register!");
        mSensorManager.registerListener(this, mHeartRateSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        Log.d(TAG,"going to unregister!");
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

        mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);//getDefaultSensor(STRING_TYPE_HEART_RATE);//Sensor.TYPE_HEART_RATE);

        setContentView(R.layout.activity_main);

        client = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        Log.i("test", "Connection failed");
                    }
                })
                .addApi(Wearable.API)
                .build();

        client.connect();
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
                mTextView.setText("Measuring...");
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Log.i(TAG, "--------------------------");
        //Log.i(TAG, msg);
        Log.i(TAG, ""+ sensorEvent.sensor.getType());
    Log.i(TAG, ""+sensorEvent.values[0]);
        Log.i("live","--------------");
        sendMessage(String.valueOf( sensorEvent.values[0]), null);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
    private void sendMessage(final String message, final byte[] payload) {
        Log.i(TAG, "WEAR Sending message " + message);
        Wearable.NodeApi.getConnectedNodes(client).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
            @Override
            public void onResult(NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                List<Node> nodes = getConnectedNodesResult.getNodes();
                for (Node node : nodes) {
                    Log.i(TAG, "WEAR sending " + message + " to " + node);
                    mTextView.setText(message);
                    Wearable.MessageApi.sendMessage(client, node.getId(), message, payload).setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                        @Override
                        public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                            Log.i(TAG, "WEAR Result " + sendMessageResult.getStatus());
                              //  mTextView.setText(node.getId().toString());
                        }
                    });
                }

            }
        });
    }


    @Override
    public void onConnected(Bundle bundle) {
        Wearable.MessageApi.addListener(client, this);
        sendMessage("/start", null);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection failed");
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Wearable.MessageApi.removeListener(client, this);
        client.disconnect();
    }
}
