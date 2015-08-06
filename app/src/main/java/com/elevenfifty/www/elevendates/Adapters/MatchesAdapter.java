package com.elevenfifty.www.elevendates.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.elevenfifty.www.elevendates.Models.DateMatch;
import com.elevenfifty.www.elevendates.R;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 *
 * Created by bkeck on 12/29/14.
 *
 */
public class MatchesAdapter extends ArrayAdapter<DateMatch> {
    // We'll have class level variables here for an inflater, resource
    // and context
    private LayoutInflater inflater;
    private int resource;
    private Context context;


    public MatchesAdapter(Context context, int resource, DateMatch[] objects) {
        super(context, resource, objects);
        // instantiate our class level variables
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.resource = resource;
        this.context = context;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // This is the final countdown... er... ViewHolder
        final ViewHolder holder;

        // Check if the current convertView is null so we know if we
        // need to inflate our layout
        if (convertView == null) {
            convertView = inflater.inflate(resource, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder)convertView.getTag();
        }


        // Get the item at this position
        DateMatch match = getItem(position);

        // Fill out the fields in our view.  We'll use Picasso to
        // generate a thumbnail for the user's profile image
        holder.matchName.setText(match.getTargetUser().displayText());
        Picasso.with(context).load(match.getTargetUser().getImage())
                .resize(80, 80).centerInside().into(holder.matchImage);

        // fulfill our requirement to return a View
        return convertView;
    }

    static class ViewHolder {
        // Bind our views here
        @Bind(R.id.matchName)
        TextView matchName;

        @Bind(R.id.matchImage)
        ImageView matchImage;

        // Create a constructor to hold our ButterKnife.bind call
        public ViewHolder(View view) {
            ButterKnife.bind(this, view);

        }
    }
}
