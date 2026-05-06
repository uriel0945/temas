package com.example.temas;

class Worker {
    final long id;
    final String nombre;
    final String apellidos;
    final int edad;
    final String oficio;
    final String descripcionOficio;
    final String numeroContacto;

    Worker(long id, String nombre, String apellidos, int edad, String oficio,
            String descripcionOficio, String numeroContacto) {
        this.id = id;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.edad = edad;
        this.oficio = oficio;
        this.descripcionOficio = descripcionOficio;
        this.numeroContacto = numeroContacto;
    }
}
