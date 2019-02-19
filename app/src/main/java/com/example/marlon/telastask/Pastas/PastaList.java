package com.example.marlon.telastask.Pastas;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.marlon.telastask.R;

import java.util.List;

public class PastaList extends ArrayAdapter<Pastas> {
    private Activity context;
    private List<Pastas> pastasList;

    public PastaList(Activity context, List<Pastas> pastasList){
        super(context, R.layout.listview_pasta, pastasList);
        this.context = context;
        this.pastasList = pastasList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem = inflater.inflate(R.layout.listview_pasta, null, true);

        TextView txtName = (TextView) listViewItem.findViewById(R.id.txtNamePasta);

        Pastas pasta = pastasList.get(position);

        txtName.setText(pasta.getPastaName());

        return  listViewItem;
    }
}
