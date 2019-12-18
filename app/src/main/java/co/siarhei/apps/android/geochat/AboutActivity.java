
package co.siarhei.apps.android.geochat;


import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import mehdi.sakout.aboutpage.AboutPage;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setImage(R.drawable.geochat_logo_large_foreground)
                .setDescription("A geolocation-based messaging and media content sharing platform where the user interacts with the people who surround them geographically, rather than interacting only with their existing friends selected by their profile, as is common with existing messaging applications.")
                .addGroup("Connect with the GeoChat development team:")
                .addEmail("dexter0175@gmail,com")
                .addWebsite("http://bsuir.by")
                .addPlayStore(this.getPackageName())
                .addGitHub("dexter0175")
                .create();

        setContentView(aboutPage);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
