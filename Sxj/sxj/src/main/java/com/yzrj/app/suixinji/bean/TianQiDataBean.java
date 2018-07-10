package com.yzrj.app.suixinji.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by admin on 2017/2/23.
 */
public class TianQiDataBean implements Serializable {
    public List<TQBean> forecast;
    public String city;//": "厦门",
    public String aqi;//": "15",
    public String ganmao;//": "各项气象条件适宜，发生感冒机率较低。但请避免长期处于空调房间中，以防感冒。",
    public String wendu;//": "29"

    public static class TQBean implements Serializable {
        public String date;//": "6日星期四",
        public String high;//": "高温 30℃",
        public String fengli;//": "微风级",
        public String low;//": "低温 25℃",
        public String fengxiang;//": "无持续风向",
        public String type;//": "中雨"
    }
}
