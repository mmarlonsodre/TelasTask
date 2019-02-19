package com.example.marlon.telastask.Subtarefas;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marlon.telastask.MainActivity;
import com.example.marlon.telastask.Pastas.Main2Activity;
import com.example.marlon.telastask.R;
import com.example.marlon.telastask.Tarefas.AddTarefaActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.marlon.telastask.Pastas.Main2Activity.PASTA_ID;
import static com.example.marlon.telastask.Tarefas.AddTarefaActivity.TAREFA_ID;


public class AddSubtarefaActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawer;
    TextView txtTarefaName;
    ListView listViewSubtarefa;
    DatabaseReference databaseSubtarefas;
    List<Subtarefa> subtarefas;
    FloatingActionButton floatAddSubtarefa;
    TextView profileUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_drawer_layout3);

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Task Manager");

        //drawer da toolbar
        drawer = findViewById(R.id.drawer_layout);

        //Hamburger
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.drawer_open,
                R.string.drawer_close);

        //sincronizar estado com o navigation viewer
        toggle.syncState();

        //NavigationView
        NavigationView navigationView = findViewById(R.id.navigation_viewer);
        //Configurar a activity como "quem responde quando um item é selecionado"
        navigationView.setNavigationItemSelectedListener(this);

        txtTarefaName = (TextView) findViewById(R.id.txtTarefaName);

        //List View das tarefas
        listViewSubtarefa = (ListView) findViewById(R.id.listSubtarefa);
        listViewSubtarefa.setDivider(null);
        subtarefas = new ArrayList<>();

        // puxar os dados da pasta da Intent
        final Intent intent = getIntent();
        String id = intent.getStringExtra(PASTA_ID);
        String id1 = intent.getStringExtra(TAREFA_ID);
        String name = intent.getStringExtra(AddTarefaActivity.TAREFA_NAME);
        txtTarefaName.setText(name);

        //TextView
        View headView = navigationView.getHeaderView(0);
        profileUid = headView.findViewById(R.id.headerUid);


        //Referencia do firebase
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            databaseSubtarefas = FirebaseDatabase.getInstance().getReference("subtarefas").child(uid).child(id).child(id1).child("subtarefas").getRef();
            databaseSubtarefas.keepSynced(true);
            profileUid.setText(" " + uid);
        }

        //Adicionar Subtarefa
        floatAddSubtarefa = (FloatingActionButton) findViewById(R.id.floatSubtarefa);
        floatAddSubtarefa.setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddSubtarefaDialog();
            }
        });

        listViewSubtarefa.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Subtarefa subtarefa = subtarefas.get(position);
                showUpdateSubtarefaDialog(subtarefa.getSubId(), subtarefa.getSubName());
                return true;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        databaseSubtarefas.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                subtarefas.clear();

                for (DataSnapshot subtarefaSnapshot : dataSnapshot.getChildren()) {
                    Subtarefa subtarefa = subtarefaSnapshot.getValue(Subtarefa.class);
                    subtarefas.add(subtarefa);
                }

                SubtarefaLista subtarefaListaAdapter = new SubtarefaLista(AddSubtarefaActivity.this, subtarefas);
                listViewSubtarefa.setAdapter(subtarefaListaAdapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showAddSubtarefaDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.dialog_subtarefa, null);
        dialogBuilder.setView(dialogView);

        final EditText edtName = (EditText) dialogView.findViewById(R.id.edtNameSubtarefa);
        final EditText edtTexto = (EditText) dialogView.findViewById(R.id.edtTextoSubtarefa);
        final Button btnSalvarSubtarefa = (Button) dialogView.findViewById(R.id.btnSalvarSubtarefa);

        //Aviso no topo do update
        dialogBuilder.setTitle("Salvar Subtarefa");
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        //btão para adicionar
        btnSalvarSubtarefa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtName.getText().toString().trim();
                String texto = edtTexto.getText().toString().trim();


                if (!TextUtils.isEmpty(name)) {
                    //Salvar no RealTime Database
                    //Unique ID para as tarefas
                    String id = databaseSubtarefas.push().getKey();

                    //crear objeto
                    Subtarefa subtarefa = new Subtarefa(id, name, texto);

                    //salvar as subtarefas
                    databaseSubtarefas.child(id).setValue(subtarefa);

                    //resetar edt
                    edtName.setText("");
                    edtTexto.setText("");

                    //display
                    Toast.makeText(getApplicationContext(), "Subtarefa Salva", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(getApplicationContext(), "Erro ao Salvar Subtarefa", Toast.LENGTH_LONG).show();
                }
                alertDialog.dismiss();
            }
        });

    }

    //Dialog Update Subtarefa
    private void showUpdateSubtarefaDialog(final String subId, String subName) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.dialog_update_subtarefa, null);
        dialogBuilder.setView(dialogView);

        final EditText edtName = (EditText) dialogView.findViewById(R.id.edtUpdateNameSubtarefa);
        final EditText edtTexto = (EditText) dialogView.findViewById(R.id.edtUpdateTextoSubtarefa);
        final Button btnUpdateSubtarefa = (Button) dialogView.findViewById(R.id.btnUpdateSubtarefa);
        final Button btnDeleteSubtarefa = (Button) dialogView.findViewById(R.id.btnDeleteSubtarefa);

        //Aviso no topo do update
        dialogBuilder.setTitle("Atualizar Subtarefa " + subName);
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        //Botão do update
        btnUpdateSubtarefa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtName.getText().toString().trim();
                String texto = edtTexto.getText().toString().trim();

                if (TextUtils.isEmpty(name)) {
                    edtName.setError("Requer Nome");
                    edtTexto.setError("Requer Texto");
                    return;
                }
                updateSubtarefa(subId, subId, name, texto);
                alertDialog.dismiss();
            }
        });

        //Botão de deletar
        btnDeleteSubtarefa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSubtarefa(subId);
                alertDialog.dismiss();
            }

        });
    }


    //Update de nome e id da tarefa
    private boolean updateSubtarefa(String id, String subId, String name, String texto) {
        Subtarefa subtarefa = new Subtarefa(id, name, texto);
        databaseSubtarefas.child(subId).setValue(subtarefa);
        Toast.makeText(this, "Subtarefa Atualizada com sucesso", Toast.LENGTH_LONG).show();
        return true;
    }

    //deletar tarefa
    public void deleteSubtarefa(String subId) {
        databaseSubtarefas.child(subId).removeValue();
        Toast.makeText(this, "Subtarefa deletada", Toast.LENGTH_LONG).show();
    }

    //Botões do Menu
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.um:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(AddSubtarefaActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "Usuário Desconectado", Toast.LENGTH_LONG).show();
                break;
        }
        return false;
    }

}

