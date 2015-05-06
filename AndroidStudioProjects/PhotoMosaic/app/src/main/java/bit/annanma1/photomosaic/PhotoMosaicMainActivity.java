package bit.annanma1.photomosaic;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


public class PhotoMosaicMainActivity extends ActionBarActivity {
    File mPhotoFile;
    String mPhotoFileName;
    Uri mPhotoFileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_mosaic_main);

        Button btnPhoto = (Button) findViewById(R.id.btnPhotoIntent);
        btnPhoto.setOnClickListener(new PhotoButtonHandler());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_photo_mosaic_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check if this is the return we are waiting for
        if (requestCode == 1) {

            // Check photo data was stored correctly
            if (resultCode == RESULT_OK) {

                // Get the filepath, not the file, for BitmapFactory
                String realFilePath = mPhotoFile.getPath();

                Bitmap photoBitmap = BitmapFactory.decodeFile(realFilePath);

                // Display the bitmap in each ImageView (TODO more efficiently?)
                ImageView ul = (ImageView) findViewById(R.id.ivUL);
                ul.setImageBitmap(photoBitmap);

                ImageView ur = (ImageView) findViewById(R.id.ivUR);
                ur.setImageBitmap(photoBitmap);

                ImageView ml = (ImageView) findViewById(R.id.ivML);
                ml.setImageBitmap(photoBitmap);

                ImageView mr = (ImageView) findViewById(R.id.ivMR);
                mr.setImageBitmap(photoBitmap);
            } else {
                Toast.makeText(this, "No photo was saved.", Toast.LENGTH_LONG).show();
            }
        }
    }

    // Method to create a file to store the captured image in
    public File createTimestampedFile() {
        // Fetch system image directory
        File imgRootPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        // Make subdirectory
        File imgStorageDirectory = new File(imgRootPath, "PhotoMosaic");
        if (!imgStorageDirectory.exists()) {
            imgStorageDirectory.mkdirs();
        }

        // Get timestamp
        SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        Date currTime = new Date();
        String timestamp = timestampFormat.format(currTime);

        // Make filename
        mPhotoFileName = "IMG_" + timestamp + ".jpg";

        // Make file object from directory and filename
        File photoFile = new File(imgStorageDirectory.getPath() + File.separator + mPhotoFileName);

        // Return the full filename
        return photoFile;
    }

    // Handler for the Photo Intent button
    private class PhotoButtonHandler implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // Create timestamped file to hold image data
            mPhotoFile = createTimestampedFile();

            // Generate URI from the File instance
            mPhotoFileUri = Uri.fromFile(mPhotoFile);

            // Create an intent for the camera app
            Intent imgCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            // Attach URI to the intent
            imgCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoFileUri);

            // Launch the intent, wait for the result
            startActivityForResult(imgCaptureIntent, 1);
        }
    }
}
