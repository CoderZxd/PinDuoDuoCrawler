package com.pinduoduo.test;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import okhttp3.*;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

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

    private static String prefix = "http://mobile.yangkeduo.com/goods2.html?goods_id=";

    private static String fullPath = "D:/goodsList.txt";

    public static void main(String[] args){
        try {
            boolean isContinue = true;
            List<String> linkList = new ArrayList<String>(10000);
            OkHttpClient client = new OkHttpClient();
            MediaType mediaType = MediaType.parse("application/json");
            for(int i=1;i<Integer.MAX_VALUE && isContinue;i++){
                String params = "{\"keyword\": \"\", \"sortType\": 0, \"withCoupon\": 0,  \"pageNumber\": "+i+",\"pageSize\": 500}";
                RequestBody body = RequestBody.create(mediaType, params);
                Request request = new Request.Builder().url(url).post(body).addHeader("Content-Type", "application/json").build();
                Response response = client.newCall(request).execute();
                JSONObject resultJSONObj = JSONObject.parseObject(response.body().string());
                if(null != resultJSONObj && null == resultJSONObj.getString("errorMsg")){
                    if("true".equalsIgnoreCase(resultJSONObj.getString("success")) && "1000000".equalsIgnoreCase(resultJSONObj.getString("errorCode"))){
                        JSONObject result = resultJSONObj.getJSONObject("result");
                        if(null != result && !result.isEmpty()){
                            JSONArray goodsList = result.getJSONArray("goodsList");
                            if(null != goodsList && !goodsList.isEmpty()){
                                for(int j=0;j<goodsList.size();j++){
                                    GoodInfo goodInfo = JSONObject.parseObject(goodsList.getJSONObject(j).toJSONString(),GoodInfo.class);
                                    String link = prefix+goodInfo.getGoodsId();
                                    System.out.println(link);
                                    linkList.add(link);
                                }
                            }
                        }
                    }
                }else{
                    isContinue = false;
                    System.out.println("数据抓取结束,共抓取了"+(i-1)+"次数据,每次抓取500条,总共抓取了"+linkList.size()+"条数据。最后一次抓取返回信息为:"+resultJSONObj.getString("errorMsg"));
                }
            }
            if(!linkList.isEmpty()){
                File file = new File(fullPath);
                if (!file.exists()) {
                    file.createNewFile();
                }
                PrintWriter printWriter = new PrintWriter(file, "UTF-8"); //设置输出文件的编码为utf-8
                StringBuffer sb = new StringBuffer("");
                for(String link:linkList){
                    sb.append(link).append("\n");
                }
                printWriter.write(sb.toString());
                printWriter.close();
                System.out.println("***************************************************");
                System.out.println("抓取的数据已经写入"+fullPath+"文件中,请查看。");
                System.out.println("***************************************************");
            }
        }catch (Exception e){
            System.out.println("数据抓取异常!异常信息为:"+e.getMessage());
        }
    }
}
