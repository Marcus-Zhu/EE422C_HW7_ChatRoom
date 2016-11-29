package assignment7;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ChangeEvent;
//import javax.swing.event.ChangeListener;

import assignment7.ChatClient;

public class ClientGUI extends Application {
	ObservableList<String> options;
	ChatClient cc = new ChatClient();
	TextArea taChatHist;
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

        Label lbRecipient = new Label("Recipient:");
        GridPane.setHalignment(lbRecipient, HPos.LEFT);

        chatGPane.add(lbRecipient, 0, 0);
        chatGPane.add(cbMsgRecepient, 1, 0);
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
            	String name =tfRecip.getText();
            	String msg = tfMsg.getText();
            	cc.message(name, msg);
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



	}


}
