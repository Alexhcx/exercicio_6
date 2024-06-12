package com.example.exercicio_6;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int EDIT_ENTITY_REQUEST = 1;
    private DatabaseHelper dbHelper;
    private TextInputEditText entidadeName;
    private EditText entidadeDescricao;
    private EntidadesAdapter entidadesAdapter;
    private List<Entity> entities;
    private int caminhoFantasmas = R.drawable.fantasmas;
    private int caminhoCriaturas = R.drawable.criaturas;
    private String tipoEntidade = "";
    private int caminhoImagem = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Drawable bordaVermelha = getDrawable(R.drawable.borda_vermelha);

        dbHelper = new DatabaseHelper(this);
        entidadeName = findViewById(R.id.entidadeName);
        entidadeDescricao = findViewById(R.id.entidadeDescricao);
        ImageView fantasmasImageView = findViewById(R.id.fantasmasImageView);
        ImageView criaturasImageView = findViewById(R.id.criaturasImageView);
        RecyclerView entidadesRView = findViewById(R.id.eventosRView);

        entidadesRView.setLayoutManager(new LinearLayoutManager(this));
        entities = new ArrayList<>();
        entidadesAdapter = new EntidadesAdapter(entities, this);
        entidadesRView.setAdapter(entidadesAdapter);

        carregarEntidades();

        fantasmasImageView.setOnClickListener(view -> {
            tipoEntidade = "Fantasma";
            caminhoImagem = caminhoFantasmas;
            fantasmasImageView.setBackground(bordaVermelha);
            criaturasImageView.setBackground(null);
        });

        criaturasImageView.setOnClickListener(view -> {
            tipoEntidade = "Criatura";
            caminhoImagem = caminhoCriaturas;
            fantasmasImageView.setBackground(null);
            criaturasImageView.setBackground(bordaVermelha);
        });

        findViewById(R.id.addAvistamento).setOnClickListener(view -> {
            if (entidadeName.getText().toString().isEmpty() || entidadeDescricao.getText().toString().isEmpty()) {
                Toast.makeText(MainActivity.this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
                return;
            }

            adicionarEntidade(tipoEntidade, caminhoImagem);
            entidadeName.setText("");
            entidadeDescricao.setText("");
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    void carregarEntidades() {
        entities.clear();
        entities.addAll(dbHelper.getAllEntities());
        entidadesAdapter.notifyDataSetChanged();
    }

    private void adicionarEntidade(String tipoEntidade, int caminhoImagem) {
        String nome = entidadeName.getText().toString();
        String descricao = entidadeDescricao.getText().toString();
        Entity entidade = new Entity(0, nome, descricao, String.valueOf(caminhoImagem));
        dbHelper.addEntity(entidade);
        carregarEntidades();

        // Mostrar o Toast
        Toast.makeText(MainActivity.this, "Entidade adicionada com sucesso!", Toast.LENGTH_SHORT).show();
    }

    // Sobrescreva o método onActivityResult
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EDIT_ENTITY_REQUEST && resultCode == RESULT_OK) {
            // Atualize a RecyclerView aqui
            carregarEntidades();
        }
    }
}