package model;

/**
 * Created by Lucas on 11/15/2016.
 */
import java.io.Serializable;

public class Administrador  extends User implements Serializable{
    private static final long serialVersionUID = 1L;
    private int nivalDeAcesso = 0;
    private boolean deleted = false;


    public int getNivalDeAcesso() {
        return nivalDeAcesso;
    }

    public void setNivalDeAcesso(int nivalDeAcesso) {
        this.nivalDeAcesso = nivalDeAcesso;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

}
