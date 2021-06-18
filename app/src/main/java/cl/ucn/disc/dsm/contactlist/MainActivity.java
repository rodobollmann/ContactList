package cl.ucn.disc.dsm.contactlist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.provider.FontsContractCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    //Initialize variable
    RecyclerView recyclerView;
    ArrayList<ContactModel> arrayList = new ArrayList<ContactModel>();
    MainAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Assign variable
        recyclerView = findViewById(R.id.recycler_view);

        //check permission
        checkPermission();

    }

    private void checkPermission() {
        //Check condition
        if (ContextCompat.checkSelfPermission(MainActivity.this
                , Manifest.permission.READ_CONTACTS)
        != PackageManager.PERMISSION_GRANTED){
            //When permission is not granted
            //Request permission
            ActivityCompat.requestPermissions(MainActivity.this
                    , new String[]{Manifest.permission.READ_CONTACTS}, 100);
        }else{
            //When permission is granted
            //Create method
            getContactList();
        }
    }

    private void getContactList() {
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        //Sort by ascending
        String sort = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC";
        //Initialize cursor
        Cursor cursor = getContentResolver().query(uri, null, null, null, sort);
        //Check condition
        if (cursor.getCount() > 0){
            //When count is greater than 0
            //Use while loop
            while (cursor.moveToNext()){
                String id = cursor.getString(cursor.getColumnIndex(
                        ContactsContract.Contacts._ID
                ));
                //Get contact name
                String name = cursor.getString(cursor.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME
                ));
                //Initialize phone uri
                Uri uriPhone = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                //Initialize selection
                String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " =?";
                //Initialize phone cursor
                Cursor phoneCursor = getContentResolver().query(
                        uriPhone, null, selection
                        , new String[]{id}, null
                );
                //Check condition
                if (phoneCursor.moveToNext()){
                    //When phone cursor move to next
                    String number = phoneCursor.getString(phoneCursor.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.NUMBER
                    ));
                    //Initialize contact model
                    ContactModel model = new ContactModel();
                    //Set name
                    model.setName(name);
                    //Set number
                    model.setNumber(number);
                    //add model in array list
                    arrayList.add(model);
                    //Close phone cursor
                    phoneCursor.close();

                }
            }
            //Close cursor
            cursor.close();
        }
        //set layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //Initialize adapter
        adapter = new MainAdapter(this, arrayList);
        // Set adapter
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //Check condition
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0]
                == PackageManager.PERMISSION_GRANTED){
            //When permission is granted
            //Call method
            getContactList();
        }else {
            //When permission is denied
            //Display toast
            Toast.makeText(MainActivity.this, "Permission Denied."
            , Toast.LENGTH_SHORT).show();
            //Call check permission method
            checkPermission();
        }
    }
}