package duarte.gabriel.chat;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    FirebaseUser myUserFirebase;
    User myUser;
    private TextView noChannelTextView;
    private ListView channelsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //.setAction("Action", null).show();
                findUsers();
            }
        });
        */
        myUserFirebase = FirebaseAuth.getInstance().getCurrentUser();
        if(myUserFirebase != null){
            myUser = new User(myUserFirebase.getDisplayName(), myUserFirebase.getEmail(), myUserFirebase.getPhotoUrl().toString(), myUserFirebase.getUid().toString(), FirebaseInstanceId.getInstance().getToken());
        }
        else
            goLogin();


        channelsListView = (ListView) findViewById(R.id.chanellListView);
        noChannelTextView = (TextView) findViewById(R.id.noChannelTextView);
        noChannelTextView.setVisibility(View.GONE);
        findUsers();
    }

    private void goLogin(){
        Intent intent = new Intent(this, LoginScreenActivity.class);
        startActivity(intent);
        finish();
    }

    private void findUsers(){
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("user");

        final ListView lv = (ListView)findViewById(R.id.chanellListView);
        final List<User> users = new ArrayList<>();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {
                    String name = (String) messageSnapshot.child("name").getValue();
                    String email = (String) messageSnapshot.child("email").getValue();
                    String photo = (String) messageSnapshot.child("photoUri").getValue();
                    String uid = (String) messageSnapshot.child("uId").getValue();
                    String fcmId = (String) messageSnapshot.child("fcmId").getValue();
                    users.add(new User(name, email, photo, uid, fcmId));

                }
                UserListAdapter mAdapter = new UserListAdapter(MainActivity.this, R.layout.user_list, users);
                lv.setAdapter(mAdapter);
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        clickedChat(users.get(position));
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void clickedChat(User user){
        //Toast.makeText(this, user.getEmail(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("me", myUser);
        intent.putExtra("friend", user);
        startActivity(intent);

    }
}
