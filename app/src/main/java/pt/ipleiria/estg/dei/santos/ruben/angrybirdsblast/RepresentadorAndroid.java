package pt.ipleiria.estg.dei.santos.ruben.angrybirdsblast;

import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import pt.ipleiria.estg.dei.gridcomponent.CellRepresentation;
import pt.ipleiria.estg.dei.gridcomponent.GridComponent;
import pt.ipleiria.estg.dei.gridcomponent.ImageCellRepresentation;
import pt.ipleiria.estg.dei.gridcomponent.SingleImageCellRepresentation;
import pt.ipleiria.estg.dei.gridcomponent.TextCellRepresentation;
import pt.ipleiria.estg.ei.p2.blast.ResultadoActivity;
import pt.ipleiria.estg.ei.p2.blast.modelo.AreaJogavel;
import pt.ipleiria.estg.ei.p2.blast.modelo.Especie;
import pt.ipleiria.estg.ei.p2.blast.modelo.Jogo;
import pt.ipleiria.estg.ei.p2.blast.modelo.OuvinteJogo;
import pt.ipleiria.estg.ei.p2.blast.modelo.bases.Base;
import pt.ipleiria.estg.ei.p2.blast.modelo.bases.BaseSuportadora;
import pt.ipleiria.estg.ei.p2.blast.modelo.objetivos.ObjetivoJogo;
import pt.ipleiria.estg.ei.p2.blast.modelo.objetivos.ObjetivoParcial;
import pt.ipleiria.estg.ei.p2.blast.modelo.objetivos.ObjetivoParcialBalao;
import pt.ipleiria.estg.ei.p2.blast.modelo.suportados.Balao;
import pt.ipleiria.estg.ei.p2.blast.modelo.suportados.Bomba;
import pt.ipleiria.estg.ei.p2.blast.modelo.suportados.Foguete;
import pt.ipleiria.estg.ei.p2.blast.modelo.suportados.Madeira;
import pt.ipleiria.estg.ei.p2.blast.modelo.suportados.Pedra;
import pt.ipleiria.estg.ei.p2.blast.modelo.suportados.Porco;
import pt.ipleiria.estg.ei.p2.blast.modelo.suportados.Suportado;
import pt.ipleiria.estg.ei.p2.blast.modelo.suportados.SuportadoAgrupavel;
import pt.ipleiria.estg.ei.p2.blast.modelo.suportados.SuportadoAgrupavelBonus;
import pt.ipleiria.estg.ei.p2.blast.modelo.suportados.SuportadoSensivelOndaChoqueComForca;
import pt.ipleiria.estg.ei.p2.blast.modelo.suportados.Vidro;
import pt.ipleiria.estg.ei.p2.blast.modelo.utils.Direcao;
import pt.ipleiria.estg.ei.p2.blast.modelo.utils.Posicao;

/**
 * Created by Ruben on 16/05/2017.
 */

public class RepresentadorAndroid implements OuvinteJogo{
    private Jogo jogo;
    private Context context;
    private GridComponent gridComponent;
    private GridComponent gridComponentObjetivos;
    public static final String EXTRA_RESULTADO="0";
    public static final String EXTRA_PONTOS="1";

    public RepresentadorAndroid(Context context, Jogo jogo, GridComponent gridComponent, GridComponent gridComponentObjetivos) {
        this.context=context;
        this.gridComponent=gridComponent;
        this.gridComponentObjetivos=gridComponentObjetivos;
        this.jogo=jogo;
        this.jogo.adicionarOuvinte(this);

        limparGrid(gridComponent);
        limparGrid(gridComponentObjetivos);


        representarAreaJogavel();
        representaObjectivos();
        representaMovimentosDisponiveis();
        representarPontuacao();

    }

    private void limparGrid(GridComponent gridComponent) {
        gridComponent.reinitialize();
    }

    private void representaMovimentosDisponiveis() {
        gridComponentObjetivos.setCurrentLayer(1);
        int coluna=gridComponentObjetivos.getNumberOfColumns()-2;

        colocarObjectivoTextoEm(new Posicao(0,coluna),"Moves");
        colocarObjectivoTextoEm(new Posicao(1,coluna),jogo.getNumeroMovimentosRestantes()+"");

    }
    private void representarPontuacao() {
        gridComponentObjetivos.setCurrentLayer(1);
        int coluna=gridComponentObjetivos.getNumberOfColumns()-1;

        colocarObjectivoTextoEm(new Posicao(0,coluna),"Score");
        colocarObjectivoTextoEm(new Posicao(1,coluna),jogo.getPontuacao()+"");

    }

    private void representaObjectivos() {
        ObjetivoJogo objetivoJogo=jogo.getObjetivoJogo();
        int num_colunas=objetivoJogo.getNumeroObjetivosParciais()+2;
        gridComponentObjetivos.setNumberOfColumns(num_colunas);

        for (int i = 0; i < jogo.getObjetivoJogo().getNumeroObjetivosParciais(); i++) {
            ObjetivoParcial objetivoParcial = jogo.getObjetivoJogo().getObjetivoParcial(i);
            representarObjetivoParcial(objetivoParcial, i);
        }
    }

