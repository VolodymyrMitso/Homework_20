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

public class ShareDialog extends DialogFragment {

    ImageView imageView;
    Bitmap mBitmap;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final Bitmap bitmap = getArguments().getParcelable("bitmap");
        mBitmap = bitmap;

        View view = inflater.inflate(R.layout.share_fragment, null);



        imageView = (ImageView) view.findViewById(R.id.iv_SharePicture_SF);
        imageView.setImageBitmap(bitmap);

        view.findViewById(R.id.btn_Share_SF).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                OutputStream output;

                File filepath = Environment.getExternalStorageDirectory();
                File dir = new File(filepath.getAbsolutePath() + "/share_image/");
                dir.mkdirs();
                File file = new File(dir, "image.png");

                try {

                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("image/jpeg");
                    output = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
                    output.flush();
                    output.close();
                    Uri uri = Uri.fromFile(file);
                    share.putExtra(Intent.EXTRA_STREAM, uri);
                    startActivity(Intent.createChooser(share, "Выберите программу:"));

                } catch (Exception e) {

                    e.printStackTrace();
                }

            }
        });

        return view;
    }
}
