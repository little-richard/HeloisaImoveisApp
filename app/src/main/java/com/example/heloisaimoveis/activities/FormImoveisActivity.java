package com.example.heloisaimoveis.activities;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.heloisaimoveis.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageClickListener;
import com.synnapps.carouselview.ImageListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FormImoveisActivity extends AppCompatActivity {

    private CarouselView carouselView;
    private Button btnUpload;
    private static final int Selected = 100;
    private StorageReference mStorage;
    int[] sampleImages = {R.drawable.img_casa1, R.drawable.img_casa2, R.drawable.img_casa3};
    List<String> listUrisImagens = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_imoveis);

        mStorage = FirebaseStorage.getInstance().getReference();
        iniciarComponentesTela();

    }

    private void iniciarComponentesTela(){
        carouselView  = findViewById(R.id.carouselView);
        btnUpload = findViewById(R.id.btnUpload);

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                escolherImagem();
            }
        });
    }


    public void escolherImagem(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Selecionar Imagem"), Selected);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Selected && resultCode == RESULT_OK){
            if(data.getClipData() != null){
                int totalItemSelected = data.getClipData().getItemCount();

                for(int i=0; i< totalItemSelected; i++){
                    Uri fileUri = data.getClipData().getItemAt(i).getUri();

                    String fileName = getFileName(fileUri);

                    StorageReference fileToUpload = mStorage.child("Images").child(fileName);
                    listUrisImagens.add(fileToUpload.getPath());
                    fileToUpload.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(FormImoveisActivity.this, "Upload Feito!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
/*
                carouselView.setPageCount(listUrisImagens.size());

                ImageListener imageListener = new ImageListener() {
                    @Override
                    public void setImageForPosition(int position, ImageView imageView) {
                        imageView.setImageURI(listUrisImagens.get(position));
                    }
                };

                carouselView.setImageListener(imageListener);*/

            } else if(data.getData() != null){
                Uri fileUri = data.getData();

                String fileName = getFileName(fileUri);

                StorageReference fileToUpload = mStorage.child("Images").child(fileName);
                fileToUpload.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(FormImoveisActivity.this, "Upload Feito!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    public String getFileName(Uri uri){
        String result = null;
        if(uri.getScheme().equals("content")){
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try{
                if(cursor != null && cursor.moveToFirst()){
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }finally {
                cursor.close();
            }
        }

        if(result == null){
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if(cut != -1){
                result = result.substring(cut + 1);
            }
        }

        return result;
    }

    public List<String> carregarImagens(){
        return null;
    }
}