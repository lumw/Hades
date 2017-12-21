package com.danyun.hades.util;


//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;

public class PropertyUtil {

    private static Properties props;

    static {
        loadProps();
    }

    synchronized static private void loadProps() {
        //logger.info("开始加载properties文件内容.......");
        props = new Properties();
        InputStream in = null;
        try {
            in = PropertyUtil.class.getClassLoader().getResourceAsStream("application.properties");
            props.load(in);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            //logger.error("配置文件未找到");
        } catch (IOException e) {
            e.printStackTrace();
            //logger.error(e.getLocalizedMessage());
        } finally {
            try {
                if (null != in) {
                    in.close();
                }
            } catch (IOException e) {
                //logger.error("配置文件流关闭出现异常");
                e.printStackTrace();
            }
        }
    }

    public static String getProperty(String key) {
        if (null == props) {
            loadProps();
        }
        return props.getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        if (null == props) {
            loadProps();
        }
        return props.getProperty(key, defaultValue);
    }
}
