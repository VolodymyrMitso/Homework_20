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
    public BankViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.bank_card, parent, false);
        return new BankViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(BankViewHolder holder, final int position) {

        final Bank bank = mBankList.get(position);

        holder.getTextView_BankName().setText(bank.getName());
        if (bank.getRegion().equals(mContext.getResources().getString(R.string.s_capital)))
            holder.getTextView_BankRegion().setText(mContext.getResources().getString(R.string.s_region));
        else
            holder.getTextView_BankRegion().setText(bank.getRegion());
        holder.getTextView_BankCity().setText(bank.getCity());
        holder.getTextView_BankPhone().setText(String.format("%s%s", mContext.getResources().getString(R.string.s_phone), bank.getPhone()));
        holder.getTextView_BankAddress().setText(String.format("%s%s", mContext.getResources().getString(R.string.s_address), bank.getAddress()));

        holder.getImageButton_Link().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBankHandler.goToLink(bank.getLink());
            }
        });

        holder.getImageButton_Map().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBankHandler.showOnMap(bank.getRegion(), bank.getCity(), bank.getAddress());
            }
        });

        holder.getImageButton_Phone().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBankHandler.callPhone(bank.getPhone());
            }
        });

        holder.getImageButton_Details().setOnClickListener(new View.OnClickListener() {
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

    ///////////////////////////////////////////////////////////////////////////

    public void setModels(List<Bank> _bankList) {
        mBankList = new ArrayList<>(_bankList);
    }

    public Bank removeItem(int position) {
        final Bank bank = mBankList.remove(position);
        notifyItemRemoved(position);
        return bank;
    }

    public void addItem(int position, Bank bank) {
        mBankList.add(position, bank);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final Bank bank = mBankList.remove(fromPosition);
        mBankList.add(toPosition, bank);
        notifyItemMoved(fromPosition, toPosition);
    }

    public void animateTo(List<Bank> bankList) {
        applyAndAnimateRemovals(bankList);
        applyAndAnimateAdditions(bankList);
        applyAndAnimateMovedItems(bankList);
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

    ///////////////////////////////////////////////////////////////////////////

    public void setBankHandler(IBankHandler bankHandler) {
        this.mBankHandler = bankHandler;
    }

    public void releaseBankHandler() {
        this.mBankHandler = null;
    }
}
