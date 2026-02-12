package com.example.lo_linking_park.model;

import java.util.Date;

public class Payment {
    private String id;
    private String sessionId;
    private String usuariId;
    private String dadesBancariesId;
    private int tempsTotalMinuts;
    private double tarifaPerHora;
    private double importTotal;
    private String estatPagament; // "pendent", "processat", "completat", "fallat", "reemborsat"
    private Date dataPagament;
    private String referenciaTransaccio;
    private String notes;
    private Date creatEl;
    private Date actualitzatEl;

    // Constructor vacío requerido por Firestore
    public Payment() {
        this.estatPagament = "pendent";
    }

    public Payment(String sessionId, String usuariId, String dadesBancariesId,
                   int tempsTotalMinuts, double tarifaPerHora, double importTotal) {
        this.sessionId = sessionId;
        this.usuariId = usuariId;
        this.dadesBancariesId = dadesBancariesId;
        this.tempsTotalMinuts = tempsTotalMinuts;
        this.tarifaPerHora = tarifaPerHora;
        this.importTotal = importTotal;
        this.estatPagament = "pendent";
        this.dataPagament = new Date();
        this.creatEl = new Date();
        this.actualitzatEl = new Date();
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getUsuariId() {
        return usuariId;
    }

    public void setUsuariId(String usuariId) {
        this.usuariId = usuariId;
    }

    public String getDadesBancariesId() {
        return dadesBancariesId;
    }

    public void setDadesBancariesId(String dadesBancariesId) {
        this.dadesBancariesId = dadesBancariesId;
    }

    public int getTempsTotalMinuts() {
        return tempsTotalMinuts;
    }

    public void setTempsTotalMinuts(int tempsTotalMinuts) {
        this.tempsTotalMinuts = tempsTotalMinuts;
    }

    public double getTarifaPerHora() {
        return tarifaPerHora;
    }

    public void setTarifaPerHora(double tarifaPerHora) {
        this.tarifaPerHora = tarifaPerHora;
    }

    public double getImportTotal() {
        return importTotal;
    }

    public void setImportTotal(double importTotal) {
        this.importTotal = importTotal;
    }

    public String getEstatPagament() {
        return estatPagament;
    }

    public void setEstatPagament(String estatPagament) {
        this.estatPagament = estatPagament;
    }

    public Date getDataPagament() {
        return dataPagament;
    }

    public void setDataPagament(Date dataPagament) {
        this.dataPagament = dataPagament;
    }

    public String getReferenciaTransaccio() {
        return referenciaTransaccio;
    }

    public void setReferenciaTransaccio(String referenciaTransaccio) {
        this.referenciaTransaccio = referenciaTransaccio;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Date getCreatEl() {
        return creatEl;
    }

    public void setCreatEl(Date creatEl) {
        this.creatEl = creatEl;
    }

    public Date getActualitzatEl() {
        return actualitzatEl;
    }

    public void setActualitzatEl(Date actualitzatEl) {
        this.actualitzatEl = actualitzatEl;
    }
}
