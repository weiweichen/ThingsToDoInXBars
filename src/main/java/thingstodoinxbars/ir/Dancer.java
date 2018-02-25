package main.java.thingstodoinxbars.ir;

/**
 * Created by weiweichen on 2/24/18.
 */
public class Dancer {
    public Dancer(Role role, Position position, int offset) {
		super();
		this.role = role;
		this.position = position;
		this.offset = offset;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	enum Role {
        RAVEN_ONE,
        LARK_ONE,
        RAVEN_TWO,
        LARK_TWO
    }

    // The dancer’s role.
    private Role role;

    // The dancer’s current position.
    private Position position;

    // With 0 being the “canonical” hands four for the dance
    // definition, positive numbers being down the hall, and
    // negative numbers being up the hall, which lark or raven
    // this is. That is LARK_ONE(offset=0) and
    // RAVEN_TWO(offset=0) are neighbors.
    // LARK_ONE(offset=0)’s next neighbor will be
    // RAVEN_TWO(offset=1), and their shadow might be
    // RAVEN_ONE(offset=-1).
    private int offset;
}
