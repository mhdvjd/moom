/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package networksample.modems.iplink;

import java.io.BufferedReader;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import networksample.modems.Modem;
import static util.Md5.md5;

/**
 *
 * @author mv
 */
public class IPLink extends Modem {

    String login = "http://192.168.1.1/";

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
        if (enable) {
            System.out.println("Setting ssid ...\n");
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
            System.out.println("Disableing wlan ...");
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
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        /**
         * Set WiFi Password & Encryption Type
         */
        map = new LinkedHashMap<>();
        map.put("wlanDisabled", "OFF");
        map.put("wpaSSID", "0");
        switch (passType) {
            case NONE:
                System.out.println("setting encrypt type to none\n");
                map.put("method", "0");
                break;
            case WEP:
                System.out.println("setting encrypt type to wep\n");
                map.put("method", "1");
                break;
            case WPA_AES:
                System.out.println("setting encrypt type to wpa\n");
                map.put("method", "3");
                break;
            case WPA2_TKIP:
                System.out.println("setting encrypt type to wpa2\n");
                map.put("method", "5");
                break;
            default:
                break;
        }
        map.put("lenght", "1");
        map.put("format", "1");
        map.put("defaultTxKeyId", "1");
        map.put("key1", "*****");
        map.put("key2", "*****");
        map.put("key3", "*****");
        map.put("key4", "*****");
        map.put("wpaAuth", "2");
        map.put("pskFormat", "0");
        if (wifiPass != null && !wifiPass.isEmpty()) {
            System.out.println("Setting WiFi password ...\n");
            map.put("pskValue", wifiPass);
        } else {
            map.put("pskValue", "********");
        }
        map.put("checkWPS2", "1");
        map.put("save", "Apply+Changes");
        map.put("submit.htm%3wlwpa.htm", "Send");

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

    @Override
    public wifiInfo getWifi() {

        String ssid = "", pass = "";
        boolean wifiDisabled = false;
        wifiPassType type = null;

        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        LinkedHashMap<String, String> prop = new LinkedHashMap<>();

        prop.put("Upgrade-Insecure-Requests", "1");
        prop.put("Referer", "http://192.168.1.1/code.htm");
        prop.put("Connection", "close");

        try {
            
            String ret = request(login + "wlbasic.htm", params, false, prop);
            System.out.println(ret);
            
            BufferedReader result = request_getContent(login + "wlbasic.htm", params, false, prop);
            if (result != null) {
                String line;
                boolean find_ssid = false;
                boolean find_enable = false;
                while ((line = result.readLine()) != null) {
                    if (find_ssid) {
                        if (line.isEmpty()) {
                            continue;
                        }
//                        System.out.println(line);
                        ssid = line.replace("\"", "");
                        find_ssid = false;
                    }
                    if (find_enable) {
                        if (line.isEmpty()) {
                            continue;
                        }
//                        System.out.println(line);
                        if (line.contains("OFF")) {
                            wifiDisabled = false;
                        } else {
                            wifiDisabled = true;
                        }
                        find_enable = false;
                    }
                    if (line.contains("name=\"ssid\"")) {
//                        System.out.println(line);
                        find_ssid = true;
                    }
                    if (line.contains("name=\"wlanDisabled\"")) {
//                        System.out.println(line);
                        find_enable = true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        prop = new LinkedHashMap<>();
        prop.put("Upgrade-Insecure-Requests", "1");
        prop.put("Referer", "http://192.168.1.1/code.htm");
        prop.put("Connection", "close");

        try {
            BufferedReader result = request_getContent(login + "wlwpa.htm", params, false, prop);
            if (result != null) {
                String line;
                while ((line = result.readLine()) != null) {
                    if (line.contains("Password")) {
//                        System.out.println(line);
//                        find_pass = true;
                    }
                    if (line.contains("document.formEncrypt.method.value")) {
                        String tmp = line.split("=")[1];
                        tmp = tmp.replace(" ", "");
                        tmp = tmp.replace(";", "");
                        if (!tmp.contains("method")) {
                            if (tmp.equals("0")) {
                                type = wifiPassType.NONE;
                            } else if (tmp.equals("1")) {
                                type = wifiPassType.WEP;
                            } else if (tmp.equals("3")) {
                                type = wifiPassType.WPA_AES;
                            } else if (tmp.equals("5")) {
                                type = wifiPassType.WPA2_TKIP;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new wifiInfo(ssid, pass, wifiDisabled, type);
    }

}
