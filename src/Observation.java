
/**
 * Storage object for observation data
 * @author Oliver Palmer
 *
 */
public class Observation {

	private float x;
	private float y;
	private float heading;
	private String color;

    public Observation(float x, float y, float heading, String color) {
        this.x = x;
        this.y = y;
        this.heading = heading;
        this.color = color;
    }

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getHeading() {
		return heading;
	}

	public void setHeading(float heading) {
		this.heading = heading;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String value) {
		this.color = value;
	}

	@Override
	public String toString() {
		return "Observation [x=" + x + ", y=" + y + ", heading=" + heading + ", color=" + color + "]";
	}
	
	
}
