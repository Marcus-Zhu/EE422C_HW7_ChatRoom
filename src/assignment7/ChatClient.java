package assignment7;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {

	private ObjectInputStream sInput;
	private ObjectOutputStream sOutput;


	public void run() throws Exception {
		setUpNetworking();
	}

	private void setUpNetworking() throws Exception {
		@SuppressWarnings("resource")
		Socket sock = new Socket("127.0.0.1", 4242);
		sInput  = new ObjectInputStream(sock.getInputStream());
		sOutput = new ObjectOutputStream(sock.getOutputStream());
		System.out.println("networking established");
		Thread readerThread = new ClientThread();
		readerThread.start();
		Scanner s = new Scanner(System.in);
		while (true){
			if (s.nextInt() == 1){
				System.out.println("asdfasdf");
				ChatMessage msg = new ChatMessage(ChatMessage.MESSAGE,null,null);
				sOutput.writeObject(msg);
			}
		}
	}

	public static void main(String[] args) {
		try {
			new ChatClient().run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class ClientThread extends Thread {
		public void run() {
			while(true) {
				try {
					ChatMessage msg = (ChatMessage) sInput.readObject();
					System.out.println(msg.getType());
				}
				catch(IOException e) {
					System.err.println("Server has close the connection: " + e);
					break;
				}
				catch(ClassNotFoundException e2) {
				}
			}
		}
	}
}
