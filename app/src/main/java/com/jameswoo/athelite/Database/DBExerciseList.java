package com.jameswoo.athelite.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.jameswoo.athelite.R;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class DBExerciseList  extends SQLiteOpenHelper {

    //The Android's default system path of your application database.
    private static String DB_PATH = "/data/data/com.jameswoo.athelite/databases/";
    private static String DB_NAME = "athelite_exercises";
    private SQLiteDatabase myDataBase;
    private final Context myContext;

    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     * @param context
     */
    public DBExerciseList(Context context) {

        super(context, DB_NAME, null, 1);
        this.myContext = context;
    }

    /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public void createDataBase() throws IOException{

        boolean dbExist = checkDataBase();

        if(!dbExist){
            this.getReadableDatabase();
            try {
                copyDataBase();
                this.close();
            } catch (IOException e) {
                this.close();
                throw new Error("Error copying database: " + e);
            }
        }

    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase(){

        SQLiteDatabase checkDB = null;

        try{
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        }catch(SQLiteException e){
            //database does't exist yet.
        }
        if(checkDB != null){
            checkDB.close();
        }
        return checkDB != null;
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    private void copyDataBase() throws IOException{

        //Open your local db as the input stream
        InputStream myInput = myContext.getResources().openRawResource(R.raw.athelite_exercises);

        // Path to the just created empty db
        String outFileName = DB_PATH + DB_NAME;

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    public void openDataBase() throws SQLException{
        String myPath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    }

    @Override
    public synchronized void close() {
        if(myDataBase != null)
            myDataBase.close();

        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean checkIfExerciseExists(String exerciseName) {
        boolean recordExists = false;
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "SELECT * " +
                    " FROM " + DBContract.ExerciseListTable.TABLE_NAME +
                    " WHERE " + DBContract.ExerciseListTable.COLUMN_NAME +
                    " =  \"" + exerciseName + "\"";

        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()) {
            recordExists = true;
        }

        cursor.close();
        db.close();

        return recordExists;
    }

    public ArrayList<String> findExercises(String searchTerm) {
        ArrayList<String> exercises = new ArrayList<>();
        String query = "SELECT * " +
                        " FROM " + DBContract.ExerciseListTable.TABLE_NAME +
                        " WHERE " + DBContract.ExerciseListTable.COLUMN_NAME +
                        " LIKE \"%" + searchTerm + "%\"" +
                        " ORDER BY " + DBContract.ExerciseListTable.COLUMN_ID + " ASC" +
                        " LIMIT 0,2000";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()) {
            do {
                String exerciseName = cursor.getString(0);
                exercises.add(exerciseName);
            } while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return exercises;
    }

    public ArrayList<String> readAllExercises() {
        SQLiteDatabase db = this.getWritableDatabase();ArrayList<String> exercises = new ArrayList<>();
        String query = "SELECT * " +
                " FROM " + DBContract.ExerciseListTable.TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()) {
            do {
                exercises.add(cursor.getString(0));
            } while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return exercises;
    }

    public boolean createExercise (String exerciseName) {
        boolean createSuccessful = false;

        if(!checkIfExerciseExists(exerciseName)){

            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(DBContract.ExerciseListTable.COLUMN_NAME, exerciseName);
            createSuccessful = db.insert(DBContract.ExerciseListTable.TABLE_NAME, null, values) > 0;

            db.close();
        }
        return createSuccessful;
    }
}
