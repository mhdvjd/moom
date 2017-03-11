/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package networksample;

import networksample.modems.Modem;
import networksample.modems.tenda.Tenda;
import networksample.modems.tplink.TpLink;

/**
 *
 * @author mv
 */
public class NetworkSample {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        if(args.length != 4){
            System.out.println("please run as 'username pass wifi-name wifi-pass'");
            return;
        }
        Modem t = new TpLink();
        if(!t.login(args[0], args[1])){
            System.out.println("invalid username or pass");
            return;
        }else{
            System.out.println("login success");
        }
        t.setWifi(args[2], args[3]);
        
//        Tenda t = new Tenda();
//        System.out.println(t.login("admin", "admin"));
//        t.setWifi("mahdi", "22m@sh@d52");
    }
    
}
