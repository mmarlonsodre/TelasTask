package com.example.marlon.telastask.Pastas;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marlon.telastask.MainActivity;
import com.example.marlon.telastask.ProfileActivity;
import com.example.marlon.telastask.R;
import com.example.marlon.telastask.Tarefas.AddTarefaActivity;
import com.example.marlon.telastask.Tarefas.Tarefa;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Main2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawer;
    DatabaseReference databasePastas;
    ListView listViewPastas;
    List<Pastas> pastas;
    FloatingActionButton floatAddPasta;
    TextView profileUid;

    public static final String PASTA_NAME = "pastaname";
    public static final String PASTA_ID = "pastaid";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_drawer_layout);

        Toolbar toolbar = findViewById(R.id.toolbar);
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

        //TextView
        View headView = navigationView.getHeaderView(0);
        profileUid = headView.findViewById(R.id.headerUid);


        //Referencia do firebase
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            databasePastas = FirebaseDatabase.getInstance().getReference("pastas");
            databasePastas.keepSynced(true);
            profileUid.setText(" " + uid);
        }

        //List View das pastas
        listViewPastas = (ListView) findViewById(R.id.listPasta);
        listViewPastas.setDivider(null);
        pastas = new ArrayList<>();



        floatAddPasta = (FloatingActionButton) findViewById(R.id.floatPasta);
        floatAddPasta.setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddPastasDialog();
            }
        });

        //Jogar as informação para a activity de Tarefa
        listViewPastas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
                Pastas pasta = pastas.get(i);
                Intent intent = new Intent(getApplicationContext(), AddTarefaActivity.class);
                intent.putExtra(PASTA_ID, pasta.getPastaId());
                intent.putExtra(PASTA_NAME, pasta.getPastaName());
                startActivity(intent);
            }
        });

        listViewPastas.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Pastas pasta = pastas.get(position);
                showUpdatePastaDialog(pasta.getPastaId(), pasta.getPastaName());
                return true;
            }
        });


    }

    //Start do programa
    @Override
    protected void onStart() {
        super.onStart();

        databasePastas.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //aparecer os artistas
                pastas.clear();
                for (DataSnapshot pastaSnapshot : dataSnapshot.getChildren()) {
                    Pastas pasta = pastaSnapshot.getValue(Pastas.class);

                    pastas.add(pasta);
                }
                //Crear o adapter da Array
                PastaList adapter = new PastaList(Main2Activity.this, pastas);
                listViewPastas.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //Dialog Update Pasta
    private void showUpdatePastaDialog(final String pastaId, String pastaName) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.dialog_update_pasta, null);
        dialogBuilder.setView(dialogView);

        final EditText edtName = (EditText) dialogView.findViewById(R.id.edtUpdateNamePasta);
        final Button btnUpdatePasta = (Button) dialogView.findViewById(R.id.btnUpdatePasta);
        final Button btnDeletePasta = (Button) dialogView.findViewById(R.id.btnDeletePasta);

        //Aviso no topo do update
        dialogBuilder.setTitle("Atualizar Pasta " + pastaName);
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        //Botão do update
        btnUpdatePasta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtName.getText().toString().trim();

                if (TextUtils.isEmpty(name)) {
                    edtName.setError("Requer Nome");
                    return;
                }
                updatePasta(pastaId, pastaId, name);

                alertDialog.dismiss();
            }
        });

        //Botão de deletar
        btnDeletePasta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePasta(pastaId);
                alertDialog.dismiss();
            }

        });

    }

    //Update de nome e id da pasta
    private boolean updatePasta(String pastaId, String id, String name) {
        Pastas pasta = new Pastas(id, name);
        databasePastas.child(pastaId).setValue(pasta);
        Toast.makeText(this, "Pasta Atualizada com sucesso", Toast.LENGTH_LONG).show();
        return true;
    }

    //deletar pasta
    private void deletePasta(String pastaId) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            databasePastas.child(pastaId).removeValue();
            DatabaseReference drTarefa = FirebaseDatabase.getInstance().getReference("tarefas").child(uid).child(pastaId);
            DatabaseReference drSubtarefas = FirebaseDatabase.getInstance().getReference("subtarefas").child(uid).child(pastaId);

            drTarefa.removeValue();
            drSubtarefas.removeValue();
            Toast.makeText(this, "Pasta deletada", Toast.LENGTH_LONG).show();
        }
    }

    //Dialog Add Pasta
    private void showAddPastasDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.dialog_pasta, null);
        dialogBuilder.setView(dialogView);


        final EditText edtName = (EditText) dialogView.findViewById(R.id.edtNamePasta);
        final Button btnSalvarPasta = (Button) dialogView.findViewById(R.id.btnSalvarPasta);

        //Aviso no topo do update
        dialogBuilder.setTitle("Salvar Pasta");
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        //btão para adicionar
        btnSalvarPasta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtName.getText().toString().trim();

                if (!TextUtils.isEmpty(name)) {
                    //Salvar no RealTime Database
                    //Unique ID para as Pastas
                    String id = databasePastas.push().getKey();

                    //crear objeto
                    Pastas pasta = new Pastas(id, name);

                    //salvar as pastas
                    databasePastas.child(id).setValue(pasta);

                    //resetar edt
                    edtName.setText("");

                    //display
                    Toast.makeText(getApplicationContext(), "Pasta Salva", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(getApplicationContext(), "Erro ao Salvar Pasta", Toast.LENGTH_LONG).show();
                }
                alertDialog.dismiss();
            }
        });

    }



    //Botão de voltar
    @Override
    public void onBackPressed() {
        //Se o navigation drawer estiver aberto, feche ele
        if (drawer.isDrawerOpen(Gravity.START)) {
            drawer.closeDrawer(Gravity.START);
        }
        //Se não estiver aberto, feche o aplicativo
        else {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(getApplicationContext(), "Usuário Desconectado", Toast.LENGTH_LONG).show();
            super.onBackPressed();

        }

    }

    //Botões do Menu
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.um:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(Main2Activity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                Toast.makeText(getApplicationContext(),"Usuário Desconectado", Toast.LENGTH_LONG).show();
                break;
        }
        return false;
    }
}
