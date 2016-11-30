package assignment7;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
/*import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;*/
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Parent;
//import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Popup;
//import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
//import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.util.ArrayList;

//import javax.swing.event.ChangeEvent;
//import javax.swing.event.ChangeListener;

import assignment7.ChatClient;

public class ClientGUI extends Application {
	ObservableList<String> options;
	ChatClient cc = new ChatClient();
	TextArea taChatHist;
	boolean groupChatMode = false;
	//private StringProperty textRecu = new SimpleStringProperty();

	ArrayList<String> chatHist = new ArrayList<String>();
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	//maxChars is the most that the text area
/*	private String getChatHist(int maxChars){
		while ()
		return ret;
	}*/

	Task<Void> task = new Task<Void>() {
	    @Override public Void call() throws Exception{
	    	while (true){
	    		Thread.sleep(5); // not sure why, but have to keep this line in here for the text area to update. reduce CPU load???
	    		//System.out.print("");  //not sure why, but have to keep this line in here for the text area to update???
	    		if (cc.newUserMsg){
	    			ChatMessage sMsg = cc.semaphoreMsg;
	    			String u = (sMsg.getUser() == null || sMsg.getUser().isEmpty()) ? null : sMsg.getUser().get(0);
	                String m = (sMsg.getMessage() == null || sMsg.getMessage().isEmpty()) ? null : sMsg.getMessage().get(0);
	                //textRecu.setValue(textRecu.getValue() +u + ": " + m);
	                System.out.println("CHAT GUI SAYS: " + u + ": " + m);
	                Platform.runLater(new Runnable (){
	                	public void run() {
	                		taChatHist.setText(u + ": " + m);
	                    }
	                });

	    			//taChatHist.appendText("");
	    			cc.newUserMsg = false;
	    		}
	    	}
	        //return null;
	    }
	};


	@Override
	public void start(Stage primaryStage) throws Exception {

		cc.setUpNetworking();

        //======================CHAT SCREEN=================================
        GridPane chatGPane = new GridPane();
        chatGPane.setPadding(new Insets(20, 10, 20, 30));
        chatGPane.setHgap(5); chatGPane.setVgap(5);

        // drop down menu shows which clients available
/*        ChatServer server = new ChatServer(); //ISSUE HERE!!!
        cc.getUserList();
        while(!cc.userListUpdated);		//TODO: UNCOMMENT!!
        ObservableList<String> options = FXCollections.observableArrayList(cc.serverUserList); */
        ComboBox<String> cbMsgRecepient = new ComboBox<String>(options);
        GridPane.setColumnSpan(cbMsgRecepient, 3);
        Label lbRecipient = new Label("Recipient:");
        GridPane.setHalignment(lbRecipient, HPos.LEFT);

        //dropdown menu to change mode from individual <-> group chat
        ArrayList <String> modeOpt = new ArrayList<String>();
        modeOpt.add("Individual Chat"); modeOpt.add("Group Chat");
        ComboBox<String> cbModeSel = new ComboBox<String>(FXCollections.observableArrayList(modeOpt));
        cbModeSel.setValue(modeOpt.get(0));  //initial value
        GridPane.setColumnSpan(cbModeSel, 3);
        Label lbMode = new Label("Current Mode:");
        GridPane.setHalignment(lbMode, HPos.LEFT);

        //make send button
        Button btSend = new Button("Send");
        GridPane.setColumnSpan(btSend,1);

        TextField tfMsg = new TextField();
        GridPane.setColumnSpan(tfMsg,3);

        // create textbox for user to specify receiver
        Label lbRecip = new Label("Recipient:");
        GridPane.setHalignment(lbRecip, HPos.LEFT);
        TextField tfRecip = new TextField();
        GridPane.setColumnSpan(tfRecip,3);

        taChatHist = new TextArea();
        taChatHist.setPrefHeight(200);
        GridPane.setColumnSpan(taChatHist, 4);
        //taChatHist.textProperty().bind(textRecu);

        //chatGPane.add(lbRecipient, 0, 0);
        //chatGPane.add(cbMsgRecepient, 1, 0);
        chatGPane.add(lbMode, 0, 0);
        chatGPane.add(cbModeSel, 1, 0);
        chatGPane.add(taChatHist, 0, 1);
        chatGPane.add(lbRecip, 0, 2);
        chatGPane.add(tfRecip, 1, 2);
        chatGPane.add(tfMsg, 0, 3);
        chatGPane.add(btSend, 3, 3);

        Scene chatScn = new Scene(chatGPane, 350, 250);

        btSend.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	/*//method 1
            	String name = cbMsgRecepient.getValue();
            	String msg = tfMsg.getText();
            	cc.message(name, msg);*/

            	//method 2
            	if (!groupChatMode){
            		String name =tfRecip.getText();
                	String msg = tfMsg.getText();
                	cc.message(name, msg);
            	}
            	else{
            		String name =tfRecip.getText();
                	String msg = tfMsg.getText();
                	//cc.groupMessage(name, msg);
            	}

            }
        });

        // is group chat selected?
        cbModeSel.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue ov, String t, String t1) {
              if (cbModeSel.getValue().equals("Group Chat")){
            	  groupChatMode = true;
            	  btSend.setText("Send group");
            	  System.out.println("Mode: group chat");
              }

              else{
            	  groupChatMode = false;
            	  btSend.setText("Send");
            	  System.out.println("Mode: individual");
              }

            }

        });

