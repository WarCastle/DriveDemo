package com.castle.drive.utils;

import cn.hutool.core.text.StrBuilder;
import com.castle.drive.exception.UtilException;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author YuLong
 * @Date 2023/11/9 16:29
 * @Classname StringUtils
 * @Description 字符串处理工具类
 */
public class StringUtils {

    public final static String  EMPTY_STRING        = "";
    public final static String  BLANK_SPRING_STRING = " ";
    public static final char    CHAR_TILDE          = '~';
    // 定义日志接口
    private static final Logger logger              = LoggerFactory.getLogger(StringUtils.class);

    public static UUID getRandomUUID() {
        return UUID.randomUUID();
    }

    public static boolean isBlank(CharSequence cs) {
        return org.apache.commons.lang3.StringUtils.isBlank(cs);
    }

    /**
     * check that it is not null or empty string
     */
    public static boolean isNotNullOrEmpty(Object obj) {
        return !isNullOrEmpty(obj);
    }

    /**
     * check if null or empty string
     */
    public static boolean isNullOrEmpty(Object obj) {
        return (null == obj || EMPTY_STRING.equals(obj)||"null".equals(obj)||obj.equals(null));
    }

    /**
     * 检查是否相等
     *
     * @author zhangxc
     * @date 2016年7月7日
     * @param obj
     * @param expectValue
     * @return
     */
    public static boolean isEquals(Object obj, String expectValue) {
        if (isNullOrEmpty(obj) || isNullOrEmpty(expectValue)) {
            return false;
        }
        return expectValue.equals(obj.toString());
    }

    /**
     * 检查是否相等（忽略大小写）
     *
     * @author zhangxc
     * @date 2016年7月7日
     * @param obj
     * @param expectValue
     * @return
     */
    public static boolean isEqualsNoCasetive(Object obj, String expectValue) {
        if (isNullOrEmpty(obj) || isNullOrEmpty(expectValue)) {
            return false;
        }
        return expectValue.toUpperCase().equals(obj.toString().toUpperCase());
    }

    /**
     * escape html str
     */
    public static String escapeHtml(String str) {
        return StringEscapeUtils.escapeHtml4(str);
    }

    /**
     * unescape html str
     */
    public static String unEscapeHtml(String str) {
        return StringEscapeUtils.unescapeHtml4(str);
    }

    /**
     * substring with utf-8 bytes length
     *
     * @param utfStr the string with utf-8 encoding
     */
    public static String subStringWithBytesLength(String utfStr, int bytesLength) throws UtilException {
        if (null == utfStr) {
            throw new UtilException("input string cannot be null");
        }
        if (bytesLength <= 0) {
            throw new UtilException("bytesLength should >0");
        }
        byte[] bytes = null;
        try {
            bytes = utfStr.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new UtilException("not support encoding", e);
        }
        if (bytes.length <= bytesLength) {
            return utfStr;
        }
        int index = bytesLength;
        String temp1 = new String(bytes, 0, index);
        String temp2 = new String(bytes, 0, index + 1);
        // when temp2 is end with a new char
        if (temp2.length() > temp1.length()) {
            return temp1;
        }
        // when temp2.length()=temp1.length()
        temp2 = new String(bytes, 0, index - 1);
        // when temp2 is end with a whole char
        if (temp2.length() < temp1.length()) {
            return temp2;
        }
        // when the new String(bytes, 0, index) is in the middle char
        return new String(bytes, 0, index - 2);
    }

    /**
     * split string(bytes) into list
     */
    public static List<String> subStringToListWithBytesLength(String utfStr, int bytesLength) throws UtilException {
        if (null == utfStr) {
            throw new UtilException("input string cannot be null");
        }
        if (bytesLength <= 0) {
            throw new UtilException("bytesLength should >0");
        }
        List<String> list = new ArrayList<>();
        String temp = utfStr;
        String data = EMPTY_STRING;
        while (temp.length() != data.length()) {
            temp = temp.substring(data.length());
            data = subStringWithBytesLength(temp, bytesLength);
            list.add(data);
        }
        return list;
    }

