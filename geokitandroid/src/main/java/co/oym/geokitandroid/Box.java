package co.oym.geokitandroid;

/**
 * A 2D-box in WGS84 coordinates. <br>
 * -180 < west and east < 180 <br>
 * -90 < north and south < 90 <br>
 */
public class Box {

	public double north;
	public double east;
	public double south;
	public double west;

	@Override
	public String toString() {
		return "Box{" +
				"west=" + west +
				", south=" + south +
				", east=" + east +
				", north=" + north +

				'}';
	}
}
