package model;

import java.io.Serializable;

/**
 * Created by gudominguete on 19/02/17.
 */

public class HistoricoRelatorio implements Serializable {

    private int id;

    private String relatorio;

    private Integer status;

    private Denuncia denuncia;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRelatorio() {
        return relatorio;
    }

    public void setRelatorio(String relatorio) {
        this.relatorio = relatorio;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Denuncia getDenuncia() {
        return denuncia;
    }

    public void setDenuncia(Denuncia denuncia) {
        this.denuncia = denuncia;
    }
}
