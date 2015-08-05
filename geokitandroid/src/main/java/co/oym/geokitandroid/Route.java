package co.oym.geokitandroid;

import com.mapbox.mapboxsdk.geometry.BoundingBox;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.text.MessageFormat;

import co.oym.mapbox_sdk.R;

/**
 * A Route represents the instructions and the geometry needed to get from a start point to an end point, eventually going through via(s) points.
 */
public class Route {

	/**
	 * A Route request.
	 */
	public static class Request {
		public static final int TM_PEDESTRIAN = 0;
		public static final int TM_FASTEST_CAR = 1;
		public static final int TM_PUBLIC_TRANSPORTATION = 2;

		public static final String UNIT_KM = "KM";
		public static final String UNIT_MILES = "MI";

		/** The route start coordinate in WGS84 **/
		public LatLng start;
		/** The route end coordinate in WGS84 **/
		public LatLng end;
		/** The route vias coordinates in WGS84 **/
		public java.util.List<LatLng> vias;
		/** The transport mode: Route.Request.TM_PEDESTRIAN or Route.Request.TM_FASTEST_CAR **/
		public int transportMode = TM_FASTEST_CAR;
		/** The distance unit: Route.Request.UNIT_KM or Route.Request.UNIT_MILES **/
		public String distanceUnit = UNIT_KM;
	}

	/**
	 * A Route response.
	 */
	public static class Response {
		/** A unique identifier for this route **/
		public String routeKey;
		/** The distance unit: Route.Request.UNIT_KM or Route.Request.UNIT_MILES **/
		public String distanceUnit;
		/** The total length of the route, depending on distanceUnit **/
		public float length;
		/** The total time of the route, in minutes **/
		public float time;
		/** A list of route instructions, for navigation **/
		public java.util.List<Instruction> instructions;
		/** A list of route sections, for navigation **/
		public java.util.List<Section> sections;
		/** A list of route vias, for navigation **/
		public java.util.List<Via> vias;
		/** The bounds of the route **/
		public BoundingBox bounds;
		/** The list of points for the route **/
		public java.util.List<LatLng> positions;
		/** A list of bitmask for each points in positions list **/
		public java.util.List<Integer> levels;

		@Override
		public String toString() {
			return "Response{" +
					"routeKey='" + routeKey + '\'' +
					", distanceUnit='" + distanceUnit + '\'' +
					", length=" + length +
					", time=" + time +
					", instructions=" + instructions +
					", sections=" + sections +
					", vias=" + vias +
					", bounds=" + bounds +
					", levels=" + levels +
					", positions=" + positions +
					'}';
		}
	}

	/**
	 * A Route instruction.
	 */
	public static class Instruction {
		/** The Id of the maneuver **/
		public int ID;
		/** The length of the maneuver **/
		public float length;
		/** The time in minutes of the maneuver **/
		public float time;
		/** The vertex index inside Route.positions list **/
		public int vertexIndex;
		/** The WGS84 coordinate of the maneuver **/
		public LatLng position;
		/** The attributes of the maneuver. See Route.Utility.render2() method to automatically decode attributes into a human readable format **/
		public java.util.Map<String,String> attributes;

		@Override
		public String toString() {
			return "Instruction{" +
					"ID=" + ID +
					", length=" + length +
					", time=" + time +
					", vertexIndex=" + vertexIndex +
					", position=" + position +
					", attributes=" + attributes +
					'}';
		}
	}

	/**
	 * A Route section.
	 */
	public static class Section {
		/** The type of section **/
		public String type;
		/** The start vertex of the section in the Route.positions list **/
		public int startVertex;
		/** The end vertex of the section in the Route.positions list **/
		public int endVertex;
		/** The start instruction in the Route.instructions list **/
		public int startInstruction;
		/** The end instruction in the Route.instructions list **/
		public int endInstruction;
		/** Some additional info about the type of section **/
		public String trunkCode;

		@Override
		public String toString() {
			return "Section{" +
					"type='" + type + '\'' +
					", startVertex=" + startVertex +
					", endVertex=" + endVertex +
					", startInstruction=" + startInstruction +
					", endInstruction=" + endInstruction +
					", trunkCode='" + trunkCode + '\'' +
					'}';
		}
	}

