/**
 * java -cp udgv.diad.servicio.chat.servidor.jar ChatServer <PuertoLocal> 
 */
package udgv.diad.servicio.chat;

//------------- Impotación de librerías -----------//
import java.net.*;  //Abstracción de la capa y protocolos de red disponibles en Sistema Operativo
import java.io.*;   //Abstracción del sistema de entrada y salida



/**
 * @reviewer 
 * @author MARIANO SOTO MS213239444
 *
 */

@SuppressWarnings("deprecation")
public class ChatServer implements Runnable {  
	

	private ChatServerThread clients[] = new ChatServerThread[50];
	private ServerSocket server = null;
	private Thread       thread = null;
	private int clientCount = 0;  

//--- Método para inicializar el Servidor---//
	@Override
	public void run() {  
		while (thread != null)
		{  try
		    {  System.out.println("Waiting for a client ..."); 
		       addThread(server.accept()); }
		    catch(IOException ioe)
		    {  System.out.println("Server accept error: " + ioe); stop(); }
		 }		
	}
	
	//--- Constructor de la clase --//
	   public ChatServer(int port)  {  
		  try
	      {  System.out.println("Binding to port " + port + ", please wait  ...");
	         server = new ServerSocket(port);  
	         System.out.println("Server started: " + server);
	         start(); }
	      catch(IOException ioe)
	      {  System.out.println("Can not bind to port " + port + ": " + ioe.getMessage()); }
	   }
	   
	//---- comenzar el servicio ----//
	   public void start() {
		   if (thread == null)
		      {  thread = new Thread(this); 
		         thread.start();
		      }
	   }
	   
    //---- comenzar el servicio ----//
	   public void stop() {
		   if (thread != null)
		      {  thread.stop(); 
		         thread = null;
		      }
	   }
	   
	   
	   private int findClient(int ID)
	   {  for (int i = 0; i < clientCount; i++)
	         if (clients[i].getID() == ID)
	            return i;
	      return -1;
	   }

	   public synchronized void handle(int ID, String input)
	   {  if (input.equals(".bye"))
	      {  clients[findClient(ID)].send(".bye");
	         remove(ID); }
	      else
	         for (int i = 0; i < clientCount; i++)
	            clients[i].send(ID + ": " + input);   
	   }
	   
	   public synchronized void remove(int ID)
	   {  int pos = findClient(ID);
	      if (pos >= 0)
	      {  ChatServerThread toTerminate = clients[pos];
	         System.out.println("Removing client thread " + ID + " at " + pos);
	         if (pos < clientCount-1)
	            for (int i = pos+1; i < clientCount; i++)
	               clients[i-1] = clients[i];
	         clientCount--;
	         try
	         {  toTerminate.close(); }
	         catch(IOException ioe)
	         {  System.out.println("Error closing thread: " + ioe); }
	         toTerminate.stop(); }
	   }
	   
	   private void addThread(Socket socket)
	   {  if (clientCount < clients.length)
	      {  System.out.println("Client accepted: " + socket);
	         clients[clientCount] = new ChatServerThread(this, socket);
	         try
	         {  clients[clientCount].open(); 
	            clients[clientCount].start();  
	            clientCount++; }
	         catch(IOException ioe)
	         {  System.out.println("Error opening thread: " + ioe); } }
	      else
	         System.out.println("Client refused: maximum " + clients.length + " reached.");
	   }
	   
	   public static void main(String args[]) { 

		   @SuppressWarnings("unused")
		ChatServer server = null;
		      if (args.length != 1)
		         System.out.println("Usage: java ChatServer port");
		      else
		         server = new ChatServer(Integer.parseInt(args[0]));
	   }
	   
	   

	//-------------- Medios de acceso a atributos -----------------------//	   

	public ChatServerThread[] getClients() {
		return clients;
	}

	public void setClients(ChatServerThread[] clients) {
		this.clients = clients;
	}

	public ServerSocket getServer() {
		return server;
	}

	public void setServer(ServerSocket server) {
		this.server = server;
	}

	public Thread getThread() {
		return thread;
	}

	public void setThread(Thread thread) {
		this.thread = thread;
	}

	public int getClientCount() {
		return clientCount;
	}

	public void setClientCount(int clientCount) {
		this.clientCount = clientCount;
	}
	   
	   
}
