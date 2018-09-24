package com.example.wojci.partyupapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.text.format.DateFormat;
import android.widget.Toast;


import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class ChatActivity extends AppCompatActivity {

    private static int SING_IN_REQUEST_CODE = 1;
    private FirebaseListAdapter<chatMessage> adapter;

///////////

    RelativeLayout activity_chat;
    FloatingActionButton fab;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()== R.id.menu_sing_out)
        {
            AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Snackbar.make(activity_chat,"You are no longer signed in",Snackbar.LENGTH_SHORT).show();
                }
            });
        }
        return true;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_main_menu, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode ==SING_IN_REQUEST_CODE)
        {
            if(resultCode ==RESULT_OK)
            {
                Snackbar.make(activity_chat,"Hello, can try to login to the app",Snackbar.LENGTH_SHORT).show();
                displayChatMessage();
            }
            else
            {
                Snackbar.make(activity_chat,"Something is wrong, try again later",Snackbar.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        activity_chat = (RelativeLayout) findViewById(R.id.activity_chat);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText input = (EditText)findViewById(R.id.input);
                FirebaseDatabase.getInstance().getReference().push().setValue(new chatMessage(input.getText().toString(),
                        FirebaseAuth.getInstance().getCurrentUser().getEmail()));
                input.setText("");
            }
        });
        //checking if user is singed-in , if not it is navigating to the paige where user can login
        if(FirebaseAuth.getInstance().getCurrentUser() == null)
        {
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(),SING_IN_REQUEST_CODE);
        }
        else
        {
            Snackbar.make(activity_chat,"Welcome" + FirebaseAuth.getInstance().getCurrentUser().getEmail(),Snackbar.LENGTH_SHORT).show();
            displayChatMessage();
        }
    }
    private void displayChatMessage()
    {
        ListView listOfMessage = (ListView)findViewById(R.id.list_of_message);
        Query query =FirebaseDatabase.getInstance().getReference();
        FirebaseListOptions<chatMessage> options = new FirebaseListOptions.Builder<chatMessage>()
                .setQuery(query, chatMessage.class)
                .setLayout(R.layout.chat_list_item)
                .build();
        ///////
        adapter = new FirebaseListAdapter<chatMessage>(options) {
        ////////
            @Override
            protected void populateView(View v, chatMessage model, int position) {

                //get references to the view of chat_list_item.
                TextView messageText,messageUser,messageTime;

                messageText = (TextView) v.findViewById(R.id.message_text);
                messageUser = (TextView) v.findViewById(R.id.message_user);
                messageTime = (TextView) v.findViewById(R.id.message_time);

                messageText.setText(model.getMessageText());
                messageUser.setText(model.getMessageUser());
                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",model.getMessageTime()));
            }
        };
        adapter.startListening();
        listOfMessage.setAdapter(adapter);
     }
}
