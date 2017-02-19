package model;

import java.io.Serializable;

/**
 * Created by gudominguete on 19/02/17.
 */

public class Estado implements Serializable {

    private int id;

    private String nome;

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
}
