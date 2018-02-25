package main.java.thingstodoinxbars.ir;

/**
 * Created by weiweichen on 2/24/18.
 */
public final class Position {
	public Position(int position, int offset) {
		super();
		this.position = position;
		this.offset = offset;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	private int position;
	private int offset;
}
