/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bulkLoad;

import Seguridad.RolVO;
import Seguridad.UsuarioVO;
import com.google.gson.Gson;
import empresa.CiudadVO;
import empresa.EstadoVO;
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
    private List<BulkLoadVO> empresas;
    private String observ = "";
    private BulkLoadVO carga;
    Gson gson = new Gson();
    List<String> columnasRead;
    BulkLoadDao dao = new BulkLoadDaoImplements();
    List<EstadoVO> estados = new ArrayList<EstadoVO>();
    List<CiudadVO> ciudades = new ArrayList<>();
    List<BulkLoadVO> cargaMasiva = new ArrayList<>();

    public void init() {
        columnas = new ArrayList<>();
//        System.out.println("inicia");
        columnas.add("Nombre");
        columnas.add("Direccion");
        columnas.add("Estado");
        columnas.add("Ciudad");
        columnas.add("Codigo Postal");
        columnas.add("Telefono");
        columnas.add("Folio de convenio");
        columnas.add("RFC de la Empresa");
        columnas.add("Correo de Empresa");
        ciudades = dao.getCiudades();
        estados = dao.getEstados();

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
        empresas = new ArrayList<>();

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

                            carga.setNombre_empresa(cell.toString());

                            break;
                        case 1:

                            carga.setDireccion(cell.toString());

                            break;

                        case 2:

                            carga.setNombre_estado(cell.toString());

                            break;

                        case 3:

                            carga.setNombre_ciudad(cell.toString());

                            break;

                        case 4:
                            cell.setCellType(Cell.CELL_TYPE_STRING);
                            String cp;

                            cp = cell.toString();

                            carga.setCodigo_postal(cp);

                            break;
                        case 5:
                            String tel = "";
                            cell.setCellType(Cell.CELL_TYPE_STRING);

                            tel = cell.toString();

                            System.out.println("***************c" + tel);
                            carga.setTelefono(tel);
                            break;
                        case 6:

                            carga.setConvenio(cell.toString());

                            break;
                        case 7:

                            carga.setRfc(cell.toString());

                            break;
                        case 8:
                            carga.setCorreo_empresa(cell.toString());
                            break;

                    }

                }
                bulkLoad.add(carga);

            }

        }

        for (int i = 0; i < bulkLoad.size(); i++) {
            observ = "";
            Integer estado_id = existeEstado(bulkLoad.get(i).getNombre_estado());
            Integer ciudad_id = null;
            if (bulkLoad.size() > 0) {
                if (bulkLoad.get(i).getNombre_empresa().equals("") || bulkLoad.get(i).getNombre_empresa() == null) {
                    observ += "El campo Nombre esta en blanco" + ". ";
                } else if (bulkLoad.get(i).getNombre_empresa().length() > 50) {
                    observ += "El campo Nombre excede numero de caracteres (50)" + ". ";
                }
                if (bulkLoad.get(i).getDireccion().equals("") || bulkLoad.get(i).getDireccion() == null) {
                    observ += "El campo Direccion esta en blanco" + ". ";
                } else if (bulkLoad.get(i).getDireccion().length() > 200) {
                    observ += "El campo Direccion excede numero de caracteres (200)" + ". ";
                }
                if (bulkLoad.get(i).getNombre_estado().equals("") || bulkLoad.get(i).getNombre_estado() == null) {
                    observ += "El campo Estado esta en blanco" + ". ";
                } else if (bulkLoad.get(i).getNombre_estado().length() > 50) {
                    observ += "El campo Estado excede numero de caracteres (50)" + ". ";
                } else if (estado_id == null) {
                    observ += "El estado no existe en la base de datos" + ".";
                } else if (estado_id > 0) {
                    bulkLoad.get(i).setId_estado(estado_id);
                    ciudad_id = existeCiudad(bulkLoad.get(i).getNombre_ciudad(), estado_id);
                }

                if (bulkLoad.get(i).getNombre_ciudad().equals("") || bulkLoad.get(i).getNombre_ciudad() == null) {
                    observ += "El campo Ciudad esta en blanco" + ". ";
                } else if (bulkLoad.get(i).getNombre_ciudad().length() > 50) {
                    observ += "El campo Ciudad excede numero de caracteres (50)" + ". ";
                } else if (ciudad_id == null) {
                    observ += "La ciudad no existe en la base de datos o esa ciudad no pertenece a el estado elegido" + ".";
                } else if (ciudad_id > 0) {
                    bulkLoad.get(i).setId_ciudad(estado_id);
                }

                if (bulkLoad.get(i).getCodigo_postal().equals("") || bulkLoad.get(i).getCodigo_postal() == null) {
                    observ += "El campo Codigo Postal esta en blanco" + ". ";
                } else if (bulkLoad.get(i).getCodigo_postal().length() > 15) {
                    observ += "El campo Codigo Postal excede numero de caracteres (15)" + ". ";
                }
                if (bulkLoad.get(i).getTelefono().equals("") || bulkLoad.get(i).getTelefono() == null) {
                    observ += "El campo Telefono esta en blanco" + ". ";
                } else if (bulkLoad.get(i).getTelefono().length() > 13) {
                    observ += "El campo Telefono excede numero de caracteres (13)" + ". ";
                }

                if (bulkLoad.get(i).getConvenio().equals("") || bulkLoad.get(i).getConvenio() == null) {
                    observ += "El campo Folio de convenio esta en blanco" + ". ";
                } else if (bulkLoad.get(i).getTelefono().length() > 20) {
                    observ += "El campo Folio de convenio excede numero de caracteres (20)" + ". ";
                }

                if (bulkLoad.get(i).getRfc().equals("") || bulkLoad.get(i).getRfc() == null) {
                    observ += "El campo RFC de la Empresa esta en blanco" + ". ";
                } else if (bulkLoad.get(i).getTelefono().length() > 20) {
                    observ += "El campo RFC de la Empresa excede numero de caracteres (20)" + ". ";
                }
                if(bulkLoad.get(i).getCorreo_empresa().equals("")|| bulkLoad.get(i).getCorreo_empresa()==null){
                    observ += "El campo Correo de Empresa esta en blanco";
                } else if (bulkLoad.get(i).getCorreo_empresa().length() > 50) {
                    observ += "El campo Correo de Empresa  excede numero de caracteres (50)" + ". ";
                }

                if (observ.equals("")) {
                    empresas.add(bulkLoad.get(i));
                } else {
                    bulkLoad.get(i).setObservaciones(observ);
                }
            }

        }
        System.out.println("tamaño empresas: " + empresas.size());
        System.out.println("tamaño bulkload: " + bulkLoad.size());

        System.out.println("empresas:   " + gson.toJson(empresas));
        System.out.println("********************************");
        System.out.println("bulkload:   " + gson.toJson(bulkLoad));
        cargaMasiva = dao.bulkLoad(gson.toJson(empresas), gson.toJson(bulkLoad));
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

    public Integer existeEstado(String estado) {
        Integer existe = null;
        if (estados.size() > 0) {
            for (int i = 0; i < estados.size(); i++) {
                if (estados.get(i).getNombre_estado().trim().equalsIgnoreCase(estado.trim())) {
                    //System.out.println("entra if rol");
                    //System.out.println(roles.get(i).getId_rol());
                    System.out.println("id: " + estados.get(i).getId_estado());
                    return estados.get(i).getId_estado();
                }
            }
        }

        return existe;
    }

    public Integer existeCiudad(String ciudad, Integer estado) {
        Integer existe = null;
        if (ciudades.size() > 0) {
            for (int i = 0; i < ciudades.size(); i++) {
                if (ciudades.get(i).getNombre_ciudad().trim().equalsIgnoreCase(ciudad.trim())) {
                    //System.out.println("entra if rol");
                    //System.out.println(roles.get(i).getId_rol());

                    if (ciudades.get(i).getId_estado() == estado) {
                        return ciudades.get(i).getId_ciudad();
                    }

                }
            }
        }

        return existe;
    }
}
