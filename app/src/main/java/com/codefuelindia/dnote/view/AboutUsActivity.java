package com.codefuelindia.dnote.view;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.codefuelindia.dnote.R;

public class AboutUsActivity extends AppCompatActivity implements View.OnClickListener {

    TextView textView_website, textView_email, textView_number;
    ImageView imageView_location;

    String website, email, number;

    ColorStateList oldTextColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        textView_website = findViewById(R.id.about_us_tv_website);
        textView_email = findViewById(R.id.about_us_tv_email);
        textView_number = findViewById(R.id.about_us_tv_number);
        imageView_location = findViewById(R.id.about_us_iv_location);

        website = textView_website.getText().toString();
        email = textView_email.getText().toString();
        number = textView_number.getText().toString();

        oldTextColor = textView_website.getTextColors();

        textView_website.setOnClickListener(this);
        textView_email.setOnClickListener(this);
        textView_number.setOnClickListener(this);
        imageView_location.setOnClickListener(this);


        textView_website.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(website));
                startActivity(browserIntent);

                return true;
            }
        });

        textView_email.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                sendIntent.setType("plain/text");
                sendIntent.setData(Uri.parse(email));
                sendIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
                sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
                startActivity(sendIntent);

                return true;
            }
        });

        textView_number.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + number));
                startActivity(intent);

                return true;
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.about_us_tv_website:
                goToWebsite();
                break;

            case R.id.about_us_tv_email:
                goToGmail();
                break;

            case R.id.about_us_tv_number:
                goToDialer();
                break;

            case R.id.about_us_iv_location:
                jumpToAddress();
                break;

        }

    }

    private void goToWebsite() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("website", website);
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
            textView_website.setTextColor(oldTextColor);
            Toast.makeText(getApplicationContext(), "Link copied", Toast.LENGTH_SHORT).show();
        }
    }

    private void goToGmail() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("email", email);
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getApplicationContext(), "E-mail copied", Toast.LENGTH_SHORT).show();
        }
    }

    private void goToDialer() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("number", number);
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getApplicationContext(), "Number copied", Toast.LENGTH_SHORT).show();
        }
    }

    private void jumpToAddress() {
        Uri gmmIntentUri = Uri.parse("geo:23.574246, 72.965482");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }
    }

}
