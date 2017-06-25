package mobi.appapp.awesometodo.floating_view;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Build;
import android.util.AttributeSet;
import android.view.WindowManager;
import android.widget.FrameLayout;

/**
 * 動かすことが可能なカスタムFrameLayout
 *
 * Created by taicsuzu on 2015/03/13.
 */
public class MovableFrameLayout extends FrameLayout {
    private WindowManager wm;
    private Point windowSize = new Point();
    private WindowManager.LayoutParams params;
    private int orientation = Configuration.ORIENTATION_PORTRAIT;

    public MovableFrameLayout(Context context){
        super(context);
    }

    public MovableFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MovableFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * WindowManagerとLayoutParamsをセットする
     * @param wm WindowManager
     * @param params LayoutParams
     */
    public void setWindowManagerLayoutParams(WindowManager wm, WindowManager.LayoutParams params) {
        this.wm = wm;

        if(Build.VERSION.SDK_INT >= 13) {
            this.wm.getDefaultDisplay().getSize(windowSize);
        }

        this.params = params;
    }

    /**
     * Orientationのセット
     * @param orientation 向き
     */
    public void setOrientation(int orientation){
        this.orientation = orientation;
    }

    /**
     * 指定の座標にViewを動かす
     * @param x x座標
     * @param y y座標
     */
    public void move(int x, int y){

        if(orientation == Configuration.ORIENTATION_PORTRAIT) {
            params.x = x - windowSize.x / 2;
            params.y = y - windowSize.y / 2;
        }else if(orientation == Configuration.ORIENTATION_LANDSCAPE) {
            params.x = x - windowSize.y / 2;
            params.y = y - windowSize.x / 2;
        }

        wm.updateViewLayout(this, params);
    }
}
