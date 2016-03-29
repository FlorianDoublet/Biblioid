package flq.projectbooks.UI.activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.util.IOUtils;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.nononsenseapps.filepicker.FilePickerActivity;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

import flq.projectbooks.R;
import flq.projectbooks.UI.fragments.NoticeDialogFragment;
import flq.projectbooks.data.libraries.BookFilterCatalog;
import flq.projectbooks.data.libraries.BookLibrary;
import flq.projectbooks.database.MySQLiteHelper;


public class ImportExport extends AppCompatActivity implements NoticeDialogFragment.NoticeDialogListener {


    public final static int FILE_CODE_IMPORT = 1;
    public final static int FILE_CODE_EXPORT = 2;

    private static final int RC_SIGN_IN = 9001;
    private static final int DRIVE_SIGN_IN = 9002;
    static public DriveId biblioidFolderDriveID = null;
    static public DriveId biblioidFileDriveID = null;
    public GoogleApiClient mGoogleApiClient;
    private MySQLiteHelper db;
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_export);
        setTitle("Import export des données");

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(Drive.SCOPE_APPFOLDER, Drive.SCOPE_FILE)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_APPFOLDER)
                .addScope(Drive.SCOPE_FILE)
                .build();

        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleSignIn(view);
            }
        });

        db = new MySQLiteHelper(this);

        float size = db.getDBSizeInMb();


        DecimalFormat df = new DecimalFormat("##.##");
        df.setRoundingMode(RoundingMode.DOWN);
        ((TextView) findViewById(R.id.infoPoids)).setText("Taille actuel de la base de données : " + df.format(size) + " mo");
    }

    public void googleSignIn(View view) {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void createBackup(View view) {
        if (biblioidFolderDriveID != null) {
            saveDatabaseOnCloud();
        } else {
            createBiblioidFolder();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null && requestCode == FILE_CODE_IMPORT && resultCode == Activity.RESULT_OK) {
            uri = data.getData();

            try {
                db.importDatabase(uri.getPath());
                displayMessage("Données chargées.");
                BookLibrary.getInstance().updateLocalList();
                BookFilterCatalog.getInstance().updateLocalList();
                float size = db.getDBSizeInMb();
                DecimalFormat df = new DecimalFormat("##.##");
                df.setRoundingMode(RoundingMode.DOWN);
                ((TextView) findViewById(R.id.infoPoids)).setText("Taille actuel de la base de données : " + df.format(size) + " mo");

            } catch (IOException e) {
                displayMessage("Erreur, impossible d'importer les données.");
            }

        }

        if (data != null && requestCode == FILE_CODE_EXPORT && resultCode == Activity.RESULT_OK) {
            uri = data.getData();

            DialogFragment newFragment = new NoticeDialogFragment();
            newFragment.show(getFragmentManager(), "NoticeDialogFragment");

        }

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }

        if (requestCode == DRIVE_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleDriveSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();


            mGoogleApiClient.connect(GoogleApiClient.SIGN_IN_MODE_OPTIONAL);

            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, DRIVE_SIGN_IN);
        }
    }

    private void handleDriveSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            ((TextView) findViewById(R.id.cloudStatut)).setText("Connecté");
            getDriveIdIfBiblioidFolderExist(false);
        }
    }

    private void getDriveIdIfBiblioidFolderExist(final boolean saveDBB) {
        Query query = new Query.Builder()
                .addFilter(Filters.and(Filters.eq(SearchableField.TITLE, "Biblioid"), Filters.eq(SearchableField.TRASHED, false)))
                .build();

        Drive.DriveApi.query(mGoogleApiClient, query)
                .setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {

                    @Override
                    public void onResult(DriveApi.MetadataBufferResult result) {
                        // Iterate over the matching Metadata instances in mdResultSet
                        if (result.getStatus().isSuccess() && result.getMetadataBuffer().getCount() > 0 && result.getMetadataBuffer().get(0).isExplicitlyTrashed() == false) {
                            try {
                                Thread.sleep(300);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            biblioidFolderDriveID = result.getMetadataBuffer().get(0).getDriveId();
                            if (saveDBB) {
                                createEmptyBiblioidDatabase();
                            }
                            if (result.getMetadataBuffer().get(0).getFileSize() > 0) {
                                ((TextView) findViewById(R.id.cloudStatut)).setText("Connecté : aucune sauvegarde trouvée.");
                            } else {
                                ((TextView) findViewById(R.id.cloudStatut)).setText("Connecté");

                                findViewById(R.id.saveDatabaseOnCloudButton).setVisibility(View.VISIBLE);
                                findViewById(R.id.readDatabaseOnCloudButton).setVisibility(View.VISIBLE);
                                findViewById(R.id.backupInfoDate).setVisibility(View.VISIBLE);
                                findViewById(R.id.backupInfoPoids).setVisibility(View.VISIBLE);
                                findViewById(R.id.SharingButton).setVisibility(View.VISIBLE);

                                Query query = new Query.Builder()
                                        .addFilter(Filters.and(Filters.eq(SearchableField.TITLE, "books.db"), Filters.eq(SearchableField.TRASHED, false)))
                                        .build();


                                Drive.DriveApi.query(mGoogleApiClient, query)
                                        .setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {

                                            @Override
                                            public void onResult(DriveApi.MetadataBufferResult result) {
                                                if (result.getStatus().isSuccess() && result.getMetadataBuffer().getCount() > 0 && result.getMetadataBuffer().get(0).isExplicitlyTrashed() == false) {
                                                    biblioidFileDriveID = result.getMetadataBuffer().get(0).getDriveId();

                                                    float size = ((float) result.getMetadataBuffer().get(0).getFileSize() / (float) 1024 / (float) 1024);
                                                    DecimalFormat df = new DecimalFormat("##.##");
                                                    df.setRoundingMode(RoundingMode.DOWN);

                                                    ((TextView) findViewById(R.id.backupInfoPoids)).setText("Dernière sauvegarde : " + df.format(size) + " mo");

                                                    long actualSeconds = System.currentTimeMillis() / 1000;
                                                    long lastModifiedSeconds = result.getMetadataBuffer().get(0).getModifiedDate().getTime() / 1000;
                                                    long seconds = actualSeconds - lastModifiedSeconds;
                                                    int day = (int) TimeUnit.SECONDS.toDays(seconds);
                                                    long hours = TimeUnit.SECONDS.toHours(seconds) - (day * 24);
                                                    long minute = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds) * 60);

                                                    String text = "Dernière sauvegarde : Il y a ";
                                                    String instantAgo = "un instant";
                                                    if (day > 0) {
                                                        text += day + " j ";
                                                        instantAgo = "";
                                                    }
                                                    if (hours > 0) {
                                                        text += hours + " h ";
                                                        instantAgo = "";
                                                    }
                                                    if (minute > 0) {
                                                        text += minute + " m ";
                                                        instantAgo = "";
                                                    }
                                                    ((TextView) findViewById(R.id.backupInfoDate)).setText(text + instantAgo);

                                                }
                                            }
                                        });

                            }
                        } else {
                            ((TextView) findViewById(R.id.cloudInfo)).setText("Aucune sauvegarde n'a été trouvée sur le cloud.");
                        }
                        findViewById(R.id.saveDatabaseOnCloudButton).setVisibility(View.VISIBLE);
                    }

                });

        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void readDatabaseOnCloud(View view) {
        Query query = new Query.Builder()
                .addFilter(Filters.and(Filters.eq(SearchableField.TITLE, "books.db"), Filters.eq(SearchableField.TRASHED, false)))
                .build();


        Drive.DriveApi.query(mGoogleApiClient, query)
                .setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {

                    @Override
                    public void onResult(DriveApi.MetadataBufferResult result) {
                        // Iterate over the matching Metadata instances in mdResultSet
                        if (result.getStatus().isSuccess() && result.getMetadataBuffer().getCount() > 0) {
                            DriveFile file = result.getMetadataBuffer().get(0).getDriveId().asDriveFile();
                            file.open(mGoogleApiClient, DriveFile.MODE_READ_ONLY, null)
                                    .setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
                                        @Override
                                        public void onResult(DriveApi.DriveContentsResult result) {
                                            if (result.getStatus().isSuccess()) {
                                                try {
                                                    DriveContents contents = result.getDriveContents();
                                                    byte[] array = IOUtils.toByteArray(contents.getInputStream());
                                                    String filePath = getApplicationContext().getFilesDir().getAbsolutePath() + "/books.db";
                                                    DataOutputStream dataStreamWriter = new DataOutputStream(new FileOutputStream(new File(filePath)));
                                                    dataStreamWriter.write(array);
                                                    dataStreamWriter.close();

                                                    db.importDatabase(filePath);
                                                    BookLibrary.getInstance().updateLocalList();
                                                    BookFilterCatalog.getInstance().updateLocalList();
                                                    File file = new File(filePath);
                                                    file.delete();

                                                    displayMessage("Base de données correctement importée depuis le cloud !");
                                                    DecimalFormat df = new DecimalFormat("##.##");
                                                    df.setRoundingMode(RoundingMode.DOWN);
                                                    ((TextView) findViewById(R.id.infoPoids)).setText("Taille actuel de la base de données : " + df.format(db.getDBSizeInMb()) + " mo");

                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }

                                            }
                                        }
                                    });
                        }
                    }

                });
    }

    private void saveDatabaseOnCloud() {
        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TITLE, "books.db"))
                .addFilter(Filters.eq(SearchableField.TRASHED, false))
                .build();


        Drive.DriveApi.query(mGoogleApiClient, query)
                .setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {

                    @Override
                    public void onResult(DriveApi.MetadataBufferResult result) {
                        // Iterate over the matching Metadata instances in mdResultSet
                        if (result.getStatus().isSuccess() && result.getMetadataBuffer().getCount() > 0) {
                            DriveFile file = result.getMetadataBuffer().get(0).getDriveId().asDriveFile();
                            file.open(mGoogleApiClient, DriveFile.MODE_WRITE_ONLY, null).setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
                                @Override
                                public void onResult(DriveApi.DriveContentsResult result) {
                                    if (result.getStatus().isSuccess()) {
                                        DriveContents driveContents = result.getDriveContents();
                                        try {
                                            ParcelFileDescriptor parcelFileDescriptor = driveContents.getParcelFileDescriptor();
                                            FileInputStream fileInputStream = new FileInputStream(parcelFileDescriptor
                                                    .getFileDescriptor());
                                            // Read to the end of the file.
                                            //fileInputStream.read(new byte[fileInputStream.available()]);

                                            // Append to the file.
                                            FileOutputStream fileOutputStream = new FileOutputStream(parcelFileDescriptor
                                                    .getFileDescriptor());
                                            DataOutputStream writer = new DataOutputStream(fileOutputStream);


                                            db.backupDatabase(getApplicationContext().getFilesDir().getAbsolutePath(), "books.db");
                                            File fi = new File(getApplicationContext().getFilesDir().getAbsolutePath() + "/books.db");
                                            FileInputStream fis = new FileInputStream(fi);
                                            byte[] bytes = new byte[(int) fi.length()];
                                            fis.read(bytes);
                                            writer.write(bytes);

                                            driveContents.commit(mGoogleApiClient, null).setResultCallback(new ResultCallback<Status>() {
                                                @Override
                                                public void onResult(Status result) {
                                                    findViewById(R.id.readDatabaseOnCloudButton).setVisibility(View.VISIBLE);
                                                    findViewById(R.id.backupInfoDate).setVisibility(View.VISIBLE);
                                                    findViewById(R.id.backupInfoPoids).setVisibility(View.VISIBLE);
                                                    findViewById(R.id.SharingButton).setVisibility(View.VISIBLE);
                                                    ((TextView) findViewById(R.id.backupInfoDate)).setText("Dernière sauvegarde : à l'instant");

                                                    DecimalFormat df = new DecimalFormat("##.##");
                                                    df.setRoundingMode(RoundingMode.DOWN);
                                                    ((TextView) findViewById(R.id.backupInfoPoids)).setText("Dernière sauvegarde : " + df.format(db.getDBSizeInMb()) + " mo");

                                                    displayMessage("Base de données sauvegardé sur le cloud !");
                                                    getDriveIdIfBiblioidFolderExist(false);
                                                }
                                            });


                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }
                            });
                        }
                    }

                });
    }

    private void createBiblioidFolder() {
        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                .setTitle("Biblioid").build();
        Drive.DriveApi.getRootFolder(mGoogleApiClient).createFolder(
                mGoogleApiClient, changeSet).setResultCallback(new ResultCallback<DriveFolder.DriveFolderResult>() {
            @Override
            public void onResult(DriveFolder.DriveFolderResult result) {
                if (result.getStatus().isSuccess()) {
                    getDriveIdIfBiblioidFolderExist(true);
                }
            }
        });
    }

    private void createEmptyBiblioidDatabase() {
        Drive.DriveApi.newDriveContents(mGoogleApiClient)
                .setResultCallback(new
                                           ResultCallback<DriveApi.DriveContentsResult>() {
                                               @Override
                                               public void onResult(DriveApi.DriveContentsResult result) {
                                                   if (result.getStatus().isSuccess()) {
                                                       final DriveContents driveContents = result.getDriveContents();

                                                       // Perform I/O off the UI thread.
                                                       new Thread() {
                                                           @Override
                                                           public void run() {
                                                               // write content to DriveContents
                                                               OutputStream outputStream = driveContents.getOutputStream();
                                                               Writer writer = new OutputStreamWriter(outputStream);
                                                               try {
                                                                   writer.write("");
                                                                   writer.close();
                                                               } catch (IOException e) {
                                                                   //ERREUR
                                                               }

                                                               MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                                                       .setTitle("books.db")
                                                                       .setStarred(true).build();

                                                               // create a file on root folder
                                                               Drive.DriveApi.getFolder(mGoogleApiClient, biblioidFolderDriveID)
                                                                       .createFile(mGoogleApiClient, changeSet, driveContents)
                                                                       .setResultCallback(new
                                                                                                  ResultCallback<DriveFolder.DriveFileResult>() {
                                                                                                      @Override
                                                                                                      public void onResult(DriveFolder.DriveFileResult result) {
                                                                                                          //AFFICHER LES DONNEES DU FICHIER.
                                                                                                          ((TextView) findViewById(R.id.cloudInfo)).setText("Vos données ont été sauvegardées !");
                                                                                                          saveDatabaseOnCloud();
                                                                                                      }
                                                                                                  });
                                                           }
                                                       }.start();
                                                   }
                                               }
                                           });
    }

    public void exportDatabase(View view) {
        Intent intent = new Intent(this, FilePickerActivity.class);
        intent.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
        intent.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, true);
        intent.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_DIR);
        intent.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());

        startActivityForResult(intent, FILE_CODE_EXPORT);
    }

    public void importDatabase(View view) {
        Intent i = new Intent(this, FilePickerActivity.class);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
        i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);

        i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());

        startActivityForResult(i, FILE_CODE_IMPORT);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        MySQLiteHelper db = new MySQLiteHelper(this);

        Dialog d = dialog.getDialog();
        EditText editText = (EditText) d.findViewById(R.id.db_name);
        String fileName = editText.getText().toString();

        if (fileName.equals("")) {
            fileName = "books.db";
        } else {
            fileName = fileName + ".db";
        }

        try {
            db.backupDatabase(uri.getPath(), fileName);
            displayMessage("Données sauvegardées.");
        } catch (IOException e) {
            displayMessage("Erreur, impossible d'exporter les données.");
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        dialog.dismiss();
    }

    private void displayMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public void openShareActivity(View view) {
        Intent i = new Intent(this, GoogleDriveRestSharingCode.class);
        startActivity(i);
    }
}
