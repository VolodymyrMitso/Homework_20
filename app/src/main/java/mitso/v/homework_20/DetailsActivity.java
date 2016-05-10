package mitso.v.homework_20;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import mitso.v.homework_20.api.models.Bank;
import mitso.v.homework_20.constansts.Constants;
import mitso.v.homework_20.databinding.DetailsActivityBinding;
import mitso.v.homework_20.service.UpdateService;
import mitso.v.homework_20.support.SupportDetails;


public class DetailsActivity extends AppCompatActivity {

    public static boolean           isActivityRunning;

    private SupportDetails          mSupport;

    private DetailsActivityBinding  mBinding;

    private Bank                    mBank;

    private int                     mLayoutWidth;
    private ShareDialogFragment     mShareDialogFragmentFragment;

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

        mBinding = DataBindingUtil.setContentView(this, R.layout.details_activity);
        initActionBar();
        initLayout();
        mBinding.setBank(mBank);
        mSupport.addCurrenciesToActivityLayout(this, mBinding.llCurrenciesAD, mBank.getCurrencies());
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

        if (mBinding.rlDetailsAD != null) {
            mBinding.rlDetailsAD.post(new Runnable() {
                @Override
                public void run() {
                    mLayoutWidth = mBinding.rlDetailsAD.getWidth();
                }
            });
        }
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
