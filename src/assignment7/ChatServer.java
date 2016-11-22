package assignment7;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatServer {
	private List<ServerThread> threadList;

	ChatServer(){
		threadList = new ArrayList<ServerThread>();
	}

	public static void main(String[] args) {
		try {
			new ChatServer().setUpNetworking();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setUpNetworking() throws Exception {
		@SuppressWarnings("resource")
		ServerSocket serverSock = new ServerSocket(4242);
		while (true) {
			Socket clientSocket = serverSock.accept();
			ServerThread t = new ServerThread(clientSocket);
			threadList.add(t);
			t.start();
			System.out.println("got a connection");
		}
	}

	private synchronized void broadcast(ChatMessage msg) {
		for (ServerThread t : threadList){
			t.writeMsg(msg);
		}
	}

	class ServerThread extends Thread {
		Socket socket;
		ObjectInputStream sInput;
		ObjectOutputStream sOutput;
		String username;
		String date;
		ChatMessage message;

		public ServerThread(Socket clientSocket) throws IOException {
			socket = clientSocket;
			try
			{
				sOutput = new ObjectOutputStream(socket.getOutputStream());
				sInput  = new ObjectInputStream(socket.getInputStream());
			}
			catch (IOException e) {
				System.err.println("Exception creating new Input/output Streams: " + e);
				return;
			}
            date = new Date().toString() + "\n";
            System.out.println(date);
		}

		public void run() {
			while (true){
				try {
					message = (ChatMessage) sInput.readObject();
				}
				catch (IOException e) {
					System.err.println(username + " Exception reading Streams: " + e);
				}
				catch(ClassNotFoundException e) {
				}
				// the messaage part of the ChatMessage
				System.out.println(message.getType());
			}
				// Switch on the type of message receive
//				switch(message.getType()) {
//
//				case ChatMessage.MESSAGE:
//					broadcast(username + ": " + message);
//					break;
//				case ChatMessage.LOGOUT:
//					display(username + " disconnected with a LOGOUT message.");
//					keepGoing = false;
//					break;
//				case ChatMessage.WHOISIN:
//					writeMsg("List of the users connected at " + sdf.format(new Date()) + "\n");
//					// scan al the users connected
//					for(int i = 0; i < al.size(); ++i) {
//						ClientThread ct = al.get(i);
//						writeMsg((i+1) + ") " + ct.username + " since " + ct.date);
//					}
//					break;
//				}

		}
		private boolean writeMsg(ChatMessage msg) {
			if(!socket.isConnected()) {
				return false;
			}
			try {
				sOutput.writeObject(msg);
			}
			catch(IOException e) {
				System.err.println("Error sending message to " + username);
			}
			return true;
		}
	}

}
