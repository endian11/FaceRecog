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


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facede);
        Logger.addLogAdapter(new AndroidLogAdapter());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
        }
    }

    public void wrieDoneEvent(writeDoneEvent witeDoneEvent){
        //上传



    }
}
