package flq.projectbooks.utilities;

import android.content.Context;
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

import flq.projectbooks.data.Author;
import flq.projectbooks.data.Book;
import flq.projectbooks.data.libraries.BookLibrary;

/**
 * Created by doublet on 05/11/15.
 */
public class GetBookInfoGoogleBooksAPI extends GetBookInfo {

    public GetBookInfoGoogleBooksAPI(Context context) {
        super(context);
    }

    @Override
    protected Book doInBackground(String... isbns) {
        // Stop if cancelled
        if (isCancelled()) {
            return null;
        }

        String apiUrlString = "https://www.googleapis.com/books/v1/volumes?q=isbn:" + isbns[0];

        try {
            HttpURLConnection connection = null;
            // Build Connection.
            try {
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
            if (responseCode != 200) {
                Log.w(getClass().getName(), "GoogleBooksAPI request failed. Response Code: " + responseCode);
                connection.disconnect();
                return null;
            }

            // Read data from response.
            StringBuilder builder = new StringBuilder();
            BufferedReader responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = responseReader.readLine();
            while (line != null) {
                builder.append(line);
                line = responseReader.readLine();
            }
            String responseString = builder.toString();
            Log.d(getClass().getName(), "Response String: " + responseString);
            JSONObject responseJson = new JSONObject(responseString);
            // Close connection and return response code.
            connection.disconnect();

            if (responseJson.getInt("totalItems") >= 1) {
                Book newBook = BookLibrary.getInstance().getNewBook();
                String title = responseJson.getJSONArray("items").getJSONObject(0).getJSONObject("volumeInfo").getString("title");
                JSONArray arr = responseJson.getJSONArray("items").getJSONObject(0).getJSONObject("volumeInfo").getJSONArray("authors");
                List<Author> authors = new ArrayList<Author>();
                for (int i = 0; i < arr.length(); i++) {
                    Author author = new Author(arr.getString(i));
                    authors.add(author);
                }
                newBook.setAuthors(authors);


                if (responseJson.getJSONArray("items").getJSONObject(0).getJSONObject("volumeInfo").has("imageLinks")) {
                    InputStream is = (InputStream) new URL(responseJson.getJSONArray("items").getJSONObject(0).getJSONObject("volumeInfo").getJSONObject("imageLinks").getString("thumbnail")).getContent();
                    byte[] image = ByteStreams.toByteArray(is);
                    newBook.setImage(image);
                }

                if (responseJson.getJSONArray("items").getJSONObject(0).getJSONObject("volumeInfo").has("description")) {
                    String description = responseJson.getJSONArray("items").getJSONObject(0).getJSONObject("volumeInfo").getString("description");
                    newBook.setDescription(description);
                }

                if (responseJson.getJSONArray("items").getJSONObject(0).getJSONObject("volumeInfo").has("publishedDate")) {
                    String datePub = responseJson.getJSONArray("items").getJSONObject(0).getJSONObject("volumeInfo").getString("publishedDate");
                    newBook.setDatePublication(datePub);
                }

                if (responseJson.getJSONArray("items").getJSONObject(0).getJSONObject("volumeInfo").has("publisher")) {
                    String editor = responseJson.getJSONArray("items").getJSONObject(0).getJSONObject("volumeInfo").getString("publisher");
                    newBook.setEditor(editor);
                }
                if (responseJson.getJSONArray("items").getJSONObject(0).getJSONObject("volumeInfo").has("categories")) {
                    String category = "";
                    JSONArray arrCategory = responseJson.getJSONArray("items").getJSONObject(0).getJSONObject("volumeInfo").getJSONArray("categories");
                    for (int i = 0; i < arrCategory.length(); i++) {
                        if (category == "") {
                            category += arrCategory.getString(i);
                        } else {
                            category += ", " + arrCategory.getString(i);
                        }
                    }
                    newBook.setCategory(category);
                }

                if (responseJson.getJSONArray("items").getJSONObject(0).getJSONObject("volumeInfo").has("pageCount")) {
                    String nbPages = responseJson.getJSONArray("items").getJSONObject(0).getJSONObject("volumeInfo").getString("pageCount");
                    newBook.setNbPages(Integer.parseInt(nbPages));
                }


                newBook.setIsbn(isbns[0]);
                newBook.setTitle(title);
                return newBook;
            } else {
                Handler handler = new Handler(mContext.getMainLooper());
                handler.post(new Runnable() {

                    public void run() {
                        Toast.makeText(mContext, "Aucun livre trouvé.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            return null;
        } catch (SocketTimeoutException e) {
            Log.w(getClass().getName(), "Connection timed out. Returning null");
            return null;
        } catch (IOException e) {
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
