/*
 * This is ModemYar project developed by MV_MRP.
 * Copyright (c) 2017. All rights reserved.
 */
package networksample.modems.ip_link;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import networksample.modems.Modem;
import networksample.modems.WifiStatus;

/**
 *
 * @author mv
 */
public class IpLink extends Modem{

    String login = "http://192.168.1.1/";
    
    @Override
    public boolean login(String user, String pass) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("username", user);
        params.put("password", pass);
        params.put("submit.htm%3Flogin.htm", "Send");
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
        map.put("band","10");
        map.put("mode","0");
        map.put("ssid",name);
        map.put("chanwid","1");
        map.put("ctlband","0");
        map.put("chan","0");
        map.put("txpower","0");
        map.put("save","Apply+Changes");
        map.put("basicrates","15");
        map.put("operrates","4095");
        map.put("submit.htm%3Fwlbasic.htm", "send");
        try{
            String ans = generalRequest(login+"form2WlanSetup.cgi", map, true, null);
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
