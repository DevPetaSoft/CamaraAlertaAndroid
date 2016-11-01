package model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by Gustavo on 14/10/2016.
 */

public class Configuration {
    public static String base_url = "http://192.168.0.103:8080/CamaraAlertaWS/";
    public static Cidadao usuario;
    public static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();

}
