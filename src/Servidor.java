import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
 
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
        archivo= (int) Math.floor(Math.random()*2+1);
        numero=(int) Math.floor(Math.random()*25+1);
        try {
            System.out.println("Iniciado el servidor UDP");
            crearLog();
            //Siempre atendera peticiones
            while (usuarios<26) {
            	//Creacion del socket
                DatagramSocket socketUDP = new DatagramSocket(PUERTO);
            	//limpiar en buffer
            	buffer = new byte[1024];
            	int c=buffer.length;
            	 System.out.println(c);
                //Preparo la respuesta
                DatagramPacket peticionC = new DatagramPacket(buffer, buffer.length);
                 
                //Recibo el datagrama
                socketUDP.receive(peticionC);
                usuarios++;
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
                buffer = mensaje.getBytes();
                System.out.println(c);
                //creo el datagrama
                DatagramPacket respuesta = new DatagramPacket(buffer, buffer.length, direccion, puertoCliente);
                
                //Envio la información
                System.out.println("Envio la informacion del cliente");
                System.out.println(respuesta.getData());
                socketUDP.send(respuesta);
                System.out.println("3"+c);
                long tiempoFinal = System.currentTimeMillis();
                cambiarLog(nombreDelCliente,tiempoInicial,tiempoFinal);
                usuarios--;
                socketUDP.close();
            }
 
        } catch (SocketException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
 
    }

	private static void cambiarLog(String cl,long in,long fin) throws IOException {
		File log = null;
		log = new File(rutaLog);
		FileWriter fw =  new FileWriter(log.getAbsoluteFile(), true);
		PrintWriter pw = new PrintWriter(fw);
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
		pw.println("-------------------");
		fw.close();
		
	}

	private static void EnviarArchivo() throws IOException {
		String ruta="";
		if(archivo<2)
		{
			ruta=FILE100;
		}
		else
		{
			ruta=FILE250;
		}
		File file;
		file = new File(ruta);
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
	}
	
 