package com.example.root.trabajofinal;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

public class ValidarInfoUsuario extends AppCompatActivity {

    private static int VALIDAR_INFO=700;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_material_design);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

    }

    private void setupViewPager(ViewPager viewPager) {
        ListaMaterialDesign.Adapter adapter = new ListaMaterialDesign.Adapter(getSupportFragmentManager());
        adapter.addFragment(new ContenidoValidarInfoUsuario(), "List");
        viewPager.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("validar","tengo q entrar");
        if(resultCode == RESULT_OK){
            Log.e("validar","entre");
            finish();
            startActivity(getIntent());
        }
    }
}
