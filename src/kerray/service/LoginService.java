package kerray.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginService
{
    Cookie mCookie = new Cookie();
    String TAG = "kerray";

    public String httpConn(String Path, boolean isSendCookie) throws IOException
    {
        int i = 0;
        String str;
        String res = "";
        String getResult;
        InputStream inStream;

        URL url = new URL(Path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        if (isSendCookie && mCookie.getCookie().length() > 0)
        {
            conn.setRequestProperty("Cookie", mCookie.getCookie());
        }
        conn.setReadTimeout(5 * 1000);
        conn.setRequestMethod("GET");
        if (conn.getResponseCode() == 200)
        {
            inStream = conn.getInputStream();

            getResult = inputStream2String(inStream);

            // 登陆成功时把 Cookie 保存
            if (getResult.equals("access"))
            {
                while ((str = conn.getHeaderField(i++)) != null)
                    res += str;
                // 提取 Cookie 并且保存
                mCookie.setCookie(res.substring(res.indexOf("ASP"), res.indexOf("; path")));

                return getResult;
            } else
            {
                return getResult;
            }
        }
        return "连接错误";
    }

    public String inputStream2String(InputStream is) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int i = -1;
        while ((i = is.read()) != -1)
        {
            baos.write(i);
        }
        return baos.toString();
    }
}