    private void representarObjetivoParcial(ObjetivoParcial objetivoParcial, int coluna) {
        gridComponentObjetivos.setCurrentLayer(1);
        if (objetivoParcial instanceof ObjetivoParcialBalao) {
            ObjetivoParcialBalao objetivo = (ObjetivoParcialBalao) objetivoParcial;

            colocarObjectivoParcialEm(new Posicao(0, coluna),  objetivo.getEspecie().toString()+".png");
            colocarObjectivoTextoEm(new Posicao(1, coluna), objetivo.getQuantidade()+"");
        } else {
            colocarObjectivoParcialEm(new Posicao(0, coluna),  "Porco1.png");
            colocarObjectivoTextoEm(new Posicao(1, coluna), objetivoParcial.getQuantidade()+"");
        }
    }

    private void colocarObjectivoTextoEm(Posicao posicao, String s) {
        CellRepresentation representacaoAntiga=getRepresentacaoObjetivoEm(posicao);
        TextCellRepresentation representacaoNova = new TextCellRepresentation(context, s);
        if(representacaoAntiga == null){
            gridComponentObjetivos.add(posicao.getLinha(), posicao.getColuna(), representacaoNova);
        }else{
            gridComponentObjetivos.replace(posicao.getLinha(), posicao.getColuna(), representacaoAntiga, representacaoNova);

        }
    }

    private void representarAreaJogavel() {
        AreaJogavel areaJogavel=jogo.getAreaJogavel();
        int num_linhas=areaJogavel.getNumeroLinhas();
        int num_colunas=areaJogavel.getNumeroColunas();
        gridComponent.setNumberOfColumns(num_colunas);
        gridComponent.setNumberOfRows(num_linhas);

        for (int i = 0; i < num_linhas; i++) {
            for (int j = 0; j <num_colunas ; j++) {
                Base base=areaJogavel.getBase(i,j);

                if(base instanceof BaseSuportadora){
                    gridComponent.setCurrentLayer(0);
                    colocarEm(base.getPosicao(),"BaseSuportadora.png");
                    representarSuportado(base.getPosicao(), ((BaseSuportadora) base).getSuportado());
                }
            }
        }
    }

    private void representarSuportado(Posicao posicao, Suportado suportado) {
        if(suportado == null){
            return;
        }
        gridComponent.setCurrentLayer(1);

        if(suportado instanceof Balao){
            colocarEm(posicao, ((Balao) suportado).getEspecie().toString()+".png");
        }else if(suportado instanceof  Porco){
            colocarEm(posicao, "Porco"+((Porco) suportado).getForca()+".png");
        }else if(suportado instanceof  Madeira){
            colocarEm(posicao, "Madeira"+((Madeira) suportado).getForca()+".png");
        }else if(suportado instanceof  Vidro){
            colocarEm(posicao, "Vidro.png");
        }else if(suportado instanceof  Foguete){
            colocarEm(posicao, "Foguete"+((Foguete) suportado).getDirecao()+".png");
        }else if(suportado instanceof  Pedra){
            colocarEm(posicao, "Pedra"+((Pedra) suportado).getForca()+".png");
        }else if(suportado instanceof  Bomba){
            colocarEm(posicao, "Bomba.png");
        }
    }

    private void colocarEm(Posicao posicao, String imagem) {
        CellRepresentation representacaoAntiga=getRepresentacaoEm(posicao);
        SingleImageCellRepresentation representacaoNova = new SingleImageCellRepresentation(context, imagem);
        if(representacaoAntiga == null){
            gridComponent.add(posicao.getLinha(), posicao.getColuna(), representacaoNova);
        }else{
            gridComponent.replace(posicao.getLinha(), posicao.getColuna(), representacaoAntiga, representacaoNova);

        }
    }

    private void colocarObjectivoParcialEm(Posicao posicao, String imagem) {
        CellRepresentation representacaoAntiga=getRepresentacaoObjetivoEm(posicao);
        SingleImageCellRepresentation representacaoNova = new SingleImageCellRepresentation(context, imagem);
        if(representacaoAntiga == null){
            gridComponentObjetivos.add(posicao.getLinha(), posicao.getColuna(), representacaoNova);
        }else{
            gridComponentObjetivos.replace(posicao.getLinha(), posicao.getColuna(), representacaoAntiga, representacaoNova);

        }
    }
    private CellRepresentation getRepresentacaoEm(Posicao posicao) {
        List<CellRepresentation> pilhaImagens=gridComponent.get(posicao.getLinha(), posicao.getColuna());

        if(pilhaImagens.size()==0)
            return null;

        return pilhaImagens.get(0);
    }

    private CellRepresentation getRepresentacaoObjetivoEm(Posicao posicao) {
        List<CellRepresentation> pilhaImagens=gridComponentObjetivos.get(posicao.getLinha(), posicao.getColuna());

        if(pilhaImagens.size()==0)
            return null;

        return pilhaImagens.get(0);
    }


