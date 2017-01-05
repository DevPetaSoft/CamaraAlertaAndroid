package model;

/**
 * Created by Lucas on 1/4/2017.
 */

import java.io.Serializable;

public class MensagemChat implements Serializable {
    private int id;

    private String mensagem;
    // 0 - Vereador 1 - Cidadao
    private int enviadoPor;
    private int ordem;
    private boolean novo;
    private CanalDeComunicacao canal;

    public int getId() { return id; }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public int getEnviadoPor() {
        return enviadoPor;
    }

    public void setEnviadoPor(int enviadoPor) {
        this.enviadoPor = enviadoPor;
    }

    public int getOrdem() {
        return ordem;
    }

    public void setOrdem(int ordem) {
        this.ordem = ordem;
    }

    public boolean isNovo() {
        return novo;
    }

    public void setNovo(boolean novo) {
        this.novo = novo;
    }

    public CanalDeComunicacao getCanal() {
        return canal;
    }

    public void setCanal(CanalDeComunicacao canal) {
        this.canal = canal;
    }
}
