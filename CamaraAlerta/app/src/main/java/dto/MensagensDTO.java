package dto;

import model.CanalDeComunicacao;
import model.MensagemChat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lucas on 1/4/2017.
 */

public class MensagensDTO implements Serializable{

    private List<MensagemChat> list;
    private CanalDeComunicacao canal;

    public MensagensDTO() {
        this.list = new ArrayList<MensagemChat>();
    }

    public List<MensagemChat> getList() {
        return list;
    }

    public void setList(List<MensagemChat> list) {
        this.list = list;
    }

    public CanalDeComunicacao getCanal() {
        return canal;
    }

    public void setCanal(CanalDeComunicacao canal) {
        this.canal = canal;
    }
}
