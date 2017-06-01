package info.krushik.android.criminalintent;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CrimeLab { //(singleton) класс - допускают создание только одного экземпляра
    private static CrimeLab sCrimeLab; // префикс s - статическая переменная
    private List<Crime> mCrimes; //список объектов Crime

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
        mCrimes = new ArrayList<>(); // пустой список List объектов Crime

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
        mCrimes.add(c);
    }

    public List<Crime> getCrimes() {
        return mCrimes; // возвращает List
    }

    public Crime getCrime(UUID id) {
        for (Crime crime : mCrimes) {
            if (crime.getId().equals(id)){
                return crime; // возвращает объект Crime с заданным идентификатором
            }
        }
        return null;
    }
}
