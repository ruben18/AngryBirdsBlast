package pt.ipleiria.estg.dei.santos.ruben.angrybirdsblast;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import pt.ipleiria.estg.dei.gridcomponent.GridComponent;
import pt.ipleiria.estg.dei.gridcomponent.GridPanelEventHandler;
import pt.ipleiria.estg.ei.p2.blast.RepresentadorTextual;
import pt.ipleiria.estg.ei.p2.blast.modelo.Jogo;

public class MainActivity extends AppCompatActivity implements GridPanelEventHandler {
    private GridComponent gridComponent;
    private GridComponent gridComponentObjetivos;
    private Jogo jogo;
    private RepresentadorAndroid representadorAndroid;
    public static final int RESULT_ACAO=2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridComponent= (GridComponent) findViewById(R.id.grid);
        gridComponentObjetivos= (GridComponent) findViewById(R.id.objetivos);
        jogo=new Jogo();
        jogo.iterar();
        representadorAndroid=new RepresentadorAndroid(this, jogo, gridComponent, gridComponentObjetivos);

        gridComponent.startIterations(1000);

        gridComponent.setEventHandler(this);
    }

    @Override
    public void pressed(int i, int j) {
        //empty
    }

    @Override
    public void released(int i, int j) {
        if(jogo.interagir(i,j)) {
            jogo.iterar();
            representadorAndroid.representar();
        }

    }

    @Override
    public void dragged(int i, int j) {
        //empty
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==RESULT_ACAO){
            if(resultCode==RESULT_OK){
                recomecar();
            }else if(resultCode==RESULT_CANCELED){
                finish();
            }
        }

    }

    private void recomecar() {
        jogo=new Jogo();
        representadorAndroid=new RepresentadorAndroid(this, jogo, gridComponent, gridComponentObjetivos);
        jogo.iterar();
    }
}
