package com.example.heloisaimoveis.activities;

import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.heloisaimoveis.R;
import com.example.heloisaimoveis.utils.FileUtil;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageClickListener;
import com.synnapps.carouselview.ImageListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FormImoveisActivity extends AppCompatActivity {

    private CarouselView carouselView;
    private Button btnUpload;
    private static final int Selected = 100;
    private StorageReference mStorage;
    private ProgressBar progressBar;

    List<Bitmap> imgs = new ArrayList<>();
    List<String> listRefImagens = new ArrayList<>();
    List<Uri> listUris = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_imoveis);

        mStorage = FirebaseStorage.getInstance().getReference();
        iniciarComponentesTela();

    }

    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            imageView.setImageBitmap(imgs.get(position));
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Selected && resultCode == RESULT_OK){
            if(data.getClipData() != null){

                int totalItemSelected = data.getClipData().getItemCount();

                for(int i=0; i< totalItemSelected; i++){
                    Uri uri = data.getClipData().getItemAt(i).getUri();
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    imgs.add(bitmap);
                }

                carouselView.setPageCount(imgs.size());
                carouselView.setImageListener(imageListener);

                //new UploadImagesTask().execute(data.getClipData());

            } else if(data.getData() != null){
                Uri fileUri = data.getData();
                String fileName = FileUtil.getFileName(fileUri, getContentResolver());

            }
        }


    }

    private void iniciarComponentesTela(){
        carouselView  = findViewById(R.id.carouselView);
        btnUpload = findViewById(R.id.btnUpload);
        progressBar = findViewById(R.id.progressBar);

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                escolherImagem();
            }
        });

        carouselView.setPageCount(imgs.size());

        carouselView.setImageListener(imageListener);

    }

    public void escolherImagem(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Selecionar Imagem"), Selected);
    }

    private class UploadImagesTask extends AsyncTask<ClipData, Integer, Boolean>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Boolean doInBackground(ClipData... data) {
            ClipData clipData = data[0];
            int totalItemSelected = clipData.getItemCount();
            for(int i=0; i< totalItemSelected; i++){
                listRefImagens.clear();
                Uri uri = clipData.getItemAt(i).getUri();
                StorageReference fileToUpload = mStorage.child("Images").child(FileUtil.getFileName(uri, getContentResolver()));
                listRefImagens.add(fileToUpload.getPath());
                fileToUpload.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(FormImoveisActivity.this, "Upload Feito!", Toast.LENGTH_SHORT).show();
                    }

                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        int progress = (int) ((100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount());
                        progressBar.setProgress(progress);
                    }
                });
            }

            return Boolean.TRUE;
        }
    }
}