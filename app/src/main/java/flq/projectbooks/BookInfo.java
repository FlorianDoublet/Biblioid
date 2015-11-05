package flq.projectbooks;

import android.app.Activity;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.Serializable;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BookInfo#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookInfo extends Fragment implements Parcelable {
    public static final String ARG_PARAM1 = "param1";


    private Book book;

    @Override
    public void writeToParcel(Parcel dest, int flags){

    }

    @Override
    public int describeContents(){
        return 0;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @book Book Parameter 1.
     * @return A new instance of fragment BookInfo.
     */
    public static BookInfo newInstance(Book book) {
        BookInfo fragment = new BookInfo();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, book);
        fragment.setArguments(args);
        return fragment;
    }

    public BookInfo() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            book = (Book) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_info, container, false);

        TextView textViewTitle = (TextView) view.findViewById(R.id.bookInfoTitle);
        textViewTitle.setText(book.getTitle());

        TextView textViewAuthor = (TextView) view.findViewById(R.id.bookInfoAuthor);
        textViewAuthor.setText(book.getAuthor());

        TextView textViewDescription = (TextView) view.findViewById(R.id.bookInfoDescription);
        textViewDescription.setText(book.getDescription());

        ImageView imageView = (ImageView) view.findViewById(R.id.bookInfoImage);
        imageView.setImageResource(getResources().getIdentifier(book.getImage(), "drawable", view.getContext().getPackageName()));

        return view ;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
