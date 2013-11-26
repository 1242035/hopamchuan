package com.hqt.hac.helper.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hac_library.components.ChordSurfaceView;
import com.hqt.hac.config.Config;
import com.hqt.hac.view.R;

import static com.hqt.hac.utils.LogUtils.makeLogTag;

public class ChordViewAdapter extends BaseAdapter {
    public static String TAG = makeLogTag(ChordViewAdapter.class);

    Context mContext;

    /** List all chords that adapter contains */
    String[] chords;

    /** currently index of chord */
    int[] index;

    public ChordViewAdapter(Context mContext, String[] chords) {
        this.mContext = mContext;
        this.chords = chords;
        index = new int[chords.length];
    }

    @Override
    public int getCount() {
        return chords.length;
    }

    @Override
    public Object getItem(int position) {
        return chords[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        View row = convertView;

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (row == null) {
            row = inflater.inflate(R.layout.list_item_chord_view, null);
            holder = new ViewHolder();
            holder.imageChord = (ChordSurfaceView) row.findViewById(R.id.chord_surface_view);
            holder.upButton = (ImageView) row.findViewById(R.id.up_button);
            holder.downButton = (ImageView) row.findViewById(R.id.down_button);
            holder.signTextView = (TextView) row.findViewById(R.id.text_view);
            row.setTag(holder);
        }
        else {
            holder = (ViewHolder) row.getTag();
        }

        /**
         * set height for this view base on screen
         */

        // set data
        // holder.imageChord.drawChord(chords[position], index[position]);
        holder.imageChord.drawChord("Am");
        holder.signTextView.setText(index[position] + "");

        // set action
        final ViewHolder finalHolder = holder;
        holder.upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalHolder.imageChord.nextPosition();
                ++index[position];
                index[position] = index[position] % Config.FRET_POSITION_PERIOD;
                finalHolder.signTextView.setText(index[position] + "");
            }
        });

        holder.downButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalHolder.imageChord.prevPosition();
                --index[position];
                if (index[position] < 0) index[position] = Config.FRET_POSITION_PERIOD;
                finalHolder.signTextView.setText(index[position] + "");
            }
        });


        return row;
    }

    public static class ViewHolder {
        ChordSurfaceView imageChord;
        ImageView upButton;
        ImageView downButton;
        TextView signTextView;
    }
}