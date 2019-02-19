package com.example.marlon.telastask.Tarefas;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


import com.example.marlon.telastask.R;
import com.example.marlon.telastask.SubTaref;
import com.example.marlon.telastask.Subtarefas.Subtarefa;

import java.util.List;

public class TarefaList extends ArrayAdapter<Tarefa> {
    private Activity context;
    private List<Tarefa> tarefaList;

    public TarefaList(Activity context, List<Tarefa> tarefaList){
        super(context, R.layout.listview_tarefa, tarefaList);
        this.context = context;
        this.tarefaList = tarefaList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem = inflater.inflate(R.layout.listview_tarefa, null, true);

        TextView txtName = (TextView) listViewItem.findViewById(R.id.txtNameTarefa);
        TextView txtTexto = (TextView) listViewItem.findViewById(R.id.txtTextoTarefa);

        Tarefa tarefa = tarefaList.get(position);

        txtName.setText(tarefa.getTarefaName());
        txtTexto.setText(tarefa.getTarefaTexto());

        return  listViewItem;
    }
}
