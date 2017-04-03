/*
 * This is ModemYar project developed by MV_MRP.
 * Copyright (c) 2017. All rights reserved.
 */
package networksample.modems.tenda;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import networksample.modems.Modem;
import networksample.modems.WifiStatus;
import static util.Md5.md5;

/**
 *
 * @author mv
 */
public class Tenda extends Modem{
    String login = "http://192.168.1.1/";
//    String login = "http://127.0.0.1:8089/";

    @Override
    public boolean login(String user, String pass) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("username", md5(user));
        params.put("password", md5(pass));
        params.put("sessionKey", "0.1");
        try{
            String ans = generalRequest(login+"login.cgi", params, true, null);
            System.out.println(ans);
            
            return ans.contains("index");
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean changeWifiConfig(String name, String pass, WifiStatus status) {
        LinkedHashMap<String,String> map = new LinkedHashMap<>();
        map.put("action","add");
        map.put("linkType","DSL");
        map.put("pvcEn","0");
        map.put("country_area","IR|ITC");
        map.put("VPI","0");
        map.put("VCI","35");
        map.put("conType","PPPoE");
        map.put("pppUserName","add");
        map.put("pppPassword","02166908235");
        map.put("wirelessEnable","1");
        map.put("ssid", name);
        map.put("wlWpaPsk", pass);
//                wlWpaPsk=22m%40sh%40d52&
        try{
            String ans = generalRequest(login+"quicksetup.cmd", map, false, null);
            System.out.println(ans);
            
            return ans.contains("index");
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean changeLogin(String oldPass, String newUser, String newPass) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ArrayList<ConnectedDevice> getDeviceList() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public WifiConfig getWifiConfig() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean changeInternetSetting(String username, String password) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public InternetConfig getInternetSetting() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
