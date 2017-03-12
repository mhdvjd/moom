/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package networksample.modems.iplink;

import java.util.LinkedHashMap;
import networksample.modems.Modem;
import static util.Md5.md5;

/**
 *
 * @author mv
 */
public class IPLink extends Modem {

    String login = "http://127.0.0.1:8080/";

    @Override
    public boolean login(String user, String pass) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("username", user);
        params.put("password", pass);
        params.put("submit.htm?Flogin.htm", "Send");
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
        /**
         * Set SSID && Enable|Disable WLAN
         */
        /*
        if (enable) {
            map.put("band", "10");
            map.put("mode", "0");
            map.put("chanwid", "1");
            map.put("ctlband", "0");
            map.put("chan", "0");
            map.put("txpower", "0");
            map.put("basicrates", "15");
            map.put("operrates", "4095");
            if (ssid != null && !ssid.isEmpty()) {
                map.put("ssid", ssid);
            }
        } else {
            System.out.println("disableing wlan ...");
            map.put("wlanDisabled", "ON");
            map.put("basicrates", "0");
            map.put("operrates", "0");
        }
        map.put("save", "Apply Changes");
        map.put("submit.htm?Fwlbasic.htm", "send");
        try {
            String ans = request(login + "form2WlanSetup.cgi", map, true, null);
            System.out.println(ans);

            if (ans.contains("index")) {
                System.out.println("SSID changed succesfully");
            } else {
                System.out.println("Could not change ssid");
//                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        */

        /**
         * Set WiFi Password & Encryption Type
         */
        
        map = new LinkedHashMap<>();
        map.put("wlanDisabled", "OFF");
        map.put("lenght", "1");
        map.put("format", "1");
        map.put("defaultTxKeyId", "1");
        map.put("wpaAuth", "2");
        map.put("pskFormat", "0");
        map.put("checkWPS2", "1");
        map.put("save", "Apply Changes");
        map.put("submit.htm?wlwpa.htm", "Send");

        map.put("wpaSSID", "0");
        switch (passType) {
            case NONE:
                System.out.println("set encrypt type to none\n");
                map.put("method", "0");
                map.put("key1", "*****");
                map.put("key2", "*****");
                map.put("key3", "*****");
                map.put("key4", "*****");
                break;
            case WEP:
                System.out.println("set encrypt type to wep\n");
                map.put("wpaSSID", "1");
                map.put("key1", "*****");
                map.put("key2", "*****");
                map.put("key3", "*****");
                map.put("key4", "*****");
                break;
            case WPA_AES:
                System.out.println("set encrypt type to wpa\n");
                map.put("wpaSSID", "3");
                map.put("key1", "*****");
                map.put("key2", "*****");
                map.put("key3", "*****");
                map.put("key4", "*****");
                break;
            case WPA2_TKIP:
                System.out.println("set encrypt type to wpa2\n");
                map.put("wpaSSID", "5");
                map.put("key1", "*****");
                map.put("key2", "*****");
                map.put("key3", "*****");
                map.put("key4", "*****");
                break;
            default:
                break;
        }

        if (wifiPass != null && !wifiPass.isEmpty()) {
            map.put("pskValue", wifiPass);
        } else {
            map.put("pskValue", "********");
        }

        try {
            String ans = request(login + "form2WlEncrypt.cgi", map, true, null);
            System.out.println(ans);

            if (ans.contains("index")) {
                System.out.println("Pass changed succesfully");
            } else {
                System.out.println("Could not change Pass");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
         
        return true;
    }

}
