package ch.safedrive.safedrive.ui.login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import com.google.firebase.auth.FirebaseAuth;

import ch.safedrive.safedrive.R;
import ch.safedrive.safedrive.ui.hitchhiker.request.HitchhikerActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private Button btnLogin, btnRegister, btnResetPassword;
    private ProgressBar progressBar;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        btnLogin = (Button) findViewById(R.id.loginBtn);
        btnRegister = (Button) findViewById(R.id.createAccountBtn);
        inputEmail = (EditText) findViewById(R.id.emailLogin);
        inputPassword = (EditText) findViewById(R.id.pwdLogin);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnResetPassword = (Button) findViewById(R.id.forgotBtn);


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, CreateAccountActivity.class));
            }
        });

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, PwdForgotActivity.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // FOR CREATE A HITCHHIKER REQUEST
                Intent intentHitchhikerRequest = new Intent (LoginActivity.this, HitchhikerActivity.class);
                LoginActivity.this.startActivity(intentHitchhikerRequest);

                finish();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
}