package aero.zztrop;

public class PatternFilter {

	private String pattern;
	private boolean checked;

	public PatternFilter(String pattern, boolean checked) {
		this.pattern = pattern;
		this.checked = checked;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}
}
