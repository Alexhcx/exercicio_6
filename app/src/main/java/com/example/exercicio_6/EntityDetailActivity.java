package com.example.exercicio_6;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class EntityDetailActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private ImageView fantasmasImageView;
    private ImageView criaturasImageView;
    private TextView nomeEntidadeTextView;
    private EditText nomeEntidadeEditText;
    private TextView descricaoEntidadeTextView;
    private EditText descricaoEntidadeEditText;
    private Button deletarButton;
    private Button atualizarButton;
    private TextView addAvistamentoTextView;
    private EditText localEditText;
    private EditText dataText;
    private EditText timeText;
    private EntityDetailAdapter avistamentoAdapter;
    private List<Sighting> avistamentos;
    private RecyclerView avistamentoRecyclerView;
    // Caminhos das imagens para Fantasmas e Criaturas
    private int caminhoFantasmas = R.drawable.fantasmas;
    private int caminhoCriaturas = R.drawable.criaturas;

    // Variáveis para guardar o tipo e o caminho da imagem
    private String tipoEntidade = "";
    private int caminhoImagem = 0;

    // ID da entidade que está sendo editada
    private int entityId = -1;

    // Adicione os recursos de áudio
    private MediaPlayer somFantasma;
    private MediaPlayer somCriatura;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entity_detail_activity);

        Drawable bordaVermelha = getDrawable(R.drawable.borda_vermelha);
        dbHelper = new DatabaseHelper(this);

        // Inicializa os elementos da interface
        fantasmasImageView = findViewById(R.id.fantasmaView);
        criaturasImageView = findViewById(R.id.criaturaView);

        //nomeEntidadeTextView = findViewById(R.id.nomeEntityView);
        nomeEntidadeEditText = findViewById(R.id.nomeEditText);

        //descricaoEntidadeTextView = findViewById(R.id.descriptionEntityView);
        descricaoEntidadeEditText = findViewById(R.id.descricaoEditTextText);

        deletarButton = findViewById(R.id.deletarEntity);
        atualizarButton = findViewById(R.id.atualizarEntity);
        addAvistamentoTextView = findViewById(R.id.adicionarAvistamentoView);

        localEditText = findViewById(R.id.localEditText);
        dataText = findViewById(R.id.dataText);
        timeText = findViewById(R.id.timeText);

        // Inicialize os MediaPlayers
        somFantasma = MediaPlayer.create(this, R.raw.somfantasma);
        somCriatura = MediaPlayer.create(this, R.raw.somcriatura);

        // Inicialize a lista de avistamentos
        avistamentos = new ArrayList<>();

        // Encontre a RecyclerView no seu layout
        avistamentoRecyclerView = findViewById(R.id.avistamentoRecyclerView);
        avistamentoRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Defina o Adapter para a RecyclerView
        avistamentoRecyclerView.setAdapter(avistamentoAdapter);

        fantasmasImageView.setOnClickListener(view -> {
            tipoEntidade = "Fantasma";
            caminhoImagem = caminhoFantasmas;
            fantasmasImageView.setBackground(bordaVermelha); // Adiciona a borda
            criaturasImageView.setBackground(null); // Remove a borda
            somFantasma.start(); // Toca o som do fantasma
        });

        criaturasImageView.setOnClickListener(view -> {
            tipoEntidade = "Criatura";
            caminhoImagem = caminhoCriaturas;
            fantasmasImageView.setBackground(null);
            criaturasImageView.setBackground(bordaVermelha);
            somCriatura.start(); // Toca o som da criatura
        });

        // Obtém o ID da entidade passada pela MainActivity
        entityId = getIntent().getIntExtra("entity_id", -1);

        // Se o ID for válido, recupera os dados da entidade do banco de dados
        if (entityId != -1) {
            dbHelper = new DatabaseHelper(this);
            Entity entity = dbHelper.getEntityById(entityId);

            // Inicialize o Adapter com o entityId
            avistamentoAdapter = new EntityDetailAdapter(avistamentos, this, entityId);
            avistamentoRecyclerView.setAdapter(avistamentoAdapter);

            carregarAvistamentos();

            // Define os dados na tela
            nomeEntidadeEditText.setText(entity.getName());
            descricaoEntidadeEditText.setText(entity.getDescription());

            if (entity.getImage().equalsIgnoreCase(String.valueOf(caminhoFantasmas))) {
                tipoEntidade = "Fantasma";
                caminhoImagem = caminhoFantasmas;
                fantasmasImageView.setBackground(bordaVermelha); // Adiciona a borda
                criaturasImageView.setBackground(null); // Remove a borda da outra ImageView
            } else {
                tipoEntidade = "Criatura";
                caminhoImagem = caminhoCriaturas;
                fantasmasImageView.setBackground(null); // Adiciona a borda
                criaturasImageView.setBackground(bordaVermelha); // Remove a borda da outra ImageView
            }

        }

        findViewById(R.id.addAvistamento).setOnClickListener(view -> {
            adicionarAvistamento();
            // Atualizar a RecyclerView após adicionar
            avistamentoAdapter.atualizarAvistamentos(); // Adicione esta linha
        });

        findViewById(R.id.atualizarEntity).setOnClickListener(view -> {
            // Valida se os campos de entrada estão preenchidos
            if (nomeEntidadeEditText.getText().toString().isEmpty() || descricaoEntidadeEditText.getText().toString().isEmpty()) {
                Toast.makeText(EntityDetailActivity.this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Adiciona uma nova entidade ao banco de dados
            atualizarEntidade(tipoEntidade, caminhoImagem);

            setResult(RESULT_OK);
            //finish();
        });

        findViewById(R.id.deletarEntity).setOnClickListener(view -> {

            setResult(RESULT_OK);

            // Deleta entidade do banco de dados
            deletarEntidade();

            setResult(RESULT_OK);
            finish();
        });

        Log.d("EntityDetailActivity", "entityId: " + entityId);

    }

    private void carregarAvistamentos() {
        // Limpar a lista existente
        avistamentos.clear();

        // Obter os avistamentos do banco de dados
        avistamentos.addAll(dbHelper.getSightingsByEntityId(entityId));

        // Notificar o adapter que os dados mudaram
        avistamentoAdapter.notifyDataSetChanged();
    }

    private void adicionarAvistamento() {
        String local = localEditText.getText().toString();
        String data = dataText.getText().toString();
        String horario = timeText.getText().toString();

        // Validação
        if (local.isEmpty() || data.isEmpty() || horario.isEmpty()) {
            Toast.makeText(EntityDetailActivity.this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Adicionar o avistamento ao banco de dados
        dbHelper.addSighting(entityId, data, horario, local);

        localEditText.setText("");
        dataText.setText("");
        timeText.setText("");
    }

    private void atualizarEntidade(String tipoEntidade, int caminhoImagem) {
        String nome = nomeEntidadeEditText.getText().toString();
        String descricao = descricaoEntidadeEditText.getText().toString();

        // Use o ID da entidade que está sendo editada:
        Entity entidade = new Entity(entityId, nome, descricao, String.valueOf(caminhoImagem));

        // Adicione a entidade ao banco de dados
        dbHelper.updateEntity(entidade);

        nomeEntidadeEditText.setText(entidade.getName());
        descricaoEntidadeEditText.setText(entidade.getDescription());
    }

    private void deletarEntidade() {

        // Use o ID da entidade que está sendo editada:
        if (entityId != -1) {

            dbHelper.deleteEntity(entityId);

            Toast.makeText(this, "Entidade deletada com sucesso!", Toast.LENGTH_SHORT).show();

            finish();
        } else {
            Toast.makeText(this, "Erro ao deletar entidade.", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (somFantasma != null) {
            somFantasma.release();
        }
        if (somCriatura != null) {
            somCriatura.release();
        }
    }

}