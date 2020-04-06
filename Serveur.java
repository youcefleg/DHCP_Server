import java.net.*;
import java.io.*;
/** 

CREATED BY Youcef LEG On 14/12/2019
*/
public class Serveur {
	public static void main(String args[]) throws Exception {
		int nbr = 0;
		DatagramSocket socket=new DatagramSocket(50068);
		while(nbr<5) {
			Thread_Service T_S = new Thread_Service(socket,nbr);
			T_S.start();
			nbr=nbr+1;
		}
	}
}
class Thread_Service extends Thread {

	final String discover =  "DHCP-Discover";
	final String offer =  "DHCP-Offer";
	final String request =  "DHCP-request";
	final String ack =  "DHCP-Acknowledgement";
	String plage[][] = {
{"192.168.1.1","192.168.2.1","192.168.3.1","192.168.4.1","192.168.5.1"},
{"192.168.1.2","192.168.2.2","192.168.3.2","192.168.4.2","192.168.5.2"},
{"192.168.1.3","192.168.2.3","192.168.3.3","192.168.4.3","192.168.5.3"},
{"192.168.1.4","192.168.2.4","192.168.3.4","192.168.4.4","192.168.5.4"},
{"192.168.1.5","192.168.2.5","192.168.3.5","192.168.4.5","192.168.5.5"},
{"192.168.1.6","192.168.2.6","192.168.3.6","192.168.4.6","192.168.5.6"},
{"192.168.1.7","192.168.2.7","192.168.3.7","192.168.4.7","192.168.5.7"},
{"192.168.1.8","192.168.2.8","192.168.3.8","192.168.4.8","192.168.5.8"},
{"192.168.1.9","192.168.2.9","192.168.3.9","192.168.4.9","192.168.5.9"},
{"192.168.1.10","192.168.2.10","192.168.3.10","192.168.4.10","192.168.5.10"}};


	String bail[][] = {
{"1","3","8","4","2"},
{"8","7","9","5","1"},
{"6","4","2","1","9"},
{"5","1","6","5","9"},
{"4","7","9","8","3"},
{"2","5","8","1","4"},
{"6","1","9","8","4"},
{"9","3","5","2","1"},
{"3","8","7","6","5"}};
	
	int numero;
	int i=0;
	DatagramSocket socket;
	Thread_Service(DatagramSocket s,int nbr){
		socket = s;
		numero = nbr;
	}

	public void run(){
		try {
			while(true){
			System.out.println("\nServeur ["+(numero+1)+ "] est en ligne...\n");
			byte buffer[] = new byte[1024];
			DatagramPacket req = new DatagramPacket(buffer,buffer.length);
			socket.receive(req);
			String m = new String(req.getData());
			//Si c'est un DHCP-Discover
			if(m.startsWith(discover)){
				System.out.println("***Serveur ["+(numero+1)+"] a detecter un : "+ m + " depuis le port: " +req.getPort()+"\n-->"+offer+ " envoié");
				//Envoie du DHCP-OFFER
				String x = offer + ":\n" +"Adresse : "+plage[i][numero]+ "\n"+"Bail : "+bail[i][numero]+ "\nDns : "+plage[9][numero]+"\nPasserelle : "+plage[9][numero]+" "+numero +" "+i; 
				req.setData(x.getBytes());
				req.setLength(x.length());
				socket.send(req);
			}
			//Si c'est un DHCP-Request
			else if(m.startsWith(request)){
				String v = m.substring(12,23);//recuperer l'adresse
				String num = m.substring(24,25);//recuperer le num de serveur qui a envoyé
				String ii = m.substring(26,27);//recuperer le i de serveur qui a envoyé

				numero = Integer.parseInt(num);
				i = Integer.parseInt(ii);

				System.out.println("***Serveur ["+(numero+1)+"] "+"Le client dans le port: " +req.getPort()+" a confirmé qu'il veut prend l'adresse: "+v);
				//retardé l'exécution à 4 sec
				try{
    					Thread.sleep(4000);
				}
				catch(InterruptedException ex){
    					Thread.currentThread().interrupt();}

				//vérification que cette adresse ip que le client veut est tjr disponible
				if(plage[i][numero].equals(v)){
					String x = ack + ":\n" +"Adresse IP : "+plage[i][numero]+ "\n"+"Bail : "+bail[i][numero]+ "\nDns :"+plage[9][numero]+"\nPasserelle :"+ plage[9][numero]+"\n "; 
					req.setData(x.getBytes());
					req.setLength(x.length());
					socket.send(req);
					System.out.println("-->"+ack+" envoié");
					i++;
				}
				//sinon cette adresse a été attribue a une autre machine
				else{
					String x = "Désolé, Cette addresse ip n'est plus disponibleee";
					req.setData(x.getBytes());
					req.setLength(x.length());
					socket.send(req);
				}
			}
		}
		}
		catch (IOException e) { System.out.println("Error");}
	}
} 
