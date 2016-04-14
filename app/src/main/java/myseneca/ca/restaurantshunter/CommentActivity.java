package myseneca.ca.restaurantshunter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class CommentActivity extends AppCompatActivity {

    public static final String mPhotosDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            + "/" + MainActivity.RH_PHOTO_FOLDER;

    private static final int REQUEST_WRITE_STORAGE = 112;
    private static final int REQUEST_CAMERA = 115;

    private Restaurant mRestaurant;
    private boolean mFinishActivity;
    private ImageButton mImageButtonD;
    private ImageButton mImageButton1;
    private ImageButton mImageButton2;
    private ImageButton mImageButton3;
    private ImageButton mImageButton4;
    private ImageButton mImageButton5;
    private ImageButton mImageButton6;
    private ImageButton mImageButton7;
    private ImageButton mImageButton8;
    private int mChoseImgbtnNum;
    private String mPreFileName;
    private String mTakePhotoPath;
    private ArrayList<String> mPhotoFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        //Ref: http://sourcey.com/android-custom-centered-actionbar-with-material-design/
        //Add custom toolbar which has home button
        Toolbar toolbar = (Toolbar) findViewById(R.id.comm_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mRestaurant = (Restaurant) getIntent().getSerializableExtra("RestaurantObj");

        mFinishActivity = ((int) getIntent().getSerializableExtra("RequestFrom") == MainActivity.COMM_FROM_LIST) ? true : false;
        TextView tvName = (TextView)findViewById(R.id.CommResName);
        tvName.setText(mRestaurant.getName());

        RatingBar ratingBar = (RatingBar)findViewById(R.id.comm_rates);
        ratingBar.setRating(mRestaurant.getRate());

        EditText commContent = (EditText)findViewById(R.id.comm_content);
        commContent.setText(mRestaurant.getComment(), TextView.BufferType.EDITABLE);

        mPhotoFiles = Restaurant.transToImageStringArray(mRestaurant.getImages());

        addListenerOnButton();
        loadBtnsThumbs();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {

            RatingBar ratingBar = (RatingBar)findViewById(R.id.comm_rates);
            EditText commContent = (EditText)findViewById(R.id.comm_content);

            mRestaurant.setRate((int) ratingBar.getRating());
            mRestaurant.setComment(commContent.getText().toString());
            mRestaurant.setImages(Restaurant.convertImageArrayToString(mPhotoFiles));

            MainActivity.mDb.updateRestaurantComment(mRestaurant);

            if (mFinishActivity) {
                Intent resListIntent= new Intent(this, RestaurantListActivity.class);
                startActivity(resListIntent);
                finish();
            } else {
                Intent intent = new Intent();
                intent.putExtra("UpdateMarker_RestaurantObj", mRestaurant);
                intent.setAction("myseneca.ca.restaurantshunter.updatMarker");
                sendBroadcast(intent);
            }
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public void addListenerOnButton() {

        mChoseImgbtnNum = 0;

        mImageButtonD = (ImageButton) findViewById(R.id.comm_ResPhoto);
        mImageButtonD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mChoseImgbtnNum = 1;
                selectImage();
            }
        });

        mImageButton1 = (ImageButton) findViewById(R.id.photo1);
        mImageButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mChoseImgbtnNum = 1;
                selectImage();
            }
        });
        mImageButton2 = (ImageButton) findViewById(R.id.photo2);
        mImageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mChoseImgbtnNum = 2;
                selectImage();
            }
        });
        mImageButton3 = (ImageButton) findViewById(R.id.photo3);
        mImageButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mChoseImgbtnNum = 3;
                selectImage();
            }
        });
        mImageButton4 = (ImageButton) findViewById(R.id.photo4);
        mImageButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mChoseImgbtnNum = 4;
                selectImage();
            }
        });
        mImageButton5 = (ImageButton) findViewById(R.id.photo5);
        mImageButton5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mChoseImgbtnNum = 5;
                selectImage();
            }
        });
        mImageButton6 = (ImageButton) findViewById(R.id.photo6);
        mImageButton6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mChoseImgbtnNum = 6;
                selectImage();
            }
        });
        mImageButton7 = (ImageButton) findViewById(R.id.photo7);
        mImageButton7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mChoseImgbtnNum = 7;
                selectImage();
            }
        });
        mImageButton8 = (ImageButton) findViewById(R.id.photo8);
        mImageButton8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mChoseImgbtnNum = 8;
                selectImage();
            }
        });
    }

    //Ref: http://www.theappguruz.com/blog/android-take-photo-camera-gallery-code-sample
    private void selectImage() {

        final CharSequence[] items = { "Take Photo", "Choose from Library", "Cancel" };
        mPreFileName = "RH_" + mRestaurant.getId() + "_" + String.valueOf(mChoseImgbtnNum);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString (R.string.dlg_title_add_photo));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) && checkSavePhotoPermission()) {
                    //http://developer.android.com/training/camera/photobasics.html
                    if (items[item].equals("Take Photo")) {
                        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) && checkCameraPermission()) {
                            //Open Camera
                            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(createPhotoFileInAlbumUri(mPreFileName)));
                            startActivityForResult(cameraIntent, MainActivity.PHOTO_REQUEST_CAMERA);
                        }

                    } else if (items[item].equals("Choose from Library")) {
                        //Open Photo Album
                        Intent intent = new Intent(
                                Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                        intent.setType("image/*");
                        startActivityForResult(Intent.createChooser(intent, "Select File"), MainActivity.PHOTO_SELECT_FILE);

                    } else if (items[item].equals("Cancel")) {
                        dialog.dismiss();
                    }
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == MainActivity.PHOTO_REQUEST_CAMERA) {
                //Ref: http://blog-emildesign.rhcloud.com/?p=590
                //Get our saved file into a bitmap object:
                Bitmap bitmap = decodeSampledBitmapFromFile(mTakePhotoPath);
                assignThumb(bitmap);

            } else if (requestCode == MainActivity.PHOTO_SELECT_FILE) {
                Uri selectedImageUri = data.getData();
                String[] projection = {MediaStore.MediaColumns.DATA};
                CursorLoader cursorLoader = new CursorLoader(this, selectedImageUri, projection, null, null, null);
                Cursor cursor = cursorLoader.loadInBackground();
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();

                String selectedImagePath = cursor.getString(column_index);

                try {
                    copyFile(selectedImagePath, createPhotoFileInAlbumUri(mPreFileName).getAbsolutePath());
                }catch (IOException e) {
                    e.printStackTrace();
                }

                Bitmap bitmap = decodeSampledBitmapFromFile(mTakePhotoPath);

                assignThumb(bitmap);
            }
            insertPhotoToArray(mPreFileName, mPhotoFiles);
        }
    }

    private File createPhotoFileInAlbumUri(String preFileName) {

        File direct = new File(mPhotosDir);
        direct.mkdirs();

        mTakePhotoPath = mPhotosDir + "/" +preFileName + ".jpg";
        File newfile = new File(mTakePhotoPath);
        try {
            newfile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return newfile;
    }

    //Ref: http://www.theappguruz.com/blog/android-take-photo-camera-gallery-code-sample
    public static Bitmap decodeSampledBitmapFromFile(String path)
    {
        Bitmap bm;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        final int REQUIRED_SIZE = 200;
        int scale = 1;
        while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                && options.outHeight / scale / 2 >= REQUIRED_SIZE)
            scale *= 2;
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeFile(path, options);
        return bm;
    }

    private void assignThumb(Bitmap bitmap) {
        switch (mChoseImgbtnNum) {
            case 1: {
                mImageButtonD.setImageBitmap(bitmap);
                mImageButton1.setImageBitmap(bitmap);
                break;
            }
            case 2:
                mImageButton2.setImageBitmap(bitmap);
                break;
            case 3:
                mImageButton3.setImageBitmap(bitmap);
                break;
            case 4:
                mImageButton4.setImageBitmap(bitmap);
                break;
            case 5:
                mImageButton5.setImageBitmap(bitmap);
                break;
            case 6:
                mImageButton6.setImageBitmap(bitmap);
                break;
            case 7:
                mImageButton7.setImageBitmap(bitmap);
                break;
            case 8:
                mImageButton8.setImageBitmap(bitmap);
                break;
        }
    }

    public void copyFile(String selectedImagePath, String string) throws IOException {

        File newfile = new File(string);
        if (newfile.exists())
            newfile.delete();

        InputStream in = new FileInputStream(selectedImagePath);
        OutputStream out = new FileOutputStream(string);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private ArrayList<String> insertPhotoToArray(String photo, ArrayList<String> photoArray) {

        boolean hasDuplicated = false;
        if (photoArray != null) {
            for (String s : photoArray) {
                if (s.equals(photo)) {
                    hasDuplicated = true;
                    break;
                }
            }
        }
        if (!hasDuplicated)
            photoArray.add(photo);

        return photoArray;
    }

    private String getPhotoFileName(int btnID) {
        String photoFileName = "";
        if (mPhotoFiles != null && mPhotoFiles.size() > 0) {
            for (String s : mPhotoFiles) {
                String[] strSplit = s.split("_");
                if (strSplit.length > 0 && strSplit[strSplit.length-1].equals(String.valueOf(btnID)))
                {
                    photoFileName = s;
                    break;
                }
            }
        }
        return photoFileName;
    }

    private void loadBtnsThumbs() {
        for (int i = 1; i < 9; i++) {
            mChoseImgbtnNum = i;
            String file = getPhotoFileName(mChoseImgbtnNum);
            if (!file.equals("")) {
                Bitmap bm = decodeSampledBitmapFromFile(mPhotosDir + "/" + file + ".jpg");
                if (bm!=null)
                    assignThumb(bm);
            }
        }
        mChoseImgbtnNum = 0;
    }

    private boolean checkCameraPermission(){
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.CAMERA)) {

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.CAMERA},
                        REQUEST_CAMERA);

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.CAMERA},
                        REQUEST_CAMERA);
            }
            return false;
        } else {
            return true;
        }
    }

    private boolean checkSavePhotoPermission(){
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_WRITE_STORAGE);

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_WRITE_STORAGE);
            }
            return false;
        } else {
            return true;
        }
    }
}


