package info.krushik.android.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import info.krushik.android.criminalintent.database.CrimeBaseHelper;
import info.krushik.android.criminalintent.database.CrimeCursorWrapper;
import info.krushik.android.criminalintent.database.CrimeDbSchema.CrimeTable;

public class CrimeLab { //(singleton) класс - допускают создание только одного экземпляра
    private static CrimeLab sCrimeLab; // префикс s - статическая переменная

//    private List<Crime> mCrimes; //список объектов Crime // ->db
    private Context mContext;
    private SQLiteDatabase mDatabase;

//Чтобы создать синглетный класс, следует создать класс с закрытым конструктором и методом get().
//Если экземпляр уже существует, то get() просто возвращает его.
//Если экземпляр еще не существует, то get() вызывает конструктор для его создания.

    //метод get()
    public static CrimeLab get(Context context) { //в методе get() конструктору CrimeLab передается параметр Context
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    //закрытый конструктор
    private CrimeLab(Context context) {
//        mAppContext = appContext;
        mContext = context.getApplicationContext();
        mDatabase = new CrimeBaseHelper(mContext).getWritableDatabase(); // открываем БД, если файлаБД не существует, то он создается
//        mCrimes = new ArrayList<>(); // пустой список List объектов Crime // ->db

// Генератор 100 crime
//        for (int i = 0; i < 100; i++) { //массив 100 однообразных объектов Crime
//            Crime crime = new Crime();
//            crime.setTitle("Crime #" + i);
//            crime.setSolved(i % 2 == 0); // Для каждого второго объекта
//            mCrimes.add(crime);
//        }
    }

    //Добавление нового объекта Crime
    public void addCrime(Crime c) {
//        mCrimes.add(c); // ->db
        ContentValues values = getContentValues(c);
        mDatabase.insert(CrimeTable.NAME, null, values); // (таблица, nullColumnHack, вставляемые данные)
    }

    //Удаление текущего объекта Crime
    public void deleteCrime(Crime c) {
//        if (mCrimes.size() > 0) {
//            mCrimes.remove(c); // ->db
//        }
        String uuidString = c.getId().toString();

        mDatabase.delete(CrimeTable.NAME,
                CrimeTable.Cols.UUID + " = ?", // условие WHERE
                new String[] { uuidString }); // значения аргументов в условии WHERE
    }

    //чтение всего списка преступлений
    public List<Crime> getCrimes() {
//        return mCrimes; // возвращает List // ->db

        List<Crime> crimes = new ArrayList<>();
        CrimeCursorWrapper cursor = queryCrimes(null, null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                crimes.add(cursor.getCrime());
                cursor.moveToNext();
            }
        } finally {
            cursor.close(); // курсоры нужно закрывать!!!
        }
        return crimes;
    }

    //чтение 1го преступления по id
    public Crime getCrime(UUID id) {
//        for (Crime crime : mCrimes) { // ->db
//            if (crime.getId().equals(id)) {
//                return crime; // возвращает объект Crime с заданным идентификатором
//            }
//        }
        CrimeCursorWrapper cursor = queryCrimes(
                CrimeTable.Cols.UUID + " = ?",
                new String[] { id.toString() }
        );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getCrime();
        } finally {
            cursor.close();
        }
    }

    //Обновление записи
    public void updateCrime(Crime crime) {
        String uuidString = crime.getId().toString();
        ContentValues values = getContentValues(crime);

        mDatabase.update(CrimeTable.NAME, //имя таблицы
                values, //объект ContentValues каждой обновляемой записи
                CrimeTable.Cols.UUID + " = ?", // условие WHERE
                new String[] { uuidString }); // значения аргументов в условии WHERE
    }

    // преобразовует объект Crime в ContentValues
    private static ContentValues getContentValues(Crime crime) {
        ContentValues values = new ContentValues();
        values.put(CrimeTable.Cols.UUID, crime.getId().toString());
        values.put(CrimeTable.Cols.TITLE, crime.getTitle());
        values.put(CrimeTable.Cols.DATE, crime.getDate().getTime());
        values.put(CrimeTable.Cols.SOLVED, crime.isSolved() ? 1 : 0);
        values.put(CrimeTable.Cols.SUSPECT, crime.getSuspect());
        values.put(CrimeTable.Cols.SUSPECTPHONE, crime.getSuspectPhone());

        return values;
    }

    // Чтение из базы данных
    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                CrimeTable.NAME, // String table - таблица
                null, // String[] columns - столбци, null выбирает все столбцы
                whereClause,  // String where - условие WHERE
                whereArgs, // String[] whereArgs - значения аргументов в условии WHERE
                null, // String groupBy
                null, // String having
                null, // String orderBy
                null // String limit
        );
        return new CrimeCursorWrapper(cursor);
    }
}
