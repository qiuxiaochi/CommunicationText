package thefirstchange.example.com.communicationtext.usersignin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import thefirstchange.example.com.communicationtext.R;
import thefirstchange.example.com.communicationtext.activity.BaseForCloseActivity;
import thefirstchange.example.com.communicationtext.config.Config;
import thefirstchange.example.com.communicationtext.https.BaseCallBack;
import thefirstchange.example.com.communicationtext.https.BaseOkHttpClient;
import thefirstchange.example.com.communicationtext.login.LoginActivity;
import thefirstchange.example.com.communicationtext.netty.commandToserver.SendToServer;
import thefirstchange.example.com.communicationtext.routePath.UserRoutePath;
import thefirstchange.example.com.communicationtext.util.CodeUtils;
import thefirstchange.example.com.communicationtext.util.CountDownTimerUtils;
import thefirstchange.example.com.communicationtext.util.EncryptTool;
import thefirstchange.example.com.communicationtext.util.MyDialog;
import thefirstchange.example.com.communicationtext.util.NetworkUtils;


/*
 用户注册时  应对验证码界面
 */
public class RegisterCaptchaActivity extends BaseForCloseActivity implements View.OnClickListener{

    private ImageView imagecode_iv;
    private EditText imagecode_et;
    private Button get_imagecode_bt;

    private CodeUtils codeUtils;


    private ImageView registercaptchaback_tv ;
    private TextView submitReg_tv;

    private EditText registercaptcha_et;
    private TextView registercaptchachongfa_et;
    private TextView phone_tv;


