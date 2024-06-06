package com.reservasayala.controllers

import android.icu.util.Calendar
import android.os.Bundle
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import com.iamageo.library.BeautifulDialog
import com.iamageo.library.description
import com.iamageo.library.onNegative
import com.iamageo.library.onPositive
import com.iamageo.library.position
import com.iamageo.library.title
import com.iamageo.library.type
import com.reservasayala.R
import com.reservasayala.adapters.ItemAdapter
import com.reservasayala.models.Booking
import com.reservasayala.utils.FirebaseRD
import com.reservasayala.utils.Utils

/**
 * Actividad para editar una reserva existente.
 */
class EditBookingActivity : AppCompatActivity() {
    /**
     * Método llamado cuando se crea la actividad.
     */

    private lateinit var btUpdateBooking: Button
    private lateinit var btCancelEdit: Button
    private lateinit var calendar: CalendarView
    private lateinit var selectHour: TextView
    private lateinit var txtDescription: EditText
    private lateinit var tabSite: TabLayout
    private lateinit var tabTurno: TabLayout

    private lateinit var bookingReceived: Booking

    private lateinit var dialog: BottomSheetDialog

    private lateinit var list: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Establece el diseño de la actividad como activity_edit_booking.xml
        setContentView(R.layout.activity_edit_booking)
        // Ajusta el padding de la vista principal para que coincida con los márgenes de la barra de sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btUpdateBooking = findViewById(R.id.btAceptarEdit)
        btCancelEdit = findViewById(R.id.btCancelarEdit)
        calendar = findViewById(R.id.CalendarEdit)
        selectHour = findViewById(R.id.txtHourEdit)
        txtDescription = findViewById(R.id.txtDescriptionEdit)

        tabSite = findViewById(R.id.tabSiteEdit)
        tabTurno = findViewById(R.id.tabTurno)
        bookingReceived = Booking()

        //Se coge la cita a actualizar pasada por el intent
        if (intent.hasExtra("bid")) {
            bookingReceived.id = intent.getStringExtra("bid").toString()
            bookingReceived.status = intent.getStringExtra("bstatus").toString()
            bookingReceived.site = intent.getStringExtra("bsite").toString()
            bookingReceived.date = intent.getStringExtra("bdate").toString()
            bookingReceived.hour = intent.getStringExtra("bhour").toString()
            bookingReceived.profUID = intent.getStringExtra("bprofuid").toString()
            bookingReceived.descrip = intent.getStringExtra("bdescrip").toString()
        }
        list = ArrayList()

        //Los valores de los campos de la vista se establecen a los de la cita recogida
        calendar.setDate(bookingReceived.getCompleteDate().timeInMillis)
        txtDescription.setText(bookingReceived.descrip)
        selectHour.text = bookingReceived.hour

        if (bookingReceived.site == R.string.hall.toString()){
            tabSite.getTabAt(1)?.select()
        }else{
            tabSite.getTabAt(0)?.select()
        }

        if (resources.getStringArray(R.array.hora_dia).contains(selectHour.text)){
            list.addAll(resources.getStringArray(R.array.hora_dia))
            tabTurno.getTabAt(0)?.select()

        }else{
            list.addAll(resources.getStringArray(R.array.hora_tarde))
            tabTurno.getTabAt(1)?.select()
        }

        val selectedCalendar = Calendar.getInstance()
        calendar.minDate = selectedCalendar.timeInMillis

        //Listener para el calendarView
        calendar.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, month, dayOfMonth)
            bookingReceived.date = "${dayOfMonth}/${month + 1}/${year}"
        }

        //listener del tabLayout de lugar
        tabSite.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab?.position == 0) {
                    bookingReceived.site = getString(R.string.library)
                } else {
                    bookingReceived.site = getString(R.string.hall)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        //listener del tabLayout de turno
        tabTurno.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                list.clear()
                val stringsArray: Array<String>
                //Depende de la posicion pulsada se rellena el array de horas
                if (tab?.position == 0) {
                    stringsArray = resources.getStringArray(R.array.hora_dia)
                    list.addAll(stringsArray)
                    selectHour.text = ""
                } else {
                    stringsArray = resources.getStringArray(R.array.hora_tarde)
                    list.addAll(stringsArray)
                    selectHour.text = ""
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        //Muestra una bottomSheet con las horas
        selectHour.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.bottom_sheet, null)
            dialog = BottomSheetDialog(this)
            dialog.setContentView(dialogView)
            val recyclerView:RecyclerView = dialogView.findViewById(R.id.rvItem)
            val itemAdapter = ItemAdapter(list)
            recyclerView.adapter = itemAdapter
            dialog.show()

            //Adaptador que permite coger la hora elegida
            itemAdapter.setOnItemClickListener(object : ItemAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    val selectedArray: Array<String>
                    if (tabTurno.selectedTabPosition == 0) {
                        selectedArray = resources.getStringArray(R.array.hora_dia)
                    } else {
                        selectedArray = resources.getStringArray(R.array.hora_tarde)
                    }
                    // Obtén el elemento correspondiente al array de strings y la posición
                    val elementoSeleccionado = selectedArray[position]
                    // Haz lo que necesites con el elemento seleccionado, como mostrarlo en un TextView
                    selectHour.text = elementoSeleccionado
                    bookingReceived.hour = elementoSeleccionado
                    dialog.dismiss()
                }
            })
        }

        //Boton cancelar cita. Cierra la actividad
        btCancelEdit.setOnClickListener { this.finish() }

        //Boton para actualizar la cita seleccionada
        btUpdateBooking.setOnClickListener {
            BeautifulDialog.build(this)
                .title(getString(R.string.are_you_sure), titleColor = R.color.black)
                .description("")
                .type(type = BeautifulDialog.TYPE.NONE)
                .position(BeautifulDialog.POSITIONS.CENTER)
                .onPositive(text = getString(R.string.accept), shouldIDismissOnClick = true) {
                    FirebaseRD().checkDate(bookingReceived.date, bookingReceived.hour, bookingReceived.site) { resultado ->
                        if (resultado == 0) {
                            bookingReceived.descrip = txtDescription.text.toString()
                            //Si se pulsa si se borra la cita del array y de la base de datos
                            FirebaseRD().editBooking(bookingReceived)
                            Utils.Toast(this, getString(R.string.updated_appointment))
                            dialog.dismiss()
                            this.finish()
                        }
                    }
                }
                .onNegative(text = getString(R.string.cancel)) {}
        }
    }
}