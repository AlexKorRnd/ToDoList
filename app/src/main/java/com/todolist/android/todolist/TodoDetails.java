package com.todolist.android.todolist;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.sql.SQLException;

/**
 * Created by Алексей on 03.07.2015.
 */
public class TodoDetails extends Activity {
    private EditText mTitleText;
    private EditText mBodyText;
    private Long mRowId;
    private TodoDBAdapter mDbHelper;
    private Spinner mCategory;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        mDbHelper = new TodoDBAdapter(this);

        try {
            mDbHelper.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        setContentView(R.layout.todo_edit);

        mCategory = (Spinner) findViewById(R.id.category);
        mTitleText = (EditText) findViewById(R.id.todo_edit_summary);
        mBodyText = (EditText) findViewById(R.id.todo_edit_description);

        Button confirmButton = (Button) findViewById(R.id.todo_edit_button);
        mRowId = null;
        Bundle extras = getIntent().getExtras();
        mRowId = (bundle == null) ? null : (Long) bundle
                .getSerializable(TodoDBAdapter.KEY_ROWID);
        if (extras != null) {
            mRowId = extras.getLong(TodoDBAdapter.KEY_ROWID);
        }


        confirmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                try {
                    populateFields();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                setResult(RESULT_OK);
                finish();
            }

        });
    }

    private void populateFields() throws SQLException {
        if (mRowId != null) {
            Cursor todo = mDbHelper.fetchTodo(mRowId);

            String category = todo.getString(todo
                    .getColumnIndexOrThrow(TodoDBAdapter.KEY_CATEGORY));

            for (int i = 0; i < mCategory.getCount(); i++) {

                String s = (String) mCategory.getItemAtPosition(i);
                Log.e(null, s + " " + category);
                if (s.equalsIgnoreCase(category)) {
                    mCategory.setSelection(i);
                }
            }

            mTitleText.setText(todo.getString(todo
                    .getColumnIndexOrThrow(TodoDBAdapter.KEY_SUMMARY)));
            mBodyText.setText(todo.getString(todo
                    .getColumnIndexOrThrow(TodoDBAdapter.KEY_DESCRIPTION)));
        }
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putSerializable(TodoDBAdapter.KEY_ROWID, mRowId);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveState();
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            populateFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void saveState() {
        String category = (String) mCategory.getSelectedItem();
        String summary = mTitleText.getText().toString();
        String description = mBodyText.getText().toString();

        if (mRowId == null) {
            long id = mDbHelper.createTodo(category, summary, description);
            if (id > 0) {
                mRowId = id;
            }
        } else {
            mDbHelper.updateTodo(mRowId, category, summary, description);
        }
    }
}
