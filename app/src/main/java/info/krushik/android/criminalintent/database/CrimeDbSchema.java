package info.krushik.android.criminalintent.database;

public class CrimeDbSchema {
    //класс описания таблицы
    public static final class CrimeTable {
        public static final String NAME = "crimes";

        public static final class Cols { //столбцы
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String SOLVED = "solved";
            public static final String SUSPECT = "suspect";
        }
    }
}
