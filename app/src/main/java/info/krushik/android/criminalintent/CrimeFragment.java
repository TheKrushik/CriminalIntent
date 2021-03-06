package info.krushik.android.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;
import java.util.Date;
import java.util.UUID;

public class CrimeFragment extends Fragment {

    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";
    private static final String DIALOG_ZOOM = "DialogZoom";
    private static final int REQUEST_DATE = 0; // константа для кода запроса
    private static final int REQUEST_TIME = 1; // константа для кода запроса
    private static final int REQUEST_CONTACT = 1;
    private static final int REQUEST_PHOTO = 2;
    private static final int REQUEST_PHOTO_ZOOM = 8;

    private Crime mCrime;
    private File mPhotoFile; // Сохранение местонахождения файла фотографии
    private EditText mTitleField;
    private Button mDateButton;
    private Button mTimeButton;
    private CheckBox mSolvedCheckBox;
    private Button mSuspectButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private Button mReportButton;
    private Callbacks mCallbacks; //Добавление обратных вызовов

    private static final int REQUEST_SUSPECT_PHONE = 5;
    private Button mSuspectPhoneButton;
    private static final int REQUEST_CALL_SUSPECT = 6;
    private Button mCallSuspectButton;

    /**
     * Необходимый интерфейс для активности-хоста.
     */
    public interface Callbacks {
        void onCrimeUpdated(Crime crime);
    }

    // получает UUID, создает пакет аргументов, создает экземпляр фрагмента,
    // а затем присоединяет аргументы к фрагменту.
    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); //явно указываем FragmentManager что есть OptionsMenu
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity()).updateCrime(mCrime); // записаны в базу данных при завершении CrimeFragment
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        //тема преступления
        mTitleField = (EditText) v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence c, int start, int count, int after) {
                // Здесь намеренно оставлено пустое место
            }

            @Override
            public void onTextChanged(CharSequence c, int start, int before, int count) {
                mCrime.setTitle(c.toString());
                updateCrime(); //при изменении краткого описания
            }

            @Override
            public void afterTextChanged(Editable c) {
                // И здесь тоже
            }
        });

        // выбор времени приступления
        mDateButton = (Button) v.findViewById(R.id.crime_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                // вызов DatePickerFragment и передача ему даты
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE); // назначаем CrimeFragment целевым фрагментом экземпляра DatePickerFragment
                dialog.show(manager, DIALOG_DATE);
            }
        });

//        mTimeButton = (Button) v.findViewById(R.id.crime_time);
//        updateTime();
//        mTimeButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FragmentManager manager = getFragmentManager();
//                // вызов DatePickerFragment и передача ему временной метки
//                TimePickerFragment dialog = TimePickerFragment.newInstance(mCrime.getTime());
//                dialog.setTargetFragment(CrimeFragment.this, REQUEST_TIME); // назначаем CrimeFragment целевым фрагментом экземпляра TimePickerFragment
//                dialog.show(manager, DIALOG_TIME);
//            }
//        });


        // статус раскрытия преступления
        mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Назначение флага раскрытия преступления
                mCrime.setSolved(isChecked);
                updateCrime(); //при изменении состояния раскрытия преступления
            }
        });

        //отправка проишествия
        mReportButton = (Button) v.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                Intent i = new Intent(Intent.ACTION_SEND);
//                i.setType("text/plain");
//                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
//                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
//                i = Intent.createChooser(i, getString(R.string.send_report));
//                startActivity(i);
                // упрощение построение интентов (то же что и веше с интентами)
                ShareCompat.IntentBuilder b = ShareCompat.IntentBuilder.from(getActivity());
                b.setType("text/plain");
                b.setSubject(getString(R.string.crime_report_subject));
                b.setText(getCrimeReport());
                b.startChooser();
            }
        });

        //выбор подозреваемого
        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
