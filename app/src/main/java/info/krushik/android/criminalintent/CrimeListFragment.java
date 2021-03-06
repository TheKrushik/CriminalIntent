package info.krushik.android.criminalintent;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

public class CrimeListFragment extends Fragment {

    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";

    private TextView mCrimeEmpty;
    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;
    private int mCurrentPosition;
    private boolean mSubtitleVisible; // признак видимости подзаголовка
    private Callbacks mCallbacks; // хранения объекта, реализующего Callbacks

    /**
     * Обязательный интерфейс для активности-хоста.
     * механизм вызова методов активности-хоста
     * Неважно, какая активность является хостом, — если она реализует CrimeListFragment.Callbacks
     */
    public interface Callbacks { // интерфейс обратного вызова
        void onCrimeSelected(Crime crime);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (Callbacks) activity; //задается ее значение
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); //явно указываем FragmentManager что есть OptionsMenu
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);
        mCrimeEmpty = (TextView) view.findViewById(R.id.crime_empty_text_view);
        mCrimeRecyclerView = (RecyclerView) view.findViewById(R.id.crime_recycler_view);
        //класс LinearLayoutManager размещает элементы в вертикальном списке.
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (savedInstanceState != null) {
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE); //Сохранение признака видимости подзаголовка
        }

        updateUI();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible); //Востановление признака видимости подзаголовка
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null; // сбрасывается ее значение
    }

    // настраивает пользовательский интерфейс CrimeListFragment.
// создает объект CrimeAdapter и назначает его RecyclerView.
    public void updateUI() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();

        if (crimes.size() == 0) { // если список пустой
            mCrimeEmpty.setVisibility(View.VISIBLE);
        }else {
            mCrimeEmpty.setVisibility(View.GONE);
        }

        if (mAdapter == null) {
            mAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mAdapter); // создаем список
        } else {
            mAdapter.setCrimes(crimes); // закрепляем список
            mAdapter.notifyDataSetChanged(); // обновляем список
//            mAdapter.notifyItemChanged(mCurrentPosition); // обновляем один элемент в списке

            updateSubtitle();
        }
    }

    // создание меню в фрагменте
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);
//изменение меню
        MenuItem subtitleItem = menu.findItem(R.id.menu_item_show_subtitle);
        if (mSubtitleVisible) {
            subtitleItem.setTitle(R.string.hide_subtitle);
        } else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    //Реакция на выбор команды меню
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_crime:
                Crime crime = new Crime(); // создает новый объект Crime
                CrimeLab.get(getActivity()).addCrime(crime); // добавляет его в CrimeLab
//                Intent intent = CrimePagerActivity.newIntent(getActivity(), crime.getId());
//                startActivity(intent); // запускает экземпляр CrimePagerActivity
                updateUI(); //содержимое списка также немедленно перезагружается после добавления нового преступления.
                mCallbacks.onCrimeSelected(crime); // вызов выбора варианта интерфейса
                return true;
            case R.id.menu_item_show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu(); // повторное создание элементов действий
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item); //вызывает реализацию суперкласса
        }
    }

    // задает подзаголовок панели инструментов
    private void updateSubtitle() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
//        int crimeCount = crimeLab.getCrimes().size();
//        String subtitle = getString(R.string.subtitle_format, crimeCount); //строка подзаголовка
        int crimeSize = crimeLab.getCrimes().size();
        String subtitle = getResources()
                .getQuantityString(R.plurals.subtitle_plural, crimeSize, crimeSize);//Множественное число

        if (!mSubtitleVisible) { // отображение или скрытие подзаголовка
            subtitle = null;
        }

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    //ViewHolder
    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Crime mCrime;
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private TextView mTimeTextView;
        private CheckBox mSolvedCheckBox;

        public CrimeHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this); // клик
            mTitleTextView = (TextView) itemView.findViewById(R.id.list_item_crime_title_text_view);
            mDateTextView = (TextView) itemView.findViewById(R.id.list_item_crime_date_text_view);
            mTimeTextView = (TextView) itemView.findViewById(R.id.list_item_crime_time_text_view);
            mSolvedCheckBox = (CheckBox) itemView.findViewById(R.id.list_item_crime_solved_check_box);
        }

        public void bindCrime(Crime crime) {
            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle());
//            mDateTextView.setText(mCrime.getDate().toString());
            mDateTextView.setText(DateFormat.format("yyyy MMM dd , EEEE", mCrime.getDate()).toString());
//            mTimeTextView.setText(DateFormat.format("HH:mm", mCrime.getTime()).toString());
            mSolvedCheckBox.setChecked(mCrime.isSolved());
        }

        // Обработка касаний в CrimeHolder
        @Override
        public void onClick(View v) {
//            Toast.makeText(getActivity(), mCrime.getTitle() + " clicked!", Toast.LENGTH_SHORT).show();
            mCurrentPosition = getLayoutPosition(); // сохраняем позицию списка в переменную

            // Запуск активности // метод getActivity() для передачи активности-хоста как объекта Context
//            Intent intent = CrimePagerActivity.newIntent(getActivity(), mCrime.getId());
//            startActivity(intent);
            mCallbacks.onCrimeSelected(mCrime); // вызов выбора варианта интерфейса
        }
    }

    //Adapter
    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {
        private List<Crime> mCrimes;

        public CrimeAdapter(List<Crime> crimes) {
            mCrimes = crimes;
        }

        // вызывается виджетом RecyclerView, когда ему потребуется новое представление для отображения элемента.
// В этом методе мы создаем объект View и упаковываем его в ViewHolder.
        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_crime, parent, false);
            return new CrimeHolder(view);
        }

        // связывает представление View объекта ViewHolder с объектом модели.
// При вызове он получает ViewHolder и позицию в наборе данных.
        @Override
        public void onBindViewHolder(CrimeHolder holder, int position) {
            Crime crime = mCrimes.get(position);
            holder.bindCrime(crime); // Связывание адаптера с CrimeHolder
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }

        //закрепить отображаемые в нем данные
        public void setCrimes(List<Crime> crimes) {
            mCrimes = crimes;
        }
    }
}
