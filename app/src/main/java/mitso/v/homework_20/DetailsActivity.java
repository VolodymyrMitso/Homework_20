package mitso.v.homework_20;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import mitso.v.homework_20.api.models.Bank;
import mitso.v.homework_20.constansts.Constants;
import mitso.v.homework_20.service.UpdateService;
import mitso.v.homework_20.support.SupportDetails;

public class DetailsActivity extends AppCompatActivity {

    public static boolean       isActivityRunning;

    private SupportDetails      mSupport;

    private Bank                mBank;

    private int                 mLayoutWidth;
    private ShareDialogFragment mShareDialogFragmentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (UpdateService.isServiceRunning) {
            mSupport.showToast(this, getResources().getString(R.string.s_updating));
            finish();
        }

        isActivityRunning = true;

        mSupport = new SupportDetails();

        final Bundle bundle = getIntent().getExtras();
        mBank = (Bank) bundle.getSerializable(Constants.BANK_BUNDLE_KEY);

        setContentView(R.layout.details_activity);
        initActionBar();
        initLayout();
        initViews();

        final LinearLayout linearLayout_Currencies = (LinearLayout) findViewById(R.id.rl_Currencies_AD);
        mSupport.addCurrenciesToActivityLayout(this, linearLayout_Currencies, mBank.getCurrencies());
    }

    private void initActionBar() {

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_details_arrow);

            getSupportActionBar().setTitle(Html.fromHtml(Constants.FONT_COLOR_1 +
                    Integer.toHexString(getResources().getColor(R.color.c_action_bar_text)).substring(2) +
                    Constants.FONT_COLOR_2 + mBank.getName() + Constants.FONT_COLOR_3));
        }
    }

    private void initLayout() {

        final RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.rl_DetailsLayout_AD);
        if (relativeLayout != null) {
            relativeLayout.post(new Runnable() {
                @Override
                public void run() {
                    mLayoutWidth = relativeLayout.getWidth();
                }
            });
        }
    }

    private void initViews() {

        final TextView textView_BankName = (TextView) findViewById(R.id.tv_BankName_AD);
        final TextView textView_BankRegion = (TextView) findViewById(R.id.tv_BankRegion_AD);
        final TextView textView_BankCity = (TextView) findViewById(R.id.tv_BankCity_AD);
        final TextView textView_BankAddress = (TextView) findViewById(R.id.tv_BankAddress_AD);
        final TextView textView_BankPhone = (TextView) findViewById(R.id.tv_BankPhone_AD);
        final TextView textView_BankDate = (TextView) findViewById(R.id.tv_BankDate_AD);
        final TextView textView_BankTime = (TextView) findViewById(R.id.tv_BankTime_AD);

        ifViewNotNullSetText(textView_BankName, mBank.getName());
        ifViewNotNullSetText(textView_BankRegion, mBank.getRegion());
        ifViewNotNullSetText(textView_BankCity, mBank.getCity());
        ifViewNotNullSetText(textView_BankAddress, mBank.getAddress());
        ifViewNotNullSetText(textView_BankPhone, mBank.getPhone());
        ifViewNotNullSetText(textView_BankDate, new SimpleDateFormat(Constants.DATE_FORMAT).format(mBank.getDate()));
        ifViewNotNullSetText(textView_BankTime, new SimpleDateFormat(Constants.TIME_FORMAT).format(mBank.getDate()));
    }

    private void ifViewNotNullSetText(TextView _textView, String _string) {

        if (_textView != null)
            _textView.setText(_string);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (UpdateService.isServiceRunning) {
            mSupport.showToast(this, getResources().getString(R.string.s_updating));
            finish();
        }

        isActivityRunning = true;
    }

    @Override
    protected void onPause() {
        super.onPause();

        isActivityRunning = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                finish();

                return true;
            case R.id.mi_Link_c:

                final Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mBank.getLink()));
                if (browserIntent.resolveActivity(getPackageManager()) != null)
                    startActivity(Intent.createChooser(browserIntent, getResources().getString(R.string.choose_program)));
                else
                    mSupport.showToast(this, getResources().getString(R.string.necessary_program));

                return true;
            case R.id.mi_Map_DM:

                final Intent addressIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.GEO + mBank.getRegion() + ", " + mBank.getCity() + ", " + mBank.getAddress()));
                if (addressIntent.resolveActivity(getPackageManager()) != null)
                    startActivity(Intent.createChooser(addressIntent, getResources().getString(R.string.choose_program)));
                else
                    mSupport.showToast(this, getResources().getString(R.string.necessary_program));

                return true;
            case R.id.mi_Phone_DM:

                final Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(Constants.TEL + mBank.getPhone()));
                if (callIntent.resolveActivity(getPackageManager()) != null)
                    startActivity(Intent.createChooser(callIntent, getResources().getString(R.string.choose_program)));
                else
                    mSupport.showToast(this, getResources().getString(R.string.necessary_program));

                return true;
            case R.id.mi_Share_DM:

                mShareDialogFragmentFragment = new ShareDialogFragment();
                final Bundle bundle = new Bundle();

                final LinearLayout linearLayout = mSupport.createDialogLayout(this, mBank, mLayoutWidth);
                final Bitmap bitmap = mSupport.loadBitmapFromView(linearLayout, mLayoutWidth);

                bundle.putParcelable(Constants.BITMAP_BUNDLE_KEY, bitmap);
                mShareDialogFragmentFragment.setArguments(bundle);
                mShareDialogFragmentFragment.show(getSupportFragmentManager(), Constants.SHARE_DIALOG_FRAGMENT_TAG);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