//        pickContact.addCategory(Intent.CATEGORY_HOME); // Фиктивный код для проверки фильтра контактов
        mSuspectButton = (Button) v.findViewById(R.id.crime_suspect);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });
        if (mCrime.getSuspect() != null) {
            mSuspectButton.setText(mCrime.getSuspect());
        }

        // Защита от отсутствия контактных приложений
        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mSuspectButton.setEnabled(false);
        }


        final Intent pickContentPhone = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        //pickContentContact.addCategory(Intent.CATEGORY_HOME);
        mSuspectPhoneButton = (Button) v.findViewById(R.id.suspect_phone);
        mSuspectPhoneButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivityForResult(pickContentPhone, REQUEST_SUSPECT_PHONE);
            }
        });
        if (mCrime.getSuspectPhone() != null) {
            mSuspectPhoneButton.setText(mCrime.getSuspectPhone());
        }

        mCallSuspectButton = (Button) v.findViewById(R.id.call_suspect);
        mCallSuspectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_DIAL);
                i.setData(Uri.parse("tel:" + mCrime.getSuspectPhone()));
                startActivity(i);
            }
        });

        if (packageManager.resolveActivity(pickContentPhone, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mSuspectPhoneButton.setEnabled(false);
        }

        mPhotoButton = (ImageButton) v.findViewById(R.id.crime_camera);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        boolean canTakePhoto = mPhotoFile != null && //недоступности места, в котором должна сохраняться фотография
                captureImage.resolveActivity(packageManager) != null; //отсутствии приложения камеры
        mPhotoButton.setEnabled(canTakePhoto);

        if (canTakePhoto) {
            Uri uri = Uri.fromFile(mPhotoFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });


        mPhotoView = (ImageView) v.findViewById(R.id.crime_photo);
        updatePhotoView();
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();

                PhotoZoomFragment dialog = PhotoZoomFragment.newInstance(mPhotoFile);
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_PHOTO_ZOOM); // назначаем CrimeFragment целевым фрагментом экземпляра DatePickerFragment
                dialog.show(manager, DIALOG_ZOOM);
            }
        });

        return v;
    }


    // Реакция на получение данных от диалогового окна
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);

            updateCrime(); //при изменении даты
            updateDate();
        }
//        if (requestCode == REQUEST_TIME) {
//            Date time = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
//            mCrime.setTime(time);
//            updateTime();
//        }
        else if (requestCode == REQUEST_CONTACT && data != null) { // Получение имени контакта
            Uri contactUri = data.getData();
// Определение полей, значения которых должны быть возвращены запросом.
            String[] queryFields = new String[]{ContactsContract.Contacts.DISPLAY_NAME};
// Выполнение запроса - contactUri здесь выполняет функции условия "where"
            Cursor c = getActivity().getContentResolver().query(contactUri, queryFields, null, null, null);
            try {
// Проверка получения результатов
                if (c.getCount() == 0) {
                    return;
                }
// Извлечение первого столбца данных - имени подозреваемого.
                c.moveToFirst();
                String suspect = c.getString(0);
                mCrime.setSuspect(suspect);
                updateCrime(); //при изменении подозреваемого
                mSuspectButton.setText(suspect);
            } finally {
                c.close();
            }
        } else if (requestCode == REQUEST_SUSPECT_PHONE && data != null) {
            Uri contactUri = data.getData();
            String[] queryFields = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};

            Cursor c = getActivity().getContentResolver().query(contactUri, queryFields, null, null, null);
            try {

                if (c.getCount() == 0) {
                    return;
                }
                c.moveToFirst();
                String suspectPhone = c.getString(0);
                mCrime.setSuspectPhone(suspectPhone);
                mSuspectPhoneButton.setText(suspectPhone);
            } finally {
                c.close();
            }
        } else if (requestCode == REQUEST_PHOTO) {
            updateCrime(); //при изменении фотографии
            updatePhotoView();
        }
    }

    //CrimeFragment в своей внутренней работе часто будет выполнять этот хитрый маневр из двух шагов:
    // шаг влево, сохранить mCrime в CrimeLab. Шаг вправо, вызвать mCallbacks.onCrimeUpdated(Crime).
    private void updateCrime() {
        CrimeLab.get(getActivity()).updateCrime(mCrime);
        mCallbacks.onCrimeUpdated(mCrime);
    }

    // обновляем дату
    private void updateDate() {
        mDateButton.setText(DateFormat.format("EEEE , MMM dd yyyy", mCrime.getDate()).toString()); // Tuesday, Jul 22, 2015
//        mDateButton.setText(mCrime.getDate().toString());
    }

    // обновляем время
//    private void updateTime() {
//        mTimeButton.setText(DateFormat.format("HH:mm", mCrime.getTime()).toString()); // 17:11
////        mTimeButton.setText(mCrime.getTime().toString());
//    }

    //создает четыре строки, соединяет их и возвращает полный отчет.
    private String getCrimeReport() {
        String solvedString = null;
        if (mCrime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();

        String suspect = mCrime.getSuspect();
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }

        String report = getString(R.string.crime_report, mCrime.getTitle(), dateString, solvedString, suspect);

        return report;
    }

    //Обновление mPhotoView
    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }

    // создание меню в фрагменте
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime, menu);
    }

    //Реакция на выбор команды меню
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_delete_crime:
                CrimeLab.get(getActivity()).deleteCrime(mCrime); // удаляет его в CrimeLab
                getActivity().finish(); // возврат к предыдущей активности
                return true;
            default:
                return super.onOptionsItemSelected(item); //вызывает реализацию суперкласса
        }
    }
}
