package com.example.blog;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Signup extends AppCompatActivity implements View.OnClickListener{

    private EditText Name;
    private EditText Gender;
    private EditText Age;
    private EditText Number;
    private EditText Email;
    private EditText Password;
    private Button Signup;
    private TextView Login;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        Name = (EditText) findViewById(R.id.name_txt);
        Gender = (EditText) findViewById(R.id.gender_txt);
        Age = (EditText) findViewById(R.id.age_txt);
        Number = (EditText) findViewById(R.id.number_txt);
        Email = (EditText) findViewById(R.id.email_txt);
        Password = (EditText) findViewById(R.id.password_txt);
        Signup = (Button) findViewById(R.id.signup_btn);
        Login = (TextView) findViewById(R.id.login_txt);
        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();

        Signup.setOnClickListener(this);
        Login.setOnClickListener(this);
    }

    public void signup(){

        String name = Name.getText().toString();
        String gender = Gender.getText().toString();
        String age = Age.getText().toString();
        String number = Number.getText().toString();
        String email = Email.getText().toString();
        String password = Password.getText().toString();

        if(name.isEmpty()){
            Name.setError("Please enter your name");
            Name.requestFocus();
            return;
        }

        if(gender.isEmpty()){
            Gender.setError("Please enter your gender");
            Gender.requestFocus();
            return;
        }

        if(age.isEmpty()){
            Age.setError("Please enter your age");
            Age.requestFocus();
            return;
        }

        if(number.isEmpty()){
            Number.setError("Please enter your number");
            Number.requestFocus();
            return;
        }

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

        progressDialog.setMessage("Registrating User...");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if(!task.isSuccessful()){

                    FirebaseAuthException e = (FirebaseAuthException)task.getException();
                    Toast.makeText(Signup.this, "Failed Registration: "+e.getMessage(), Toast.LENGTH_SHORT).show();
//                    Toast.makeText(MainActivity.this,"Please enter detail again",Toast.LENGTH_LONG).show();
                }else{
//                    Toast.makeText(signup.this,"welcome",Toast.LENGTH_LONG).show();
                    finish();
                    startActivity(new Intent(Signup.this, Home.class));
                }
            }
        });

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference user = firebaseDatabase.getReference("users");

        String [] token = email.split("@");

        User u1 = new User(name,number,gender,age,"");
        user.child(token[0]).setValue(u1);
    }

    @Override
    public void onClick(View view) {

        if(view == Signup){
            signup();
        }

        if(view == Login){
            finish();
            startActivity(new Intent(Signup.this, MainActivity.class));
        }

    }

}
