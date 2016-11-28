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
		userList.add("a");
		passwordMap.put("a", "aa");
		userList.add("b");
		passwordMap.put("b", "bb");
		userList.add("c");
		passwordMap.put("c", "cc");
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
			while (true) {
				try {
					msg = (ChatMessage) sInput.readObject();
				} catch (Exception e) {
					if (!logout)
						System.err.println(userName + " Exception reading Streams: " + e);
					continue;
				}
				// the messaage part of the ChatMessage
				if (msg == null)
					continue;

				ChatMessage msg1;
				String u = (msg.getUser() == null || msg.getUser().isEmpty()) ? null : msg.getUser().get(0);
				String m = (msg.getMessage() == null || msg.getMessage().isEmpty()) ? null : msg.getMessage().get(0);

				System.out.println(msg);

				List<String> ul = msg.getUser();
				List<String> ml = msg.getMessage();
				List<String> tmpList = new ArrayList<String>();
				boolean hasGroup = false;

				switch (msg.getType()) {

				case ChatMessage.LOGIN:
					if (passwordMap.get(u) != null && passwordMap.get(u).equals(m) && !currentUserList.contains(u)) {
						userName = u;
						currentUserList.add(userName);
						userThreadMap.put(userName, this);
						msg1 = new ChatMessage(ChatMessage.LOGIN, u, ChatMessage.SUCCESS);
						writeMsg(msg1);
						System.out.println(u + " LOGIN SUCCESS!");
					} else {
						msg1 = new ChatMessage(ChatMessage.LOGIN, u, ChatMessage.FAIL);
						writeMsg(msg1);
						System.out.println(u + " LOGIN ERROR!");
					}
					break;
				case ChatMessage.SIGNUP:
					if (!passwordMap.containsKey(u)) {
						userName = u;
						userList.add(u);
						passwordMap.put(u, m);
						msg1 = new ChatMessage(ChatMessage.SIGNUP, u, ChatMessage.SUCCESS);
						writeMsg(msg1);
						System.out.println(u + " SIGNUP SUCCESS!");
					} else {
						msg1 = new ChatMessage(ChatMessage.SIGNUP, u, ChatMessage.FAIL);
						writeMsg(msg1);
						System.out.println(u + " SIGNUP ERROR!");
					}
					break;
				case ChatMessage.CHANGEPWD:
					if (passwordMap.containsKey(u) && passwordMap.get(u).equals(m)) {
						userName = u;
						passwordMap.put(u, msg.getMessage().get(1));
						msg1 = new ChatMessage(ChatMessage.CHANGEPWD, u, ChatMessage.SUCCESS);
						writeMsg(msg1);
						System.out.println(u + " CHANGEPWD SUCCESS!");
					} else {
						msg1 = new ChatMessage(ChatMessage.CHANGEPWD, u, ChatMessage.FAIL);
						writeMsg(msg1);
						System.out.println(u + " CHANGEPWD ERROR!");
					}
					break;
				case ChatMessage.LOGOUT:
					boolean success = false;
					if (u != null)
						success = currentUserList.remove(u);
					if (!success) {
						msg1 = new ChatMessage(ChatMessage.LOGOUT, userName, ChatMessage.FAIL);
						writeMsg(msg1);
						System.out.println("LOGOUT ERROR!");
					} else {
						userThreadMap.remove(msg.getUser().get(0));
						msg1 = new ChatMessage(ChatMessage.LOGOUT, userName, ChatMessage.SUCCESS);
						writeMsg(msg1);
						System.out.println("LOGOUT SUCCESS!");
						userName = null;
						logout = true;
					}
					break;
				case ChatMessage.MESSAGE:
					if (userThreadMap.containsKey(u)) {
						ServerThread t = userThreadMap.get(u);
						msg1 = new ChatMessage(ChatMessage.MESSAGE, userName, m);
						t.writeMsg(msg1);
						System.out.println(userName + " to " + u + " MESSAGE SUCCESS!");
					} else {
						msg1 = new ChatMessage(ChatMessage.SENDERR, u, ChatMessage.FAIL);
						writeMsg(msg1);
						System.out.println(userName + " to " + u + " MESSAGE ERROR!");
					}
					break;
				case ChatMessage.GROUPMSG:
					for (ArrayList<String> s : userGroupList) {
						if (s.containsAll(ul) && ul.containsAll(s)) {
							hasGroup = true;
							break;
						}
					}
					if (hasGroup) {
						msg1 = new ChatMessage(ChatMessage.GROUPMSG, ul, ml);
						for (String s : ul) {
							ServerThread t = userThreadMap.get(s);
							if (t != null && !s.equals(userName))
								t.writeMsg(msg1);
						}
						System.out.println(ul + " GROUPMSG SUCCESS!");
					} else {
						tmpList.add(ChatMessage.FAIL);
						msg1 = new ChatMessage(ChatMessage.SENDERR, ul, tmpList);
						writeMsg(msg1);
						System.out.println(ul + " GROUPMSG ERROR!");
					}
					break;
				case ChatMessage.USERLIST: {
					msg1 = new ChatMessage(ChatMessage.USERLIST, userList, new ArrayList<String>());
					writeMsg(msg1);
					System.out.println(userName + " USERLIST SUCCESS!");
					break;
				}
				case ChatMessage.FRIENDREQUEST:
					if (userThreadMap.containsKey(u) && (!friendMap.containsKey(u)
							|| (friendMap.get(u) != null && !friendMap.get(u).contains(userName)))) {
						ServerThread t = userThreadMap.get(u);
						msg1 = new ChatMessage(ChatMessage.FRIENDREQUEST, userName, new String());
						t.writeMsg(msg1);
						System.out.println(u + " FRIENDREQUEST SUCCESS!");
					} else {
						msg1 = new ChatMessage(ChatMessage.FRIENDREQUEST, u, ChatMessage.FAIL);
						writeMsg(msg1);
						System.out.println(u + " FRIENDREQUEST ERROR!");
					}
					break;
				case ChatMessage.FRIENDREQUESTACK:
					if (userThreadMap.containsKey(u) && (!friendMap.containsKey(u)
							|| (friendMap.get(u) != null && !friendMap.get(u).contains(userName)))) {
						if (!friendMap.containsKey(u)) {
							ml.add(userName);
							friendMap.put(u, (ArrayList<String>) ml);
							ml.clear();
							ml.add(u);
							friendMap.put(userName, (ArrayList<String>) ml);
						} else {
							friendMap.get(u).add(userName);
							friendMap.get(userName).add(u);
						}
						ServerThread t = userThreadMap.get(u);
						msg1 = new ChatMessage(ChatMessage.FRIENDREQUESTACK, userName, new String());
						t.writeMsg(msg1);
						System.out.println(u + " FRIENDREQUESTACK SUCCESS!");
					} else {
						System.out.println(u + " FRIENDREQUESTACK ERROR!");
					}
					break;
				case ChatMessage.GROUPREQUEST:
					for (ArrayList<String> s : userGroupList) {
						if (s.containsAll(ul) && ul.containsAll(s)) {
							hasGroup = true;
							break;
						}
					}
					if (!hasGroup) {
						tmpList.add(ChatMessage.SUCCESS);
						userGroupList.add((ArrayList<String>) ul);
						msg1 = new ChatMessage(ChatMessage.GROUPREQUEST, ul, tmpList);
						for (String s : ul) {
							ServerThread t = userThreadMap.get(s);
							if (t != null)
								t.writeMsg(msg1);
						}
						System.out.println(ul + " GROUPREQUEST SUCCESS!");
					} else {
						tmpList.add(ChatMessage.FAIL);
						msg1 = new ChatMessage(ChatMessage.GROUPREQUEST, ul, tmpList);
						writeMsg(msg1);
						System.out.println(ul + " GROUPREQUEST ERROR!");
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
				System.out.println(" Message sent to " + userName);
			} catch (IOException e) {
				System.err.println("Error sending message to " + userName);
			}
			return true;
		}
	}

}
