package com.example.exercicio_6;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class EntityDetailAdapter extends RecyclerView.Adapter<EntityDetailAdapter.ViewHolder> {

    private List<Sighting> avistamentos;
    private Context context;
    private DatabaseHelper dbHelper; // Adicione o DatabaseHelper
    private int entityId; // ID da entidade para a qual os avistamentos pertencem

    public EntityDetailAdapter(List<Sighting> avistamentos, Context context, int entityId) {
        this.avistamentos = avistamentos;
        this.context = context;
        this.entityId = entityId;
        this.dbHelper = new DatabaseHelper(context); // Inicialize o DatabaseHelper
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.entity_detail_adapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Sighting avistamento = avistamentos.get(position);
        holder.localAvistamentoEditText.setText(avistamento.getLocal());
        holder.dataAvistamentoEditText.setText(avistamento.getData());
        holder.timeAvistamentoEditText.setText(avistamento.getHorario());

        // Configurar Listener para o botão "Excluir"
        holder.bExcluirAvistamentoedit.setOnClickListener(v -> {
            excluirAvistamento(position);
        });

        // Configurar Listener para o botão "Atualizar"
        holder.bAtualizarAvistamento.setOnClickListener(v -> {
            // 1. Obter os novos valores dos EditTexts
            String novoLocal = holder.localAvistamentoEditText.getText().toString();
            String novaData = holder.dataAvistamentoEditText.getText().toString();
            String novoHorario = holder.timeAvistamentoEditText.getText().toString();

            // 2. Atualizar o objeto Avistamento
            avistamento.setLocal(novoLocal);
            avistamento.setData(novaData);
            avistamento.setHorario(novoHorario);

            // 3. Atualizar o avistamento no banco de dados
            dbHelper.updateSighting(avistamento.getId(), entityId, novaData, novoHorario, novoLocal);

            // 4. Notificar o Adapter sobre a mudança (opcional, se não estiver atualizando a UI diretamente)
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return avistamentos.size();
    }

    private void excluirAvistamento(int position) {
        Sighting avistamento = avistamentos.get(position);
        dbHelper.deleteSighting(avistamento.getId());

        // Remova o avistamento da lista e notifique o adaptador
        avistamentos.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, avistamentos.size());

        // Mostrar um Toast
        Toast.makeText(context, "Avistamento excluído com sucesso!", Toast.LENGTH_SHORT).show();
    }


    // Atualizar a RecyclerView após adicionar, excluir ou atualizar um avistamento
    public void atualizarAvistamentos() {
        avistamentos.clear();
        avistamentos.addAll(dbHelper.getSightingsByEntityId(entityId));
        notifyDataSetChanged();

        Toast.makeText(context, "Avistamento atualizado com sucesso!", Toast.LENGTH_SHORT).show();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public EditText localAvistamentoEditText;
        public EditText dataAvistamentoEditText;
        public EditText timeAvistamentoEditText;
        public Button bExcluirAvistamentoedit;
        public Button bAtualizarAvistamento;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            localAvistamentoEditText = itemView.findViewById(R.id.localAvistamentoEditText);
            dataAvistamentoEditText = itemView.findViewById(R.id.dataAvistamentoEditText);
            timeAvistamentoEditText = itemView.findViewById(R.id.timeAvistamentoEditText);
            bExcluirAvistamentoedit = itemView.findViewById(R.id.excluirAvistamento);
            bAtualizarAvistamento = itemView.findViewById(R.id.atualizarAvistamento);
        }
    }
}