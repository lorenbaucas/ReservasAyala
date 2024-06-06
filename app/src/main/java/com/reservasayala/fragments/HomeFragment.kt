package com.reservasayala.fragments

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import com.iamageo.library.BeautifulDialog
import com.iamageo.library.description
import com.iamageo.library.hideNegativeButton
import com.iamageo.library.onPositive
import com.iamageo.library.position
import com.iamageo.library.title
import com.iamageo.library.type
import com.reservasayala.R
import com.reservasayala.adapters.ItemAdapter
import com.reservasayala.models.Booking
import com.reservasayala.utils.FirebaseRD
import com.reservasayala.utils.Notification
import com.reservasayala.utils.NotificationData.message
import com.reservasayala.utils.NotificationData.title
import com.reservasayala.utils.Utils
import com.reservasayala.utils.channelID
import com.reservasayala.utils.notificationID
import android.icu.util.Calendar

/**
 * Fragmento principal que gestiona la creación y visualización de reservas.
 * Permite al usuario seleccionar una fecha, hora y descripción para realizar una reserva.
 * También gestiona la programación de notificaciones para recordar la reserva en diferentes momentos.
 */
class HomeFragment : Fragment() {

    private lateinit var btnReservas: Button
    private lateinit var calendario: CalendarView
    private lateinit var selectedCalendar: Calendar
    private lateinit var selectHour: TextView
    private lateinit var txtDescription: EditText
    private lateinit var dialog: BottomSheetDialog
    private lateinit var itemAdapter: ItemAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var list: ArrayList<String>
    private lateinit var tabSite: TabLayout
    private lateinit var tabTurno: TabLayout
    private lateinit var place: String
    private lateinit var date: String

    /**
     * Método llamado para crear la vista asociada al fragmento.
     * Infla el diseño de la interfaz de usuario desde el archivo XML `fragment_home.xml`.
     * @param inflater El inflador de diseño utilizado para inflar el diseño.
     * @param container El contenedor de la vista padre en el cual se inflará el diseño.
     * @param savedInstanceState Datos de estado previamente guardados de la actividad.
     * @return La vista inflada del fragmento o null si ocurrió un error.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    /**
     * Método llamado después de que la vista asociada al fragmento se haya creado.
     * Inicializa y configura los elementos de la interfaz de usuario y define los listeners necesarios.
     * @param view La vista que ha sido creada.
     * @param savedInstanceState Datos de estado previamente guardados de la actividad.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicialización de elementos de la interfaz de usuario
        btnReservas = view.findViewById(R.id.btn_booking)
        calendario = view.findViewById(R.id.calendarioView)
        selectHour = view.findViewById(R.id.txtSelectItem)
        txtDescription = view.findViewById(R.id.txtDescription)

        list = ArrayList()
        // Configuración de pestañas (TabLayout) y otras variables
        tabSite = view.findViewById(R.id.tabSite)
        tabSite.getTabAt(0)?.select()
        place = getString(R.string.library)

        tabTurno = view.findViewById(R.id.tabTurno)
        tabTurno.getTabAt(0)?.select()

        // Obtención de la fecha actual y configuración del calendario
        selectedCalendar = Calendar.getInstance()
        calendario.minDate = selectedCalendar.timeInMillis // Establecer la fecha mínima como la fecha actual
        val monthSelected = selectedCalendar.get(Calendar.MONTH)
        val year = selectedCalendar.get(Calendar.YEAR)

        date = "${selectedCalendar.get(Calendar.DAY_OF_MONTH)}/${monthSelected + 1}/${year}"

        list = FirebaseRD().getOfferedHours(
            requireContext(),
            date,
            tabTurno.selectedTabPosition,
            place
        )
        /**
         * Establece un listener de cambio de fecha al objeto calendario.
         * Cuando se cambia la fecha en el calendario, ejecuta la lógica correspondiente.
         */
        calendario.setOnDateChangeListener { view, year, month, dayOfMonth ->

            // Establece la fecha seleccionada en el Calendar
            selectedCalendar.set(year, month, dayOfMonth)
            // Formatea la fecha seleccionada en el formato dd/MM/yyyy y la asigna a la variable date
            date = "${dayOfMonth}/${month + 1}/${year}"
            list = FirebaseRD().getOfferedHours(
                requireContext(),
                date,
                tabTurno.selectedTabPosition,
                place
            )
        }

