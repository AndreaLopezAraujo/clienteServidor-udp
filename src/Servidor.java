import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.DatatypeConverter;
 
public class Servidor {
	static int numero;
	static int archivo;
	static int usuarios;
	static String rutaLog;
	static String FILE100 = "./data/100MiB.txt";
	static String FILE250 = "./data/250MiB1.txt";
 
    public static void main(String[] args) {
 
        final int PUERTO = 5000;
        byte[] buffer = new byte[1024];
        usuarios=0;
        archivo= (int) Math.floor(Math.random()*3+1);
        numero=(int) Math.floor(Math.random()*25+1);
        try {
            System.out.println("Iniciado el servidor UDP");
            System.out.println("--------------------------------------------------");
            System.out.println("Archivo a enviar: "+ decirArchivo());
            System.out.println("El archivo se va a enviar a "+  numero+" clientes");
            crearLog();
            //Siempre atendera peticiones
            while (usuarios<26) {
            	//Creacion del socket
                DatagramSocket socketUDP = new DatagramSocket(PUERTO);
            	//limpiar en buffer
            	buffer = new byte[1024];
                //Preparo la respuesta
                DatagramPacket peticionC = new DatagramPacket(buffer, buffer.length);
                 
                //Recibo el datagrama
                socketUDP.receive(peticionC);
                usuarios++;
                System.out.println("--------------------------------------------------");
                System.out.println("Recibo la informacion del cliente");
                long tiempoInicial = System.currentTimeMillis();
                
                //Convierto lo recibido y mostrar el mensaje
                String nombreDelCliente = new String(peticionC.getData());
                System.out.println("Cliente: "+nombreDelCliente);
                //Obtengo el puerto y la direccion de origen
                int puertoCliente = peticionC.getPort();
                InetAddress direccion = peticionC.getAddress();
                //Enviar Archivo
                String mensaje = "¡Hola mundo desde el servidor!";
                //hash del archivo
                String h = EnviarArchivo();
                buffer = mensaje.getBytes();
                //creo el datagrama
                DatagramPacket respuesta = new DatagramPacket(buffer, buffer.length, direccion, puertoCliente);
                
                //Envio la información
                System.out.println("Envio la informacion del cliente");
                socketUDP.send(respuesta);
                socketUDP.receive(peticionC);
                mensaje=new String(peticionC.getData());
                long tiempoFinal = System.currentTimeMillis();
                cambiarLog(nombreDelCliente,tiempoInicial,tiempoFinal,h,mensaje);
                usuarios--;
                numero--;
                
                System.out.println("Faltan "+ numero+" clientes por enviar archivo");
                if(numero<1)
                {
                	 System.out.println("--------------------------------------------------");
                	System.out.println("Se debe elegir nuevo archivo a enviar");
                	numero=(int) Math.floor(Math.random()*25+1);
                	archivo= (int) Math.floor(Math.random()*3+1);
                	System.out.println("Archivo a enviar: "+ decirArchivo());
                	System.out.println("El archivo se va a enviar a "+  numero+" clientes");
                }
                socketUDP.close();
            }
 
        } catch (SocketException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 
    }

	private static void cambiarLog(String cl,long in,long fin,String h1,String h2) throws IOException {
		File log = null;
		log = new File(rutaLog);
		FileWriter fw =  new FileWriter(log.getAbsoluteFile(), true);
		PrintWriter pw = new PrintWriter(fw);
		pw.println("--------------------------------------------------");
		pw.println("Cliente: "+cl+"/n");
		if(archivo<2)
		{
			pw.println("Archvo enviado: "+"FILE100");
		}
		else
		{
			pw.println("Archvo enviado: "+"FILE250");
		}
		
		pw.println("Tiempo de transferencia desde el servidor: "+(fin-in)+"ms");
		if(h1.equals(h2))
		{
			pw.println("El mensaje se recibio corectamente.");
		}
		else {
			pw.println("El mensaje no se recibio correctamente");
		}
		fw.close();
		
	}

	private static String EnviarArchivo() throws Exception {
		String ruta=decirArchivo();
		File file;
		file = new File(ruta);
		return calcMD5(ruta);
	}

	private static String decirArchivo() {
		if(archivo<2)
		{
			return FILE100;
		}
		return FILE250;
	}

	private static void crearLog() throws IOException {
		DateFormat df = new SimpleDateFormat("dd-MM-yy_HH-mm-ss"); 
        long miliSec = System.currentTimeMillis();
        Date current = new Date(miliSec);
		File log = null;
		rutaLog = "./data/prueba_"+ df.format(current) + ".txt";
		   
        log = new File(rutaLog);
        if (!log.exists()) {
            log.createNewFile();
        }
        FileWriter fw = new FileWriter(log);
        fw.close();
        }
	public static String calcMD5(String path) throws Exception {
        byte[] buffer = new byte[8192];
        MessageDigest md = MessageDigest.getInstance("MD5");

        DigestInputStream dis = new DigestInputStream(new FileInputStream(new File(path)), md);
        try {
            while (dis.read(buffer) > 0);
        }finally{
            dis.close();
        }

        byte[] bytes = md.digest();

        return DatatypeConverter.printBase64Binary(bytes);
	}
	
	}

	
 