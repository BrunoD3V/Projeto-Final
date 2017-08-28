package edu.projeto_final.getenvcontants;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

public class MainActivity extends AppCompatActivity {

    EditText Bssid1Edit;
    EditText Bssid2Edit;
    EditText Bssid3Edit;
    EditText Bssid4Edit;

    EditText pBssid1Edit;
    EditText pBssid2Edit;
    EditText pBssid3Edit;
    EditText pBssid4Edit;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bssid1Edit = (EditText) findViewById(R.id.bssid1Edit);
        Bssid2Edit = (EditText) findViewById(R.id.bssid2Edit);
        Bssid3Edit = (EditText) findViewById(R.id.bssid3Edit);
        Bssid4Edit = (EditText) findViewById(R.id.bssid4Edit);


        pBssid1Edit = (EditText) findViewById(R.id.pBssid1Edit);
        pBssid2Edit = (EditText) findViewById(R.id.pBssid2Edit);
        pBssid3Edit = (EditText) findViewById(R.id.pBssid3Edit);
        pBssid4Edit = (EditText) findViewById(R.id.pBssid4Edit);

        Calc();
    }

    public void onClickCalcBtn(View v){

        if(isEmpty(Bssid1Edit) || isEmpty(Bssid2Edit) || isEmpty(Bssid3Edit) || isEmpty(Bssid4Edit)
                || isEmpty(pBssid1Edit) || isEmpty(pBssid2Edit) || isEmpty(pBssid3Edit) || isEmpty(pBssid4Edit) ){
            Toast.makeText(getApplicationContext(),"Tem que preencher todos os campos",Toast.LENGTH_LONG).show();
            return;
        }
        double dB1 = Double.valueOf(Bssid1Edit.getText().toString());
        double dB2 = Double.valueOf(Bssid2Edit.getText().toString());
        double dB3 = Double.valueOf(Bssid3Edit.getText().toString());
        double dB4 = Double.valueOf(Bssid4Edit.getText().toString());

        double pB1 = Double.valueOf(pBssid1Edit.getText().toString());
        double pB2 = Double.valueOf(pBssid2Edit.getText().toString());
        double pB3 = Double.valueOf(pBssid3Edit.getText().toString());
        double pB4 = Double.valueOf(pBssid4Edit.getText().toString());

        double [][] data = {{1,10*Math.log10(dB1)}, {1, 10*Math.log10(dB2)},
                {1, 10*Math.log10(dB3)}, {1, 10 * Math.log10(dB4)}};
        double [][] Pot = {{pB1}, {pB2}, {pB3}, {pB4}};
        //  double [][] inv = {{4,14}, {14,54}};
        RealMatrix M = MatrixUtils.createRealMatrix(data);
        RealMatrix RSSI = MatrixUtils.createRealMatrix(Pot);
        RealMatrix T = M.transpose();

        RealMatrix Res = T.multiply(M);

        // RealMatrix Inv = MatrixUtils.createRealMatrix(inv);

        RealMatrix Inv = new LUDecomposition(Res).getSolver().getInverse();
        //System.out.println(Inv.toString());
        RealMatrix Res2 = Inv.multiply(T);
        RealMatrix Rfinal = Res2.multiply(RSSI);
        //double exp = (Rfinal.getEntry(1)-RSSI.getEntry(0,0))/(10 * -1 * Rfinal.getEntry(1,1));
        //  double dist = Math.pow(10,exp);
        // double teste = 10 * Math.log10(7.84);
        String resultados = Rfinal.toString();
        for(int i = 0; i <=3; i++){
            double exp = (Rfinal.getEntry(0,0)- RSSI.getEntry(i,0))/(-10 *Rfinal.getEntry(1,0) );
            double dist = Math.pow(10, exp);
            double error = Math.abs(Math.pow(10, M.getEntry(i,1)/10) - dist)/Math.pow(10,M.getEntry(i,1)/10);
            System.out.println(Math.pow(10,M.getEntry(i,1)/10));
            resultados += "\n distancia a " + i + " " + dist + " erro = " + error;
        }

        Intent i = new Intent(this, ShowResults.class);
        i.putExtra("resultados", resultados);
        startActivity(i);

        // mainText.setText(Double.valueOf(teste).toString());
    }

    public void Calc(){


    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }
}
