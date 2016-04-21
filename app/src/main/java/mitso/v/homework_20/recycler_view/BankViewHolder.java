package mitso.v.homework_20.recycler_view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import mitso.v.homework_20.R;

public class BankViewHolder extends RecyclerView.ViewHolder {

    private TextView mTextView_BankName;
    private TextView mTextView_BankRegion;
    private TextView mTextView_BankCity;
    private TextView mTextView_BankPhone;
    private TextView mTextView_BankAddress;

    private ImageButton mImageButton_Link;
    private ImageButton mImageButton_Map;
    private ImageButton mImageButton_Phone;
    private ImageButton mImageButton_Details;

    public BankViewHolder(View itemView) {
        super(itemView);

        mTextView_BankName = (TextView) itemView.findViewById(R.id.tv_BankName_BC);
        mTextView_BankRegion = (TextView) itemView.findViewById(R.id.tv_BankRegion_BC);
        mTextView_BankCity = (TextView) itemView.findViewById(R.id.tv_BankCity_BC);
        mTextView_BankPhone = (TextView) itemView.findViewById(R.id.tv_BankPhone_BC);
        mTextView_BankAddress = (TextView) itemView.findViewById(R.id.tv_BankAddress_BC);

        mImageButton_Link = (ImageButton) itemView.findViewById(R.id.btn_Link_BC);
        mImageButton_Map = (ImageButton) itemView.findViewById(R.id.btn_Map_BC);
        mImageButton_Phone = (ImageButton) itemView.findViewById(R.id.btn_Phone_BC);
        mImageButton_Details = (ImageButton) itemView.findViewById(R.id.btn_Details_BC);
    }

    public TextView getTextView_BankAddress() {
        return mTextView_BankAddress;
    }

    public TextView getTextView_BankPhone() {
        return mTextView_BankPhone;
    }

    public TextView getTextView_BankCity() {
        return mTextView_BankCity;
    }

    public TextView getTextView_BankRegion() {
        return mTextView_BankRegion;
    }

    public TextView getTextView_BankName() {
        return mTextView_BankName;
    }

    public ImageButton getImageButton_Link() {
        return mImageButton_Link;
    }

    public ImageButton getImageButton_Map() {
        return mImageButton_Map;
    }

    public ImageButton getImageButton_Phone() {
        return mImageButton_Phone;
    }

    public ImageButton getImageButton_Details() {
        return mImageButton_Details;
    }
}
