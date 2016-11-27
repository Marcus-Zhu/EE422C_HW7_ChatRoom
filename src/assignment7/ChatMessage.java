package assignment7;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatMessage implements Serializable{

	private static final long serialVersionUID = 2541508903015946103L;

	static final int LOGIN = 1, SIGNUP = 4, CHANGEPWD = 5, MESSAGE = 6, SENDERR = 7,
			GROUPMSG = 8, USERLIST = 9, GROUPREQUEST = 10, FRIENDREQUEST = 11,
			FRIENDREQUESTACK = 12, HISTORYREQUEST = 2, LOGOUT = 3;
	static final String SUCCESS = "1", FAIL = "2";
	static final int TYPENUM = 12;
	private int type;
	private String timeStamp;
	private List<String> user;
	private List<String> message;

	ChatMessage(int type, String u, String m){
		this(type, new ArrayList<String>(), new ArrayList<String>());
		user = new ArrayList<String>();
		message = new ArrayList<String>();
		if (u != null) user.add(u);
		if (m != null) message.add(m);
	}

	ChatMessage(int type, List<String> ul, String m){
//		this(type, ul, new ArrayList<String>());
//		message.add(m);
	}

	ChatMessage(int type, List<String> ul, List<String> ml) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		if (type < 1 || type > TYPENUM){
			System.err.println("Invalid Type!");
			return;
		}
		this.timeStamp = sdf.format(new Date());
		this.type = type;
		this.user = ul;
		this.message = ml;
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

