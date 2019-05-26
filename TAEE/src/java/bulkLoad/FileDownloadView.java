/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bulkLoad;

import java.io.InputStream;
import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author ANIPROXTOART
 */
@ManagedBean(name = "beanDowload")
@ViewScoped
public class FileDownloadView implements Serializable{

     private StreamedContent file;
     
    public FileDownloadView() {        
        InputStream stream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/resources/plantillas/Usuario.xlsx");
        file = new DefaultStreamedContent(stream,"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "Plantilla-Usuario.xlsx");
    }
 
    public StreamedContent getFile() {
        return file;
    }
  
    
}
