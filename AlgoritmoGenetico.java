package algoritmo_genetico;

import java.util.*;

public class AlgoritmoGenetico {
	private float ganancias[][] = {
			{0.0f, 0.0f, 0.0f, 0.0f},
			{0.28f, 0.25f, 0.15f, 0.20f},
			{0.45f, 0.41f, 0.25f, 0.33f},
			{0.65f, 0.55f, 0.40f, 0.42f},
			{0.78f, 0.65f, 0.50f, 0.48f},
			{0.90f, 0.75f, 0.62f, 0.53f},
			{1.02f, 0.80f, 0.73f, 0.56f},
			{1.13f, 0.85f, 0.82f, 0.58f},
			{1.23f, 0.88f, 0.90f, 0.60f},
			{1.32f, 0.90f, 0.96f, 0.60f},
			{1.38f, 0.90f, 1.00f, 0.60f}
	};
	private Vector<float[][]> poblacion;
	private Vector<float[][]> bitacoraCruzas;
	private float cromosomas[][];
	private float aptitudes[];
	private boolean firstParents = false;
	private boolean firstSons = false;
	private int cruzas[];
	private int puntosCruza[];
	private float padresCruzas[][];
	private float hijosCruzas[][];
	private int mutaciones[];
	private float ejemploMutacion[][];
	private Random random;
	private int g;
	private float pCruza;
	private float pMuta;
	private int n;
	private int c;
	private int t;
	
	public AlgoritmoGenetico(int generaciones, float probCruza, float probMuta, int tamPoblacion, int ciudades){
		t = 0;
		g = generaciones;
		pCruza = probCruza;
		pMuta = probMuta;
		n = tamPoblacion;
		c = ciudades;
		poblacion = new Vector<float[][]>();
		bitacoraCruzas = new Vector<float[][]>();
		cromosomas = new float[n][c+1];
		aptitudes = new float[n];
		cruzas = new int[n];
		puntosCruza = new int[2];
		padresCruzas = new float[4][c+1];
		hijosCruzas = new float[4][c+1];
		mutaciones = new int[n];
		ejemploMutacion = new float[2][c+1];
		inicializar();
		evaluar();
		imprimirCromosomas();
		poblacion.addElement(cromosomas);
		while(t < g){
			t+=1;
			cromosomas = seleccionRuleta();
			cruzas[t] = cruzaUnPunto();
			mutaciones[t] = mutacionUniforme();
			evaluar();
			poblacion.addElement(cromosomas);
			imprimirCromosomas();
		}
		//System.out.println(padresCruzas[0][padresCruzas[0].length]);
		//System.out.println(padresCruzas[2][padresCruzas[2].length]);
	}
	
	private int mutacionUniforme(){
		random = new Random();
		int cant = 0;
		for(int i=0; i<n; i++){
			if(random.nextFloat() > pMuta) continue;
			int pos = random.nextInt(c);
			// Guardar ejemplo de mutación:
			ejemploMutacion[0][0] = t;
			for(int j=1; j<c; j++) ejemploMutacion[0][j] = cromosomas[i][j-1];
			float valor = cromosomas[i][pos];
			float nuevoValor = 0;
			do{
				nuevoValor = random.nextFloat()*10;
			} while((int)valor == (int)nuevoValor);
			cromosomas[i][pos] = nuevoValor;
			// Guardar ejemplo de mutación:
			ejemploMutacion[1][0] = t;
			for(int j=1; j<c; j++) ejemploMutacion[1][j] = cromosomas[i][j-1];
			cant++;
		}
		return cant;
	}
	
