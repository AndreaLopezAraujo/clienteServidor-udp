import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.DatatypeConverter;
 
public class Cliente {
	static String rutaLog;
	static int id;
 
    public static void main(String[] args) {
 
        //puerto del servidor
        final int PUERTO_SERVIDOR = 5000;
        //buffer donde se almacenara los mensajes
        byte[] buffer = new byte[1024];
 
        try {
            //Obtengo la localizacion de localhost
            InetAddress direccionServidor = InetAddress.getByName("localhost");
 
            //Creo el socket de UDP
            DatagramSocket socketUDP = new DatagramSocket();
            id=(int) Math.floor(Math.random()*200+1);
            System.out.println("Cliente numero: "+id);
            String mensaje = " "+id;
 
            //Convierto el mensaje a bytes
            buffer = mensaje.getBytes();
 
            //Creo un datagrama
            DatagramPacket pregunta = new DatagramPacket(buffer, buffer.length, direccionServidor, PUERTO_SERVIDOR);
 
            //Lo envio con send
            System.out.println("Envio el datagrama");
            socketUDP.send(pregunta);
            //Numero de paquetes
            buffer = new byte[6];
            DatagramPacket peticion = new DatagramPacket(buffer, buffer.length);
            socketUDP.receive(peticion);
            mensaje = new String(peticion.getData());
            String ms=mensaje.replace(" ","");
            System.out.println("Numero de paquetes: "+ms);
            double n=Double.parseDouble(ms);
            
            //Preparo la respuesta
            buffer = new byte[100000];
            
 
            //Recibo la respuesta
            socketUDP.receive(peticion);
            mensaje = new String(peticion.getData());
            System.out.println("Recibo el archivo");
            crearArchivo();
            int i=0;
            BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(rutaLog)));
            while(i<n)
            {

            		wr.write(mensaje);
            		socketUDP.receive(peticion);
                    mensaje = new String(peticion.getData());
                    System.out.println("Packet: "+(i+1));
                    i++;
            }
            wr.close();
            System.out.println("Archivo recivido.\n");
            
 
            //Cojo los datos y lo muestro
            /**mensaje = new String(peticion.getData());
            crearArchivo();
            boolean c=false;
            while(!c)
            {
            	int res = mensaje.indexOf("Fin");
            	if(res != -1)
           	 {
           		 c=true;
           	 } 
            	else {
		            	 modificarArchivo(mensaje);
		            	 mensaje="OK";
		            	 buffer = mensaje.getBytes();
		                 pregunta = new DatagramPacket(buffer, buffer.length, direccionServidor, PUERTO_SERVIDOR);
		                 socketUDP.send(pregunta);
		                 buffer = new byte[1024];
		                 peticion = new DatagramPacket(buffer, buffer.length);
		            	 socketUDP.receive(peticion);
		            	 mensaje = new String(peticion.getData());
            	 }
            }*/
            //codigo hash
            mensaje=calcMD5(rutaLog);
            buffer = mensaje.getBytes();
            pregunta = new DatagramPacket(buffer, buffer.length, direccionServidor, PUERTO_SERVIDOR);
            socketUDP.send(pregunta);
 
            //cierro el socket
            System.out.println("Conexion finalizada");
            socketUDP.close();
 
        } catch (SocketException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnknownHostException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 
    }
	private static void crearArchivo() throws IOException {
    	File log = null;
		rutaLog = "./data/archivoRecivido_Cliente"+id+ ".txt";
		   
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