    /**
     * get last size string with maxsize,if string.length> maxsize , return string 'ABC' to '~BC'
     *
     * @throws UtilException
     */
    public static String getLastStringWithTilde(String str, int maxSize) throws UtilException {
        if (maxSize <= 3) {
            throw new UtilException("max size should >3");
        }
        byte[] bytes = str.getBytes();
        if (bytes.length <= maxSize) {
            return str;
        }
        String temp1 = new String(bytes, bytes.length - maxSize + 1, maxSize - 1);
        logger.debug(temp1 + StringUtils.BLANK_SPRING_STRING + temp1.length() + StringUtils.BLANK_SPRING_STRING
                + temp1.getBytes().length);

        // the first char is not messy code
        if (temp1.getBytes().length <= maxSize - 1) {
            return CHAR_TILDE + temp1;
        }
        String temp2 = new String(bytes, bytes.length - maxSize + 2, maxSize - 2);
        logger.debug(temp2 + StringUtils.BLANK_SPRING_STRING + temp2.length() + StringUtils.BLANK_SPRING_STRING
                + temp2.getBytes().length);
        // the first char is not messy code
        if (temp2.getBytes().length <= maxSize - 2) {
            return CHAR_TILDE + temp2;
        }
        String temp3 = new String(bytes, bytes.length - maxSize + 3, maxSize - 3);
        logger.debug(temp3 + StringUtils.BLANK_SPRING_STRING + temp3.length() + StringUtils.BLANK_SPRING_STRING
                + temp3.getBytes().length);
        // the first char is not messy code
        if (temp3.getBytes().length <= maxSize - 3) {
            return CHAR_TILDE + temp3;
        }
        throw new UtilException("this cannot be true in utf-8");
    }

