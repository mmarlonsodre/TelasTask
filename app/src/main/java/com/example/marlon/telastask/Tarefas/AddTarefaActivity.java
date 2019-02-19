package com.example.marlon.telastask.Tarefas;

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
import com.example.marlon.telastask.Subtarefas.AddSubtarefaActivity;
import com.example.marlon.telastask.Subtarefas.Subtarefa;
import com.example.marlon.telastask.User.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.marlon.telastask.Pastas.Main2Activity.PASTA_ID;

public class AddTarefaActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawer;
    TextView txtPastaName;
    ListView listViewTarefas, listViewSubs;
    DatabaseReference databaseTarefas, databasePastas;
    List<Tarefa> tarefas;
    FloatingActionButton floatAddTarefa, floatAddUser;
    TextView profileUid;

    public static final String TAREFA_NAME = "tarefaname";
    public static final String TAREFA_ID = "tarefaid";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_drawer_layout2);

        Toolbar toolbar = findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Task Manager");

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

        //List View das tarefas
        listViewTarefas = (ListView) findViewById(R.id.listTarefa);
        listViewTarefas.setDivider(null);
        tarefas = new ArrayList<>();
        txtPastaName = (TextView) findViewById(R.id.txtPastaName);

        // puxar os dados da pasta da Intent
        final Intent intent = getIntent();
        String id = intent.getStringExtra(PASTA_ID);
        final String name = intent.getStringExtra(Main2Activity.PASTA_NAME);
        txtPastaName.setText(name);

        //TextView
        View headView = navigationView.getHeaderView(0);
        profileUid = headView.findViewById(R.id.headerUid);


        //Referencia do firebase
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            databasePastas = FirebaseDatabase.getInstance().getReference("pastas");
            databaseTarefas = FirebaseDatabase.getInstance().getReference("tarefas").child(id).child("tarefa").getRef();
            databaseTarefas.keepSynced(true);
            profileUid.setText(" " + uid);
        }

        //FloatActionButtom
        floatAddUser = findViewById(R.id.floatAddUser);
        floatAddUser.setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddUserDialog();
            }
        });

        floatAddTarefa = (FloatingActionButton) findViewById(R.id.floatTarefa);
        floatAddTarefa.setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddTarefaDialog();
            }
        });

        //Jogar as informação para a activity de Tarefa
        listViewTarefas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
                String id = intent.getStringExtra(PASTA_ID);
                Tarefa tarefa = tarefas.get(i);
                Intent intent = new Intent(getApplicationContext(), AddSubtarefaActivity.class);
                intent.putExtra(TAREFA_ID, tarefa.getTarefaId());
                intent.putExtra(PASTA_ID, id);
                intent.putExtra(TAREFA_NAME, tarefa.getTarefaName());
                startActivity(intent);
            }
        });

        listViewTarefas.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Tarefa tarefa = tarefas.get(position);
                showUpdateTarefaDialog(tarefa.getTarefaId(), tarefa.getTarefaName());
                return true;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        databaseTarefas.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tarefas.clear();

                for (DataSnapshot tarefaSnapshot : dataSnapshot.getChildren()) {
                    Tarefa tarefa = tarefaSnapshot.getValue(Tarefa.class);
                    tarefas.add(tarefa);
                }

                TarefaList tarefaListAdapter = new TarefaList(AddTarefaActivity.this, tarefas);
                listViewTarefas.setAdapter(tarefaListAdapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showAddTarefaDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.dialog_tarefa, null);
        dialogBuilder.setView(dialogView);

        final EditText edtName = (EditText) dialogView.findViewById(R.id.edtNameTarefa);
        final EditText edtTexto = (EditText) dialogView.findViewById(R.id.edtTextoTarefa);
        final Button btnSalvarTarefa = (Button) dialogView.findViewById(R.id.btnSalvarTarefa);


        //Aviso no topo do update
        dialogBuilder.setTitle("Adicionar Tarefa");
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        //btão para adicionar
        btnSalvarTarefa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtName.getText().toString().trim();
                String texto = edtTexto.getText().toString().trim();


                if (!TextUtils.isEmpty(name)) {
                    //Salvar no RealTime Database
                    //Unique ID para as tarefas
                    String id = databaseTarefas.push().getKey();

                    //crear objeto
                    Tarefa tarefa = new Tarefa(id, name, texto);

                    //salvar as tarefas
                    databaseTarefas.child(id).setValue(tarefa);

                    //resetar edt
                    edtName.setText("");
                    edtTexto.setText("");

                    //display
                    Toast.makeText(getApplicationContext(), "Tarefa Salva", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(getApplicationContext(), "Erro ao Salvar Tarefa", Toast.LENGTH_LONG).show();
                }
                alertDialog.dismiss();
            }
        });
    }

    //Dialog para adicionar usuarios
    private void showAddUserDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.dialog_compartilhamento, null);
        dialogBuilder.setView(dialogView);


        final EditText edtUid = (EditText) dialogView.findViewById(R.id.edtUidCompartilhamento);
        final Button btnCompartilhar = (Button) dialogView.findViewById(R.id.btnCompartilhar);

        //Aviso no topo do update
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        //botão para adicionar
        btnCompartilhar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uid = edtUid.getText().toString().trim();

                if (!TextUtils.isEmpty(uid)) {
                    //Salvar no RealTime Database
                    //Unique ID para as Pastas
                    final Intent intent = getIntent();
                    String id = intent.getStringExtra(PASTA_ID);
                    String id2 = databasePastas.child(id).child("users").push().getKey();

                   Users user = new Users(uid);

                    //salvar as pastas
                    databasePastas.child(id).child("users").setValue(user);

                    //resetar edt
                    edtUid.setText("");

                    //display
                    Toast.makeText(getApplicationContext(), "Pasta Compartilhada", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(getApplicationContext(), "Erro ao Compartilhar Pasta", Toast.LENGTH_LONG).show();
                }
                alertDialog.dismiss();
            }
        });

    }

    //Dialog Update Tarefa
    private void showUpdateTarefaDialog(final String tarefaId, String tarefaName) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.dialog_update_tarefa, null);
        dialogBuilder.setView(dialogView);

        final EditText edtName = (EditText) dialogView.findViewById(R.id.edtUpdateNameTarefa);
        final EditText edtTexto = (EditText) dialogView.findViewById(R.id.edtUpdateTextoTarefa);
        final Button btnUpdateTarefa= (Button) dialogView.findViewById(R.id.btnUpdateTarefa);
        final Button btnDeleteTarefa= (Button) dialogView.findViewById(R.id.btnDeleteTarefa);

        //Aviso no topo do update
        dialogBuilder.setTitle("Atualizar Tarefa " + tarefaName);
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        //Botão do update
        btnUpdateTarefa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtName.getText().toString().trim();
                String texto = edtTexto.getText().toString().trim();

                if(TextUtils.isEmpty(name)){
                    edtName.setError("Requer Nome");
                    edtTexto.setError("Requer Texto");
                    return;
                }
                updateTarefa(tarefaId ,name, texto);
                alertDialog.dismiss();
            }
        });

        //Botão de deletar
        btnDeleteTarefa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTarefa(tarefaId);
                alertDialog.dismiss();
            }
        });
    }

    //Update de nome e id da tarefa
    private  boolean updateTarefa(String id, String name, String texto){
        Tarefa tarefa = new Tarefa(id, name, texto);
        Subtarefa subtarefa = new Subtarefa(id, name, texto);
        databaseTarefas.child(id).setValue(tarefa);
        Toast.makeText(this, "Tarefa Atualizada com sucesso", Toast.LENGTH_LONG).show();
        return true;
    }

    //deletar tarefa
    public void deleteTarefa(String tarefaId) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            databaseTarefas.child(tarefaId).removeValue();
            Intent intent = getIntent();
            String id = intent.getStringExtra(PASTA_ID);
            DatabaseReference drTarefa = FirebaseDatabase.getInstance().getReference("tarefas").child(uid).child(id).child(tarefaId).getRef();
            DatabaseReference drSubtarefas = FirebaseDatabase.getInstance().getReference("subtarefas").child(uid).child(id).child(tarefaId).getRef();

            drTarefa.removeValue();
            drSubtarefas.removeValue();
            Toast.makeText(this, "Tarefa deletada", Toast.LENGTH_LONG).show();
        }
    }
    //Botões do Menu
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId){
            case R.id.um:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(AddTarefaActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                Toast.makeText(getApplicationContext(),"Usuário Desconectado", Toast.LENGTH_LONG).show();
                break;
        }
        return false;
    }
}
