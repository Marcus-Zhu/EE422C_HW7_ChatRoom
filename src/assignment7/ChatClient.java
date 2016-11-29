package assignment7;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ChatClient {

    private ObjectInputStream sInput;
    private ObjectOutputStream sOutput;
    private String userName;
    //private List<String> serverUserList;
    public List<String> serverUserList;
    boolean userListUpdated = false;
    private List<ArrayList<String>> groupList;
    private List<String> friendList;
    Scanner scanner;

    public ChatClient()
    {
        serverUserList = new ArrayList<String>();
        friendList = new ArrayList<String>();
        groupList = new ArrayList<ArrayList<String>>();
        scanner = new Scanner(System.in);
    }

	public static void main(String[] args) {
		try {
			ChatClient c = new ChatClient();
			c.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	
	public void run() throws Exception {
		setUpNetworking();

        (new Thread()
        {
            public void run()
            {
                while(scanner.hasNext())
                {
                    String cmd = scanner.nextLine();
                    String name, pwd;
                    List<String> nl;
                    switch(cmd)
                    {
                    case "login":
                        System.out.print("Username: ");
                        name = scanner.nextLine();
                        System.out.print("Pwd: ");
                        pwd = scanner.nextLine();
                        login(name, pwd);
                        break;
                    case "logout":
                        logout();
                        break;
                    case "signup":
                        System.out.print("Username: ");
                        name = scanner.nextLine();
                        System.out.print("Pwd: ");
                        pwd = scanner.nextLine();
                        signup(name, pwd);
                        break;
                    case "changepwd":
                        System.out.print("Username: ");
                        name = scanner.nextLine();
                        System.out.print("Old Pwd: ");
                        String opwd = scanner.nextLine();
                        System.out.print("New Pwd: ");
                        String npwd = scanner.nextLine();
                        changePwd(name, opwd, npwd);
                        break;
                    case "message":
                        System.out.print("Username: ");
                        name = scanner.nextLine();
                        System.out.print("Message: ");
                        String m = scanner.nextLine();
                        message(name, m);
                        break;
                    case "usrlist":
                        getUserList();
                        break;
                    case "reqfnd":
                        System.out.print("Username: ");
                        name = scanner.nextLine();
                        requestFriend(name);
                        break;
                    case "reqfndack":
                        System.out.print("Username: ");
                        name = scanner.nextLine();
                        requestFriendAck(name);
                        break;
                    case "creategrp":
                        System.out.print("Username: ");
                        nl = new ArrayList<String>();
                        while(true)
                        {
                            name = scanner.nextLine();
                            if (name.equals("END"))
                                break;
                            nl.add(name);
                        };
                        requestGroup(nl);
                        break;
                    case "gmessage":
                        System.out.print("Username: ");
                        nl = new ArrayList<String>();
                        while(true)
                        {
                            name = scanner.nextLine();
                            if (name.equals("END"))
                                break;
                            nl.add(name);
                        };
                        System.out.print("Message: ");
                        String msg = scanner.nextLine();
                        groupMessage(nl, msg);
                        break;
                    }
                }
            }
        }).start();
    }

    public void setUpNetworking() throws Exception
    {
        @SuppressWarnings("resource")
        Socket sock = new Socket("127.0.0.1", 4242);
        sInput  = new ObjectInputStream(sock.getInputStream());
        sOutput = new ObjectOutputStream(sock.getOutputStream());
        System.out.println("networking established");
        Thread readerThread = new ClientThread();
        readerThread.start();
    }

    public boolean writeMsg(ChatMessage msg)
    {
        try
        {
            sOutput.writeObject(msg);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return true;
    }

    public boolean login(String name, String pwd)
    {
        ChatMessage msg = new ChatMessage(ChatMessage.LOGIN, name, pwd);
        return writeMsg(msg);
    }
    public boolean signup(String name, String pwd)
    {
        ChatMessage msg = new ChatMessage(ChatMessage.SIGNUP, name, pwd);
        return writeMsg(msg);
    }
    public boolean changePwd(String name, String oldPwd, String newPwd)
    {
        List<String> ul = new ArrayList<String>();
        ul.add(name);
        List<String> pl = new ArrayList<String>();
        pl.add(oldPwd);
        pl.add(newPwd);
        ChatMessage msg = new ChatMessage(ChatMessage.CHANGEPWD, ul, pl);
        return writeMsg(msg);
    }
    public boolean logout()
    {
        System.out.println("Logout: " + userName);
        ChatMessage msg = new ChatMessage(ChatMessage.LOGOUT, userName, new String());
        return writeMsg(msg);
    }
    public boolean message(String target, String message)
    {
        ChatMessage msg = new ChatMessage(ChatMessage.MESSAGE, target, message);
        return writeMsg(msg);
    }
    public boolean groupMessage(List<String> target, String message)
    {
        List<String> ml = new ArrayList<String>();
        target.add(userName);
        for(int i = 0; i < target.size(); i++)
        {
            String str = target.get(i);
            if (str.equals(userName))
                ml.add(message);
            else
                ml.add(null);
        }
        ChatMessage msg = new ChatMessage(ChatMessage.GROUPMSG, target, ml);
        return writeMsg(msg);
    }
    public boolean getUserList()
    {
    	userListUpdated = false;
        ChatMessage msg = new ChatMessage(ChatMessage.USERLIST, new String(), new String());
        return writeMsg(msg);
    }
    public boolean requestFriend(String target)
    {
        ChatMessage msg = new ChatMessage(ChatMessage.FRIENDREQUEST, target, new String());
        return writeMsg(msg);
    }
    public boolean requestFriendAck(String target)
    {
        ChatMessage msg = new ChatMessage(ChatMessage.FRIENDREQUESTACK, target, ChatMessage.SUCCESS);
        friendList.add(target);
        return writeMsg(msg);
    }
    public boolean requestGroup(List<String> users)
    {
        users.add(userName);
        ChatMessage msg = new ChatMessage(ChatMessage.GROUPREQUEST, users, new ArrayList<String>());
        return writeMsg(msg);
    }
    public void showMessage(String usr, String msg)
    {
        System.out.println(usr + ": " + msg);
    }

    public void friendRequest(String usr)
    {
        System.out.println(usr + "sent you a friend request. Accept?");
    }

    public void showGroupMessage(List<String> ul, List<String> ml)
    {
        System.out.println(ul + ": " + ml);
    }


    class ClientThread extends Thread
    {
        public void run()
        {
            while(true)
            {

                ChatMessage msg;
                try
                {
                    msg = (ChatMessage) sInput.readObject();

                }
                catch(IOException e)
                {
                    System.err.println("Server has close the connection: " + e);
                    break;
                }
                catch(ClassNotFoundException e2)
                {
                    System.err.println("ClassNotFoundException: " + e2);
                    break;
                }

                System.out.println("### Username: " + userName);

                String u = (msg.getUser() == null || msg.getUser().isEmpty()) ? null : msg.getUser().get(0);
                String m = (msg.getMessage() == null || msg.getMessage().isEmpty()) ? null : msg.getMessage().get(0);
                System.out.println(msg);  //IMPORTANT!!!
                switch(msg.getType())
                {
                case ChatMessage.LOGIN:
                    if (m.equals(ChatMessage.SUCCESS))
                    {
                        userName = u;
                        System.out.println(userName + " LOGIN SUCCESS!");
                    }
                    else
                    {
                        System.out.println(userName + " LOGIN ERROR!");
                    }
                    break;
                case ChatMessage.SIGNUP:
                    if (!u.equals(userName))
                        break;
                    if (m.equals(ChatMessage.SUCCESS))
                    {
                        System.out.println(u + " SIGNUP SUCCESS!");
                    }
                    else
                    {
                        System.out.println(u + " SIGNUP ERROR!");
                    }
                    break;
                case ChatMessage.CHANGEPWD:
                    if (!u.equals(userName))
                        break;
                    if (m.equals(ChatMessage.SUCCESS))
                    {
                        System.out.println(u + " CHANGEPWD SUCCESS!");
                    }
                    else
                    {
                        System.out.println(u + " CHANGEPWD ERROR!");
                    }
                    break;
                case ChatMessage.LOGOUT:
                    if (!u.equals(userName))
                        break;
                    if (m.equals(ChatMessage.SUCCESS))
                    {
                        userName = null;
                        System.out.println("LOGOUT SUCCESS!");
                    }
                    else
                    {
                        System.out.println("LOGOUT ERROR!");
                    }
                    break;
                case ChatMessage.MESSAGE:
                    if (!u.equals(userName))
                        break;
                    showMessage(u, m);
                    break;
                case ChatMessage.GROUPMSG:
                    if (!msg.getUser().contains(userName))
                        break;
                    showGroupMessage(msg.getUser(), msg.getMessage());
                    break;
                case ChatMessage.GROUPREQUEST:
                    groupList.add((ArrayList<String>) msg.getUser());
                    break;
                case ChatMessage.SENDERR:
                    break;
                case ChatMessage.USERLIST:
                {
                    serverUserList = msg.getUser();
                    userListUpdated = true;
                    System.out.println(userName + " USERLIST SUCCESS! " + serverUserList);
                    break;
                }
                case ChatMessage.FRIENDREQUEST:
                    friendRequest(u);
                    break;
                case ChatMessage.FRIENDREQUESTACK:
                    friendList.add(u);
                    System.out.println(userName + " FRIENDREQUESTACK SUCCESS! " + u);
                    break;
                }
                msg = null;
            }
        }
    }
}
