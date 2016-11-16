package com.slightech.testwifipwd;

/**
 * Created by Rokey on 2016/11/15.
 */


import android.util.Log;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WifiManager {

    public List<WifiInfo> Read(String packageCodePath,String wifiSsid) throws Exception {
        List<WifiInfo> wifiInfos = new ArrayList<WifiInfo>();
        Process process = null;
        String cmd = "chmod 777 " + packageCodePath;
        DataOutputStream dataOutputStream = null;
        DataInputStream dataInputStream = null;
        StringBuffer wifiConf = new StringBuffer();
        try {
            process = Runtime.getRuntime().exec("su");
            dataOutputStream = new DataOutputStream(process.getOutputStream());
            dataInputStream = new DataInputStream(process.getInputStream());
            //如果已经有读取权限，就不需要执行这一步
            //dataOutputStream.writeBytes(cmd + "\n");
            dataOutputStream
                    .writeBytes("cat /data/misc/wifi/wpa_supplicant.conf\n");
            dataOutputStream.writeBytes("exit\n");

            dataOutputStream.flush();
            InputStreamReader inputStreamReader = new InputStreamReader(
                    dataInputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(
                    inputStreamReader);
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                wifiConf.append(line);
            }

            bufferedReader.close();
            inputStreamReader.close();
            process.waitFor();
        } catch (Exception e) {

            throw e;
        } finally {
            try {
                if (dataOutputStream != null) {
                    dataOutputStream.close();
                }
                if (dataInputStream != null) {
                    dataInputStream.close();
                }
                process.destroy();
            } catch (Exception e) {
//                throw e;
            }
        }

        Pattern network = Pattern.compile("network=\\{([^\\}]+)\\}", Pattern.DOTALL);
        Log.i("TAG", "wifiConf.length" + wifiConf.length()+"wifissid"+wifiSsid);
        Matcher networkMatcher = network.matcher(wifiConf.toString());
        while (networkMatcher.find()) {
            String networkBlock = networkMatcher.group();
            Pattern ssid = Pattern.compile("ssid=\"([[\\u4e00-\\u9fa5]+[^\"]]+)\"");
            Matcher ssidMatcher = ssid.matcher(networkBlock);

            if (ssidMatcher.find()) {
                WifiInfo wifiInfo = new WifiInfo();
                wifiInfo.Ssid = ssidMatcher.group(1);
                Pattern psk = Pattern.compile("psk=\"([^\"]+)\"");
                Matcher pskMatcher = psk.matcher(networkBlock);
                if (pskMatcher.find()) {
                    wifiInfo.Password = pskMatcher.group(1);
                } else {
                    wifiInfo.Password = "无密码";
                }
                Pattern key_mgmt = Pattern.compile("key_mgmt=([\\S]+)");
                Matcher key_mgnmtMatcher = key_mgmt.matcher(networkBlock);
                if (key_mgnmtMatcher.find()){
                    wifiInfo.security = key_mgnmtMatcher.group(1);
                }else {
                    wifiInfo.security = "NONE";
                }
//                if (wifiInfo.Ssid.equals(wifiSsid)){
                    wifiInfos.add(wifiInfo);
//                }
            }
        }

        return wifiInfos;
    }

}