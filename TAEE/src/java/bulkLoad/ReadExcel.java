/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bulkLoad;

import Seguridad.UsuarioVO;
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

    public void init() {
        System.out.println("inicia");
        columnas.add("Nombre");
        columnas.add("Apellidos");
        columnas.add("Correo Electronico");
        columnas.add("Contraseña");
        columnas.add("Rol");
    }

    public boolean leerExcel(String ruta) throws FileNotFoundException, IOException {
        init();
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

                while (flag == 1) {
                    // iterate on cells for the current row
                    Iterator<Cell> cellIterator = row.cellIterator();
                    int contador = 0;
                    while (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();
                        if (columnas.size() > 0) {
                            if (columnas.get(contador).equalsIgnoreCase(cell.toString())) {
                                flag = 1;
                                mensaje = true;

                            } else {
                                flag = 0;
                                mensaje = false;

                                workbook.close();
                                file.close();
                                return mensaje;
                            }

                            contador++;
                        }

                    }
                    if (flag == 1) {
                        flag = 2;
                        leerDatos(ruta);
                    }
                }

                System.out.println();
            }

        }

        workbook.close();
        file.close();
        return mensaje;
    }

    public String leerDatos(String ruta) throws FileNotFoundException, IOException {
        bulkLoad = new ArrayList<>();
        usuarios = new ArrayList<>();
        carga = new BulkLoadVO();
        String mensaje = "";
        FileInputStream file = new FileInputStream(new File(ruta));
        System.out.println("Entra a otro proceso");
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        // we get first sheet
        XSSFSheet sheet = workbook.getSheetAt(0);

        // we iterate on rows
        Iterator<Row> rowIterator = sheet.iterator();

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            if (row.getRowNum() > 0) {
                // iterate on cells for the current row
                Iterator<Cell> cellIterator = row.cellIterator();

                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();

                    switch (cell.getColumnIndex()) {
                        case 0:
                            carga.setNombre(cell.toString());
                            if(carga.getNombre().equals("")){
                               observ+="El campo esta en blanco"+"\n";
                            }else if(carga.getNombre().length()>50){
                                observ+="El tamaño de caracteres es incorrecto"+"\n";
                            }
                                
                            break;
                        case 1:
                            carga.setApellidos(cell.toString());
                            if(carga.getApellidos().equals("")){
                               observ+="El campo esta en blanco"+"\n";
                            }else if(carga.getNombre().length()>50){
                                observ+="El tamaño de caracteres es incorrecto"+"\n";
                            }
                            break;

                        case 2:
                            carga.setCorreo(cell.toString());
                            if(carga.getCorreo().equals("")){
                               observ+="El campo esta en blanco"+"\n";
                            }else if(carga.getNombre().length()>50){
                                observ+="El tamaño de caracteres es incorrecto"+"\n";
                            }
                            break;

                        case 3:
                            carga.setContraseña(cell.toString());
                            if(carga.getContraseña().equals("")){
                               observ+="El campo esta en blanco"+"\n";
                            }else if(carga.getNombre().length()>50){
                                observ+="El tamaño de caracteres es incorrecto"+"\n";
                            }
                            break;

                        case 4:
                            carga.setNombre_rol(cell.toString());
                            if(carga.getNombre_rol().equals("")){
                               observ+="El campo esta en blanco"+"\n";
                            }else if(carga.getNombre().length()>50){
                                observ+="El tamaño de caracteres es incorrecto"+"\n";
                            }
                            break;
                            

                    }
                    carga.setObservaciones(observ);
                     if((carga.getObservaciones().trim()).equals("")){
                            usuarios.add(carga);
                     }
                     bulkLoad.add(carga);

                }

                System.out.println();
            }

        }

        workbook.close();
        file.close();
        return mensaje;
    }
}
