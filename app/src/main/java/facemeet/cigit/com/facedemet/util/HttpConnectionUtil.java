package facemeet.cigit.com.facedemet.util;

import com.orhanobut.logger.Logger;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;

/**
 * Created by lizhanwei on 17/8/29.
 */

public class HttpConnectionUtil {

    /**
     * 上传多个文件
     * @param actionUrl http url
     * @param uploadFilePaths 文件路径
     * @return server返回的json字符串
     */
    public static String uploadFile(Map<String,String> maps,String actionUrl, String[] uploadFilePaths){
        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "---------------------------7df2882560ede";
        Logger.w("url:"+actionUrl);
        DataOutputStream dataOutputStream = null;
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        StringBuffer stringBuffer = new StringBuffer();
        String tempLine = null;

        try {
            //统一资源
            URL url = new URL(actionUrl);
            //连接类的父类 抽象类
            URLConnection urlConnection = url.openConnection();
            //http的连接类
            HttpURLConnection httpURLConnection = (HttpURLConnection)urlConnection;

            //设置是否从httpurlConnection读入
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            //post request 不能使用cache
            httpURLConnection.setUseCaches(false);
            //设定请求的方法，默认是GET
            httpURLConnection.setRequestMethod("POST");

            //设置字符编码连接参数
            httpURLConnection.setRequestProperty("Connection","Keep-Alive");

            //set 字符编码
            httpURLConnection.setRequestProperty("Charset","UTF-8");


            //设置请求内容类型
            httpURLConnection.setRequestProperty("Content-Type","multipart/form-data; boundary="+boundary);

            //设置DataOutputStream
            dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
//            for (int i=0; i<uploadFilePaths.length; i++){
                String uploadFile = uploadFilePaths[0];
                String fileName = uploadFile.substring(uploadFile.lastIndexOf("//")+1);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("userName","test");
                jsonObject.put("passWord","123456");
                jsonObject.put("reqId","123456");

            if (maps != null){
                Set<String> keys = maps.keySet();
                for (String k:keys){
                    String js=maps.get(k);
                    jsonObject.put(k,js);
                }
            }

            try{
                    dataOutputStream.writeBytes(twoHyphens+boundary+end);
                    dataOutputStream.writeBytes("Content-Disposition:form-data;name=\"param\"");
                    dataOutputStream.writeBytes(end);
                    dataOutputStream.writeBytes(end);
                    dataOutputStream.write( jsonObject.toString().getBytes("UTF-8"));
                    Logger.json(jsonObject.toString());
                    dataOutputStream.writeBytes(end);

                    dataOutputStream.writeBytes(twoHyphens+boundary+end);
                    dataOutputStream.writeBytes("Content-Disposition:form-data; " + "name=\"photo\""+";" +
                            "filename="+"1"+"\".jpg\""+end);
//                dataOutputStream.writeBytes(end);
                    dataOutputStream.writeBytes("Content-Type:image/jpeg");
                    dataOutputStream.writeBytes(end);
                    dataOutputStream.writeBytes(end);
                    FileInputStream fileInputStream = new FileInputStream(uploadFile);

                    int bufferSize = 1024;
                    byte[] buffer = new byte[bufferSize];
                    int length = -1;
                    while((length=fileInputStream.read(buffer)) != -1){
                        dataOutputStream.write(buffer,0,length);
                    }
                    dataOutputStream.writeBytes(end);
                    dataOutputStream.writeBytes(twoHyphens+boundary+twoHyphens+end);
                    fileInputStream.close();


                }catch (Exception e){
                    stringBuffer.append(e.getMessage());

                }

//            }

            dataOutputStream.flush();
            int responseCode= httpURLConnection.getResponseCode();
            if ( responseCode>= 300){
                stringBuffer.append("HTTP Request is not success, Response code is "+httpURLConnection.getResponseCode());
                throw new Exception(stringBuffer.toString());
            }
            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                inputStream = httpURLConnection.getInputStream();
                inputStreamReader = new InputStreamReader(inputStream);
                bufferedReader = new BufferedReader(inputStreamReader);
                while ((tempLine = bufferedReader.readLine()) != null) {
                    stringBuffer.append(tempLine);
                    stringBuffer.append("\n");
                }
                Logger.json(stringBuffer.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (dataOutputStream != null){
                try {
                    dataOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bufferedReader != null){
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStreamReader != null){
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return stringBuffer.toString();
    }

    public static String toUtf8(String str) {
        String result = null;
        try {
            result = new String(str.getBytes("utf-8"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }
}