        /**
         * Agrega un listener de selección de pestañas al objeto tabSite.
         * Cuando se selecciona una pestaña, se actualiza el lugar correspondiente.
         */
        tabSite.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            /**
             * Método que se llama cuando se selecciona una pestaña.
             * Actualiza el lugar correspondiente según la posición de la pestaña seleccionada.
             * @param tab La pestaña seleccionada.
             */
            override fun onTabSelected(tab: TabLayout.Tab?) {
                // Verifica si la posición de la pestaña seleccionada es la primera (0)
                if (tab?.position == 0) {
                    // Actualiza el lugar con el valor de la cadena "library" de los recursos de la aplicación
                    place = getString(R.string.library)
                    list = FirebaseRD().getOfferedHours(
                        requireContext(),
                        date,
                        tabTurno.selectedTabPosition,
                        place
                    )
                } else {
                    // Actualiza el lugar con el valor de la cadena "hall" de los recursos de la aplicación
                    place = getString(R.string.hall)
                    list = FirebaseRD().getOfferedHours(
                        requireContext(),
                        date,
                        tabTurno.selectedTabPosition,
                        place
                    )
                }
            }

            /**
             * Método que se llama cuando se deselecciona una pestaña.
             * @param tab La pestaña deseleccionada.
             */
            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            /**
             * Método que se llama cuando se vuelve a seleccionar una pestaña.
             * @param tab La pestaña reseleccionada.
             */
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        /**
         * Agrega un listener de selección de pestañas al objeto tabTurno.
         * Cuando se selecciona una pestaña, se actualiza la lista de elementos
         * y se limpia el texto del objeto selectHour.
         */
        tabTurno.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            /**
             * Método que se llama cuando se selecciona una pestaña.
             * @param tab La pestaña seleccionada.
             */
            override fun onTabSelected(tab: TabLayout.Tab?) {
                // Limpia la lista de elementos
                list.clear()
                // Verifica si la posición de la pestaña seleccionada es la primera (0)
                if (tab?.position == 0) {
                    // Obtiene el array de strings correspondiente al turno de día
                    list = FirebaseRD().getOfferedHours(requireContext(), date, 0, place)
                    // Limpia el texto del objeto selectHour
                    selectHour.setText("")
                } else {
                    // Obtiene el array de strings correspondiente al turno de tarde
                    list = FirebaseRD().getOfferedHours(requireContext(), date, 1, place)
                    // Limpia el texto del objeto selectHour
                    selectHour.setText("")
                }
            }

            /**
             * Método que se llama cuando se deselecciona una pestaña.
             * @param tab La pestaña deseleccionada.
             */
            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            /**
             * Método que se llama cuando se vuelve a seleccionar una pestaña.
             * @param tab La pestaña reseleccionada.
             */
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        /**
         * Establece un OnClickListener para el objeto selectHour.
         * Cuando se hace clic en selectHour, se abre un diálogo de hoja inferior
         * que muestra una lista de elementos. El usuario puede seleccionar un elemento
         * de la lista y este se mostrará en el objeto selectHour.
         */
        selectHour.setOnClickListener {
            // Inflar el diseño del diálogo de hoja inferior
            val dialogView = layoutInflater.inflate(R.layout.bottom_sheet, null)
            // Crear una instancia de BottomSheetDialog
            dialog = BottomSheetDialog(requireContext())
            // Establecer el contenido del diálogo de hoja inferior
            dialog.setContentView(dialogView)
            // Encontrar el RecyclerView dentro del diseño del diálogo
            recyclerView = dialogView.findViewById(R.id.rvItem)
            // Crear un adaptador de elementos para la lista de elementos proporcionada
            itemAdapter = ItemAdapter(list)
            // Establecer el adaptador en el RecyclerView
            recyclerView.adapter = itemAdapter
            // Mostrar el diálogo de hoja inferior
            dialog.show()

            // Establecer un Listener para manejar los clics en los elementos del RecyclerView
            itemAdapter.setOnItemClickListener(object : ItemAdapter.OnItemClickListener {
                /**
                 * Método que se llama cuando se hace clic en un elemento del RecyclerView.
                 * @param position La posición del elemento seleccionado en la lista.
                 */
                override fun onItemClick(position: Int) {
                    // Obtener el elemento seleccionado del array
                    val elementoSeleccionado = list.get(position)
                    // Establecer el elemento seleccionado en el objeto selectHour
                    selectHour.text = elementoSeleccionado
                    // Cerrar el diálogo de hoja inferior
                    dialog.dismiss()
                }
            })
        }

