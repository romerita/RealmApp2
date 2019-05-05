package com.example.realmapp;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

public class MainActivity extends AppCompatActivity implements RealmChangeListener<RealmResults<Contact>> {

    Realm realm;
    Adapter adapter;
    ListView listView;
    RealmResults<Contact> results;
    boolean asc = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(android.R.id.content).setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        realm = Realm.getDefaultInstance();
        listView = findViewById(R.id.listView);
        load();

        FloatingActionButton add = findViewById(R.id.fab);
        FloatingActionButton edadOrder = findViewById(R.id.extras_order);
        FloatingActionButton edadBet = findViewById(R.id.extras_between);
        FloatingActionButton genDisct = findViewById(R.id.extras_only);
        FloatingActionButton refresh = findViewById(R.id.extras_refresh);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogoAdd();
            }
        });
        edadOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                order();
            }
        });
        edadBet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterAge();
            }
        });
        genDisct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMorF();
            }
        });
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                load();
            }
        });
    }

    void load(){
        results = realm.where(Contact.class).findAll();
        results.addChangeListener(this);

        adapter = new Adapter(this,results,R.layout.item_contact);
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void createNewContact(String name, String number,int edad, boolean genero){
        realm.beginTransaction();
        Contact contact = new Contact(1+System.currentTimeMillis(),name,number,edad,genero);
        realm.copyToRealm(contact);
        realm.commitTransaction();
    }

    private void dialogoAdd(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Añade un contacto");
        View viewInflate = LayoutInflater.from(this).inflate(R.layout.dialog_add,null);
        builder.setView(viewInflate);

        final EditText names = viewInflate.findViewById(R.id.name);
        final EditText numbers = viewInflate.findViewById(R.id.numero);
        final EditText edad = viewInflate.findViewById(R.id.edad);
        final ToggleButton sexo = viewInflate.findViewById(R.id.sexo);


        builder.setPositiveButton("Añadir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String nameStr = names.getText().toString().trim();
                String numberStr = numbers.getText().toString().trim();
                String edadStr = edad.getText().toString().trim();
                boolean sexoBoolean = sexo.getText().toString().equals("Hombre");

                if (nameStr.length() >0 && numbers.length() > 0 && edadStr.length() > 0){
                    int edadInt = Integer.parseInt(edad.getText().toString());
                    createNewContact(nameStr,numberStr,edadInt,sexoBoolean);
                }else if (nameStr.length() >0 && numbers.length() > 0){
                    createNewContact(nameStr,numberStr,0,sexoBoolean);
                }
                else Toast.makeText(MainActivity.this, "No se pueden dejar campos vacíos", Toast.LENGTH_SHORT).show();

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void filterAge(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Buscar entre edades");
        View viewInflate = LayoutInflater.from(this).inflate(R.layout.dialog_between,null);
        builder.setView(viewInflate);

        final EditText edada = viewInflate.findViewById(R.id.edada);
        final EditText edadb = viewInflate.findViewById(R.id.edadb);


        builder.setPositiveButton("Añadir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String a =edada.getText().toString().trim();
                String b =edadb.getText().toString().trim();
                if (a.length() >0 && b.length() >0){
                    showonly(Integer.parseInt(a),Integer.parseInt(b));
                }
                else Toast.makeText(MainActivity.this, "No se pueden dejar campos vacíos", Toast.LENGTH_SHORT).show();

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    void order(){
        if (asc) {
            results = results.sort("edad");
            asc = false;
        }else {
            results = results.sort("edad", Sort.DESCENDING);
            asc=true;
        }
        adapter = new Adapter(this,results,R.layout.item_contact);
        listView.setAdapter(adapter);
    }
    void showonly(int min, int max){
        results = results.where()
                .greaterThan("edad", min)
                .lessThan("edad",max)
                .findAll();
        adapter = new Adapter(this,results,R.layout.item_contact);
        listView.setAdapter(adapter);

    }
    boolean genere=true;
    void showMorF(){
        results = realm.where(Contact.class)
                .equalTo("sexo",genere)
                .findAll();
        genere=!genere;
        adapter = new Adapter(this,results,R.layout.item_contact);
        listView.setAdapter(adapter);
    }

    @Override
    public void onChange(RealmResults<Contact> element) {
        adapter.notifyDataSetChanged();
    }
}
