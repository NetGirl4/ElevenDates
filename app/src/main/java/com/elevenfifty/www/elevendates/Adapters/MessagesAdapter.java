package com.elevenfifty.www.elevendates.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.elevenfifty.www.elevendates.Models.DateChat;
import com.elevenfifty.www.elevendates.Models.DateUser;
import com.elevenfifty.www.elevendates.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 *
 * Created by bkeck on 1/3/15.
 *
 */
public class MessagesAdapter extends ArrayAdapter<DateChat> {
    // Are there any class level variables we need?

    private LayoutInflater inflater;
    private int resource;
    private Context context;


    public MessagesAdapter(Context context, int resource, DateChat[] objects) {
        super(context, resource, objects);
        // Should we instantiate anything else here?
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.resource = resource;
        this.context = context;


    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // I left a ViewHolder around here somewhere...
        final ViewHolder holder;

        // OK, I found my ViewHolder... is anything null around these
        // here parts?
        if (convertView == null) {
            convertView = inflater.inflate(resource, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        // Now would be a good time to get the data for this message
        DateChat message = getItem(position);

        // Figure out who sent it, and populate the view properly
        if (message.getSender().getObjectId().equals(DateUser.getCurrentUser().getObjectId())) {
            holder.messageOther.setVisibility(View.INVISIBLE);
            holder.messageCurrent.setVisibility(View.VISIBLE);
            holder.messageCurrent.setText(message.getChatText());
        } else {
            holder.messageCurrent.setVisibility(View.INVISIBLE);
            holder.messageOther.setVisibility(View.VISIBLE);
            holder.messageOther.setText(message.getChatText());
        }

        return convertView;
    }

    static class ViewHolder {
        // You'll probably want to bind some things here.  I mean, you
        // don't have to, but things probably won't work if you don't

        // Bind our views here
        @Bind(R.id.messageOther)
        TextView messageOther;

        @Bind(R.id.messageCurrent)
        TextView messageCurrent;

        // Call the construction company...

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }



        // Create a constructor to hold our ButterKnife.bind call

    }
}
