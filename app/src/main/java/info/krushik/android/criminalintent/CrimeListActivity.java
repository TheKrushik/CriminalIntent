package info.krushik.android.criminalintent;

import android.content.Intent;
import android.support.v4.app.Fragment;

// хост для экземпляра CrimeListFragment
public class CrimeListActivity extends SingleFragmentActivity
        implements CrimeListFragment.Callbacks, CrimeFragment.Callbacks {

    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }

    @Override
    protected int getLayoutResId() { // Переход к файлу двухпанельного макета
//        return R.layout.activity_twopane;
        return R.layout.activity_masterdetail;
    }

    //выбор преступления в любом варианте интерфейса
    @Override
    public void onCrimeSelected(Crime crime) { //CrimeListFragment будет вызывать этот метод в CrimeHolder.onClick(…)

        if (findViewById(R.id.detail_fragment_container) == null) { //наличие в макете detail_fragment_container
            Intent intent = CrimePagerActivity.newIntent(this, crime.getId());
            startActivity(intent);
        } else {
            //удаляет существующий экземпляр CrimeFragment из detail_fragment_container (если он имеется)
            // и добавляет экземпляр CrimeFragment, который мы хотим там видеть.
            Fragment newDetail = CrimeFragment.newInstance(crime.getId());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, newDetail)
                    .commit();
        }
    }

    @Override
    public void onCrimeUpdated(Crime crime) { // перезагрузки списка
        CrimeListFragment listFragment = (CrimeListFragment)
                getSupportFragmentManager()
                        .findFragmentById(R.id.fragment_container);
        listFragment.updateUI();
    }
}
