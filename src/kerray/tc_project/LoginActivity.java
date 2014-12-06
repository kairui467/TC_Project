package kerray.tc_project;

import java.io.IOException;

import kerray.service.LoginService;
import kerray.tool.MD5;
import android.R.string;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity
{
    private LoginService mLoginService;
    private MD5 mMD5;

    private TextView UnameText;
    private TextView PwdText;
    private Button OK_btn;

    private String inStream;
    private String loginPath;
    private String menuListPath;
    private String reUser;
    private String userName;

    private int state;
    private final int ACCESS = 0;
    private final int NO_USER = 10;
    private final int PWD_ERR = 20;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        mLoginService = new LoginService();
        mMD5 = new MD5();

        UnameText = (TextView) findViewById(R.id.Uname_edit);
        PwdText = (TextView) findViewById(R.id.pwd_edit);
        OK_btn = (Button) findViewById(R.id.Ok_Btn);
        OK_btn.setOnClickListener(new OnClickListener()
        {
            public void onClick(View v)
            {
                new Thread(mRunnable).start();
            }
        });

    }

    @SuppressLint("HandlerLeak")
    public Handler mHandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            if (msg.what == 111)
            {
                switch (state)
                {
                    // 返回成功后判断是什么身份登陆
                    case ACCESS:
                        if (reUser.indexOf("我的考勤") > 0)
                        {
                            Intent _Intent = new Intent(LoginActivity.this, Main.class);
                            _Intent.putExtra("userName", userName);
                            startActivity(_Intent);
                        }
                        break;
                    // 密码错误
                    case PWD_ERR:
                        Toast.makeText(LoginActivity.this, "密码错误", 0).show();
                        break;
                    // 不存在该用户
                    case NO_USER:
                        Toast.makeText(LoginActivity.this, "不存在该用户", 0).show();
                        break;
                }
            }
        }

    };

    Runnable mRunnable = new Runnable()
    {
        public void run()
        {
            userName = UnameText.getText().toString();
            String _Pwd = PwdText.getText().toString();

            loginPath = "http://192.168.1.106/Requests/login.ashx?uname=" + Uri.encode(userName) + "&pwd=" + mMD5.getMD5Str(_Pwd);
            menuListPath = "http://192.168.1.106/Requests/menuList.ashx";
            try
            {
                // 发送登陆请求
                inStream = mLoginService.httpConn(loginPath, false);
                // 解析返回类型
                if (inStream.equals("access"))
                {
                    // 设置状态为登陆成功
                    state = ACCESS;
                    // 带上得到的 Cookie 请求首页 xml 数据
                    reUser = mLoginService.httpConn(menuListPath, true);
                } else if (inStream.equals("不存在该用户"))
                {
                    state = NO_USER;
                } else if (inStream.equals("密码错误"))
                {
                    state = PWD_ERR;
                }

                mHandler.obtainMessage(111, state).sendToTarget();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }

    };

    private void dialog()
    {
        AlertDialog.Builder builder = new Builder(LoginActivity.this);
        builder.setTitle("提示");
        builder.setMessage("目前只支持查询学生考勤！\n		请以学生身份登录！");
        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

}
