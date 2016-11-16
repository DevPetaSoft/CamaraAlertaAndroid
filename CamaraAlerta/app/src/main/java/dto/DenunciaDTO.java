package dto;

/**
 * Created by Lucas on 11/15/2016.
 */

import java.util.ArrayList;
import java.util.List;
import model.Denuncia;
import java.io.Serializable;

public class DenunciaDTO implements Serializable{

    private Denuncia denuncia;
    private List<String> listaFotos = new ArrayList<String>();

    public DenunciaDTO(Denuncia denuncia, List<String> listaFotos){
        this.denuncia = denuncia;
        this.listaFotos = listaFotos;
    }

    public Denuncia getDenuncia(){
        return this.denuncia;
    }

    public void setDenuncia(Denuncia denuncia){
        this.denuncia = denuncia;
    }

    public List<String> getListaFotos(){
        return this.listaFotos;
    }

    public void setListaFotos(ArrayList<String> listaFotos){
        this.listaFotos = listaFotos;
    }
}
