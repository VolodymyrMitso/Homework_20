package mitso.v.homework_20;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import mitso.v.homework_20.constansts.Constants;
import mitso.v.homework_20.support.SupportMain;

public class ShareDialogFragment extends DialogFragment {

    private SupportMain     mSupport;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mSupport = new SupportMain();

        final Bitmap bitmap = getArguments().getParcelable(Constants.BITMAP_BUNDLE_KEY);

        final View view = inflater.inflate(R.layout.share_fragment, null);

        final ImageView mImageView = (ImageView) view.findViewById(R.id.iv_SharePicture_SF);
        mImageView.setImageBitmap(bitmap);

        view.findViewById(R.id.btn_Share_SF).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final File filepath = Environment.getExternalStorageDirectory();
                final File directory = new File(filepath.getAbsolutePath() + "/Converter Lab/");
                directory.mkdirs();
                File file = new File(directory, "share image.png");

                final OutputStream output;

                try {

                    final Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("image/jpeg");
                    output = new FileOutputStream(file);
                    if (bitmap != null)
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
                    output.flush();
                    output.close();
                    final Uri uri = Uri.fromFile(file);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, uri);

                    if (shareIntent.resolveActivity(getContext().getPackageManager()) != null)
                        startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.choose_program)));
                    else
                        mSupport.showToast(getContext(), getResources().getString(R.string.necessary_program));

                } catch (Exception _error) {
                    mSupport.printException(_error);
                }
            }
        });

        return view;
    }
}
