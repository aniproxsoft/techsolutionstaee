/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SendMail;

import java.io.Serializable;
import java.util.Properties;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author ANIPROXTOART
 */
@ManagedBean(name = "beanSendMailer")
@ViewScoped
public class SendMailer implements Serializable {

    public SendMailer() {
    }

    public void sendMail(String destino, String empresa, String user) {
        String remitente = "aniproxsoft@gmail.com";
        String password = "SoftwarePrueba.01";
        String msj = "<center><h1 style='color: #038C65'>¡ENHORABUENA! " + user + " </h1>";
        msj += "<div style='width:60%'><h2 >Su empresa: " + empresa + "</h2>" + "<h2> Ha sido abrobada por nuestro administrador, usted ahora puede publicar vacantes.</h2> </div>"
                + "</center>";
        Properties properties = new Properties();

        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.user", remitente);
        properties.put("mail.smtp.clave", password);

        Session session = Session.getDefaultInstance(properties);
        MimeMessage mensaje = new MimeMessage(session);

        try {
            mensaje.addRecipients(Message.RecipientType.TO, destino);
            mensaje.setSubject("Aprobación de empresas TAEG");
            mensaje.setText(msj, "utf-8", "html");
            Transport transporte = session.getTransport("smtp");
            transporte.connect("smtp.gmail.com", remitente, password);
            transporte.sendMessage(mensaje, mensaje.getAllRecipients());
            transporte.close();
            System.out.println("enviado");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void sendMailNoAprobado(String destino, String empresa, String user) {
        String remitente = "aniproxsoft@gmail.com";
        String password = "SoftwarePrueba.01";
        String msj = "<center><h1 style='color: #038C65'>¡DESAFORTUNADAMENTE! " + user + " </h1>";
        msj += "<div style='width:60%'><h2 >Su empresa: " + empresa + "</h2>" + "<h2> Ha sido desaprobada por nuestro administrador, usted ya no puede "
                + "publicar vacantes para mayor información mandenos un correo a: techsolutions@gmail.com.</h2> </div>"
                + "</center>";
        Properties properties = new Properties();

        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.user", remitente);
        properties.put("mail.smtp.clave", password);

        Session session = Session.getDefaultInstance(properties);
        MimeMessage mensaje = new MimeMessage(session);

        try {
            mensaje.addRecipients(Message.RecipientType.TO, destino);
            mensaje.setSubject("Aprobación de empresas TAEG");
            mensaje.setText(msj, "utf-8", "html");
            Transport transporte = session.getTransport("smtp");
            transporte.connect("smtp.gmail.com", remitente, password);
            transporte.sendMessage(mensaje, mensaje.getAllRecipients());
            transporte.close();
            System.out.println("enviado");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

//    public void sendMail() {
//        String remitente = "aniproxsoft@gmail.com";
//        String password = "SoftwarePrueba.01";
//        String destino = "antonio.01yea@gmail.com";
//        String empresa = "ANIPROXSOFT";
//        String user = "Toño";
//        String msj = "<center><h1 style='color: #038C65'>¡ENHORABUENA! " + user + " </h1>";
//        msj += "<div style='width:60%'><h2 >Su empresa: " + empresa + "</h2>" + "<h2> Ha sido abrobada por nuestro asministrador, usted ahora puede publicar vacantes.</h2> </div>"
//                + "</center>";
//        Properties properties = new Properties();
//
//        properties.put("mail.smtp.host", "smtp.gmail.com");
//        properties.put("mail.smtp.port", "587");
//        properties.put("mail.smtp.auth", "true");
//        properties.put("mail.smtp.starttls.enable", "true");
//        properties.put("mail.smtp.user", remitente);
//        properties.put("mail.smtp.clave", password);
//
//        Session session = Session.getDefaultInstance(properties);
//        MimeMessage mensaje = new MimeMessage(session);
//
//        try {
//            mensaje.addRecipients(Message.RecipientType.TO, destino);
//            mensaje.setSubject("Aprobación de empresas TAEG");
//            mensaje.setText(msj, "utf-8", "html");
//            Transport transporte = session.getTransport("smtp");
//            transporte.connect("smtp.gmail.com", remitente, password);
//            transporte.sendMessage(mensaje, mensaje.getAllRecipients());
//            transporte.close();
//            System.out.println("enviado");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
}
