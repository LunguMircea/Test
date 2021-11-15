package Bank;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream.GetField;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Queue;
import javax.swing.*;
import javax.swing.text.AttributeSet.ColorAttribute;

import org.w3c.dom.Text;
import org.w3c.dom.css.RGBColor;

import Bank.ClientGUI.RegisterGUI;

public class ClientGUI {

	private JFrame frame;
	static Socket socket;
	
	public class TranzactionGUI{
		public TranzactionGUI(byte[] data)
		{
			String[] dataStrings = new String(data).split(":");
			JTextField textbox;
			frame.dispose();
			frame = new JFrame();
			frame.setBounds(100, 100, 500, 500);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setLayout(new GridLayout(9,2,5,5));
			frame.add(new JLabel("Nume:"));
			textbox = new JTextField(dataStrings[2]);
			textbox.setEditable(false);
			frame.add(textbox);
			frame.add(new JLabel("Cont RON:"));
			textbox = new JTextField(dataStrings[6]);
			textbox.setEditable(false);
			frame.add(textbox);
			frame.add(new JLabel("Sold RON:"));
			textbox = new JTextField(dataStrings[10]);
			textbox.setEditable(false);
			frame.add(textbox);
			frame.add(new JLabel("Cont EURO:"));
			textbox = new JTextField(dataStrings[14]);
			textbox.setEditable(false);
			frame.add(textbox);
			frame.add(new JLabel("Sold EURO:"));
			textbox = new JTextField(dataStrings[18]);
			textbox.setEditable(false);
			frame.add(textbox);
			frame.add(new JLabel("Moneda Tranzactionata:"));
			frame.add(new JComboBox<Moneda>(Moneda.values()));
			frame.add(new JLabel("Numerar:"));
			frame.add(new JTextField());
			JButton btn = new JButton("Depozit ");
			btn.addActionListener(new ActionListener() {
				String Message="TRANZACTIE:";
				ArrayList<String> Data= new ArrayList<String>();
				int index;
				@Override
				public void actionPerformed(ActionEvent e) {
					Arrays.stream(frame.getContentPane().getComponents())
					.forEach(comp ->{
						if(comp instanceof JTextField)
							Data.add(((JTextField) comp).getText()+":");
						if(comp instanceof JLabel)
							Data.add(((JLabel) comp).getText());
						if(comp instanceof JComboBox)
							Data.add(((JComboBox) comp).getSelectedItem()+":");
						
					});
					try {
						if( socket == null || socket.isBound() == false)
						{
							socket = new Socket(InetAddress.getLocalHost(),999);
						}
						OutputStream out =  socket.getOutputStream();
						InputStream rec = socket.getInputStream();
						if(Data.get(11).equals("RON:")) 
						{
							Message+="NrCont:"+Data.get(3);
						}
						else
						{
							Message+="NrCont:"+Data.get(7);
						}
						Message+="Numerar:"+Data.get(13);
						Message+="Moneda:"+Data.get(11);
						Message+="Destinatie:BANK";
						out.write(Message.getBytes());
						Message = new String();
						Data = new ArrayList<String>();
						Timer delayAction = new Timer(100, new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								byte[] resp=null;
								try {
									resp = rec.readNBytes(rec.available());
								} catch (IOException e1) {
									e1.printStackTrace();
								}
								if(new String(resp).split(":")[0].equals("Accepted"))
								{
						
									//JOptionPane.showMessageDialog(null, "Accepted");
									TranzactionGUI window = new TranzactionGUI(resp);
									
									 
								}
								else
								{
									JOptionPane.showMessageDialog(null, "Rejected\n"+new String(resp).split(":")[0]);
								}

							}
						});
						delayAction.setRepeats(false);
						delayAction.start();
					} catch (UnknownHostException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} 

				}});
			frame.add(btn);
			btn = new JButton("Retragere ");
			btn.addActionListener(new ActionListener() {
				String Message="TRANZACTIE:";
				ArrayList<String> Data= new ArrayList<String>();
				int index;
				@Override
				public void actionPerformed(ActionEvent e) {
					Arrays.stream(frame.getContentPane().getComponents())
					.forEach(comp ->{
						if(comp instanceof JTextField)
							Data.add(((JTextField) comp).getText()+":");
						if(comp instanceof JLabel)
							Data.add(((JLabel) comp).getText());
						if(comp instanceof JComboBox)
							Data.add(((JComboBox) comp).getSelectedItem()+":");
						
					});
					try {
						if( socket == null || socket.isBound() == false)
						{
							socket = new Socket(InetAddress.getLocalHost(),999);
						}
						OutputStream out =  socket.getOutputStream();
						InputStream rec = socket.getInputStream();
						if(Data.get(11).equals("RON:")) 
						{
							Message+="NrCont:"+Data.get(3);
						}
						else
						{
							Message+="NrCont:"+Data.get(7);
						}
						Message+="Numerar:"+Data.get(13);
						Message+="Moneda:"+Data.get(11);
						Message+="Destinatie:CLIENT";
						out.write(Message.getBytes());
						Message = new String();
						Data = new ArrayList<String>();
						Timer delayAction = new Timer(100, new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								byte[] resp=null;
								try {
									resp = rec.readNBytes(rec.available());
								} catch (IOException e1) {
									e1.printStackTrace();
								}
								if(new String(resp).split(":")[0].equals("Accepted"))
								{
						
									//JOptionPane.showMessageDialog(null, "Accepted");
									TranzactionGUI window = new TranzactionGUI(resp);
									
									 
								}
								else
								{
									JOptionPane.showMessageDialog(null, "Rejected\n"+new String(resp).split(":")[0]);
								}

							}
						});
						delayAction.setRepeats(false);
						delayAction.start();
					} catch (UnknownHostException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} 

				}});
			frame.add(btn);
			btn = new JButton("Inchide cont");
			btn.setBackground(new Color(100,0,0));
			btn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						if( socket == null || socket.isBound() == false)
						{
							socket = new Socket(InetAddress.getLocalHost(),999);
						}
						OutputStream out =  socket.getOutputStream();
						InputStream rec = socket.getInputStream();
						out.write("INCHIDE_CONT".getBytes());
						Timer delayAction = new Timer(100, new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								byte[] resp=null;
								try {
									resp = rec.readNBytes(rec.available());
								} catch (IOException e1) {
									e1.printStackTrace();
								}
								if(new String(resp).split(":")[0].equals("Accept"))
								{
									frame.dispose();	
									ClientGUI window = new ClientGUI();
									window.frame.setVisible(true);		 
								}
								else
								{
									JOptionPane.showMessageDialog(null, "Rejected\n"+new String(resp).split(":")[0]);
								}

							}
						});
						delayAction.setRepeats(false);
						delayAction.start();
					} catch (UnknownHostException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} 

				}});
			
			frame.add(btn);
			frame.setVisible(true);
			
			
		}
	}

	public class LoginGUI{
		public LoginGUI() {
			frame.dispose();
			frame = new JFrame();
			frame.setBounds(100, 100, 350, 150);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setLayout(new GridLayout(3,2,5,5));
			frame.add(new JLabel("CNP Client:"));
			frame.add(new JTextField());
			frame.add(new JLabel("Parola:"));
			frame.add(new JTextField());
			JButton btn = new JButton("Clear");
			btn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Arrays.stream(frame.getContentPane().getComponents())
					.forEach(comp ->{if(comp instanceof JTextField)
						((JTextField)comp).setText(null);
					});}});
			frame.add(btn);

			btn = new JButton("Login");
			btn.setBackground(new Color(0,220,0));		
			btn.addActionListener(new ActionListener() {
				String Message="LOGIN_CONT:";
				@Override
				public void actionPerformed(ActionEvent e) {
					Arrays.stream(frame.getContentPane().getComponents())
					.forEach(comp ->{
						if(comp instanceof JTextField)
							Message+=((JTextField) comp).getText()+":";
						if(comp instanceof JLabel)
							Message+=((JLabel) comp).getText();
					});
					try {
						if( socket == null || socket.isBound() == false)
						{
							socket = new Socket(InetAddress.getLocalHost(),999);
						}
						OutputStream out =  socket.getOutputStream();
						InputStream rec = socket.getInputStream();
						out.write(Message.getBytes());
						Message="LOGIN_CONT:";
						Timer delayAction = new Timer(100, new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								byte[] resp=null;
								try {
									resp = rec.readNBytes(rec.available());
								} catch (IOException e1) {
									e1.printStackTrace();
								}
								if(new String(resp).split(":")[0].equals("Accepted"))
								{
						
									//JOptionPane.showMessageDialog(null, "Accepted");
									TranzactionGUI window = new TranzactionGUI(resp);
									
									 
								}
								else
								{
									JOptionPane.showMessageDialog(null, "Rejected\n"+new String(resp).split(":")[0]);
								}

							}
						});
						delayAction.setRepeats(false);
						delayAction.start();
					} catch (UnknownHostException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} 

				}});
			frame.add(btn);

			frame.setVisible(true);

		}
	}

	public class RegisterGUI {
		RegisterGUI()
		{
			frame.dispose();
			frame = new JFrame();
			frame.setBounds(100, 100, 300, 300);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setLayout(new GridLayout(6,2,5,5));
			frame.add(new JLabel("Nume:"));
			frame.add(new JTextField());
			frame.add(new JLabel("CNP:"));
			frame.add(new JTextField());
			frame.add(new JLabel("Tel:"));
			frame.add(new JTextField());
			frame.add(new JLabel("Adresa:"));
			frame.add(new JTextField());
			frame.add(new JLabel("Parola:"));
			frame.add(new JTextField());

			JButton btn = new JButton("Clear");
			btn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Arrays.stream(frame.getContentPane().getComponents())
					.forEach(comp ->{if(comp instanceof JTextField)
						((JTextField)comp).setText(null);
					});}});
			frame.add(btn);

			btn = new JButton("Register");
			btn.setBackground(new Color(0,220,0));		
			btn.addActionListener(new ActionListener() {
				String Message="CREARE_CONT:";
				@Override
				public void actionPerformed(ActionEvent e) {
					Arrays.stream(frame.getContentPane().getComponents())
					.forEach(comp ->{
						if(comp instanceof JTextField)
							Message+=((JTextField) comp).getText()+":";
						if(comp instanceof JLabel)
							Message+=((JLabel) comp).getText();
					});
					try {
						if( socket == null || socket.isBound() == false || socket.isClosed())
						{
							socket = new Socket(InetAddress.getLocalHost(),999);
						}
						OutputStream out =  socket.getOutputStream();
						InputStream rec = socket.getInputStream();
						out.write(Message.getBytes());
						Message="CREARE_CONT:";
						Timer delayAction = new Timer(100, new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								byte[] resp=null;
								try {
									resp = rec.readNBytes(rec.available());
									if(new String(resp).split(":")[0].equals("Accepted"))
									{
										frame.dispose();
										JOptionPane.showMessageDialog(null, "Accepted\nNumarul de cont este "+ new String(resp).split(":")[2]);
										ClientGUI window = new ClientGUI();
										window.frame.setVisible(true);
									}
									else
									{
										JOptionPane.showMessageDialog(null, "Rejected");
									}
								}								 
								catch (Exception e1) {
									System.out.println(e1.getMessage());
								}

							}
						});
						delayAction.setRepeats(false);
						delayAction.start();
					} catch (UnknownHostException e1) {
						// TODO Auto-generated catch block
						System.out.println(e1.getMessage());
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						System.out.println(e1.getMessage());
					} 

				}});
			frame.add(btn);

			frame.setVisible(true);
		}
	}

	public static void main(String[] args) {
		ClientGUI window = new ClientGUI();
		window.frame.setVisible(true);
	}

	public ClientGUI() {
		frame = new JFrame();
		frame.setBounds(100, 100, 200, 200);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new GridLayout(2,0));

		JButton btnLogin = new JButton("Login");
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				LoginGUI loginGUI = new LoginGUI();	
			}
		});
		JButton btnRegister = new JButton("Register"); 
		btnRegister.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				RegisterGUI registerGUI = new RegisterGUI();	
			}
		});
		frame.add(btnLogin);
		frame.add(btnRegister);

	}

}
