package facemeet.cigit.com.facedemet;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;

import facemeet.cigit.com.facedemet.customview.RectSurfaceView;
import facemeet.cigit.com.facedemet.util.HttpConnectionUtil;

/**
 * Created by lizhanwei on 17/9/12.
 */

public class LoginAccount extends Activity implements View.OnClickListener {

    private static final String TAG = LoginAccount.class.getCanonicalName();
    private String userName = "";
    private String fileName="";
    private RectSurfaceView mySurfaceView;
    private String url_recog = "http://192.168.253.1:10080/API/FaceRecogWebSer.ashx";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleIntent();
        setContentView(R.layout.activity_login);
        
        initEvents();
    }

    private void initEvents() {
        mySurfaceView = (RectSurfaceView) findViewById(R.id.login_surface);
        findViewById(R.id.pwd_login).setOnClickListener(this);
        findViewById(R.id.face_detct).setOnClickListener(this);
    }
    private BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (("com.cigit.reg").equals(intent.getAction())){
                String name = intent.getStringExtra("name");
                Logger.d("收到eventbus  main");
                new AsyncTask<String, Void, String>() {
                    @Override
                    protected String doInBackground(String... params) {
                        String res = HttpConnectionUtil.uploadFile(null,url_recog,new String[]{params[0]});
                        return res;
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        super.onPostExecute(s);
                        Logger.json(s);
                        Toast.makeText(LoginAccount.this,s,Toast.LENGTH_SHORT).show();
                    }
                }.execute(name);
            }
        }
    };
    private void handleIntent() {
//        userName = getIntent().getStringExtra("user_name");
//        Log.w(TAG,"userName="+userName);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.face_detct:
                //刷脸登录UI
//                startActivity(new Intent(this,FaceDeActivity.class));
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
                String currentDateandTime = sdf.format(new Date());
                fileName = Environment.getExternalStorageDirectory().getPath() +
                        "/sample_picture_" + currentDateandTime + ".jpg";
                mySurfaceView.takePicture(fileName);

                break;
            case R.id.pwd_login:
                //密码登录UI
                break;
        }
    }
}
