/*
 * This is ModemYar project developed by MV_MRP.
 * Copyright (c) 2017. All rights reserved.
 */
package networksample.modems.tplink;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import java.util.ArrayList;
import java.util.List;
import networksample.modems.Modem;
import networksample.modems.WifiStatus;

/**
 *
 * @author mv
 */
public class TpLink extends Modem {

    final String baseUrl = "http://192.168.1.1/";
    final WebClient webClient = new WebClient(BrowserVersion.CHROME);

    @Override
    public boolean login(String user, String pass) {
        try {
            HtmlPage loginPage = webClient.getPage(baseUrl);
            HtmlForm form = loginPage.getFormByName("Login_Form");

            HtmlInput username = form.getInputByName("Login_Name");
            username.setValueAttribute(user);

            HtmlInput password = form.getInputByName("Login_Pwd");
            password.setValueAttribute(pass);

            HtmlPage indexPage
                    = (HtmlPage) form.getInputByValue("Login").click();

            return checkLogin(indexPage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean changeLogin(@SuppressWarnings("unused") String oldPass, String newUser, String newPass) {

        try {
            HtmlPage rpsysPage = webClient.getPage(baseUrl + "rpSys.html");
            if (!checkLogin(rpsysPage)) {
                throw new RuntimeException("Login first");
            }
            // go to administration page
            HtmlPage navPage = (HtmlPage) rpsysPage.getFrameByName("navigation").getEnclosedPage();
            navPage = navPage.getAnchorByHref("navigation-maintenance.html").click();
            navPage.getAnchorByHref("../maintenance/tools_admin.htm").click();

            // get the page
            HtmlPage mainPage = (HtmlPage) rpsysPage.getFrameByName("main").getEnclosedPage();

            // set new values
            mainPage.getElementByName("uiViewTools_Password").setAttribute("value", newPass);
            mainPage.getElementByName("uiViewTools_PasswordConfirm").setAttribute("value", newPass);

            // press the save button
            mainPage.getElementByName("SaveBtn").click();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public ArrayList<ConnectedDevice> getDeviceList() {
        ArrayList<ConnectedDevice> connectedDevices = new ArrayList<>();
        try {
            HtmlPage rpsysPage = webClient.getPage(baseUrl + "rpSys.html");
            if (!checkLogin(rpsysPage)) {
                throw new RuntimeException("Login first");
            }
            HtmlPage navPage = (HtmlPage) rpsysPage.getFrameByName("navigation").getEnclosedPage();

            navPage = navPage.getAnchorByHref("navigation-basic.html").click();
            navPage.getAnchorByHref("../basic/home_lan.htm").click();
            HtmlPage mainPage = (HtmlPage) rpsysPage.getFrameByName("main").getEnclosedPage();

            // Is there another (and better) way to get dhcp table?
            HtmlTable dhcpTable = (HtmlTable) mainPage.getFirstByXPath("/html/body/form/div/table[4]/tbody/tr[2]/td[3]/table");

            for (int i = 2; i < dhcpTable.getRowCount(); i++) {
                connectedDevices.add(new ConnectedDevice(
                        dhcpTable.getRow(i).getCell(0).asText(),
                        dhcpTable.getRow(i).getCell(1).asText(),
                        dhcpTable.getRow(i).getCell(2).asText()));
            }

            return connectedDevices;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean changeWifiConfig(String name, String pass, WifiStatus status) {
        try {
            HtmlPage rpsysPage = webClient.getPage(baseUrl + "rpSys.html");
            if (!checkLogin(rpsysPage)) {
                throw new RuntimeException("Login first");
            }

            // go to wlan page
            HtmlPage navPage = (HtmlPage) rpsysPage.getFrameByName("navigation").getEnclosedPage();
            navPage = navPage.getAnchorByHref("navigation-basic.html").click();
            navPage.getAnchorByHref("../basic/home_wlan.htm").click();

            // get the page
            HtmlPage mainPage = (HtmlPage) rpsysPage.getFrameByName("main").getEnclosedPage();

            // set ssid name
            if (name != null) {
                mainPage.getElementByName("ESSID").setAttribute("value", name);
            }

            // set password
            if (pass != null) {
                mainPage.getElementByName("PreSharedKey").setAttribute("value", pass);
            }

            // set wifi visible or hidden
            if (status == WifiStatus.visible || status == WifiStatus.hidden) {
                String visibleValue;
                if (status == WifiStatus.visible) {
                    visibleValue = "0";
                } else {
                    System.out.println("setting wifi to hidden");
                    visibleValue = "1";
                }
                List<DomElement> hideElements = mainPage.getElementsByName("ESSID_HIDE_Selection");
                for (DomElement hideElement : hideElements) {
                    if (hideElement.getAttribute("value").equals(visibleValue)) {
                        System.out.println(hideElement);
                        hideElement.click();
                        break;
                    }
                }
            }

            // apply changes
            mainPage = mainPage.getElementByName("SaveBtn").click();

            /*
            set wifi active or deactive: this setting does not need to press save button
             */
            if (status == WifiStatus.visible || status == WifiStatus.deactive) {
                String activeValue;     // 0: deactive, 1: active
                if (status == WifiStatus.deactive) {
                    activeValue = "0";
                } else {
                    activeValue = "1";
                }
                List<DomElement> apEnableElements = mainPage.getElementsByName("wlan_APenable");
                for (DomElement apEnableElement : apEnableElements) {
                    if (apEnableElement.getAttribute("value").equals(activeValue)) {
                        apEnableElement.click();
                        break;
                    }
                }
            }

            // TODO: return according to result of save button
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public WifiConfig getWifiConfig() {
        String ssid_name;
        String password;
        WifiStatus wifiStatus = null;

        try {
            HtmlPage rpsysPage = webClient.getPage(baseUrl + "rpSys.html");
            if (!checkLogin(rpsysPage)) {
                throw new RuntimeException("Login first");
            }
            HtmlPage navPage = (HtmlPage) rpsysPage.getFrameByName("navigation").getEnclosedPage();
            navPage = navPage.getAnchorByHref("navigation-basic.html").click();
            navPage.getAnchorByHref("../basic/home_wlan.htm").click();

            HtmlPage mainPage = (HtmlPage) rpsysPage.getFrameByName("main").getEnclosedPage();
            ssid_name = mainPage.getElementByName("ESSID").getAttribute("value");
            password = mainPage.getElementByName("PreSharedKey").getAttribute("value");

            List<DomElement> apElements = mainPage.getElementsByName("wlan_APenable");
            for (DomElement apElement : apElements) {
                if (apElement.hasAttribute("CHECKED")) {
                    if (apElement.getAttribute("value").equals("0")) {
                        wifiStatus = WifiStatus.deactive;
                    } else {
                        wifiStatus = WifiStatus.visible;
                    }
                    break;
                }
            }
            if (wifiStatus == WifiStatus.visible) {
                List<DomElement> hideElements = mainPage.getElementsByName("ESSID_HIDE_Selection");
                for (DomElement hideElement : hideElements) {
                    if (hideElement.hasAttribute("CHECKED")) {
                        if (hideElement.getAttribute("value").equals("1")) {
                            wifiStatus = WifiStatus.hidden;
                        } else {
                            // dont change status
                        }
                        break;
                    }
                }
            }

            return new WifiConfig(ssid_name, password, wifiStatus);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean changeInternetSetting(String username, String password) {
        try {
            HtmlPage rpsysPage = webClient.getPage(baseUrl + "rpSys.html");
            if (!checkLogin(rpsysPage)) {
                throw new RuntimeException("Login first");
            }
            HtmlPage navPage = (HtmlPage) rpsysPage.getFrameByName("navigation").getEnclosedPage();
            navPage = navPage.getAnchorByHref("navigation-basic.html").click();
            navPage.getAnchorByHref("../basic/home_wan.htm").click();

            HtmlPage mainPage = (HtmlPage) rpsysPage.getFrameByName("main").getEnclosedPage();

            /*
            Hard Coded Options:
                PVC --> PVC2
                VPI --> 0
                VCI --> 35
                Use PPPoE
             */
            // select PVC0
            HtmlSelect wanvcSelect = (HtmlSelect) mainPage.getElementByName("wan_VC");
            HtmlOption desiredOption = wanvcSelect.getOptionByValue("PVC2");
            wanvcSelect.setSelectedAttribute(desiredOption, true);
            // update mainPage: is required after setSelectedAttribute
            mainPage = (HtmlPage) rpsysPage.getFrameByName("main").getEnclosedPage();

            // Choose PPPoE as WAN Type
            List<DomElement> EncapElements = mainPage.getElementsByName("wanTypeRadio");
            for (DomElement EncapElement : EncapElements) {
                if (EncapElement.getAttribute("value").equals("Two")) { // two is PPPoE
                    mainPage = EncapElement.click();
                    break;
                }
            }
            // set VPI and VCI
            mainPage.getElementByName("Alwan_VPI").setAttribute("value", "0");
            mainPage.getElementByName("Alwan_VCI").setAttribute("value", "35");

            // set username and password
            mainPage.getElementByName("wan_PPPUsername").setAttribute("value", username);
            mainPage.getElementByName("wan_PPPPassword").setAttribute("value", password);

            // submit changes
            mainPage.getElementByName("SaveBtn").click();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public InternetConfig getInternetSetting() {
        String username;
        String password;
        int vpi;
        int vci;
        try {
            HtmlPage rpsysPage = webClient.getPage(baseUrl + "rpSys.html");
            if (!checkLogin(rpsysPage)) {
                throw new RuntimeException("Login first");
            }
            HtmlPage navPage = (HtmlPage) rpsysPage.getFrameByName("navigation").getEnclosedPage();
            navPage = navPage.getAnchorByHref("navigation-basic.html").click();
            navPage.getAnchorByHref("../basic/home_wan.htm").click();

            HtmlPage mainPage = (HtmlPage) rpsysPage.getFrameByName("main").getEnclosedPage();

            // TODO: Make correct decision for default PVC
            HtmlSelect wanvcSelect = (HtmlSelect) mainPage.getElementByName("wan_VC");
            HtmlOption desiredOption = wanvcSelect.getOptionByValue("PVC2");
            wanvcSelect.setSelectedAttribute(desiredOption, true);

            // update mainPage: is required after setSelectedAttribute
            mainPage = (HtmlPage) rpsysPage.getFrameByName("main").getEnclosedPage();

            vpi = Integer.parseInt(mainPage.getElementByName("Alwan_VPI").getAttribute("value"));
            vci = Integer.parseInt(mainPage.getElementByName("Alwan_VCI").getAttribute("value"));
            username = mainPage.getElementByName("wan_PPPUsername").getAttribute("value");
            password = mainPage.getElementByName("wan_PPPPassword").getAttribute("value");

            return new InternetConfig(username, password, vci, vpi);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean checkLogin(HtmlPage page) {
        try {
            HtmlForm form = page.getFormByName("Login_Form");
        } catch (ElementNotFoundException e) {
            return true;
        }
        return false;
    }

}
