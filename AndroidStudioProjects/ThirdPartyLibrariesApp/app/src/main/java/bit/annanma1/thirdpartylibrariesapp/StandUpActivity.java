package bit.annanma1.thirdpartylibrariesapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;


public class StandUpActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stand_up);

        // Bind the ImageView
        ImageView imageView = (ImageView) findViewById(R.id.imageViewStandUp);

        // Set the ImageView OnClickListener
        imageView.setOnClickListener(new ClickHandler());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_stand_up, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_explode) {
            Intent goTo = new Intent(StandUpActivity.this, ExplodeImageActivity.class);
            startActivity(goTo);
        }

        if (id == R.id.action_ripple) {
            Intent goTo = new Intent(StandUpActivity.this, RippleBackgroundActivity.class);
            startActivity(goTo);
        }

        finish();
        return super.onOptionsItemSelected(item);
    }

    // Handler for the stand up animation. Called when the ImageView is clicked
    private class ClickHandler implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            YoYo.with(Techniques.StandUp).duration(1000).playOn(v);
        }
    }
}
