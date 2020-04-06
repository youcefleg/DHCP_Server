import java.io.*;
import java.net.*;
/** 

*CREATED BY Youcef LEG On 14/12/2019
*/
public class Client {
	

	final static  String discover =  "DHCP-Discover";
	final static String offer =  "DHCP-Offer";
	final static String request =  "DHCP-request";
	final static String ack =  "DHCP-Acknowledgement";
	int i =0;

	public static void main(String argv[]) throws Exception{

		int taille = 1024;
		boolean loop = true;		

		DatagramSocket socket = new DatagramSocket();
		socket.setBroadcast(true);//activer le broadcast pour cette socket
		InetAddress ip = InetAddress.getByName("127.0.0.255");//adresse du recepteur
		byte buffer[] = discover.getBytes();//donnée a envoiyé
		int length = discover.length();//taille de donnée a envoiyé
		System.out.println("\n*** Recherche d'un Serveur DHCP... ***");//message just pour afficher l'etat du client
		Thread.sleep(3000);//retardé le client pour simuler l'etat réel
		DatagramPacket packet = new DatagramPacket(buffer,length, ip ,50068);//preparation du packet pour l'envoyé
		//Envoie du DHCP DISCOVER
		socket.send(packet);

		// reception du flux
		while(loop == true){		//boucler jusqu'a avoir adresse ou recevoir adresse non parfait
			DatagramPacket packet2 = new DatagramPacket(new byte[taille],taille);//préparation packet de reception
			socket.receive(packet2);//recevoir le packet
			String m = new String(packet2.getData());//copier les donnée dans un String
			if(m.startsWith(offer)){
				String showOffer=m.substring(0,88);//afficher l'offre du serveur(juste les données necessaire)
				System.out.println("\n"+"-->Client a reçue un " + showOffer);
    				System.out.println("de la part du serveur qui est : " + packet2.getAddress() 
                       			+ "|" + packet2.getPort() +"\n");//afficher les infos du l'emetteur
				
				String getbail = m.substring(41,42);//extraire du Bail depuis le packet
				int bail = Integer.parseInt(getbail);//convertir le bail en un entier
				if(bail>3){	//prend le bail > 3
					
					String last = m.substring(88,91);//extraire des infos du serveur
					String last2= m.substring(91,92);//extraire des infos du serveur
					String x = request+ m.substring(22,34)+last+last2;//preparation de request
					packet2.setData(x.getBytes());
					packet2.setLength(x.length());
					socket.send(packet2);//emittion de reques
					System.out.println("*** Obtention d'adresse ip... ***\n");//message just pour afficher l'etat du client
				}
				//bail inferieur ou égale à 3
				else{
					System.out.println("-->Bail est un peu petit, configuration non accépté");
					loop=false;//quitter la boucle dans le cas de bail inf à 3
				}
			}
			//réception d'un ACK
			else if(m.startsWith(ack)){
				String config = m.substring(21);//coupé le msg reçu et prend juste la configuration
				Thread.sleep(2000);
				System.out.println("-->Ma nouvelle configuration est :"+config);
				loop=false;//quitter la boucle dans le cas de succes
			}
			else{
				System.out.println(m);
				loop=false;//quitter la boucle dans le cas ou l'adresse est pris par un autre client
			}
		}
		socket.close();//fermer la communication UDP
	}
}
		
