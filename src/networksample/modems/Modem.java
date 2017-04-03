/*
 * This is ModemYar project developed by MV_MRP.
 * Copyright (c) 2017. All rights reserved.
 */
package networksample.modems;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author mv
 */
public abstract class Modem {

    private String cookie = null;
    
    
    public abstract boolean login(String user, String pass);
    public abstract boolean changeLogin(String oldPass, String newUser, String newPass);
    
    public abstract ArrayList<ConnectedDevice> getDeviceList();
    
    public abstract boolean changeWifiConfig(String name, String pass, WifiStatus status);
    public abstract WifiConfig getWifiConfig();
    
    public abstract boolean changeInternetSetting(String username, String password);
    public abstract InternetConfig getInternetSetting();

    
    
    
    public String generalRequest(String url, LinkedHashMap<String, String> params, boolean isPost, 
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
            if(temp!=null)
                this.cookie = temp;
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

    
    
    
    public static class ConnectedDevice {
        private final String name;
        private final String ip;
        private final String mac;
        public ConnectedDevice(String name, String ip, String mac) {
            this.name = name;
            this.ip = ip;
            this.mac = mac;
        }

        public String getName() {
            return name;
        }

        public String getIp() {
            return ip;
        }

        public String getMac() {
            return mac;
        }
    }

    public static class InternetConfig {
        private final String username;
        private final String password;
        private final int vci;
        private final int vpi;
        
        public InternetConfig(String username, String password, int vci, int vpi) {
            this.username = username;
            this.password = password;
            this.vci = vci;
            this.vpi = vpi;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public int getVci() {
            return vci;
        }

        public int getVpi() {
            return vpi;
        }
    }

    public static class WifiConfig {
        private final String name;
        private final String pass;
        private final WifiStatus status;

        public WifiConfig(String name, String pass, WifiStatus status) {
            this.name = name;
            this.pass = pass;
            this.status = status;
        }

        public String getName() {
            return name;
        }

        public String getPass() {
            return pass;
        }

        public WifiStatus getStatus() {
            return status;
        }
        
    }
}
