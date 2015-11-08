package flq.projectbooks;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.common.io.ByteStreams;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by doublet on 05/11/15.
 */
public class GetBookInfo extends AsyncTask<String, Void, Void> {

    private Context mContext;
    public GetBookInfo (Context context){
        mContext = context;
    }

    @Override
    protected Void doInBackground(String... isbns) {
        // Stop if cancelled
        if(isCancelled()){
            return null;
        }

        String key = "" ;

        String apiUrlString =   "https://www.googleapis.com/books/v1/volumes?q=isbn:" + isbns[0] ;

        try{
            HttpURLConnection connection = null;
            // Build Connection.
            try{
                URL url = new URL(apiUrlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setReadTimeout(5000); // 5 seconds
                connection.setConnectTimeout(5000); // 5 seconds
            } catch (MalformedURLException e) {
                // Impossible: The only two URLs used in the app are taken from string resources.
                e.printStackTrace();
            } catch (ProtocolException e) {
                // Impossible: "GET" is a perfectly valid request method.
                e.printStackTrace();
            }
            int responseCode = connection.getResponseCode();
            if(responseCode != 200){
                Log.w(getClass().getName(), "GoogleBooksAPI request failed. Response Code: " + responseCode);
                connection.disconnect();
                return null;
            }

            // Read data from response.
            StringBuilder builder = new StringBuilder();
            BufferedReader responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = responseReader.readLine();
            while (line != null){
                builder.append(line);
                line = responseReader.readLine();
            }
            String responseString = builder.toString();
            Log.d(getClass().getName(), "Response String: " + responseString);
            JSONObject responseJson = new JSONObject(responseString);
            // Close connection and return response code.
            connection.disconnect();

            if(responseJson.getInt("totalItems") >= 1) {
                String title = responseJson.getJSONArray("items").getJSONObject(0).getJSONObject("volumeInfo").getString("title");
                JSONArray arr = responseJson.getJSONArray("items").getJSONObject(0).getJSONObject("volumeInfo").getJSONArray("authors");
                String author = "";
                for (int i = 0; i < arr.length(); i++) {
                    if (author == "") {
                        author += arr.getString(i);
                    } else {
                        author += " , " + arr.getString(i);
                    }
                }

                Book newBook = BookLibrary.getInstance().getNewBook();
                if(responseJson.getJSONArray("items").getJSONObject(0).getJSONObject("volumeInfo").has("imageLinks")){
                    InputStream is = (InputStream) new URL(responseJson.getJSONArray("items").getJSONObject(0).getJSONObject("volumeInfo").getJSONObject("imageLinks").getString("thumbnail")).getContent();
                    byte[] image = ByteStreams.toByteArray(is);
                    newBook.setImage(image);
                }

                if(responseJson.getJSONArray("items").getJSONObject(0).getJSONObject("volumeInfo").has("description")) {
                    String description = responseJson.getJSONArray("items").getJSONObject(0).getJSONObject("volumeInfo").getString("description");
                    newBook.setDescription(description);
                }

                newBook.setTitle(title);
                newBook.setAuthor(author);


                BookLibrary.getInstance().Add(newBook);
                Handler handler = new Handler(mContext.getMainLooper());
                handler.post(new Runnable() {

                    public void run() {
                        Toast.makeText(mContext, "Livre ajouté", Toast.LENGTH_SHORT).show();
                    }
                });
            }else{
                Handler handler = new Handler(mContext.getMainLooper());
                handler.post(new Runnable() {

                    public void run() {
                        Toast.makeText(mContext, "Aucun livre trouvé.", Toast.LENGTH_SHORT).show();
                    }
                });
            }


            return null; //return responseJson;
        } catch (SocketTimeoutException e) {
            Log.w(getClass().getName(), "Connection timed out. Returning null");
            return null;
        } catch (IOException e){
            Log.d(getClass().getName(), "IOException when connecting to Google Books API.");
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
            Log.d(getClass().getName(), "JSONException when connecting to Google Books API.");
            e.printStackTrace();
            return null;
        }
    }

}
