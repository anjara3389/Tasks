package proyectohabitos.example.neita.habitos;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BaseHelper extends SQLiteOpenHelper {

    String table = "CREATE TABLE activity(id INTEGER PRIMARY KEY,name TEXT,l BOOLEAN,m BOOLEAN,x BOOLEAN,j BOOLEAN,v BOOLEAN,s BOOLEAN,d BOOLEAN,since_date INTEGER,reminder INTEGER)";
    String tableSpan = "CREATE TABLE span(id INTEGER PRIMARY KEY,span_id INTEGER,beg_date INTEGER,end_date INTEGER)";
    public static final int VERSION = 2;

    public BaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, VERSION, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(table);
        db.execSQL(tableSpan);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //en el caso de actualizar la tabla se la borra y se la vuelve a crear
        db.execSQL("DROP TABLE IF EXISTS ACTIVITY");
        db.execSQL("DROP TABLE IF EXISTS SPAN");
        onCreate(db);
    }
}

