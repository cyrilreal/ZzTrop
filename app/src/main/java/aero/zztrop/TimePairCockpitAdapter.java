package aero.zztrop;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TimePairCockpitAdapter extends ArrayAdapter<TimePair> {

	private ArrayList<TimePair> items;

	public TimePairCockpitAdapter(
			Context context,
			int textViewResourceId,
			ArrayList<TimePair> items) {
		super(context, textViewResourceId, items);
		this.items = items;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) this
					.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			switch (items.size()) {
			case 4:
				v = vi.inflate(R.layout.row_result_cockpit_4, null);
				break;
				
			case 5:
				v = vi.inflate(R.layout.row_result_cockpit_5, null);
				break;
				
			case 6:
				v = vi.inflate(R.layout.row_result_cockpit_6, null);
				break;

			default:
				v = vi.inflate(R.layout.row_result_cockpit, null);
				break;
			}
		}

		TimePair tp = items.get(position);
		if (tp != null) {
			TextView tvTimePair = (TextView) v.findViewById(R.id.tvTimePair);
			TextView tvDuration = (TextView) v.findViewById(R.id.tvDuration);
			if (tvTimePair != null) {
				if (tp.isSelected()) {
					tvTimePair.setTextColor(Color.MAGENTA);
					tvDuration.setTextColor(Color.MAGENTA);
				} else {
					tvTimePair.setTextColor(Color.WHITE);
					tvDuration.setTextColor(Color.WHITE);

				}

				tvTimePair.setText(tp.getTimePairAsString());
			}
			if (tvDuration != null) {
				tvDuration.setText(tp.getDurationString());
			}
		}

		return v;
	}
}