	/**
	 * A Route via.
	 */
	public static class Via {
		/** The vertex index inside Route.positions list **/
		public int vertexIndex;
		/** The WGS84 coordinate of the via **/
		public LatLng position;

		@Override
		public String toString() {
			return "Via{" +
					"vertexIndex=" + vertexIndex +
					", position=" + position +
					'}';
		}
	}

	/**
	 * Some utility methods to decode route isntructions into human readable format
	 */
	public static class Utility {

		/**
		 * Tells if a point at with given level value should be displayed a given zoom level.
		 *
		 * @param displayLevelValue
		 * @param zoomLevel
		 * @return
		 */
		public boolean checkDisplayLevel(int displayLevelValue, int zoomLevel) {
			/*
			bit 1 : level 00 + 01
			bit 2 : level 02 + 03
			bit 3 : level 04 + 05
			bit 4 : level 06 + 07
			bit 5 : level 08 + 09
			bit 6 : level 10 + 11
			bit 7 : level 12 + 13
			bit 8 : level >= 14
			*/
			final int comparableZ = 16 - zoomLevel;
			if (comparableZ < 14) {
				int bitmask = (int) Math.pow(2, Math.floor(comparableZ / 2));
				return ((displayLevelValue & bitmask) == bitmask);
			} else {
				int bitmask = (int) Math.pow(2, 14);
				return ((displayLevelValue & bitmask) == bitmask);
			}
		}

		//private static java.util.Map<String, String[]> lgResources = new java.util.HashMap<String, String[]>();


		public static String renderInstruction(Instruction i, android.content.res.Resources res) {
			if (res == null)
				return "no available resources"; //res = com.oym.android.impl.GeoKitContext.getContext().getResources();

			if (i == null) {
				return null;
			}

			String manType = null;
			String manHeading = null;
			String manRd = null;
			String manRdnr = null;
			String manDir = null;
			String manBranch = null;
			String adExitNum = null;
			String adExitRd = null;
			String adExitRdnr = null;
			String adToll = null;
			//String adPedest = null;
			//String adSkipRoadLeft = null;
			//String adSkipRoadRight = null;
			String adBridge = null;
			String adRoundAbout = null;
			String adTunnel = null;
			String adCross = null;
			String adPassBy = null;
			String adContinue = null;
			String adStay = null;
			String adPtType = null;
			//String adPtEnter = null;
			String adPtName = null;
			String adPtDir = null;
			String adPtStop = null;
			String adPtDelay = null;
			boolean isAd = false;

			java.util.Iterator<String> it = i.attributes.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				String value = i.attributes.get(key);
				if (key.equals("man-type")) manType = value;
				if (key.equals("man-heading")) manHeading = value;
				if (key.equals("man-rd")) manRd = value;
				if (key.equals("man-rdnr")) manRdnr = value;
				if (key.equals("man-dir")) manDir = value;
				if (key.equals("man-branch")) manBranch = value;
				if (key.equals("ad-exit-num")) adExitNum = value;
				if (key.equals("ad-exit-rd")) adExitRd = value;
				if (key.equals("ad-exit-rdnr")) adExitRdnr = value;
				if (key.equals("ad-toll")) adToll = value;
				//if (key.equals("ad-pedest")) adPedest = value;
				//if (key.equals("ad-skip-road-left")) adSkipRoadLeft = value;
				//if (key.equals("ad-skip-road-right")) adSkipRoadRight = value;
				if (key.equals("ad-bridge")) adBridge = value;
				if (key.equals("ad-roundabout")) adRoundAbout = value;
				if (key.equals("ad-tunnel")) adTunnel = value;
				if (key.equals("ad-cross")) adCross = value;
				if (key.equals("ad-passby")) adPassBy = value;
				if (key.equals("ad-continue")) adContinue = value;
				if (key.equals("ad-stay")) adStay = value;
				if (key.equals("ad-pt-type")) adPtType = value;
				//if (key.equals("ad-pt-enter")) adPtEnter = value;
				if (key.equals("ad-pt-name")) adPtName = value;
				if (key.equals("ad-pt-dir")) adPtDir = value;
				if (key.equals("ad-pt-stop")) adPtStop = value;
				if (key.equals("ad-pt-delay")) adPtDelay = value;
				if (key.indexOf("ad-") == 0) isAd = true;
			}

