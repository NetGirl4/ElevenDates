package com.elevenfifty.www.elevendates;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.elevenfifty.www.elevendates.Adapters.MatchesAdapter;
import com.elevenfifty.www.elevendates.Models.DateMatch;
import com.elevenfifty.www.elevendates.Models.DateUser;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.List;

public class MatchesActivity extends ListActivity {
    private MatchesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matches);

        // We cheated here - we used ListActivity as our subclass
        // so we don't need ButterKnife on this view.  Yeah cheating!
        final ListView listView = getListView();


        // Let's get all our matches from Parse and then do something
        // with them
        ParseQuery<DateMatch> query = ParseQuery.getQuery(DateMatch.class);
        query.whereEqualTo(Constants.CURRENT_USER, DateUser.getCurrentUser());
        query.whereEqualTo(Constants.MUTUAL_MATCH, true);
        query.include(Constants.TARGET_USER);
        query.findInBackground(new FindCallback<DateMatch>() {
            @Override
            public void done(List<DateMatch> list, ParseException e) {
                adapter = new MatchesAdapter(MatchesActivity.this,
                        R.layout.match_row, list.toArray(new DateMatch[list.size()]));
                listView.setAdapter(adapter);
            }
        });
    }

    // We can subclass an onListItemClick method here to define
    // what happens when we click on a list item

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent intent = new Intent(this, ChatActivity.class);

        DateMatch match = adapter.getItem(position);
        DataStore.getInstance().setOtherUser(match.getTargetUser());

        startActivity(intent);
    }
}
