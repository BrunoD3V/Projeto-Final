package edu.projeto_final.multilateration;

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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends Activity {
    TextView mainText;
    WifiManager mainWifi;
    WifiReceiver receiverWifi;
    List<ScanResult> wifiList;

    FirebaseDatabase database;
    private DatabaseReference databaseRef;

    private int newID = 0;

    EditText edtPos;
    EditText edtX;
    EditText edtY;
    EditText edtD1;
    EditText edtD2;
    EditText edtD3;
    EditText edtD4;

    Button btnClear;
    Button btnUpdate;
    Button btnStoreDB;

    List<Integer> powersBssid1 = new ArrayList<>();
    List<Integer> powersBssid2 = new ArrayList<>();
    List<Integer> powersBssid3 = new ArrayList<>();
    List<Integer> powersBssid4 = new ArrayList<>();

    private List<Double> errosDist = new ArrayList<>();
    private List<Double> errosY = new ArrayList<>();
    private List<Double> errosX = new ArrayList<>();
    private List<Double> errosYCalc = new ArrayList<>();
    private List<Double> errosXCalc = new ArrayList<>();

    double maxDist;
    double minDist;

    private List<Double> mediasBssid1 = new ArrayList<>();
    private List<Double> mediasBssid2 = new ArrayList<>();
    private List<Double> mediasBssid3 = new ArrayList<>();
    private List<Double> mediasBssid4 = new ArrayList<>();
    private List<Double> mediasExpurgadasBssid1 = new ArrayList<>();
    private List<Double> mediasExpurgadasBssid2 = new ArrayList<>();
    private List<Double> mediasExpurgadasBssid3 = new ArrayList<>();
    private List<Double> mediasExpurgadasBssid4 = new ArrayList<>();

    private double mediaErroDist;
    private double mediaErroX;
    private double mediaErroY;
    private double mediaXCalc;
    private double mediaYCalc;
    private double mediaD1Calc;
    private double mediaD2Calc;
    private double mediaD3Calc;
    private double mediaD4Calc;
    private double mediaErroD1;
    private double mediaErroD2;
    private double mediaErroD3;
    private double mediaErroD4;

    private List<Double> listErroD1 = new ArrayList<>();
    private List<Double> listErroD2 = new ArrayList<>();
    private List<Double> listErroD3 = new ArrayList<>();
    private List<Double> listErroD4 = new ArrayList<>();
    private List<Double> listD1Calc = new ArrayList<>();
    private List<Double> listD2Calc = new ArrayList<>();
    private List<Double> listD3Calc = new ArrayList<>();
    private List<Double> listD4Calc = new ArrayList<>();

    private List<Double> desvioPBssid1 = new ArrayList<>();
    private List<Double> desvioPBssid2 = new ArrayList<>();
    private List<Double> desvioPBssid3 = new ArrayList<>();
    private List<Double> desvioPBssid4 = new ArrayList<>();
    private double desvioPErroDist;
    private double desvioPErroX;
    private double desvioPErroY;
    private double desvioPErroD1;
    private double desvioPErroD2;
    private double desvioPErroD3;
    private double desvioPErroD4;
    private double desvioPErroXCalc;
    private double desvioPErroYCalc;
    private double desvioPErroD1Calc;
    private double desvioPErroD2Calc;
    private double desvioPErroD3Calc;
    private double desvioPErroD4Calc;


    private int count = 0;

    private IntentFilter filter = new IntentFilter();

    private double d1;
    private double d2;
    private double d3;
    private double d4;

    private double rX;
    private double rY;
    private double rD1;
    private double rD2;
    private double rD3;
    private double rD4;

    private double eX;
    private double eY;
    private double eD1;
    private double eD2;
    private double eD3;
    private double eD4;
    private double eDist;

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

    private double x;
    private double y;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainText = findViewById(R.id.mainText);
        mainWifi = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference();

        edtPos = findViewById(R.id.edtPos);
        edtX = findViewById(R.id.edtX);
        edtY = findViewById(R.id.edtY);
        edtD1 = findViewById(R.id.edtD1);
        edtD2 = findViewById(R.id.edtD2);
        edtD3 = findViewById(R.id.edtD3);
        edtD4 = findViewById(R.id.edtD4);

        btnClear = findViewById(R.id.btnClear);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtPos.setText("");
                edtX.setText("");
                edtY.setText("");
                edtD1.setText("");
                edtD2.setText("");
                edtD3.setText("");
                edtD4.setText("");
            }
        });

        btnStoreDB = findViewById(R.id.btnStoreDB);
        btnStoreDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String posID = "pos" + edtPos.getText().toString() + "_" + generateID();

                databaseRef.child("medicoes").child(posID).child("x").setValue(x);
                databaseRef.child("medicoes").child(posID).child("y").setValue(y);
                databaseRef.child("medicoes").child(posID).child("d1").setValue(d1);
                databaseRef.child("medicoes").child(posID).child("d2").setValue(d2);
                databaseRef.child("medicoes").child(posID).child("d3").setValue(d3);
                databaseRef.child("medicoes").child(posID).child("d4").setValue(d4);
                databaseRef.child("medicoes").child(posID).child("eX").setValue(eX);
                databaseRef.child("medicoes").child(posID).child("eY").setValue(eY);
                databaseRef.child("medicoes").child(posID).child("eD1").setValue(eD1);
                databaseRef.child("medicoes").child(posID).child("eD2").setValue(eD2);
                databaseRef.child("medicoes").child(posID).child("eD3").setValue(eD3);
                databaseRef.child("medicoes").child(posID).child("eD4").setValue(eD4);
                databaseRef.child("medicoes").child(posID).child("rX").setValue(rX);
                databaseRef.child("medicoes").child(posID).child("rY").setValue(rY);
                databaseRef.child("medicoes").child(posID).child("rD1").setValue(rD1);
                databaseRef.child("medicoes").child(posID).child("rD2").setValue(rD2);
                databaseRef.child("medicoes").child(posID).child("rD3").setValue(rD3);
                databaseRef.child("medicoes").child(posID).child("rD4").setValue(rD4);
                databaseRef.child("medicoes").child(posID).child("eDist").setValue(eDist);
                databaseRef.child("medicoes").child(posID).child("media_e_dPadrao").child("mediaD1Calc").setValue(mediaD1Calc);
                databaseRef.child("medicoes").child(posID).child("media_e_dPadrao").child("desvioPErroD1Calc").setValue(desvioPErroD1Calc);
                databaseRef.child("medicoes").child(posID).child("media_e_dPadrao").child("mediaD2Calc").setValue(mediaD2Calc);
                databaseRef.child("medicoes").child(posID).child("media_e_dPadrao").child("desvioPErroD2Calc").setValue(desvioPErroD2Calc);
                databaseRef.child("medicoes").child(posID).child("media_e_dPadrao").child("mediaD3Calc").setValue(mediaD3Calc);
                databaseRef.child("medicoes").child(posID).child("media_e_dPadrao").child("desvioPErroD3Calc").setValue(desvioPErroD3Calc);
                databaseRef.child("medicoes").child(posID).child("media_e_dPadrao").child("mediaD4Calc").setValue(mediaD4Calc);
                databaseRef.child("medicoes").child(posID).child("media_e_dPadrao").child("desvioPErroD4Calc").setValue(desvioPErroD4Calc);
                databaseRef.child("medicoes").child(posID).child("media_e_dPadrao").child("mediaErroD1").setValue(mediaErroD1);
                databaseRef.child("medicoes").child(posID).child("media_e_dPadrao").child("desvioPErroD1").setValue(desvioPErroD1);
                databaseRef.child("medicoes").child(posID).child("media_e_dPadrao").child("mediaErroD2").setValue(mediaErroD2);
                databaseRef.child("medicoes").child(posID).child("media_e_dPadrao").child("desvioPErroD2").setValue(desvioPErroD2);
                databaseRef.child("medicoes").child(posID).child("media_e_dPadrao").child("mediaErroD3").setValue(mediaErroD3);
                databaseRef.child("medicoes").child(posID).child("media_e_dPadrao").child("desvioPErroD3").setValue(desvioPErroD3);
                databaseRef.child("medicoes").child(posID).child("media_e_dPadrao").child("mediaErroD4").setValue(mediaErroD4);
                databaseRef.child("medicoes").child(posID).child("media_e_dPadrao").child("desvioPErroD4").setValue(desvioPErroD4);
                databaseRef.child("medicoes").child(posID).child("media_e_dPadrao").child("mediaErroDist").setValue(mediaErroDist);
                databaseRef.child("medicoes").child(posID).child("media_e_dPadrao").child("desvioPErroDist").setValue(desvioPErroDist);
                databaseRef.child("medicoes").child(posID).child("media_e_dPadrao").child("mediaErroX").setValue(mediaErroX);
                databaseRef.child("medicoes").child(posID).child("media_e_dPadrao").child("desvioPErroX").setValue(desvioPErroX);
                databaseRef.child("medicoes").child(posID).child("media_e_dPadrao").child("mediaErroY").setValue(mediaErroY);
                databaseRef.child("medicoes").child(posID).child("media_e_dPadrao").child("desvioPErroY").setValue(desvioPErroY);
                databaseRef.child("medicoes").child(posID).child("media_e_dPadrao").child("mediaXCalc").setValue(mediaXCalc);
                databaseRef.child("medicoes").child(posID).child("media_e_dPadrao").child("desvioPErroXCalc").setValue(desvioPErroXCalc);
                databaseRef.child("medicoes").child(posID).child("media_e_dPadrao").child("mediaYCalc").setValue(mediaYCalc);
                databaseRef.child("medicoes").child(posID).child("media_e_dPadrao").child("desvioPErroYCalc").setValue(desvioPErroYCalc);
                databaseRef.child("medicoes").child(posID).child("maxDist").setValue(maxDist);
                databaseRef.child("medicoes").child(posID).child("minDist").setValue(minDist);
            }
        });

        btnUpdate = findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!isEmpty(edtX) && !isEmpty(edtY) && !isEmpty(edtD1) && !isEmpty(edtD2) && !isEmpty(edtD3) && !isEmpty(edtD4)){
                    count = 0;
                    powersBssid1.clear();
                    powersBssid2.clear();
                    powersBssid3.clear();
                    powersBssid4.clear();

                    rX = Double.valueOf(edtX.getText().toString());
                    rY = Double.valueOf(edtY.getText().toString());
                    rD1 = Double.valueOf(edtD1.getText().toString());
                    rD2 = Double.valueOf(edtD2.getText().toString());
                    rD3 = Double.valueOf(edtD3.getText().toString());
                    rD4 = Double.valueOf(edtD4.getText().toString());
                }
            }
        });

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

            if(count <= 1000)
                count ++;

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
                    powersBssid1.add(rssi);

                if(bsssid.equalsIgnoreCase(bssid2))
                    powersBssid2.add(rssi);

                if(bsssid.equalsIgnoreCase(bssid3))
                    powersBssid3.add(rssi);

                if(bsssid.equalsIgnoreCase(bssid4))
                    powersBssid4.add(rssi);

                if(count % 100 == 0 || count == 1000){
                    double mediaBssid1 = meanCalc(powersBssid1);
                    double desvioPadraoBssid1 = getStdDev(powersBssid1, mediaBssid1);
                    List<Integer> expurgedPowersBssid1 = expurgePowers(powersBssid1, mediaBssid1, desvioPadraoBssid1);
                    double mediaExpurgadaBssid1 = meanCalc(expurgedPowersBssid1);

                    double mediaBssid2 = meanCalc(powersBssid2);
                    double desvioPadraoBssid2 = getStdDev(powersBssid2, mediaBssid2);
                    List<Integer> expurgedPowersBssid2 = expurgePowers(powersBssid2, mediaBssid2, desvioPadraoBssid2);
                    double mediaExpurgadaBssid2 = meanCalc(expurgedPowersBssid2);

                    double mediaBssid3 = meanCalc(powersBssid3);
                    double desvioPadraoBssid3 = getStdDev(powersBssid3, mediaBssid3);
                    List<Integer> expurgedPowersBssid3 = expurgePowers(powersBssid3, mediaBssid3, desvioPadraoBssid3);
                    double mediaExpurgadaBssid3 = meanCalc(expurgedPowersBssid3);

                    double mediaBssid4 = meanCalc(powersBssid4);
                    double desvioPadraoBssid4 = getStdDev(powersBssid4, mediaBssid4);
                    List<Integer> expurgedPowersBssid4 = expurgePowers(powersBssid4, mediaBssid4, desvioPadraoBssid4);
                    double mediaExpurgadaBssid4 = meanCalc(expurgedPowersBssid4);

                    d1 = getDistance(mediaExpurgadaBssid1);
                    eD1 = getError(rD1,d1);
                    d2 = getDistance(mediaExpurgadaBssid2);
                    eD2 = getError(rD2,d2);
                    d3 = getDistance(mediaExpurgadaBssid3);
                    eD3 = getError(rD3,d3);
                    d4 = getDistance(mediaExpurgadaBssid4);
                    eD4 = getError(rD4,d4);

                    mediasBssid1.add(mediaBssid1);
                    mediasBssid2.add(mediaBssid2);
                    mediasBssid3.add(mediaBssid3);
                    mediasBssid4.add(mediaBssid4);
                    mediasExpurgadasBssid1.add(mediaExpurgadaBssid1);
                    mediasExpurgadasBssid2.add(mediaExpurgadaBssid2);
                    mediasExpurgadasBssid3.add(mediaExpurgadaBssid3);
                    mediasExpurgadasBssid4.add(mediaExpurgadaBssid4);

                    listD1Calc.add(d1);
                    listD2Calc.add(d2);
                    listD3Calc.add(d3);
                    listD4Calc.add(d4);
                    listErroD1.add(eD1);
                    listErroD2.add(eD2);
                    listErroD3.add(eD3);
                    listErroD4.add(eD4);

                    desvioPBssid1.add(desvioPadraoBssid1);
                    desvioPBssid2.add(desvioPadraoBssid2);
                    desvioPBssid3.add(desvioPadraoBssid3);
                    desvioPBssid4.add(desvioPadraoBssid4);
                }
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

            x = A.getEntry(0,0);
            eX = getError(rX,x);
            y = A.getEntry(1,0);
            eY = getError(rY, y);
            eDist = getDistError(x,rX,y,rY);

            errosDist.add(eDist);
            errosX.add(eX);
            errosY.add(eY);
            errosXCalc.add(x);
            errosYCalc.add(y);

            if(listD1Calc.size() == 10 && listD2Calc.size() == 10 && listD3Calc.size() == 10 && listD4Calc.size() == 10 &&
                listErroD1.size() == 10 && listErroD2.size() == 10 && listErroD3.size() == 10 && listErroD4.size() == 10 &&
                    errosDist.size() == 10 && errosX.size() == 10 && errosY.size() == 10 && errosXCalc.size() == 10 &&
                        errosYCalc.size() == 10){
                            mediaD1Calc = meanCalcDouble(listD1Calc);
                            desvioPErroD1Calc = getStdDevDouble(listD1Calc,mediaD1Calc);
                            mediaD2Calc = meanCalcDouble(listD2Calc);
                            desvioPErroD2Calc = getStdDevDouble(listD2Calc,mediaD2Calc);
                            mediaD3Calc = meanCalcDouble(listD3Calc);
                            desvioPErroD3Calc = getStdDevDouble(listD3Calc,mediaD3Calc);
                            mediaD4Calc = meanCalcDouble(listD4Calc);
                            desvioPErroD4Calc = getStdDevDouble(listD4Calc,mediaD4Calc);
                            mediaErroD1 = meanCalcDouble(listErroD1);
                            desvioPErroD1 = getStdDevDouble(listErroD1,mediaErroD1);
                            mediaErroD2 = meanCalcDouble(listErroD2);
                            desvioPErroD2 = getStdDevDouble(listErroD2,mediaErroD2);
                            mediaErroD3 = meanCalcDouble(listErroD3);
                            desvioPErroD3 = getStdDevDouble(listErroD3,mediaErroD3);
                            mediaErroD4 = meanCalcDouble(listErroD4);
                            desvioPErroD4 = getStdDevDouble(listErroD4,mediaErroD4);
                            mediaErroDist = meanCalcDouble(errosDist);
                            desvioPErroDist = getStdDevDouble(errosDist,mediaErroDist);
                            mediaErroX = meanCalcDouble(errosX);
                            desvioPErroX = getStdDevDouble(errosX,mediaErroX);
                            mediaErroY = meanCalcDouble(errosY);
                            desvioPErroY = getStdDevDouble(errosY,mediaErroY);
                            mediaXCalc = meanCalcDouble(errosXCalc);
                            desvioPErroXCalc = getStdDevDouble(errosXCalc,mediaXCalc);
                            mediaYCalc = meanCalcDouble(errosYCalc);
                            desvioPErroYCalc = getStdDevDouble(errosYCalc,mediaYCalc);
                            //Max e Min
                            maxDist = maxDist(errosDist);
                            minDist = minDist(errosDist);
            }

            String posicao = "meanX: " + mediaXCalc + "\nmeanY: " + mediaYCalc;
            String dist = "\nMaxDist: " + maxDist + "MinDist: " + minDist;
            String errors = "\neX: " + eX + " eY: " + eY + "\neD1: " + eD1 + "\neD2: " + eD2 + "\neD3: " + eD3 + "\neD4: " + eD4 + "\n";
            String distError = "\nMean eDist: " + mediaErroDist;

            sb.append(errors);
            sb.append(posicao);
            sb.append(dist);
            sb.append(distError);
            sb.append("\nCount= " + count);
            sb.append("\nSize " + listD1Calc.size());

            mainText.setText(sb);
        }
    }

    private double maxDist(List<Double> errosDist){
        double result = Collections.max(errosDist);
        return result;
    }

    private double minDist(List<Double> errosDist){
        double result = Collections.min(errosDist);
        return result;
    }

    private int generateID(){
        return ++newID;
    }

    private double getDistError(double eX, double rX, double eY, double rY){
        double x = eX-rX;
        double y = eY-rY;
        return Math.sqrt(Math.pow(x,2) + Math.pow(y,2));
    }

    private double getError(double realValue, double calcValue){

        return (calcValue-realValue)/realValue;
    }


    private double meanCalc(List<Integer> data){
        double aux = 0.0;
        for(Integer i: data){
            aux += i;
        }
        return aux/data.size();
    }

    private double getStdDev(List<Integer> rssis, double mean){
        double aux = 0;
        for(Integer i: rssis){
            aux += (i - mean) * (i - mean);
        }
        return Math.sqrt(aux/rssis.size());
    }

    private double meanCalcDouble(List<Double> data){
        double aux = 0.0;
        for(Double i: data){
            aux += i;
        }
        return aux/data.size();
    }

    private double getStdDevDouble(List<Double> data, double mean){
        double aux = 0;
        for(Double i: data){
            aux += (i - mean) * (i - mean);
        }
        return Math.sqrt(aux/data.size());
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

    private double getDistance(double RSSI){

        double exp = (c + RSSI) / (-10 * n);
        double dist = Math.pow(10, exp);
        return dist;
    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }
}

