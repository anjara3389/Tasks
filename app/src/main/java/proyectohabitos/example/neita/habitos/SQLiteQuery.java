package proyectohabitos.example.neita.habitos;//simplifica las consultas a la base de datos. La apertura y cierre de la base de datos también.

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;


public class SQLiteQuery {

    public String query;

    public SQLiteQuery(String query) {
        this.query = query;
    }

    //Retorna datos de la base de datos dada una consulta sql(query).
    public Object[][] getRecords(SQLiteDatabase db) throws Exception {
        Cursor c = db.rawQuery(query, null);
        if (c.moveToFirst()) {
            Object data[][] = new Object[c.getCount()][c.getColumnCount()];
            do {
                for (int i = 0; i < c.getColumnCount(); i++) {
                    if (c.getType(i) == Cursor.FIELD_TYPE_FLOAT) {
                        data[c.getPosition()][i] = c.getFloat(i);
                    } else if (c.getType(i) == Cursor.FIELD_TYPE_INTEGER) {
                        data[c.getPosition()][i] = c.getLong(i);
                    } else if (c.getType(i) == Cursor.FIELD_TYPE_STRING) {
                        Pattern pat = Pattern.compile("[\\\\d]{4}-[\\\\d]{2}-[\\\\d]{2} [\\\\d]{2}:[\\\\d]{2}:[\\\\d]{2}");
                        if (pat.matcher(c.getString(i)).matches()) { //si es fecha
                            SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date date = dateTimeFormat.parse(c.getString(i));
                            data[c.getPosition()][i] = date;
                        } else {
                            data[c.getPosition()][i] = c.getString(i);
                        }
                    } else if (c.getType(i) == Cursor.FIELD_TYPE_BLOB) {
                        data[c.getPosition()][i] = c.getBlob(i);
                    } else if (c.getType(i) == Cursor.FIELD_TYPE_NULL) {
                        data[c.getPosition()][i] = null;
                    }
                }
            }
            while (c.moveToNext());
            return data;
        }
        return null;
    }

    public Object[] getRecord(SQLiteDatabase db) throws Exception {
        Object[][] records = getRecords(db);
        if (records != null) {
            if (records.length > 1) {
                throw new Exception("Operación inválida");
            } else if (records[0] != null && records.length == 1) {
                return records[0];
            }
        }
        return null;
    }

    public Object getObject(SQLiteDatabase db) throws Exception {
        Object[] record = getRecord(db);
        if (record != null) {
            if (record.length > 1) {
                throw new Exception("Operación inválida");
            } else if (record[0] != null && record.length == 1) {
                return record[0];
            }
        }
        return null;
    }

    public Integer getInteger(SQLiteDatabase db) throws Exception {
        Object obj = getObject(db);
        if (obj != null) {
            return getAsInteger(obj);
        }
        return null;
    }

    public Long getLong(SQLiteDatabase db) throws Exception {
        Object obj = getObject(db);
        if (obj != null) {
            return getAsLong(obj);
        }
        return null;
    }

    public Integer getAsInteger(Object obj) {
        if (obj == null) {
            return null;
        } else if (obj instanceof Integer) {
            return (Integer) obj;
        } else if (obj instanceof Long) {
            return ((Long) obj).intValue();
        } else {
            throw new RuntimeException("No se puede convertir " + obj.getClass().getSimpleName() + " a long");
        }

    }

    public Long getAsLong(Object obj) {
        if (obj == null) {
            return null;
        } else if (obj instanceof Integer) {
            return ((Integer) obj).longValue();
        } else if (obj instanceof Long) {
            return ((Long) obj);
        } else {
            throw new RuntimeException("No se puede convertir " + obj.getClass().getSimpleName() + " a long");
        }

    }

    public String getAsString(Object o) {
        if (o == null) {
            return null;
        } else if (o instanceof byte[]) {
            return new String((byte[]) o);
        }
        return o.toString();
    }
}
