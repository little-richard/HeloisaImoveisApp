package com.example.heloisaimoveis.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.heloisaimoveis.R;

public class MainActivity extends AppCompatActivity {
    private Button btn_fazenda;
    private Button btn_imoveis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.iniciarComponentesTela();
    }

    private void iniciarComponentesTela(){
        btn_fazenda = findViewById(R.id.button_fazendas);
        btn_imoveis = findViewById(R.id.button_imoveis);

        clickBtnFazenda();
        clickBtnImoveis();
    }

    private void clickBtnFazenda(){
        btn_fazenda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FazendasActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });
    }

    private void clickBtnImoveis(){
        btn_imoveis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ImoveisActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });
    }

}
