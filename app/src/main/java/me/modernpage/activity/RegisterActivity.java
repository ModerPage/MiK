package me.modernpage.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import me.modernpage.util.Constants;
import me.modernpage.entity.UserEntity;
import me.modernpage.task.RegisterUser;

public class RegisterActivity extends AppCompatActivity implements RegisterUser.OnRegisterUser {
    private static final String TAG = "RegisterActivity";
    private static final String REGISTERED = "registered";
    private static final String USERNAME_EXIST = "username_exist";
    private static final String EMAIL_EXIST = "email_exist";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Button mSignup = findViewById(R.id.register_sign_up);
        mSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText mUsername = findViewById(R.id.register_username);
                EditText mFullname = findViewById(R.id.register_fullname);
                EditText mEmail = findViewById(R.id.register_email);
                EditText mPassword = findViewById(R.id.register_password);
                EditText mConfirmPassword = findViewById(R.id.register_confirmpassword);

                boolean isValidRegister = true;

                if(isBlank(mUsername.getText().toString())) {
                    mUsername.setError("the field can't be empty");
                    isValidRegister = false;
                } else if (Constants.Regex.isNotValid(mUsername.getText().toString().trim(), Constants.Regex.USERNAME_REGEX)) {
                    isValidRegister = false;
                    mUsername.setError("username must be 6 characters long at least and can contain \".\" \"_\" chars");
                }

                if(isBlank(mFullname.getText().toString())) {
                    mFullname.setError("the field can't be empty");
                    isValidRegister = false;
                } else if (Constants.Regex.isNotValid(mFullname.getText().toString().trim(), Constants.Regex.FULLNAME_REGEX)) {
                    isValidRegister = false;
                    mFullname.setError("full name must be 8 characters long at least");
                }

                if(isBlank(mEmail.getText().toString())) {
                    mEmail.setError("the field can't be empty");
                    isValidRegister = false;
                } else if (Constants.Regex.isNotValid(mEmail.getText().toString().trim(), Constants.Regex.EMAIL_REGEX)) {
                    mEmail.setError("invalid email address");
                    isValidRegister = false;
                }

                if(isBlank(mPassword.getText().toString())) {
                    mPassword.setError("the field can't be empty");
                    isValidRegister = false;
                } else if (Constants.Regex.isNotValid(mPassword.getText().toString().trim(), Constants.Regex.PASSWORD_REGEX)) {
                    isValidRegister = false;
                    mPassword.setError("password must be 8 characters long at least and contain a digit");
                }

                if(isBlank(mConfirmPassword.getText().toString())) {
                    mConfirmPassword.setError("the field can't be empty");
                    isValidRegister = false;
                }

                if(isValidRegister) {
                    if(!mPassword.getText().toString().equals(mConfirmPassword.getText().toString()))
                        mConfirmPassword.setError("the confirm password is not same");
                    else {
                        RegisterUser registerUser = new RegisterUser(RegisterActivity.this);
                        UserEntity user = new UserEntity(mUsername.getText().toString().trim().toLowerCase(), mFullname.getText().toString().trim(),
                                mEmail.getText().toString().trim(), mPassword.getText().toString().trim());
                        registerUser.execute(user);
                    }
                }

            }
        });
    }

    @Override
    public void onRegisterUserFinished(String status) {
        if(status == null) {
            Toast.makeText(this,"occurs something wrong", Toast.LENGTH_SHORT).show();
        } else {
            if (REGISTERED.equals(status)) {
                finish();
            }
            if (EMAIL_EXIST.equals(status)) {
                Toast.makeText(this,"email exists",Toast.LENGTH_SHORT).show();
            }
            if (USERNAME_EXIST.equals(status)) {
                Toast.makeText(this,"username exists",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isBlank(String text) {
        return text.trim().length() == 0;
    }

    public void goToSignin(View view) {
        finish();
    }
}
