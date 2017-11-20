package objects.message;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class SettingsMessage extends Message {

	private static final long serialVersionUID = -8795204841944741761L;
	
	private Integer uid;
	Color color;
	Font font;
	
	public SettingsMessage(Integer uid,Color color,Font font) {
		this.uid = uid;
		this.color = color;
		this.font = font;
	}
	
	public Integer getUID() {
		return uid;
	}
	
	public Color getColor() {
		return color;
	}
	
	public Font getFont() {
		return font;
	}
}