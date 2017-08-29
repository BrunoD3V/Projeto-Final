package edu.projeto_final.multilateration;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    TextView mainText;
    WifiManager mainWifi;
    WifiReceiver receiverWifi;
    List<ScanResult> wifiList;

    private IntentFilter filter = new IntentFilter();

    private double d1;
    private double d2;
    private double d3;
    private double d4;

    StringBuilder sb = new StringBuilder();



    final static String bssid1 = "18:d6:c7:51:7b:38";
    final static String bssid2 = "18:d6:c7:51:7d:44";
    final static String bssid3 = "18:d6:c7:51:7f:12";
    final static String bssid4 = "18:d6:c7:51:69:64";

    final static double n = 3.025;
    final static double c = 28.493;

    final static double x1 = 0.5;
    final static double y1 = 14.37;

    final static double x2 = 2.89;
    final static double y2 = 17.2;

    final static double x3 = 6.13;
    final static double y3 = 10.12;

    final static double x4 = 2.81;
    final static double y4 = 3.46;







    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainText = (TextView) findViewById(R.id.mainText);
        mainWifi = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        receiverWifi = new WifiReceiver();
        registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        mainWifi.startScan();
        mainText.setText("\\nStarting Scan...\\n");
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "Refresh");
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        mainWifi.startScan();

        return super.onMenuItemSelected(featureId, item);
    }

    protected void onPause() {
        unregisterReceiver(receiverWifi);
        super.onPause();
    }

    protected void onResume() {
        registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        super.onResume();
    }

    class WifiReceiver extends BroadcastReceiver {
        public void onReceive(Context c, final Intent intent) {


            if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(intent.getAction())) {
                registerReceiver(receiverWifi, filter);
                mainWifi.startScan();
            }

            sb = new StringBuilder();
            wifiList = mainWifi.getScanResults();
            ScanResult result;

            String bsssid;
            int rssi;
            for (int i = 1; i < wifiList.size(); i++) {
                result = wifiList.get(i);

                bsssid = result.BSSID;

                rssi = result.level;

                if(bsssid.equalsIgnoreCase(bssid1))
                    d1 = getDistance(rssi);

                if(bsssid.equalsIgnoreCase(bssid2))
                    d2 = getDistance(rssi);

                if(bsssid.equalsIgnoreCase(bssid3))
                    d3 = getDistance(rssi);

                if(bsssid.equalsIgnoreCase(bssid4))
                    d4 = getDistance(rssi);
            }


            double[][] distDif = {{2 * x1 - 2 * x2, 2 * y1 - 2 * y2}, {2 * x1 - 2 * x3,
                    2 * y1 - 2 * y3}, {2 * x1 - 2 * x4, 2 * y1 - 2 * y4}};


            double[][] preB = {{d2 * d2 - d1 * d1 - (x2 * x2 + y2 * y2) + (x1 * x1 + y1 * y1)},
                    {d3 * d3 - d1 * d1 - (x3 * x3 + y3 * y3) + (x1 * x1 + y1 * y1)},
                    {d4 * d4 - d1 * d1 - (x4 * x4 + y4 * y4) + (x1 * x1 + y1 * y1)}};

            RealMatrix A = MatrixUtils.createRealMatrix(distDif);
            RealMatrix B = MatrixUtils.createRealMatrix(preB);

            RealMatrix T = A.transpose();

            A = T.multiply(A);

            A = new LUDecomposition(A).getSolver().getInverse();

            A = A.multiply(T);

            A = A.multiply(B);

            double x = A.getEntry(0,0);
            double y = A.getEntry(1,0);
            String posicao = "x : " + x + " y : " + y;
            String dist = "\nd1 :" + d1 + "\n d2 : " + d2 + " \n d3 : " + d3 + "\n d4 : " + d4;
            sb.append(posicao);
            sb.append(dist);
            mainText.setText(sb);






        }

    }

    private double getDistance(int RSSI){

        double exp = (c + RSSI) / (-10 * n);
        double dist = Math.pow(10, exp);
        return dist;
    }


}

