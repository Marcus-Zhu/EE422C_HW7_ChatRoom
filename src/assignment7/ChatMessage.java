package assignment7;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ChatMessage implements Serializable{

	private static final long serialVersionUID = 2541508903015946103L;

	static final int MESSAGE = 1, LOGIN = 2, LOGOUT = 3, USERLIST = 4,
			GROUPREQUEST = 5, FRIENDREQUEST = 6, HISTORYREQUEST = 7;
	private int type;
	private String timeStamp;
	private List<String> user;
	private List<String> message;

	ChatMessage(int type, List<String> user, List<String> message) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		if (user == null || message == null){
			this.type = type;
			return;
		}
		if (type <= 3 && type >= 1 && user.size() != message.size()){
			this.type = -1;
			System.err.println("Length of user and message does not match!");
			return;
		}
		if (type < 1 || type > 7){
			System.err.println("Invalid Type!");
			return;
		}
		this.timeStamp = sdf.format(new Date());
		this.type = type;
		this.user = user;
		this.message = message;
	}

	protected int getType() {
		return type;
	}

	protected String getTimeStamp() {
		return timeStamp;
	}

	protected List<String> getUser() {
		return user;
	}

	protected List<String> getMessage() {
		return message;
	}

}

