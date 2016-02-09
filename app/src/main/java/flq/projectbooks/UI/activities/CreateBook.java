package flq.projectbooks.UI.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import flq.projectbooks.R;
import flq.projectbooks.data.Book;
import flq.projectbooks.database.LinkTablesDataSource;
import flq.projectbooks.utilities.GetBookInfo;
import flq.projectbooks.utilities.GetBookInfoAmazonAPI;
import flq.projectbooks.utilities.GetBookInfoGoogleBooksAPI;

public class CreateBook extends ActionBarActivity implements GetBookInfo.AsyncResponse {
    public static final String ARG_PARAM1 = "param1";
    public final static String GIVE_BOOK_BACK = "flq.NEWBOOK";
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static protected List<Integer> bookSourcesLogos;
    static protected List<Book> retrievedBook;
    static byte[] imageBook;
    static byte[] photo;
    static private int indexBookImage = 0;
    static private int indexBookTitle = 0;
    static private int indexBookDescription = 0;
    static private int indexBookAuthor = 0;
    static private int indexBookDatePublication = 0;
    static private int indexBookPublisher = 0;
    static private int indexBookCategory = 0;
    static private int indexBookNbPages = 0;
    protected List<GetBookInfo> bookSources;
    protected boolean isLoadingBookFromSource;
    private Book book;
    private int indexSourceBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isLoadingBookFromSource = false;
        bookSources = new ArrayList<>();

        setContentView(R.layout.activity_create_book);

        if (retrievedBook == null) {
            bookSourcesLogos = new ArrayList<>();
            retrievedBook = new ArrayList<>();
            photo = null;
            ((LinearLayout)findViewById(R.id.topMenuCreateBook)).removeAllViews();

            findViewById(R.id.bookTitleImageButton).setVisibility(View.GONE);
            findViewById(R.id.bookAuthorImageButton).setVisibility(View.GONE);
            findViewById(R.id.bookDescriptionImageButton).setVisibility(View.GONE);
            findViewById(R.id.bookDatePublicationImageButton).setVisibility(View.GONE);
            findViewById(R.id.bookPublisherImageButton).setVisibility(View.GONE);
            findViewById(R.id.bookCategoryImageButton).setVisibility(View.GONE);
            findViewById(R.id.bookNbPagesImageButton).setVisibility(View.GONE);
            findViewById(R.id.bookImageImageButton).setVisibility(View.GONE);

            findViewById(R.id.bookTitleImageButtonLock).setVisibility(View.GONE);
            findViewById(R.id.bookAuthorImageButtonLock).setVisibility(View.GONE);
            findViewById(R.id.bookDescriptionImageButtonLock).setVisibility(View.GONE);
            findViewById(R.id.bookDatePublicationImageButtonLock).setVisibility(View.GONE);
            findViewById(R.id.bookPublisherImageButtonLock).setVisibility(View.GONE);
            findViewById(R.id.bookCategoryImageButtonLock).setVisibility(View.GONE);
            findViewById(R.id.bookNbPagesImageButtonLock).setVisibility(View.GONE);
            findViewById(R.id.bookImageImageButtonLock).setVisibility(View.GONE);
        } else {
            if (retrievedBook.size() != 0) {
                if (indexBookImage >= retrievedBook.size()) {
                    ((ImageView) findViewById(R.id.coverView)).setImageDrawable(new BitmapDrawable(BitmapFactory.decodeByteArray(imageBook, 0, imageBook.length)));
                } else {
                    ((ImageView) findViewById(R.id.coverView)).setImageDrawable(new BitmapDrawable(BitmapFactory.decodeByteArray(retrievedBook.get(indexBookImage).getImage(), 0, retrievedBook.get(indexBookImage).getImage().length)));
                }
            }
            findViewById(R.id.bookTitleImageButton).setVisibility(View.VISIBLE);
            findViewById(R.id.bookAuthorImageButton).setVisibility(View.VISIBLE);
            findViewById(R.id.bookDescriptionImageButton).setVisibility(View.VISIBLE);
            findViewById(R.id.bookDatePublicationImageButton).setVisibility(View.VISIBLE);
            findViewById(R.id.bookPublisherImageButton).setVisibility(View.VISIBLE);
            findViewById(R.id.bookCategoryImageButton).setVisibility(View.VISIBLE);
            findViewById(R.id.bookNbPagesImageButton).setVisibility(View.VISIBLE);
            findViewById(R.id.bookImageImageButton).setVisibility(View.VISIBLE);

            findViewById(R.id.bookTitleImageButtonLock).setVisibility(View.VISIBLE);
            findViewById(R.id.bookAuthorImageButtonLock).setVisibility(View.VISIBLE);
            findViewById(R.id.bookDescriptionImageButtonLock).setVisibility(View.VISIBLE);
            findViewById(R.id.bookDatePublicationImageButtonLock).setVisibility(View.VISIBLE);
            findViewById(R.id.bookPublisherImageButtonLock).setVisibility(View.VISIBLE);
            findViewById(R.id.bookCategoryImageButtonLock).setVisibility(View.VISIBLE);
            findViewById(R.id.bookNbPagesImageButtonLock).setVisibility(View.VISIBLE);
            findViewById(R.id.bookImageImageButtonLock).setVisibility(View.VISIBLE);
        }

