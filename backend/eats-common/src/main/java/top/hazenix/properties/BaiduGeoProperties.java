package top.hazenix.properties;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "hazeeats.baidu")
@Data
public class
BaiduGeoProperties {
    private String geoCodingUrl;
    private String routePlanUrl;
    private String AK;
    private String shopLng;//经度
    private String shopLat;//纬度

}