    /**
     *
     * 将集合转化为按指定分隔符分隔的字符串
     * @author zhangxc
     * @date 2016年7月19日
     * @param list
     * @param separator
     * @return
     */
    public static String listToString(List list, char separator) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
            if (i < list.size() - 1) {
                sb.append(separator);
            }
        }
        return sb.toString();
    }

    /**
     * get url response file name by useragent
     */
    public static String getEncodeFileName(String showName) {
        return showName;
    }


    /**
     * 判断是否是符合的正则表达式
     * @param pattenStr
     * @param extensions
     * @return
     */
    public static boolean isMatcherPatten(String pattenStr,String extensions){
        Pattern patten = Pattern.compile(pattenStr);
        Matcher matcher = patten.matcher(extensions);
        if(matcher.find()){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 生成指定长度的随机字符数字
     * @param length
     * @return
     */
    public static String getRandomString(int length) {
        String[] randStr = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R",
                "S", "T", "U", "V", "W", "X", "Y", "Z", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
                "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "0", "1",
                "2", "3", "4", "5", "6", "7", "8", "9" };
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int randomValue = (int) Math.round(Math.random() * (randStr.length - 1));
            sb.append(randStr[randomValue]);
        }
        return sb.toString();
    }


    /**
     *
     * @Title: compileStr
     * @Description: (将带逗号的字符串组装成带单引号)
     * @param @param str  "1,2,3,4"
     * @return String    返回类型 '1','2','3','4'
     * @throws
     */
    public static String compileStr(String str) {
        StringBuilder strBuilder = new StringBuilder();
        if (isNull(str)) {
            throw new IllegalArgumentException("Invalid str, str == " + str);
        }
        String[] ss = str.split(",");
        for (int i = 0; i < ss.length; i++) {
            if (i == ss.length - 1) {
                strBuilder.append("'").append(ss[i]).append("'");
                break;
            }
            if (!isNull(ss[i])) {
                strBuilder.append("'").append(ss[i]).append("'").append(",");
            }
        }
        return strBuilder.toString();
    }


    /**
     * @description 将obj转换成BigDecimal，并做安全校验  默认为0
     * @author sip
     * @param obj
     * @return
     */
    public static BigDecimal valueOfBigDecimal(Object obj){
        if(isNullOrEmpty(obj)){
            return BigDecimal.ZERO;
        }
        if(isNumeric(obj)){
            return new BigDecimal(String.valueOf(obj));
        }
        return BigDecimal.ZERO;
    }

    /**
     * @description 将obj转换成Double，并做安全校验  默认为0
     * @author sip
     * @param obj
     * @return
     */
    public static Double valueOfDouble(Object obj){
        if(isNullOrEmpty(obj)){
            return 0D;
        }
        if(isNumeric(obj)){
            return Double.parseDouble(String.valueOf(obj));
        }
        return 0D;
    }

    /**
     * @description 将obj转换成String，并做安全校验  默认为""
     * @author sip
     * @param obj
     * @return
     */
    public static String valueOfString(Object obj){
        if(isNullOrEmpty(obj)){
            return "";
        }
        return String.valueOf(obj);
    }

    /**
     * @description 将obj转换成Float，并做安全校验  默认为0
     * @author sip
     * @param obj
     * @return
     */
    public static Float valueOfFloat(Object obj){
        if(isNullOrEmpty(obj)){
            return 0F;
        }
        if(isNumeric(obj)){
            return Float.parseFloat(String.valueOf(obj));
        }
        return 0F;
    }

    /**
     * @description 将obj转换成Integer，并做安全校验  默认为0
     * @param obj
     * @return
     */
    public static Integer valueOfInteger(Object obj){
        if(isNullOrEmpty(obj)){
            return 0;
        }
        if(isNumeric(obj)){
            return Integer.parseInt(String.valueOf(obj));
        }
        return 0;
    }

    /**
     * @description 将obj转换成Long，并做安全校验  默认为0L
     * @param obj
     * @return
     */
    public static Long valueOfLong(Object obj){
        if(isNullOrEmpty(obj)){
            return 0L;
        }
        if(isNumeric(obj)){
            return Long.parseLong(String.valueOf(obj));
        }
        return 0L;
    }

    /**
     * 转boolean
     * @param obj
     * @return
     */
    public static boolean valueOfBoolean(Object obj){
        if(isNullOrEmpty(obj)){
            return false;
        }
        return Boolean.valueOf(obj.toString());
    }

    /**
     * @description 判断obj是否是数字类型
     * @param obj
     * @return
     */
    public static boolean isNumeric(Object obj){
        try {
            Double.parseDouble(valueOfString(obj));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 这是一个补丁方法，为了去掉PO.toUpdate()的返回语句中的where条件
     * @description String sql = "select name from user where userId = 1";
     * 				sql = docjing(sql);       //  sql = select name from user
     * @param update
     * @return
     */
    public static String docking(String update){
        int lastIndexOf = update.lastIndexOf("WHERE") >= 0 ? update.lastIndexOf("WHERE") : update.length();
        return update.substring(0,lastIndexOf)
                .replace("TIMESTAMP","")
                .replace("DATETIME", "")
                .replace("CHANGE", "`CHANGE`");
    }

    /**
     * 这是一个补丁方法，为了将从页面传输到后台的List<Map>转换成接口使用的String[]
     * @param source
     * @param key
     * @return
     */
    public static <T extends Map> String[] patchString(List<T> source, String key){
        String[] returnBean = null;
        if(source != null && !source.isEmpty()){
            returnBean = new String[source.size()];
            for (int i = 0; i < source.size(); i++) {
                returnBean[i] = ((Map)source.get(i)).get(key) == null ? "" : ((Map)source.get(i)).get(key).toString();
            }
        }
        return returnBean;
    }

    public static boolean containsEmoji(String source) {
        int len = source.length();
        boolean isEmoji = false;
        for (int i = 0; i < len; i++) {
            char hs = source.charAt(i);
            if (0xd800 <= hs && hs <= 0xdbff) {
                if (source.length() > 1) {
                    char ls = source.charAt(i + 1);
                    int uc = ((hs - 0xd800) * 0x400) + (ls - 0xdc00) + 0x10000;
                    if (0x1d000 <= uc && uc <= 0x1f77f) {
                        return true;
                    }
                }
            } else {
                // non surrogate
                if (0x2100 <= hs && hs <= 0x27ff && hs != 0x263b) {
                    return true;
                } else if (0x2B05 <= hs && hs <= 0x2b07) {
                    return true;
                } else if (0x2934 <= hs && hs <= 0x2935) {
                    return true;
                } else if (0x3297 <= hs && hs <= 0x3299) {
                    return true;
                } else if (hs == 0xa9 || hs == 0xae || hs == 0x303d
                        || hs == 0x3030 || hs == 0x2b55 || hs == 0x2b1c
                        || hs == 0x2b1b || hs == 0x2b50 || hs == 0x231a) {
                    return true;
                }
                if (!isEmoji && source.length() > 1 && i < source.length() - 1) {
                    char ls = source.charAt(i + 1);
                    if (ls == 0x20e3) {
                        return true;
                    }
                }
            }
        }
        return isEmoji;
    }

    public static boolean isNull(Object o) {
        if(null == o) {
            return true;
        }else {
            if(o.equals("") || o.toString().trim().equals("")) {
                return true;
            }else {
                if(o instanceof List) {
                    List l = (List)o;
                    if(l.isEmpty()) {
                        return true;
                    }else {
                        return false;
                    }
                }else {
                    if(o instanceof Map) {
                        Map m = (Map)o;
                        if(m.isEmpty()) {
                            return true;
                        }else {
                            return false;
                        }
                    }else {
                        return false;
                    }
                }
            }
        }
    }

    public static boolean isNotNull(Object o) {
        return !isNull(o);
    }

    private static boolean isEmojiCharacter(char codePoint) {
        return (codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA)
                || (codePoint == 0xD)
                || ((codePoint >= 0x20) && (codePoint <= 0xD7FF))
                || ((codePoint >= 0xE000) && (codePoint <= 0xFFFD))
                || ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF));
    }

    public static String filterEmoji(String source) {
        if (org.apache.commons.lang3.StringUtils.isBlank(source)) {
            return source;
        }
        StringBuilder buf = null;
        int len = source.length();
        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);
            if (isEmojiCharacter(codePoint)) {
                if (buf == null) {
                    buf = new StringBuilder(source.length());
                }
                buf.append(codePoint);
            }
        }
        if (buf == null) {
            return source;
        } else {
            if (buf.length() == len) {
                buf = null;
                return source;
            } else {
                return buf.toString();
            }
        }
    }


    public static String join(Object[] array, String separator) {
        if (array == null) {
            return null;
        }
        return join(array, separator, 0, array.length);
    }

    public static String join(Object[] array, String separator, int startIndex, int endIndex) {
        if (array == null) {
            return null;
        }
        if (separator == null) {
            separator = "";
        }

        // endIndex - startIndex > 0:   Len = NofStrings *(len(firstString) + len(separator))
        //           (Assuming that all Strings are roughly equally long)
        int bufSize = (endIndex - startIndex);
        if (bufSize <= 0) {
            return "";
        }

        bufSize *= ((array[startIndex] == null ? 16 : array[startIndex].toString().length())
                + separator.length());

        StrBuilder buf = new StrBuilder(bufSize);

        for (int i = startIndex; i < endIndex; i++) {
            if (i > startIndex) {
                buf.append(separator);
            }
            if (array[i] != null) {
                buf.append(array[i]);
            }
        }
        return buf.toString();
    }
}

