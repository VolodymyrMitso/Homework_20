package mitso.v.homework_20;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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

            }
        });

        return view;
    }
}
