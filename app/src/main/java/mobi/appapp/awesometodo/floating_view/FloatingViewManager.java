package mobi.appapp.awesometodo.floating_view;

import android.content.Context;

/**
 * Created by taicsuzu on 2017/05/21.
 */

public class FloatingViewManager {

    private static FloatingViewManager floatingViewManager;

    public static FloatingViewManager getInstance(Context context){
        if(floatingViewManager == null){
            floatingViewManager = new FloatingViewManager(context);
        }

        return floatingViewManager;
    }

    private FloatingView floatingView;

    private FloatingViewManager(Context context){
        floatingView = new FloatingView(context);
    }

    public void addViewOnWindow(){
        if(floatingView != null){
            floatingView.addViewOnWindow();
        }
    }

    public void removeViewOnWindow(){
        if(floatingView != null){
            floatingView.removeFromWindowIfShowing();
        }
    }

    void updateViewOrientation(){
        if (floatingView != null) {
            floatingView.updateOrientation();
        }
    }
}
