/** NEW PATTERN HOWTO
 * 
 *  Add the pattern label in values/strings.xml : <string-array name="patterns_cockpit">
 *  add the pattern algorithm here in the computeRest method, be sure to have the exact
 *  same label as in the string-array !!! * 
 */

package aero.zztrop;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

public class ZzEngineCockpit {

	private static final String PERIOD_TYPE_REST = "Rest";

	// set the date time format for all user inputs
	private SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");

	private Calendar calStart;
	private Calendar calEnd;

	private int pause;
	private long xTime; // duration forced by user

	private int afterTkf, beforeLdg;

	private String pattern = null;
	protected ArrayList<TimePair> alTimePairs;

	public ZzEngineCockpit() {
		alTimePairs = new ArrayList<TimePair>();
		calStart = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		calEnd = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

		// initialize the calendar at January the 1st of 1970
		// to avoid daylight saving time problems
		calStart.setTimeInMillis(0);
		calEnd.setTimeInMillis(0);

		// prevent the date formatter from accepting invalid value
		// and set it to UTC reference
		sdfTime.setLenient(false);
		sdfTime.setTimeZone(TimeZone.getTimeZone("UTC"));
	}

	// if aftertkf and before ldg > 0, set new start and end time
	public void adjustStartEnd() {
		if (afterTkf > 0) {
			calStart.add(Calendar.MINUTE, afterTkf);
		}
		if (beforeLdg > 0) {
			calEnd.add(Calendar.MINUTE, -beforeLdg);
		}
	}

