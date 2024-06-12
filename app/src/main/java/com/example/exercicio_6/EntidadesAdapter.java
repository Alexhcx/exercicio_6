package com.example.exercicio_6;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class EntidadesAdapter extends RecyclerView.Adapter<EntidadesAdapter.ViewHolder> {

    private List<Entity> entities;
    private Context context;
    private ActivityResultLauncher<Intent> editEntityLauncher;

    public EntidadesAdapter(List<Entity> entities, Context context) {
        this.entities = entities;
        this.context = context;

        // Inicializar o ActivityResultLauncher
        editEntityLauncher = ((AppCompatActivity) context).registerForActivityResult(
                new StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                        // Recarregue os dados na RecyclerView
                        if (context instanceof MainActivity){
                            ((MainActivity) context).carregarEntidades();
                        }
                    }
                }
        );
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.entity_adapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Entity entity = entities.get(position);
        holder.entityName.setText(entity.getName());
        holder.descriptionTextView.setText(entity.getDescription());

        // Carrega a imagem usando Glide (converte String para int)
        Glide.with(context)
                .load(Integer.parseInt(entity.getImage()))
                .into(holder.entityImageView);

        // Define o listener de clique para o item
        holder.itemView.setOnClickListener(v -> {
            // Crie uma Intent para a nova Activity
            Intent intent = new Intent(context, EntityDetailActivity.class);

            // Passe o ID da entidade para a nova Activity
            intent.putExtra("entity_id", entity.getId());

            // Inicie a Activity usando o ActivityResultLauncher:
            editEntityLauncher.launch(intent);
        });
    }

    @Override
    public int getItemCount() {
        return entities.size();
    }

    public void updateEntities(List<Entity> newEntities) {
        entities.clear();
        entities.addAll(newEntities);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView entityName;
        public TextView descriptionTextView;
        public ImageView entityImageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            entityName = itemView.findViewById(R.id.entityName);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            entityImageView = itemView.findViewById(R.id.entityImageView);
        }
    }
}