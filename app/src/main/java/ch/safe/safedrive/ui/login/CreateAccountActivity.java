package ch.safe.safedrive.ui.login;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

import ch.safe.safedrive.R;
import ch.safe.safedrive.model.User;

public class CreateAccountActivity extends AppCompatActivity {

    EditText InputEmail, InputePassword, InputFirstname, InputLastname, InputPhone;
    Button validateBtn;
    FirebaseAuth firebaseAuth;

    String email;
    String password;
    String lastname;
    String firstname;
    String phone;

    FirebaseDatabase database;
    DatabaseReference myRef;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        InputEmail = (EditText) findViewById(R.id.emailInput);
        InputePassword = (EditText) findViewById(R.id.pwdInput);
        validateBtn = (Button) findViewById(R.id.valideBtn);

        InputFirstname = (EditText) findViewById(R.id.lastnameInput);
        InputLastname = (EditText) findViewById(R.id.firstnameInput);
        InputPhone = (EditText) findViewById(R.id.phoneInput);

        firebaseAuth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("user");

        validateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 email = InputEmail.getText().toString();
                 password = InputePassword.getText().toString();
                 lastname = InputLastname.getText().toString();
                firstname = InputFirstname.getText().toString();
                lastname = InputLastname.getText().toString();

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(getApplicationContext(),"Please fill in the required fields",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    Toast.makeText(getApplicationContext(),"Please fill in the required fields",Toast.LENGTH_SHORT).show();
                }

                if(password.length()<6){
                    Toast.makeText(getApplicationContext(),"Password must be at least 6 characters",Toast.LENGTH_SHORT).show();
                }
                if (TextUtils.isEmpty(lastname)) {
                    InputLastname.setError("Cannot be empty");
                    return;
                }
                if (TextUtils.isEmpty(firstname)) {
                    InputFirstname.setError("Cannot be empty");
                    return;
                }
              /*  if (TextUtils.isEmpty(phone)) {
                    InputPhone.setError("Cannot be empty");
                    return;
                }*/

                //storeUser();

                firebaseAuth.createUserWithEmailAndPassword(email,password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    FirebaseUser user = firebaseAuth.getCurrentUser();
                                    storeUser(user.getUid());
                                    startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                                    finish();
                                }
                                else{
                                    Toast.makeText(getApplicationContext(),"E-mail already use",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });


    }

    public void storeUser(String uid)
    {
       User user = new User(uid, email,firstname,lastname,phone);
       myRef.child(user.getId()).setValue(user);
    }
}