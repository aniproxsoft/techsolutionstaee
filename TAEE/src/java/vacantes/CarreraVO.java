/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vacantes;

/**
 *
 * @author ANIPROXTOART
 */
public class CarreraVO {
	
private Integer id_carrera;
private Integer id_nivel;
private String carrera_desc;

    public Integer getId_carrera() {
        return id_carrera;
    }

    public void setId_carrera(Integer id_carrera) {
        this.id_carrera = id_carrera;
    }

    public Integer getId_nivel() {
        return id_nivel;
    }

    public void setId_nivel(Integer id_nivel) {
        this.id_nivel = id_nivel;
    }

    public String getCarrera_desc() {
        return carrera_desc;
    }

    public void setCarrera_desc(String carrera_desc) {
        this.carrera_desc = carrera_desc;
    }
}
