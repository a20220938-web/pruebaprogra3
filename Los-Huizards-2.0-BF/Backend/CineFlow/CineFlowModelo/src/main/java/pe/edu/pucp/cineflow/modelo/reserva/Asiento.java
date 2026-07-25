package pe.edu.pucp.cineflow.modelo.reserva;

import pe.edu.pucp.cineflow.modelo.catalogo.*;

public class Asiento{
	private int idAsiento;
	private char fila;
	private int numero;
	private boolean esConadis;
	private EstadoAsiento estado;
	private Funcion funcion;

	public Asiento() {}

	public Asiento(int idAsiento, char fila, int numero, boolean esConadis, EstadoAsiento estado, Funcion funcion){
		this.idAsiento = idAsiento;
		this.fila = fila;
		this.numero = numero;
		this.esConadis = esConadis;
		this.estado = estado;
		this.funcion = funcion;
	}

	public int getIdAsiento(){
		return idAsiento;
	}

	public void setIdAsiento(int IdAsiento){
		this.idAsiento = IdAsiento;
	}

	public char getFila(){
		return fila;
	}

	public void setFila(char fila){
		this.fila = fila;
	}

	public int getNumero(){
		return numero;
	}

	public void setNumero(int numero){
		this.numero = numero;
	}

	public boolean isEsConadis() { return esConadis; }
	public void setEsConadis(boolean esConadis) { this.esConadis = esConadis; }

	public EstadoAsiento getEstado(){
		return estado;
	}

	public void setEstado(EstadoAsiento estado){
		this.estado = estado;
	}

	public void cambiarEstado(EstadoAsiento nuevoEstado){
		this.estado = nuevoEstado;
	}

	public String obtenerDatosParaReporte() {
		return "Película: " + funcion.getPelicula().getTitulo() +
				" | Asiento: " + fila + numero;
	}

	@jakarta.json.bind.annotation.JsonbTransient
	public Funcion getFuncion() {
		return funcion;
	}
}