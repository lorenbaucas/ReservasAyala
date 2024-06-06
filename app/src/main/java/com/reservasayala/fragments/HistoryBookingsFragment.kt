package com.reservasayala.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.reservasayala.R
import com.reservasayala.adapters.RecyclerBookingsAdapter
import com.reservasayala.models.Booking
import com.reservasayala.utils.FirebaseRD

/**
 * Fragmento para mostrar el historial de reservas del usuario.
 */
class HistoryBookingsFragment : Fragment(), FirebaseRD.FirebaseCallback {
    private lateinit var recViewH: RecyclerView
    private lateinit var userUID: String
    private lateinit var adapterBh: RecyclerBookingsAdapter

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
        return inflater.inflate(R.layout.fragment_history_bookings, container, false)
    }

    /**
     * Método llamado una vez que la vista del fragmento ha sido creada.
     * @param view La vista inflada del fragmento.
     * @param savedInstanceState El estado previamente guardado de este fragmento, si existe.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recViewH = view.findViewById(R.id.rvHistoryBookings)

        // Obtiene el UID de la sesión actual desde las preferencias compartidas
        val prefs = requireContext().getSharedPreferences(
            getString(R.string.prefs_file),
            Context.MODE_PRIVATE
        )
        userUID = prefs.getString("UID", "").toString()

        // Carga las reservas del historial del usuario
        loadBookings()

        // Configura el RecyclerView con un LinearLayoutManager
        recViewH.layoutManager = LinearLayoutManager(requireContext())
    }

    /**
     * Método para cargar las reservas del historial del usuario desde Firebase.
     */
    private fun loadBookings() {
        FirebaseRD().readBookingsHistory(userUID, this)
    }

    /**
     * Método de callback llamado cuando las reservas del historial se cargan correctamente desde Firebase.
     * @param bookings La lista de reservas del historial cargadas desde Firebase.
     */
    override fun onBookingsLoaded(bookings: ArrayList<Booking>) {
        // Muestra las reservas del historial en el RecyclerView
        showBookings(bookings)
    }

    /**
     * Método para mostrar las reservas del historial en el RecyclerView.
     * @param bookingsP La lista de reservas del historial a mostrar.
     */
    private fun showBookings(bookingsP: ArrayList<Booking>) {
        // Inicializa el adaptador del RecyclerView y lo asigna al RecyclerView
        adapterBh = RecyclerBookingsAdapter(bookingsP)
        recViewH.adapter = adapterBh
        adapterBh.notifyDataSetChanged()
    }
}