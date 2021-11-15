package Bank;

import java.lang.reflect.Array;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.Number;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Scanner;


enum Moneda{RON,EURO}

interface Subject {

	//methods to register and unregister observers
	public void register(Socket socket);
	public void unregister();
	
	//method to get updates from subject
	public void update(BigInteger ultimulCont);
	
}

class Tranzactie{	
	
	BigInteger sursa,destinatie;
	Moneda moneda;
	double valuare;
	public Tranzactie(BigInteger sursa,BigInteger destinatie, Moneda moneda, double valuare) 
	{
		this.sursa = sursa;
		this.destinatie = destinatie;
		this.moneda = moneda;
		this.valuare = valuare;
	} 
}

class ContBancar {
	private BigInteger contNr;
	private ArrayList<Tranzactie>tranzactii;
	private Moneda moneda;
	private int MIN_SOLD = 1000;
	private ClientBancar clientRef;
	
	public ContBancar(BigInteger contNr,Moneda moneda,ClientBancar clientRef)
	{
		tranzactii = new ArrayList<Tranzactie>();
		this.contNr = contNr;		
		this.moneda = moneda;
		this.clientRef = clientRef;
	}
	public BigInteger getContNr() {
		return contNr;
	}
	@Override
	public String toString() {
		return ":NrCont:"+contNr.toString()+":Moneda:"+moneda+":Sold:"+String.valueOf(calcualteSold());
	}
	private double sold;
	public double calcualteSold() {
		sold=0;
		tranzactii.forEach(tr -> sold +=tr.valuare);
		return sold;
	}
	public boolean AddTransfer(BigInteger contNrDest, Moneda moneda, double valuare) 
	{
		if(calcualteSold()-valuare <MIN_SOLD)
			return false;
		else 
		{
			tranzactii.add(new Tranzactie(this.contNr, contNrDest, this.moneda, -valuare));
			
			return true;
		}
	}
	public boolean AddDeposit(BigInteger contNrSrc, Moneda moneda, double valuare) 
	{
		if(calcualteSold()+valuare <MIN_SOLD)
			return false;
		else 
		tranzactii.add( new Tranzactie(contNrSrc, this.contNr, this.moneda, valuare));
		clientRef.update(this.contNr);
		return true;
	}
	
}


class ClientBancar implements Subject{		
	private String nume,adresa;
	private int password;
	ContBancar contRON,contEURO;
	Socket observer;
	public String getNume() {
		return nume;
	}

	private BigInteger CNP,tel;
	
	public BigInteger getCNP() {
		return CNP;
	}
	public int getPassword() {
		return password;
	}

	public ClientBancar(String nume, String adresa, BigInteger CNP, BigInteger tel,BigInteger contNrRON,BigInteger contNrEURO,int password) {
		this.nume = nume;
		this.adresa = adresa;
		this.CNP = CNP;
		this.tel = tel;
		this.password = password;
		contRON = new ContBancar(contNrRON, Moneda.RON,this);
		contEURO = new ContBancar(contNrEURO, Moneda.EURO,this);
	}
	@Override
	public String toString()
	{
		return "Nume:"+nume+":Cont RON:"+contRON.toString()+"\n:Cont EURO:"+contEURO.toString()+":";
	}
	@Override
	public void register(Socket obj) {
		// TODO Auto-generated method stub
		observer = obj;
	}
	@Override
	public void unregister() {
		// TODO Auto-generated method stub
		observer = null;
	}
	@Override
	public void update(BigInteger ultimulCont) {
		// TODO Auto-generated method stub
		if(observer != null)
			try {
				observer.getOutputStream().write((this.toString() + 
						"\nUltimul transfer a fost facut pe contul:"+ultimulCont.toString()+
						"\n---------------------\n").getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
}
