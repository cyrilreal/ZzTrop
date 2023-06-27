package aero.zztrop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class PatternFilterAdapter extends ArrayAdapter<PatternFilter> {

	private PatternFilter[] items;

	public PatternFilterAdapter(
			Context context,
			int textViewResourceId,
			PatternFilter[] items) {
		super(context, textViewResourceId, items);
		this.items = items;
	}

	private class ViewHolder {
		TextView pattern;
		CheckBox cbx;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;

		if (convertView == null) {
			LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			convertView = vi.inflate(R.layout.row_pattern_filter, null);

			holder = new ViewHolder();
			holder.pattern = convertView
					.findViewById(R.id.tvPattern);
			holder.cbx = (CheckBox) convertView.findViewById(R.id.cbxPattern);
			convertView.setTag(holder);

			holder.cbx.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					CheckBox cb = (CheckBox) v;
					PatternFilter pf = (PatternFilter) cb.getTag();

					pf.setChecked(cb.isChecked());
				}
			});
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}

		PatternFilter pf = items[position];
		holder.pattern.setText(pf.getPattern());
		holder.cbx.setChecked(pf.isChecked());
		holder.cbx.setTag(pf);

		return convertView;
	}
}