			if (manType.equals("HEAD")) {

				// heading
				String heading = "";
				if (manHeading.equals("N")) heading = res.getString(R.string.instr_heading_n);
				if (manHeading.equals("NE")) heading = res.getString(R.string.instr_heading_ne);
				if (manHeading.equals("E")) heading = res.getString(R.string.instr_heading_e);
				if (manHeading.equals("SE")) heading = res.getString(R.string.instr_heading_se);
				if (manHeading.equals("S")) heading = res.getString(R.string.instr_heading_s);
				if (manHeading.equals("SW")) heading = res.getString(R.string.instr_heading_sw);
				if (manHeading.equals("W")) heading = res.getString(R.string.instr_heading_w);
				if (manHeading.equals("NW")) heading = res.getString(R.string.instr_heading_nw);

				String on = (manRd != null || manRdnr != null) ? decodeRoad(manRd, manRdnr) : "";
				String toward = (manBranch != null) ? decodeBranch(manBranch) : "";

				// on_road
				String onMsg = !on.equals("") ? rebuild(res, R.string.instr_head_on_road, new Object[]{on}) : "";
				// toward
				String towardMsg = !toward.equals("") ? rebuild(res, R.string.instr_head_toward, new Object[]{toward}) : "";
				// final: heading + on_road + toward
				return rebuild(res, R.string.instr_head, new Object[]{heading, onMsg, towardMsg});


			} else if (manType.equals("CONTINUE")) {

				final String on = (manRd != null || manRdnr != null) ? decodeRoad(manRd, manRdnr) : "";
				final String bridge = (isAd && adBridge != null) ? adBridge : "";
				final String ra = (isAd && adRoundAbout != null) ? adRoundAbout : "";
				final String tunnel = (isAd && adTunnel != null) ? adTunnel : "";
				final String cross = (isAd && adCross != null) ? adCross : "";
				final String passby = (isAd && adPassBy != null) ? adPassBy : "";

				// on
				String onMsg = !on.equals("") ? rebuild(res, R.string.instr_continue_on_road, new Object[]{on}) : "";
				// advice
				String adviceMsg = (isAd && !bridge.equals("")) ? rebuild(res, R.string.instr_continue_advice_bridge, new Object[]{bridge}) : "";
				adviceMsg = (isAd && !ra.equals("")) ? rebuild(res, R.string.instr_continue_advice_roundabouts, new Object[]{ra}) : adviceMsg;
				adviceMsg = (isAd && !tunnel.equals("")) ? rebuild(res, R.string.instr_continue_advice_tunnel, new Object[]{tunnel}) : adviceMsg;
				adviceMsg = (isAd && !cross.equals("")) ? rebuild(res, R.string.instr_continue_advice_cross, new Object[]{cross}) : adviceMsg;
				adviceMsg = (isAd && !passby.equals("")) ? rebuild(res, R.string.instr_continue_advice_passby, new Object[]{passby}) : adviceMsg;

				// final: on_road + advice
				return rebuild(res, R.string.instr_continue, new Object[]{onMsg, adviceMsg});


			} else if (manType.equals("TURN")) {
				final String heading = decodeHeading2(manHeading, res);
				final String merge = (manBranch != null) ? decodeBranch(manBranch) : "";
				final String at = (manRd != null || manRdnr != null) ? decodeRoad(manRd, manRdnr) : "";

				// at
				final String atMsg = !at.equals("") ? rebuild(res, R.string.instr_turn_at, new Object[]{at}) : "";
				// merge
				final String mergeMsg = !merge.equals("") ? rebuild(res, R.string.instr_turn_merge, new Object[]{merge}) : "";
				// advice
				String advice = "";
				String adviceMsg = "";
				if (adContinue != null) {
					advice = decodeRoad(adContinue, null);
					adviceMsg = !advice.equals("") ? rebuild(res, R.string.instr_turn_advice_continue, new Object[]{advice}) : "";
				}
				if (advice.equals("") && adStay != null && adStay.equals("YES")) {
					advice = decodeRoad(null, manRdnr);
					adviceMsg = !advice.equals("") ? rebuild(res, R.string.instr_turn_advice_stay, new Object[]{advice}) : "";
				}

				// final: heading + at + merge + advice
				return rebuild(res, R.string.instr_turn, new Object[]{heading, atMsg, mergeMsg, adviceMsg});


			} else if (manType.equals("UTURN")) {
				return "";


			} else if (manType.equals("KEEPLEFT") || manType.equals("KEEPRIGHT")) {

				final String keepMsg = manType.equals("KEEPLEFT") ? res.getString(R.string.instr_keep_left) : res.getString(R.string.instr_keep_right);
				final String follow = (manDir != null) ? decodeDirection(manDir) : "";
				final String merge = (manBranch != null) ? decodeBranch(manBranch) : (manRd != null || manRdnr != null) ? decodeRoad(manRd, manRdnr) : "";

				// advice
				final String adviceMsg = (adToll != null) ? res.getString(R.string.instr_keep_advice_toll) : "";
				// follow
				final String followMsg = !follow.equals("") ? rebuild(res, R.string.instr_keep_follow, new Object[]{follow}) : "";
				// merge
				final String mergeMsg = !merge.equals("") ? rebuild(res, R.string.instr_keep_merge, new Object[]{merge}) : "";
				// final: heading + at + merge + advice
				return rebuild(res, R.string.instr_keep, new Object[]{keepMsg, followMsg, mergeMsg, adviceMsg});


			} else if (manType.equals("ROUNDABOUT")) {

				final String name = (manRd != null || manRdnr != null) ? decodeRoad(manRd, manRdnr) : "";
				String exitNumber = "";
				if (adExitNum.equals("1"))
					exitNumber = res.getString(R.string.instr_roundabout_exit_n1);
				if (adExitNum.equals("2"))
					exitNumber = res.getString(R.string.instr_roundabout_exit_n2);
				if (adExitNum.equals("3"))
					exitNumber = res.getString(R.string.instr_roundabout_exit_n3);
				if (adExitNum.equals("4"))
					exitNumber = res.getString(R.string.instr_roundabout_exit_n4);
				if (adExitNum.equals("5"))
					exitNumber = res.getString(R.string.instr_roundabout_exit_n5);
				if (adExitNum.equals("6"))
					exitNumber = res.getString(R.string.instr_roundabout_exit_n6);
				if (adExitNum.equals("7"))
					exitNumber = res.getString(R.string.instr_roundabout_exit_n7);
				if (adExitNum.equals("8"))
					exitNumber = res.getString(R.string.instr_roundabout_exit_n8);
				if (adExitNum.equals("9"))
					exitNumber = res.getString(R.string.instr_roundabout_exit_n9);
				final String onto = (adExitRd != null || adExitRdnr != null) ? decodeRoad(adExitRd, adExitRdnr) : "";


				// name
				final String nameMsg = !name.equals("") ? rebuild(res, R.string.instr_roundabout_name, new Object[]{name}) : res.getString(R.string.instr_roundabout_noname);
				// exit
				final String exitMsg = !exitNumber.equals("") ? rebuild(res, R.string.instr_roundabout_exit, new Object[]{exitNumber}) : "";
				// onto
				final String ontoMsg = !onto.equals("") ? rebuild(res, R.string.instr_roundabout_on_road, new Object[]{onto}) : "";

				// final: name + exit + on_road
				return rebuild(res, R.string.instr_roundabout, new Object[]{nameMsg, exitMsg, ontoMsg});


			} else if (manType.equals("RAMP")) {

				final String onto = (manBranch != null) ? decodeBranch(manBranch) : (manRd != null || manRdnr != null) ? decodeRoad(manRd, manRdnr) : "";
				final String toward = (manDir != null) ? decodeDirection(manDir) : "";

				// onto
				final String ontoMsg = !onto.equals("") ? rebuild(res, R.string.instr_ramp_on_road, new Object[]{onto}) : "";
				// toward
				final String towardMsg = !toward.equals("") ? rebuild(res, R.string.instr_ramp_toward, new Object[]{toward}) : "";

				// final: on_road + toward
				return rebuild(res, R.string.instr_ramp, new Object[]{ontoMsg, towardMsg});


			} else if (manType.equals("MERGE")) {

				if (manRd != null || manRdnr != null || manDir != null) {
					//final String heading = decodeHeading2(manHeading, res);
					final String onto = (manRd != null || manRdnr != null) ? decodeRoad(manRd, manRdnr) : "";
					final String toward = (manDir != null) ? decodeDirection(manDir) : "";

					// onto
					String ontoMsg = !onto.equals("") ? rebuild(res, R.string.instr_merge_on_road, new Object[]{onto}) : "";
					// toward
					final String towardMsg = !toward.equals("") ? rebuild(res, R.string.instr_merge_toward, new Object[]{toward}) : "";

					// final: on_road + toward
					return rebuild(res, R.string.instr_merge, new Object[]{ontoMsg, towardMsg});

				} else {
					// final: noname
					return rebuild(res, R.string.instr_merge_noname, new Object[]{});
				}


			} else if (manType.equals("EXIT")) {

				final String toward = (manDir != null) ? decodeDirection(manDir) : "";

				// exit_number
				final String exitNumberMsg = rebuild(res, R.string.instr_exit_number, new Object[]{adExitNum});
				// toward
				final String towardMsg = !toward.equals("") ? rebuild(res, R.string.instr_exit_toward, new Object[]{toward}) : "";

				// final: exit_number + toward
				return rebuild(res, R.string.instr_exit, new Object[]{exitNumberMsg, towardMsg});


			} else if (manType.equals("EMBARK")) {

				final String transportType = decodeTransportType2(adPtType, res) + (adPtName != null ? " " + adPtName : "");
				final String toward = (adPtDir != null) ? adPtDir : "";
				final String stop = (adPtStop != null) ? adPtStop : "";
				final String delay = (adPtDelay != null) ? renderTime2(adPtDelay, res) : "";

				// name
				final String nameMsg = rebuild(res, R.string.instr_embark_name, new Object[]{transportType});
				// toward
				final String towardMsg = !toward.equals("") ? rebuild(res, R.string.instr_embark_toward, new Object[]{toward}) : "";
				// next_stop
				final String nextStopMsg = !stop.equals("") ? rebuild(res, R.string.instr_embark_next_stop, new Object[]{stop}) : "";
				// wait_time
				final String waitTimeMsg = !delay.equals("") ? rebuild(res, R.string.instr_embark_wait_time, new Object[]{delay}) : "";

				// final: name + toward + next_stop + wait_time
				return rebuild(res, R.string.instr_embark, new Object[]{nameMsg, towardMsg, nextStopMsg, waitTimeMsg});


			} else if (manType.equals("CHANGE")) {

				final String transportType = decodeTransportType2(adPtType, res) + " " + adPtName;
				final String toward = (adPtDir != null) ? adPtDir : "";
				final String stop = (adPtStop != null) ? adPtStop : "";
				final String delay = (adPtDelay != null) ? renderTime2(adPtDelay, res) : "";

				// name
				final String nameMsg = rebuild(res, R.string.instr_change_name, new Object[]{transportType});
				// toward
				final String towardMsg = !toward.equals("") ? rebuild(res, R.string.instr_change_toward, new Object[]{toward}) : "";
				// next_stop
				final String nextStopMsg = !stop.equals("") ? rebuild(res, R.string.instr_change_next_stop, new Object[]{stop}) : "";
				// wait_time
				final String waitTimeMsg = !delay.equals("") ? rebuild(res, R.string.instr_change_wait_time, new Object[]{delay}) : "";

				// final: name + toward + next_stop + wait_time
				return rebuild(res, R.string.instr_change, new Object[]{nameMsg, towardMsg, nextStopMsg, waitTimeMsg});

			} else if (manType.equals("DISEMBARK")) {

				final String name = (manRd != null || manRdnr != null) ? decodeRoad(manRd, manRdnr) : "";
				final String nameMsg = !name.equals("") ? name : "";

				// final: name
				return rebuild(res, R.string.instr_disembark, new Object[]{nameMsg});


			} else if (manType.equals("TOLLBOOTH")) {
			} else if (manType.equals("BOATFERRY")) {
			} else if (manType.equals("RAILFERRY")) {
			} else if (manType.equals("LEAVEYOURCAR")) {

				final String advice = (manRd != null || manRdnr != null) ? decodeRoad(manRd, manRdnr) : "";
				// advice
				final String adviceMsg = !advice.equals("") ? rebuild(res, R.string.instr_leaveyourcar_advice, new Object[]{advice}) : "";

				// final: advice
				return rebuild(res, R.string.instr_leaveyourcar, new Object[]{adviceMsg});

			} else if (manType.equals("END")) {
				return res.getString(R.string.instr_end);
			}

