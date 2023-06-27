package aero.zztrop;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class TimePair {
	
	private Calendar calStart;		
	private Calendar calEnd;
	private String strType;	//rest or service
	private boolean isSelected = false;	//is selected or not in the result list

	public static final String PERIOD_TYPE_REST = "Rest";

	/**
	 * @return the calStart
	 */
	public Calendar getCalStart() {
		return calStart;
	}

	/**
	 * @return the calEnd
	 */
	public Calendar getCalEnd() {
		return calEnd;
	}

	/**
	 * @return the strType
	 */
	public String getStrType() {
		return strType;
	}
	
	public TimePair(){
		calStart = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		calEnd = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		strType = null;
	}
	
	public TimePair(Calendar cal1, Calendar cal2, String type){
		calStart = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		calEnd = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		calStart.setTimeInMillis(cal1.getTimeInMillis());
		calEnd.setTimeInMillis(cal2.getTimeInMillis());
		strType = type;
	}

	public Calendar getDuration(){
		Calendar calDuration = Calendar.getInstance(
				TimeZone.getTimeZone("UTC"));
		calDuration.setTimeInMillis(
				calEnd.getTimeInMillis() - calStart.getTimeInMillis());
		return calDuration;
	}

	public String getDurationString(){
		SimpleDateFormat sdf = new SimpleDateFormat("H'h'mm");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		Calendar calDuration = Calendar.getInstance(
				TimeZone.getTimeZone("UTC"));
		calDuration.setTimeInMillis(
				calEnd.getTimeInMillis() - calStart.getTimeInMillis());
		return sdf.format(calDuration.getTime());
	}
	
	public String getTimePairAsString() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		StringBuilder sb = new StringBuilder();
		sb.append(sdf.format(calStart.getTime()));
		sb.append(" / ");
		sb.append(sdf.format(calEnd.getTime()));
		return sb.toString();
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public boolean isSelected() {
		return isSelected;
	}
}
