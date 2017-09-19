package facemeet.cigit.com.facedemet;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import facemeet.cigit.com.facedemet.customview.RectSurfaceView;
import facemeet.cigit.com.facedemet.event.writeDoneEvent;
import facemeet.cigit.com.facedemet.util.ConstantUtil;
import facemeet.cigit.com.facedemet.util.HttpConnectionUtil;
import facemeet.cigit.com.facedemet.util.UploadTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getCanonicalName();
    private EditText et_number;
    private EditText et_name;
    RectSurfaceView myRectSurface;
    RelativeLayout ll_reg_part;
    LinearLayout ll_login_part;
    Dialog dlg;

    private IntentFilter intentFile = new IntentFilter("com.cigit.reg");
    private TextView tv_login;
    private Button btn_login;
    private HashMap<String,String> parmas = new HashMap<>();
    private String type="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_login).setOnClickListener(this);
        Logger.addLogAdapter(new AndroidLogAdapter());
        et_number = (EditText) findViewById(R.id.et_account);
        et_name = (EditText) findViewById(R.id.et_name);
        tv_login = (TextView) findViewById(R.id.tv_to_login);
        tv_login.setOnClickListener(this);
        ll_login_part = (LinearLayout) findViewById(R.id.btn_login_part);
        ll_reg_part = (RelativeLayout) findViewById(R.id.reg_part);
        btn_login = (Button) ll_login_part.findViewById(R.id.face_detct_lg);
        btn_login.setOnClickListener(this);
        myRectSurface = (RectSurfaceView) findViewById(R.id.reg_surface);
        //初始化logger

    }

    @Override
    protected void onStart() {
        super.onStart();
        dlg = createLoadingDialog(this,"请稍等");

        registerReceiver(br,intentFile);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (dlg != null){
            dlg.hide();
            dlg.dismiss();
        }
        unregisterReceiver(br);
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
                        String res;
                        String url;
                        if (ConstantUtil.recogType.equals(type)){
                            url = ConstantUtil.url_recog;
                            parmas.clear();
                        }else{
                            url = ConstantUtil.url_detect;

                        }
                        res = HttpConnectionUtil.uploadFile(parmas,url,new String[]{params[0]});
                        return res;
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        super.onPostExecute(s);
                        Logger.json(s);
                        if (dlg != null){
                            dlg.hide();
                        }
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            final int stat = jsonObject.getInt("status");
                            if (stat == 0){
                            }else{

                            }
                            String psBtn = (type.equals(ConstantUtil.regType)?"去登录":"登录成功");
                            new AlertDialog.Builder(MainActivity.this).setMessage(stat==0?(type.equals(ConstantUtil.regType)?"注册"+jsonObject.getString("msg")+",去登录":"登录成功,编号："+jsonObject.getString("number")+"姓名："+jsonObject.getString("name")):jsonObject.getString("msg"))
                                    /*.setTitle(type.equals(ConstantUtil.regType)?"人脸注册":"人脸识别登录")*/.
                                    setPositiveButton(stat == 0 ? psBtn : "重试", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if  (type.equals(ConstantUtil.regType) && stat ==0){

                                                ll_reg_part.setVisibility(View.GONE);
                                                ll_login_part.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    }).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this,s,Toast.LENGTH_SHORT).show();
                        }


                    }
                }.execute(name,type);
            }
        }
    };

    @Override
    public void onClick(View v) {
    //登录，跳转到刷新登录 还是密码登录UI
        switch (v.getId()){
            case R.id.btn_login:
                type = ConstantUtil.regType;
                if (!TextUtils.isEmpty(et_name.getText().toString()) &&!TextUtils.isEmpty(et_number.getText().toString())){

                    parmas.put("number",et_number.getText().toString());
                    parmas.put("name",et_name.getText().toString());
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
                    String currentDateandTime = sdf.format(new Date());
                    String fileName = Environment.getExternalStorageDirectory().getPath() +
                            "/reg_pic_" + currentDateandTime + ".jpg";

                    myRectSurface.takePicture(fileName);
                    dlg.show();
                }else{
                    Toast.makeText(this,"请输入用户名和编号",Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.tv_to_login:
                ll_reg_part.setVisibility(View.GONE);
                ll_login_part.setVisibility(View.VISIBLE);
                break;

            case R.id.face_detct_lg:
                //刷脸登录UI
                type = ConstantUtil.recogType;
                dlg.show();
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
                String currentDateandTime1 = sdf1.format(new Date());
                String fileName1 = Environment.getExternalStorageDirectory().getPath() +
                        "/sample_picture_" + currentDateandTime1 + ".jpg";
                myRectSurface.takePicture(fileName1);

            default:
                Log.e(TAG,"登录失败，default");
                break;
        }

    }

    @Override
    public void onBackPressed() {
        if (ll_login_part.getVisibility() == View.VISIBLE){
            ll_reg_part.setVisibility(View.VISIBLE);
            ll_login_part.setVisibility(View.GONE);
        }else{

            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    public static Dialog createLoadingDialog(Context context, String msg) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.loading_dialog, null);// 得到加载view
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局
        // main.xml中的ImageView
        ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img);
        TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);// 提示文字
        // 加载动画
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
                context, R.anim.load_animation);
        // 使用ImageView显示动画
        spaceshipImage.startAnimation(hyperspaceJumpAnimation);
        tipTextView.setText(msg);// 设置加载信息

        Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog

        loadingDialog.setCancelable(false);// 不可以用“返回键”取消
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.FILL_PARENT));// 设置布局
        return loadingDialog;

    }

}
