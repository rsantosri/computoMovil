package com.museumspotter.modelos;

public class User {

    private int id;
    private String correo;
    private boolean estudiante;
    private boolean soloMuseosGratis;
    private boolean distanciaCaminable;
    private String tagBusqueda;


    public User() {
    }

    public User(int id, String correo, boolean estudiante, boolean soloMuseosGratis, boolean distanciaCaminable, String tagBusqueda) {
        this.id = id;
        this.correo = correo;
        this.estudiante = estudiante;
        this.soloMuseosGratis = soloMuseosGratis;
        this.distanciaCaminable = distanciaCaminable;
        this.tagBusqueda = tagBusqueda;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public boolean isEstudiante() {
        return estudiante;
    }

    public void setEstudiante(boolean estudiante) {
        this.estudiante = estudiante;
    }

    public boolean isSoloMuseosGratis() {
        return soloMuseosGratis;
    }

    public void setSoloMuseosGratis(boolean soloMuseosGratis) {
        this.soloMuseosGratis = soloMuseosGratis;
    }

    public boolean isDistanciaCaminable() {
        return distanciaCaminable;
    }

    public void setDistanciaCaminable(boolean distanciaCaminable) {
        this.distanciaCaminable = distanciaCaminable;
    }

    public String getTagBusqueda() {
        return tagBusqueda;
    }

    public void setTagBusqueda(String tagBusqueda) {
        this.tagBusqueda = tagBusqueda;
    }
}
