package Contenido;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.google.gson.Gson;

//El cliente debe poder conectarse al servidor de directorios para pedir un archivo
//El cliente debe tener la opción de crear un archivo de 10kb como máximo
//El cliente debe tener la opción de elegir un archivo y modificarlo
//El cliente debe tener la opción de ver el contenido de un archivo
//El cliente debe tener la opción de eliminar un archivo


public class Cliente {
	
	private final static Logger log = LoggerFactory.getLogger(Cliente.class);
	public static BufferedReader entrada;
	public static PrintWriter salida;
	private static ObjectOutputStream Object;
	private String mensaje;
	private Scanner miScanner = new Scanner(System.in);
	private static ArrayList<String> recursos;
	private String carpComp;
	private Gson gson;
	private static ArrayList<String> listaS;
	private static Socket s;
	private static int puertoS;

		public Cliente(ArrayList<String> listaS) {
			this.listaS = listaS;
			int thread = (int) Thread.currentThread().getId();
			String packetName = Cliente.class.getSimpleName().toString()+thread;
			MDC.put("log.name",packetName);
		}
			
		
		public static boolean conectarse(int idS) {
			
			int puerto;
			String IpS;
			boolean conectado = false;
			while((conectado!=true) && (idS<=listaS.size()-1)) {
				try {
					
					String division[] = listaS.get(idS).split(":");
					IpS = division[0];
					puerto = Integer.parseInt(division[1]);
					s = new Socket(IpS,puerto);
					log.info("Conectandose al server de archivos");
					entrada = new BufferedReader (new InputStreamReader (s.getInputStream()));
					salida = new PrintWriter (s.getOutputStream(), true);
					Object = new ObjectOutputStream(s.getOutputStream());
					s.setSoTimeout(1000);
					conectado = true;
						}catch (IOException e){
							log.info("Intentos de conexion "+idS);	
							++idS;
					}
			}
		return conectado;
	}
		
		
		public ArrayList<String> listaRecursos(String ruta){
			carpComp = ruta;
			File dir = new File(ruta);
			ArrayList<String> recursos = new ArrayList<String>();
			String[] archivos = dir.list();
			if(archivos==null) {
				System.out.println("Sin archivos");
			}else {
				for (String recu : archivos) {
					File dir2 = new File(ruta+"/"+recu);
						if(!dir2.isDirectory()) {
							recursos.add(recu);
						}	
				}
			}
		return recursos;
		}

		public static void notificar() throws InterruptedException{
			salida.println("1");
			salida.println(puertoS);
				try {
						Thread.sleep(2000);
						Object.writeObject(recursos);
						Object.flush();
						log.info("El envio del archivo fue exitoso");
				} catch (IOException e) {
					log.info("Ha ocurrido un error en el envio del archivo");
					conectarse(0);
		}
	}

		public void preguntarPorExistenciaDeArchivo() {
			
		}
		
		
				
		public void inicio(int IdS) throws InterruptedException {
		
			if (conectarse(IdS)){
			
				String division[] = listaS.get(listaS.size()-1).split(":");
				Random aleatorio = new Random();
				int r=aleatorio.nextInt(100);
				puertoS =  Integer.parseInt(division[1])+r;
				System.out.println("");
				System.out.println("El nodo ha sido iniciado!");
				System.out.println("Indique la ruta a la carpeta que contiene los recursos compartidos: ");
				mensaje = miScanner.nextLine();
				ArrayList<String> recurso = listaRecursos(mensaje);
				
				if(!recurso.isEmpty()) {
					System.out.print("Recursos Disponibles: ");
					for (String string : recurso) {
						System.out.print(string+", ");
					}
					System.out.println("");
					recursos = recurso;
					notificar();
				}
				iniciarServerSide(puertoS,recursos);
				iniciarConexion();
				menuCliente();
			}else {
				log.info("Error, la conexion con el servidor de archivos se ha caido");
			}
		}
		
		public static void iniciarConexion() {
			Conexion c = new Conexion();
			Thread cThread = new Thread(c);
			cThread.start();
		}
		
		public void menuCliente() {
			int opcion = 0;
			
			while(opcion != 4) {
				System.out.println("***********Menu Principal*********");
				System.out.println("1. Leer archivo             ");
				System.out.println("2. Editar archivo     ");
				System.out.println("3. Preguntar por la existencia de archivo");
				System.out.println("4. Salir                        ");
				System.out.println("------------------------------------");
				System.out.println("Opcion======>");
				try {
					opcion = Integer.parseInt(miScanner.nextLine().trim());
					
					switch (opcion) {
					case 1:
						preguntarPorExistenciaDeArchivo();
					break;
					
					case 2:
						System.out.println(recursos);
						break;
						
					case 3:
						
						break;
						
					case 4:
						salida.println("4");
						break;
					default:
						System.out.println("Opcion Invalida!");
						break;
					}
				} catch (NumberFormatException e) {
					System.out.println("Error, intente nuevamente");
				}
			}
		}
		
		public void iniciarServerSide(int puerto,ArrayList<String> recursos) {
			ServidorArchivo1 servidor = new ServidorArchivo1(puerto, recursos, carpComp);
			Thread servidorThread = new Thread(servidor);
			servidorThread.start();
		}
		
	}
