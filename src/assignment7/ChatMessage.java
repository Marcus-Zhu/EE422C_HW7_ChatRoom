package assignment7;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.util.Pair;

public class ChatMessage implements Serializable{

	private static final long serialVersionUID = 2541508903015946103L;

	static final int LOGIN = 1, HISTORYREQUEST = 2, LOGOUT = 3, SIGNUP = 4,
			CHANGEPWD = 5, MESSAGE = 6, SENDERR = 7, GROUPMSG = 8,
			USERLIST = 9, GROUPREQUEST = 10, FRIENDREQUEST = 11, FRIENDREQUESTACK = 12;
    private static final Map<Integer, String> typeMap;
    static
    {
    	typeMap = new HashMap<Integer, String>();
    	typeMap.put(1, "LOGIN");
    	typeMap.put(2, "LOGIHISTORYREQUESTN");
    	typeMap.put(3, "LOGOUT");
    	typeMap.put(4, "SIGNUP");
    	typeMap.put(5, "CHANGEPWD");
    	typeMap.put(6, "MESSAGE");
    	typeMap.put(7, "SENDERR");
    	typeMap.put(8, "GROUPMSG");
    	typeMap.put(9, "USERLIST");
    	typeMap.put(10, "GROUPREQUEST");
    	typeMap.put(11, "FRIENDREQUEST");
    	typeMap.put(12, "FRIENDREQUESTACK");
    }
	static final String SUCCESS = "1", FAIL = "2";
	static final int TYPENUM = 12;
	private int type;
	private String timeStamp;
	private List<String> user;
	private List<String> message;
	private List<String> history;

	ChatMessage(int type, String u, String m){
		this(type, new ArrayList<String>(), new ArrayList<String>());
		user = new ArrayList<String>();
		message = new ArrayList<String>();
		if (u != null) user.add(u);
		if (m != null) message.add(m);
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

	public ChatMessage(int type, List<String> ul, ArrayList<Pair<String, String>> arrayList) {
		this(type, new ArrayList<String>(), new ArrayList<String>());
		this.user = ul;
		message = new ArrayList<String>();
		history = new ArrayList<String>();
		for (Pair<String, String> p : arrayList){
			message.add(p.getKey());
			history.add(p.getValue());
		}
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

	protected List<String> getHistory(){
		return history;
	}

	public String toString(){
		return timeStamp + " Type: " + typeMap.get(type) + "\n" + user + "\n" + message;
	}

}

