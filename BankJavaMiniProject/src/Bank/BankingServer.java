package Bank;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serial;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Observer;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class BankingServer {
	static ArrayList<ClientBancar>BankClients = new ArrayList<>();
	static Socket socketFisk;
	public static void main(String[] args) throws Exception {

		ServerSocket serverSocket = new ServerSocket(999);
		ServerSocket serverSocket2 = new ServerSocket(998);
		
		Thread fiskTh = new Thread(new Runnable() {	
			@Override
			public void run() {
				try {
					socketFisk =  serverSocket2.accept();
					InputStream inp=socketFisk.getInputStream();
					OutputStream out = socketFisk.getOutputStream();	
					while(true) {
					while(socketFisk.isConnected() && socketFisk.isClosed() == false)
					{
						if(inp.available() > 0)
						{
							
								byte[] message= new byte[inp.available()];
								byte[] resp;
								inp.read(message,0,inp.available());
								resp = parseMessageFisk(message);
								System.out.println(new String(resp));
								out.write(resp);
						}
					}}								
					} catch (Exception e) {
					// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}

		private byte[] parseMessageFisk(byte[] message) throws Exception {
			byte[] resp = new byte[0];
			try
			{
			String[] fields = (new String(message)).split(":");
			Optional<ClientBancar> clientFinder ;
			switch(fields[0])
			{
			case "START_URMARIT":
				System.out.println(fields[1]);
				clientFinder = BankClients.stream().findFirst().filter(p-> p.getCNP().equals(new BigInteger(fields[1])));
				if(clientFinder.isPresent())
				{
					resp = "Urmarire inceputa!".getBytes();
					clientFinder.get().register(socketFisk);
				}
				else
				{
					resp = "Clientul nu a fost gasit!".getBytes();
				}
				break;
			case "STOP_URMARIT":
				clientFinder = BankClients.stream().findFirst().filter(p-> p.getCNP().equals(new BigInteger(fields[1])));
				if(clientFinder.isPresent())
				{
					resp = "Urmarire terminata!".getBytes();
					clientFinder.get().unregister();
				}
				else
				{
					resp = "Clientul nu a fost gasit!".getBytes();
				}
				break;
			default:
				throw new Exception("Invalid request type");
			}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
			
			return resp;
		}});
		fiskTh.start();
		
		while(true) {
			Socket socket =  serverSocket.accept();
			InputStream inp=socket.getInputStream();
			OutputStream out = socket.getOutputStream();
			while(socket.isConnected() && socket.isClosed() == false)
			{
				if(inp.available() > 0)
				{
					byte[] message= new byte[inp.available()];
					inp.read(message,0,inp.available());
					byte[] resp = parseMessageClient(message);
					System.out.println(new String(message));
					out.write(resp);
				}
			}
			
		}
	

}
private static byte[] parseMessageClient(byte[] message) throws Exception {
	String[] fields = (new String(message)).split(":");
	byte[] resp = new byte[0];
	try {
		switch(fields[0])
		{
		case "CREARE_CONT":
			if(fields.length != 11)	
				throw new Exception("Invalid number of fields");
			BigInteger nrContRON = getNewContNumber(Moneda.RON);
			BigInteger nrContEURO = getNewContNumber(Moneda.EURO);
			ClientBancar client = new ClientBancar(fields[2],fields[8],new BigInteger(fields[4]),new BigInteger(fields[6]), nrContRON, nrContEURO, Integer.valueOf(fields[10]));
			BankClients.add(client);
			resp = ("Accepted:New account nr:"+nrContRON+" and " + nrContEURO).getBytes();
			break;
		case "LOGIN_CONT":
			if(fields.length != 5)	
				throw new Exception("Invalid number of fields");
			resp = LoginCont(new BigInteger(fields[2]),Integer.valueOf(fields[4]));
			break;
		case "TRANZACTIE":
			if(fields.length != 9)	throw new Exception("Invalid number of fields");
			ClientBancar clientFound = 
					(ClientBancar) BankClients.stream().filter(new Predicate<ClientBancar>() {
						@Override
						public boolean test(ClientBancar t) {
							if(Moneda.RON == Moneda.valueOf(fields[6]) && (new BigInteger(fields[2]).equals(t.contRON.getContNr())))
								return true;
							if(Moneda.EURO == Moneda.valueOf(fields[6]) && (new BigInteger(fields[2]).equals(t.contEURO.getContNr())))
								return true;
							return false;
						}
					}).toArray()[0];

			boolean result = false;
			double valuta = Double.valueOf(fields[4]);
			if(!(fields[8].equals("BANK") || fields[8].equals("CLIENT") || fields[8].equals("TRANSFER"))) throw new Exception("Operatie necunoscuta");
			if(fields[8].equals("CLIENT")) valuta =-valuta;

			if(Moneda.RON == Moneda.valueOf(fields[6])){
				result = clientFound.contRON.AddDeposit(clientFound.contRON.getContNr(),Moneda.valueOf(fields[6]),valuta);}
			else {
				result = clientFound.contEURO.AddDeposit(clientFound.contEURO.getContNr(),Moneda.valueOf(fields[6]),valuta);}
			resp = LoginCont(clientFound.getCNP(),clientFound.getPassword());
			if(result == false) throw new Exception("Operatie esuata");
			break;

		case "INCHIDE_CONT":
			if(fields.length != 1)	throw new Exception("Invalid number of fields");
			if(clientLogat.contEURO.calcualteSold()!=0 || clientLogat.contRON.calcualteSold()!=0)
				resp = "Sold nenul operatie esuata".getBytes();
			else 
			{
				BankClients.remove(clientLogat);
				resp ="Accept".getBytes();
			}

			break;
		default:
			throw new Exception("Invalid request type");
		}
	}
	catch(Exception e)
	{
		System.out.println(e.getMessage());
	}
	return resp;
}
static ClientBancar clientLogat;
private static byte[] LoginCont(BigInteger CNP, int passwd) {
	Stream<ClientBancar> clientFinder = BankClients.stream().filter(p -> p.getCNP().equals(CNP) && p.getPassword() == passwd );
	BankClients.forEach(p-> {if(p.getCNP().equals(CNP) && p.getPassword() == passwd )
		clientLogat = p;});
	if(clientFinder.count() ==0)
		return "Numar cont sau parola invalida".getBytes();
	else
		return ("Accepted:"+clientLogat.toString()).getBytes();


}

static BigInteger contNrAux = new BigInteger("1234");
private static BigInteger getNewContNumber(Moneda moneda) {
	BankClients.stream().forEach(cont ->{if(cont.contRON.getContNr().compareTo(contNrAux)>=0) contNrAux = cont.contRON.getContNr().add(BigInteger.ONE);});
	BankClients.stream().forEach(cont ->{if(cont.contEURO.getContNr().compareTo(contNrAux)>=0) contNrAux = cont.contEURO.getContNr().add(BigInteger.ONE);});
	if(moneda == Moneda.EURO)
		contNrAux= contNrAux.add(BigInteger.ONE);
	return contNrAux;
}
}


