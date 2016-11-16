package com.slightech.testwifipwd;

import android.content.Context;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private WifiManager wifiManage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wifiManage = new WifiManager();
        android.net.wifi.WifiManager manager = (android.net.wifi.WifiManager) getSystemService(Context.WIFI_SERVICE);
        android.net.wifi.WifiInfo connectionInfo = manager.getConnectionInfo();
        String  ssid = connectionInfo.getSSID();
        int deviceVersion = Build.VERSION.SDK_INT;
        //API17之后 SSID带引号，去掉引号
        if (deviceVersion >= 17) {
            ssid = ssid.substring(1,ssid.length()-1);
        }
        try {
            Init(ssid);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public void Init(String ssid) throws Exception {
        List<WifiInfo> wifiInfos = wifiManage.Read(getPackageCodePath(),ssid);
        ListView wifiInfosView=(ListView)findViewById(R.id.WifiInfosView);
        WifiAdapter ad = new WifiAdapter(wifiInfos,MainActivity.this);
        wifiInfosView.setAdapter(ad);
    }

    public class WifiAdapter extends BaseAdapter {

        List<WifiInfo> wifiInfos =null;
        Context con;

        public WifiAdapter(List<WifiInfo> wifiInfos,Context con){
            this.wifiInfos =wifiInfos;
            this.con = con;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return wifiInfos.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return wifiInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            convertView = LayoutInflater.from(con).inflate(android.R.layout.simple_list_item_1, null);
            TextView tv = (TextView)convertView.findViewById(android.R.id.text1);
            tv.setText("Wifi:"+wifiInfos.get(position).Ssid+"\n密码:"+wifiInfos.get(position).Password+"\n加密方式："+wifiInfos.get(position).security);
            return convertView;
        }

    }
}
