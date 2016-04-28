package mitso.v.homework_20.support;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.List;

import mitso.v.homework_20.R;
import mitso.v.homework_20.api.models.Bank;
import mitso.v.homework_20.api.models.Currency;
import mitso.v.homework_20.constansts.Constants;

public class SupportDetails {

    public void addCurrenciesToActivityLayout(Context _context, LinearLayout _linearLayout, List<Currency> _currencyList) {

        for (int i = 0; i < _currencyList.size(); i++) {

            final Currency currency = _currencyList.get(i);

            final RelativeLayout relativeLayout = new RelativeLayout(_context);
            relativeLayout.setPadding(0, _context.getResources().getDimensionPixelOffset(R.dimen.d_size_7dp),
                    0, _context.getResources().getDimensionPixelOffset(R.dimen.d_size_7dp));

            final RelativeLayout.LayoutParams nameParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            nameParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            nameParams.addRule(RelativeLayout.CENTER_VERTICAL);

            final TextView name = new TextView(_context);
            name.setTextSize(15); // !!!
            name.setTextColor(_context.getResources().getColor(R.color.c_text));
            name.setTypeface(Typeface.DEFAULT_BOLD);
            name.setText(String.format("%s%s", currency.getName().substring(0, 1).toUpperCase(), currency.getName().substring(1)));

            name.setLayoutParams(nameParams);
            relativeLayout.addView(name);

            final RelativeLayout.LayoutParams purchaseParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            purchaseParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            purchaseParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);

            final TextView purchase = new TextView(_context);
            purchase.setTextSize(15); // !!!
            purchase.setTextColor(_context.getResources().getColor(R.color.c_text));
            purchase.setTypeface(Typeface.DEFAULT_BOLD);
            purchase.setText(String.format("%.4f", currency.getPurchase()));
            purchase.setId(R.id.temp_id);

            purchase.setLayoutParams(purchaseParams);
            relativeLayout.addView(purchase);

            final RelativeLayout.LayoutParams saleParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            saleParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            saleParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            saleParams.addRule(RelativeLayout.BELOW, R.id.temp_id);

            final TextView sale = new TextView(_context);
            sale.setTextSize(15); // !!!
            sale.setTextColor(_context.getResources().getColor(R.color.c_text));
            sale.setTypeface(Typeface.DEFAULT_BOLD);
            sale.setText(String.format("%.4f", currency.getSale()));

            sale.setLayoutParams(saleParams);
            relativeLayout.addView(sale);

            _linearLayout.addView(relativeLayout);

            if (i != _currencyList.size() - 1) {

                final LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        _context.getResources().getDimensionPixelOffset(R.dimen.d_size_1dp));

                final TextView line = new TextView(_context);
                line.setBackgroundColor(_context.getResources().getColor(R.color.c_line));

                line.setLayoutParams(lineParams);

                _linearLayout.addView(line);
            }
        }
    }

    public LinearLayout createDialogLayout(Context _context, Bank _bank, int _layoutWidth) {

        final LinearLayout linearLayout = new LinearLayout(_context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setBackgroundColor(_context.getResources().getColor(R.color.c_view_bg));
        linearLayout.setPadding(
                _context.getResources().getDimensionPixelOffset(R.dimen.d_size_20dp),
                _context.getResources().getDimensionPixelOffset(R.dimen.d_size_20dp),
                _context.getResources().getDimensionPixelOffset(R.dimen.d_size_20dp),
                _context.getResources().getDimensionPixelOffset(R.dimen.d_size_20dp)
        );

        final TextView name = new TextView(_context);
        name.setTextColor(_context.getResources().getColor(R.color.c_title));
        name.setText(_bank.getName());
        name.setTextSize(21); // !!!
        name.setGravity(Gravity.START);

        final TextView region = new TextView(_context);
        region.setTextColor(_context.getResources().getColor(R.color.c_subtitle));
        if (_bank.getRegion().equals(_context.getResources().getString(R.string.s_capital)))
            region.setText(_context.getResources().getString(R.string.s_region));
        else
            region.setText(_bank.getRegion());
        region.setTextSize(17); // !!!
        region.setGravity(Gravity.START);

        final TextView city = new TextView(_context);
        city.setTextColor(_context.getResources().getColor(R.color.c_subtitle));
        city.setText(_bank.getCity());
        city.setTextSize(17); // !!!
        city.setGravity(Gravity.START);

        final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                _layoutWidth / 3 * 2,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, _context.getResources().getDimensionPixelOffset(R.dimen.d_size_10dp),
                                0, _context.getResources().getDimensionPixelOffset(R.dimen.d_size_10dp));

        final TextView date = new TextView(_context);
        date.setLayoutParams(layoutParams);
        date.setTextColor(_context.getResources().getColor(R.color.c_title_pink));
        date.setText(new SimpleDateFormat(Constants.DATE_AND_TIME_FORMAT_OUT).format(_bank.getDate()) );
        date.setTextSize(17); // !!!
        date.setGravity(Gravity.CENTER);

        linearLayout.addView(name);
        linearLayout.addView(region);
        linearLayout.addView(city);
        linearLayout.addView(date);

        addCurrenciesToDialogLayout(_context, linearLayout, _bank.getCurrencies());

        return linearLayout;
    }

    private void addCurrenciesToDialogLayout(Context _context, LinearLayout _linearLayout, List<Currency> _currencyList) {

        for (int i = 0; i < _currencyList.size(); i++) {

            final Currency currency = _currencyList.get(i);

            final RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);

            final RelativeLayout relativeLayout = new RelativeLayout(_context);
            relativeLayout.setLayoutParams(layoutParams);
            relativeLayout.setPadding(0, _context.getResources().getDimensionPixelOffset(R.dimen.d_size_7dp),
                    0, _context.getResources().getDimensionPixelOffset(R.dimen.d_size_7dp));

            final RelativeLayout.LayoutParams nameParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            nameParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            nameParams.addRule(RelativeLayout.CENTER_VERTICAL);

            final TextView abbreviation = new TextView(_context);
            abbreviation.setTextColor(_context.getResources().getColor(R.color.c_share_currency));
            abbreviation.setText(currency.getAbbreviation());
            abbreviation.setTextSize(19); // !!!
            abbreviation.setTypeface(Typeface.DEFAULT_BOLD);

            abbreviation.setLayoutParams(nameParams);
            relativeLayout.addView(abbreviation);

            final RelativeLayout.LayoutParams purchaseAndSaleParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            purchaseAndSaleParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            purchaseAndSaleParams.addRule(RelativeLayout.CENTER_VERTICAL);

            final TextView purchaseAndSale = new TextView(_context);
            purchaseAndSale.setTextColor(_context.getResources().getColor(R.color.c_text));
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

            purchaseAndSale.setLayoutParams(purchaseAndSaleParams);
            relativeLayout.addView(purchaseAndSale);

            _linearLayout.addView(relativeLayout);
        }
    }

    public Bitmap loadBitmapFromView(View _view, int _layoutWidth) {

        _view.layout(0, 0, _view.getMeasuredWidth(), _view.getMeasuredHeight());
        _view.measure(_layoutWidth, LinearLayout.LayoutParams.WRAP_CONTENT);

        final Bitmap bitmap = Bitmap.createBitmap(_view.getMeasuredWidth(), _view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        _view.layout(0, 0, _view.getMeasuredWidth(), _view.getMeasuredHeight());
        _view.draw(canvas);

        return bitmap;
    }

    public void showToast(Context _context, String _string) {

        final TextView textView = new TextView(_context);
        textView.setBackgroundColor(_context.getResources().getColor(R.color.c_toast_bg));
        textView.setTextColor(_context.getResources().getColor(R.color.c_toast_text));
        textView.setText(_string);
        textView.setGravity(Gravity.CENTER);
        final int padding = _context.getResources().getDimensionPixelSize(R.dimen.d_size_15dp);
        textView.setPadding(padding, padding, padding, padding);

        final Toast toast = new Toast(_context);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setView(textView);
        toast.show();
    }
}
