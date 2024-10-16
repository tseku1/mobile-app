package com.example.lab5_1;

import android.app.Activity;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickAddName(View view) {
        EditText nameEditText = findViewById(R.id.txtName);
        String name = nameEditText.getText().toString();

        // Check if the name is empty
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show();
                return; // Exit the method if the input is invalid
            }
        }

        ContentValues values = new ContentValues();
        values.put(MyProvider.name, name);
        Uri uri = getContentResolver().insert(MyProvider.CONTENT_URI, values);
        if (uri != null) {
            Toast.makeText(this, "New record inserted", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Failed to insert record", Toast.LENGTH_LONG).show();
        }
    }
}
