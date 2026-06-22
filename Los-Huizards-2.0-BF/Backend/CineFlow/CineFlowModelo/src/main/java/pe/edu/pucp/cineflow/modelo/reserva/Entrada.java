package pe.edu.pucp.cineflow.modelo.reserva;



public class Entrada{ 
	private int IdEntrada;
	private double precioBase;
	private tipoEntrada tipo;
	
	public Entrada() {}

	public Entrada(int Identrada, double precioBase, tipoEntrada tipo){
	this.IdEntrada = Identrada;
	this.precioBase = precioBase;
	this.tipo = tipo;
	}
	
	public int getIdEntrada(){
		return IdEntrada;
	}
	
	public void setIdEntrada(int Identrada){
			this.IdEntrada = Identrada;
	}
	
	public double getPrecioBase(){
		return precioBase;
	}
	
	public void setPrecioBase(double precioBase){
			this.precioBase = precioBase;
	}
	
	public tipoEntrada getTipo(){
		return tipo;
	}
	
	public void setTipo(tipoEntrada tipo){
			this.tipo = tipo;
	}
	
	public double calcularPrecio(){
		double precio = precioBase;
		if (tipo == tipoEntrada.NINO) {
			precio *= 0.7;
		} else if (tipo == tipoEntrada.CONADIS) {
			precio *= 0.5;
		}
		return precio;
	}
}
