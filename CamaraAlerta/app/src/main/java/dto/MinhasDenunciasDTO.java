package dto;

import model.Denuncia;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Lucas on 12/16/2016.
 */
public class MinhasDenunciasDTO implements Serializable{

    private ArrayList<Denuncia> minhasDenuncias;
    private int numeroDeDenuncias;

    public MinhasDenunciasDTO(ArrayList<Denuncia> minhasDenuncias, int numeroDeDenuncias){
        this.minhasDenuncias = minhasDenuncias;
        this.numeroDeDenuncias = numeroDeDenuncias;
    }

    public ArrayList<Denuncia> getMinhasDenuncias() {
        return minhasDenuncias;
    }

    public void setMinhasDenuncias(ArrayList<Denuncia> minhasDenuncias) {
        this.minhasDenuncias = minhasDenuncias;
    }

    public int getNumeroDeDenuncias() {
        return numeroDeDenuncias;
    }

    public void setNumeroDeDenuncias(int numeroDeDenuncias) {
        this.numeroDeDenuncias = numeroDeDenuncias;
    }

}
