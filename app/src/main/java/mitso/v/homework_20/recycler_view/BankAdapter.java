package mitso.v.homework_20.recycler_view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import mitso.v.homework_20.R;
import mitso.v.homework_20.api.models.Bank;

public class BankAdapter extends RecyclerView.Adapter<BankViewHolder> {

    private List<Bank>      mBankList;
    private IBankHandler    mBankHandler;
    private Context         mContext;

    public BankAdapter(Context _context, List<Bank> _bankList) {
        this.mContext = _context;
        this.mBankList = new ArrayList<>(_bankList);
    }

    @Override
    public BankViewHolder onCreateViewHolder(ViewGroup _parent, int _viewType) {
        final View itemView = LayoutInflater.from(_parent.getContext()).inflate(R.layout.bank_card, _parent, false);
        return new BankViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(BankViewHolder _holder, final int _position) {

        final Bank bank = mBankList.get(_position);

        _holder.getTextView_BankName().setText(bank.getName());
        _holder.getTextView_BankRegion().setText(bank.getRegion());
        _holder.getTextView_BankCity().setText(bank.getCity());
        _holder.getTextView_BankPhone().setText(String.format("%s%s", mContext.getResources().getString(R.string.s_phone), bank.getPhone()));
        _holder.getTextView_BankAddress().setText(String.format("%s%s", mContext.getResources().getString(R.string.s_address), bank.getAddress()));

        _holder.getImageButton_Link().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBankHandler.goToLink(bank.getLink());
            }
        });

        _holder.getImageButton_Map().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBankHandler.showOnMap(bank.getRegion(), bank.getCity(), bank.getAddress());
            }
        });

        _holder.getImageButton_Phone().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBankHandler.callPhone(bank.getPhone());
            }
        });

        _holder.getImageButton_Details().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBankHandler.showDetails(bank);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mBankList.size();
    }

    public Bank removeItem(int _position) {
        final Bank bank = mBankList.remove(_position);
        notifyItemRemoved(_position);
        return bank;
    }

    public void addItem(int _position, Bank _bank) {
        mBankList.add(_position, _bank);
        notifyItemInserted(_position);
    }

    public void moveItem(int _fromPosition, int _toPosition) {
        final Bank bank = mBankList.remove(_fromPosition);
        mBankList.add(_toPosition, bank);
        notifyItemMoved(_fromPosition, _toPosition);
    }

    public void animateTo(List<Bank> _bankList) {
        applyAndAnimateRemovals(_bankList);
        applyAndAnimateAdditions(_bankList);
        applyAndAnimateMovedItems(_bankList);
    }

    private void applyAndAnimateRemovals(List<Bank> _bankList) {
        for (int i = mBankList.size() - 1; i >= 0; i--) {
            final Bank bank = mBankList.get(i);
            if (!_bankList.contains(bank)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<Bank> _bankList) {
        for (int i = 0, count = _bankList.size(); i < count; i++) {
            final Bank bank = _bankList.get(i);
            if (!mBankList.contains(bank)) {
                addItem(i, bank);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<Bank> _bankList) {
        for (int toPosition = _bankList.size() - 1; toPosition >= 0; toPosition--) {
            final Bank bank = _bankList.get(toPosition);
            final int fromPosition = mBankList.indexOf(bank);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public void setBankHandler(IBankHandler _bankHandler) {
        this.mBankHandler = _bankHandler;
    }

    public void releaseBankHandler() {
        this.mBankHandler = null;
    }
}
