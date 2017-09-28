package facemeet.cigit.com.facedemet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import java.util.regex.Matcher;

import facemeet.cigit.com.facedemet.util.MatchUtil;
import facemeet.cigit.com.facedemet.util.SharedUtil;

/**
 * Created by lizhanwei on 17/9/12.
 */

public class SettingAct extends Activity implements View.OnClickListener {

    private static final String TAG = SettingAct.class.getCanonicalName();
    private EditText et_port;
    private EditText et_ip;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleIntent();

        setContentView(R.layout.activity_setting);
        Logger.addLogAdapter(new AndroidLogAdapter());
        initView();
        initEvents();
    }

    private void initView() {
        et_ip = (EditText) findViewById(R.id.et_ip);
        et_port = (EditText) findViewById(R.id.et_port);

        et_ip.setText(SharedUtil.getValue(this,SharedUtil.IP,"", SharedUtil.ShareType.STRING));
        et_port.setText(SharedUtil.getValue(this,SharedUtil.PORT,"", SharedUtil.ShareType.STRING));
    }

    private void initEvents() {
        findViewById(R.id.btn_save).setOnClickListener(this);
    }
    private void handleIntent() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_save:

                //首先判断IP地址是否合法
                boolean isIpOk = MatchUtil.isIp(et_ip.getText().toString());

                //再判断port是否合法
                boolean isPortOk = MatchUtil.isDigital(et_port.getText().toString());


                //如果以上两步都通过了，则进行保存，并返回
                if (isPortOk && isIpOk){
                    Logger.w("ip和port合法");

                    //Sharedprefercence保存ip地址和port
                    SharedUtil.setSharedprefer(SharedUtil.IP,et_ip.getText().toString(),this);
                    SharedUtil.setSharedprefer(SharedUtil.PORT,et_port.getText().toString(),this);

                    //返回ip和port给上个页面（startActivityForResult）
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putString(SharedUtil.IP,et_ip.getText().toString());
                    bundle.putString(SharedUtil.PORT,et_port.getText().toString());

                    intent.putExtras(bundle);
                    setResult(100,intent);
                    finish();
                }else{

                    String des = "";
                    if (!isIpOk&&!isPortOk){
                        des = "IP地址和端口号不合法";
                    }else if (!isIpOk){
                        des = "IP地址不合法";
                    }else if (!isPortOk){
                        des = "端口号不合法";
                    }
                    Toast.makeText(this, des,Toast.LENGTH_SHORT).show();
                }

                break;
            default:
                break;
        }
    }
}
