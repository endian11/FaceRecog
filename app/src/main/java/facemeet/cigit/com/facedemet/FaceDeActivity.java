package facemeet.cigit.com.facedemet;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.View;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;

import facemeet.cigit.com.facedemet.customview.MySurfaceView;
import facemeet.cigit.com.facedemet.event.writeDoneEvent;
import facemeet.cigit.com.facedemet.util.HttpConnectionUtil;

/**
 * Created by lizhanwei on 17/9/12.
 */

public class FaceDeActivity extends Activity implements View.OnClickListener {

    private String url_detect = "http://192.168.253.1:10080/API/FaceDetectWebSer.ashx";
    private String fileName;

    private MySurfaceView mySurfaceView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facede);
        Logger.addLogAdapter(new AndroidLogAdapter());
        findViewById(R.id.btn_take_pic).setOnClickListener(this);
        mySurfaceView = (MySurfaceView) findViewById(R.id.mysurface);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_take_pic:
                //拍照

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
                String currentDateandTime = sdf.format(new Date());
                fileName = Environment.getExternalStorageDirectory().getPath() +
                    "/sample_picture_" + currentDateandTime + ".jpg";
                mySurfaceView.takePicture(fileName);
                break;
        }
    }

    public void wrieDoneEvent(writeDoneEvent witeDoneEvent){
        //上传



    }
}
