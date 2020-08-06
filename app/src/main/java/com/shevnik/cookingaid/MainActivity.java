package com.shevnik.cookingaid;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //Объявим переменные компонентов
    Button button1;
    Button button2;
    ListView listView;
    TextView selection;
    //ArrayList<String> selected = new ArrayList<String>();
    String[] ingredients;

    final String LOG_TAG = "myLogs";

    //Переменная для работы с БД
    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDBHelper = new DatabaseHelper(this);

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

        //Найдем компоненты в XML разметке
        button1 = (Button) findViewById(R.id.button_to_list);
        button2 = (Button) findViewById(R.id.button_to_search);

        button1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {

                Boolean filled = false;
                Intent intent = new Intent(MainActivity.this, RecipeListActivity.class);
                intent.putExtra("filled", filled);
                startActivity(intent);
            }
        });

        selection = (TextView) findViewById(R.id.textView2);
        listView = (ListView) findViewById(R.id.ingredient_list);
        //Создаем адаптер
        // устанавливаем режим выбора пунктов списка
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        // Создаем адаптер, используя массив из файла ресурсов
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.ingredient_selection, android.R.layout.simple_list_item_multiple_choice);
        listView.setAdapter(adapter);

        // получаем массив из файла ресурсов
        ingredients = getResources().getStringArray(R.array.ingredient_selection);

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // пишем в лог выделенные элементы
                SparseBooleanArray sbArray = listView.getCheckedItemPositions();
                ArrayList<String> selected = new ArrayList<>();
                for (int i = 0; i < sbArray.size(); i++) {
                    int key = sbArray.keyAt(i);
                    if (sbArray.get(key)) {
                        selected.add(ingredients[key]);
                    }
                }

                if(selected.size() < 1)
                {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Выберите хотя бы один ингредиент",
                            Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                else
                    {
                    Boolean filled = true;
                    Intent intent = new Intent(MainActivity.this, RecipeListActivity.class);
                    intent.putExtra("selected", selected);
                    intent.putExtra("filled", filled);
                    startActivity(intent);
                    selected.clear();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_author:
                Intent intent = new Intent(MainActivity.this, AuthorActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_test:
                Intent intent2 = new Intent(MainActivity.this, TestActivity.class);
                startActivity(intent2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}