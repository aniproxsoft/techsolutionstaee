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
import java.util.ArrayList;
import java.util.List;
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

    private boolean rendered;
    private boolean rendered2;
    private Integer progress;
    private String pathProyect = "";
    ReadExcel reader = new ReadExcel();
    private static final File LOCATION = new File("web\\resources\\files\\");
    private List<BulkLoadVO> cargas;
    private List<BulkLoadVO> status;
    private UploadedFile file;

    @PostConstruct
    public void init() {
        cargas = new ArrayList<>();
        status = new ArrayList<>();
        ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        pathProyect = (String) servletContext.getRealPath("/").replace("\\build\\web", "");
        rendered = true;
        rendered2 = false;
        progress = 0;
        RequestContext.getCurrentInstance().update("progress");
//        System.out.println("ddd " + pathProyect);
    }

    public void nuevo() {
        init();

    }

    public void upload() throws IOException, URISyntaxException {

        RequestContext.getCurrentInstance().update("tabla1");
        progress = 10;
        String path;
        if (file != null) {
            progress += 23;
            String prefix = FilenameUtils.getBaseName(file.getFileName());
            String suffix = FilenameUtils.getExtension(file.getFileName());
            if (suffix.equals("xlsx")) {
                rendered = false;
                rendered2 = true;
                RequestContext.getCurrentInstance().update("botonGuardar");
                RequestContext.getCurrentInstance().update("upload");
                RequestContext.getCurrentInstance().update("botonNuevo");
                progress += 13;
                System.out.println("Uploaded file now: " + file.getFileName());
                InputStream inputStream = file.getInputstream();
                OutputStream outputStream = null;
                String name = prefix + "." + suffix;
                System.out.println("locaname: " + pathProyect + LOCATION + "\\" + name);
                progress += 28;
                File fileCopy = new File(pathProyect + LOCATION + "\\" + name);
                String ruta = pathProyect + LOCATION + "\\" + name;
                outputStream = new FileOutputStream(fileCopy);
                int read = 0;
                byte[] bytes = new byte[1024];
                progress += 7;
                while ((read = inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);

                }
                progress += 8;

                cargas = reader.leerExcel(ruta);
                if (cargas != null) {
                    BulkLoadVO bulkStatus = new BulkLoadVO();
                    BulkLoadVO fail = new BulkLoadVO();
                    BulkLoadVO update = new BulkLoadVO();
                    BulkLoadVO insert = new BulkLoadVO();
                    bulkStatus.setType("Excel");
                    bulkStatus.setValue(cargas.get(0).getBulks());
                    fail.setType("Fallidos");
                    fail.setValue(cargas.get(0).getFails());
                    update.setType("Modificados");
                    update.setValue(cargas.get(0).getUpdates());
                    insert.setType("Insertados");
                    insert.setValue(cargas.get(0).getInserts());
                    status.add(bulkStatus);
                    status.add(fail);
                    status.add(update);
                    status.add(insert);
                    progress = 100;
                    RequestContext.getCurrentInstance().update("tabla2");
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "", "El proceso terminó"));
                    RequestContext.getCurrentInstance().update("mensajes");
                    RequestContext.getCurrentInstance().execute("ocultaMsj(3000)");
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Las columnas del archivo son incorrectas por favor descarge la plantilla o el documento esta en blanco"));
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

    public Integer getProgress() {
        if (progress == null) {
            progress = 0;
        } else {

            if (progress > 100) {
                progress = 100;
            }
        }

        return progress;
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

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    public List<BulkLoadVO> getStatus() {
        return status;
    }

    public void setStatus(List<BulkLoadVO> status) {
        this.status = status;
    }

    public List<BulkLoadVO> getCargas() {
        return cargas;
    }

    public void setCargas(List<BulkLoadVO> cargas) {
        this.cargas = cargas;
    }

    public boolean isRenderd() {
        return rendered;
    }

    public void setRenderd(boolean renderd) {
        this.rendered = renderd;
    }

    public boolean isRendered2() {
        return rendered2;
    }

    public void setRendered2(boolean rendered2) {
        this.rendered2 = rendered2;
    }

}
