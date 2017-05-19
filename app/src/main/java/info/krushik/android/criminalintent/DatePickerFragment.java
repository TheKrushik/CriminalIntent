package info.krushik.android.criminalintent;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class DatePickerFragment extends DialogFragment {

    private static final String ARG_DATE = "date";
    public static final String EXTRA_DATE = "info.krushik.android.criminalintent.date"; // помещаем дату возврата

    private DatePicker mDatePicker;

    public static DatePickerFragment newInstance(Date date) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);

        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Date date = (Date) getArguments().getSerializable(ARG_DATE); // получите объект Date из аргументов и используем его с Calendar

        Calendar calendar = Calendar.getInstance(); // календарь для получения нужных значений(месяца, дня и года)
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_date, null); // заполняем представление
//        DatePicker dp = new DatePicker(getActivity()); // вариант в коде без представления

        mDatePicker = (DatePicker) v.findViewById(R.id.dialog_date_date_picker);
        mDatePicker.init(year, month, day, null); // сообщаем DatePicker что нам нужно использовать

        return new AlertDialog.Builder(getActivity())
                .setView(v) // назначаем представление диалоговому окну.
//                .setView(dp) // вариант в коде без представления
                .setTitle(R.string.date_picker_title)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int year = mDatePicker.getYear();
                                int month = mDatePicker.getMonth();
                                int day = mDatePicker.getDayOfMonth();
                                Date date = new GregorianCalendar(year, month, day).getTime();
                                sendResult(Activity.RESULT_OK, date); // Передача информации
                            }
                        })
                .create();
    }

    // Обратный вызов целевого фрагмента
    private void sendResult(int resultCode, Date date) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent intent = new Intent(); // создает интент,
        intent.putExtra(EXTRA_DATE, date); // помещает в него дату как дополнение,
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent); //вызывает CrimeFragment.onActivityResult(…)
    }

}
