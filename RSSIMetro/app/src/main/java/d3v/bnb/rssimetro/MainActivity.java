package d3v.bnb.rssimetro;


import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends Activity {
    TextView mainText;
    WifiManager mainWifi;
    WifiReceiver receiverWifi;
    List<ScanResult> wifiList;

    private IntentFilter filter = new IntentFilter();

    StringBuilder sb = new StringBuilder();

    List<Integer> powerBssid1 = new ArrayList<>();
    List<Integer> powerBssid2 = new ArrayList<>();
    List<Integer> powerBssid3 = new ArrayList<>();
    List<Integer> powerBssid4 = new ArrayList<>();

    final static String bssid1 = "18:d6:c7:51:7b:38";
    final static String bssid2 = "18:d6:c7:51:7d:44";
    final static String bssid3 = "18:d6:c7:51:7f:12";
    final static String bssid4 = "18:d6:c7:51:69:64";

    private double mediaBssid1, mediaBssid2, mediaBssid3, mediaBssid4;
    private double mediaExpurgadaBssid1, mediaExpurgadaBssid2, mediaExpurgadaBssid3,  mediaExpurgadaBssid4;

    private List<Integer> expurgedPowersBssid1 = new ArrayList<>();
    private List<Integer> expurgedPowersBssid2 = new ArrayList<>();
    private List<Integer> expurgedPowersBssid3 = new ArrayList<>();
    private List<Integer> expurgedPowersBssid4 = new ArrayList<>();

    private int count;


    /*
    List<Integer> powersEntrada = new ArrayList<>();
    List<Integer> powerCentro = new ArrayList<>();
    List<Integer> powerFundo = new ArrayList<>();
    List<Integer> powerCisco1 = new ArrayList<>();
    List<Integer> powerCisco2 = new ArrayList<>();
    List<Integer> powerCisco3 = new ArrayList<>();
    List<Integer> powerCisco4 = new ArrayList<>();

    final static String entrada = "88:d7:f6:81:63:48";
    final static String centro = "88:d7:f6:81:64:28";
    final static String fundo = "88:d7:f6:81:63:38";
    final static String cisco1 = "68:7f:74:aa:e8:fa";
    final static String cisco2 = "68:7f:74:aa:ea:00";
    final static String cisco3 = "c8:d7:19:e5:a1:e5";
    final static String cisco4 = "c8:d7:19:e5:a2:23";

    private double mediaExpurgadaEntrada, mediaExpurgadaCentro, mediaExpurgadaFundo,  mediaExpurgadaCisco1,mediaExpurgadaCisco2,mediaExpurgadaCisco3,mediaExpurgadaCisco4;
    private double mediaEntrada, mediaCentro, mediaFundo, mediaCisco1, mediaCisco2, mediaCisco3, mediaCisco4;

    private List<Integer> expurgedpowersEntrada = new ArrayList<>();
    private List<Integer> expurgedPowersCentro = new ArrayList<>();
    private List<Integer> expurgedPowersFundo = new ArrayList<>();
    private List<Integer> expurgedPowersCisco1 = new ArrayList<>();
    private List<Integer> expurgedPowersCisco2 = new ArrayList<>();
    private List<Integer> expurgedPowersCisco3 = new ArrayList<>();
    private List<Integer> expurgedPowersCisco4 = new ArrayList<>();
    */

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainText = (TextView) findViewById(R.id.mainText);
        mainWifi = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        count = 0;
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
        mainText.setText("Starting Scan");
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
            count++;
            // System.out.println("AQUI");
            if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(intent.getAction())){
                registerReceiver(receiverWifi, filter);
                mainWifi.startScan();
            }

            sb = new StringBuilder();
            wifiList = mainWifi.getScanResults();

            //System.out.println(wifiList.toString());
            ScanResult result;

            String ssid, bsssid;
            int rssi;
            for (int i = 1; i < wifiList.size(); i++) {
                result = wifiList.get(i);
                ssid = result.SSID;
                bsssid = result.BSSID;
                rssi = result.level;
                if(count < 51){
                    System.out.println(bsssid);

                    if(bsssid.equalsIgnoreCase(bssid1))
                        powerBssid1.add(rssi);
                    if(bsssid.equalsIgnoreCase(bssid2))
                        powerBssid2.add(rssi);
                    if(bsssid.equalsIgnoreCase(bssid3))
                        powerBssid3.add(rssi);
                    if(bsssid.equalsIgnoreCase(bssid4))
                        powerBssid4.add(rssi);


                    mediaBssid1 = meanRssi(powerBssid1);
                    double desvioPadraoBssid1 = getStdDev(powerBssid1, mediaBssid1);
                    expurgedPowersBssid1 = expurgePowers(powerBssid1, mediaBssid1, desvioPadraoBssid1);
                    mediaExpurgadaBssid1 = meanRssi(expurgedPowersBssid1);

                    mediaBssid2 = meanRssi(powerBssid2);
                    double desvioPadraoBssid2 = getStdDev(powerBssid2, mediaBssid2);
                    expurgedPowersBssid2 = expurgePowers(powerBssid2,mediaBssid2, desvioPadraoBssid2);
                    mediaExpurgadaBssid2 = meanRssi(expurgedPowersBssid2);

                    mediaBssid3 = meanRssi(powerBssid3);
                    double desvioPadraoBssid3 = getStdDev(powerBssid3, mediaBssid3);
                    expurgedPowersBssid3 = expurgePowers(powerBssid3,mediaBssid3, desvioPadraoBssid3);
                    mediaExpurgadaBssid3 = meanRssi(expurgedPowersBssid3);

                    mediaBssid4 = meanRssi(powerBssid4);
                    double desvioPadraoBssid4 = getStdDev(powerBssid4, mediaBssid4);
                    expurgedPowersBssid4 = expurgePowers(powerBssid4, mediaBssid4, desvioPadraoBssid4);
                    mediaExpurgadaBssid4 = meanRssi(expurgedPowersBssid4);

                    /*
                    mediaCisco2 = meanRssi(powerCisco2);
                    double desvioPadraoCisco2 = getStdDev(powerCisco2, mediaCisco2);
                    expurgedPowersCisco2 = expurgePowers(powerCisco2, mediaCisco2, desvioPadraoCisco2);
                    mediaExpurgadaCisco2 = meanRssi(expurgedPowersCisco2);

                    mediaCisco3 = meanRssi(powerCisco3);
                    double desvioPadraoCisco3 = getStdDev(powerCisco3, mediaCisco3);
                    expurgedPowersCisco3 = expurgePowers(powerCisco3, mediaCisco3, desvioPadraoCisco3);
                    mediaExpurgadaCisco3 = meanRssi(expurgedPowersCisco3);

                    mediaCisco4 = meanRssi(powerCisco4);
                    double desvioPadraoCisco4 = getStdDev(powerCisco4, mediaCisco4);
                    expurgedPowersCisco4 = expurgePowers(powerCisco4, mediaCisco4, desvioPadraoCisco4);
                    mediaExpurgadaCisco4 = meanRssi(expurgedPowersCisco4);
                       */
                }

                System.out.println("SSID: " + ssid);
                System.out.println("BSSID: " + bsssid);
                System.out.println("SIGNAL: " + rssi);

                //sb.append("\n SSID = " + ssid);
                // sb.append(" BSSID = " + bsssid);
                // sb.append(" RSSSI = " + rssi);

            }
            if(count == 100){
                powerBssid1.clear();
                expurgedPowersBssid1.clear();

                powerBssid2.clear();
                expurgedPowersBssid2.clear();

                powerBssid3.clear();
                expurgedPowersBssid3.clear();

                powerBssid4.clear();
                expurgedPowersBssid4.clear();

                count = 0;
            }

            //TODO: switch ordenado consoante posição a registar.
            sb.append("\n" + count);
            sb.append("\n BSSID 1:");
            sb.append("\n media = " + mediaBssid1);
            sb.append("\n mediaExpurgada = " + mediaExpurgadaBssid1);
            sb.append("\n BSSID 2:");
            sb.append("\n media = " + mediaBssid2);
            sb.append("\n mediaExpurgada = " + mediaExpurgadaBssid2);
            sb.append("\n BSSID 3:");
            sb.append("\n media = " + mediaBssid3);
            sb.append("\n mediaExpurgada = " + mediaExpurgadaBssid3);
            sb.append("\n BSSID 4:");
            sb.append("\n media = " + mediaBssid4);
            sb.append("\n mediaExpurgada = " + mediaExpurgadaBssid4);

            mainText.setText(sb);

        }
        //System.out.println(rssiLists.get(0).toString());
    }

    private double meanRssi(List<Integer> rssis){
        double aux = 0.0;
        for(Integer i: rssis){
            aux += i;
        }
        return (double) aux/rssis.size();
    }

    private double getStdDev(List<Integer> rssis, double mean){
        double aux = 0;
        for(Integer i: rssis){
            aux += (i - mean) * (i - mean);
        }
        return Math.sqrt(aux/rssis.size());
    }
    private List<Integer> expurgePowers(List<Integer> rssis, double mean, double stddev){
        List<Integer> expurgedPower = new ArrayList<>();
        for(Integer i: rssis){
            if(i >= mean - 2 * stddev){
                expurgedPower.add(i);
            }
        }
        return expurgedPower;
    }
    private double getDistance(double rsssip, double rsssi_cal){
        if(rsssip >= rsssi_cal)
            return Math.pow(10, rsssip / rsssi_cal);
        return 0.9 * Math.pow(7.71, rsssip / rsssi_cal) + 0.11;
    }
}
