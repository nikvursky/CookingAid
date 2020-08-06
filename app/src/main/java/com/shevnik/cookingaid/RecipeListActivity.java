package com.shevnik.cookingaid;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import android.database.SQLException;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class RecipeListActivity extends AppCompatActivity {

    ListView listView;
    TextView textWarning;

    //Переменная для работы с БД
    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;

    final String ATTRIBUTE_NAME_IMAGE = "image";

    final String LOG_TAG = "myLogs";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);

        Bundle extras = getIntent().getExtras();
        Boolean filled = extras.getBoolean("filled");
        String idimg;

        mDBHelper = new DatabaseHelper(this);

        textWarning = (TextView) findViewById(R.id.warning);
        textWarning.setVisibility(View.INVISIBLE);

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

        //Список клиентов
        ArrayList<HashMap<String, Object>> clients = new ArrayList<HashMap<String, Object>>();

        //Список параметров конкретного клиента
        HashMap<String, Object> client;

        //Отправляем запрос в БД
        Cursor cursor = mDb.rawQuery("SELECT * FROM food_recipes", null);
        cursor.moveToFirst();


        Integer check = 0;
        //Пробегаем по всем клиентам
        while (!cursor.isAfterLast()) {
            client = new HashMap<String, Object>();

            Integer count = 0;
            //Заполняем клиента
            client.put("id", cursor.getLong(0));
            client.put("name", cursor.getString(1));
            client.put("time", cursor.getString(3));
            client.put("difficulty", cursor.getString(4));
            client.put("type", cursor.getString(6));
            client.put("ingredients", cursor.getString(2));
            client.put("directions", cursor.getString(5));

            idimg = cursor.getString(8);
            int id = getResources().getIdentifier("com.shevnik.cookingaid:drawable/" + idimg, null, null);
            client.put("image", id);
            client.put("imagename", cursor.getString(8));
            String toCompare = cursor.getString(2);
            Integer compare = cursor.getInt(7);

            Log.d(LOG_TAG, compare.toString());

            if (filled == true)
            {
                ArrayList<String> selected = extras.getStringArrayList("selected");

                String[] daysArray = new String[selected.size()];
                for (int i = 0; i < selected.size(); i++) {
                    daysArray[i] = selected.get(i);
                    if(toCompare.contains(daysArray[i]))
                    {
                        count++;
                    }
                }
                if(count.equals(compare))
                {
                    //Закидываем клиента в список клиентов
                    clients.add(client);
                    check++;
                    //Переходим к следующему клиенту
                    cursor.moveToNext();
                }
                else
                    //Переходим к следующему клиенту
                    cursor.moveToNext();
            }
            else
            {
                //Закидываем клиента в список клиентов
                clients.add(client);
                check++;

                //Переходим к следующему клиенту
                cursor.moveToNext();
            }
        }
        cursor.close();

        if (check == 0)
        {
            textWarning.setVisibility(View.VISIBLE);
        }

        //Какие параметры клиента мы будем отображать в соответствующих
        //элементах из разметки adapter_item.xml
        String[] from = {"name", "time", "difficulty", "type", "image", "id", "ingredients", "directions"};
        int[] to = {R.id.dishName, R.id.aboutTime, R.id.aboutDifficulty, R.id.aboutType, R.id.img};

        //Создаем адаптер
        final SimpleAdapter adapter = new SimpleAdapter(this, clients, R.layout.recipe_list_item, from, to);

        listView = (ListView) findViewById(R.id.recipe_list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, Object> obj = (HashMap<String, Object>) adapter.getItem(position);
                String name = (String) obj.get("name");
                String time = (String) obj.get("time");
                String difficulty = (String) obj.get("difficulty");
                String type = (String) obj.get("type");
                String ingredients = (String) obj.get("ingredients");
                String directions = (String) obj.get("directions");
                String imagename = (String) obj.get("imagename");
                Intent intent = new Intent(RecipeListActivity.this, RecipeActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("time", time);
                intent.putExtra("difficulty", difficulty);
                intent.putExtra("type", type);
                intent.putExtra("ingredients", ingredients);
                intent.putExtra("directions", directions);
                intent.putExtra("imagename", imagename);
                startActivity(intent);
            }
        });
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
                Intent intent = new Intent(RecipeListActivity.this, MainActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_author:
                Intent intent1 = new Intent(RecipeListActivity.this, AuthorActivity.class);
                startActivity(intent1);
                return true;
            case R.id.action_test:
                Intent intent2 = new Intent(RecipeListActivity.this, TestActivity.class);
                startActivity(intent2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
