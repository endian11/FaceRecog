package facemeet.cigit.com.facedemet.customview;

/**
 * Created by lizhanwei on 17/9/12.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import facemeet.cigit.com.facedemet.event.writeDoneEvent;
import facemeet.cigit.com.facedemet.util.HttpConnectionUtil;

/**
 * 圆形SurfaceView
 * 这个SurfaceView 使用时 必须设置其background，可以设置全透明背景
 * @author lizhanwei
 */
public class RectSurfaceView extends SurfaceView implements SurfaceHolder.Callback,Camera.PictureCallback {

    private static final String TAG = "MySurfaceView";
    private Paint paint;
    private Camera camera;
    private int height; // 矩形的高
    private int width;//矩形的宽
    private Context mcontext;

//    private String url_detect = "http://192.168.253.1:10080/API/FaceDetectWebSer.ashx";
    private String mPictureFileName;

    public RectSurfaceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mcontext = context;
        initView();
    }

    public RectSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mcontext = context;
        initView();
    }

    public RectSurfaceView(Context context) {
        super(context);
        this.mcontext = context;
        initView();
    }


    private void initView() {

        this.setFocusable(true);
        this.setFocusableInTouchMode(true);
        getHolder().addCallback(this);
    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);


        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        height=heightSize;
        width = widthSize;

        Log.e("onMeasure", "draw: widthMeasureSpec = " +widthSize + "  heightMeasureSpec = " + heightSize);

        setMeasuredDimension(widthSize, heightSize);



    }

    @Override
    public void draw(Canvas canvas) {
        Log.e("onDraw", "draw: test");
        Path path = new Path();
//        path.ad(height / 2, height / 2, height / 2, Path.Direction.CCW);
        RectF rect = new RectF(0,0,width,height);
        path.addRect(rect, Path.Direction.CCW);
        canvas.clipPath(path, Region.Op.REPLACE);
        super.draw(canvas);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.e("onDraw", "onDraw");
        super.onDraw(canvas);
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.e("onDraw", "surfaceCreated");

        startC(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        Log.e("onDraw", "surfaceChanged");
        if (holder.getSurface() == null) {
            //检查SurfaceView是否存在
            return;
        }

        //改变设置前先关闭相机
        try {
            camera.stopPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //使用最佳比例配置重启相机
        try {
            camera.setPreviewDisplay(holder);
            final Camera.Parameters parameters = camera.getParameters();
            final Camera.Size size = getBestPreviewSize(width, height);
            parameters.setPreviewSize(640, 480);
            parameters.setPictureSize(640,480);
            camera.setParameters(parameters);
            camera.startPreview();
        } catch (Exception e) {
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.e("onDraw", "surfaceDestroyed");
        if (camera != null) {
            camera.stopPreview();
            camera.release();
        }
        if (EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
    }


    private void startC(SurfaceHolder holder) {
        System.out.println("surfacecreated");
        //获取camera对象
        camera = Camera.open();
        try {
            //设置预览监听
            camera.setPreviewDisplay(holder);
            Camera.Parameters parameters = camera.getParameters();
            if (this.getResources().getConfiguration().orientation
                    != Configuration.ORIENTATION_LANDSCAPE) {
                parameters.set("orientation", "portrait");
                camera.setDisplayOrientation(90);
                parameters.setRotation(90);
            } else {
                parameters.set("orientation", "landscape");
                camera.setDisplayOrientation(0);
                parameters.setRotation(0);
            }
            camera.setParameters(parameters);
            //启动摄像头预览
            camera.startPreview();
            System.out.println("camera.startpreview");

        } catch (IOException e) {
            e.printStackTrace();
            camera.release();
            System.out.println("camera.release");
        }
    }


    public void takePicture(final String fileName) {
        Logger.i(TAG, "Taking picture");
        this.mPictureFileName = fileName;
        // Postview and jpeg are sent in the same buffers if the queue is not empty when performing a capture.
        // Clear up buffers to avoid mCamera.takePicture to be stuck because of a memory issue
//        camera.setPreviewCallback(null);
        // PictureCallback is implemented by the current class
        camera.takePicture(null, null, this);
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        Log.i(TAG, "Saving a bitmap to file");
        // The camera preview was automatically stopped. Start it again.
        camera.startPreview();
//        camera.setPreviewCallback(this);

        // Write the image in a file (in jpeg format)
        try {
            FileOutputStream fos = new FileOutputStream(mPictureFileName);
            Logger.d("PictureFileName:"+mPictureFileName);
            fos.write(data);
            fos.close();
            if (mcontext != null){

                Intent intent = new Intent();
                intent.setAction("com.cigit.reg");
                intent.putExtra("name",mPictureFileName);
                mcontext.sendBroadcast(intent);
            }

        } catch (IOException e) {
            Log.e("PictureDemo", "Exception in photoCallback", e);
        }


    }

    private Camera.Size getBestPreviewSize(int width, int height) {
        Camera.Size result = null;
        final Camera.Parameters p = camera.getParameters();
        //特别注意此处需要规定rate的比是大的比小的，不然有可能出现rate = height/width，但是后面遍历的时候，current_rate = width/height,所以我们限定都为大的比小的。
        float rate = (float) Math.max(width, height)/ (float)Math.min(width, height);
        float tmp_diff;
        float min_diff = -1f;
        for (Camera.Size size : p.getSupportedPreviewSizes()) {
            float current_rate = (float) Math.max(size.width, size.height)/ (float)Math.min(size.width, size.height);
            tmp_diff = Math.abs(current_rate-rate);
            if( min_diff < 0){
                min_diff = tmp_diff ;
                result = size;
            }
            if( tmp_diff < min_diff ){
                min_diff = tmp_diff ;
                result = size;
            }
        }
        return result;
    }

    public void parameters(Camera camera) {
        List<Camera.Size> pictureSizes = camera.getParameters().getSupportedPictureSizes();
        List<Camera.Size> previewSizes = camera.getParameters().getSupportedPreviewSizes();
        Camera.Size psize;
        for (int i = 0; i < pictureSizes.size(); i++) {
            psize = pictureSizes.get(i);
            Logger.d("pictureSize:"+psize.width+" x "+psize.height);
        }
        for (int i = 0; i < previewSizes.size(); i++) {
            psize = previewSizes.get(i);
            Logger.d("previewSize:"+psize.width+" x "+psize.height);
        }
    }
}
