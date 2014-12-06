package kerray.text;

import kerray.service.LoginService;
import kerray.tool.MD5;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

public class PersonServiceTest extends AndroidTestCase
{
    String TAG = "kerray";

    public void testPersons() throws Exception
    {
        LoginService ls = new LoginService();
        MD5 m = new MD5();

        String md5 = m.getMD5Str("123456");

        String path = "http://192.168.1.106/Requests/login.ashx?uname=" + Uri.encode("李开睿") + "&pwd=" + md5;

        String inStream = ls.httpConn(path, false);

        Log.i("kerray", "" + inStream);

        if (inStream.equals("access"))
        {
            Log.i("kerray", "请求成功");
            String reStr = ls.httpConn("http://192.168.1.106/Requests/menuList.ashx", true);
            Log.i("kerray", "" + reStr.indexOf("我的考勤"));

        } else if (inStream.equals("不存在该用户"))
        {
            Log.i("kerray", "不存在该用户");
        } else if (inStream.equals("密码错误"))
        {
            Log.i("kerray", "密码错误");
        }

        //Log.i(TAG, "Cookie:" + new Cookie().getCookie());
        String msg = ls.httpConn("http://192.168.1.106/Requests/getUserCheckIn.ashx?stdName=" + Uri.encode("李开睿"), true);
        Log.i(TAG, msg);

    }
}
