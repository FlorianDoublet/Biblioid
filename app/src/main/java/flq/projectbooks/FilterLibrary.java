package flq.projectbooks;

import android.widget.Filter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by doublet on 01/10/15.
 */
public class FilterLibrary implements Serializable {

    List<BookFilter> filters;

    public FilterLibrary() {
        filters = new ArrayList<>();
    }

    public void Add(BookFilter filter){
        filters.add(filter);
    }

    public List<BookFilter> getFilters(){
        return filters;
    }

    public BookFilter getNewFilters(){
        return new BookFilter();
    }

    public void DeleteFilters(BookFilter filter){
        filters.remove(filter);
    }
}
