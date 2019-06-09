package com.museumspotter.modelos;

import java.util.List;

public class Museum {

    private int id;
    private String nombre;
    private double latitud;
    private double longitud;
    private String direccion;
    private double calificacion;
    private boolean gratis;
    private boolean gratisEstudiantes;
    private boolean gratisDomingo;
    private List<String> categoria;
    private String imagen;

    public Museum() {

    }

    public Museum(int id, String nombre, double latitud, double longitud, String direccion,
                  double calificacion, boolean gratis, boolean gratisEstudiante,
                  boolean gratisDomingo, List<String> categoria, String imagen) {
        this.id = id;
        this.nombre = nombre;
        this.latitud = latitud;
        this.longitud = longitud;
        this.direccion = direccion;
        this.calificacion = calificacion;
        this.gratis = gratis;
        this.gratisEstudiantes = gratisEstudiante;
        this.gratisDomingo = gratisDomingo;
        this.categoria = categoria;
        this.imagen = imagen;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public double getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(double calificacion) {
        this.calificacion = calificacion;
    }

    public boolean isGratis() {
        return gratis;
    }

    public void setGratis(boolean gratis) {
        this.gratis = gratis;
    }

    public boolean isGratisEstudiantes() {
        return gratisEstudiantes;
    }

    public void setGratisEstudiantes(boolean gratisEstudiantes) {
        this.gratisEstudiantes = gratisEstudiantes;
    }

    public boolean isGratisDomingo() {
        return gratisDomingo;
    }

    public void setGratisDomingo(boolean gratisDomingo) {
        this.gratisDomingo = gratisDomingo;
    }

    public List<String> getCategoria() {
        return categoria;
    }

    public void setCategoria(List<String> categoria) {
        this.categoria = categoria;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
}