/*        //make the text area autoscroll down
        taChatHist.textProperty().addListener(new ChangeListener<Object>() {
            @Override
            public void changed(ObservableValue<?> observable, Object oldValue,Object newValue) {
                taChatHist.setScrollTop(Double.MAX_VALUE); //this will scroll to the bottom
                //use Double.MIN_VALUE to scroll to the top
            }
        });*/

        // =========================POPUP WINDOW============================
        GridPane passGPane = new GridPane();
        Scene passScn = new Scene(passGPane, 320, 180);
        passGPane.setPadding(new Insets(20, 10, 20, 30));
        passGPane.setHgap(5); chatGPane.setVgap(5);

        //make labels and text fields
        //label, text field for old password
        Label lbOPass = new Label("Old Password:");
        GridPane.setHalignment(lbOPass, HPos.LEFT);
        GridPane.setColumnSpan(lbOPass,1);
        TextField tfOPass = new TextField();
        GridPane.setColumnSpan(tfOPass, 4);

        //label, text field for new password
        Label lbNPass = new Label("New Password:");
        GridPane.setHalignment(lbNPass, HPos.LEFT);
        GridPane.setColumnSpan(lbNPass,1);
        TextField tfNPass = new TextField();
        GridPane.setColumnSpan(tfNPass, 4);

        //make button
        Button btnConfirm = new Button("Confirm");

        passGPane.add(lbOPass, 0,0);
        passGPane.add(tfOPass,1,0);
        passGPane.add(lbNPass, 0,1);
        passGPane.add(tfNPass,1,1);
        passGPane.add(btnConfirm,2,2);

        // ============create login scene - displays at start===============
        GridPane loginGPane = new GridPane();
        loginGPane.setPadding(new Insets(40, 0, 0, 50));
        loginGPane.setHgap(5); loginGPane.setVgap(5);

        //Scene scene = new Scene(gridPane, 300, 150);

        Label lbUser = new Label("Username:");
        GridPane.setHalignment(lbUser, HPos.RIGHT);
        TextField tfUser = new TextField();
        GridPane.setColumnSpan(tfUser, 4);

        Label lbPass = new Label("Password:");
        GridPane.setHalignment(lbPass, HPos.RIGHT);
        PasswordField tfPass = new PasswordField();
        GridPane.setColumnSpan(tfPass,4);

        Button btLogin = new Button("Login");
        //GridPane.setMargin(btLogin, new Insets(10, 0, 0, 0));
        GridPane.setColumnSpan(btLogin, 2);

        Button btSignUp = new Button("Sign Up");
        GridPane.setColumnSpan(btSignUp, 2);

        Button btChngPass = new Button("Change Password");
        //btChngPass.setPrefWidth(120);
        GridPane.setColumnSpan(btChngPass, 5);
        //GridPane.setHalignment(btChngPass, HPos.CENTER);

        TextArea taErrors = new TextArea();
        taErrors.setPrefHeight(80);
        taErrors.setPrefWidth(0);GridPane.setHalignment(taErrors, HPos.CENTER);
        GridPane.setColumnSpan(taErrors, 7);

        loginGPane.add(lbUser, 0, 0);
        loginGPane.add(tfUser, 1, 0);
        loginGPane.add(lbPass, 0, 1);
        loginGPane.add(tfPass, 1, 1);
        loginGPane.add(btLogin, 1, 2);
        loginGPane.add(btSignUp,3, 2);
        loginGPane.add(btChngPass,1, 3);
        loginGPane.add(taErrors, 0,4);

        Scene loginScn = new Scene(loginGPane, 350, 250);
        primaryStage.setTitle("Chat client");
        primaryStage.setScene(loginScn);
        primaryStage.show();

        btLogin.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Pressed login");
                cc.login(tfUser.getText(), tfPass.getText());
                primaryStage.setTitle("Chat client - " + tfUser.getText());
               /* cc.getUserList();
                while(!cc.userListUpdated);		//TODO: UNCOMMENT!!
                options = FXCollections.observableArrayList(cc.serverUserList);*/

                // start chat background thread
                //backgroundThr = new Service <Void>(){


/*					@Override
					protected Task<Void> createTask() {
						return new Task<Void>(){
							@Override
							protected Void call() throws Exception {
								for (int x = 0){
									System.out.println("Background task running");
								}
								return null;
							}

						};
					}*/
                //};
                //btLogin.progressProperty().bind(task.progressProperty());
                new Thread(task).start();
                primaryStage.setScene(chatScn);
            }
        });

        btSignUp.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Pressed create account");
                cc.signup(tfUser.getText(), tfPass.getText());

            }
        });

        btChngPass.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Pressed change pass");
                Stage stgChangePass = new Stage();
                stgChangePass.setTitle("Change Password");
                stgChangePass.setScene(passScn);
                stgChangePass.show();

            }
        });




	}


}
