package info.krushik.android.criminalintent;

import android.support.v4.app.Fragment;

// хост для экземпляра CrimeListFragment
public class CrimeListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }
}
