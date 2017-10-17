package objects;

import java.io.Serializable;

abstract class Message implements Serializable{
	private static final long serialVersionUID = 1L;
	int type;
}