	private int cruzaUnPunto(){
		random = new Random();
		int cant = 0;
		for(int i=0; i<n; i++){
			if(random.nextFloat() > pCruza) continue;
			int p1, p2;
			do{
				p1 = random.nextInt(n);
				p2 = random.nextInt(n);
			}while(p1 == p2);
			int r = random.nextInt(c);
			if(r == (c-1)) r -= 1;
			//Guardar Padres:
			if(!firstParents){
				padresCruzas[0][0] = t;
				padresCruzas[1][0] = t;
				for(int j=1; j<c; j++){
					padresCruzas[0][j] = cromosomas[p1][(j-1)];
					padresCruzas[1][j] = cromosomas[p2][(j-1)];
				}
				puntosCruza[0] = (int) r+1;
				firstParents = true;
			} else{
				padresCruzas[2][0] = t;
				padresCruzas[3][0] = t;
				for(int j=1; j<c; j++){
					padresCruzas[2][j] = cromosomas[p1][j];
					padresCruzas[3][j] = cromosomas[p2][j];
				}
				puntosCruza[1] = (int) r+1;
			}
			// Hijos
			for(int j=(r+1); j<c; j++){
				float temp = cromosomas[p1][j];
				cromosomas[p1][j] = cromosomas[p2][j];
				cromosomas[p2][j] = temp;
			}
			// Guardar Hijos:
			if(!firstSons){
				hijosCruzas[0][0] = t;
				hijosCruzas[1][0] = t;
				for(int j=1; j<c; j++){
					hijosCruzas[0][j] = cromosomas[p1][(j-1)];
					hijosCruzas[1][j] = cromosomas[p2][(j-1)];
				}
				firstSons = true;
			} else{
				hijosCruzas[2][0] = t;
				hijosCruzas[3][0] = t;
				for(int j=0; j<c; j++){
					hijosCruzas[2][j] = cromosomas[p1][j];
					hijosCruzas[3][j] = cromosomas[p2][j];
				}
			}
			cant++;
		}
		return cant;
	}
	
	private float[][] seleccionRuleta(){
		float temp[][] = new float[n][c+1];
		float sumaAptitudes = calcularSumaAptitudes();
		float sumaVE = 0;
		random = new Random();
		for(int i=0; i<n; i++)
			sumaVE += (cromosomas[i][c])/(sumaAptitudes/n);
			//sumaVE += (float)(aptitudes[i])/(sumaAptitudes/n);
		for(int i=0; i<n; i++){
			float r = random.nextFloat()*sumaVE;
			System.out.println("Random r = "+r);
			float suma = 0;
			for(int j=0; j<n; j++){
				suma += cromosomas[j][c]/(sumaAptitudes/n);
				//suma+=(float) aptitudes[j]/(sumaAptitudes/n);
				if(suma >= r){
					System.out.println("suma (Entro) = "+suma);
					for(int k=0; k<c; k++){
						temp[i][k] = cromosomas[j][k];
					}
					break;
				}
			}
		}
		return temp;
	}
	
	private float calcularSumaAptitudes(){
		float sumaApt = 0;
		for(int i=0; i<n; i++){
			sumaApt += cromosomas[i][c];
			//sumaApt += aptitudes[i];
		}
		return sumaApt;
	}
	
	private void evaluar(){
		for(int i=0; i<n; i++){
			cromosomas[i][c] = calcularAptitud(i); 
			//aptitudes[i] = calcularAptitud(i);
		}
	}
	
	private float calcularAptitud(int individuo){
		float aptitud = 0;
		float sumaGanancias = 0;
		float v = 0;
		for(int i=0; i<c; i++){
			sumaGanancias += calcularGanancias(cromosomas[individuo][i],i);
			v += (int)cromosomas[individuo][i];
		}
		v = Math.abs(v-10);
		aptitud = sumaGanancias / (500*v+1);
		return aptitud;
	}
	
	private float calcularGanancias(float cromosoma,int col){
		float cr = ganancias[(int)cromosoma][col];
		return cr;
	}
	
	private void inicializar(){
		random = new Random();
		for(int i=0; i<n; i++){
			for(int j=0; j<=c; j++){
				cromosomas[i][j] = (j == c ? 0 : random.nextFloat()*10);
			}
			mutaciones[i] = cruzas[i] = 0;
		}
	}
	
	private void imprimirCromosomas(){
		for(int i=0; i<n; i++){
			System.out.print("( "+(i+1)+" ) [");
			for(int j=0; j<c; j++){
				System.out.print("- "+(int)cromosomas[i][j]);
				if(j==(c-1)){
					System.out.println("] - ["+cromosomas[i][c]+"]");
				}
			}
		}
	}
	
	public float[][] getEjemploMutacion(){
		return ejemploMutacion;
	}
	
	public int[] getPuntosCruza(){
		return puntosCruza;
	}
	
	public float[][] getHijosCruzas(){
		return hijosCruzas;
	}
	
	public float[][] getPadresCruzas(){
		return padresCruzas;
	}
	
	public int[] getNumMutas(){
		return mutaciones;
	}
	
	public int[] getNumCruzas(){
		return cruzas;
	}
	
	public Vector getPoblacion(){
		return poblacion;
	}
}
