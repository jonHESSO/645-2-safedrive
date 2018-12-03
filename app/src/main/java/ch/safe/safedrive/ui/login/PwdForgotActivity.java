package ch.safe.safedrive.ui.login;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import ch.safe.safedrive.R;

public class PwdForgotActivity extends AppCompatActivity {

    EditText emailInput;
    Button btnNewPass;
    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pwd_forgot);
        emailInput = (EditText) findViewById(R.id.emailInputFP);
        btnNewPass = (Button) findViewById(R.id.sendLinkFP);

        firebaseAuth = FirebaseAuth.getInstance();

        btnNewPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailInput.getText().toString();

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(getApplicationContext(),"Please fill e-mail",Toast.LENGTH_SHORT).show();
                    return;
                }

                firebaseAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(getApplicationContext(),"Password reset link was sent your email address",Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(PwdForgotActivity.this, LoginActivity.class));
                                }
                                else{
                                    Toast.makeText(getApplicationContext(),"Mail sending error",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });


    }
}