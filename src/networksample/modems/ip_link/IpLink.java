/*
 * This is ModemYar project developed by MV_MRP.
 * Copyright (c) 2017. All rights reserved.
 */
package networksample.modems.ip_link;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import java.util.ArrayList;
import java.util.List;
import networksample.modems.Modem;
import networksample.modems.WifiStatus;

/**
 *
 * @author mv
 */
public class IpLink extends Modem {

    final String baseUrl = "http://192.168.1.1/";
    final WebClient webClient = new WebClient(BrowserVersion.CHROME);
    String username = "";

    @Override
    public boolean login(String user, String pass) {
        try {
            /*
            TODO:
                You can also implement empty classes to stop htmlUnity go verbose on
                console about css/javaScript errors with:
                webClient.setCssErrorHandler(new SilentCssErrorHandler());    
                webClient.setJavaScriptErrorListener(new JavaScriptErrorListener(){});
             */
            webClient.getOptions().setThrowExceptionOnScriptError(false);

            HtmlPage loginPage = webClient.getPage(baseUrl);
            HtmlForm form = loginPage.getFormByName("test");

            HtmlInput username = loginPage.getHtmlElementById("username");
            username.setValueAttribute(user);

            HtmlInput password = loginPage.getHtmlElementById("password");
            password.setValueAttribute(pass);

            HtmlPage indexPage
                    = (HtmlPage) form.getInputByValue("Login").click();

            this.username = user;

            return checkLogin(indexPage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }

    @Override
    public boolean changeLogin(String oldPass, String newUser, String newPass) {
        // now only support password
        try {
            HtmlPage userConfigPage = webClient.getPage(baseUrl + "userconfig.htm");
            if (!checkLogin(userConfigPage)) {
                throw new RuntimeException("Login first");
            }
            List<DomElement> userSelectElements = userConfigPage.getElementsByName("select");
            for (DomElement userElement : userSelectElements) {
                if (userElement.getAttribute("onclick").contains("'" + this.username + "'")) {
                    userConfigPage = userElement.click();
                    userConfigPage.getElementByName("oldpass").setAttribute("value", oldPass);
                    userConfigPage.getElementByName("newpass").setAttribute("value", newPass);
                    userConfigPage.getElementByName("confpass").setAttribute("value", newPass);
                    userConfigPage.getElementByName("modify").click();
                    // TODO: parse result of click
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public WifiConfig getWifiConfig() {

        String ssid_name;
        String password;
        WifiStatus wifiStatus;

        try {
            // get ssid name and wifi status
            HtmlPage wlbasicPage = webClient.getPage(baseUrl + "wlbasic.htm");
            if (!checkLogin(wlbasicPage)) {
                throw new RuntimeException("Login first");
            }
            ssid_name = wlbasicPage.getElementByName("ssid").asText();
            System.out.println(ssid_name);
            if (wlbasicPage.getElementByName("wlanDisabled").getAttribute("value").equals("OFF")) {
                wifiStatus = WifiStatus.visible;
            } else {
                wifiStatus = WifiStatus.deactive;
            }

            // get password
            HtmlPage wlwpaPage = webClient.getPage(baseUrl + "wlwpa.htm");
            password = wlwpaPage.getElementByName("pskValue").asText();

            // return
            return new WifiConfig(ssid_name, password, wifiStatus);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean changeWifiConfig(String name, String pass, WifiStatus status) {
        try {
            if (name != null || status != null) {
                // Note: when status is (or wants to be) deactive, name could not be changed
                // get the page
                HtmlPage wlbasicPage = webClient.getPage(baseUrl + "wlbasic.htm");
                if (!checkLogin(wlbasicPage)) {
                    throw new RuntimeException("Login first");
                }
                // set ssid 
                if (name != null) {
                    wlbasicPage.getElementByName("ssid").setAttribute("value", name);
                }
                // set wifi status
                if (status != null) {
                    if (status == WifiStatus.visible || status == WifiStatus.hidden) {
                        if (wlbasicPage.getElementByName("wlanDisabled").getAttribute("value").equals("ON")) {
                            wlbasicPage.getElementByName("wlanDisabled").click();
                        }
                    } else if (wlbasicPage.getElementByName("wlanDisabled").getAttribute("value").equals("OFF")) {
                        wlbasicPage.getElementByName("wlanDisabled").click();
                    }
                }
                // save & apply
                wlbasicPage.getElementByName("save").click();
                //TODO: parse result of click
            }
            if (pass != null) {
                HtmlPage wlwpaPage = webClient.getPage(baseUrl + "wlwpa.htm");
                if (wlwpaPage.getElementByName("method").asText().equals("None")
                        || wlwpaPage.getElementByName("method").asText().equals("WEP")) {
                    // Note: does not support these methodes yet
                } else {
                    wlwpaPage.getElementByName("pskValue").setAttribute("value", pass);
                    wlwpaPage.getElementByName("save").click();
                    //TODO: parse result of click
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public InternetConfig getInternetSetting() {
        String pppUsername;
        String pppPassword;
        int vci;
        int vpi;
        try {
            HtmlPage wanPage = webClient.getPage(baseUrl + "wan.htm");
            if (!checkLogin(wanPage)) {
                throw new RuntimeException("Login first");
            }
            // select fisrt setting
            wanPage.getElementByName("select").click();
            // get username
            pppUsername = wanPage.getElementByName("pppUserName").asText();
            // get password 
            pppPassword = wanPage.getElementByName("pppPassword").asText();
            // get VPI & VCI
            vpi = Integer.parseInt(wanPage.getElementByName("vpi").asText());
            vci = Integer.parseInt(wanPage.getElementByName("vci").asText());

            // return 
            return new InternetConfig(pppUsername, pppPassword, vci, vpi);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean changeInternetSetting(String username, String password) {
        try {
            HtmlPage wanPage = webClient.getPage(baseUrl + "wan.htm");
            if (!checkLogin(wanPage)) {
                throw new RuntimeException("Login first");
            }
            // select fisrt setting
            wanPage.getElementByName("select").click();
            // set username
            if (username != null) {
                wanPage.getElementByName("pppUserName").setAttribute("value", username);
            }
            // set password
            if (password != null) {
                wanPage.getElementByName("pppPassword").setAttribute("value", password);
            }
            // save & apply
            wanPage.getElementByName("modify").click();
            // TODO: parse result of click
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public ArrayList<ConnectedDevice> getDeviceList() {
        /*
            This modem doesnt show ip address and device name :(
         */
        try {
            ArrayList<ConnectedDevice> connectedDevices = new ArrayList<ConnectedDevice>();
            HtmlPage wlstatblPage = webClient.getPage(baseUrl + "wlstatbl.htm");
            if (!checkLogin(wlstatblPage)) {
                throw new RuntimeException("Login first");
            }
            HtmlTable table = (HtmlTable) wlstatblPage.getElementsByTagName("table").get(0);
            for (int rawIdx = 2; rawIdx < table.getRowCount(); rawIdx++) {
                HtmlElement macElement = table.getRow(rawIdx).getCell(0);
                connectedDevices.add(new ConnectedDevice("No Name", "No IP", macElement.asText()));
            }
            return connectedDevices;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean checkLogin(HtmlPage page) {
        if (page.getElementById("loginBtn") == null) {
            return true;
        }
        return false;
    }
}
