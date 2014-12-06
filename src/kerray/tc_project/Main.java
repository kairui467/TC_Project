package kerray.tc_project;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import kerray.service.LoginService;
import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class Main extends Activity
{
    private TextView userNameText;
    private TextView NameText;
    private TextView classText;
    private TextView numberText;
    private TextView sexText;

    private TextView showText;

    private LoginService mLoginService;
    private String userName;
    private String msg = "";
    String userID;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        userNameText = (TextView) findViewById(R.id.userName_text);
        NameText = (TextView) findViewById(R.id.name_text);
        classText = (TextView) findViewById(R.id.class_text);
        numberText = (TextView) findViewById(R.id.stdNumber_text);
        sexText = (TextView) findViewById(R.id.sex_text);
        showText = (TextView) findViewById(R.id.show_text);

        mLoginService = new LoginService();

        Intent _Intent = getIntent();
        Bundle bundle = _Intent.getExtras();
        userName = bundle.getString("userName");

        new Thread(mRunnable).start();
    }

    Runnable mRunnable = new Runnable()
    {
        public void run()
        {

            String path = "http://192.168.1.106/Requests/getUserCheckIn.ashx?type=getStdInfo&stdName=" + Uri.encode(userName);

            try
            {
                msg = mLoginService.httpConn(path, true);
            } catch (IOException e)
            {
                e.printStackTrace();
            }

            mHandler.obtainMessage(222, msg).sendToTarget();
        }
    };

    Runnable mRunnable2 = new Runnable()
    {
        public void run()
        {
            String path = "http://192.168.1.106/Requests/getUserCheckIn.ashx?type=getStdChecking&stdID=" + userID;

            try
            {
                msg = mLoginService.httpConn(path, true);
            } catch (IOException e)
            {
                e.printStackTrace();
            }

            mHandler.obtainMessage(333, msg).sendToTarget();
        }
    };

    @SuppressLint("HandlerLeak")
    public Handler mHandler = new Handler()
    {

        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case 222:
                    String string = (String) msg.obj;

                    try
                    {
                        // 解析学生信息
                        JSONObject json = new JSONObject(string);

                        userID = json.getString("用户ID");

                        userNameText.setText("用户名:" + json.optString("用户名"));
                        NameText.setText("姓名:" + json.optString("姓名"));
                        classText.setText("班级名称:" + json.getString("班级名称"));
                        numberText.setText("学号:" + json.optString("学号"));
                        sexText.setText("性别:" + json.optString("性别"));

                        new Thread(mRunnable2).start();
                    } catch (JSONException e)
                    {
                        e.printStackTrace();
                    }

                    break;
                case 333:
                    String str3 = (String) msg.obj;
                    String str4 = "";

                    try
                    {
                        // 解析考勤数据
                        JSONObject json = new JSONObject(str3);
                        JSONArray numberList = json.getJSONArray("T1");
                        for (int i = 0; i < numberList.length(); i++)
                        {
                            str4 += "时间:" + numberList.getJSONObject(i).optString("时间") + "\n";
                            str4 += "课程名称:" + numberList.getJSONObject(i).optString("课程名称") + "\n";
                            str4 += "星期:" + numberList.getJSONObject(i).optString("星期") + "\n";
                            str4 += "时长（分钟）:" + numberList.getJSONObject(i).optString("时长（分钟）") + "\n";
                            str4 += "情况:" + numberList.getJSONObject(i).optString("情况") + "\n";
                            str4 += "备注:" + numberList.getJSONObject(i).optString("备注") + "\n\n\n";
                        }

                        showText.setText(str4);

                    } catch (JSONException e)
                    {
                        // TODO 自动生成的 catch 块
                        e.printStackTrace();
                    }

                    break;
            }
        }

    };

}
