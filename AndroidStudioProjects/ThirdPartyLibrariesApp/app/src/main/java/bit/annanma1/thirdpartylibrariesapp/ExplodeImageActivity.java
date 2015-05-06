package bit.annanma1.thirdpartylibrariesapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.easyandroidanimations.library.Animation;
import com.easyandroidanimations.library.AnimationListener;
import com.easyandroidanimations.library.ExplodeAnimation;


public class ExplodeImageActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explode_image);

        // Bind the ImageView
        ImageView imageView = (ImageView) findViewById(R.id.imageViewExplode);

        // Set the ImageView OnClickListener
        imageView.setOnClickListener(new ClickHandler());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_explode_image, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_ripple) {
            Intent goTo = new Intent(ExplodeImageActivity.this, RippleBackgroundActivity.class);
            startActivity(goTo);
        }

        if (id == R.id.action_standup) {
            Intent goTo = new Intent(ExplodeImageActivity.this, StandUpActivity.class);
            startActivity(goTo);
        }

        finish();
        return super.onOptionsItemSelected(item);
    }

    // Handler for the explode animation. Called when the ImageView is clicked
    private class ClickHandler implements View.OnClickListener {
        @Override
        public void onClick(final View v) {
            new ExplodeAnimation(v)
                    .setExplodeMatrix(ExplodeAnimation.MATRIX_3X3)
                    .setInterpolator(new DecelerateInterpolator())
                    .setDuration(1000)
                    .setListener(new AnimationListener() {
                        @Override
                        //Once the animation has finished, restore the image to the screen
                        public void onAnimationEnd(Animation animation) {
                            v.setVisibility(View.VISIBLE);
                        }
                    })
                    .animate();
        }
    }
}
