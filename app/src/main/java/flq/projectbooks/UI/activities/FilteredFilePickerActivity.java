package flq.projectbooks.UI.activities;

import android.os.Environment;

import com.nononsenseapps.filepicker.AbstractFilePickerActivity;
import com.nononsenseapps.filepicker.AbstractFilePickerFragment;

import java.io.File;

import flq.projectbooks.UI.fragments.FilteredFilePickerFragment;

/**
 * Created by Lucas on 19/04/2016.
 */
public class FilteredFilePickerActivity extends AbstractFilePickerActivity<File> {

    public FilteredFilePickerActivity() {
        super();
    }

    @Override
    protected AbstractFilePickerFragment<File> getFragment(
            final String startPath, final int mode, final boolean allowMultiple,
            final boolean allowCreateDir) {
        AbstractFilePickerFragment<File> fragment = new FilteredFilePickerFragment();
        // startPath is allowed to be null. In that case, default folder should be SD-card and not "/"
        fragment.setArgs(startPath != null ? startPath : Environment.getExternalStorageDirectory().getPath(),
                mode, allowMultiple, allowCreateDir);
        return fragment;
    }
}
