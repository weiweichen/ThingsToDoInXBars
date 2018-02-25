package main.java.thingstodoinxbars.ir;

/**
 * Created by weiweichen on 2/24/18.
 */
public class FacingDirection implements Direction {
    public FacingDirection(Dancer direction) {
		super();
		this.direction = direction;
	}

	public Dancer getDirection() {
		return direction;
	}

	public void setDirection(Dancer direction) {
		this.direction = direction;
	}

	private Dancer direction;

}
