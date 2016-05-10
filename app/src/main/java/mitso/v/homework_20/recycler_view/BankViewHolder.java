package mitso.v.homework_20.recycler_view;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import mitso.v.homework_20.databinding.BankCardBinding;

public class BankViewHolder extends RecyclerView.ViewHolder {

    private BankCardBinding     mBinding;

    public BankViewHolder(View itemView) {
        super(itemView);

        mBinding = DataBindingUtil.bind(itemView);
    }

    public BankCardBinding getBinding() {
        return mBinding;
    }
}