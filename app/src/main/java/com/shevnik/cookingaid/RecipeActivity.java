package com.shevnik.cookingaid;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class RecipeActivity extends AppCompatActivity {

    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;

    TextView textView1, textView2, textView3, textView4, textView5, textView6;
    ImageView imageView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        mDBHelper = new DatabaseHelper(this);

        imageView1 = (ImageView) findViewById(R.id.img);
        textView1 = (TextView) findViewById(R.id.dishName);
        textView2 = (TextView) findViewById(R.id.aboutTime);
        textView3 = (TextView) findViewById(R.id.aboutDifficulty);
        textView4 = (TextView) findViewById(R.id.aboutType);
        textView5 = (TextView) findViewById(R.id.aboutIngredients);
        textView6 = (TextView) findViewById(R.id.aboutDirections);

        try {
            mDBHelper.updateDataBase();
        } catch (IOException mIOException) {
            throw new Error("UnableToUpdateDatabase");
        }

        try {
            mDb = mDBHelper.getWritableDatabase();
        } catch (SQLException mSQLException) {
            throw mSQLException;
        }


        Bundle extras = getIntent().getExtras();
        String imagename = extras.getString("imagename");
        String name = extras.getString("name");
        String time = extras.getString("time");
        String difficulty = extras.getString("difficulty");
        String type = extras.getString("type");
        String ingredients = extras.getString("ingredients");
        String directions = extras.getString("directions");


        int id = getResources().getIdentifier("com.shevnik.cookingaid:drawable/" + imagename, null, null);
        imageView1.setImageResource(id);
        textView1.setText(name);
        textView2.setText(time);
        textView3.setText(difficulty);
        textView4.setText(type);
        textView5.setText(ingredients);
        textView6.setText(directions);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_full, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_main:
                Intent intent = new Intent(RecipeActivity.this, MainActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_author:
                Intent intent1 = new Intent(RecipeActivity.this, AuthorActivity.class);
                startActivity(intent1);
                return true;
            case R.id.action_test:
                Intent intent2 = new Intent(RecipeActivity.this, TestActivity.class);
                startActivity(intent2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

