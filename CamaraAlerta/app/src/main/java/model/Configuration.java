package model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by Gustavo on 14/10/2016.
 */

public class Configuration {
    //public static String base_url = "http://10.0.2.2:9000/";
    //public static String base_url = "http://192.168.1.4:9000/";
    public static String base_url = "http://191.252.0.77:80/";
    public static Cidadao usuario;
    public static boolean loginNormal = false;
    public static boolean loginFacebook = false;
    public static boolean loginGoogle = false;
    //public static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
    public static Gson gson = new GsonBuilder().setDateFormat("MMM dd, yyyy hh:mm:ss a").create();
}
