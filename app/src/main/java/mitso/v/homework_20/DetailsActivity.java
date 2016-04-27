package mitso.v.homework_20;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import mitso.v.homework_20.api.models.Bank;
import mitso.v.homework_20.api.models.Currency;

public class DetailsActivity extends AppCompatActivity {

    public static boolean isActivityRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isActivityRunning = true;

        setContentView(R.layout.details_activity);

        Bundle bundle = getIntent().getExtras();
        Bank bank = (Bank) bundle.getSerializable("bank");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow);

            getSupportActionBar().setTitle(Html.fromHtml("<font color='#" +
                    Integer.toHexString(getResources().getColor(R.color.c_action_bar_text)).substring(2) +
                    "'>" + bank.getName() + "</font>"));
        }

        TextView textView_BankRegion = (TextView) findViewById(R.id.tv_BankRegion_AD);
        TextView textView_BankCity = (TextView) findViewById(R.id.tv_BankCity_AD);
        TextView textView_BankName = (TextView) findViewById(R.id.tv_BankName_AD);
        TextView textView_BankAddress = (TextView) findViewById(R.id.tv_BankAddress_AD);
        TextView textView_BankPhone = (TextView) findViewById(R.id.tv_BankPhone_AD);
        TextView textView_BankDate = (TextView) findViewById(R.id.tv_BankDate_AD);
        TextView textView_BankTime = (TextView) findViewById(R.id.tv_BankTime_AD);


        if (bank.getRegion().equals(getResources().getString(R.string.s_capital)))
            textView_BankRegion.setText(getResources().getString(R.string.s_region));
        else
            textView_BankRegion.setText(bank.getRegion());
        textView_BankCity.setText(bank.getCity());
        textView_BankName.setText(bank.getName());
        textView_BankAddress.setText(bank.getAddress());
        textView_BankPhone.setText(bank.getPhone());
        textView_BankDate.setText(new SimpleDateFormat("dd MMMM yyyy").format(bank.getDate()));
        textView_BankTime.setText(new SimpleDateFormat("HH:mm:ss").format(bank.getDate()));



        LinearLayout linearLayout_Currencies = (LinearLayout) findViewById(R.id.rl_Currencies_AD);

        createCurrencyCard(linearLayout_Currencies, bank.getCurrencies());
    }

    private void createCurrencyCard(LinearLayout _linearLayout, List<Currency> _currencyList) {

        for (int i = 0; i < _currencyList.size(); i++) {

            Currency currency = _currencyList.get(i);

            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);

            RelativeLayout relativeLayout = new RelativeLayout(this);
            relativeLayout.setLayoutParams(layoutParams);
            relativeLayout.setPadding(0, getResources().getDimensionPixelOffset(R.dimen.d_size_7dp),
                    0, getResources().getDimensionPixelOffset(R.dimen.d_size_7dp));

            RelativeLayout.LayoutParams nameParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            nameParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            nameParams.addRule(RelativeLayout.CENTER_VERTICAL);

            TextView name = new TextView(this);
            name.setTextColor(getResources().getColor(R.color.c_text));
            name.setTypeface(Typeface.DEFAULT_BOLD);
            name.setText(String.format("%s%s", currency.getName().substring(0, 1).toUpperCase(), currency.getName().substring(1)));

            name.setLayoutParams(nameParams);
            relativeLayout.addView(name);

            RelativeLayout.LayoutParams purchaseParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            purchaseParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            purchaseParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);

            TextView purchase = new TextView(this);
            purchase.setTextColor(getResources().getColor(R.color.c_text));
            purchase.setTypeface(Typeface.DEFAULT_BOLD);
            purchase.setText(String.format("%.4f", currency.getPurchase()));
            purchase.setId(1);

            purchase.setLayoutParams(purchaseParams);
            relativeLayout.addView(purchase);

            RelativeLayout.LayoutParams saleParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            saleParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            saleParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            saleParams.addRule(RelativeLayout.BELOW, 1);

            TextView sale = new TextView(this);
            sale.setTextColor(getResources().getColor(R.color.c_text));
            sale.setTypeface(Typeface.DEFAULT_BOLD);
            sale.setText(String.format("%.4f", currency.getSale()));

            sale.setLayoutParams(saleParams);
            relativeLayout.addView(sale);

            _linearLayout.addView(relativeLayout);

            if (i != _currencyList.size() - 1) {

                LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        getResources().getDimensionPixelOffset(R.dimen.d_size_1dp));

                TextView line = new TextView(this);
                line.setBackgroundColor(getResources().getColor(R.color.c_line));

                line.setLayoutParams(lineParams);

                _linearLayout.addView(line);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        isActivityRunning = true;
    }

    @Override
    protected void onPause() {
        super.onPause();

        isActivityRunning = true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