	public void computeRest() {
		alTimePairs.clear();

		// Create time objects for different types of rests and for amplitude
		Calendar restShort = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		restShort.setTimeInMillis(0);
		Calendar restLong = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		restLong.setTimeInMillis(0);
		Calendar amp = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

		// add one day if start is later than end
		if (calStart.after(calEnd)) {
			calEnd.add(Calendar.DAY_OF_MONTH, 1);
		}
		// compute time span between start and end
		amp.setTimeInMillis(calEnd.getTimeInMillis()
				- calStart.getTimeInMillis());

		// create a temporary time object that holds the latest end time value
		Calendar calTempStart = Calendar.getInstance(TimeZone
				.getTimeZone("UTC"));
		Calendar calTempEnd = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

		// PATTERN_R_R_R:
		if (pattern.equals("R / R / R")) {

			// compute short rest duration and round it to the
			// lower minute by zeroing seconds
			restShort
					.setTimeInMillis((amp.getTimeInMillis() - convertMinutesToMillis(pause * 2)) / 3);

			restShort.set(Calendar.SECOND, 0);

			// first time pair calculation
			calTempStart.setTimeInMillis(calStart.getTimeInMillis());

			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restShort.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// second time pair calculation
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);

			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restShort.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// third time pair calculation
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);

			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restShort.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));
			return;
		}

		// PATTERN_R_2R_2R_R:
		if (pattern.equals("R / 2R / 2R / R")) {

			// multiple of 6 with 3 pauses
			restShort
					.setTimeInMillis((amp.getTimeInMillis() - convertMinutesToMillis(pause * 3)) / 6);

			// long rest is 2 times the short one
			restLong.setTimeInMillis(restShort.getTimeInMillis() * 2);
			// let's round rest time to the lower minute
			// by zeroing seconds
			restShort.set(Calendar.SECOND, 0);
			restLong.set(Calendar.SECOND, 0);

			// first period (short)
			calTempStart.setTimeInMillis(calStart.getTimeInMillis());
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restShort.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// second period (long)
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restLong.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// third period (long)
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restLong.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// fourth period (short)
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restShort.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));
			return;
		}

		// PATTERN_R_R_R_R:
		if (pattern.equals("R / R / R / R")) {

			restShort
					.setTimeInMillis((amp.getTimeInMillis() - convertMinutesToMillis(pause * 3)) / 4);

			// let's round rest time to the lower minute
			// by zeroing seconds
			restShort.set(Calendar.SECOND, 0);

			// 1st period
			calTempStart.setTimeInMillis(calStart.getTimeInMillis());
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restShort.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 2nd period
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restShort.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 3rd period
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restShort.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 4th period
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restShort.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));
			return;
		}

		// PATTERN_R_R:
		if (pattern.equals("R / R")) {

			restLong.setTimeInMillis((amp.getTimeInMillis() - convertMinutesToMillis(pause)) / 2);

			// let's round rest time to the lower minute
			// by zeroing seconds
			restLong.set(Calendar.SECOND, 0);

			// 1st period
			calTempStart.setTimeInMillis(calStart.getTimeInMillis());
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restLong.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 2nd period
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restLong.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));
			return;
		}

		// PATTERN_R_2R_R:
		if (pattern.equals("R / 2R / R")) {

			restShort
					.setTimeInMillis((amp.getTimeInMillis() - convertMinutesToMillis(pause * 2)) / 4);

			// long rest is 2 times the short one
			restLong.setTimeInMillis(restShort.getTimeInMillis() * 2);
			// let's round rest time to the lower minute
			// by zeroing seconds
			restShort.set(Calendar.SECOND, 0);
			restLong.set(Calendar.SECOND, 0);

			// 1st period
			calTempStart.setTimeInMillis(calStart.getTimeInMillis());
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restShort.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 2nd period
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restLong.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 3rd period
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restShort.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));
			return;
		}

		// PATTERN_R_3R_2R:
		if (pattern.equals("R / 3R / 2R")) {

			restShort
					.setTimeInMillis((amp.getTimeInMillis() - convertMinutesToMillis(pause * 2)) / 6);

			// long rest is 3 times the short one
			restLong.setTimeInMillis(restShort.getTimeInMillis() * 3);

			// let's round rest time to the lower minute
			// by zeroing seconds
			restShort.set(Calendar.SECOND, 0);
			restLong.set(Calendar.SECOND, 0);

			// 1st period
			calTempStart.setTimeInMillis(calStart.getTimeInMillis());
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restShort.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 2nd period
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restLong.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 3rd period
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restShort.getTimeInMillis() * 2);
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			return;
		}

		// PATTERN_2R_3R_R:
		if (pattern.equals("2R / 3R / R")) {

			restShort
					.setTimeInMillis((amp.getTimeInMillis() - convertMinutesToMillis(pause * 2)) / 6);

			// long rest is 3 times the short one
			restLong.setTimeInMillis(restShort.getTimeInMillis() * 3);

			// let's round rest time to the lower minute
			// by zeroing seconds
			restShort.set(Calendar.SECOND, 0);
			restLong.set(Calendar.SECOND, 0);

			// 1st period
			calTempStart.setTimeInMillis(calStart.getTimeInMillis());
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restShort.getTimeInMillis() * 2);
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 2nd period
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restLong.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 3rd period
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restShort.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			return;
		}

		// PATTERN_R_R_2R_2R:
		// multiple of 6 with 3 pauses
		if (pattern.equals("R / R / 2R / 2R")) {

			restShort
					.setTimeInMillis((amp.getTimeInMillis() - convertMinutesToMillis(pause * 3)) / 6);

			// long rest is 2 times the short one
			restLong.setTimeInMillis(restShort.getTimeInMillis() * 2);
			// let's round rest time to the lower minute
			// by zeroing seconds
			restShort.set(Calendar.SECOND, 0);
			restLong.set(Calendar.SECOND, 0);

			// 1st period
			calTempStart.setTimeInMillis(calStart.getTimeInMillis());
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restShort.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 2nd period
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restShort.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 3rd period
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restLong.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 4th period
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restLong.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));
			return;
		}

		// PATTERN_R_R_3R_3R_3R_R (air Canada)
		// multiple of 12 with 5 pauses
		if (pattern.equals("R / R / 3R / 3R / 3R / R")) {

			restShort
					.setTimeInMillis((amp.getTimeInMillis() - convertMinutesToMillis(pause * 5)) / 12);

			// long rest is 3 times the short one
			restLong.setTimeInMillis(restShort.getTimeInMillis() * 3);

			// round to the lower minute by zeroing seconds
			restShort.set(Calendar.SECOND, 0);
			restLong.set(Calendar.SECOND, 0);

			// 1st period (short)
			calTempStart.setTimeInMillis(calStart.getTimeInMillis());
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restShort.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 2nd period (short)
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restShort.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 3rd period (long)
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restLong.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 4th period (long)
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restLong.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 5th period (long)
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restLong.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 6th period (short)
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restShort.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));
			return;
		}

		// PATTERN_R_R_R_R_R_R (Air Canada)
		if (pattern.equals("R / R / R / R / R / R")) {

			restShort
					.setTimeInMillis((amp.getTimeInMillis() - convertMinutesToMillis(pause * 5)) / 6);

			restShort.set(Calendar.SECOND, 0);

			// 1st time pair calculation
			calTempStart.setTimeInMillis(calStart.getTimeInMillis());

			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restShort.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 2nd time pair calculation
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);

			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restShort.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 3rd time pair calculation
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);

			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restShort.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 4th time pair calculation
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);

			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restShort.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 5th time pair calculation
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);

			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restShort.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 6th time pair calculation
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);

			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restShort.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));
			return;
		}

		// PATTERN_R_R_R_3R_3R_3R (Quantas)
		// multiple of 12 with 5 pauses
		if (pattern.equals("R / R / R / 3R / 3R / 3R")) {

			restShort
					.setTimeInMillis((amp.getTimeInMillis() - convertMinutesToMillis(pause * 5)) / 12);

			// long rest is 3 times the short one
			restLong.setTimeInMillis(restShort.getTimeInMillis() * 3);
			// round to the lower minute by zeroing seconds
			restShort.set(Calendar.SECOND, 0);
			restLong.set(Calendar.SECOND, 0);

			// 1st period (short)
			calTempStart.setTimeInMillis(calStart.getTimeInMillis());
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restShort.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 2nd period (short)
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restShort.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 3rd period (short)
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restShort.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 4th period (long)
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restLong.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 5th period (long)
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restLong.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 6th period (long)
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restLong.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));
			return;
		}

		// PATTERN_2R_2R_R_R multiple of 6 with 3 pauses
		if (pattern.equals("2R / 2R / R / R")) {

			restShort
					.setTimeInMillis((amp.getTimeInMillis() - convertMinutesToMillis(pause * 3)) / 6);

			// long rest is 2 times the short one
			restLong.setTimeInMillis(restShort.getTimeInMillis() * 2);
			// let's round rest time to the lower minute
			// by zeroing seconds
			restShort.set(Calendar.SECOND, 0);
			restLong.set(Calendar.SECOND, 0);

			// 1st period
			calTempStart.setTimeInMillis(calStart.getTimeInMillis());
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restLong.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 2nd period
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restLong.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 3rd period
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restShort.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 4th period
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restShort.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));
			return;
		}

		// PATTERN_R_4R_3R:
		if (pattern.equals("R / 4R / 3R")) {

			restShort
					.setTimeInMillis((amp.getTimeInMillis() - convertMinutesToMillis(pause * 2)) / 8);

			// long rest is 4 times the short one
			restLong.setTimeInMillis(restShort.getTimeInMillis() * 4);

			// let's round rest time to the lower minute
			// by zeroing seconds
			restShort.set(Calendar.SECOND, 0);
			restLong.set(Calendar.SECOND, 0);

			// 1st period
			calTempStart.setTimeInMillis(calStart.getTimeInMillis());
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restShort.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 2nd period
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restLong.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 3rd period
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restShort.getTimeInMillis() * 3);
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			return;
		}

		// PATTERN_R_R_2R_2R_2R_R (air Canada) multiple of 9 with 5 pauses
		if (pattern.equals("R / R / 2R / 2R / 2R / R")) {

			restShort
					.setTimeInMillis((amp.getTimeInMillis() - convertMinutesToMillis(pause * 5)) / 9);

			// long rest is 2 times the short one
			restLong.setTimeInMillis(restShort.getTimeInMillis() * 2);

			// round to the lower minute by zeroing seconds
			restShort.set(Calendar.SECOND, 0);
			restLong.set(Calendar.SECOND, 0);

			// 1st period (short)
			calTempStart.setTimeInMillis(calStart.getTimeInMillis());
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restShort.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 2nd period (short)
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restShort.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 3rd period (long)
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restLong.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 4th period (long)
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restLong.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 5th period (long)
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restLong.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 6th period (short)
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restShort.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));
			return;
		}

		// PATTERN_RminusX_R_X,
		// default r (sleep inertia) is 30'
		// compute short rest duration and round it to the
		// lower minute by zeroing seconds
		if (pattern.equals("R - x / R / x")) {

			restShort
					.setTimeInMillis((amp.getTimeInMillis() - convertMinutesToMillis(pause * 2)) / 2);

			restShort.set(Calendar.SECOND, 0);

			// first time pair calculation
			calTempStart.setTimeInMillis(calStart.getTimeInMillis());

			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restShort.getTimeInMillis() - xTime);
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// second time pair calculation
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);

			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restShort.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// third time pair calculation
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);

			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ xTime);
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));
			return;
		}

		// PATTERN_X_R_RminusX,
		// default r (sleep inertia) is 30'
		// compute short rest duration and round it to the
		// lower minute by zeroing seconds
		if (pattern.equals("x / R / R - x")) {

			restShort
					.setTimeInMillis((amp.getTimeInMillis() - convertMinutesToMillis(pause * 2)) / 2);

			restShort.set(Calendar.SECOND, 0);

			// first time pair calculation
			calTempStart.setTimeInMillis(calStart.getTimeInMillis());
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ xTime);
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// second time pair calculation
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);

			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restShort.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// third time pair calculation
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);

			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restShort.getTimeInMillis() - xTime);
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));
			return;
		}

		// PATTERN_RminusX_R_R_X (Air France),
		// default r (sleep inertia) is 30'
		// compute short rest duration and round it to the
		// lower minute by zeroing seconds
		if (pattern.equals("R - x / R / R / x")) {

			restShort
					.setTimeInMillis((amp.getTimeInMillis() - convertMinutesToMillis(pause * 3)) / 3);

			restShort.set(Calendar.SECOND, 0);

			// first time pair calculation
			calTempStart.setTimeInMillis(calStart.getTimeInMillis());

			calTempEnd.setTimeInMillis(calStart.getTimeInMillis()
					+ restShort.getTimeInMillis() - xTime);
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// second time pair calculation
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);

			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restShort.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// third time pair calculation
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);

			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restShort.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// fourth time pair calculation
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);

			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ xTime);
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));
			return;
		}

		// PATTERN_30m_R_R_1h:
		// substract 1h30 and 3 pauses from amplitude,
		// divide the remaining time in 2 equals periods
		if (pattern.equals("30\' / R / R / 1h")) {

			restLong.setTimeInMillis((amp.getTimeInMillis()
					- convertMinutesToMillis(90) - convertMinutesToMillis(pause * 3)) / 2);

			// let's round rest time to the lower minute
			// by zeroing seconds
			restLong.set(Calendar.SECOND, 0);

			// 1st period
			calTempStart.setTimeInMillis(calStart.getTimeInMillis());
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ convertMinutesToMillis(30));
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 2nd period
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restLong.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 3rd period
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restLong.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 4th period
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ convertMinutesToMillis(60));
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));
			return;
		}

		// PATTERN_30m_R_R_R_R_1h:
		// substract 1h30 and 5 pauses from amplitude,
		// divide the remaining time in 4 equals periods
		if (pattern.equals("30\' / R / R / R / R / 1h")) {

			restLong.setTimeInMillis((amp.getTimeInMillis()
					- convertMinutesToMillis(90) - convertMinutesToMillis(pause * 5)) / 4);

			// let's round rest time to the lower minute
			// by zeroing seconds
			restLong.set(Calendar.SECOND, 0);

			// 1st period
			calTempStart.setTimeInMillis(calStart.getTimeInMillis());
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ convertMinutesToMillis(30));
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 2nd period
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restLong.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 3rd period
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restLong.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 4thd period
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restLong.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 4th period
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restLong.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 6th period
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ convertMinutesToMillis(60));
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));
			return;
		}

		// PATTERN_X_X_R_R:
		// compute short rest duration and round it to the
		// lower minute by zeroing seconds
		if (pattern.equals("x / x / R / R")) {

			restShort.setTimeInMillis((amp.getTimeInMillis()
					- convertMinutesToMillis(pause * 3) - xTime * 2) / 2);

			restShort.set(Calendar.SECOND, 0);

			// first time pair calculation
			calTempStart.setTimeInMillis(calStart.getTimeInMillis());

			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ xTime);
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// second time pair calculation
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);

			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ xTime);
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// third time pair calculation
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);

			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restShort.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// fourth time pair calculation
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);

			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restShort.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));
			return;
		}

		// PATTERN_R_R_X_X:
		// compute short rest duration and round it to the
		// lower minute by zeroing seconds
		if (pattern.equals("R / R / x / x")) {

			restShort.setTimeInMillis((amp.getTimeInMillis()
					- convertMinutesToMillis(pause * 3) - xTime * 2) / 2);

			restShort.set(Calendar.SECOND, 0);

			// first time pair calculation
			calTempStart.setTimeInMillis(calStart.getTimeInMillis());

			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restShort.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// second time pair calculation
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);

			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restShort.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// third time pair calculation
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);

			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ xTime);
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// fourth time pair calculation
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);

			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ xTime);
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));
			return;
		}

		// PATTERN_X_X_X_R_R_R:
		// compute short rest duration and round it to the
		// lower minute by zeroing seconds

		if (pattern.equals("x / x / x / R / R / R")) {

			restShort.setTimeInMillis((amp.getTimeInMillis()
					- convertMinutesToMillis(pause * 5) - xTime * 3) / 3);

			restShort.set(Calendar.SECOND, 0);

			// first time pair calculation
			calTempStart.setTimeInMillis(calStart.getTimeInMillis());

			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ xTime);
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// second time pair calculation
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);

			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ xTime);
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// third timepair calculation
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);

			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ xTime);
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 4th time pair calculation
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);

			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restShort.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 5th time pair calculation
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);

			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restShort.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 6th time pair calculation
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);

			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restShort.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			return;
		}

		// PATTERN_R_R_R_X_X_X:
		// compute short rest duration and round it to the
		// lower minute by zeroing seconds

		if (pattern.equals("R / R / R / x / x / x")) {

			restShort.setTimeInMillis((amp.getTimeInMillis()
					- convertMinutesToMillis(pause * 5) - xTime * 3) / 3);

			restShort.set(Calendar.SECOND, 0);

			// first time pair calculation
			calTempStart.setTimeInMillis(calStart.getTimeInMillis());

			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restShort.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// second time pair calculation
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);

			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restShort.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 3rd time pair calculation
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);

			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restShort.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 4th time pair calculation
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);

			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ xTime);
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 5th time pair calculation
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);

			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ xTime);
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 6th time pair calculation
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);

			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ xTime);
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));
			return;
		}

		// PATTERN_R_X_R:
		// compute short rest duration and round it to the
		// lower minute by zeroing seconds
		if (pattern.equals("R / x / R")) {

			restShort.setTimeInMillis((amp.getTimeInMillis()
					- convertMinutesToMillis(pause * 2) - xTime) / 2);

			restShort.set(Calendar.SECOND, 0);

			// first time pair calculation
			calTempStart.setTimeInMillis(calStart.getTimeInMillis());

			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restShort.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 2nd time pair calculation
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);

			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ xTime);
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 3rd time pair calculation
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);

			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restShort.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));
			return;
		}

		// PATTERN_R_X_X_R:
		// compute short rest duration and round it to the
		// lower minute by zeroing seconds
		if (pattern.equals("R / x / x / R")) {

			restShort.setTimeInMillis((amp.getTimeInMillis()
					- convertMinutesToMillis(pause * 3) - xTime * 2) / 2);

			restShort.set(Calendar.SECOND, 0);

			// first time pair calculation
			calTempStart.setTimeInMillis(calStart.getTimeInMillis());

			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restShort.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 2nd time pair calculation
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);

			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ xTime);
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 3rd time pair calculation
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);

			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ xTime);
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 4th time pair calculation
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);

			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restShort.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));
			return;
		}

		// PATTERN_X_R_R_X:
		// compute short rest duration and round it to the
		// lower minute by zeroing seconds
		if (pattern.equals("x / R / R / x")) {

			restShort.setTimeInMillis((amp.getTimeInMillis()
					- convertMinutesToMillis(pause * 3) - xTime * 2) / 2);

			restShort.set(Calendar.SECOND, 0);

			// 1st time pair calculation
			calTempStart.setTimeInMillis(calStart.getTimeInMillis());

			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ xTime);
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 2nd time pair calculation
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);

			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restShort.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 3rd time pair calculation
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);

			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restShort.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 4th time pair calculation
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);

			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ xTime);
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));
			return;
		}

		// PATTERN_X_X_R_R_R_X:
		// compute short rest duration and round it to the
		// lower minute by zeroing seconds
		if (pattern.equals("x / x / R / R / R / x")) {

			restShort.setTimeInMillis((amp.getTimeInMillis()
					- convertMinutesToMillis(pause * 5) - xTime * 3) / 3);

			restShort.set(Calendar.SECOND, 0);

			// first time pair calculation
			calTempStart.setTimeInMillis(calStart.getTimeInMillis());

			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ xTime);
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// second time pair calculation
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);

			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ xTime);
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 3rd time pair calculation
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);

			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restShort.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 4th time pair calculation
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);

			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restShort.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 5th time pair calculation
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);

			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restShort.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 6th timepair calculation
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);

			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ xTime);
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));
			return;
		}

		// PATTERN_R_R_X_X_X_X:
		// compute short rest duration and round it to the
		// lower minute by zeroing seconds

		if (pattern.equals("R / R / x / x / x / x")) {

			restShort.setTimeInMillis((amp.getTimeInMillis()
					- convertMinutesToMillis(pause * 5) - xTime * 4) / 2);

			restShort.set(Calendar.SECOND, 0);

			// first time pair calculation
			calTempStart.setTimeInMillis(calStart.getTimeInMillis());

			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restShort.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// second time pair calculation
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);

			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restShort.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 3rd time pair calculation
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);

			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ xTime);
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 4th time pair calculation
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);

			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ xTime);
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 5th time pair calculation
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);

			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ xTime);
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 6th time pair calculation
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);

			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ xTime);
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));
			return;
		}
		// PATTERN_X_X_X_X_R_R:
		// compute short rest duration and round it to the
		// lower minute by zeroing seconds
		if (pattern.equals("x / x / x / x / R / R")) {

			restShort.setTimeInMillis((amp.getTimeInMillis()
					- convertMinutesToMillis(pause * 5) - xTime * 4) / 2);

			restShort.set(Calendar.SECOND, 0);

			// first time pair calculation
			calTempStart.setTimeInMillis(calStart.getTimeInMillis());

			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ xTime);
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// second time pair calculation
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);

			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ xTime);
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// third timepair calculation
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);

			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ xTime);
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 4th time pair calculation
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);

			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ xTime);
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 5th time pair calculation
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);

			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restShort.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 6th time pair calculation
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);

			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restShort.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			return;
		}

		// PATTERN_X_X_R_R-X_R-X:
		// compute short rest duration and round it to the
		// lower minute by zeroing seconds
		if (pattern.equals("x / x / R / R - x / R - x")) {

			restShort.setTimeInMillis((amp.getTimeInMillis()
					- convertMinutesToMillis(pause * 4)) / 3);

			restShort.set(Calendar.SECOND, 0);

			// first time pair calculation
			calTempStart.setTimeInMillis(calStart.getTimeInMillis());

			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ xTime);
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// second time pair calculation
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);

			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ xTime);
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// third timepair calculation
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);

			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restShort.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 4th time pair calculation
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);

			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restShort.getTimeInMillis() - xTime);
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 5th time pair calculation
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);

			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restShort.getTimeInMillis() - xTime);
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			return;
		}

		// PATTERN_R_R_2R_R_R:
		// compute short rest duration and round it to the
		// lower minute by zeroing seconds
		if (pattern.equals("R / R / 2R / R / R")) {

			restShort.setTimeInMillis((amp.getTimeInMillis()
					- convertMinutesToMillis(pause * 4)) / 6);

			restShort.set(Calendar.SECOND, 0);

			// first time pair calculation
			calTempStart.setTimeInMillis(calStart.getTimeInMillis());

			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restShort.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// second time pair calculation
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);

			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restShort.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// third timepair calculation
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);

			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ 2 * restShort.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 4th time pair calculation
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);

			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restShort.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 5th time pair calculation
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);

			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ restShort.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			return;
		}
	}

	// takes every rest periods in the list, substract one
	// minute to each until they are dividable by 5
	public void roundTimes() {
		int n = 0; // number of "substract one minute" iterations
		TimePair tp; // a temporary timepair
		for (int i = 0; i < alTimePairs.size(); i++) {
			// get the time pair
			tp = alTimePairs.get(i);
			if (tp.getStrType() == PERIOD_TYPE_REST) {
				long lg = tp.getDuration().getTimeInMillis();
				while (convertMillisToMinutes(lg) % 5 != 0) {
					lg -= convertMinutesToMillis(1);
					n++;
				}
				// set the new time pair end
				tp.getCalEnd().setTimeInMillis(
						tp.getCalStart().getTimeInMillis() + lg);
			}
			// adjust the next period accordingly
			if (i < alTimePairs.size() - 1) {
				tp = alTimePairs.get(i + 1);
				tp.getCalStart().add(Calendar.MINUTE, -n);
				tp.getCalEnd().add(Calendar.MINUTE, -n);
			}
		}

		// now round the start times
		n = 0; // reset number of "substract one minute" iterations
		// search how many minutes we have to substract
		// to round the start time
		tp = alTimePairs.get(0);
		int minutes = tp.getCalStart().get(Calendar.MINUTE);
		while (minutes % 5 != 0) {
			minutes--;
			n++;
		}
		for (int i = 0; i < alTimePairs.size(); i++) {
			// get the time pair
			tp = alTimePairs.get(i);
			// adjust
			tp.getCalStart().add(Calendar.MINUTE, -1 * n);
			tp.getCalEnd().add(Calendar.MINUTE, -1 * n);
		}
	}

	public void shiftTimes(int nbMinutes) {
		for (int i = 0; i < alTimePairs.size(); i++) {
			// get the time pair
			alTimePairs.get(i).getCalStart().add(Calendar.MINUTE, nbMinutes);
			alTimePairs.get(i).getCalEnd().add(Calendar.MINUTE, nbMinutes);
		}
	}

	public void modifyAllTimes(int nbMinutes) {
		int n = 0; // number of times an adjustment is made
		for (int i = 0; i < alTimePairs.size(); i++) {
			// get the time pair
			TimePair tp = alTimePairs.get(i);
			if (tp.getStrType().equals(PERIOD_TYPE_REST)) {

				// set the new time pair end
				tp.getCalEnd().add(Calendar.MINUTE, nbMinutes);
				n++;
			}
			// adjust the next period times accordingly
			if (i < alTimePairs.size() - 1) {
				TimePair tpNext = alTimePairs.get(i + 1);
				tpNext.getCalStart().add(Calendar.MINUTE, nbMinutes * n);
				tpNext.getCalEnd().add(Calendar.MINUTE, nbMinutes * n);
			}
		}
	}

	/**
	 * Adjust the time pair of a given period. id represent the position of the
	 * period in the arraylist. nbMinutes is the number of minutes to add (or
	 * substract)
	 */
	public void adjustTimes(int id, int nbMinutes) {
		// first adjust the given period
		TimePair tp = alTimePairs.get(id);
		tp.getCalEnd().add(Calendar.MINUTE, nbMinutes);
		// if nbMinutes > 0, "push" the periods after
		if (nbMinutes > 0) {
			for (int i = id + 1; i < alTimePairs.size(); i++) {
				// get the time pair
				tp = alTimePairs.get(i);
				// set the new time pair start and end
				tp.getCalStart().add(Calendar.MINUTE, nbMinutes);
				tp.getCalEnd().add(Calendar.MINUTE, nbMinutes);
			}
		}
		// if nbMinutes < 0, "push back" the periods after
		else if (nbMinutes < 0) {
			for (int i = id + 1; i < alTimePairs.size(); i++) {
				// get the time pair
				tp = alTimePairs.get(i);
				// set the new time pair start and end
				tp.getCalStart().add(Calendar.MINUTE, nbMinutes);
				tp.getCalEnd().add(Calendar.MINUTE, nbMinutes);
			}
		}

	}

	/**
	 * Modify the time pair of a given period. nbMinutes is the number of
	 * minutes to add (or substract)
	 */
	public void modifyTimePair(int id, int nbMinutes) {
		// remove/add nbMinutes from every timpePair except the id one
		// add/remove (nbMinutes * (number of timePair -1)) to the id one

		// create an array storing timepair durations
		Calendar[] durationsArray = new Calendar[alTimePairs.size()];

		for (int i = 0; i < alTimePairs.size(); i++) {
			durationsArray[i] = alTimePairs.get(i).getDuration();
		}

		// check that all rests will be > 0, if not rise a flag
		boolean flag = true;

		for (int i = 0; i < alTimePairs.size(); i++) {
			if (i == id) {
				if (nbMinutes < 0) {
					if (convertMillisToMinutes(durationsArray[i]
							.getTimeInMillis())
							+ nbMinutes
							* (alTimePairs.size() - 1) <= 0) {
						flag = false;
					}
				}
			} else {
				if (nbMinutes > 0) {
					if (convertMillisToMinutes(durationsArray[i]
							.getTimeInMillis()) - nbMinutes <= 0) {
						flag = false;
					}
				}
			}
		}

		// fill the array with the modified durations if flag is not true
		if (flag) {
			for (int i = 0; i < alTimePairs.size(); i++) {
				if (i == id) {
					durationsArray[i].add(Calendar.MINUTE, nbMinutes
							* (alTimePairs.size() - 1));
				} else {
					durationsArray[i].add(Calendar.MINUTE, -nbMinutes);
				}
			}
		}

		// re write the timePair array
		for (int i = 0; i < alTimePairs.size(); i++) {
			if (i == 0) {
				alTimePairs.get(i).getCalStart()
						.setTimeInMillis(calStart.getTimeInMillis());
			} else {
				// set start time to the preceeding end time...
				alTimePairs
						.get(i)
						.getCalStart()
						.setTimeInMillis(
								alTimePairs.get(i - 1).getCalEnd()
										.getTimeInMillis());
				// ... and add a pause
				alTimePairs.get(i).getCalStart().add(Calendar.MINUTE, pause);

			}
			// set end time
			alTimePairs
					.get(i)
					.getCalEnd()
					.setTimeInMillis(
							alTimePairs.get(i).getCalStart().getTimeInMillis()
									+ durationsArray[i].getTimeInMillis());
		}
	}

	protected int convertMillisToMinutes(long millis) {
		return (int) millis / (60 * 1000);
	}

	protected int convertMinutesToMillis(int minutes) {
		return minutes * 60 * 1000;
	}

	public Calendar getCalEnd() {
		return calEnd;
	}

	public void setCalEnd(Calendar calEnd) {
		this.calEnd = calEnd;
	}

	public void setCalEnd(String calEnd) {
		try {
			this.calEnd.setTimeInMillis(sdfTime.parse(calEnd).getTime());
		} catch (ParseException e) {
		}
	}

	public Calendar getCalStart() {
		return calStart;
	}

	public void setCalStart(Calendar calStart) {
		this.calStart = calStart;
	}

	public void setCalStart(String calStart) {
		try {
			this.calStart.setTimeInMillis(sdfTime.parse(calStart).getTime());
		} catch (ParseException e) {
		}
	}

	public void setPause(int pause) {
		this.pause = pause;
	}

	public void setxTime(long xTime) {
		this.xTime = xTime;
	}

	public void setAfterTkf(int afterTkf) {
		this.afterTkf = afterTkf;
	}

	public void setBeforeLdg(int beforeLdg) {
		this.beforeLdg = beforeLdg;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public ArrayList<TimePair> getAlTimePairs() {
		return alTimePairs;
	}

	public void selectTimePair(int index) {
		for (int i = 0; i < alTimePairs.size(); i++) {
			if (i == index) {
				alTimePairs.get(i)
						.setSelected(!alTimePairs.get(i).isSelected());
			} else {
				alTimePairs.get(i).setSelected(false);
			}
		}
	}

	public int getSelectedTimePair() {
		for (int i = 0; i < alTimePairs.size(); i++) {
			if (alTimePairs.get(i).isSelected()) {
				return i;
			}
		}
		// in case of error
		return -1;
	}
}
