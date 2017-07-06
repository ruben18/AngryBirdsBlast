package pt.ipleiria.estg.ei.p2.blast.modelo.suportados;

import pt.ipleiria.estg.ei.p2.blast.modelo.bases.BaseSuportadora;

import java.util.List;

/**
 * Created by catarina on 24/04/2017.
 */
public class Bomba extends SuportadoAgrupavelBonus {

    public Bomba(BaseSuportadora baseSuportadora) {
        super(baseSuportadora);
    }

    @Override
    protected void aplicarDestruicaoSimples() {
        getJogo().informarCombinacaoBombasAtivada(this);
        expandirExplosao(baseSuportadora.getAreaJogavel().getBasesSuporteAdjacentes(getBaseSuportadora().getPosicao()));
    }

    @Override
    protected void aplicarDestruicaoDupla() {
        getJogo().informarBombaAtivada(this);
        expandirExplosao(baseSuportadora.getAreaJogavel().getBasesSuporteProximas(getBaseSuportadora().getPosicao()));
    }

    private void expandirExplosao(List<BaseSuportadora> bases) {
        for (BaseSuportadora base : bases) {
            base.reagirBonus();
        }
    }

}
