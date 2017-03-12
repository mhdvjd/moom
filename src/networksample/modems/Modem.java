/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package networksample.modems;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author mv
 */
public abstract class Modem {
        
    String cookie = null;
        
    public enum wifiPassType {
        NONE,
        WEP,
        WPA_AES,
        WPA2_TKIP,
    }
    
    public abstract boolean login(String user, String pass);
    public abstract boolean setWifi(String ssid, String wifiPass, boolean enable, wifiPassType passType);

    
    public String request(String url, LinkedHashMap<String, String> params, boolean isPost,
            HashMap<String, String> prop)throws MalformedURLException, IOException {

        if(!isPost && params!=null)
            url = url+"?"+getQuery(params);
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("Accept-Language", "fa,en;q=0.5");
        con.setRequestProperty("Accept-Charset", "UTF-8");

        if(prop!=null){
            prop.entrySet().stream().forEach((entrySet) -> {
                con.setRequestProperty(
                        entrySet.getKey(),
                        entrySet.getValue()
                );
            });
        }

        if(cookie!=null){
            con.setRequestProperty("Cookie", cookie);
        }

        if(isPost && params!=null){
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                wr.writeBytes(getQuery(params));
                wr.flush();
            }
        }
        int responseCode=200;
        try {
            responseCode = con.getResponseCode();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("responseCode: "+responseCode);
        StringBuilder response = new StringBuilder();
        if(responseCode==200){
            String temp = con.getHeaderField("Set-Cookie");
            if(temp!=null){
                this.cookie = temp;
                System.out.println(this.cookie);
            }
            System.out.println("no cookie set");
            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                String inputLine;

                while ((inputLine = in.readLine()) != null ) {
                    //            Funcs.log("netHandler.sendPostIN:", ""+inputLine);
                    //            x = inputLine.indexOf("<!DOCTYPE html>");
                    //            if(x>=0)
                    //            break;
                    //            inputLine = inputLine.substring(0, x);
                    response.append(inputLine);
                }
            }
            return response.toString();
        }else
            return null;

        
    }

    private String getQuery(LinkedHashMap<String, String> params) {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (Map.Entry<String, String> pair : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(pair.getKey());
            result.append("=");
            result.append(pair.getValue());
//            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
//            result.append("=");
//            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }
}
