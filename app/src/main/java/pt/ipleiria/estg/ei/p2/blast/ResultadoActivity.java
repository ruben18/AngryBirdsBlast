package pt.ipleiria.estg.ei.p2.blast;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import pt.ipleiria.estg.dei.santos.ruben.angrybirdsblast.MainActivity;
import pt.ipleiria.estg.dei.santos.ruben.angrybirdsblast.R;
import pt.ipleiria.estg.dei.santos.ruben.angrybirdsblast.RepresentadorAndroid;


public class ResultadoActivity extends AppCompatActivity {
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultado);

        Intent intent = getIntent();
        String message = intent.getStringExtra(RepresentadorAndroid.EXTRA_RESULTADO);
        String pontos = intent.getStringExtra(RepresentadorAndroid.EXTRA_PONTOS);

        Log.d("Rmessage", message);
        Log.d("Ppontos", pontos);

        TextView textViewEstado = (TextView)findViewById(R.id.textViewEstado);
        TextView textViewPontos= (TextView)findViewById(R.id.textViewScore);

        textViewPontos.setText(pontos);
        textViewEstado.setText(message);

    }

    public void novoJogo(View view) {
        setResult(RESULT_OK);
        finish();
    }

    public void fecharJogo(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }
}
