package com.elevenfifty.www.elevendates;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.elevenfifty.www.elevendates.Adapters.MessagesAdapter;
import com.elevenfifty.www.elevendates.Models.ChatMessageArrived;
import com.elevenfifty.www.elevendates.Models.DateChat;
import com.elevenfifty.www.elevendates.Models.DateUser;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.List;
import java.util.Timer;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

public class ChatActivity extends ListActivity {
    // Bind what we need from our layout
    @Bind(R.id.messageInput)
    TextView newMessage;

    @Bind(R.id.sendButton)
    ImageButton sendButton;

    private MessagesAdapter adapter;
    private String chatRoom;

    private ParseQuery<DateChat> query;

    private Timer timer;

    private ListView listView;

    private DateUser otherUser;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        listView = getListView();

        // Get our users that are in this chatroom, and create our query
        DateUser currentUser = (DateUser)DateUser.getCurrentUser();
        otherUser = DataStore.getInstance().getOtherUser();

        chatRoom = currentUser.getObjectId().compareTo(otherUser.getObjectId()) < 0 ?
                currentUser.getObjectId() + "-" + otherUser.getObjectId() :
                otherUser.getObjectId() + "-" + currentUser.getObjectId();

        query = ParseQuery.getQuery(DateChat.class);
        query.whereEqualTo(Constants.CHAT_ROOM, chatRoom);
        query.include(Constants.SENDER);
        query.orderByAscending("createdAt");


        // Close the soft keyboard by default - (casting a method)
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(newMessage.getWindowToken(), 0);


        // Create a timer for our ghetto "get new messages" plan
       /* timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                queryMessages();

            }
        }, 0, 5000);*/


        // Make the enter key on the keyboard send messages
        newMessage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_NULL &&
                        event.getAction() == KeyEvent.ACTION_DOWN) {
                    sendMessage();
                }
                return true;
            }
        });


        // Toss an on click listener on our button
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        queryMessages();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        // Cancel that timer, so all the things don't get mad at us
        //timer.cancel();
        EventBus.getDefault().unregister(this);
        super.onPause();

    }

    // Might be a good idea to create a function that calls our
    // Query for us and sets our listView adapter
    private void queryMessages() {
        query.findInBackground(new FindCallback<DateChat>() {
            @Override
            public void done(List<DateChat> list, ParseException e) {
                if (list != null) {
                    adapter = new MessagesAdapter(ChatActivity.this,
                            R.layout.message_row, list.toArray(new DateChat[list.size()]));
                    listView.setAdapter(adapter);
                }
            }
        });

    }


    // While we're at it, let's make a function to send new messages
    // as well.  You know, if you are into that sort of thing
    private void sendMessage() {
        if (!newMessage.getText().toString().equals("")) {
            DateChat dateChat = new DateChat();
            dateChat.setChatRoom(chatRoom);
            dateChat.setSender((DateUser) DateUser.getCurrentUser());
            dateChat.setChatText(newMessage.getText().toString());

            dateChat.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    ParsePush push = new ParsePush();
                    ParseQuery<DateUser> userQuery = ParseQuery.getQuery(DateUser.class);
                    userQuery.whereEqualTo(Constants.OBJECT_ID, otherUser.getObjectId());
                    ParseQuery<ParseInstallation> query =
                            ParseQuery.getQuery(ParseInstallation.class);
                    query.whereMatchesQuery("user", userQuery);

                    push.setQuery(query);
                    push.setMessage("You have a new chat message!");
                    push.sendInBackground();

                    newMessage.setText("");
                    queryMessages();
                }
            });
        }
    }

    public void onEvent(ChatMessageArrived event) {
        queryMessages();
    }
}
