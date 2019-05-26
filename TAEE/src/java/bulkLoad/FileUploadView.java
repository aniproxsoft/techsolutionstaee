/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bulkLoad;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.util.IOUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author ANIPROXTOART
 */
@ManagedBean(name = "beanUpload")
@ViewScoped
public class FileUploadView implements Serializable {

    private String pathProyect = "";
    ReadExcel reader = new ReadExcel();
    private static final File LOCATION = new File("web\\resources\\files\\");

    private UploadedFile file;

    @PostConstruct
    public void init() {
        ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        pathProyect = (String) servletContext.getRealPath("/").replace("\\build\\web", "");

        System.out.println("ddd " + pathProyect);
    }

    public void upload() throws IOException, URISyntaxException {
        String path;
        if (file != null) {
            String prefix = FilenameUtils.getBaseName(file.getFileName());
            String suffix = FilenameUtils.getExtension(file.getFileName());
            if (suffix.equals("xlsx")) {
                System.out.println("Uploaded file now: " + file.getFileName());
                InputStream inputStream = file.getInputstream();
                OutputStream outputStream = null;
                String name = prefix + "." + suffix;
                System.out.println("locaname: " + pathProyect + LOCATION + "\\" + name);

                File fileCopy = new File(pathProyect + LOCATION + "\\" + name);
                String ruta = pathProyect + LOCATION + "\\" + name;
                outputStream = new FileOutputStream(fileCopy);
                int read = 0;
                byte[] bytes = new byte[1024];

                while ((read = inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                }
                if (reader.leerExcel(ruta)) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Las columnas del archivo son correctas"));
                    RequestContext.getCurrentInstance().update("mensajes");
                    RequestContext.getCurrentInstance().execute("ocultaMsj(3000)");
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Las columnas del archivo son incorrectas por favor descarge la plantilla"));
                    RequestContext.getCurrentInstance().update("mensajes");
                    RequestContext.getCurrentInstance().execute("ocultaMsj(3000)");
                }

                System.out.println("Done!");

            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Error el formato de archivo no coincide"));
                RequestContext.getCurrentInstance().update("mensajes");
                RequestContext.getCurrentInstance().execute("ocultaMsj(3000)");
            }

        } else {

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Error no ha seleccionado un archivo"));
            RequestContext.getCurrentInstance().update("mensajes");
            RequestContext.getCurrentInstance().execute("ocultaMsj(3000)");

        }
    }

    public void handleFileUpload(FileUploadEvent event) {

    }

    public String getPathProyect() {
        return pathProyect;
    }

    public void setPathProyect(String pathProyect) {
        this.pathProyect = pathProyect;
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

}
