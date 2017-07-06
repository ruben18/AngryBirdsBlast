package pt.ipleiria.estg.ei.p2.blast.modelo.suportados;

import pt.ipleiria.estg.ei.p2.blast.modelo.bases.BaseSuportadora;
import pt.ipleiria.estg.ei.p2.blast.modelo.utils.Direcao;

import java.util.List;

public class Foguete extends SuportadoAgrupavelBonus {

    private Direcao direcao;

    public Foguete(BaseSuportadora baseSuportadora) {
        super(baseSuportadora);
        direcao = Direcao.values()[baseSuportadora.getAreaJogavel().getValorAleatorio(Direcao.values().length)];
    }

    @Override
    protected void aplicarDestruicaoDupla() {
        getJogo().informarCombinacaoFoguetesLancados(this);
        destruirColuna(baseSuportadora.getPosicao().getColuna());
        destruirLinha(baseSuportadora.getPosicao().getLinha());
    }

    @Override
    protected void aplicarDestruicaoSimples() {
        int posicao = direcao == Direcao.VERTICAL ? baseSuportadora.getPosicao().getColuna() : baseSuportadora.getPosicao().getLinha();

        getJogo().informarFogueteLancado(this);
        if (direcao == Direcao.VERTICAL)
            destruirColuna(posicao);
        else
            destruirLinha(posicao);
    }

    public Direcao getDirecao() {
        return direcao;
    }


    private void destruirLinha(int linha) {
        List<BaseSuportadora> bases = baseSuportadora.getAreaJogavel().getBasesSuporteDaLinha(linha);
        for (BaseSuportadora base : bases) {
            base.reagirBonus();
        }
    }

    private void destruirColuna(int coluna) {
        List<BaseSuportadora> bases = baseSuportadora.getAreaJogavel().getBasesSuporteDaColuna(coluna);
        for (BaseSuportadora base : bases) {
            base.reagirBonus();
        }
    }

    public void inverterDirecao() {
        direcao = direcao.perpendicular();
        getJogo().informarMudancaDirecaoFoguete(this);
    }

}
