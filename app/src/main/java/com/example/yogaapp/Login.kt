package com.example.yogaapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit


class Login : AppCompatActivity() {
    val TAG = "gfx"
    val OTP_VIEW = 1
    val PHONE_VIEW = 2
    val LOADING_VIEW = 3

    private lateinit var auth: FirebaseAuth
    private var storedVerificationId: String? = ""
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    var isOtpSent = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        var phoneNumber = findViewById<EditText>(R.id.phoneNumber)
        var otp = findViewById<EditText>(R.id.otp)

        auth = Firebase.auth

        showLayout(PHONE_VIEW)

        findViewById<Button>(R.id.loginButon).setOnClickListener {
            if (validatePhoneNumber(phoneNumber.text.toString())) {
                showLayout(LOADING_VIEW)
                startPhoneNumberVerification(phoneNumber.text.toString())
            } else {
                Snackbar.make(it, "Invalid Phone Number", Snackbar.LENGTH_LONG).show()
            }
        }

        findViewById<Button>(R.id.verifyOtpButon).setOnClickListener {
            showLayout(LOADING_VIEW)
            verifyPhoneNumberWithCode(storedVerificationId, otp.text.toString())
        }



        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.d(TAG, "onVerificationCompleted:$credential")
                //signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.d(TAG, "onVerificationFailed", e)
                showLayout(PHONE_VIEW)
                if (e is FirebaseAuthInvalidCredentialsException) {
                    Log.d(TAG, "onVerificationFailed: Invalid request")
                } else if (e is FirebaseTooManyRequestsException) {
                    Log.d(TAG, "onVerificationFailed: SMS quota exceeded")
                } else if (e is FirebaseAuthMissingActivityForRecaptchaException) {
                    Log.d(TAG, "onVerificationFailed: Missing Activity for reCAPTCHA")
                }
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken,
            ) {
                Log.d(TAG, "onCodeSent:$verificationId")
                storedVerificationId = verificationId
                resendToken = token
                isOtpSent = true
                showLayout(OTP_VIEW)
            }
        }

    }

    fun validatePhoneNumber(phoneNumber: String): Boolean = phoneNumber.matches(Regex("^[0-9]{10}$"))

    private fun startPhoneNumberVerification(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber("+91"+phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    mainActivity()
                    val user = task.result?.user
                } else {
                    Log.d(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Log.d(TAG, "signInWithPhoneAuthCredential: Invalid OTP")
                    }
                    // Update UI

                }
            }
    }

    private fun verifyPhoneNumberWithCode(verificationId: String?, code: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
        signInWithPhoneAuthCredential(credential)
    }

    private fun showLayout(show:Int){
        val otpLayout = findViewById<LinearLayout>(R.id.otpLayout)
        val phoneLayout = findViewById<LinearLayout>(R.id.phoneLayout)
        val progressIndicator = findViewById<CircularProgressIndicator>(R.id.progressIndicator)
        if (show==OTP_VIEW) {
            otpLayout.visibility = View.VISIBLE
            phoneLayout.visibility = View.GONE
            progressIndicator.visibility = View.GONE
        } else if (show==PHONE_VIEW){
            otpLayout.visibility = View.GONE
            phoneLayout.visibility = View.VISIBLE
            progressIndicator.visibility = View.GONE
        }else if(show==LOADING_VIEW){
            otpLayout.visibility = View.GONE
            phoneLayout.visibility = View.GONE
            progressIndicator.visibility = View.VISIBLE
        }
    }

    private fun mainActivity(){
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}