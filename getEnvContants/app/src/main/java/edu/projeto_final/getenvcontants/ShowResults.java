package edu.projeto_final.getenvcontants;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ShowResults extends AppCompatActivity {

    TextView mainText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_results);
        mainText = (TextView) findViewById(R.id.mainText);

        Intent i = getIntent();
        String resultados = i.getStringExtra("resultados");

        mainText.setText(resultados);
    }
}
