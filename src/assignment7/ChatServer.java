package assignment7;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServer {
	private List<ServerThread> threadList;
	private List<String> userList;
	private List<String> currentUserList;
	private Map<String, ServerThread> userThreadMap;
	private Map<String, String> passwordMap;
	private List<ArrayList<String>> userGroupList;
	private Map<String, ArrayList<String>> friendMap;

	ChatServer() {
		threadList = new ArrayList<ServerThread>();
		userList = new ArrayList<String>();
		userThreadMap = new ConcurrentHashMap<String, ServerThread>();
		currentUserList = new ArrayList<String>();
		passwordMap = new ConcurrentHashMap<String, String>();
		userGroupList = new ArrayList<ArrayList<String>>();
		friendMap = new ConcurrentHashMap<String, ArrayList<String>>();
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
		for (ServerThread t : threadList) {
			int cnt = 0;
			for (String s : msg.getUser()) {
				if (s.equals(t.userName))
					t.writeMsg(new ChatMessage(msg.getType(), t.userName, msg.getMessage().get(cnt)));
				cnt++;
			}
		}
	}

	class ServerThread extends Thread {
		Socket socket;
		ObjectInputStream sInput;
		ObjectOutputStream sOutput;
		String userName;
		String date;
		ChatMessage msg;

		public ServerThread(Socket clientSocket) throws IOException {
			socket = clientSocket;
			try {
				sOutput = new ObjectOutputStream(socket.getOutputStream());
				sInput = new ObjectInputStream(socket.getInputStream());
			} catch (IOException e) {
				System.err.println("Exception creating new Input/output Streams: " + e);
				return;
			}
			date = new Date().toString() + "\n";
			System.out.println(date);
		}

		public void run() {
			boolean logout = false;
			while (!logout) {
				try {
					msg = (ChatMessage) sInput.readObject();
				} catch (Exception e) {
					System.err.println(userName + " Exception reading Streams: " + e);
					continue;
				}
				// the messaage part of the ChatMessage
				if (msg == null) continue;
				switch (msg.getType()) {
				case ChatMessage.LOGIN:
					System.out.println(msg.getTimeStamp()+" Type: LOGIN");
					System.out.println(msg.getUser());
					System.out.println(msg.getMessage());
					String u = msg.getUser().get(0);
					String m = msg.getMessage().get(0);
					if (passwordMap.get(u) != null && passwordMap.get(u).equals(m) && !currentUserList.contains(u)){
						userName = u;
						currentUserList.add(userName);
						userThreadMap.put(userName, this);
						ChatMessage msg1 = new ChatMessage(ChatMessage.LOGIN,u,ChatMessage.SUCCESS);
						writeMsg(msg1);
						System.out.println(u+" LOGIN SUCCESS!");
					}
					else {
						ChatMessage msg1 = new ChatMessage(ChatMessage.LOGIN,u,ChatMessage.FAIL);
						writeMsg(msg1);
						System.out.println(u+" LOGIN ERROR!");
					}
					break;
				case ChatMessage.SIGNUP:
					System.out.println(msg.getTimeStamp()+" Type: SIGNUP");
					System.out.println(msg.getUser());
					System.out.println(msg.getMessage());
					String u1 = msg.getUser().get(0);
					String m1 = msg.getMessage().get(0);
					if (!passwordMap.containsKey(u1)){
						userName = u1;
						userList.add(u1);
						passwordMap.put(u1, m1);
						ChatMessage msg1 = new ChatMessage(ChatMessage.SIGNUP,u1,ChatMessage.SUCCESS);
						writeMsg(msg1);
						System.out.println(u1+ " SIGNUP SUCCESS!");
					}
					else {
						ChatMessage msg1 = new ChatMessage(ChatMessage.SIGNUP,u1,ChatMessage.FAIL);
						writeMsg(msg1);
						System.out.println(u1+" SIGNUP ERROR!");
					}
					break;
				case ChatMessage.CHANGEPWD:
					System.out.println(msg.getTimeStamp()+" Type: CHANGEPWD");
					System.out.println(msg.getUser());
					System.out.println(msg.getMessage());
					String u2 = msg.getUser().get(0);
					String m2 = msg.getMessage().get(0);
					if (passwordMap.containsKey(u2) && passwordMap.get(u2).equals(m2)){
						userName = u2;
						passwordMap.put(u2, msg.getMessage().get(1));
						ChatMessage msg1 = new ChatMessage(ChatMessage.CHANGEPWD,u2,ChatMessage.SUCCESS);
						writeMsg(msg1);
						System.out.println(u2+ " CHANGEPWD SUCCESS!");
					}
					else {
						ChatMessage msg1 = new ChatMessage(ChatMessage.CHANGEPWD,u2,ChatMessage.FAIL);
						writeMsg(msg1);
						System.out.println(u2+" CHANGEPWD ERROR!");
					}
					break;
				case ChatMessage.LOGOUT:
					System.out.println(msg.getTimeStamp()+" Type: LOGOUT");
					System.out.println(msg.getUser());
					System.out.println(msg.getMessage());
					boolean success = currentUserList.remove(msg.getUser().get(0));
					if (!success){
						ChatMessage msg1 = new ChatMessage(ChatMessage.LOGOUT,userName,ChatMessage.FAIL);
						writeMsg(msg1);
						System.out.println("LOGOUT ERROR!");
					}
					else {
						userThreadMap.remove(msg.getUser().get(0));
						ChatMessage msg1 = new ChatMessage(ChatMessage.LOGOUT,userName,ChatMessage.SUCCESS);
						writeMsg(msg1);
						System.out.println("LOGOUT SUCCESS!");
//						logout = true;
					}
					break;
				case ChatMessage.MESSAGE:
					System.out.println(msg.getTimeStamp()+" Type: MESSAGE");
					System.out.println(msg.getUser());
					System.out.println(msg.getMessage());
					String u3 = msg.getUser().get(0);
					String m3 = msg.getMessage().get(0);
					if (userThreadMap.containsKey(u3)){
						ServerThread t = userThreadMap.get(u3);
						ChatMessage msg1 = new ChatMessage(ChatMessage.MESSAGE,userName,m3);
						t.writeMsg(msg1);
						System.out.println(userName + " to " + u3 + " MESSAGE SUCCESS!");
					}
					else {
						ChatMessage msg1 = new ChatMessage(ChatMessage.MESSAGE,u3,ChatMessage.FAIL);
						writeMsg(msg1);
						System.out.println(userName + " to " + u3+" MESSAGE ERROR!");
					}
					break;
				case ChatMessage.GROUPMSG:
					System.out.println(msg.getTimeStamp()+" Type: GROUPMSG");
					System.out.println(msg.getUser());
					System.out.println(msg.getMessage());
					List<String> ul = msg.getUser();
					String mg = msg.getMessage().get(0);
					if (userGroupList.contains(ul)){
						ChatMessage msg1 = new ChatMessage(ChatMessage.GROUPMSG,ul,mg);
						for (String s: ul){
							ServerThread t = userThreadMap.get(s);
							if (t != null)
								t.writeMsg(msg1);
						}
						System.out.println(ul+ " GROUPMSG SUCCESS!");
					}
					else {
						ChatMessage msg1 = new ChatMessage(ChatMessage.GROUPMSG,ul,ChatMessage.FAIL);
						writeMsg(msg1);
						System.out.println(ul+" GROUPMSG ERROR!");
					}
					break;
				case ChatMessage.USERLIST:{
					System.out.println(msg.getTimeStamp()+" Type: USERLIST");
					System.out.println(msg.getUser());
					System.out.println(msg.getMessage());
					ChatMessage msg1 = new ChatMessage(ChatMessage.GROUPMSG,userList,new ArrayList<String>());
					writeMsg(msg1);
					System.out.println(userName+" USERLIST SUCCESS!");
					break;
				}
				case ChatMessage.FRIENDREQUEST:
					System.out.println(msg.getTimeStamp()+" Type: FRIENDREQUEST");
					System.out.println(msg.getUser());
					System.out.println(msg.getMessage());
					String u4 = msg.getUser().get(0);
					String m4 = msg.getMessage().get(0);
					if (userThreadMap.containsKey(u4) && (!friendMap.containsKey(u4)
							|| (friendMap.get(u4) != null && !friendMap.get(u4).contains(userName)))){
						ServerThread t = userThreadMap.get(u4);
						ChatMessage msg1 = new ChatMessage(ChatMessage.FRIENDREQUEST,userName,new String());
						t.writeMsg(msg1);
						System.out.println(u4+ " FRIENDREQUEST SUCCESS!");
					}
					else {
						ChatMessage msg1 = new ChatMessage(ChatMessage.FRIENDREQUEST,u4,ChatMessage.FAIL);
						writeMsg(msg1);
						System.out.println(u4+" FRIENDREQUEST ERROR!");
					}
					break;
				case ChatMessage.FRIENDREQUESTACK:
					System.out.println(msg.getTimeStamp()+" Type: FRIENDREQUESTACK");
					System.out.println(msg.getUser());
					System.out.println(msg.getMessage());
					String u5 = msg.getUser().get(0);
					if (userThreadMap.containsKey(u5) && (!friendMap.containsKey(u5)
							|| (friendMap.get(u5) != null && !friendMap.get(u5).contains(userName)))){
						if (!friendMap.containsKey(u5)){
							ArrayList<String> ml = new ArrayList<String>();
							ml.add(userName);
							friendMap.put(u5, ml);
							ml.clear();
							ml.add(u5);
							friendMap.put(userName, ml);
						}
						else{
							friendMap.get(u5).add(userName);
							friendMap.get(userName).add(u5);
						}
						ServerThread t = userThreadMap.get(u5);
						ChatMessage msg1 = new ChatMessage(ChatMessage.FRIENDREQUESTACK,userName,new String());
						t.writeMsg(msg1);
						System.out.println(u5+ " FRIENDREQUESTACK SUCCESS!");
					}
					else {
						System.out.println(u5+" FRIENDREQUESTACK ERROR!");
					}
					break;
				}
				msg = null;
			}

		}

		private boolean writeMsg(ChatMessage msg) {
			if (!socket.isConnected()) {
				return false;
			}
			try {
				sOutput.writeObject(msg);
				System.out.println(userName + " Message sent!");
			} catch (IOException e) {
				System.err.println("Error sending message to " + userName);
			}
			return true;
		}
	}

}
