package com.mihao.ancient_empire.Base64;

import org.junit.Test;
import java.util.Base64.Encoder;
import java.util.Base64.Decoder;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;

public class Base64Test {

    /**
     * base64加密方法
     *
     * Create by 暴沸 2016年5月24日  at 下午8:21:20
     *
     * Mailto:baofei_dyz@foxmail.com
     *
     * @param plainText
     * @return
     */
    public static String getEncodedBase64(String plainText){
        String encoded = null;
        try {
            byte[] bytes =plainText.getBytes("UTF-8");
            encoded = Base64.getEncoder().encodeToString(bytes);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }



        return encoded;
    }

    /**
     * base64解密方法
     *
     * Create by 暴沸 2016年5月24日  at 下午8:21:02
     *
     * Mailto:baofei_dyz@foxmail.com
     *
     * @param plainText
     * @return
     */
    public static String getDecodedBase64(String plainText) throws UnsupportedEncodingException {
        byte[] decoded = null;
        try {
            byte[] bytes =plainText.getBytes("UTF-8");
            decoded = Base64.getDecoder().decode(bytes);
        } catch (Exception e) {
            // TODO: handle exception
        }

        return new String(decoded, "UTF-8");
    }


    @Test
    public void base64() throws IOException {
        String name = "http://10.0.11.154:8080/recorderfileserver/resources/2019-07-19/webchat_file";
        Encoder encoder = Base64.getEncoder();
        String result = encoder.encodeToString(name.getBytes());
        System.out.println(result);

        Decoder decoder = Base64.getDecoder();
        System.out.println(new String(decoder.decode(result)));
    }

    @Test
    public void name() throws IOException {
//        String name = "http://10.0.11.154:8080/recorderfileserver/resources/2019-07-19/webchat_file/log.xml";
        String name= "adTNjdXNlcjEyMwo";
        String result = new BASE64Encoder().encode(name.getBytes());
        System.out.println(result);
        System.out.println(new String(new BASE64Decoder().decodeBuffer("adTNjdXNlcjEyMwo")));
    }

    @Test
    public void name2() throws IOException {
        String base64Url = "aHR0cDovLzEwLjAuMTEuODE6ODA4MC9yZWNvcmRlcmZpbGVzZXJ2ZXIvcmVzb3VyY2VzL3dlaXhpbl9zaG9ydHZpZGVvX3BhdGgvMjAxOV8wOV8xNi9rc2dva2ZqVG5FQmx6QWxva3p6S0Zfb0xZdW9ndngzT3EyX0c4bGVUclRtU0lpVGdvcTJ1YWwzMkk4b3l2RGdhLm1wNA==";
        String s = new String(new BASE64Decoder().decodeBuffer(base64Url), "UTF-8");
        System.out.println(s);
    }

    @Test
    public void name3() throws IOException {
        String base64Url = "aHR0cDovLzEwLjAuMTEuMTU0OjgwODAvcmVjb3JkZXJmaWxlc2VydmVyL3Jlc291cmNlcy8yMDE5LTA4LTI5L3dlYmNoYXRfZmlsZS8lRTYlQjUlOEIlRTglQUYlOTUlRTklOTclQUUlRTQlQkIlQjcudHh0";
        base64Url = base64Url.substring(0, base64Url.indexOf("/"));
        String ecodeS = "aHR0cDovLzEwLjAuMTEuMTU0OjgwODAvcmVjb3JkZXJmaWxlc2VydmVyL3Jlc291cmNlcy8yMDE5LTA4LTE0L3dlYmNoYXRfZmlsZS8lRTYlQjUlOEIlRTglQUYlOTUlRTYlOTYlODclRTQlQkIlQjYudHh0";
        ecodeS.replace("\r", "").replace("\n", "");
        String s = new String(new BASE64Decoder().decodeBuffer(base64Url), "UTF-8");
        System.out.println(s);
    }

    @Test
    public void name4() {
        String fileUrl = "AAAAAAAAAcdBB";
        if (fileUrl.indexOf("&") > 0) {
            fileUrl = fileUrl.substring(0, fileUrl.indexOf("&"));
        }
        System.out.println(fileUrl);
    }
}