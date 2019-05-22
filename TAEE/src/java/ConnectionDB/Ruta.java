/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ConnectionDB;

import java.net.URL;

/**
 *
 * @author ANIPROXTOART
 */
public class Ruta {

    private final String dbMysql = "dbmysql.properties";

    public URL getFileDbMysql() {
        return getClass().getResource(dbMysql);
    }

}
