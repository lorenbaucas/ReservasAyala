package com.reservasayala.utils

import android.content.Context
import android.widget.Toast
import com.reservasayala.R
import com.reservasayala.databinding.ActivityMainBinding

/**
 * Clase de utilidades que proporciona funciones útiles para diversas operaciones en la aplicación.
 */
class Utils {

    /**
     * Objeto companion que proporciona acceso a métodos y propiedades estáticas.
     */
    companion object {

        /**
         * Referencia al objeto de enlace de la actividad principal.
         */
        lateinit var binding: ActivityMainBinding

        /**
         * Código de resultado para iniciar sesión.
         */
        const val RC_SIGN_IN = 9001

        /**
         * Método para mostrar un mensaje Toast.
         * @param cont Contexto en el que se muestra el Toast.
         * @param sms Mensaje a mostrar en el Toast.
         */
        fun Toast(cont: Context, sms: String) {
            Toast.makeText(cont, sms, Toast.LENGTH_SHORT).show()
        }

        fun getPreferences(cont: Context): String {
            val prefs = cont.applicationContext.getSharedPreferences(
                cont.getString(R.string.prefs_file),  // Nombre del archivo de preferencias
                Context.MODE_PRIVATE  // Modo de acceso a las preferencias
            )
            return prefs.getString("UID", "").toString()
        }
    }
}