package com.listatarefas;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText tarefa;
    private ImageView btnAddTarefa;
    private ListView listaTarefas;
    private SQLiteDatabase database;
    private ArrayList<String> arrayTarefas;
    private ArrayList<Integer> idTarefas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


            //Instanciando componentes
            tarefa = (EditText) findViewById(R.id.textoTarefa);
            btnAddTarefa = (ImageView) findViewById(R.id.buttonAddTarefa);

            //banco de dados
            database = openOrCreateDatabase("TAREFASBD", MODE_PRIVATE, null);

            //tabelas tarefafs
            database.execSQL("CREATE TABLE IF NOT EXISTS tarefas(id INTEGER PRIMARY KEY AUTOINCREMENT, tarefa VARCHAR)");

            btnAddTarefa.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String  textoTarefa = tarefa.getText().toString();
                    salvaTarefa(textoTarefa);
                }
            });

        recuperaTarefas();

        listaTarefas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //recupera o id do item na posicao selecionada
                int posicaoIndex = idTarefas.get(position);
               dialog(posicaoIndex);
            }
        });

        }

    private void salvaTarefa(String textoTarefa){

        try {
            if (!textoTarefa.isEmpty()) {
                database.execSQL("INSERT INTO tarefas(tarefa) values('" + textoTarefa + "')");
                tarefa.setText("");
                Toast.makeText(getApplicationContext(), "Tarefa cadastrada", Toast.LENGTH_SHORT).show();
                recuperaTarefas();
            }else {
                Toast.makeText(getApplicationContext(), "Campo tarefa vazio", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void recuperaTarefas() {
        try {
            //Busca dados do banco
            Cursor cursor = database.rawQuery("SELECT * FROM tarefas ORDER BY id DESC", null);

            //recupera index da coluna
            int indexTarefa = cursor.getColumnIndex("tarefa");
            int indexIdTarefa = cursor.getColumnIndex("id");

            //listView de tarefas
            listaTarefas = (ListView) findViewById(R.id.ListaTarefas);


            //adapter para a listView
            idTarefas = new ArrayList<>();
            arrayTarefas = new ArrayList<>();
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),R.layout.list_view,arrayTarefas);
            listaTarefas.setAdapter(adapter);

            //volta o cursor pro inicio da lista
            cursor.moveToFirst();

            while (cursor != null) {
                arrayTarefas.add(cursor.getString(indexTarefa));
                idTarefas.add(cursor.getInt(indexIdTarefa));
                cursor.moveToNext();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void apagarTarefas(int id){

        try{
            database.execSQL("DELETE from tarefas where id ="+id);
            Toast.makeText(getApplicationContext(),"Tarefa apagada",Toast.LENGTH_SHORT).show();
            recuperaTarefas();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void dialog(final int id){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Apagar Tarefa");
        builder.setMessage("Deseja Apagar essa tarefa?");
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setCancelable(false);
        builder.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                apagarTarefas(id);
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