        Intent intent = getIntent();
        if (intent.hasExtra(DisplayBooks.GIVE_BOOK)) {
            setTitle("Modification d'un livre");
            book = (Book) intent.getSerializableExtra(DisplayBooks.GIVE_BOOK);
        } else {
            if(intent.hasExtra(Main.GIVE_BOOK_WITH_ISBN)){
                ((EditText) findViewById(R.id.bookISBN)).setText(intent.getStringExtra(Main.GIVE_BOOK_WITH_ISBN));
                book = (Book) intent.getSerializableExtra(Main.ASK_NEW_BOOK);
                loadSource(null);
            }else{
                setTitle("Création d'un livre");
                book = (Book) intent.getSerializableExtra(Main.ASK_NEW_BOOK);
            }
        }

        ((TextView) findViewById(R.id.bookTitle)).setText(book.getTitle());
        ((TextView) findViewById(R.id.bookISBN)).setText(book.getIsbn());

        ((TextView) findViewById(R.id.bookAuthor)).setText(LinkTablesDataSource.authorsToString(book.getAuthors()));

        ((TextView) findViewById(R.id.bookDescription)).setText(book.getDescription());
        ((TextView) findViewById(R.id.bookDatePublication)).setText(book.getDatePublication());
        ((TextView) findViewById(R.id.bookEditeur)).setText(book.getEditor());
        ((TextView) findViewById(R.id.bookCategorie)).setText(LinkTablesDataSource.categoriesToString(book.getCategories()));
        ((TextView) findViewById(R.id.bookNbPages)).setText(String.valueOf(book.getNbPages()));

        initTextViewListener();

