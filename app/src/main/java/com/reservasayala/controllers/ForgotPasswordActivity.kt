package com.reservasayala.controllers

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.reservasayala.R
import com.reservasayala.utils.Utils

/**
 * Actividad para restablecer la contraseña del usuario.
 */
class ForgotPasswordActivity : AppCompatActivity() {
    // Declaración de variables
    private lateinit var auth: FirebaseAuth
    private lateinit var btnResetPassword: Button
    private lateinit var btnBack: Button
    private lateinit var txtRecoverEmail: EditText

    /**
     * Método llamado cuando se crea la actividad.
     * @param savedInstanceState El estado de la instancia guardada si la actividad se recrea después de ser destruida.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        // Inicializa FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Encuentra y asigna vistas a variables
        txtRecoverEmail = findViewById(R.id.txtRecuperarEmail)
        btnResetPassword = findViewById(R.id.btn_ressetPassword)
        btnBack = findViewById(R.id.btn_back)

        // Establece un OnClickListener para el botón de restablecimiento de contraseña
        btnResetPassword.setOnClickListener {
            // Verifica si el campo de correo electrónico no está vacío
            if (txtRecoverEmail.text.toString().trim().isNotEmpty()) {
                // Envía un correo electrónico de restablecimiento de contraseña al correo electrónico proporcionado
                auth.sendPasswordResetEmail(txtRecoverEmail.text.toString())
                // Muestra un mensaje de notificación para informar al usuario que se ha enviado un correo electrónico de restablecimiento de contraseña
                Utils.Toast(this, getString(R.string.notification_resetPassword))
            }
        }

        // Establece un OnClickListener para el botón de retroceso
        btnBack.setOnClickListener { finish() }
    }
}