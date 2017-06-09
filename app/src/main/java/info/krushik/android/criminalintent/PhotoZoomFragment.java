package info.krushik.android.criminalintent;


import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import java.io.File;


public class PhotoZoomFragment extends DialogFragment {

    private static final String TAG = "PhotoZoomFragment";
    private static final String ARG_FILE = "file";

    private ImageView mPhotoView;
    private File mPhotoFile;

    public static PhotoZoomFragment newInstance(File file) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_FILE, file);

        PhotoZoomFragment fragment = new PhotoZoomFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater
                .from(getActivity())
                .inflate(R.layout.fragment_zoom_photo, null);

        mPhotoView = (ImageView) v.findViewById(R.id.zoom_photo);
        mPhotoFile = (File) getArguments().getSerializable(ARG_FILE); // CrimeLab.get(getActivity()).getPhotoFile(mCrime);
        Log.d(TAG, "mPhotoFile: " + mPhotoFile);

        updatePhotoView();

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setCancelable(true)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                .create();
    }

    private void updatePhotoView() {
        Log.d(TAG, "PhotoZoomFragment : updatePhotoView started");
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            Log.d(TAG, "mPhotoFile was null or does not exist: " + mPhotoFile);
            mPhotoView.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }
}
