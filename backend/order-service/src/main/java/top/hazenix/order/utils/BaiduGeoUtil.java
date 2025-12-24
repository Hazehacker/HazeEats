package top.hazenix.order.utils;

/**
 * 选择了ak或使用IP白名单校验：
 */



import top.hazenix.properties.BaiduGeoProperties;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedHashMap;
import java.util.Map;
@Data
@Slf4j
@Component
public class BaiduGeoUtil {

    @Autowired
    private BaiduGeoProperties baiduGeoProperties;




    /**
     * 默认ak
     * 选择了ak，使用IP白名单校验：
     * 根据您选择的AK已为您生成调用代码
     * 检测到您当前的ak设置了IP白名单校验
     * 您的IP白名单中的IP非公网IP，请设置为公网IP，否则将请求失败
     * 请在IP地址为0.0.0.0/0 外网IP的计算发起请求，否则将请求失败
     */
    public String requestGeoCodingAPI(String detailAddress) throws Exception {
        String strUrl = baiduGeoProperties.getGeoCodingUrl();
        String AK = baiduGeoProperties.getAK();
        Map<String,String> param = new LinkedHashMap<String, String>();
        param.put("address", detailAddress);
        param.put("output", "json");
        param.put("ak", AK);
//        param.put("callback", "showLocation");
            //如果添加这一栏，会多用一个showLocation&&showLocation()包裹json

        if (strUrl == null || strUrl.length() <= 0 || param == null || param.size() <= 0) {
            return null;
        }

        StringBuffer queryString = new StringBuffer();
        queryString.append(strUrl);

        for (Map.Entry<?, ?> pair :  param.entrySet()) {
            queryString.append(pair.getKey() + "=");
            //    第一种方式使用的 jdk 自带的转码方式  第二种方式使用的 spring 的转码方法 两种均可
            //    queryString.append(URLEncoder.encode((String) pair.getValue(), "UTF-8").replace("+", "%20") + "&");
            queryString.append(UriUtils.encode((String) pair.getValue(), "UTF-8") + "&");
        }

        if (queryString.length() > 0) {
            queryString.deleteCharAt(queryString.length() - 1);
        }

        java.net.URL url = new URL(queryString.toString());
        log.info(queryString.toString());
        URLConnection httpConnection = (HttpURLConnection) url.openConnection();
        httpConnection.connect();

        InputStreamReader isr = new InputStreamReader(httpConnection.getInputStream());
        BufferedReader reader = new BufferedReader(isr);
        StringBuffer buffer = new StringBuffer();
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        reader.close();
        isr.close();
        log.info("地理编码接口返回json: {}",buffer.toString());
        return buffer.toString();
    }

    public String requestRoutePlanApi(double lnt , double lat) throws IOException {
        String strUrl = baiduGeoProperties.getRoutePlanUrl();
        Map<String,String> param = new LinkedHashMap<String, String>();
        String lngAndLat = baiduGeoProperties.getShopLat()+","+baiduGeoProperties.getShopLng();
        String originLngAndLat = lat +","+ lnt;
        param.put("origin", originLngAndLat);
        param.put("destination", lngAndLat);
        param.put("ak", baiduGeoProperties.getAK());
        if (strUrl == null || strUrl.length() <= 0 || param == null || param.size() <= 0) {
            return null;
        }

        StringBuffer queryString = new StringBuffer();
        queryString.append(strUrl);
        for (Map.Entry<?, ?> pair : param.entrySet()) {
            queryString.append(pair.getKey() + "=");
            //    第一种方式使用的 jdk 自带的转码方式  第二种方式使用的 spring 的转码方法 两种均可
            //    queryString.append(URLEncoder.encode((String) pair.getValue(), "UTF-8").replace("+", "%20") + "&");
            queryString.append(UriUtils.encode((String) pair.getValue(), "UTF-8") + "&");
        }

        if (queryString.length() > 0) {
            queryString.deleteCharAt(queryString.length() - 1);
        }

        java.net.URL url = new URL(queryString.toString());
        log.info(queryString.toString());
        URLConnection httpConnection = (HttpURLConnection) url.openConnection();
        httpConnection.connect();

        InputStreamReader isr = new InputStreamReader(httpConnection.getInputStream());
        BufferedReader reader = new BufferedReader(isr);
        StringBuffer buffer = new StringBuffer();
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        reader.close();
        isr.close();
        log.info("路线规划接口返回json: {}",buffer.toString());
        return buffer.toString();
    }




}