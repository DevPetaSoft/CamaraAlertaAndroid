package model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Lucas on 11/15/2016.
 */

public class Vereador extends User implements Serializable {
    private static final long serialVersionUID = 1L;
    private Date dataCadastro;
    private int nivalDeAcesso = 0;
    private boolean emDia=true;
    private Cidade cidade;
    private String cpf;
    private Administrador criadoPor;
    private boolean deleted = false;

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public int getNivalDeAcesso() {
        return nivalDeAcesso;
    }

    public void setNivalDeAcesso(int nivalDeAcesso) {
        this.nivalDeAcesso = nivalDeAcesso;
    }

    public boolean isEmDia() {
        return emDia;
    }

    public void setEmDia(boolean emDia) {
        this.emDia = emDia;
    }

    public Cidade getCidade() {
        return cidade;
    }

    public void setCidade(Cidade cidade) {
        this.cidade = cidade;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public Administrador getCriadoPor() {
        return criadoPor;
    }

    public void setCriadoPor(Administrador criadoPor) {
        this.criadoPor = criadoPor;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

}
