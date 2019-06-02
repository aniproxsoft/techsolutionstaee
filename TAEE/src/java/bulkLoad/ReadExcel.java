/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bulkLoad;

import Seguridad.RolVO;
import Seguridad.UsuarioVO;
import com.google.gson.Gson;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.apache.poi.ss.usermodel.Cell;
import static org.apache.poi.ss.usermodel.CellType.BLANK;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.context.RequestContext;

/**
 *
 * @author ANIPROXTOART
 */
public class ReadExcel {

    List<String> columnas = new ArrayList<>();
    int flag = 1;
    private List<BulkLoadVO> bulkLoad;
    private List<BulkLoadVO> usuarios;
    private String observ = "";
    private BulkLoadVO carga;
    Gson gson = new Gson();
    List<String> columnasRead;
    BulkLoadDao dao = new BulkLoadDaoImplements();
    List<RolVO> roles = new ArrayList<>();
    List<BulkLoadVO> cargaMasiva = new ArrayList<>();

    public void init() {
        columnas= new ArrayList<>();
        System.out.println("inicia");
        columnas.add("Nombre");
        columnas.add("Apellidos");
        columnas.add("Correo Electronico");
        columnas.add("Contraseña");
        columnas.add("Rol");
        roles = dao.getRoles();

    }

    //Realiza el proceso completo
    public List<BulkLoadVO> leerExcel(String ruta) throws FileNotFoundException, IOException {
        init();
        columnasRead = new ArrayList<>();
        boolean mensaje = false;
        FileInputStream file = new FileInputStream(new File(ruta));
        // we create an XSSF Workbook object for our XLSX Excel File
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        // we get first sheet
        XSSFSheet sheet = workbook.getSheetAt(0);

        // we iterate on rows
        Iterator<Row> rowIterator = sheet.iterator();

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            if (row.getRowNum() == 0) {

                // iterate on cells for the current row
                Iterator<Cell> cellIterator = row.cellIterator();

                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    if (columnas.size() > 0) {

                        columnasRead.add(cell.toString());

                    }

                }

            }

        }
        workbook.close();
        file.close();
        if (comparaColumnas(columnas, columnasRead)) {
            if (leerDatos(ruta)) {

                return cargaMasiva;
            } else {
              
                return null;
            }

        } else {
            workbook.close();
            file.close();
                           

            return null;

        }

    }
//Compara si las columnas son iguales a las de la plantilla

    public boolean comparaColumnas(List<String> columnas, List<String> columnasRead) {
        boolean bandera = false;
        if (columnas.size() == columnasRead.size()) {
            for (int i = 0; i < columnas.size(); i++) {
                if (columnas.get(i).trim().equals(columnasRead.get(i).trim())) {
                    bandera = true;
                } else {
                   
                    return false;
                }
            }
        }

        return bandera;
    }

    //Metodo que lee los datos y almacena en objetos 
    public boolean leerDatos(String ruta) throws FileNotFoundException, IOException {
        bulkLoad = new ArrayList<>();
        usuarios = new ArrayList<>();

        boolean flag = false;
        FileInputStream files = new FileInputStream(new File(ruta));
//        System.out.println("Entra a otro proceso");
        XSSFWorkbook workbook = new XSSFWorkbook(files);
        // we get first sheet
        XSSFSheet sheet = workbook.getSheetAt(0);

        // we iterate on rows
        Iterator<Row> rowIterator = sheet.iterator();

        while (rowIterator.hasNext()) {
            carga = new BulkLoadVO();
            Row row = rowIterator.next();
            if (row.getRowNum() > 0) {
                // iterate on cells for the current row
                Iterator<Cell> cellIterator = row.cellIterator();

                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();

                    switch (cell.getColumnIndex()) {
                        case 0:

                            carga.setNombre(cell.toString());

                            break;
                        case 1:
                            carga.setApellidos(cell.toString());

                            break;

                        case 2:
                            carga.setCorreo(cell.toString());

                            break;

                        case 3:
                            carga.setContraseña(cell.toString());

                            break;

                        case 4:
                            carga.setNombre_rol(cell.toString());

                            break;

                    }

                }
                bulkLoad.add(carga);

            }

        }
        for (int i = 0; i < bulkLoad.size(); i++) {
            observ="";
            Integer rol=existeRol(bulkLoad.get(i).getNombre_rol());
            if (bulkLoad.size() > 0) {
                if (bulkLoad.get(i).getNombre().equals("") || bulkLoad.get(i).getNombre() == null) {
                    observ += "El campo Nombre esta en blanco"+". ";
                } else if (bulkLoad.get(i).getNombre().length() > 50) {
                    observ += "El campo Nombre excede numero de caracteres (50)"+". ";
                }
                if (bulkLoad.get(i).getApellidos().equals("") || bulkLoad.get(i).getApellidos()== null) {
                    observ += "El campo Apellidos esta en blanco"+". ";
                } else if (bulkLoad.get(i).getApellidos().length() > 50) {
                    observ += "El campo Apellidos excede numero de caracteres (50)"+". ";
                }
                if (bulkLoad.get(i).getCorreo().equals("") || bulkLoad.get(i).getCorreo() == null) {
                    observ += "El campo Correo esta en blanco"+". ";
                } else if (bulkLoad.get(i).getCorreo().length() > 50) {
                    observ += "El campo Correo excede numero de caracteres (50)"+". ";
                }
                if (bulkLoad.get(i).getContraseña().equals("") || bulkLoad.get(i).getContraseña() == null) {
                    observ += "El campo Contraseña esta en blanco"+". ";
                } else if (bulkLoad.get(i).getContraseña().length() > 50) {
                    observ += "El campo Contraseña excede numero de caracteres (50)"+". ";
                }
                if (bulkLoad.get(i).getNombre_rol().equals("") || bulkLoad.get(i).getNombre_rol() == null) {
                    observ += "El campo Rol esta en blanco"+". ";
                } else if (bulkLoad.get(i).getNombre_rol().length() > 50) {
                    observ += "El campo Rol excede numero de caracteres (50)"+". ";
                }else if(rol==null){
                    observ+="El Rol no existe en la base de datos"+". ";
                }else if(rol>0){
                    bulkLoad.get(i).setId_rol(rol);
                }
                 
                
                if(observ.equals("")){
                    usuarios.add(bulkLoad.get(i));
                }else{
                    bulkLoad.get(i).setObservaciones(observ);
                }
            }

        }
        System.out.println("tamaño usuarios: " + usuarios.size());
        System.out.println("tamaño bulkload: " + bulkLoad.size());

//        System.out.println("usuarios:   " + gson.toJson(usuarios));
//        System.out.println("********************************");
//        System.out.println("bulkload:   " + gson.toJson(bulkLoad));
        cargaMasiva = dao.bulkLoad(gson.toJson(usuarios), gson.toJson(bulkLoad));
        if (cargaMasiva.size() > 0) {
            flag = true;
            workbook.close();
            files.close();
        } else {
            flag = false;
        }
        workbook.close();
        files.close();
        File borrarFile = new File(ruta);
        if (borrarFile.delete()) {
            System.out.println("se borro");
        } else {
            System.out.println("no se borro");
        }
        return flag;
    }

    public Integer existeRol(String rol) {
        Integer existe = null;
        if (roles.size() > 0) {
            for (int i = 0; i < roles.size(); i++) {
                if (roles.get(i).getNombre_rol().trim().equalsIgnoreCase(rol.trim())) {
                    //System.out.println("entra if rol");
                    //System.out.println(roles.get(i).getId_rol());
                    return roles.get(i).getId_rol();
                }
            }
        }

        return existe;
    }
}
