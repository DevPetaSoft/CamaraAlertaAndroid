package dto;


import java.io.Serializable;

import model.Cidadao;

/**
 * Created by gudominguete on 31/01/17.
 */

public class FacebookDTO implements Serializable {

    private Cidadao cidadao;
    private Boolean novo;

    public FacebookDTO(Cidadao cidadao, Boolean novo) {
        this.cidadao = cidadao;
        this.novo = novo;
    }

    public Cidadao getCidadao() {
        return cidadao;
    }

    public void setCidadao(Cidadao cidadao) {
        this.cidadao = cidadao;
    }

    public Boolean getNovo() {
        return novo;
    }

    public void setNovo(Boolean novo) {
        this.novo = novo;
    }
}
