/*
 * This is ModemYar project developed by MV_MRP.
 * Copyright (c) 2017. All rights reserved.
 */
package networksample;

import java.util.Base64;
import networksample.modems.Modem;
import networksample.modems.WifiStatus;
import networksample.modems.tenda.Tenda;
import networksample.modems.tplink.TpLink;
import util.Md5;

/**
 *
 * @author mv
 */
public class NetworkSample {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
//        if(args.length != 4){
//            System.out.println("please run as 'username pass wifi-name wifi-pass'");
//            return;
//        }
//        Modem t = new TpLink();
//        if(!t.login(args[0], args[1])){
//            System.out.println("invalid username or pass");
//            return;
//        }else{
//            System.out.println("login success");
//        }
//        t.changeWifiConfig(args[2], args[3]);
        
        TpLink t = new TpLink();
        System.out.println(t.login("admin", "myhome2220653"));
        t.changeWifiConfig("mahdi", "22m@sh@d52", WifiStatus.visible);
    }
    
}
