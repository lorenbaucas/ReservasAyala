package com.reservasayala.controllers

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.reservasayala.R
import com.reservasayala.utils.Utils

/**
 * Actividad para el registro de nuevos usuarios.
 */
class SignUpActivity : AppCompatActivity() {
    // Declaración de variables
    private lateinit var txtNameUser: EditText
    private lateinit var txtEmail: EditText
    private lateinit var txtPass: EditText
    private lateinit var txtRepeatPass: EditText
    private lateinit var btnRegistrar: Button
    private lateinit var util: Utils
    private lateinit var auth: FirebaseAuth

    /**
     * Método llamado cuando se crea la actividad.
     * @param savedInstanceState El estado de la instancia guardada si la actividad se recrea después de ser destruida.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Inicializa las variables y asigna vistas a ellas
        btnRegistrar = findViewById(R.id.btn_register)
        txtEmail = findViewById(R.id.txtEmailRegister)
        txtNameUser = findViewById(R.id.txtUserName)
        txtPass = findViewById(R.id.txtPasswordReg)
        txtRepeatPass = findViewById(R.id.txtConfirmPassword)

        // Inicializa el objeto Utils y FirebaseAuth
        util = Utils()
        auth = FirebaseAuth.getInstance()

        // Establece un OnClickListener para el botón de registro
        btnRegistrar.setOnClickListener { registerWithEmail() }
    }

    /**
     * Método para registrar un nuevo usuario utilizando correo electrónico y contraseña.
     */
    private fun registerWithEmail() {
        // Comprueba si el correo electrónico no está vacío
        if (txtEmail.text.trim().isNotEmpty()) {
            // Comprueba si el nombre de usuario no está vacío
            if (txtNameUser.text.trim().isNotEmpty()) {
                // Comprueba si la contraseña no está vacía
                if (txtPass.text.trim().isNotEmpty()) {
                    // Comprueba si la confirmación de contraseña no está vacía
                    if (txtRepeatPass.text.trim().isNotEmpty()) {
                        // Comprueba si ambas contraseñas son iguales
                        if (txtPass.text.toString() == txtRepeatPass.text.toString()) {
                            // Crea un nuevo usuario utilizando el correo electrónico y la contraseña proporcionados
                            auth.createUserWithEmailAndPassword(
                                txtEmail.text.toString(),
                                txtPass.text.toString()
                            ).addOnSuccessListener {
                                val user = it.user
                                // Actualiza el perfil del usuario con el nombre proporcionado
                                val profileUpdates = UserProfileChangeRequest.Builder()
                                    .setDisplayName(txtNameUser.text.toString()).build()
                                user?.updateProfile(profileUpdates)
                                    ?.addOnCompleteListener { profileUpdateTask ->
                                        if (profileUpdateTask.isSuccessful) {
                                            // El perfil del usuario se actualizó correctamente
                                            Utils.Toast(
                                                this,
                                                getString(R.string.user) + user.displayName + getString(
                                                    R.string.created
                                                )
                                            )
                                            createUserPrefs(it.user)
                                            val intent = Intent(this, MainActivity::class.java)
                                            startActivity(intent)
                                            finish() // Finaliza la actividad actual después de iniciar sesión correctamente
                                        } else { Utils.Toast(this, getString(R.string.error)) }
                                    }
                            }.addOnFailureListener {
                                // Error al crear el usuario
                                Utils.Toast(this, getString(R.string.error))
                            }
                        } else {
                            // Las contraseñas no coinciden
                            txtPass.setError(getString(R.string.different_password))
                            txtRepeatPass.setError(getString(R.string.different_password))
                            Utils.Toast(this, getString(R.string.different_password))
                        }
                    } else {
                        // La confirmación de la contraseña está vacía
                        txtRepeatPass.setError(getString(R.string.required))
                    }
                } else {
                    // La contraseña está vacía
                    txtPass.setError(getString(R.string.required))
                }
            } else {
                // El nombre de usuario está vacío
                txtNameUser.setError(getString(R.string.required))
            }
        } else {
            // El correo electrónico está vacío
            txtEmail.setError(getString(R.string.required))
        }
    }

    /**
     * Crea y almacena las preferencias del usuario en el archivo de preferencias compartidas de la aplicación.
     * @param user El usuario actualmente autenticado en Firebase.
     */
    private fun createUserPrefs(user: FirebaseUser?) {
        // Obtiene una instancia del objeto SharedPreferences.Editor para realizar cambios en las preferencias compartidas
        val prefs =
            getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()

        // Almacena el nombre de usuario, el correo electrónico y el identificador único del usuario actual, si están disponibles
        // bajo las claves "name", "email" y "UID" respectivamente
        prefs.putString("name", user?.displayName)
        prefs.putString("email", user?.email)
        prefs.putString("UID", user?.uid)

        // Aplica los cambios en el editor de preferencias compartidas para escribir los datos en el archivo de preferencias compartidas de forma asíncrona
        prefs.apply()
    }
}