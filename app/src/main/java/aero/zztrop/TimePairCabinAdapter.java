package aero.zztrop;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TimePairCabinAdapter extends ArrayAdapter<TimePair> {

	private ArrayList<TimePair> items;

	public TimePairCabinAdapter(Context context, int textViewResourceId,
			ArrayList<TimePair> items) {
		super(context, textViewResourceId, items);
		this.items = items;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) this.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			switch (items.size()) {
			case 5:
				v = vi.inflate(R.layout.row_result_cabin_5, null);
				break;

			case 6:
				v = vi.inflate(R.layout.row_result_cabin_6, null);
				break;

			default:
				v = vi.inflate(R.layout.row_result_cabin, null);
				break;
			}
		}

		TimePair tp = items.get(position);
		if (tp != null) {
			TextView tvTimePair = (TextView) v.findViewById(R.id.tvTimePair);
			TextView tvDuration = (TextView) v.findViewById(R.id.tvDuration);
			TextView tvPeriodType = (TextView) v
					.findViewById(R.id.tvPeriodType);
			if (tvTimePair != null) {
				tvTimePair.setText(tp.getTimePairAsString());
			}
			if (tvDuration != null) {
				tvDuration.setText(tp.getDurationString());
			}
			if (tvPeriodType != null) {
				if (tp.getStrType() == ZzEngineCabin.PERIOD_TYPE_REST) {
					tvPeriodType.setText(R.string.rest_UPPERCASE);
				} else if (tp.getStrType() == ZzEngineCabin.PERIOD_TYPE_SERVICE) {
					tvPeriodType.setText(R.string.service_UPPERCASE);
				}
			}
		}

		return v;
	}
}
