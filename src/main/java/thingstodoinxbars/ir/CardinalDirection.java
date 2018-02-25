package main.java.thingstodoinxbars.ir;

/**
 * Created by weiweichen on 2/24/18.
 */
public class CardinalDirection implements Direction {
    public CardinalDirection(Cardinal direction) {
		super();
		this.direction = direction;
	}
	public Cardinal getDirection() {
		return direction;
	}
	public void setDirection(Cardinal direction) {
		this.direction = direction;
	}
	enum Cardinal {
        UP,
        DOWN,
        IN,
        OUT
    }
    private Cardinal direction;

}
