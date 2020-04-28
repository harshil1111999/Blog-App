package com.example.blog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText Email;
    private EditText Password;
    private Button Login;
    private TextView Signup_txt;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Email = (EditText) findViewById(R.id.email_txt);
        Password = (EditText) findViewById(R.id.password_txt);
        Login = (Button) findViewById(R.id.login_btn);
        Signup_txt = (TextView) findViewById(R.id.signup_txt);
        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(this, Home.class));
        }

        Signup_txt.setOnClickListener(this);
        Login.setOnClickListener(this);

    }

    public void login(){

        String email = Email.getText().toString();
        String password = Password.getText().toString();

        if(email.isEmpty()){
            Email.setError("Please enter your email");
            Email.requestFocus();
            return;
        }

        if(password.isEmpty()){
            Password.setError("Please enter your password");
            Password.requestFocus();
            return;
        }

        progressDialog.setMessage("Loging In User...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if(!task.isSuccessful()){

                    FirebaseAuthException e = (FirebaseAuthException)task.getException();
                    Toast.makeText(MainActivity.this, "Failed Registration: "+e.getMessage(), Toast.LENGTH_LONG).show();
//                    Toast.makeText(MainActivity.this,"User already exists",Toast.LENGTH_LONG).show();
                }else{
//                    Toast.makeText(MainActivity.this,"welcome",Toast.LENGTH_LONG).show();
                    finish();
                    startActivity(new Intent(MainActivity.this, Home.class));
                }
            }
        });
    }

    @Override
    public void onClick(View view) {

        if(view == Login){
            login();
        }

        if(view == Signup_txt){
            finish();
            startActivity(new Intent(MainActivity.this, Signup.class));
        }

    }
}
