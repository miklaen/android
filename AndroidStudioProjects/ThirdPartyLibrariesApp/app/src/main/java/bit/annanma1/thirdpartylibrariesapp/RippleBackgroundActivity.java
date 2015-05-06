package bit.annanma1.thirdpartylibrariesapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.skyfishjy.library.RippleBackground;


public class RippleBackgroundActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ripple_background);

        // Bind the ImageView
        ImageView imageView = (ImageView) findViewById(R.id.centerImage);

        // Set the ImageView OnClickListener
        imageView.setOnClickListener(new RippleHandler());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ripple_background, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_explode) {
            Intent goTo = new Intent(RippleBackgroundActivity.this, ExplodeImageActivity.class);
            startActivity(goTo);
        }

        if (id == R.id.action_standup) {
            Intent goTo = new Intent(RippleBackgroundActivity.this, StandUpActivity.class);
            startActivity(goTo);
        }

        finish();
        return super.onOptionsItemSelected(item);
    }

    // Handler for the ripple animation. Called when the ImageView is clicked
    public class RippleHandler implements View.OnClickListener {
        // Bind the ripple background
        final RippleBackground rippleBackground = (RippleBackground) findViewById(R.id.content);

        @Override
        public void onClick(View v) {
            // If the background is currently rippling when this click handler is called...
            if (rippleBackground.isRippleAnimationRunning()) {
                // ...stop the ripple animation
                rippleBackground.stopRippleAnimation();
            } else {
                // ...or if not, start the ripple animation
                rippleBackground.startRippleAnimation();
            }
        }
    }
}
