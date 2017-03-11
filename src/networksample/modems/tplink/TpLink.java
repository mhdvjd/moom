/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package networksample.modems.tplink;

import java.util.LinkedHashMap;
import networksample.modems.Modem;
import static util.Md5.md5;

/**
 *
 * @author mv
 */
public class TpLink extends Modem{

    String login = "http://192.168.1.1/";
    
    @Override
    public boolean login(String user, String pass) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("username", user);
        params.put("password", pass);
        params.put("submit.htm%3Flogin.htm", "Send");
        try{
            String ans = request(login+"login.cgi", params, true, null);
            System.out.println(ans);
            
            return ans.contains("index");
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean setWifi(String name, String pass) {
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
            String ans = request(login+"form2WlanSetup.cgi", map, true, null);
            System.out.println(ans);
            
            return ans.contains("index");
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }
    
}
