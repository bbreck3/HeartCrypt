package fr.inria.mimove.eyeheartyou;


        import android.util.Log;
        import com.google.android.gms.wearable.DataEventBuffer;
        import com.google.android.gms.wearable.MessageEvent;
        import com.google.android.gms.wearable.Node;
        import com.google.android.gms.wearable.WearableListenerService;
        import android.support.v4.content.LocalBroadcastManager;
        import android.content.Context;
        import android.content.Intent;
        import android.content.IntentFilter;
        import android.content.BroadcastReceiver;

public class WearService extends WearableListenerService {
public static String value;
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(WearService.class.getSimpleName(), "WEAR create");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(WearService.class.getSimpleName(), "WEAR destroy");
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        super.onDataChanged(dataEvents);
        Log.i(WearService.class.getSimpleName(), "WEAR Data changed " );
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);
        Log.i(WearService.class.getSimpleName(), "WEAR Message that we just received is " + messageEvent.getPath());
        value = messageEvent.getPath();
        Intent messageIntent = new Intent();
        messageIntent.setAction(Intent.ACTION_SEND);
        messageIntent.putExtra("heart_value", value);
        LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);

    }

    @Override
    public void onPeerConnected(Node peer) {
        super.onPeerConnected(peer);
        Log.i(WearService.class.getSimpleName(), "WEAR Connected ");
    }

    @Override
    public void onPeerDisconnected(Node peer) {
        super.onPeerDisconnected(peer);
        Log.i(WearService.class.getSimpleName(), "WEAR Disconnected");
    }
}