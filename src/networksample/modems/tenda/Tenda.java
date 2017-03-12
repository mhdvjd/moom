/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package networksample.modems.tenda;

import java.util.LinkedHashMap;
import networksample.modems.Modem;
import static util.Md5.md5;

/**
 *
 * @author mv
 */
public class Tenda extends Modem {

    String login = "http://192.168.1.1/";
//    String login = "http://127.0.0.1:8089/";

    @Override
    public boolean login(String user, String pass) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("username", md5(user));
        params.put("password", md5(pass));
        params.put("sessionKey", "0.1");
        try {
            String ans = request(login + "login.cgi", params, true, null);
            System.out.println(ans);

            return ans.contains("index");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean setWifi(String ssid, String wifiPass, boolean enable, wifiPassType passType) {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("action", "add");
        map.put("linkType", "DSL");
        map.put("pvcEn", "0");
        map.put("country_area", "IR|ITC");
        map.put("VPI", "0");
        map.put("VCI", "35");
        map.put("conType", "PPPoE");
        map.put("pppUserName", "add");
        map.put("pppPassword", "02166908235");
        map.put("wirelessEnable", "1");
        map.put("ssid", ssid);
        map.put("wlWpaPsk", wifiPass);
//                wlWpaPsk=22m%40sh%40d52&
        try {
            String ans = request(login + "quicksetup.cmd", map, false, null);
            System.out.println(ans);

            return ans.contains("index");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
