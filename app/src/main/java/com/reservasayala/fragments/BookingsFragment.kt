package com.reservasayala.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.reservasayala.R
import com.reservasayala.adapters.ViewPagerAdapter

/**
 * Fragmento para mostrar las reservas (appointments) del usuario.
 */
class BookingsFragment : Fragment() {
    // Declaración de variables
    private lateinit var tabs: TabLayout
    private lateinit var vpBookings: ViewPager2
    private lateinit var pageAdapter: ViewPagerAdapter

    /**
     * Método llamado para crear y devolver la vista asociada al fragmento.
     * @param inflater El objeto LayoutInflater que se utilizará para inflar la vista.
     * @param container El ViewGroup al que se adjuntará la vista.
     * @param savedInstanceState El estado previamente guardado de este fragmento, si existe.
     * @return La vista asociada al fragmento.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infla el layout para este fragmento
        return inflater.inflate(R.layout.fragment_appointments, container, false)
    }

    /**
     * Método llamado una vez que la vista del fragmento ha sido creada.
     * @param view La vista inflada del fragmento.
     * @param savedInstanceState El estado previamente guardado de este fragmento, si existe.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tabs = view.findViewById(R.id.tabLayoutBookings)
        vpBookings = view.findViewById(R.id.vpBookings)

        // Inicializa el adaptador de páginas (viewPager)
        pageAdapter = ViewPagerAdapter(parentFragmentManager, viewLifecycleOwner.lifecycle)
        vpBookings.adapter = pageAdapter

        // Agrega un listener a las pestañas del TabLayout
        tabs.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(p0: TabLayout.Tab?) {
                if (p0 != null) {
                    // Cambia la página actual del ViewPager cuando se selecciona una pestaña
                    vpBookings.currentItem = p0.position
                }
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
                // Método vacío, no se realiza ninguna acción cuando se deselecciona una pestaña
            }

            override fun onTabReselected(p0: TabLayout.Tab?) {
                // Método vacío, no se realiza ninguna acción cuando se vuelve a seleccionar una pestaña
            }
        })

        // Registra un callback para detectar el cambio de página en el ViewPager
        vpBookings.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                // Selecciona la pestaña correspondiente al cambiar de página en el ViewPager
                tabs.selectTab(tabs.getTabAt(position))
            }
        })
    }
}