package objects.message;

import java.io.Serializable;

//An Abstract class that will allow for any subclass to be send to and from client/server
public abstract class Message implements Serializable {
	private static final long serialVersionUID = 1L;
}
