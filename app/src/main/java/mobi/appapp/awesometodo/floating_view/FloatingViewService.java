package mobi.appapp.awesometodo.floating_view;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import mobi.appapp.awesometodo.main.MainActivity;
import mobi.appapp.awesometodo.R;
import mobi.appapp.awesometodo.model.Task;

/**
 * Created by taicsuzu on 2017/05/20.
 */

public class FloatingViewService extends Service {
    private static int NOTIFICATION_ID = 249;
    private RealmResults<Task> remainingTask;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        remainingTask = Realm.getDefaultInstance()
                .where(Task.class)
                .equalTo("isDone", false)
                .findAll();

        remainingTask.addChangeListener(new RealmChangeListener<RealmResults<Task>>() {
            @Override
            public void onChange(RealmResults<Task> tasks) {
                updateNotification();
            }
        });

        updateNotification();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        if(remainingTask != null){
            remainingTask.removeAllChangeListeners();
        }

        stopSelf(NOTIFICATION_ID);
        super.onDestroy();
    }

    protected void updateNotification(){
        Intent mainIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, mainIntent, 0);

        Notification notification;

        Notification.Builder builder = new Notification.Builder(this);
        builder.setSmallIcon(R.drawable.ic_notification);
        builder.setContentTitle("What to do next?");
        builder.setContentIntent(pendingIntent);

        if(remainingTask == null || remainingTask.size() == 0){
            builder.setContentText("All tasks have been done!");
        }else{
            builder.setContentText(remainingTask.get(0).getContent());
        }

        // Notificationから時刻を消す
        builder.setWhen(0);

        if(Build.VERSION.SDK_INT >= 16) {
            notification = builder.build();
        }else {
            notification = builder.getNotification();
        }

        startForeground(NOTIFICATION_ID, notification);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        FloatingViewManager floatingViewManager = FloatingViewManager.getInstance(this);
        floatingViewManager.updateViewOrientation();
    }
}
