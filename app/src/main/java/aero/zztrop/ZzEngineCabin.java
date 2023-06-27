/** NEW PATTERN HOWTO
 * 
 *  Add the pattern label in values/strings.xml : <string-array name="patterns_cabin">
 *  add the pattern algorithm here in the computeRest method, be sure to have the exact
 *  same label as in the string-array !!! * 
 */

package aero.zztrop;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

public class ZzEngineCabin {

	// set the date time format for all user inputs
	private SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");

	protected Calendar calStart; // first rest starting time
	protected Calendar calEnd; // last rest or last service ending time
	private int pause; // pause time between rests or rest/service
	private int service, serviceLast; // service and last service duration
	private String pattern;

	protected ArrayList<TimePair> alTimePairs;

	public static final String PERIOD_TYPE_REST = "Rest";
	public static final String PERIOD_TYPE_SERVICE = "Service";

	private boolean pauseAfterService = false;

	public ZzEngineCabin() {
		this.setPauseAfterService(pauseAfterService);
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

	public void computeRest() {
		alTimePairs.clear();

		// Create time objects for different types of rests and amplitude
		Calendar rest = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		Calendar longRest = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		Calendar amp = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

		// initialize the calendar at January the 1st of 1970
		// to avoid daylight saving time problems
		rest.setTimeInMillis(0);
		amp.setTimeInMillis(0);

		// add one day if start is later than end
		if (calStart.after(calEnd)) {
			calEnd.add(Calendar.DAY_OF_MONTH, 1);
		}

		// calculate amplitude (first rest start until last rest/service end)
		amp.setTimeInMillis(calEnd.getTimeInMillis()
				- calStart.getTimeInMillis());

		// create a temporary time object that holds the latest end time value
		Calendar calTempStart = Calendar.getInstance(TimeZone
				.getTimeZone("UTC"));
		Calendar calTempEnd = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		calTempStart.setTimeInMillis(0);
		calTempEnd.setTimeInMillis(0);

		// test all patterns for matching
		// R • R • Serv.
		if (pattern.equals("(Srv) • R • R • Srv")) {
			// first, calculate the rest time
			rest.setTimeInMillis((amp.getTimeInMillis()
					- convertMinutesToMillis(pause * 2) - convertMinutesToMillis(serviceLast)) / 2);

			// round to the lower minute by zeroing seconds
			rest.set(Calendar.SECOND, 0);

			// 1st rest
			calTempStart.setTimeInMillis(calStart.getTimeInMillis());
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ rest.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 2nd rest
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ rest.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// service
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ convertMinutesToMillis(serviceLast));
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_SERVICE));
			return;
		}

		// R • Serv. • R
		if (pattern.equals("(Srv) • R • Srv • R")) {
			rest.setTimeInMillis((amp.getTimeInMillis()
					- convertMinutesToMillis(pause * 2) - convertMinutesToMillis(serviceLast)) / 2);

			// consider pause after Service
			if (pauseAfterService == false) {
				rest.add(Calendar.MINUTE, pause / 2);
			}

			// round to lower minute
			rest.set(Calendar.SECOND, 0);

			// 1st rest
			calTempStart.setTimeInMillis(calStart.getTimeInMillis());
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ rest.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// service
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ convertMinutesToMillis(serviceLast));
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_SERVICE));

			// 2nd rest
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			if (pauseAfterService) {
				calTempStart.add(Calendar.MINUTE, pause);
			}
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ rest.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));
			return;
		}

		// R • R • Serv. • R
		if (pattern.equals("(Srv) • R • R • Srv • R")) {
			rest.setTimeInMillis((amp.getTimeInMillis()
					- convertMinutesToMillis(pause * 3) - convertMinutesToMillis(serviceLast)) / 3);

			// consider pause after Service
			if (pauseAfterService == false) {
				rest.add(Calendar.MINUTE, pause / 3);
			}

			// round to lower minute
			rest.set(Calendar.SECOND, 0);

			// 1st rest
			calTempStart.setTimeInMillis(calStart.getTimeInMillis());
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ rest.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 2nd rest
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ rest.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// service
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ convertMinutesToMillis(serviceLast));
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_SERVICE));

			// 3rd rest
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			if (pauseAfterService) {
				calTempStart.add(Calendar.MINUTE, pause);
			}
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ rest.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));
			return;
		}

		// R • R • R • Serv.
		if (pattern.equals("(Srv) • R • R • R • Srv")) {
			rest.setTimeInMillis((amp.getTimeInMillis()
					- convertMinutesToMillis(pause * 3)
					- convertMinutesToMillis(service) - convertMinutesToMillis(serviceLast)) / 3);

			// round to lower minute
			rest.set(Calendar.SECOND, 0);

			// 1st rest
			calTempStart.setTimeInMillis(calStart.getTimeInMillis());
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ rest.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 2nd rest
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ rest.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 3rd rest
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ rest.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// last service
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ convertMinutesToMillis(serviceLast));
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_SERVICE));
			return;
		}

		// R • R • R • R • Serv.
		if (pattern.equals("(Srv) • R • R • R • R • Srv")) {
			rest.setTimeInMillis((amp.getTimeInMillis()
					- convertMinutesToMillis(pause * 4)
					- convertMinutesToMillis(service) - convertMinutesToMillis(serviceLast)) / 4);

			// round to lower minute
			rest.set(Calendar.SECOND, 0);

			// 1st rest
			calTempStart.setTimeInMillis(calStart.getTimeInMillis());
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ rest.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 2nd rest
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ rest.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 3rd rest
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ rest.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 4th rest
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ rest.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// last service
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ convertMinutesToMillis(serviceLast));
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_SERVICE));
			return;
		}

		// R • 3R • 3R • R • Serv.
		if (pattern.equals("(Srv) • R • 3R • 3R • R • Srv")) {
			rest.setTimeInMillis((amp.getTimeInMillis()
					- convertMinutesToMillis(pause * 4)
					- convertMinutesToMillis(service) - convertMinutesToMillis(serviceLast)) / 8);

			// round to lower minute
			rest.set(Calendar.SECOND, 0);

			longRest.setTimeInMillis(rest.getTimeInMillis() * 3);
			// round long rest to lower minute
			longRest.set(Calendar.SECOND, 0);

			// 1st rest
			calTempStart.setTimeInMillis(calStart.getTimeInMillis());
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ rest.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 2nd rest (long)
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ longRest.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 3rd rest (long)
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ longRest.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 4th rest
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ rest.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// last service
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ convertMinutesToMillis(serviceLast));
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_SERVICE));
			return;
		}

		// R • R • R • Serv. • R • R
		if (pattern.equals("(Srv) • R • R • R • Srv • R • R")) {
			rest.setTimeInMillis((amp.getTimeInMillis()
					- convertMinutesToMillis(pause * 5) - convertMinutesToMillis(serviceLast)) / 5);

			// consider pause after Service
			if (pauseAfterService == false) {
				rest.add(Calendar.MINUTE, pause / 5);
			}

			// round to lower minute
			rest.set(Calendar.SECOND, 0);

			// 1st rest
			calTempStart.setTimeInMillis(calStart.getTimeInMillis());
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ rest.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 2nd rest
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ rest.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 3rd rest
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ rest.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// service
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ convertMinutesToMillis(serviceLast));
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_SERVICE));

			// 4th rest
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			if (pauseAfterService) {
				calTempStart.add(Calendar.MINUTE, pause);
			}
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ rest.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 5th rest
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ rest.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));
			return;
		}

		// R • Serv. • R • Serv.
		if (pattern.equals("(Srv) • R • Srv • R • Srv")) {
			rest.setTimeInMillis((amp.getTimeInMillis()
					- convertMinutesToMillis(pause * 3)
					- convertMinutesToMillis(service) - convertMinutesToMillis(serviceLast)) / 2);

			// consider pause after Service
			if (pauseAfterService == false) {
				rest.add(Calendar.MINUTE, pause / 2);
			}

			// round to lower minute
			rest.set(Calendar.SECOND, 0);

			// 1st rest
			calTempStart.setTimeInMillis(calStart.getTimeInMillis());
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ rest.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// service
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ convertMinutesToMillis(service));
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_SERVICE));

			// 2nd rest
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			if (pauseAfterService) {
				calTempStart.add(Calendar.MINUTE, pause);
			}
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ rest.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// last service
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ convertMinutesToMillis(serviceLast));
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_SERVICE));
			return;
		}

		// R • R • Serv. • R • R • Serv.
		if (pattern.equals("(Srv) • R • R • Srv • R • R • Srv")) {

			rest.setTimeInMillis((amp.getTimeInMillis()
					- convertMinutesToMillis(pause * 5)
					- convertMinutesToMillis(service) - convertMinutesToMillis(serviceLast)) / 4);

			// consider pause after Service
			if (pauseAfterService == false) {
				rest.add(Calendar.MINUTE, pause / 4);
			}

			// round to lower minute
			rest.set(Calendar.SECOND, 0);

			// 1st rest
			calTempStart.setTimeInMillis(calStart.getTimeInMillis());
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ rest.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 2d rest
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ rest.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// service
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ convertMinutesToMillis(service));
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_SERVICE));

			// 3rd rest
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			if (pauseAfterService) {
				calTempStart.add(Calendar.MINUTE, pause);
			}
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ rest.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 4th rest
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ rest.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 2nd service
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ convertMinutesToMillis(serviceLast));
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_SERVICE));
			return;
		}

		// R • R • Serv. • 2R • 2R • Serv.
		if (pattern.equals("(Srv) • R • R • Srv • 2R • 2R • Srv")) {

			rest.setTimeInMillis((amp.getTimeInMillis()
					- convertMinutesToMillis(pause * 5)
					- convertMinutesToMillis(service) - convertMinutesToMillis(serviceLast)) / 6);

			// consider pause after Service
			if (pauseAfterService == false) {
				rest.add(Calendar.MINUTE, pause / 8);
			}
			// round to lower minute
			rest.set(Calendar.SECOND, 0);

			longRest.setTimeInMillis(rest.getTimeInMillis() * 2);
			// round long rest to lower minute
			longRest.set(Calendar.SECOND, 0);

			// 1st short rest
			calTempStart.setTimeInMillis(calStart.getTimeInMillis());
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ rest.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 2nd short rest
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ rest.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// service
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ convertMinutesToMillis(service));
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_SERVICE));

			// 1st long rest
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			if (pauseAfterService) {
				calTempStart.add(Calendar.MINUTE, pause);
			}
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ longRest.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 2nd long rest
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ longRest.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 2nd service
			// service
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ convertMinutesToMillis(serviceLast));
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_SERVICE));
			return;
		}

		// R • R • Serv. • R • Serv.
		if (pattern.equals("(Srv) • R • R • Srv • R • Srv")) {

			rest.setTimeInMillis((amp.getTimeInMillis()
					- convertMinutesToMillis(pause * 4)
					- convertMinutesToMillis(service) - convertMinutesToMillis(serviceLast)) / 3);

			// consider pause after Service
			if (pauseAfterService == false) {
				rest.add(Calendar.MINUTE, pause / 3);
			}

			// round to lower minute
			rest.set(Calendar.SECOND, 0);

			// 1st rest
			calTempStart.setTimeInMillis(calStart.getTimeInMillis());
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ rest.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 2nd rest
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ rest.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// service
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ convertMinutesToMillis(service));
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_SERVICE));

			// 3rd rest
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			if (pauseAfterService) {
				calTempStart.add(Calendar.MINUTE, pause);
			}
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ rest.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// last service
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ convertMinutesToMillis(serviceLast));
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_SERVICE));
			return;
		}

		// R • Serv. • R • R • Serv.
		if (pattern.equals("(Srv) • R • Srv • R • R • Srv")) {

			rest.setTimeInMillis((amp.getTimeInMillis()
					- convertMinutesToMillis(pause * 4)
					- convertMinutesToMillis(service) - convertMinutesToMillis(serviceLast)) / 3);

			// consider pause after Service
			if (pauseAfterService == false) {
				rest.add(Calendar.MINUTE, pause / 3);
			}

			// round to lower minute
			rest.set(Calendar.SECOND, 0);

			// 1st rest
			calTempStart.setTimeInMillis(calStart.getTimeInMillis());
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ rest.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// service
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ convertMinutesToMillis(service));
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_SERVICE));

			// 2nd rest
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			if (pauseAfterService) {
				calTempStart.add(Calendar.MINUTE, pause);
			}
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ rest.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 3rd rest
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ rest.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// last service
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ convertMinutesToMillis(serviceLast));
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_SERVICE));
			return;
		}

		// R • 3R • Serv. • R • 3R • Serv.
		if (pattern.equals("(Srv) • R • 3R • Srv • R • 3R • Srv")) {

			rest.setTimeInMillis((amp.getTimeInMillis()
					- convertMinutesToMillis(pause * 5)
					- convertMinutesToMillis(service) - convertMinutesToMillis(serviceLast)) / 8);

			// consider pause after Service
			if (pauseAfterService == false) {
				rest.add(Calendar.MINUTE, pause / 8);
			}
			// round to lower minute
			rest.set(Calendar.SECOND, 0);

			longRest.setTimeInMillis(rest.getTimeInMillis() * 3);
			// round long rest to lower minute
			longRest.set(Calendar.SECOND, 0);

			// 1st short rest
			calTempStart.setTimeInMillis(calStart.getTimeInMillis());
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ rest.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 1st long rest
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ longRest.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// service
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ convertMinutesToMillis(service));
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_SERVICE));

			// 2nd short rest
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			if (pauseAfterService) {
				calTempStart.add(Calendar.MINUTE, pause);
			}
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ rest.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 2nd long rest
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ longRest.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 2nd service
			// service
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ convertMinutesToMillis(serviceLast));
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_SERVICE));
			return;
		}

		// R • 3R • Serv. • 3R • R • Serv.
		if (pattern.equals("(Srv) • R • 3R • Srv • 3R • R • Srv")) {

			rest.setTimeInMillis((amp.getTimeInMillis()
					- convertMinutesToMillis(pause * 5)
					- convertMinutesToMillis(service) - convertMinutesToMillis(serviceLast)) / 8);

			// consider pause after Service
			if (pauseAfterService == false) {
				rest.add(Calendar.MINUTE, pause / 8);
			}
			// round to lower minute
			rest.set(Calendar.SECOND, 0);

			longRest.setTimeInMillis(rest.getTimeInMillis() * 3);
			// round long rest to lower minute
			longRest.set(Calendar.SECOND, 0);

			// 1st short rest
			calTempStart.setTimeInMillis(calStart.getTimeInMillis());
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ rest.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 1st long rest
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ longRest.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// service
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ convertMinutesToMillis(service));
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_SERVICE));

			// 2nd long rest
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			if (pauseAfterService) {
				calTempStart.add(Calendar.MINUTE, pause);
			}
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ longRest.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 2nd short rest
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ rest.getTimeInMillis());
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_REST));

			// 2nd service
			// service
			calTempStart.setTimeInMillis(calTempEnd.getTimeInMillis());
			calTempStart.add(Calendar.MINUTE, pause);
			calTempEnd.setTimeInMillis(calTempStart.getTimeInMillis()
					+ convertMinutesToMillis(serviceLast));
			alTimePairs.add(new TimePair(calTempStart, calTempEnd,
					PERIOD_TYPE_SERVICE));
			return;
		}
	}

	// takes every rest periods in the list, substract one
	// minute to each until they are dividable by 5
	public void roundTimes() {
		int n = 0; // number of "substract one minute" iterations
		TimePair tp = null; // a temporary timepair
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

	public void adjustTimes(int nbMinutes) {
		int n = 0; // number of times an adjustment is made
		for (int i = 0; i < alTimePairs.size(); i++) {
			// get the time pair
			TimePair tp = alTimePairs.get(i);
			if (tp.getStrType() == PERIOD_TYPE_REST) {

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

	public void setPause(int pause) {
		this.pause = pause;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public void setService(int service) {
		this.service = service;
	}

	public void setService(String strService) {
		try {
			this.service = convertMillisToMinutes(sdfTime.parse(strService)
					.getTime());
		} catch (ParseException e) {
		}
	}

	public void setServiceLast(int serviceLast) {
		this.serviceLast = serviceLast;
	}

	public void setServiceLast(String strServiceLast) {
		try {
			this.serviceLast = convertMillisToMinutes(sdfTime.parse(
					strServiceLast).getTime());
		} catch (ParseException e) {
		}
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

	public ArrayList<TimePair> getAlTimePairs() {
		return alTimePairs;
	}

	public String getCalStartAsString() {
		return sdfTime.format(calStart.getTime());
	}

	protected long convertMinutesToMillis(int minutes) {
		return minutes * 60 * 1000;
	}

	protected int convertMillisToMinutes(long millis) {
		return (int) millis / (60 * 1000);
	}

	public void setPauseAfterService(boolean pauseAfterService) {
		this.pauseAfterService = pauseAfterService;
	}
}
