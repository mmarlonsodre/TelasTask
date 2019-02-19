package com.example.marlon.telastask.Subtarefas;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.marlon.telastask.R;
import com.example.marlon.telastask.Subtarefas.Subtarefa;

import java.util.List;

public class SubtarefaLista extends ArrayAdapter<Subtarefa> {
    private Activity context;
    private List<Subtarefa> subtarefaList;

    public SubtarefaLista(Activity context, List<Subtarefa> subtarefaList){
        super(context, R.layout.listview_subtarefa, subtarefaList);
        this.context = context;
        this.subtarefaList = subtarefaList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem = inflater.inflate(R.layout.listview_subtarefa, null, true);

        TextView txtName = (TextView) listViewItem.findViewById(R.id.txtNameSubtarefa);
        TextView txtTexto = (TextView) listViewItem.findViewById(R.id.txtTextoSubtarefa);

        Subtarefa subtarefa = subtarefaList.get(position);

        txtName.setText(subtarefa.getSubName());
        txtTexto.setText(subtarefa.getSubTexto());

        return  listViewItem;
    }
}
