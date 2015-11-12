package flq.projectbooks;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;

public class CreateBook extends ActionBarActivity implements GetBookInfo.AsyncResponse {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    public final static String GIVE_BOOK_BACK = "flq.NEWBOOK";
    private Book book;
    protected GetBookInfo asyncTask ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_book);

        Intent intent = getIntent();
        if(intent.hasExtra(DisplayBooks.GIVE_BOOK)) {
            setTitle("Modification d'un livre");
            book = (Book) intent.getSerializableExtra(DisplayBooks.GIVE_BOOK);
        } else {
            setTitle("Création d'un livre");
            book = (Book) intent.getSerializableExtra(Main.ASK_NEW_BOOK);
        }

        ((TextView)findViewById(R.id.bookTitle)).setText(book.getTitle());
        ((TextView)findViewById(R.id.bookISBN)).setText(book.getIsbn());
        ((TextView)findViewById(R.id.bookAuthor)).setText(book.getAuthor());
        ((TextView)findViewById(R.id.bookDescription)).setText(book.getDescription());
        if(book.getImage() != null) {
            ((ImageView) findViewById(R.id.bookThumbnail)).setImageDrawable(new BitmapDrawable(BitmapFactory.decodeByteArray(book.getImage(), 0, book.getImage().length)));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_book, menu);
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

    public void bookCreation(View view){
        EditText title = (EditText) findViewById(R.id.bookTitle);
        EditText author = (EditText) findViewById(R.id.bookAuthor);
        EditText isbn = (EditText) findViewById(R.id.bookISBN);
        EditText description = (EditText) findViewById(R.id.bookDescription);

        book.setTitle(title.getText().toString());
        book.setAuthor(author.getText().toString());
        book.setIsbn(isbn.getText().toString());
        book.setDescription(description.getText().toString());

        BookLibrary.getInstance().UpdateOrAddBook(book);

        finish();
    }

    public void takePhoto(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void loadISBN(View view){
        EditText isbn = (EditText) findViewById(R.id.bookISBN);
        asyncTask = new GetBookInfo(getApplicationContext()) ; //.execute(ISBN);
        asyncTask.delegate = this;
        asyncTask.execute(isbn.getText().toString());
    }

    @Override
    public void processFinish(Book output){
        this.book = output;
        ((TextView)findViewById(R.id.bookTitle)).setText(book.getTitle());
        ((TextView)findViewById(R.id.bookISBN)).setText(book.getIsbn());
        ((TextView)findViewById(R.id.bookAuthor)).setText(book.getAuthor());
        ((TextView)findViewById(R.id.bookDescription)).setText(book.getDescription());
        if(book.getImage() != null) {
            ((ImageView) findViewById(R.id.bookThumbnail)).setImageDrawable(new BitmapDrawable(BitmapFactory.decodeByteArray(book.getImage(), 0, book.getImage().length)));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

            book.setImage(stream.toByteArray());

            ImageView bookThumbnail = (ImageView) findViewById(R.id.bookThumbnail);
            bookThumbnail.setImageBitmap(imageBitmap);
        }
    }
}
