package com.hswift.erp.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * Created by Jay Shen 2018-5-11 09:48:08
 *
 * @author Jay Shen
 */
public class JsonUtils {

    public static JSONObject ok(){
        JSONObject obj = new JSONObject();
        JSONObject tmp = new JSONObject();
        tmp.put("message", "成功");
        obj.put("code", 200);
        obj.put("data", tmp);
        return obj;
    }

}
