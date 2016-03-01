package flq.projectbooks.utilities;

import android.content.Context;
import android.os.AsyncTask;

import flq.projectbooks.data.Book;

/**
 * Created by Lucas on 30/01/2016.
 * <p/>
 * Abstract class that represent a source for retrieving books.
 * Every new source must extends this class and implements doInBackground method from AsyncTask method.
 * <p/>
 * The prototype : protected Book doInBackground(String... isbns);
 * In this method you can return the retrieved book or null.
 */
public abstract class GetBookInfo extends AsyncTask<String, Void, Book> {

    public AsyncResponse delegate = null;
    protected Context mContext;
    protected Integer sourceLogoName ;

    public GetBookInfo(Context context) {
        mContext = context;
    }

    @Override
    protected void onPostExecute(Book result) {
        delegate.processFinish(result, sourceLogoName);
    }

    public interface AsyncResponse {
        void processFinish(Book output, Integer sourceLogoName);
    }

}
