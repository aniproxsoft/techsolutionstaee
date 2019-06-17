/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bulkLoad;

import empresa.EmpresaVO;

/**
 *
 * @author ANIPROXTOART
 */
public class BulkLoadVO extends EmpresaVO {

    private Integer lote;
    private Integer bulk_id;
    private String type = "";
    private Integer value;
    private Integer fails;
    private Integer bulks;
    private Integer updates;
    private Integer inserts;
    private String observaciones;

    public Integer getLote() {
        return lote;
    }

    public void setLote(Integer lote) {
        this.lote = lote;
    }

    public Integer getBulk_id() {
        return bulk_id;
    }

    public void setBulk_id(Integer bulk_id) {
        this.bulk_id = bulk_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public Integer getFails() {
        return fails;
    }

    public void setFails(Integer fails) {
        this.fails = fails;
    }

    public Integer getBulks() {
        return bulks;
    }

    public void setBulks(Integer bulks) {
        this.bulks = bulks;
    }

    public Integer getUpdates() {
        return updates;
    }

    public void setUpdates(Integer updates) {
        this.updates = updates;
    }

    public Integer getInserts() {
        return inserts;
    }

    public void setInserts(Integer inserts) {
        this.inserts = inserts;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

}
