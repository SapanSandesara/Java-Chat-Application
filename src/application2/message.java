package application2;

public class message {
	String code;
	String date;
	String sender;
	String message;

	public message(String code, String date, String sender, String message) {
		this.code = code;
		this.date = date;
		this.sender = sender;
		this.message = message;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String toString() {
//		if(this.code.equals("0")) {
//			return "NEW MESSAGE:" +date+" "+sender+" "+message;
//		}
//		else {
		return date + " " + sender + " " + message;
		// }
	}
}
