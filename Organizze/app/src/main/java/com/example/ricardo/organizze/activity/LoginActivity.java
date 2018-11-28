package com.example.ricardo.organizze.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ricardo.organizze.R;
import com.example.ricardo.organizze.config.ConfiguracaoFirebase;
import com.example.ricardo.organizze.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class LoginActivity extends AppCompatActivity {

    private EditText campoEmail, campoSenha;
    private Button botaoEntrar;
    private Usuario usuario;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        campoEmail = findViewById(R.id.editEmailLogin);
        campoSenha = findViewById(R.id.editSenhaLogin);
        botaoEntrar = findViewById(R.id.buttonEntrar);

        botaoEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String txtEmail = campoEmail.getText().toString();
                String txtSenha = campoSenha.getText().toString();

                if (!txtEmail.isEmpty()){
                    if (!txtSenha.isEmpty()){

                        usuario = new Usuario();
                        usuario.setEmail(txtEmail);
                        usuario.setSenha(txtSenha);
                        validarLogin();

                    }else {
                        Toast.makeText(LoginActivity.this,"Preencha a Senha!",Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(LoginActivity.this,"Preencha o Email!",Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    public void validarLogin(){

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.signInWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){

                    startActivity(new Intent(LoginActivity.this,PrincipalActivity.class));
                    finish();

                }else{
                    String excecao = "";
                    try{
                        throw task.getException();
                    }catch (FirebaseAuthInvalidUserException e) {
                        excecao = "Usuario não esta cadastrado.";
                    }catch (FirebaseAuthInvalidCredentialsException e ) {
                        excecao = "E-mail e senha não correspondem ao um usuario valido.";
                    }catch (Exception e){
                        excecao = "Erro ao cadastrar o usuario" + e.getMessage();
                        e.printStackTrace();
                    }

                    Toast.makeText(LoginActivity.this,excecao,Toast.LENGTH_LONG).show();
                }

            }
        });
    }
}
