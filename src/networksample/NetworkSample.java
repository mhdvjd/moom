/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package networksample;

import java.util.StringTokenizer;
import networksample.modems.Modem;
import networksample.modems.tenda.Tenda;
import networksample.modems.iplink.IPLink;

/**
 *
 * @author mv
 */
public class NetworkSample {

    enum modemModels {
        Tenda,
        IP_Link,
        TP_Link,
        D_Link,
    }

    enum modemFunctions {
        login,
        setWifi,
        logout,
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {

        Modem modem = null;

        System.out.println("Please Select Modem: \n"
                + modemModels.Tenda.ordinal() + ":" + modemModels.Tenda + "\n"
                + modemModels.IP_Link.ordinal() + ":" + modemModels.IP_Link + "\n"
                + modemModels.TP_Link.ordinal() + ":" + modemModels.TP_Link + "\n"
                + modemModels.D_Link.ordinal() + ":" + modemModels.D_Link);

        String userModel = System.console().readLine();
        modemModels userModemModel = modemModels.values()[Integer.parseInt(userModel)];

        switch (userModemModel) {
            case Tenda:
                System.out.println("your modem is " + modemModels.Tenda + "\n");
                modem = new Tenda();
                break;
            case IP_Link:
                System.out.println("your modem is " + modemModels.IP_Link + "\n");
                modem = new IPLink();
                break;
            case TP_Link:
                System.out.println("your modem is " + modemModels.TP_Link + "\n");
                break;
            case D_Link:
                System.out.println("your modem is " + modemModels.D_Link + "\n");
                break;
            default:
                System.out.println("This model is not supported yet\n");
                break;
        }

        while (true) {
            System.out.println("\n\n=======================================================\n"
                    + "Please Select Function: \n"
                    + modemFunctions.login.ordinal() + ":" + modemFunctions.login + "\n"
                    + modemFunctions.setWifi.ordinal() + ":" + modemFunctions.setWifi + "\n"
                    + modemFunctions.logout.ordinal() + ":" + modemFunctions.logout);
            String userFunction = System.console().readLine();
            modemFunctions userModemFunction = modemFunctions.values()[Integer.parseInt(userFunction)];
            switch (userModemFunction) {
                case login:
                    System.out.println("\nPlease Enter \"USERNAME PASSWORD\"");
                    String input = System.console().readLine();
                    StringTokenizer tokenizer = new StringTokenizer(input, " ");
                    int i = 0;
                    String username = null,
                     password = null;
                    while (tokenizer.hasMoreElements()) {
                        switch (i) {
                            case 0:
                                username = tokenizer.nextElement().toString();
                                break;
                            case 1:
                                password = tokenizer.nextElement().toString();
                                break;
                        }
                        i++;
                    }
                    if (modem.login(username, password)) {
                        System.out.println("login successfully :)");
                    } else {
                        System.out.println("login failed :)");
                    }
                    break;
                case setWifi:
                    System.out.println("\nPlease Enter \"SSID PASS ENABLE TYPE\"");
                    
                    input = System.console().readLine();
                    tokenizer = new StringTokenizer(input, " ");
                    i = 0;
                    String ssid = null,
                     wifiPass = null;
                    boolean enable = true;
                    Modem.wifiPassType type = Modem.wifiPassType.WPA2_TKIP; // default value
                    String tmp;
                    
                    while (tokenizer.hasMoreElements()) {
                        switch (i) {
                            case 0:
                                ssid = tokenizer.nextElement().toString();
                                break;
                            case 1:
                                wifiPass = tokenizer.nextElement().toString();
                                break;
                            case 2:
                                tmp = tokenizer.nextElement().toString();
                                if (tmp.equals("no")) {
                                    enable = false;
                                } else {
                                    enable = true;
                                }
                                break;
                            case 3:
                                tmp = tokenizer.nextElement().toString();
                                if (tmp.equals("wep")) {
                                    type = Modem.wifiPassType.WEP;
                                } else if (tmp.equals("wpa")) {
                                    type = Modem.wifiPassType.WPA_AES;
                                } else {
                                    type = Modem.wifiPassType.WPA2_TKIP;
                                }
                                break;
                            default:
                                break;
                        }
                        i++;
                    }
                    modem.setWifi(ssid, wifiPass, enable, type);
                    break;
                case logout:
                    System.out.println("Bye Bye ...");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Your function is not supported yet\n");
                    break;
            }
        }
        /*
        if (args.length != 4) {
            System.out.println("please run as 'username pass wifi-name wifi-pass'");
            return;
        }

        Modem t = new IPLink();
        if (!t.login(args[0], args[1])) {
            System.out.println("invalid username or pass");
            return;
        } else {
            System.out.println("login success");
        }
        t.setWifi(args[2], args[3]);
         */
    }

}
