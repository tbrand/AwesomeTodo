package mobi.appapp.awesometodo.main;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import es.dmoral.toasty.Toasty;
import io.realm.Realm;
import io.realm.RealmResults;
import mobi.appapp.awesometodo.R;
import mobi.appapp.awesometodo.floating_view.FloatingViewManager;
import mobi.appapp.awesometodo.floating_view.FloatingViewService;
import mobi.appapp.awesometodo.model.Task;
import mobi.appapp.awesometodo.utils.PreferenceWrapper;

public class MainActivity extends AppCompatActivity {
    private final static int REQUEST_OVERLAY_PERMISSION = 101;
    private TaskAdapter taskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTaskList();
        setAddTask();
        setMode();
        setView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQUEST_OVERLAY_PERMISSION: {
                startServiceIfNotRunning();
            } break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(canUseOverlay()) {
            FloatingViewManager manager = FloatingViewManager.getInstance(this);
            manager.removeViewOnWindow();
        }
    }

    @Override
    protected void onPause() {
        PreferenceWrapper preferenceWrapper = PreferenceWrapper.getInstance(this);

        if(preferenceWrapper.getMode() && canUseOverlay()) {
            FloatingViewManager manager = FloatingViewManager.getInstance(this);
            manager.addViewOnWindow();
        }

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if(taskAdapter != null){
            taskAdapter.onDestroy();
        }

        super.onDestroy();
    }

    private void startServiceIfNotRunning(){
        if (!isFloatingViewServiceRunning(FloatingViewService.class)) {
            Intent service = new Intent(this, FloatingViewService.class);
            startService(service);
        }
    }

    private void stopServiceIfRunning(){
        if (isFloatingViewServiceRunning(FloatingViewService.class)) {
            Intent service = new Intent(this, FloatingViewService.class);
            stopService(service);
        }
    }

    private boolean isFloatingViewServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private boolean canUseOverlay(){
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(this)) {
                // Need to acquire overlay permission
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_OVERLAY_PERMISSION);
                return false;
            } else {
                // Acquired
                return true;
            }
        }else{
            // No need to check
            return true;
        }
    }

    private void setTaskList(){
        taskAdapter = new TaskAdapter(this);
        taskAdapter.onCreate();

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.main_recycler_view);
        recyclerView.setAdapter(taskAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setAddTask(){
        final EditText taskContent = (EditText)findViewById(R.id.main_add_task_content);

        findViewById(R.id.main_add_task).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String _taskContent = taskContent.getText().toString();

                if(_taskContent.length() > 0){
                    addTask(_taskContent);

                    taskContent.setText("");
                    hideKeyboard(v);
                }
            }
        });
    }

    private void setMode(){
        findViewById(R.id.main_switch_mode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferenceWrapper preferenceWrapper = PreferenceWrapper.getInstance(MainActivity.this);
                preferenceWrapper.setMode(!preferenceWrapper.getMode());
                setModeView();
            }
        });

        setModeView();
    }

    private void setModeView(){
        View backgroundRight = findViewById(R.id.main_mode_background_right);
        View backgroundLeft  = findViewById(R.id.main_mode_background_left);

        TextView textRight = (TextView)findViewById(R.id.main_mode_text_right);
        TextView textLeft  = (TextView)findViewById(R.id.main_mode_text_left);

        PreferenceWrapper preferenceWrapper = PreferenceWrapper.getInstance(this);

        if(preferenceWrapper.getMode()){
            textLeft.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
            textRight.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));

            backgroundLeft.setBackgroundResource(R.drawable.main_switch_on_left);
            backgroundRight.setBackgroundResource(R.drawable.main_switch_on_right);

            startServiceIfNotRunning();
        }else{
            textLeft.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
            textRight.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));

            backgroundLeft.setBackgroundResource(R.drawable.main_switch_off_left);
            backgroundRight.setBackgroundResource(R.drawable.main_switch_off_right);

            stopServiceIfRunning();
        }
    }

    private void addTask(final String content){
        Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Task task = realm.createObject(Task.class);
                task.setDone(false);
                task.setNext(false);
                task.setCreatedAt(System.currentTimeMillis());
                task.setContent(content);
            }
        });
    }

    private void hideKeyboard(View view){
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void setView(){

        findViewById(R.id.main_delete_all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final RealmResults<Task> doneTasks = Realm.getDefaultInstance()
                        .where(Task.class)
                        .equalTo("isDone", true)
                        .findAll();

                final int deleteNum = doneTasks.size();

                if(deleteNum == 0)
                    return;

                Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        doneTasks.deleteAllFromRealm();

                        Toasty.success(
                                MainActivity.this,
                                String.format("Done %d tasks!!", deleteNum)
                        ).show();
                    }
                });
            }
        });
    }
}
