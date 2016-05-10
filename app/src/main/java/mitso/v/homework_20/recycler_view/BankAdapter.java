package mitso.v.homework_20.recycler_view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import mitso.v.homework_20.api.models.Bank;
import mitso.v.homework_20.databinding.BankCardBinding;

public class BankAdapter extends RecyclerView.Adapter<BankViewHolder> {

    private List<Bank>      mBankList;
    private IBankHandler    mBankHandler;

    public BankAdapter(List<Bank> _bankList) {
        this.mBankList = new ArrayList<>(_bankList);
    }

    @Override
    public BankViewHolder onCreateViewHolder(ViewGroup _parent, int _viewType) {
        final BankCardBinding binding = BankCardBinding.inflate(LayoutInflater.from(_parent.getContext()), _parent, false);
        return new BankViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(BankViewHolder _holder, final int _position) {

        final Bank bank = mBankList.get(_position);

        _holder.getBinding().setBank(bank);

        _holder.getBinding().setClickerLink(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBankHandler.goToLink(bank.getLink());
            }
        });

        _holder.getBinding().setClickerMap(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBankHandler.showOnMap(bank.getRegion(), bank.getCity(), bank.getAddress());
            }
        });

        _holder.getBinding().setClickerPhone(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBankHandler.callPhone(bank.getPhone());
            }
        });

        _holder.getBinding().setClickerDetails(new View.OnClickListener() {
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
