package com.todolist.android.todolist;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.sql.SQLException;

/**
 * Created by Алексей on 03.07.2015.
 */



public class TodoOverview extends ListActivity {

    private TodoDBAdapter dbHelper;
    private static final int ACTIVITY_CREATE = 0;
    private static final int ACTIVITY_EDIT = 1;
    private static final int DELETE_ID = Menu.FIRST + 1;
    private Cursor cursor;

    private Button addToDoButton;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.to_do_list);

        this.getListView().setDividerHeight(2);
        dbHelper = new TodoDBAdapter(this);

        try {
            dbHelper.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        fillData();

        addToDoButton = (Button) findViewById(R.id.add_todo_button);
        addToDoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTodo();
            }
        });

        registerForContextMenu(getListView());
    }

    // Создаем меню, основанное на XML-файле
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_menu, menu);
        return true;
    }

    // Реакция на выбор меню
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.insert:
                createTodo();
                return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.insert:
                createTodo();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }*/

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case DELETE_ID:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
                        .getMenuInfo();
                dbHelper.deleteTodo(info.id);
                fillData();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    private void createTodo() {
        Intent intent = new Intent(this, TodoDetails.class);
        startActivityForResult(intent, ACTIVITY_CREATE);
    }

    @Override
    protected void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);
        Intent intent = new Intent(this, TodoDetails.class);
        intent.putExtra(TodoDBAdapter.KEY_ROWID, id);
        // активити вернет результат если будет вызвано с помощью этого метода
        startActivityForResult(intent, ACTIVITY_EDIT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();

    }

    private void fillData() {
        cursor = dbHelper.fetchAllTodos();

        String[] from = new String[] { TodoDBAdapter.KEY_SUMMARY };
        int[] to = new int[] { R.id.label };

        SimpleCursorAdapter notes = new SimpleCursorAdapter(this,
                R.layout.todo_row, cursor, from, to, 0);

       /* // создаем адаптер
        PriorityImageDBAdapter sAdapter = new PriorityImageDBAdapter(this, R.layout.todo_row,
                cursor, from, to);

        ListView lvSimple = (ListView) findViewById(android.R.id.list);
        lvSimple.setAdapter(sAdapter);*/

        setListAdapter(notes);

        TextView textView = (TextView) findViewById(R.id.empty);
        if (cursor.getCount() == 0){
            textView.setVisibility(View.VISIBLE);
        }
        else{
            textView.setVisibility(View.INVISIBLE);
        }


    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 0, R.string.menu_delete);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }

    /*public class PriorityImageDBAdapter extends SimpleCursorAdapter {

        public PriorityImageDBAdapter(Context context, int layout, Cursor cursor, String[] from,
                                      int[] to) {
            super(context, layout, cursor, from, to, 0);
        }

        public void setViewImage(ImageView imageView, String value) {
            super.setViewImage(imageView, value);

            String array[] = getResources().getStringArray(R.array.priorities);

            if (value == array[0]) {
                imageView.setImageResource(R.drawable.red_exclamation_point);
            } else{
                imageView.setImageResource(R.drawable.yellow_exclamation_point);
            }
        }
    }*/
}