        /**
         * Establece un OnClickListener asignado al botón de reservas (btnReservas).
         * Se activa cuando se hace clic en el botón de reservas y realiza varias validaciones antes de crear una reserva.
         * Verifica si se ha seleccionado una hora y si se ha ingresado una descripción.
         * Si no se ha seleccionado ninguna fecha en el calendario, obtiene la fecha actual.
         * Luego, verifica si la fecha y la hora seleccionadas ya están en la base de datos.
         * Si la fecha y la hora ya están reservadas, muestra un mensaje de error.
         * Si no hay conflictos, crea la reserva, programa las notificaciones y crea un canal de notificaciones.
         * Si ocurre algún error durante el proceso, muestra un mensaje de error.
         */
        btnReservas.setOnClickListener {
            if (selectHour.text.isNotEmpty()) {
                selectHour.setError(null)
                val description = txtDescription.text.toString().trim()
                if (description.isNotEmpty()) {
                    FirebaseRD().checkDate(date, selectHour.text.toString(), place) { resultado ->
                        if (resultado == 0) {
                            // La fecha y hora no están en la base de datos
                            FirebaseRD().crearDatos(getBooking())

                            title = description
                            message =
                                "$place - ${selectedCalendar.get(Calendar.DAY_OF_MONTH)}/${
                                    selectedCalendar.get(Calendar.MONTH)
                                }/${selectedCalendar.get(Calendar.YEAR)} - ${selectHour.text}"

                            scheduleNotifications()
                            createNotificationChannel()

                            // Mostrar un cuadro de diálogo para las alarmas programadas
                            BeautifulDialog.build(requireActivity())
                                .title(title, titleColor = R.color.black)
                                .description(message, color = R.color.black)
                                .type(type = BeautifulDialog.TYPE.SUCCESS)
                                .position(BeautifulDialog.POSITIONS.CENTER)
                                .hideNegativeButton(hide = true)
                                .onPositive(text = getString(R.string.accept), shouldIDismissOnClick = true) {}

                        } else { Utils.Toast(requireContext(), getString(R.string.reserv_error)) }
                    }
                } else { txtDescription.setError(getString(R.string.required)) }
            } else { selectHour.setError(getString(R.string.required)) }
        }
    }

    private fun scheduleNotifications() {
        // Programar alarma 1 semana antes
        scheduleNotification(-7 * 24 * 3600 * 1000) // Restar 1 semana

        // Programar alarma 3 días antes
        scheduleNotification(-3 * 24 * 3600 * 1000) // Restar 3 días

        // Programar alarma 1 día antes
        scheduleNotification(-1 * 24 * 3600 * 1000) // Restar 1 día

        // Programar alarma 1 hora antes
        scheduleNotification(-3600 * 1000) // Restar 1 hora
    }

    /**
     * Este método programa una notificación para ser enviada en un momento específico.
     * Recibe un parámetro `timeDeltaMillis` que representa la cantidad de tiempo (en milisegundos) antes del evento para programar la notificación.
     * Crea un intent para la clase Notification que manejará la recepción de la notificación.
     * Asigna el título y el mensaje de la notificación utilizando los datos proporcionados por el usuario y la selección del calendario.
     * Crea un PendingIntent para el intent de la notificación.
     * Obtiene el servicio de AlarmManager del contexto y programa la notificación para el tiempo específico usando `setExactAndAllowWhileIdle`.
     * Muestra un cuadro de diálogo con el título y el mensaje de la notificación para cada alarma programada.
     */
    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleNotification(timeDeltaMillis: Long) {
        val intent = Intent(requireContext(), Notification::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            notificationID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val timeInMillis = getTime() + timeDeltaMillis // Agregar el tiempo de anticipación

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            timeInMillis,
            pendingIntent
        )
    }


    /**
     * Obtiene el tiempo en milisegundos correspondiente a la fecha completa de la reserva.
     * @return El tiempo en milisegundos de la fecha completa de la reserva.
     */
    private fun getTime(): Long {
        val calendar: Calendar?
        // Obtiene la fecha completa de la reserva
        val book = getBooking()
        calendar = book.getCompleteDate()

        // Devuelve el tiempo en milisegundos de la fecha completa de la reserva
        return calendar.timeInMillis
    }

    /**
     * Obtiene los detalles de la reserva a partir de la información ingresada por el usuario.
     * @return Objeto Booking que contiene los detalles de la reserva, como fecha, descripción, hora, lugar, estado, etc.
     */
    private fun getBooking(): Booking {
        // Crea un objeto Booking con los detalles de la reserva
        return Booking(
            date,  // Fecha de la reserva
            txtDescription.text.toString().trim(),  // Descripción de la reserva
            selectHour.text.toString(),  // Hora de la reserva
            Utils.getPreferences(requireContext()),  // UID del usuario que realiza la reserva
            place,  // Lugar de la reserva (ejemplo: biblioteca, sala)
            "Pendiente"  // Estado de la reserva (ejemplo: Pendiente)
        )
    }

    /**
     * Crea un canal de notificación para mostrar notificaciones relacionadas con las reservas.
     */
    private fun createNotificationChannel() {
        // Obtiene el nombre del canal de recursos de cadena
        val name = getString(R.string.bookings)

        // Crea un nuevo canal de notificación con el ID del canal, el nombre y la importancia predeterminada
        val channel = NotificationChannel(channelID, name, NotificationManager.IMPORTANCE_DEFAULT)

        // Obtiene el administrador de notificaciones del contexto actual
        val notificationManager =
            requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Crea el canal de notificación utilizando el administrador de notificaciones
        notificationManager.createNotificationChannel(channel)
    }

}