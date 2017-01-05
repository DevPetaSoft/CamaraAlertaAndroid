package model;

/**
 * Classe do canal de comunicação entre o vereador e o cidadão
 * Created by Lucas on 1/4/2017.
 */

import java.io.Serializable;
import java.util.List;

public class CanalDeComunicacao implements Serializable {

    private int id;
    private Denuncia denuncia;
    private Cidadao cidadao;
    private Vereador vereador;
    private List<MensagemChat> mensagens;

    public int getId() { return id; }

    public Denuncia getDenuncia() {
        return denuncia;
    }

    public void setDenuncia(Denuncia denuncia) {
        this.denuncia = denuncia;
    }

    public Cidadao getCidadao() {
        return cidadao;
    }

    public void setCidadao(Cidadao cidadao) {
        this.cidadao = cidadao;
    }

    public Vereador getVereador() {
        return vereador;
    }

    public void setVereador(Vereador vereador) {
        this.vereador = vereador;
    }

    public List<MensagemChat> getMensagens() {
        return mensagens;
    }

    public void setMensagens(List<MensagemChat> mensagens) {
        this.mensagens = mensagens;
    }
}
