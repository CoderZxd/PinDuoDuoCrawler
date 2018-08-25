package com.pinduoduo.test;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import okhttp3.*;

/**
 * @author CoderZZ
 * @Title: ${FILE_NAME}
 * @Project: PicCrawler
 * @Package com.test.pic.crawler
 * @description: 多多进宝单品推广页面数据抓取
 * @Version 1.0
 * @create 2018-08-25 23:36
 **/
public class PinDuoDuoCrawler {
    public static String url = "http://jinbao.pinduoduo.com/network/api/common/goodsList";

    public static void main(String[] args) throws Exception{

        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"keyword\": \"\", \"sortType\": 0, \"withCoupon\": 0, \"categoryId\": -1, \"pageNumber\": 1,\"pageSize\": 60}");
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = client.newCall(request).execute();
        JSONObject resultJSONObj = JSONObject.parseObject(response.body().string());
        if(null != resultJSONObj && null == resultJSONObj.getString("errorMsg")){
            if("true".equalsIgnoreCase(resultJSONObj.getString("success")) && "1000000".equalsIgnoreCase(resultJSONObj.getString("errorCode"))){
                JSONObject result = resultJSONObj.getJSONObject("result");
                if(null != result && !result.isEmpty()){
                    JSONArray goodsList = result.getJSONArray("goodsList");
                    if(null != goodsList && !goodsList.isEmpty()){
                        for(int i=0;i<goodsList.size();i++){
                            GoodInfo goodInfo = JSONObject.parseObject(goodsList.getJSONObject(i).toJSONString(),GoodInfo.class);
                            System.out.println("http://mobile.yangkeduo.com/goods2.html?goods_id="+goodInfo.getGoodsId());
                        }
                    }
                }
            }
        }else{
            System.out.println("数据抓取失败!");
        }
    }
}
