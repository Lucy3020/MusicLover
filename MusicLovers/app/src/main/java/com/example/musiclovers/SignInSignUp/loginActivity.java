package com.example.musiclovers.SignInSignUp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.musiclovers.MainActivity;
import com.example.musiclovers.Services.PlaceHolder;
import com.example.musiclovers.R;
import com.example.musiclovers.Models.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * DONE
 */
public class loginActivity extends AppCompatActivity {

    Button btn_createNewAccount;
    Button btn_logIn;
    EditText emailLogIn;
    EditText passwordLogIn;
    TextView forgottenPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //Let retrofit do its job
        PlaceHolder placeHolder = retrofit.create(PlaceHolder.class);
        findViewById();

        btn_createNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(loginActivity.this, createNewAccount.class);
                startActivity(intent);
            }
        });

        btn_logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailLogIn.getText().toString().trim();
                String password = passwordLogIn.getText().toString();
                if((email.isEmpty() || password.isEmpty()) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Toast.makeText(loginActivity.this, "Invalid Username or Password!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Call<User> call = placeHolder.login(email, password);
                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if(!response.isSuccessful()) {
                            Toast.makeText(loginActivity.this,response.code() + ": " + response.message(), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Toast.makeText(getApplicationContext(), "Success!!!", Toast.LENGTH_SHORT).show();
                        User user = response.body();
                        Intent intent = new Intent(loginActivity.this, MainActivity.class);
                        startActivity(intent);
                        SaveSharedPreference.setUser(getApplicationContext(), user.getUserName(), user.getEmail(), user.getAvatar(), user.get_id());
                        ConstraintLayout loading = findViewById(R.id.loading);
                        loading.setVisibility(View.VISIBLE);
                        finish();
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Something went wrong! 😢 \n Please try again! \n"+t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    private void findViewById(){
        btn_createNewAccount = findViewById(R.id.activity_login_create_account);
        btn_logIn = findViewById(R.id.activity_login_log_in);
        emailLogIn = findViewById(R.id.activity_login_email);
        passwordLogIn = findViewById(R.id.activity_login_password);
        forgottenPassword = findViewById(R.id.activity_login_forgotten_password);
    }
}