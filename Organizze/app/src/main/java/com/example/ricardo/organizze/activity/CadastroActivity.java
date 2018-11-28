package com.example.ricardo.organizze.activity;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ricardo.organizze.R;
import com.example.ricardo.organizze.config.ConfiguracaoFirebase;
import com.example.ricardo.organizze.helper.Base64Custom;
import com.example.ricardo.organizze.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class CadastroActivity extends AppCompatActivity {

    private EditText campoNome, campoEmail, campoSenha;
    private Button botaoCadastrar;
    private FirebaseAuth autenticacao;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);


        campoNome = findViewById(R.id.editNome);
        campoEmail = findViewById(R.id.editEmail);
        campoSenha = findViewById(R.id.editSenha);
        botaoCadastrar = findViewById(R.id.btnCadastrar);

        botaoCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String txtNome = campoNome.getText().toString();
                String txtEmail = campoEmail.getText().toString();
                String txtSenha = campoSenha.getText().toString();

                if (!txtNome.isEmpty()){
                    if (!txtEmail.isEmpty()){
                        if (!txtSenha.isEmpty()){

                            usuario = new Usuario();
                            usuario.setNome(txtNome);
                            usuario.setEmail(txtEmail);
                            usuario.setSenha(txtSenha);
                            cadastrarUsuario();

                        }else {
                            Toast.makeText(CadastroActivity.this,"Preencha a Senha!",Toast.LENGTH_LONG).show();
                        }
                    }else {
                        Toast.makeText(CadastroActivity.this,"Preencha o Email!",Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(CadastroActivity.this,"Preencha o Nome!",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void cadastrarUsuario(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmail(), usuario.getSenha()
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    String idUsuario = Base64Custom.codificarBase64(usuario.getEmail());
                    usuario.setIdUsuario(idUsuario);
                    usuario.salvar();
                    finish();

                }else {
                        String excecao = "";
                        try{
                            throw task.getException();
                        }catch (FirebaseAuthWeakPasswordException e){
                            excecao = "Digite uma senha mais forte!";
                        }catch (FirebaseAuthInvalidCredentialsException e){
                            excecao = "Por favor, digite um email valido";
                        }catch (FirebaseAuthUserCollisionException e){
                            excecao = "Esta conta ja foi cadastrada";
                        }catch (Exception e){
                            excecao = "Erro ao cadastrar o usuario" + e.getMessage();
                            e.printStackTrace();
                        }


                    Toast.makeText(CadastroActivity.this,excecao,Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
