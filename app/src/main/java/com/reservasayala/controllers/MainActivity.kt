package com.reservasayala.controllers

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.reservasayala.R
import com.reservasayala.databinding.ActivityMainBinding
import com.reservasayala.fragments.BookingsFragment
import com.reservasayala.fragments.HomeFragment
import com.reservasayala.fragments.ProfileFragment
import com.reservasayala.utils.Utils

/**
 * Actividad principal que gestiona la navegación entre fragmentos y configura el comportamiento de la interfaz de usuario.
 */
class MainActivity : AppCompatActivity() {
    /**
     * Método llamado cuando se crea la actividad.
     * @param savedInstanceState El estado de la instancia guardada si la actividad se recrea después de ser destruida.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Habilita el modo de borde a borde para la actividad
        enableEdgeToEdge()

        // Infla el layout de la actividad utilizando el enlace de datos y lo establece como contenido de la actividad
        Utils.binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(Utils.binding.root)

        // Reemplaza el fragmento inicial con el fragmento de inicio (HomeFragment)
        replaceFragment(HomeFragment())

        // Establece un listener para el BottomNavigationView
        Utils.binding.bottomNavigation.setOnItemSelectedListener {
            // Reemplaza el fragmento actual con el fragmento correspondiente según la opción seleccionada en el BottomNavigationView
            when (it.itemId) {
                R.id.navigation_home -> replaceFragment(HomeFragment())
                R.id.navigation_appointments -> replaceFragment(BookingsFragment())
                R.id.navigation_profile -> replaceFragment(ProfileFragment())
                else -> {}
            }
            true
        }
    }

    /**
     * Método llamado cuando se presiona el botón de retroceso.
     */
    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.frame_layout)

        // Si el fragmento actual es BookingsFragment o ProfileFragment, cambia al fragmento HomeFragment
        if (currentFragment is BookingsFragment || currentFragment is ProfileFragment) {
            replaceFragment(HomeFragment())
            // Cada vez que se presiona el botón de retroceso, se cambia el elemento seleccionado en el BottomNavigationView al elemento de inicio
            Utils.binding.bottomNavigation.selectedItemId = R.id.navigation_home
        } else { super.onBackPressed() }
    }

    /**
     * Reemplaza el fragmento actual en el contenedor (frame_layout) con el fragmento proporcionado.
     * @param fragment El fragmento que se va a mostrar.
     */
    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}