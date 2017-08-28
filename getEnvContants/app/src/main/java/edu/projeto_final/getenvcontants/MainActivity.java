package edu.projeto_final.getenvcontants;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.util.DoubleArray;

public class MainActivity extends AppCompatActivity {

    EditText entradaEdit;
    EditText centroEdit;
    EditText fundoEdit;
    EditText cisco1Edit;
    EditText cisco2Edit;
    EditText cisco3Edit;
    EditText cisco4Edit;
    EditText pEntradaEdit;
    EditText pCentroEdit;
    EditText pFundoEdit;
    EditText pCisco1Edit;
    EditText pCisco2Edit;
    EditText pCisco3Edit;
    EditText pCisco4Edit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        entradaEdit = (EditText) findViewById(R.id.entradaEdit);
        centroEdit = (EditText) findViewById(R.id.centroEdit);
        fundoEdit = (EditText) findViewById(R.id.fundoEdit);
        cisco1Edit = (EditText) findViewById(R.id.cisco1Edit);
        cisco2Edit = (EditText) findViewById(R.id.cisco2Edit);
        cisco3Edit = (EditText) findViewById(R.id.cisco3Edit);
        cisco4Edit = (EditText) findViewById(R.id.cisco4Edit);

        pEntradaEdit = (EditText) findViewById(R.id.pEntradaEdit);
        pCentroEdit = (EditText) findViewById(R.id.pCentroEdit);
        pFundoEdit = (EditText) findViewById(R.id.pFundoEdit);
        pCisco1Edit = (EditText) findViewById(R.id.pCisco1Edit);
        pCisco2Edit = (EditText) findViewById(R.id.pCisco2Edit);
        pCisco3Edit = (EditText) findViewById(R.id.pCisco3Edit);
        pCisco4Edit = (EditText) findViewById(R.id.pCisco4Edit);
        Calc();



    }

    public void onClickCalcBtn(View v){

        if(isEmpty(entradaEdit) || isEmpty(centroEdit) || isEmpty(fundoEdit) || isEmpty(cisco1Edit)
                || isEmpty(cisco2Edit)  || isEmpty(cisco3Edit) || isEmpty(cisco4Edit)  || isEmpty(pEntradaEdit)
                || isEmpty(pCentroEdit) || isEmpty(pFundoEdit) || isEmpty(pCisco1Edit) || isEmpty(pCisco2Edit)
                || isEmpty(pCisco3Edit)  ||isEmpty(pCisco4Edit)){
            Toast.makeText(getApplicationContext(),"Tem que preencher todos os campos",Toast.LENGTH_LONG).show();
            return;
        }
        double dE = Double.valueOf(entradaEdit.getText().toString());
        double dC = Double.valueOf(centroEdit.getText().toString());
        double dF = Double.valueOf(fundoEdit.getText().toString());
        double dC1 = Double.valueOf(cisco1Edit.getText().toString());
        double dC2 = Double.valueOf(cisco2Edit.getText().toString());
        double dC3 = Double.valueOf(cisco3Edit.getText().toString());
        double dC4 = Double.valueOf(cisco4Edit.getText().toString());

        double pE = Double.valueOf(pEntradaEdit.getText().toString());
        double pC = Double.valueOf(pCentroEdit.getText().toString());
        double pF = Double.valueOf(pFundoEdit.getText().toString());
        double pC1 = Double.valueOf(pCisco1Edit.getText().toString());
        double pC2 = Double.valueOf(pCisco2Edit.getText().toString());
        double pC3 = Double.valueOf(pCisco3Edit.getText().toString());
        double pC4 = Double.valueOf(pCisco4Edit.getText().toString());

        double [][] data = {{1,10*Math.log10(dE)}, {1, 10*Math.log10(dC)},
                {1, 10*Math.log10(dF)}, {1, 10 * Math.log10(dC1)}, {1, 10 * Math.log10(dC2)},
                {1, 10 * Math.log10(dC3)}, {1, 10 * Math.log10(dC4)}};
        double [][] Pot = {{pE}, {pC}, {pF}, {pC1}, {pC2}, {pC3}, {pC4}};
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
        for(int i = 0; i <=6; i++){
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
