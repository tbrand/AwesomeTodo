package mobi.appapp.awesometodo.floating_view;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import mobi.appapp.awesometodo.main.MainActivity;
import mobi.appapp.awesometodo.R;
import mobi.appapp.awesometodo.model.Task;

/**
 * Created by taicsuzu on 2017/05/20.
 */

public class FloatingView extends FloatingWindow implements View.OnTouchListener{
    private int downX, downY;
    private boolean moving = false;
    private RealmResults<Task> remainingTasks;

    public FloatingView(Context context) {
        super(context);
    }

    @Override
    protected WindowManager.LayoutParams setLayoutParams() {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                (int)getContext().getResources().getDimension(R.dimen.floating_view_width),
                (int)getContext().getResources().getDimension(R.dimen.floating_view_height),
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL|WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);

        Point display = new Point();
        getWindowManager().getDefaultDisplay().getSize(display);
        params.x = display.x / 3;
        params.y = -display.y / 3;

        return params;
    }

    @Override
    protected View onCreateView() {
        View view;

        view = getLayoutInflater().inflate(R.layout.floating_view, null);
        view.setOnTouchListener(this);

        ((MovableFrameLayout)view).setWindowManagerLayoutParams(
                getWindowManager(),
                getLayoutParams()
        );

        ((MovableFrameLayout)view).setOrientation(getContext().getResources().getConfiguration().orientation);

        final TextView taskNum = (TextView)view.findViewById(R.id.floating_view_remaining_tasks);

        remainingTasks = Realm.getDefaultInstance()
                .where(Task.class)
                .equalTo("isDone", false)
                .findAll();

        taskNum.setText(remainingTasks.size() + "");

        remainingTasks.addChangeListener(new RealmChangeListener<RealmResults<Task>>() {
            @Override
            public void onChange(RealmResults<Task> tasks) {
                taskNum.setText(tasks.size() + "");
            }
        });

        return view;
    }

    protected void updateOrientation(){
        ((MovableFrameLayout)getView()).setOrientation(getContext().getResources().getConfiguration().orientation);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                actionDown(event);
                return false;
            case MotionEvent.ACTION_MOVE:
                actionMove(event);
                return false;
            case MotionEvent.ACTION_UP:
                actionUp(event);
                return false;
        }
        return false;
    }

    private void actionDown(MotionEvent event){
        downX = (int)event.getRawX();
        downY = (int)event.getRawY();
        moving = false;
    }

    private void actionMove(MotionEvent event){
        if((Math.abs(downY-event.getRawY()) > getPixelFromDp(8) && Math.abs(downX-event.getRawX()) > getPixelFromDp(8)) || moving){
            int nowMoveX = (int)event.getRawX();
            int nowMoveY = (int)event.getRawY() - (int)getContext().getResources().getDimension(R.dimen.floating_view_height)/2;
            ((MovableFrameLayout)getView()).move(nowMoveX, nowMoveY);
            moving = true;
        }
    }

    private void actionUp(MotionEvent event){
        if(!moving){
            launchMainActivity();
            moving = false;
        }
    }

    private void launchMainActivity(){
        Intent mainIntent = new Intent(getContext(), MainActivity.class);
        getContext().startActivity(mainIntent);
    }

    private int getPixelFromDp(float dp){
        float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public void onDestroy(){
        if(remainingTasks != null) {
            remainingTasks.removeAllChangeListeners();
        }
    }
}
