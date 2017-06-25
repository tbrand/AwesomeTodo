package mobi.appapp.awesometodo.model;

import io.realm.RealmObject;

/**
 * Created by taicsuzu on 2017/05/21.
 */

public class Task extends RealmObject{

    private String content;
    private boolean isDone;
    private boolean isNext;
    private long createdAt;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public boolean isNext() {
        return isNext;
    }

    public void setNext(boolean next) {
        isNext = next;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public static Task mockTask(String content, boolean isDone, boolean isNext){
        Task task = new Task();
        task.setContent(content);
        task.setDone(isDone);
        task.setNext(isNext);
        return task;
    }
}
