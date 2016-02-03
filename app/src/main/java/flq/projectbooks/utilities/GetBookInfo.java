package flq.projectbooks.utilities;

import android.content.Context;
import android.os.AsyncTask;

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
