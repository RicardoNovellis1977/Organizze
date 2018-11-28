package com.example.ricardo.organizze.activity;

import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ricardo.organizze.R;
import com.example.ricardo.organizze.config.ConfiguracaoFirebase;
import com.example.ricardo.organizze.helper.Base64Custom;
import com.example.ricardo.organizze.helper.DateCustom;
import com.example.ricardo.organizze.model.Movimentacao;
import com.example.ricardo.organizze.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class ReceitasActivity extends AppCompatActivity {

    private TextInputEditText campoData, campoCategoria, campoDescricao;
    private EditText campoValor;
    private Movimentacao movimentacao;
    private DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private Double receitaTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receitas);

        campoValor = findViewById(R.id.editValor1);
        campoData = findViewById(R.id.editData1);
        campoCategoria = findViewById(R.id.editCategoria1);
        campoDescricao = findViewById(R.id.editDescricao1);

        campoData.setText(DateCustom.dataAtual());

        recuperarReceitaTotal();
    }

    public void recuperarReceitaTotal(){

        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUsuario);
        DatabaseReference usuarioRef = firebaseRef.child("usuarios").child(idUsuario);

        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                receitaTotal = usuario.getReceitaTotal();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    public void salvarReceita(View view) {

        if (validarCampoReceita()) {

            movimentacao = new Movimentacao();
            String data = campoData.getText().toString();
            Double valorRecuperado = Double.parseDouble(campoValor.getText().toString());


            movimentacao.setValor(valorRecuperado);
            movimentacao.setCategoria(campoCategoria.getText().toString());
            movimentacao.setDescricao(campoDescricao.getText().toString());
            movimentacao.setData(data);
            movimentacao.setTipo("r");


            Double receitaAtualizada = receitaTotal + valorRecuperado;
            atualizarReceita(receitaAtualizada);

            movimentacao.salvar(data);
            finish();
        }
    }

    public Boolean validarCampoReceita() {

        String textoValor = campoValor.getText().toString();
        String textoData = campoData.getText().toString();
        String textoCategoria = campoCategoria.getText().toString();
        String textoDescricao = campoDescricao.getText().toString();


        if (!textoValor.isEmpty()) {
            if (!textoData.isEmpty()) {
                if (!textoCategoria.isEmpty()) {
                    if (!textoDescricao.isEmpty()) {
                        return true;
                    }else {
                        Toast.makeText(ReceitasActivity.this,"Descrição não foi preechido !",Toast.LENGTH_LONG).show();
                        return false;
                    }
                }else {
                    Toast.makeText(ReceitasActivity.this,"Categoria não foi preechido !",Toast.LENGTH_LONG).show();
                    return false;
                }
            }else {
                Toast.makeText(ReceitasActivity.this,"Data não foi preechido !",Toast.LENGTH_LONG).show();
                return false;
            }
        }else {
            Toast.makeText(ReceitasActivity.this,"Valor não foi preechido !",Toast.LENGTH_LONG).show();
            return false;
        }
    }


    public void atualizarReceita(Double receita){

        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUsuario);
        DatabaseReference usuarioRef = firebaseRef.child("usuarios").child(idUsuario);

        usuarioRef.child("receitaTotal").setValue(receita);
    }
}
