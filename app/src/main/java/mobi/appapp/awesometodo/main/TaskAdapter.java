package mobi.appapp.awesometodo.main;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;
import mobi.appapp.awesometodo.R;
import mobi.appapp.awesometodo.model.Task;

/**
 * Created by taicsuzu on 2017/05/20.
 */

public class TaskAdapter extends RecyclerView.Adapter{
    private Activity activity;
    private DisplayMetrics metrics;
    private RealmResults<Task> tasks;

    TaskAdapter(Activity activity){
        this.activity = activity;
        this.metrics = activity.getResources().getDisplayMetrics();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = activity.getLayoutInflater().inflate(R.layout.row_task, null);

        RecyclerView.LayoutParams params =
                new RecyclerView.LayoutParams(
                        RecyclerView.LayoutParams.MATCH_PARENT,
                        (int)(metrics.density * 60)
                );

        view.setLayoutParams(params);
        return new TaskRow(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Task task = tasks.get(position);

        TaskRow taskRow = (TaskRow)holder;
        taskRow.content.setText(task.getContent());

        if(task.isDone()){
            taskRow.status.setImageResource(R.drawable.ic_task_done);
            taskRow.content.setTextColor(ContextCompat.getColor(activity, R.color.colorAccentThin));
        }else{
            taskRow.status.setImageResource(R.drawable.ic_task_doing);
            taskRow.content.setTextColor(ContextCompat.getColor(activity, R.color.colorAccent));
        }

        taskRow.clickable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        task.setDone(!task.isDone());
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return tasks == null ? 0 : tasks.size();
    }

    public void onCreate(){
        tasks = Realm.getDefaultInstance()
                .where(Task.class)
                .findAllSorted("createdAt", Sort.DESCENDING);

        tasks.addChangeListener(new RealmChangeListener<RealmResults<Task>>() {
            @Override
            public void onChange(RealmResults<Task> tasks) {
                notifyDataSetChanged();
            }
        });
    }

    public void onDestroy(){
        tasks.removeAllChangeListeners();
    }

    class TaskRow extends RecyclerView.ViewHolder{
        View clickable;
        TextView content;
        ImageView status;

        public TaskRow(View itemView) {
            super(itemView);
            clickable = itemView.findViewById(R.id.row_task_clickable);
            content = (TextView)itemView.findViewById(R.id.row_task_content);
            status = (ImageView) itemView.findViewById(R.id.row_task_status);
        }
    }
}