    @Override
    public void balaoCriado(Balao balao, Base baseInsercao) {
        gridComponent.setCurrentLayer(1);
        Posicao posicao=new Posicao(jogo.getAreaJogavel().getNumeroLinhas(), baseInsercao.getPosicao().getColuna());
        colocarEm(posicao, balao.getEspecie().toString()+".png");
    }

    @Override
    public void suportadoExplodiu(Suportado suportado) {
        Posicao posicao=suportado.getBaseSuportadora().getPosicao();

        gridComponent.clear(posicao.getLinha(), posicao.getColuna());
    }

    @Override
    public void suportadoAgrupavelMovimentou(SuportadoAgrupavel<?> suportadoAgrupavel, BaseSuportadora origem, BaseSuportadora destino) {
        gridComponent.setCurrentLayer(1);
        Posicao posicao=new Posicao(jogo.getAreaJogavel().getNumeroLinhas(), destino.getPosicao().getColuna());

        CellRepresentation representacaoAntiga;

        int linhaO, colunaO, linhaD, colunaD;

        linhaD = destino.getPosicao().getLinha();
        colunaD = destino.getPosicao().getColuna();
        if(origem !=null) {
             linhaO = origem.getPosicao().getLinha();
             colunaO = origem.getPosicao().getColuna();
            representacaoAntiga=getRepresentacaoEm(origem.getPosicao());
        }else{
            linhaO=posicao.getLinha();
            colunaO=posicao.getColuna();
            representacaoAntiga=getRepresentacaoEm(posicao);
        }

        if(representacaoAntiga!=null)
            gridComponent.moveItem(linhaO, colunaO, linhaD, colunaD, 5, representacaoAntiga);

    }

    @Override
    public void objetivosConcluidos() {
        Intent intent = new Intent(context, ResultadoActivity.class);
        String message = "Ganhou!!";
        Log.d("MENSAGEM", message);
        Log.d("PONTUACAO", jogo.getPontuacao()+"");
        intent.putExtra(EXTRA_RESULTADO, message);
        intent.putExtra(EXTRA_PONTOS, jogo.getPontuacao()+"");
        ((MainActivity)context).startActivityForResult(intent, MainActivity.RESULT_ACAO);
    }

    @Override
    public void movimentosEsgotados() {
        Intent intent = new Intent(context, ResultadoActivity.class);
        String message;

        if(jogo.getObjetivoJogo().isConcluido())
            message="Ganhou!";
        else
            message="Ganhou!";

        intent.putExtra(EXTRA_RESULTADO, message);
        intent.putExtra(EXTRA_PONTOS, jogo.getPontuacao()+"");
        ((MainActivity)context).startActivityForResult(intent, MainActivity.RESULT_ACAO);
    }

    @Override
    public void suportadoDestruidoParcialmente(SuportadoSensivelOndaChoqueComForca suportado, float percentagemRestante) {
        representarSuportado(suportado.getBaseSuportadora().getPosicao(), suportado);
    }

    @Override
    public void fogueteLancado(Foguete foguete) {

    }

    @Override
    public void combinacaoFoguetesLancados(Foguete foguete) {

    }

    @Override
    public void fogueteMudaDirecao(Foguete foguete) {

    }

    @Override
    public void porcoCriado(Porco porco, BaseSuportadora baseSuportadora) {
        gridComponent.setCurrentLayer(1);
        colocarEm(baseSuportadora.getPosicao(), "Porco"+porco.getForca()+".png");
    }

    @Override
    public void fogueteCriado(Foguete foguete, BaseSuportadora baseSuportadora) {
        gridComponent.setCurrentLayer(1);
        colocarEm(baseSuportadora.getPosicao(), "Foguete"+foguete.getDirecao().toString()+".png");
    }

    @Override
    public void vidroCriado(Vidro vidro, BaseSuportadora baseSuportadora) {
        gridComponent.setCurrentLayer(1);
        colocarEm(baseSuportadora.getPosicao(), "Vidro.png");

    }

    @Override
    public void madeiraCriada(Madeira madeira, BaseSuportadora baseSuportadora) {
        gridComponent.setCurrentLayer(1);
        colocarEm(baseSuportadora.getPosicao(), "Madeira"+madeira.getForca()+".png");

    }

    @Override
    public void pedraCriada(Pedra pedra, BaseSuportadora baseSuportadora) {
        gridComponent.setCurrentLayer(1);
        colocarEm(baseSuportadora.getPosicao(), "Pedra"+pedra.getForca()+".png");
    }

    @Override
    public void bombaAtivada(Bomba bomba) {

    }

    @Override
    public void combinacaoBombasAtivadas(Bomba bomba) {

    }

    @Override
    public void bombaCriada(Bomba bomba, BaseSuportadora baseSuportadora) {
        gridComponent.setCurrentLayer(1);
        colocarEm(baseSuportadora.getPosicao(), "Bomba.png");
    }

    @Override
    public void combinacaoBombaFogueteAtivada(SuportadoAgrupavelBonus suportadoAgrupavelBonus) {

    }

    public void representar(){
        representarAreaJogavel();
        representaObjectivos();
        representaMovimentosDisponiveis();
        representarPontuacao();
    }
}
