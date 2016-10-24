package model;

import java.io.Serializable;

/**
 * Created by gudominguete on 23/10/16.
 */

public class Cidade implements Serializable {
    private int id;
    private String nome;
    private String estado;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