        if (book.getImage() != null && retrievedBook.size() != 0 && indexBookImage < retrievedBook.size()) {
            Bitmap imageBitmap = BitmapFactory.decodeByteArray(retrievedBook.get(indexBookImage).getImage(), 0, retrievedBook.get(indexBookImage).getImage().length);
            ((ImageView) findViewById(R.id.coverView)).setImageDrawable(new BitmapDrawable(getResources(), imageBitmap));
        } else {
            if (imageBook != null) {
                Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageBook, 0, imageBook.length);
                ((ImageView) findViewById(R.id.coverView)).setImageDrawable(new BitmapDrawable(getResources(), imageBitmap));
            }
        }

        if(photo == null){
            ((ImageView) findViewById(R.id.coverView)).setImageResource(R.drawable.picturebook);
        }
    }

    public static void resetBookCreation(){
        retrievedBook = null;
        photo = null;
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

    public void bookCreation(View view) {
        EditText title = (EditText) findViewById(R.id.bookTitle);
        EditText author = (EditText) findViewById(R.id.bookAuthor);
        EditText isbn = (EditText) findViewById(R.id.bookISBN);
        EditText description = (EditText) findViewById(R.id.bookDescription);
        EditText datePub = (EditText) findViewById(R.id.bookDatePublication);
        EditText editor = (EditText) findViewById(R.id.bookEditeur);
        EditText category = (EditText) findViewById(R.id.bookCategorie);
        EditText nbPages = (EditText) findViewById(R.id.bookNbPages);

        book.setTitle(title.getText().toString());


        book.setIsbn(isbn.getText().toString());
        book.setDescription(description.getText().toString());
        book.setDatePublication(datePub.getText().toString());
        book.setEditor(editor.getText().toString());
        book.setNbPages(Integer.parseInt(nbPages.getText().toString()));

        //will feed the book with the good authors
        LinkTablesDataSource.feedBookWithAuthor(book, author);
        //will feed the book with the good categories
        LinkTablesDataSource.feedBookWithCategories(book, category);

        finish();
    }

    public void takePhoto(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void seeCover(View view) {
        findViewById(R.id.topMenuCreateBook).setVisibility(View.GONE);
        findViewById(R.id.scrollViewCreateBook).setVisibility(View.GONE);
        findViewById(R.id.validateCreationBookLayout).setVisibility(View.GONE);
    }

    public void hideCover(View view) {
        findViewById(R.id.topMenuCreateBook).setVisibility(View.VISIBLE);
        findViewById(R.id.scrollViewCreateBook).setVisibility(View.VISIBLE);
        findViewById(R.id.validateCreationBookLayout).setVisibility(View.VISIBLE);
    }

    public void loadSource(View view) {
        bookSources.clear();
        bookSourcesLogos.clear();
        retrievedBook.clear();

        indexBookImage = 0;
        indexBookTitle = 0;
        indexBookDescription = 0;
        indexBookAuthor = 0;
        indexBookDatePublication = 0;
        indexBookPublisher = 0;
        indexBookCategory = 0;
        indexBookNbPages = 0;

        //If you want to add a book source, you have to add it here.
        //The code will take charge of anything else.
        bookSources.add(new GetBookInfoAmazonAPI(getApplicationContext()));
        bookSources.add(new GetBookInfoGoogleBooksAPI(getApplicationContext()));

        //You have to put a logo for the source, in the same order of the sources in the bookSources list.
        bookSourcesLogos.add(R.drawable.amazonlogo);
        bookSourcesLogos.add(R.drawable.googlelogo);

        //The last logo, which represent custom data. Keep it at the end of the list.
        bookSourcesLogos.add(R.drawable.booklogo);

        loadFromISBN();
    }

    private void loadFromISBN() {
        isLoadingBookFromSource = true;
        EditText isbn = (EditText) findViewById(R.id.bookISBN);

        if (indexSourceBook < bookSources.size()) {
            bookSources.get(indexSourceBook).delegate = this;
            bookSources.get(indexSourceBook).execute(isbn.getText().toString());
        } else {
            indexSourceBook = 0;
            isLoadingBookFromSource = false;
        }
    }

    //called by methods like "loadISBN" when the process is finished
    @Override
    public void processFinish(Book output) {

        if (output != null) {
            retrievedBook.add(output);

            if (retrievedBook.size() == 1) {

                ImageButton btnSource = new ImageButton(this);
                int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
                btnSource.setLayoutParams(new LinearLayout.LayoutParams(size, size));
                btnSource.setScaleType(ImageView.ScaleType.FIT_XY);
                btnSource.setImageResource(bookSourcesLogos.get(bookSourcesLogos.size() - 1));
                btnSource.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if(findViewById(R.id.bookImageImageButton).isEnabled()) {
                            indexBookImage = bookSourcesLogos.size()-1;
                            ((TextView) findViewById(R.id.bookTitle)).setText(book.getTitle());
                            ((ImageButton) findViewById(R.id.bookTitleImageButton)).setImageResource(bookSourcesLogos.get(indexBookImage));
                        }
                        if(findViewById(R.id.bookPublisherImageButton).isEnabled()) {
                            indexBookPublisher = bookSourcesLogos.size()-1;
                            ((TextView) findViewById(R.id.bookEditeur)).setText(book.getEditor());
                            ((ImageButton) findViewById(R.id.bookPublisherImageButton)).setImageResource(bookSourcesLogos.get(indexBookPublisher));
                        }
                        if(findViewById(R.id.bookDatePublicationImageButton).isEnabled()) {
                            indexBookDatePublication = bookSourcesLogos.size()-1;
                            ((TextView) findViewById(R.id.bookDatePublication)).setText(book.getDatePublication());
                            ((ImageButton) findViewById(R.id.bookDatePublicationImageButton)).setImageResource(bookSourcesLogos.get(indexBookDatePublication));
                        }
                        if(findViewById(R.id.bookNbPagesImageButton).isEnabled()) {
                            indexBookNbPages = bookSourcesLogos.size()-1;
                            ((TextView) findViewById(R.id.bookNbPages)).setText(String.valueOf(book.getNbPages()));
                            ((ImageButton) findViewById(R.id.bookNbPagesImageButton)).setImageResource(bookSourcesLogos.get(indexBookNbPages));
                        }
                        if(findViewById(R.id.bookTitleImageButton).isEnabled()) {
                            indexBookTitle = bookSourcesLogos.size()-1;
                            ((TextView) findViewById(R.id.bookTitle)).setText(book.getTitle());
                            ((ImageButton) findViewById(R.id.bookTitleImageButton)).setImageResource(bookSourcesLogos.get(indexBookTitle));
                        }
                        if(findViewById(R.id.bookAuthorImageButton).isEnabled()) {
                            indexBookAuthor = bookSourcesLogos.size()-1;
                            ((TextView) findViewById(R.id.bookAuthor)).setText(LinkTablesDataSource.authorsToString(book.getAuthors()));
                            ((ImageButton) findViewById(R.id.bookAuthorImageButton)).setImageResource(bookSourcesLogos.get(indexBookAuthor));

                        }
                        if(findViewById(R.id.bookCategoryImageButton).isEnabled()) {
                            indexBookCategory = bookSourcesLogos.size()-1;
                            ((TextView) findViewById(R.id.bookCategorie)).setText(LinkTablesDataSource.categoriesToString(book.getCategories()));
                            ((ImageButton) findViewById(R.id.bookCategoryImageButton)).setImageResource(bookSourcesLogos.get(indexBookCategory));
                        }
                        if(findViewById(R.id.bookDescriptionImageButton).isEnabled()) {
                            indexBookDescription = bookSourcesLogos.size()-1;
                            ((TextView) findViewById(R.id.bookDescription)).setText(book.getDescription());
                            ((ImageButton) findViewById(R.id.bookDescriptionImageButton)).setImageResource(bookSourcesLogos.get(indexBookDescription));
                        }
                    }
                });
                ((LinearLayout) findViewById(R.id.topMenuCreateBook)).addView(btnSource);

                ((TextView) findViewById(R.id.bookTitle)).setText(output.getTitle());
                ((TextView) findViewById(R.id.bookISBN)).setText(output.getIsbn());
                ((TextView) findViewById(R.id.bookAuthor)).setText(LinkTablesDataSource.authorsToString(output.getAuthors()));
                ((TextView) findViewById(R.id.bookDescription)).setText(output.getDescription());
                ((TextView) findViewById(R.id.bookDatePublication)).setText(output.getDatePublication());
                ((TextView) findViewById(R.id.bookEditeur)).setText(output.getEditor());
                ((TextView) findViewById(R.id.bookCategorie)).setText(LinkTablesDataSource.categoriesToString(output.getCategories()));
                ((TextView) findViewById(R.id.bookNbPages)).setText(String.valueOf(output.getNbPages()));

                if (output.getImage() != null) {
                    ((ImageView) findViewById(R.id.coverView)).setImageDrawable(new BitmapDrawable(BitmapFactory.decodeByteArray(output.getImage(), 0, output.getImage().length)));
                    book.setImage(output.getImage());
                    ((ImageButton) findViewById(R.id.bookImageImageButton)).setImageResource(bookSourcesLogos.get(indexBookImage));
                }
                findViewById(R.id.bookTitleImageButton).setVisibility(View.VISIBLE);
                findViewById(R.id.bookAuthorImageButton).setVisibility(View.VISIBLE);
                findViewById(R.id.bookDescriptionImageButton).setVisibility(View.VISIBLE);
                findViewById(R.id.bookDatePublicationImageButton).setVisibility(View.VISIBLE);
                findViewById(R.id.bookPublisherImageButton).setVisibility(View.VISIBLE);
                findViewById(R.id.bookCategoryImageButton).setVisibility(View.VISIBLE);
                findViewById(R.id.bookNbPagesImageButton).setVisibility(View.VISIBLE);
                findViewById(R.id.bookImageImageButton).setVisibility(View.VISIBLE);

                findViewById(R.id.bookTitleImageButtonLock).setVisibility(View.VISIBLE);
                findViewById(R.id.bookAuthorImageButtonLock).setVisibility(View.VISIBLE);
                findViewById(R.id.bookDescriptionImageButtonLock).setVisibility(View.VISIBLE);
                findViewById(R.id.bookDatePublicationImageButtonLock).setVisibility(View.VISIBLE);
                findViewById(R.id.bookPublisherImageButtonLock).setVisibility(View.VISIBLE);
                findViewById(R.id.bookCategoryImageButtonLock).setVisibility(View.VISIBLE);
                findViewById(R.id.bookNbPagesImageButtonLock).setVisibility(View.VISIBLE);
                findViewById(R.id.bookImageImageButtonLock).setVisibility(View.VISIBLE);
            }
            final int index = indexSourceBook;
            ImageButton btnSource = new ImageButton(this);
            int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
            btnSource.setLayoutParams(new LinearLayout.LayoutParams(size, size));
            btnSource.setScaleType(ImageView.ScaleType.FIT_XY);
            btnSource.setImageResource(bookSourcesLogos.get(indexSourceBook));
            btnSource.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    while (indexBookImage != index && findViewById(R.id.bookImageImageButton).isEnabled()) {
                        changeImageSource(null);
                    }
                    while (indexBookPublisher != index && findViewById(R.id.bookPublisherImageButton).isEnabled()) {
                        changePublisherSource(null);
                    }
                    while (indexBookDatePublication != index && findViewById(R.id.bookDatePublicationImageButton).isEnabled()) {
                        changeDatePublicationSource(null);
                    }
                    while (indexBookNbPages != index && findViewById(R.id.bookNbPagesImageButton).isEnabled()) {
                        changeNbPagesSource(null);
                    }
                    while (indexBookTitle != index && findViewById(R.id.bookTitleImageButton).isEnabled()) {
                        changeTitleSource(null);
                    }
                    while (indexBookAuthor != index && findViewById(R.id.bookAuthorImageButton).isEnabled()) {
                        changeAuthorSource(null);
                    }
                    while (indexBookCategory != index && findViewById(R.id.bookCategoryImageButton).isEnabled()) {
                        changeCategorySource(null);
                    }
                    while (indexBookDescription != index && findViewById(R.id.bookDescriptionImageButton).isEnabled()) {
                        changeDescriptionSource(null);
                    }
                }
            });
            ((LinearLayout) findViewById(R.id.topMenuCreateBook)).addView(btnSource);
        }
        indexSourceBook++;
        loadFromISBN();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

            book.setImage(stream.toByteArray());
            photo = stream.toByteArray();
            imageBook = stream.toByteArray();

            ((ImageView) findViewById(R.id.coverView)).setImageDrawable(new BitmapDrawable(getResources(), imageBitmap));

            if (bookSourcesLogos.size() != 0) {
                indexBookImage = bookSourcesLogos.size() - 1;
                ((ImageButton) findViewById(R.id.bookImageImageButton)).setImageResource(bookSourcesLogos.get(indexBookImage));
            }
        }
    }

    public void changeImageSource(View view) {
        indexBookImage++;
        if (indexBookImage > retrievedBook.size()) {
            indexBookImage = 0;
        }
        if(indexBookImage != retrievedBook.size()) {
            Bitmap imageBitmap = BitmapFactory.decodeByteArray(retrievedBook.get(indexBookImage).getImage(), 0, retrievedBook.get(indexBookImage).getImage().length);
            book.setImage(retrievedBook.get(indexBookImage).getImage());
            ((ImageView) findViewById(R.id.coverView)).setImageDrawable(new BitmapDrawable(getResources(), imageBitmap));
            ((ImageButton) findViewById(R.id.bookImageImageButton)).setImageResource(bookSourcesLogos.get(indexBookImage));
        }else{
            if(photo != null){
                book.setImage(photo);
                Bitmap imageBitmap = BitmapFactory.decodeByteArray(photo, 0, photo.length);
                ((ImageView) findViewById(R.id.coverView)).setImageDrawable(new BitmapDrawable(getResources(), imageBitmap));
            }else{
                ((ImageView) findViewById(R.id.coverView)).setImageResource(R.drawable.picturebook);
            }


            ((ImageButton) findViewById(R.id.bookImageImageButton)).setImageResource(bookSourcesLogos.get(indexBookImage));
        }
    }

    public void changeTitleSource(View view) {
        indexBookTitle++;
        if (indexBookTitle > retrievedBook.size()) {
            indexBookTitle = 0;
        }
        if(indexBookTitle != retrievedBook.size()) {
            ((TextView) findViewById(R.id.bookTitle)).setText(retrievedBook.get(indexBookTitle).getTitle());
            ((ImageButton) findViewById(R.id.bookTitleImageButton)).setImageResource(bookSourcesLogos.get(indexBookTitle));
        }else {
            ((TextView) findViewById(R.id.bookTitle)).setText(book.getTitle());
            ((ImageButton) findViewById(R.id.bookTitleImageButton)).setImageResource(bookSourcesLogos.get(indexBookTitle));
        }
    }

    public void changeDescriptionSource(View view) {
        indexBookDescription++;
        if (indexBookDescription > retrievedBook.size()) {
            indexBookDescription = 0;
        }
        if(indexBookDescription != retrievedBook.size()) {
            ((TextView) findViewById(R.id.bookDescription)).setText(retrievedBook.get(indexBookDescription).getDescription());
            ((ImageButton) findViewById(R.id.bookDescriptionImageButton)).setImageResource(bookSourcesLogos.get(indexBookDescription));
        }else {
            ((TextView) findViewById(R.id.bookDescription)).setText(book.getDescription());
            ((ImageButton) findViewById(R.id.bookDescriptionImageButton)).setImageResource(bookSourcesLogos.get(indexBookDescription));
        }
    }

    public void changeAuthorSource(View view) {
        indexBookAuthor++;
        if (indexBookAuthor > retrievedBook.size()) {
            indexBookAuthor = 0;
        }
        if(indexBookAuthor != retrievedBook.size()) {
            ((TextView) findViewById(R.id.bookAuthor)).setText(LinkTablesDataSource.authorsToString(retrievedBook.get(indexBookAuthor).getAuthors()));
            ((ImageButton) findViewById(R.id.bookAuthorImageButton)).setImageResource(bookSourcesLogos.get(indexBookAuthor));
        }else {
            ((TextView) findViewById(R.id.bookAuthor)).setText(LinkTablesDataSource.authorsToString(book.getAuthors()));
            ((ImageButton) findViewById(R.id.bookAuthorImageButton)).setImageResource(bookSourcesLogos.get(indexBookAuthor));
        }
    }

    public void changeDatePublicationSource(View view) {
        indexBookDatePublication++;
        if (indexBookDatePublication > retrievedBook.size()) {
            indexBookDatePublication = 0;
        }
        if(indexBookDatePublication != retrievedBook.size()) {
            ((TextView) findViewById(R.id.bookDatePublication)).setText(retrievedBook.get(indexBookDatePublication).getDatePublication());
            ((ImageButton) findViewById(R.id.bookDatePublicationImageButton)).setImageResource(bookSourcesLogos.get(indexBookDatePublication));
        }else {
            ((TextView) findViewById(R.id.bookDatePublication)).setText(book.getDatePublication());
            ((ImageButton) findViewById(R.id.bookDatePublicationImageButton)).setImageResource(bookSourcesLogos.get(indexBookDatePublication));
        }
    }

    public void changePublisherSource(View view) {
        indexBookPublisher++;
        if (indexBookPublisher > retrievedBook.size()) {
            indexBookPublisher = 0;
        }
        if(indexBookPublisher != retrievedBook.size()) {
            ((TextView) findViewById(R.id.bookEditeur)).setText(retrievedBook.get(indexBookPublisher).getEditor());
            ((ImageButton) findViewById(R.id.bookPublisherImageButton)).setImageResource(bookSourcesLogos.get(indexBookPublisher));
        }else {
            ((TextView) findViewById(R.id.bookEditeur)).setText(book.getEditor());
            ((ImageButton) findViewById(R.id.bookPublisherImageButton)).setImageResource(bookSourcesLogos.get(indexBookPublisher));
        }
    }

    public void changeCategorySource(View view) {
        indexBookCategory++;
        if (indexBookCategory > retrievedBook.size()) {
            indexBookCategory = 0;
        }
        if(indexBookCategory != retrievedBook.size()) {
            ((TextView) findViewById(R.id.bookCategorie)).setText(LinkTablesDataSource.authorsToString(retrievedBook.get(indexBookAuthor).getAuthors()));
            ((ImageButton) findViewById(R.id.bookCategoryImageButton)).setImageResource(bookSourcesLogos.get(indexBookCategory));
        }else {
            ((TextView) findViewById(R.id.bookCategorie)).setText(LinkTablesDataSource.categoriesToString(book.getCategories()));
            ((ImageButton) findViewById(R.id.bookCategoryImageButton)).setImageResource(bookSourcesLogos.get(indexBookCategory));
        }
    }

    public void changeNbPagesSource(View view) {
        indexBookNbPages++;
        if (indexBookNbPages > retrievedBook.size()) {
            indexBookNbPages = 0;
        }
        if(indexBookNbPages != retrievedBook.size()) {
            ((TextView) findViewById(R.id.bookNbPages)).setText(String.valueOf(retrievedBook.get(indexBookNbPages).getNbPages()));
            ((ImageButton) findViewById(R.id.bookNbPagesImageButton)).setImageResource(bookSourcesLogos.get(indexBookNbPages));
        }else{
            ((TextView) findViewById(R.id.bookNbPages)).setText(String.valueOf(book.getNbPages()));
            ((ImageButton) findViewById(R.id.bookNbPagesImageButton)).setImageResource(bookSourcesLogos.get(indexBookNbPages));
        }
    }

    public void lockImageSource(View view) {
        View button = findViewById(R.id.bookImageImageButton);
        button.setEnabled(!button.isEnabled());
        findViewById(R.id.bookImage).setEnabled(findViewById(R.id.bookImage).isEnabled());
        if (button.isEnabled()) {
            ((ImageButton) findViewById(R.id.bookImageImageButtonLock)).setImageResource(R.drawable.unlock);
        } else {
            ((ImageButton) findViewById(R.id.bookImageImageButtonLock)).setImageResource(R.drawable.lock);
        }
    }

    public void lockAuthorSource(View view) {
        View button = findViewById(R.id.bookAuthorImageButton);
        button.setEnabled(!button.isEnabled());
        findViewById(R.id.bookAuthor).setEnabled(findViewById(R.id.bookAuthor).isEnabled());
        if (button.isEnabled()) {
            ((ImageButton) findViewById(R.id.bookAuthorImageButtonLock)).setImageResource(R.drawable.unlock);
        } else {
            ((ImageButton) findViewById(R.id.bookAuthorImageButtonLock)).setImageResource(R.drawable.lock);
        }
    }

    public void lockDescriptionSource(View view) {
        View button = findViewById(R.id.bookDescriptionImageButton);
        button.setEnabled(!button.isEnabled());
        findViewById(R.id.bookDescription).setEnabled(findViewById(R.id.bookDescription).isEnabled());
        if (button.isEnabled()) {
            ((ImageButton) findViewById(R.id.bookDescriptionImageButtonLock)).setImageResource(R.drawable.unlock);
        } else {
            ((ImageButton) findViewById(R.id.bookDescriptionImageButtonLock)).setImageResource(R.drawable.lock);
        }
    }

    public void lockTitleSource(View view) {
        View button = findViewById(R.id.bookTitleImageButton);
        button.setEnabled(!button.isEnabled());
        findViewById(R.id.bookTitle).setEnabled(findViewById(R.id.bookTitle).isEnabled());
        if (button.isEnabled()) {
            ((ImageButton) findViewById(R.id.bookTitleImageButtonLock)).setImageResource(R.drawable.unlock);
        } else {
            ((ImageButton) findViewById(R.id.bookTitleImageButtonLock)).setImageResource(R.drawable.lock);
        }
    }

    public void lockDatePublicationSource(View view) {
        View button = findViewById(R.id.bookDatePublicationImageButton);
        button.setEnabled(!button.isEnabled());
        findViewById(R.id.bookDatePublication).setEnabled(findViewById(R.id.bookDatePublication).isEnabled());
        if (button.isEnabled()) {
            ((ImageButton) findViewById(R.id.bookDatePublicationImageButtonLock)).setImageResource(R.drawable.unlock);
        } else {
            ((ImageButton) findViewById(R.id.bookDatePublicationImageButtonLock)).setImageResource(R.drawable.lock);
        }
    }

    public void lockPublisherSource(View view) {
        View button = findViewById(R.id.bookPublisherImageButton);
        button.setEnabled(!button.isEnabled());
        findViewById(R.id.bookEditeur).setEnabled(findViewById(R.id.bookEditeur).isEnabled());
        if (button.isEnabled()) {
            ((ImageButton) findViewById(R.id.bookPublisherImageButtonLock)).setImageResource(R.drawable.unlock);
        } else {
            ((ImageButton) findViewById(R.id.bookPublisherImageButtonLock)).setImageResource(R.drawable.lock);
        }
    }

    public void lockCategorySource(View view) {
        View button = findViewById(R.id.bookCategoryImageButton);
        button.setEnabled(!button.isEnabled());
        findViewById(R.id.bookCategorie).setEnabled(findViewById(R.id.bookCategorie).isEnabled());
        if (button.isEnabled()) {
            ((ImageButton) findViewById(R.id.bookCategoryImageButtonLock)).setImageResource(R.drawable.unlock);
        } else {
            ((ImageButton) findViewById(R.id.bookCategoryImageButtonLock)).setImageResource(R.drawable.lock);
        }
    }

    public void lockNbPagesSource(View view) {
        View button = findViewById(R.id.bookNbPagesImageButton);
        button.setEnabled(!button.isEnabled());
        findViewById(R.id.bookNbPages).setEnabled(findViewById(R.id.bookNbPages).isEnabled());
        if (button.isEnabled()) {
            ((ImageButton) findViewById(R.id.bookNbPagesImageButtonLock)).setImageResource(R.drawable.unlock);
        } else {
            ((ImageButton) findViewById(R.id.bookNbPagesImageButtonLock)).setImageResource(R.drawable.lock);
        }
    }

    private void initTextViewListener() {
        ((TextView) findViewById(R.id.bookTitle)).addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = ((EditText) findViewById(R.id.bookTitle)).getText().toString();
                boolean equal = false;
                for (int i = 0; i < retrievedBook.size(); i++) {
                    if (text.equals(retrievedBook.get(i).getTitle())) {
                        equal = true;
                        ((ImageButton) findViewById(R.id.bookTitleImageButton)).setImageResource(bookSourcesLogos.get(i));
                    }
                }
                if (!equal && bookSourcesLogos.size() != 0) {
                    indexBookTitle = bookSourcesLogos.size() - 1;
                    ((ImageButton) findViewById(R.id.bookTitleImageButton)).setImageResource(bookSourcesLogos.get(indexBookTitle));
                    book.setTitle(text);
                }
            }
        });

        ((TextView) findViewById(R.id.bookDescription)).addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = ((EditText) findViewById(R.id.bookDescription)).getText().toString();
                boolean equal = false;
                for (int i = 0; i < retrievedBook.size(); i++) {
                    if (text.equals(retrievedBook.get(i).getDescription())) {
                        equal = true;
                        ((ImageButton) findViewById(R.id.bookDescriptionImageButton)).setImageResource(bookSourcesLogos.get(i));
                    }
                }
                if (!equal && bookSourcesLogos.size() != 0) {
                    indexBookDescription = bookSourcesLogos.size() - 1;
                    ((ImageButton) findViewById(R.id.bookDescriptionImageButton)).setImageResource(bookSourcesLogos.get(indexBookDescription));
                    book.setDescription(text);
                }
            }
        });

        ((TextView) findViewById(R.id.bookAuthor)).addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = ((EditText) findViewById(R.id.bookAuthor)).getText().toString();
                boolean equal = false;
                for (int i = 0; i < retrievedBook.size(); i++) {
                    if (text.equals(LinkTablesDataSource.authorsToString(retrievedBook.get(i).getAuthors()))) {
                        equal = true;
                        ((ImageButton) findViewById(R.id.bookAuthorImageButton)).setImageResource(bookSourcesLogos.get(i));
                    }
                }
                if (!equal && bookSourcesLogos.size() != 0) {
                    indexBookAuthor = bookSourcesLogos.size() - 1;
                    ((ImageButton) findViewById(R.id.bookAuthorImageButton)).setImageResource(bookSourcesLogos.get(indexBookAuthor));
                    book.setAuthors(LinkTablesDataSource.getAuthorsFromEditText((EditText) findViewById(R.id.bookAuthor)));
                }
            }
        });

        ((TextView) findViewById(R.id.bookNbPages)).addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = ((EditText) findViewById(R.id.bookNbPages)).getText().toString();
                boolean equal = false;
                for (int i = 0; i < retrievedBook.size(); i++) {
                    if (text.equals((String.valueOf(retrievedBook.get(i).getNbPages())))) {
                        equal = true;
                        ((ImageButton) findViewById(R.id.bookNbPagesImageButton)).setImageResource(bookSourcesLogos.get(i));
                    }
                }
                if (!equal && bookSourcesLogos.size() != 0) {
                    indexBookNbPages = bookSourcesLogos.size() - 1;
                    ((ImageButton) findViewById(R.id.bookNbPagesImageButton)).setImageResource(bookSourcesLogos.get(indexBookNbPages));
                    book.setNbPages(Integer.parseInt(text));
                }
            }
        });

        ((TextView) findViewById(R.id.bookCategorie)).addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = ((EditText) findViewById(R.id.bookCategorie)).getText().toString();
                boolean equal = false;
                for (int i = 0; i < retrievedBook.size(); i++) {
                    if (text.equals(LinkTablesDataSource.categoriesToString(retrievedBook.get(i).getCategories()))) {
                        equal = true;
                        ((ImageButton) findViewById(R.id.bookCategoryImageButton)).setImageResource(bookSourcesLogos.get(i));
                    }
                }
                if (!equal && bookSourcesLogos.size() != 0) {
                    indexBookCategory = bookSourcesLogos.size() - 1;
                    ((ImageButton) findViewById(R.id.bookCategoryImageButton)).setImageResource(bookSourcesLogos.get(indexBookCategory));
                    book.setCategories(LinkTablesDataSource.getCategoriesFromString(text));
                }
            }
        });

        ((TextView) findViewById(R.id.bookDatePublication)).addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = ((EditText) findViewById(R.id.bookDatePublication)).getText().toString();
                boolean equal = false;
                for (int i = 0; i < retrievedBook.size(); i++) {
                    if (text.equals(retrievedBook.get(i).getDatePublication())) {
                        equal = true;
                        ((ImageButton) findViewById(R.id.bookDatePublicationImageButton)).setImageResource(bookSourcesLogos.get(i));
                    }
                }
                if (!equal && bookSourcesLogos.size() != 0) {
                    indexBookDatePublication = bookSourcesLogos.size() - 1;
                    ((ImageButton) findViewById(R.id.bookDatePublicationImageButton)).setImageResource(bookSourcesLogos.get(indexBookDatePublication));
                    book.setDatePublication(text);
                }
            }
        });

        ((TextView) findViewById(R.id.bookEditeur)).addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = ((EditText) findViewById(R.id.bookEditeur)).getText().toString();
                boolean equal = false;
                for (int i = 0; i < retrievedBook.size(); i++) {
                    if (text.equals(retrievedBook.get(i).getEditor())) {
                        equal = true;
                        ((ImageButton) findViewById(R.id.bookPublisherImageButton)).setImageResource(bookSourcesLogos.get(i));
                    }
                }
                if (!equal && bookSourcesLogos.size() != 0) {
                    indexBookPublisher = bookSourcesLogos.size() - 1;
                    ((ImageButton) findViewById(R.id.bookPublisherImageButton)).setImageResource(bookSourcesLogos.get(indexBookPublisher));
                    book.setEditor(text);
                }
            }
        });
    }
}

