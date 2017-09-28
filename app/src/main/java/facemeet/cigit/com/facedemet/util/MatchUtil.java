package facemeet.cigit.com.facedemet.util;

import com.orhanobut.logger.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lizhanwei on 17/9/20.
 */

public class MatchUtil {
    /**
     * 判断端口号是否合法
     * @param numbers
     * @return 如果合法返回true，否则返回false
     */
    public static boolean isDigital(String numbers){
        String regx = "\\d+";
        Pattern pattern = Pattern.compile(regx);
        Matcher matcher = pattern.matcher(numbers);
        if (matcher.matches()){
            Logger.w("该字符串全是数字"+numbers);
            int port = Integer.parseInt(numbers);
            if (port<0 || port>65535){
                Logger.d("非法端口号");
                return false;
            }else{
                return true;
            }
        }
        return false;
    }

    /**
     * 正则表达式判断该字符串是否点分十进制形式的IP(类似192.168.1.12)
     * @param ip
     * @return
     */
    public static boolean isIp(String ip){
        String regx = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."

                +"(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."

                +"(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."

                +"(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
        /**
         * 上面的一个不漏就是正确的验证ip的正则表达式，简单的讲解一下

         \\d表示0~9的任何一个数字

         {2}表示正好出现两次

         [0-4]表示0~4的任何一个数字

         | 的意思是或者

         ( )上面的括号不能少，是为了提取匹配的字符串，表达式中有几个()就表示有几个相应的匹配字符串

         1\\d{2}的意思就是100~199之间的任意一个数字

         2[0-4]\\d的意思是200~249之间的任意一个数字

         25[0-5]的意思是250~255之间的任意一个数字

         [1-9]\\d的意思是10~99之间的任意一个数字

         [1-9])的意思是1~9之间的任意一个数字

         \\.的意思是.点要转义（特殊字符类似，@都要加\\转义）


         */
        Pattern pattern = Pattern.compile(regx);
        Matcher matcher = pattern.matcher(ip);
        if (matcher.matches()){
            return true;
        }
        return false;
    }
}
