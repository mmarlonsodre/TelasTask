package com.example.marlon.telastask;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class SubTaskDialog extends AppCompatDialogFragment {

    // Aqui criamos alguns atributos que estarão na nossa dialog
    private EditText mEdtTitleSubtask;
    private EditText mEdtSubtask;
    private EditText mEdtDeadlineSubtask;
    private SubtaskDialogListener listener;

    // Aqui customizamos nossa dialog fragment
    // Documentação - https://developer.android.com/reference/android/support/v7/app/AppCompatDialogFragment
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_task,null);

        builder.setView(view)
                .setTitle("Nova Subtarefa")
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Salvar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Aqui pegamos o conteúdo do edit text e atribuimos
                        // as respectivas variaveis que irão transportar os valores
                        // de um lugar ao outro
                        String titleSubtask = mEdtTitleSubtask.getText().toString();
                        String subtask = mEdtSubtask.getText().toString();
                        String deadlineSubtask = mEdtDeadlineSubtask.getText().toString();

                        listener.applyText(titleSubtask,subtask,deadlineSubtask);


                    }
                });

        //Aqui estamos dando valor aos atributos criados,
        // puxando os valores dos campos do layout dialog_task.xml
        mEdtTitleSubtask = view.findViewById(R.id.edtTitleSubtask);
        mEdtSubtask = view.findViewById(R.id.edtSubtask);
        mEdtDeadlineSubtask = view.findViewById(R.id.edtDeadlineSubtask);

        //Aqui retornamos para a Dialog
        return builder.create();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (SubtaskDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "Deve implementar SubtaskDialogListener");
        }
    }

    // Aqui vamos fazer uma classe chamada SubtaskDialogListener
    // que vai buscar e alterar campos no layout
    public interface SubtaskDialogListener{
        void applyText(String titleSubtask, String subtask, String deadlineSubtask);

    }
}
