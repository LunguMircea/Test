package Fisk;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Observer;

public class FiscalMonitor {
	static Socket socket;	
	public static void main(String[] args) throws UnknownHostException, IOException {
		// TODO Auto-generated method stub
		socket = new Socket(InetAddress.getLocalHost(),998);
		while(socket.isConnected() && socket.isClosed() == false)
		{
			OutputStream outp = socket.getOutputStream();
			InputStream inp = socket.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			if(reader.ready()) {
			String consoleInp = reader.readLine();
			outp.write(consoleInp.getBytes());
			}
			if(inp.available() > 0)
			{

				byte[] message= new byte[inp.available()];
				inp.read(message,0,inp.available());;
				System.out.println(new String(message));
			}
		}
	}

}
