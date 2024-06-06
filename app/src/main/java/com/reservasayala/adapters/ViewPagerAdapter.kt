package com.reservasayala.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.reservasayala.fragments.HistoryBookingsFragment
import com.reservasayala.fragments.PendingBookingsFragment

/**
 * Adaptador para manejar la navegación entre fragmentos en un ViewPager2.
 * @property fragmentManager El FragmentManager utilizado para gestionar los fragmentos.
 * @property lifecycle El ciclo de vida asociado al adaptador.
 */
class ViewPagerAdapter(fragmentManager : FragmentManager, lifecycle : Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle) {

    /**
     * Obtiene el número total de fragmentos en el ViewPager2.
     * @return El número total de fragmentos.
     */
    override fun getItemCount(): Int {
        return 2
    }

    /**
     * Crea un nuevo fragmento en la posición especificada.
     * @param position La posición del fragmento en el ViewPager2.
     * @return El fragmento creado.
     */
    override fun createFragment(position: Int): Fragment {
        // Devuelve un fragmento diferente según la posición
        return if (position == 0) {
            PendingBookingsFragment() // Devuelve el fragmento de reservas pendientes para la posición 0
        } else {
            HistoryBookingsFragment() // Devuelve el fragmento de historial de reservas para otras posiciones
        }
    }
}