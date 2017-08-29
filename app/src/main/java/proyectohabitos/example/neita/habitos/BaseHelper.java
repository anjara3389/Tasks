package proyectohabitos.example.neita.habitos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BaseHelper extends SQLiteOpenHelper {

    private static BaseHelper sInstance;

    private static final String DATABASE_NAME = "Demo";
    public static final int VERSION = 3;
    String table = "CREATE TABLE activity(id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT,l BOOLEAN,m BOOLEAN,x BOOLEAN,j BOOLEAN,v BOOLEAN,s BOOLEAN,d BOOLEAN,since_date INTEGER,reminder INTEGER,chrono INTEGER)";
    String tableSpan = "CREATE TABLE span(id INTEGER PRIMARY KEY AUTOINCREMENT,span_id INTEGER,beg_date INTEGER,end_date INTEGER,activity_id INTEGER, FOREIGN KEY(activity_id) REFERENCES activity(id))";


    private BaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION, null);
    }

    public static synchronized BaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new BaseHelper(context.getApplicationContext());
        }
        return sInstance;
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

    public static SQLiteDatabase getWritable(Context ctx) {
        return getInstance(ctx).getWritableDatabase();
    }

    public static SQLiteDatabase getReadable(Context ctx) {
        return getInstance(ctx).getReadableDatabase();
    }

    public static void tryClose(SQLiteDatabase db) {
        if (db != null && db.isOpen()) {
            db.close();
        }
    }

}

