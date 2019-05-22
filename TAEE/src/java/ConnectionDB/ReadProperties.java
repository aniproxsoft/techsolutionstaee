/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ConnectionDB;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 *
 * @author ANIPROXTOART
 */
public  class ReadProperties {
    private java.net.URL ruta;
    private String ipHost;
    private String puerto;
    private String basedatos;
    private String usuario;
    private String contrasena;
    private String gestor;
    
    public ReadProperties(java.net.URL path) throws IOException{
        this.ruta=path;
        init();
    }
    
    private void init()throws IOException{
        try{
        Properties props=new Properties();
        InputStream in = ruta.openStream();
        props.load(in);    
        ipHost=props.getProperty("iphost");
        puerto=props.getProperty("puerto");
        basedatos=props.getProperty("namedb");
        usuario=props.getProperty("usuario");
        contrasena=props.getProperty("contrasena");
        gestor=props.getProperty("gestor");
        in.close();
        }catch(IOException ex){            
            System.err.println("causa: " + ex.getCause());
            System.err.println("Mensaje: " + ex.getMessage());
            System.err.println("Localizacion: " + ex.getLocalizedMessage());
            System.err.println("Traza: " + ex.getStackTrace());
            throw ex;
        }
    }
    public URL getRuta() {
        return ruta;
    }
    public void setRuta(URL ruta) {
        this.ruta = ruta;
    }
    public String getIpHost() {
        return ipHost;
    }
    public void setIpHost(String ipHost) {
        this.ipHost = ipHost;
    }
    public String getPuerto() {
        return puerto;
    }
    public void setPuerto(String puerto) {
        this.puerto = puerto;
    }
    public String getBasedatos() {
        return basedatos;
    }
    public void setBasedatos(String basedatos) {
        this.basedatos = basedatos;
    }
    public String getUsuario() {
        return usuario;
    }
    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
    public String getContrasena() {
        return contrasena;
    }
    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
    public String getGestor() {
        return gestor;
    }
    public void setGestor(String gestor) {
        this.gestor = gestor;
    }
}

