package assignment7;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class ChatClient extends Application{

	private ObjectInputStream sInput;
	private ObjectOutputStream sOutput;
	private String userName;
	private List<String> userList;
	private List<String> friendList;

	public ChatClient(){
		userList = new ArrayList<String>();
		friendList = new ArrayList<String>();
	}

	public static void main(String[] args) {
		try {
			launch(args);
			ChatClient c = new ChatClient();
			c.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Chat Client");
		
		//create buttons
        Button forgotPassBtn = new Button();
        Button newUserBtn = new Button();
        TextField userNmTxt = new TextField ();
        TextField passwdTxt = new TextField ();
        
        forgotPassBtn.setText("Forgot password?");
        newUserBtn.setText("Create New Account");
        userNmTxt.setText("Enter User name");
        passwdTxt.setText("Enter Password");
        
        userNmTxt.setPrefWidth(200);
        //userNmTxt.setSpan
        passwdTxt.setPrefWidth(200);
        
        forgotPassBtn.setOnAction(new EventHandler<ActionEvent>() {
 
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Pressed forgot passwd");
            }
        });
        
        GridPane grid = new GridPane();
        grid.setVgap(10); 
        grid.setHgap(10);
        grid.setAlignment(Pos.CENTER);
        grid.add(passwdTxt, 0, 0);  // specify where to add objects to gridpane 
        grid.add(userNmTxt, 0, 1);  
        grid.add(forgotPassBtn, 0,2);
        grid.add(newUserBtn,1,2);
        primaryStage.setScene(new Scene(grid, 350, 250));
        primaryStage.show();
        
/*        StackPane root = new StackPane();
        root.getChildren().add(forgotPassBtn);
        root.getChildren().add(newUserBtn);
        primaryStage.setScene(new Scene(root, 300, 250));
        primaryStage.show();*/
		
	}
	
	public void run() throws Exception {
		setUpNetworking();

		(new Thread(){
			public void run(){
				Scanner s = new Scanner(System.in);

				while(s.hasNext()){
					String cmd = s.nextLine();
					switch(cmd){
					case "login":{
						System.out.print("Username: ");
						String name = s.nextLine();
						System.out.print("Pwd: ");
						String pwd = s.nextLine();
						login(name, pwd);
						break;
					}
					case "logout":{
						logout();
					}
					case "signup":{
						System.out.print("Username: ");
						String name = s.nextLine();
						System.out.print("Pwd: ");
						String pwd = s.nextLine();
						signup(name, pwd);
						break;
					}
					case "changepwd":{
						System.out.print("Username: ");
						String name = s.nextLine();
						System.out.print("Old Pwd: ");
						String opwd = s.nextLine();
						System.out.print("New Pwd: ");
						String npwd = s.nextLine();
						changePwd(name, opwd, npwd);
						break;
					}
					case "message":{
						System.out.print("Username: ");
						String name = s.nextLine();
						System.out.print("Message: ");
						String msg = s.nextLine();
						message(name, msg);
						break;
					}
					case "usrlist":{
						getUserList();
						break;
					}
					case "reqfnd":{
						System.out.print("Username: ");
						String name = s.nextLine();
						requestFriend(name);
						break;
					}
					case "reqfndack":{
						System.out.print("Username: ");
						String name = s.nextLine();
						requestFriendAck(name);
						break;
					}
//					case "gmessage":{
//						System.out.print("Username: ");
//						String name = s.nextLine();
//						System.out.print("Message: ");
//						String msg = s.nextLine();
//						groupMessage(name, msg);
//						break;
//					}
					}
				}
			}
		}).start();
	}

	private void setUpNetworking() throws Exception {
		@SuppressWarnings("resource")
		Socket sock = new Socket("127.0.0.1", 4242);
		sInput  = new ObjectInputStream(sock.getInputStream());
		sOutput = new ObjectOutputStream(sock.getOutputStream());
		System.out.println("networking established");
		Thread readerThread = new ClientThread();
		readerThread.start();
	}

	public boolean writeMsg(ChatMessage msg){
		try {
			sOutput.writeObject(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	public boolean login(String name, String pwd){
		ChatMessage msg = new ChatMessage(ChatMessage.LOGIN,name,pwd);
		return writeMsg(msg);
	}
	public boolean signup(String name, String pwd){
		ChatMessage msg = new ChatMessage(ChatMessage.SIGNUP,name,pwd);
		return writeMsg(msg);
	}
	public boolean changePwd(String name, String oldPwd, String newPwd){
		List<String> ul = new ArrayList<String>();
		ul.add(name);
		List<String> pl = new ArrayList<String>();
		pl.add(oldPwd);
		pl.add(newPwd);
		ChatMessage msg = new ChatMessage(ChatMessage.CHANGEPWD,ul,pl);
		return writeMsg(msg);
	}
	public boolean logout(){
		System.out.println("Username: "+userName);
		ChatMessage msg = new ChatMessage(ChatMessage.LOGOUT,userName,new String());
		return writeMsg(msg);
	}
	public boolean message(String target, String message){
		ChatMessage msg = new ChatMessage(ChatMessage.MESSAGE,target,message);
		return writeMsg(msg);
	}
	public boolean groupMessage(List<String> target, List<String> message){
		ChatMessage msg = new ChatMessage(ChatMessage.MESSAGE,target,message);
		return writeMsg(msg);
	}
	public boolean getUserList(){
		ChatMessage msg = new ChatMessage(ChatMessage.USERLIST,new String(),new String());
		return writeMsg(msg);
	}
	public boolean requestFriend(String target){
		ChatMessage msg = new ChatMessage(ChatMessage.FRIENDREQUEST,target,new String());
		return writeMsg(msg);
	}
	public boolean requestFriendAck(String target){
		ChatMessage msg = new ChatMessage(ChatMessage.FRIENDREQUESTACK,target,ChatMessage.SUCCESS);
		return writeMsg(msg);
	}

	public void showMessage(String usr, String msg){
		System.out.println(usr + ": " + msg);
	}

	public void friendRequest(String usr){
		System.out.println(usr + ": friend request");
	}

	public void showGroupMessage(List<String> ul, String msg){
		System.out.println(ul + ": " + msg);
	}


	class ClientThread extends Thread {
		public void run() {
			while(true) {

				System.out.println("###Username: "+userName);

				ChatMessage msg;
				try {
					msg = (ChatMessage) sInput.readObject();

				}
				catch(IOException e) {
					System.err.println("Server has close the connection: " + e);
					break;
				}
				catch(ClassNotFoundException e2) {
					System.err.println("ClassNotFoundException: " + e2);
					break;
				}

				String u = msg.getUser() == null ? null : msg.getUser().get(0);
				String m = msg.getMessage() == null ? null : msg.getMessage().get(0);
				System.out.println("Got: "+u+' '+m);
				switch(msg.getType()){

				case ChatMessage.LOGIN:
					System.out.println(msg.getTimeStamp()+" Type: LOGIN");
					System.out.println(msg.getUser());
					System.out.println(msg.getMessage());
					if (m.equals(ChatMessage.SUCCESS)){
						userName = u;
						System.out.println(userName + " LOGIN SUCCESS!");
					}
					else {
						System.out.println(userName + " LOGIN ERROR!");
					}
					break;
				case ChatMessage.SIGNUP:
					System.out.println(msg.getTimeStamp()+" Type: SIGNUP");
					System.out.println(msg.getUser());
					System.out.println(msg.getMessage());
					if (!u.equals(userName))
						break;
					if (m.equals(ChatMessage.SUCCESS)){
						System.out.println(u+ " SIGNUP SUCCESS!");
					}
					else {
						System.out.println(u+" SIGNUP ERROR!");
					}
					break;
				case ChatMessage.CHANGEPWD:
					System.out.println(msg.getTimeStamp()+" Type: CHANGEPWD");
					System.out.println(msg.getUser());
					System.out.println(msg.getMessage());
					if (!u.equals(userName))
						break;
					if (m.equals(ChatMessage.SUCCESS)){
						System.out.println(u+ " CHANGEPWD SUCCESS!");
					}
					else {
						System.out.println(u+" CHANGEPWD ERROR!");
					}
					break;
				case ChatMessage.LOGOUT:
					System.out.println(msg.getTimeStamp()+" Type: LOGOUT");
					System.out.println(msg.getUser());
					System.out.println(msg.getMessage());
					if (!u.equals(userName))
						break;
					if (m.equals(ChatMessage.SUCCESS)){
						userName = null;
						System.out.println("LOGOUT SUCCESS!");
					}
					else {
						System.out.println("LOGOUT ERROR!");
					}
					break;
				case ChatMessage.MESSAGE:
					System.out.println(msg.getTimeStamp()+" Type: MESSAGE");
					System.out.println(msg.getUser());
					System.out.println(msg.getMessage());
					if (!u.equals(userName))
						break;
					showMessage(u, m);
					break;
				case ChatMessage.GROUPMSG:
					System.out.println(msg.getTimeStamp()+" Type: GROUPMSG");
					System.out.println(msg.getUser());
					System.out.println(msg.getMessage());
					List<String> ul = msg.getUser();
					if (!ul.contains(userName))
						break;
					String mg = msg.getMessage().get(0);
					showGroupMessage(ul, mg);
					break;
				case ChatMessage.USERLIST:{
					System.out.println(msg.getTimeStamp()+" Type: USERLIST");
					System.out.println(msg.getUser());
					System.out.println(msg.getMessage());
					userList = msg.getUser();
					System.out.println(userName+" USERLIST SUCCESS! " + userList);
					break;
				}
				case ChatMessage.FRIENDREQUEST:
					System.out.println(msg.getTimeStamp()+" Type: FRIENDREQUEST");
					System.out.println(msg.getUser());
					System.out.println(msg.getMessage());
					friendRequest(u);
					break;
				case ChatMessage.FRIENDREQUESTACK:
					System.out.println(msg.getTimeStamp()+" Type: FRIENDREQUESTACK");
					System.out.println(msg.getUser());
					System.out.println(msg.getMessage());
					friendList.add(u);
					System.out.println(userName+ " FRIENDREQUESTACK SUCCESS! " + u);
					break;
				}
				msg = null;
			}
		}
	}


}