			return "";
		}

		private static String rebuild(android.content.res.Resources res, int resId, final Object[] args) {
			final String template = res.getString(resId);
			return MessageFormat.format(template, args);
		}

		private static String decodeHeading2(String manHeading, android.content.res.Resources res) {
			if (manHeading != null) {
				if (manHeading.equals("LEFT1")) return res.getString(R.string.instr_turn_slight_left);
				if (manHeading.equals("LEFT2")) return res.getString(R.string.instr_turn_left);
				if (manHeading.equals("LEFT3")) return res.getString(R.string.instr_turn_sharp_left);
				if (manHeading.equals("RIGHT1"))
					return res.getString(R.string.instr_turn_slight_right);
				if (manHeading.equals("RIGHT2")) return res.getString(R.string.instr_turn_right);
				if (manHeading.equals("RIGHT3"))
					return res.getString(R.string.instr_turn_sharp_right);
				if (manHeading.equals("CROSS")) return res.getString(R.string.instr_turn_cross);
			}
			return "";
		}

		private static String decodeTransportType2(String ptType, android.content.res.Resources res) {
			if (ptType != null) {
				if (ptType.equals("BOATFERRY"))
					return res.getString(R.string.instr_embark_type_boatferry);
				if (ptType.equals("RAILFERRY"))
					return res.getString(R.string.instr_embark_type_railferry);
			}
			return ptType;
		}

