package com.reservasayala.controllers

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.reservasayala.R
import com.reservasayala.utils.Utils

class LoginActivity : AppCompatActivity() {
    // Declaracion de variables
    private lateinit var auth: FirebaseAuth
    private lateinit var btnLoginGoogle: Button
    private lateinit var btnLoginWithEmail: Button
    private lateinit var btnRegisterWithEmail: Button
    private lateinit var txtEmail: EditText
    private lateinit var txtPass: EditText
    private lateinit var txtForgotpass: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inicialización de las variables
        btnLoginGoogle = findViewById(R.id.btn_loginGoogle)
        txtEmail = findViewById(R.id.txtNombreUsuario)
        txtPass = findViewById(R.id.txtPassword)
        txtForgotpass = findViewById(R.id.txtForgotPassword)
        btnLoginWithEmail = findViewById(R.id.btn_login)
        btnRegisterWithEmail = findViewById(R.id.btn_Registrar)

        auth = FirebaseAuth.getInstance()

        // Listeners
        btnLoginGoogle.setOnClickListener { onClickGoogleLogIn() }
        btnLoginWithEmail.setOnClickListener { loginWithEmail() }
        btnRegisterWithEmail.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
        txtForgotpass.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * Cuando se reinicia la app se comprueba que no haya usuario registrado
     * En caso contrario esta actividad se salta
     */
    override fun onStart() {
        super.onStart()

        val prefs: SharedPreferences =
            getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val username: String? = prefs.getString("name", "")
        if (!username?.trim().equals("")) {
            mainIntent()
            this.finish()
        }
    }

    /**
     * Metodo que recibe el token de la cuenta de google seleccionada
     * y la registra en la base de datos de Firebase
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //Confirma que la peticion venga del metodo GoogleLogIn
        if (requestCode == Utils.RC_SIGN_IN) {

            //Recoge la cuenta de google asociada a los datos del intent
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                //Se intenta coger la cuenta
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    //Si la cuenta no es nula se recogen sus credenciales
                    val credential: AuthCredential = GoogleAuthProvider.getCredential(account.idToken, null)

                    //Registramos las credenciales en FirebaseAuth
                    auth.signInWithCredential(credential).addOnCompleteListener {
                        if (it.isSuccessful) {
                            //Crear las preferencias
                            //Llevar a main_activity
                            Utils.Toast(this, getString(R.string.login))
                            Log.d("user", account.email.toString())
                            createUserPrefs(it.result.user)
                            mainIntent()
                        } else { Utils.Toast(this, getString(R.string.error)) }
                    }
                }
            } catch (e: ApiException) { Utils.Toast(this, "API Error") }
        }
    }

    /**
     * Metodo para iniciar sesion con una cuenta de google guardada en el dispositivo
     */
    private fun onClickGoogleLogIn() {
        val googleConf: GoogleSignInOptions =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build()
        val googleClient: GoogleSignInClient = GoogleSignIn.getClient(this, googleConf)
        Log.d("usuario", "0")
        googleClient.signOut()
        startActivityForResult(googleClient.signInIntent, Utils.RC_SIGN_IN)
    }

    /**
     * Método para iniciar sesión cuando YA TIENES un usuario CREADO en la BD
     */
    private fun loginWithEmail() {
        //Comprueba si el email está vacío
        if (txtEmail.text.trim().isNotEmpty()) {
            //Comprueba si la contraseña está vacía
            if (txtPass.text.trim().isNotEmpty()) {
                //Inicio de sesión con el correo y la contraseña
                auth.signInWithEmailAndPassword(
                    txtEmail.text.toString(),
                    txtPass.text.toString()
                ).addOnSuccessListener {
                    Utils.Toast(this, getString(R.string.ok_credentials))
                    createUserPrefs(it.user)
                    mainIntent()
                }.addOnFailureListener {
                    Utils.Toast(this, getString(R.string.bad_credentials))
                }
            } else { txtPass.setError(getString(R.string.required)) }
        } else { txtEmail.setError(getString(R.string.required)) }
    }

    /**
     * Método para enviar al usuario a la MainActivity
     */
    private fun mainIntent() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    /**
     * Metodo que guarda en el archivo de configuracion los datos de la sesion de un usuario
     */
    fun createUserPrefs(user: FirebaseUser?) {
        val prefs =
            getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()

        prefs.putString("name", user?.displayName)
        prefs.putString("email", user?.email)
        prefs.putString("UID", user?.uid)
        prefs.apply()
    }
}