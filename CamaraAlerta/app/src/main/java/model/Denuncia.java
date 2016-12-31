package model;

/**
 * Created by Lucas on 11/15/2016.
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Denuncia implements Serializable{

    private static final long serialVersionUID = 1L;
    private int id;
    private String titulo;
    private String descricao;
    private Date data;
    private boolean anonima;
    private String mensagem;
    private ArrayList<String> fotos;
    private ArrayList<String> fotosServidor;
    private Coordenadas coordenadas;
    private int status;
    private boolean comunicacaoPermitida;
    private String relatorio;
    private Cidadao cidadao;
    private Vereador vereador;
    private boolean deleted;
    private boolean novo;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo(){ return titulo; }

    public void setTitulo(String titulo){ this.titulo = titulo; }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public boolean isAnonima() {
        return anonima;
    }

    public void setAnonima(boolean anonima) {
        this.anonima = anonima;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public ArrayList<String> getFotosServidor() {
        return fotosServidor;
    }

    public void setFotosServidor(ArrayList<String> fotosServidor) {
        this.fotosServidor = fotosServidor;
    }

    public ArrayList<String> getFotos() {
        return fotos;
    }

    public void setFotos(ArrayList<String> fotos) {
        this.fotos = fotos;
    }

    public Coordenadas getCoordenadas() {
        return coordenadas;
    }

    public void setCoordenadas(Coordenadas coordenadas) {
        this.coordenadas = coordenadas;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isComunicacaoPermitida() {
        return comunicacaoPermitida;
    }

    public void setComunicacaoPermitida(boolean comunicacaoPermitida) {
        this.comunicacaoPermitida = comunicacaoPermitida;
    }

    public String getRelatorio() {
        return relatorio;
    }

    public void setRelatorio(String relatorio) {
        this.relatorio = relatorio;
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

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isNovo() {
        return novo;
    }

    public void setNovo(boolean novo) {
        this.novo = novo;
    }

}
