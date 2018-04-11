package com.example.phillipwitkin.quaiver;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import sequencer.Note;

/**
 * Created by phillipwitkin on 12/20/17.
 */

public class DatabaseManager extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "quaiverDB";
    private static final int DATABASE_VERSION = 2;
    // Table Names
    private static final String TABLE_SEQUENCE = "sequences";
    private static final String TABLE_NOTE = "notes";

    //Sequence Columns
    private static final String SEQUENCE_ID = "sequence_id";
    private static final String SEQUENCE_NAME = "sequence_name";

    //Note Columns
    private static final String NOTE_ID = "note_id";
    private static final String NOTE_LENGTH = "note_length";
    private static final String NOTE_FREQUENCY = "note_frequency";
    private static final String NOTE_SEQUENCE_ID = "note_sequence_id";

    public DatabaseManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION );
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sqlCreateTableSequence = "create table " + TABLE_SEQUENCE + " ( " + SEQUENCE_ID;
        sqlCreateTableSequence+=" integer primary key autoincrement, " + SEQUENCE_NAME +" text )";

        sqLiteDatabase.execSQL(sqlCreateTableSequence);

        String sqlCreateTableNote = "create table " + TABLE_NOTE + " ( " + NOTE_ID;
        sqlCreateTableNote +=" integer primary key autoincrement, " + NOTE_LENGTH +" integer, " + NOTE_FREQUENCY + " real, " + NOTE_SEQUENCE_ID + " integer )";
//        sqlCreateTableNote += " FOREIGN KEY (" + NOTE_SEQUENCE_ID + ") REFERENCES " + TABLE_SEQUENCE + " ( " + SEQUENCE_ID + " ) )";

        sqLiteDatabase.execSQL(sqlCreateTableNote);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists "+ TABLE_SEQUENCE);
        sqLiteDatabase.execSQL("drop table if exists "+ TABLE_NOTE);
        onCreate(sqLiteDatabase);
    }

    public int insertSequence(String sequenceName, ArrayList<Note> notes) {
        SQLiteDatabase db = this.getWritableDatabase( );
        String nameSelect = SEQUENCE_NAME + " = '" + sequenceName +"'";
        long res = DatabaseUtils.queryNumEntries(db, TABLE_SEQUENCE, nameSelect);
        if (res >= 1){
            return -1;
        }

        String sqlInsert = "insert into " + TABLE_SEQUENCE;
//        sqlInsert += " values("+sevenHundredNumber+",'"+ studentName+"')";
//        db.execSQL( sqlInsert );
        ContentValues insertVal = new ContentValues();
        insertVal.put(SEQUENCE_NAME, sequenceName);
        int seqID = (int)db.insert(TABLE_SEQUENCE, null, insertVal);
        // insert notes
        for (Note note : notes){
            ContentValues noteVal = new ContentValues();
            noteVal.put(NOTE_FREQUENCY, note.getFrequency());
            noteVal.put(NOTE_LENGTH, note.getLength());
            noteVal.put(NOTE_SEQUENCE_ID, seqID);
            db.insert(TABLE_NOTE, null, noteVal);
        }

        db.close( );

        return seqID;
    }

    public List<DataModel> getAllSequences( ) {
        String sqlQuery = "select * from " + TABLE_SEQUENCE;

        SQLiteDatabase db = this.getWritableDatabase( );
        Cursor cursor = db.rawQuery( sqlQuery, null );

        StringBuffer stringBuffer = new StringBuffer();
        List<DataModel> data = new ArrayList<DataModel>( );
        DataModel dm = null;

        while( cursor.moveToNext( ) ) {
            dm = new DataModel();
            String name = cursor.getString(cursor.getColumnIndexOrThrow(SEQUENCE_NAME));
            int seqID = cursor.getInt(cursor.getColumnIndexOrThrow(SEQUENCE_ID));
            dm = new DataModel();
            dm.setSequenceName(name);
            dm.setSequenceID(seqID);
            stringBuffer.append(dm);
            data.add(dm);
        }
        for (DataModel mo : data){
            Log.i("Retrieved sequences", mo.getSequenceID()+ ", ID: "+ mo.getSequenceName());
        }
        db.close( );
        return data;
    }

    public ArrayList<Note> selectNotesForSequence(int sequenceID){
        String sqlQuery = "select * from " + TABLE_NOTE + " where " + NOTE_SEQUENCE_ID + " = '" + sequenceID + "'";

        SQLiteDatabase db = this.getWritableDatabase( );
        Cursor cursor = db.rawQuery( sqlQuery, null );

        ArrayList<Note> data = new ArrayList<>( );
        Note note = null;

        while( cursor.moveToNext( ) ) {
            int noteLength = cursor.getInt(cursor.getColumnIndexOrThrow(NOTE_LENGTH));
            float noteFreq = cursor.getFloat(cursor.getColumnIndexOrThrow(NOTE_FREQUENCY));
            note = new Note(noteFreq, noteLength);
            data.add(note);
        }
        for (Note n : data){
            Log.i("Retrieved notes", "Frequency: " + n.getFrequency() + ", Length: " + n.getLength());
        }
        db.close( );
        return data;
    }

    public void updateSequence(int sequenceID, ArrayList<Note> notes){
        SQLiteDatabase db = this.getWritableDatabase( );
        String sqlDelete = "delete from " + TABLE_NOTE + " where " + NOTE_SEQUENCE_ID + " = '" + sequenceID +"'";
//        db.rawQuery( sqlDelete, null );
        db.execSQL(sqlDelete);

        for (Note note : notes){
            ContentValues noteVal = new ContentValues();
            noteVal.put(NOTE_FREQUENCY, note.getFrequency());
            noteVal.put(NOTE_LENGTH, note.getLength());
            noteVal.put(NOTE_SEQUENCE_ID, sequenceID);
            db.insert(TABLE_NOTE, null, noteVal);
        }

    }
}
