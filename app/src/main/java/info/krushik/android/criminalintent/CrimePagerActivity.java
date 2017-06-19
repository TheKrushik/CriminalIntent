package info.krushik.android.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.List;
import java.util.UUID;

// хост для экземпляра CrimeFragment
public class CrimePagerActivity extends AppCompatActivity
        implements CrimeFragment.Callbacks {

    private static final String EXTRA_CRIME_ID = "info.krushik.android.criminalintent.crime_id";

    private ViewPager mViewPager;
    private List<Crime> mCrimes;

    public static Intent newIntent(Context packageContext, UUID crimeId) {
        Intent intent = new Intent(packageContext, CrimePagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId); // передаем строковый ключ и связанное с ним значение
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);

        // получаем дополнение из интента CrimePagerActivity и передайте его CrimeFragment.newInstance(UUID).
        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);

        mViewPager = (ViewPager) findViewById(R.id.activity_crime_pager_view_pager);

//        mViewPager.setOffscreenPageLimit(5); // прорисовует количество страниц слева и права, по умолчанию - 1

        mCrimes = CrimeLab.get(this).getCrimes(); // получаем от CrimeLab набор данных — контейнер List объектов Crime

        FragmentManager fragmentManager = getSupportFragmentManager(); // получаем экземпляр FragmentManager для активности
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) { // адаптером назначается безымянный экземпляр FragmentStatePagerAdapter.

// получает экземпляр Crime для заданной позиции в наборе данных,
// после чего использует его идентификатор для создания и возвращения правильно настроенного экземпляра CrimeFragment.
            @Override
            public Fragment getItem(int position) {
                Crime crime = mCrimes.get(position);
                return CrimeFragment.newInstance(crime.getId());
            }
// возвращает текущее количество элементов в списке.
            @Override
            public int getCount() {
                return mCrimes.size();
            }
        });

        // перебор и проверка идентификаторов всех преступлений для отображения элемента, выбранного пользователем
        for (int i = 0; i < mCrimes.size(); i++) {
            if (mCrimes.get(i).getId().equals(crimeId)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }

    }

    //Интерфейс CrimeFragment.Callbacks должен быть реализован во всех активностях,
    // выполняющих функции хоста для CrimeFragment.
    @Override
    public void onCrimeUpdated(Crime crime) { //Реализация пустых обратных вызовов

    }
}
