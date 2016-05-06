package com.example.pbuskell.asynctaskegfromcodepath;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    ImageView imgvwDisplay;
    ProgressBar prgbrProgress;
    Button btnGo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgvwDisplay = (ImageView) findViewById(R.id.imgvwDisplay);
        prgbrProgress = (ProgressBar) findViewById(R.id.prgbrProgress);
        btnGo = (Button) findViewById(R.id.btnGo);

    }

    // The types specified here are the input data type, the progress type, and the result type
    private class MyAsyncTask extends AsyncTask<String, Void, Bitmap> {
        protected void onPreExecute() {
            // Runs on the UI thread before doInBackground
            // Good for toggling visibility of a progress indicator
            prgbrProgress.setVisibility(ProgressBar.VISIBLE);
        }

        protected Bitmap doInBackground(String... strings) {
            // Some long-running task like downloading an image.
            Bitmap someBitmap = downloadImageFromUrl(strings[0]);
            return someBitmap;
        }

        protected void onProgressUpdate(Progress... values) {
            // Executes whenever publishProgress is called from doInBackground
            // Used to update the progress indicator
            prgbrProgress.setProgress(values[0]);
        }

        protected void onPostExecute(Bitmap result) {
            // This method is executed in the UIThread
            // with access to the result of the long running task
            imgvwDisplay.setImageBitmap(result);
            // Hide the progress bar
            prgbrProgress.setVisibility(ProgressBar.INVISIBLE);
        }

        private Bitmap downloadBitmap(String url) {
            // initilize the default HTTP client object
            final DefaultHttpClient client = new DefaultHttpClient();

            //forming a HttoGet request
            final HttpGet getRequest = new HttpGet(url);
            try {

                HttpResponse response = client.execute(getRequest);

                //check 200 OK for success
                final int statusCode = response.getStatusLine().getStatusCode();

                if (statusCode != HttpStatus.SC_OK) {
                    Log.w("ImageDownloader", "Error " + statusCode +
                            " while retrieving bitmap from " + url);
                    return null;

                }

                final HttpEntity entity = response.getEntity();
                if (entity != null) {
                    InputStream inputStream = null;
                    try {
                        // getting contents from the stream
                        inputStream = entity.getContent();

                        // decoding stream data back into image Bitmap that android understands
                        final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                        return bitmap;
                    } finally {
                        if (inputStream != null) {
                            inputStream.close();
                        }
                        entity.consumeContent();
                    }
                }
            } catch (Exception e) {
                // You Could provide a more explicit error message for IOException
                getRequest.abort();
                Log.e("ImageDownloader", "Something went wrong while" +
                        " retrieving bitmap from " + url + e.toString());
            }

            return null;
        }



    }




}
