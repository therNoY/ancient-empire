package pers.mihao.ancient_empire.common.util;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.mihao.ancient_empire.common.annotation.KnowledgePoint;

public class HttpUtil {

    private final static Logger logger = LoggerFactory.getLogger(HttpUtil.class);



    public static Map<String, String> get(String url, String cookie) {
        BufferedReader in = null;
        Map<String, String> map = new HashMap<String, String>();

        try {
            URL realUrl = new URL(url);
            URLConnection connection = realUrl.openConnection();
            // 1. 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            if (null != cookie) {
                connection.setRequestProperty("Cookie", cookie);
            }
            // 2. 建立实际的连接
            connection.connect();
            // 3. 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuffer sb = new StringBuffer();
            String line;
            while ((line = in.readLine()) != null) {
                sb.append(line + "\n");
            }
            String c = connection.getHeaderField("Set-Cookie");
            map.put("cookie", c);
            map.put("body", sb.toString());
            return map;
        } catch (Exception e) {
            logger.error("HTTP 请求失败: {}", e);
        } finally {
            // 3.关闭连接
            safeClose(in);
        }
        return null;
    }

    /**
     * 发送HttpPost请求
     *
     * @param strURL 服务地址
     * @param params
     * @return 成功:返回json字符串<br/>
     */
    public static String post(String strURL, Map<String, String> params, String cookie) {

        BufferedReader in = null;
        try {
            URL url = new URL(strURL);// 创建连接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestMethod("POST"); // 设置请求方式
            if (null != cookie) {
                connection.setRequestProperty("Cookie", cookie);
            }
            connection.setRequestProperty("Accept", "*/*"); // 设置接收数据的格式
            connection
                .setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8"); // 设置发送数据的格式

            connection.connect();
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8"); // utf-8编码
            StringBuffer sb = new StringBuffer();
            for (String s : params.keySet()) {
                sb.append(s + "=" + params.get(s) + "&");
            }
            out.append(sb.toString().substring(0, sb.length() - 1));
            out.flush();
            out.close();

            int code = connection.getResponseCode();

            if (code == 200) {
                in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } else {
                in = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }

            // 定义BufferedReader输入流来读取URL的响应
            String line;
            StringBuffer inputBuffer = new StringBuffer();
            while ((line = in.readLine()) != null) {
                inputBuffer.append(line);
            }
            return inputBuffer.toString();

        } catch (IOException e) {
            logger.error("", e);
            return null;
        } finally {
            safeClose(in);
        }
    }

    private static void safeClose(BufferedReader in) {
        try {
            if (in != null) {
                in.close();
            }
        } catch (Exception e2) {
            logger.error("", e2);
        }
    }
}
