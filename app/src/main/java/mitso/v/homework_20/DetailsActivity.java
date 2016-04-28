package mitso.v.homework_20;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.List;

import mitso.v.homework_20.api.models.Bank;
import mitso.v.homework_20.api.models.Currency;

public class DetailsActivity extends AppCompatActivity {

    public static boolean isActivityRunning;

    private Bank mBank;
    private int layoutWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isActivityRunning = true;

        setContentView(R.layout.details_activity);

        final RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.rl_DetailsLayout_AD);
        if (relativeLayout != null) {
            relativeLayout.post(new Runnable() {
                @Override
                public void run() {
                    layoutWidth = relativeLayout.getWidth();
                }
            });
        }


        Bundle bundle = getIntent().getExtras();
        mBank = (Bank) bundle.getSerializable("bank");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_details_arrow);

            getSupportActionBar().setTitle(Html.fromHtml("<font color='#" +
                    Integer.toHexString(getResources().getColor(R.color.c_action_bar_text)).substring(2) +
                    "'>" + mBank.getName() + "</font>"));
        }

        TextView textView_BankRegion = (TextView) findViewById(R.id.tv_BankRegion_AD);
        TextView textView_BankCity = (TextView) findViewById(R.id.tv_BankCity_AD);
        TextView textView_BankName = (TextView) findViewById(R.id.tv_BankName_AD);
        TextView textView_BankAddress = (TextView) findViewById(R.id.tv_BankAddress_AD);
        TextView textView_BankPhone = (TextView) findViewById(R.id.tv_BankPhone_AD);
        TextView textView_BankDate = (TextView) findViewById(R.id.tv_BankDate_AD);
        TextView textView_BankTime = (TextView) findViewById(R.id.tv_BankTime_AD);


        if (mBank.getRegion().equals(getResources().getString(R.string.s_capital)))
            textView_BankRegion.setText(getResources().getString(R.string.s_region));
        else
            textView_BankRegion.setText(mBank.getRegion());
        textView_BankCity.setText(mBank.getCity());
        textView_BankName.setText(mBank.getName());
        textView_BankAddress.setText(mBank.getAddress());
        textView_BankPhone.setText(mBank.getPhone());
        textView_BankDate.setText(new SimpleDateFormat("dd MMMM yyyy").format(mBank.getDate()));
        textView_BankTime.setText(new SimpleDateFormat("HH:mm:ss").format(mBank.getDate()));



        LinearLayout linearLayout_Currencies = (LinearLayout) findViewById(R.id.rl_Currencies_AD);

        createCurrencyCard(linearLayout_Currencies, mBank.getCurrencies());
    }


    private void createCurrencyCard(LinearLayout _linearLayout, List<Currency> _currencyList) {

        for (int i = 0; i < _currencyList.size(); i++) {

            Currency currency = _currencyList.get(i);

            RelativeLayout relativeLayout = new RelativeLayout(this);
            relativeLayout.setPadding(0, getResources().getDimensionPixelOffset(R.dimen.d_size_7dp),
                    0, getResources().getDimensionPixelOffset(R.dimen.d_size_7dp));

            RelativeLayout.LayoutParams nameParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            nameParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            nameParams.addRule(RelativeLayout.CENTER_VERTICAL);

            TextView name = new TextView(this);
            name.setTextSize(15);
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
            purchase.setTextSize(15);
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
            sale.setTextSize(15);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    private DialogFragment shareDialogFragment;


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:

                finish();

                return true;
            case R.id.mi_Link_c:

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mBank.getLink()));

                if (browserIntent.resolveActivity(getPackageManager()) != null)
                    startActivity(Intent.createChooser(browserIntent, "Choose browser:"));
                else
                    Toast.makeText(this, "You don't have necessary program", Toast.LENGTH_SHORT).show();

                return true;
            case R.id.mi_Map_DM:

                Intent addressIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + mBank.getRegion() + ", " + mBank.getCity() + ", " + mBank.getAddress()));

                if (addressIntent.resolveActivity(getPackageManager()) != null)
                    startActivity(Intent.createChooser(addressIntent, "Choose program to show on map:"));
                else
                    Toast.makeText(this, "You don't have necessary program", Toast.LENGTH_SHORT).show();

                return true;
            case R.id.mi_Phone_DM:

                Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:+38" + mBank.getPhone()));

                if (callIntent.resolveActivity(getPackageManager()) != null)
                    startActivity(Intent.createChooser(callIntent, "Choose how to call:"));
                else
                    Toast.makeText(this, "You don't have necessary program", Toast.LENGTH_SHORT).show();

                return true;
            case R.id.mi_Share_DM:

                shareDialogFragment = new ShareDialog();

                Bundle bundle = new Bundle();

                LinearLayout linearLayout = createLayout(mBank);

                linearLayout.layout(0, 0, linearLayout.getMeasuredWidth(), linearLayout.getMeasuredHeight());

                Bitmap bitmap = loadBitmapFromView(linearLayout);

                if (bitmap == null)
                    Toast.makeText(DetailsActivity.this, "null", Toast.LENGTH_SHORT).show();

                bundle.putParcelable("bitmap", bitmap);

                shareDialogFragment.setArguments(bundle);

                shareDialogFragment.show(getSupportFragmentManager(), "share_fragment");

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public Bitmap loadBitmapFromView(View v) {

        v.measure(layoutWidth, LinearLayout.LayoutParams.WRAP_CONTENT);

        Bitmap b = Bitmap.createBitmap(v.getMeasuredWidth(), v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        v.draw(c);

        return b;
    }

    private LinearLayout createLayout(Bank _bank) {

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setBackgroundColor(getResources().getColor(R.color.c_view_bg));
        linearLayout.setPadding(
                getResources().getDimensionPixelOffset(R.dimen.d_size_20dp),
                getResources().getDimensionPixelOffset(R.dimen.d_size_20dp),
                getResources().getDimensionPixelOffset(R.dimen.d_size_20dp),
                getResources().getDimensionPixelOffset(R.dimen.d_size_20dp)
        );

        TextView name = new TextView(this);
        name.setTextColor(getResources().getColor(R.color.c_title));
        name.setText(_bank.getName());
        name.setTextSize(21);
        name.setGravity(Gravity.START);

        TextView region = new TextView(this);
        region.setTextColor(getResources().getColor(R.color.c_subtitle));
        if (mBank.getRegion().equals(getResources().getString(R.string.s_capital)))
            region.setText(getResources().getString(R.string.s_region));
        else
            region.setText(mBank.getRegion());
        region.setTextSize(17);
        region.setGravity(Gravity.START);

        TextView city = new TextView(this);
        city.setTextColor(getResources().getColor(R.color.c_subtitle));
        city.setText(_bank.getCity());
        city.setTextSize(17);
        city.setGravity(Gravity.START);

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                layoutWidth/3*2,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        layoutParams.setMargins(0,getResources().getDimensionPixelOffset(R.dimen.d_size_10dp),0,getResources().getDimensionPixelOffset(R.dimen.d_size_10dp));

        TextView date = new TextView(this);
        date.setLayoutParams(layoutParams);

        date.setTextColor(getResources().getColor(R.color.c_title_pink));
        date.setText(new SimpleDateFormat("dd MMMM yyyy - HH:mm:ss").format(_bank.getDate()) );
        date.setTextSize(17);
        date.setGravity(Gravity.CENTER);

        linearLayout.addView(name);
        linearLayout.addView(region);
        linearLayout.addView(city);
        linearLayout.addView(date);

        createCurrencyLayouts(linearLayout, _bank.getCurrencies());

        return linearLayout;
    }

    private void createCurrencyLayouts(LinearLayout _linearLayout, List<Currency> _currencyList) {

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

            TextView abbreviation = new TextView(this);
            abbreviation.setTextColor(getResources().getColor(R.color.c_share_currency));
            abbreviation.setText(currency.getAbbreviation());
            abbreviation.setTextSize(19);
            abbreviation.setTypeface(Typeface.DEFAULT_BOLD);

            abbreviation.setLayoutParams(nameParams);
            relativeLayout.addView(abbreviation);

            RelativeLayout.LayoutParams purchaseParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            purchaseParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            purchaseParams.addRule(RelativeLayout.CENTER_VERTICAL);

            TextView purchaseAndSale = new TextView(this);
            purchaseAndSale.setTextColor(getResources().getColor(R.color.c_text));

            String purchase;
            if (currency.getPurchase() < 10)
                purchase = "0" + String.format("%.2f", currency.getPurchase());
            else
                purchase = String.format("%.2f", currency.getPurchase());


            String sale;
            if (currency.getSale() < 10)
                sale = "0" + String.format("%.2f", currency.getSale());
            else
                sale = String.format("%.2f", currency.getSale());

            purchaseAndSale.setText(String.format("%s/%s", purchase, sale));
            purchaseAndSale.setTextSize(19);


            purchaseAndSale.setLayoutParams(purchaseParams);
            relativeLayout.addView(purchaseAndSale);

            _linearLayout.addView(relativeLayout);
        }
    }
}