		private static String renderTime2(String time, android.content.res.Resources res) {
			String t = "";
			int tmp = Integer.parseInt(time) / 60;
			if (tmp > 0) {
				if (tmp == 1) t += "1 " + res.getString(R.string.instr_render_time_hour);
				else t += tmp + " " + res.getString(R.string.instr_render_time_hours);
				t += " ";
			}
			tmp = Integer.parseInt(time) % 60;
			if (tmp <= 1) t += "1 " + res.getString(R.string.instr_render_time_minute);
			else t += tmp + " " + res.getString(R.string.instr_render_time_minutes);

			return t;
		}

		private static String decodeTransportType(String ptType) {
			return ptType;
		}

		private static String decodeBranch(String manBranch) {
			if (manBranch != null) {
				return manBranch.replaceAll("§", "");
			}
			return manBranch;
		}

		private static String decodeDirection(String manDir) {
			if (manDir != null) {
				String s = manDir;
				s = s.replaceAll("§", "");
				s = s.replaceAll("\\/\\/", " / ");
				s = s.replaceAll("\\+", " / ");
				s = s.replaceAll("\\-", " / ");
				return s;
			}
			return manDir;
		}

		private static String decodeRoad(String manRd, String manRdnr) {
			if (manRd != null) {
				return manRd + (manRdnr != null ? " (" + manRdnr + ")" : "");
			} else {
				return manRdnr;
			}
		}

		private static String decodeHeading(String manHeading, android.content.res.Resources res) {
			if (manHeading != null) {
				if (manHeading.equals("LEFT1")) return res.getString(R.string.instr_9);
				if (manHeading.equals("LEFT2")) return res.getString(R.string.instr_10);
				if (manHeading.equals("LEFT3")) return res.getString(R.string.instr_11);
				if (manHeading.equals("RIGHT1")) return res.getString(R.string.instr_12);
				if (manHeading.equals("RIGHT2")) return res.getString(R.string.instr_13);
				if (manHeading.equals("RIGHT3")) return res.getString(R.string.instr_14);
			}
			return "";
		}
	}
}