    private String schoolname;            //学校名字
    private String collegename ;          //学院名字
    private String majorname;             //所修专业
    private int ruxueyear;
    private String phonenumber;
    private String password;
    private String imageCode ;    //图片验证码
    private String noteCode;     //短信验证码
    private String encrypt;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registercaptcha);



        Intent intent = getIntent();
        schoolname = intent.getStringExtra("schoolname");
        collegename = intent.getStringExtra("collegename");
        majorname = intent.getStringExtra("majorname");

        ruxueyear = intent.getIntExtra("ruxueyear",2016);
        phonenumber = intent.getStringExtra("phonenumber");
        password = intent.getStringExtra("password");

        imageCode = intent.getStringExtra("imageCode");
        encrypt = intent.getStringExtra("encrypt");
        encrypt = EncryptTool.encryptStr(encrypt);

        init();
        initevent();
        setImageCode(imageCode);

    }

    public void init(){
        imagecode_iv = (ImageView)this.findViewById(R.id.imagecode_iv);
        imagecode_et = (EditText)this.findViewById(R.id.imagecode_et);
        get_imagecode_bt = (Button) this.findViewById(R.id.get_imagecode_bt);

        phone_tv = (TextView)findViewById(R.id.phone_tv);
        phone_tv.setText(phonenumber);
        registercaptchaback_tv = (ImageView)this.findViewById(R.id.registercaptchaback_tv);
        submitReg_tv = (TextView)this.findViewById(R.id.submitReg_tv);

        registercaptcha_et = (EditText)this.findViewById(R.id.registercaptcha_et);
        registercaptchachongfa_et = (TextView)this.findViewById(R.id.registercaptchachongfa_et);

    }

    public void initevent(){

        get_imagecode_bt.setOnClickListener(this);

        registercaptchaback_tv.setOnClickListener(this);
        submitReg_tv.setOnClickListener(this);
        registercaptchachongfa_et.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.registercaptchaback_tv:
                finish();
                break;
            case R.id.submitReg_tv:          //提交注册信息
                if(!NetworkUtils.isConnected(RegisterCaptchaActivity.this)){
                    Toast.makeText(RegisterCaptchaActivity.this, "当前网络不可用，请检查您的网络!", Toast.LENGTH_SHORT).show();
                    return;
                }


                noteCode = registercaptcha_et.getText().toString().trim();
                if(noteCode==null||noteCode.equals("")){

                    registercaptcha_et.setText("");
                    registercaptcha_et.requestFocus();
                    Toast.makeText(this,"请填写验证码",Toast.LENGTH_SHORT).show();
                    return;
                }

                String imagecodetemp = imagecode_et.getText().toString();
                if(imagecodetemp==null||imagecodetemp.trim().equals("")){
                    getImageCode();
                    imagecode_et.setText("");
                    imagecode_et.requestFocus();
                    Toast.makeText(this,"请填写图片验证码",Toast.LENGTH_SHORT).show();

                    return;
                }



                if(!imageCode.toLowerCase().equals(imagecodetemp.toLowerCase())){
                    Toast.makeText(this,"图片验证码错误",Toast.LENGTH_SHORT).show();

                    return;
                }

                submitRegInfo();


                break;
            case R.id.get_imagecode_bt:                 //获得图片验证码

                getImageCode();

                break;

            case R.id.registercaptchachongfa_et:      //发送验证码
                if(!NetworkUtils.isConnected(RegisterCaptchaActivity.this)){
                    Toast.makeText(RegisterCaptchaActivity.this, "当前网络不可用，请检查您的网络!", Toast.LENGTH_SHORT).show();
                    return;
                }
                BaseOkHttpClient.newBuilder()
                        .addParam("phone",phonenumber)
                        .post()
                        .url(Config.HTTPURL + UserRoutePath.API_REG_VERIFICATION_CODE)
                        .build()
                        .enqueue(new BaseCallBack() {
                            @Override
                            public void onSuccess(Object o) {

                                String json = (String)o;
                                JsonObject jsonObject = (JsonObject) new JsonParser().parse(json);
                                if(jsonObject.get("message")==null){
                                    Toast.makeText(RegisterCaptchaActivity.this, "error!", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                String msg = jsonObject.get("message").getAsString();
                                if(!msg.equals("ok")){
                                    Toast.makeText(RegisterCaptchaActivity.this, msg, Toast.LENGTH_SHORT).show();
                                }
                                //imageCode = jsonObject.get("imageCode").getAsString();
                            }

                            @Override
                            public void onError(int code) {

                            }

                            @Override
                            public void onFailure(Call call, IOException e) {

                            }
                        });


                CountDownTimerUtils mCountDownTimerUtils = new CountDownTimerUtils(registercaptchachongfa_et, 60000, 1000);
                mCountDownTimerUtils.start();
                break;

        }


    }

    //设置图片验证码
    private void setImageCode(String code) {
        codeUtils = CodeUtils.getInstance();

        Bitmap bitmap = codeUtils.createBitmap(code);
        imagecode_iv.setImageBitmap(bitmap);
    }

    //获取图片验证码
    private void getImageCode(){
        if(!NetworkUtils.isConnected(RegisterCaptchaActivity.this)){
            Toast.makeText(RegisterCaptchaActivity.this, "当前网络不可用，请检查您的网络!", Toast.LENGTH_SHORT).show();
            return;
        }
        BaseOkHttpClient.newBuilder()
                .post()
                .url(Config.HTTPURL + UserRoutePath.API_REG_IMAGE_CODE)
                .build()
                .enqueue(new BaseCallBack() {
                    @Override
                    public void onSuccess(Object o) {

                        String json = (String)o;
                        JsonObject jsonObject = (JsonObject) new JsonParser().parse(json);

                        imageCode = jsonObject.get("imageCode").getAsString();

                        //重新绘制
                        setImageCode(imageCode);
                    }

                    @Override
                    public void onError(int code) {

                    }

                    @Override
                    public void onFailure(Call call, IOException e) {

                    }
                });
    }


    private Timer timer;
    //提交注册
    private void submitRegInfo(){
        MyDialog.showBottomLoadingDialog(RegisterCaptchaActivity.this);

        BaseOkHttpClient.newBuilder()
                .addParam("schoolname", schoolname)
                .addParam("collegename", collegename)
                .addParam("majorname", majorname)
                .addParam("ruxueyear", ruxueyear)
                .addParam("password", password)
                .addParam("noteCode", noteCode)
                .addParam("phonenumber", phonenumber)
                .addParam("imagecode", imageCode)
                .addParam("encrypt", encrypt)
                .post()
                .url(Config.HTTPURL + UserRoutePath.API_REG_USERINFO)
                .build()
                .enqueue(new BaseCallBack() {
                    @Override
                    public void onSuccess(Object o) {

                        if(timer!=null){
                            timer.cancel();
                        }
                        MyDialog.dismissBottomLoadingDialog();

                        String json = (String)o;
                        JsonObject jsonObject = (JsonObject) new JsonParser().parse(json);

                        if(jsonObject.get("message")==null){
                            Toast.makeText(RegisterCaptchaActivity.this, "error!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String msg = jsonObject.get("message").getAsString();
                        if(msg.equals("ok")){
                            Toast.makeText(RegisterCaptchaActivity.this, "注册成功!", Toast.LENGTH_SHORT).show();

                            Intent a=new Intent(RegisterCaptchaActivity.this, LoginActivity.class);
                            startActivity(a);

                            RegisterCaptchaActivity.this.finish();

                        }else{
                            Toast.makeText(RegisterCaptchaActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }


                    }

                    @Override
                    public void onError(int code) {
                        if(timer!=null){
                            timer.cancel();
                        }
                        MyDialog.dismissBottomLoadingDialog();

                        Toast.makeText(RegisterCaptchaActivity.this, "服务器飞走啦,请稍后再试!", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        if(timer!=null){
                            timer.cancel();
                        }
                        MyDialog.dismissBottomLoadingDialog();

                        Toast.makeText(RegisterCaptchaActivity.this, "服务器飞走啦,请稍后再试!", Toast.LENGTH_SHORT).show();

                    }
                });

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                RegisterCaptchaActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MyDialog.dismissBottomLoadingDialog();
                        Toast.makeText(RegisterCaptchaActivity.this, "服务器繁忙,请稍后再试!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        },30000);


    }


}
