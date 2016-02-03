package flq.projectbooks.utilities;

import android.content.Context;
import android.os.AsyncTask;

import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.common.io.ByteStreams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import flq.projectbooks.Author;
import flq.projectbooks.Book;

/**
 * Created by Lucas on 30/01/2016.
 *
 * Abstract class that represent a source for retrieving books.
 * Every new source must extends this class and implements doInBackground method from AsyncTask method.
 *
 * The prototype : protected Book doInBackground(String... isbns);
 * In this method you can return the retrieved book or null.
 */
public abstract class GetBookInfo extends AsyncTask<String, Void, Book> {

    protected Context mContext;
    public GetBookInfo(Context context){
        mContext = context;
    }

    public interface AsyncResponse {
        void processFinish(Book output);
    }

    public AsyncResponse delegate = null;

    @Override
    protected void onPostExecute(Book result) {
            delegate.processFinish(result);
    }
}
