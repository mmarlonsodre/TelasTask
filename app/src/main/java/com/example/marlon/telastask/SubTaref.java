package com.example.marlon.telastask;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;


import java.util.List;

public class SubTaref extends AppCompatActivity implements SubTaskDialog.SubtaskDialogListener, NavigationView.OnNavigationItemSelectedListener {
        DrawerLayout drawer;

    private TextView mTxtTitleSubtask;
    private TextView mTxtSubtask;
    private TextView mTxtDeadlineSubtask;
    private Button mBtnAddSubtask;
    RecyclerView subtarefList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_drawer_layout2);

        Toolbar toolbar = findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Task Manager");

        drawer = findViewById(R.id.drawer_layout);

        //Subtarefas
        RecyclerView.LayoutManager llm= new LinearLayoutManager(this);
        subtarefList.setLayoutManager(llm);

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

        mTxtTitleSubtask = (TextView) findViewById(R.id.txtTitleSubSubtask);
        mTxtSubtask = (TextView) findViewById(R.id.txtSubtask);
        mTxtDeadlineSubtask = (TextView) findViewById(R.id.txtDeadlineSubtask);

        mBtnAddSubtask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });
    }

    public void openDialog(){
        SubTaskDialog subtaskDialog = new SubTaskDialog();
        subtaskDialog.show(getSupportFragmentManager(), "Subtask dialog");
    }

    @Override
    public void applyText(String titleSubtask, String subtask, String deadlineSubtask) {
        mTxtTitleSubtask.setText(titleSubtask);
        mTxtSubtask.setText(subtask);
        mTxtDeadlineSubtask.setText(deadlineSubtask);
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
            super.onBackPressed();
        }
    }

    //Botões do Menu
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId){
            case R.id.um:
                Intent intent = new Intent(
                        this,
                        MainActivity.class);
                startActivity(intent);
                break;
        }
        return false;
    }
}
