package model;

import java.io.Serializable;

/**
 * Created by gudominguete on 23/10/16.
 */

public class Cidade implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private String nome;
    private Estado estado;

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

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }
}
