package com.reservasayala.utils

import android.content.Context
import android.icu.util.Calendar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.reservasayala.R
import com.reservasayala.models.Booking

/**
 * Clase de utilidad para interactuar con la base de datos Firebase Realtime Database.
 */
class FirebaseRD {

    // Referencia a la ubicación "Appointments" en la base de datos Firebase
    private var reference = FirebaseDatabase.getInstance().getReference("Appointments")

    /**
     * Método para eliminar una reserva de la base de datos.
     * @param booking La reserva a eliminar.
     */
    fun deleteBooking(booking: Booking) {
        val bookingToDelete = reference.child(booking.id)
        bookingToDelete.removeValue()
            .addOnSuccessListener {
                // Éxito al eliminar la reserva
            }
            .addOnFailureListener { error ->
                // Error al eliminar la reserva
            }
    }

    /**
     * Compara la fecha de una reserva con la fecha actual y actualiza el estado de la reserva si es necesario.
     * @param booking La reserva a verificar.
     */
    fun checkAppoDate(booking: Booking) {
        val bookingToCheck = reference.child(booking.id)
        val appoCalendar = booking.getCompleteDate()
        val actualCalendar = Calendar.getInstance()

        if (appoCalendar.compareTo(actualCalendar) == -1) {
            // Si la fecha y hora de la reserva han pasado, actualiza el estado de la reserva a "Finalizado"
            booking.status = "Finalizado"
            // Actualiza la reserva en la base de datos
            bookingToCheck.child("status").setValue("Finalizado")
                .addOnSuccessListener {
                    // Éxito al actualizar el estado de la reserva
                }
                .addOnFailureListener { error ->
                    // Error al actualizar el estado de la reserva
                }
        }
    }

    // Interfaz para manejar la carga de reservas desde Firebase
    interface FirebaseCallback {
        fun onBookingsLoaded(bookings: ArrayList<Booking>)
    }

    /**
     * Lee las reservas pendientes de un usuario desde la base de datos Firebase.
     * @param userUID El ID del usuario.
     * @param callback El callback que se invocará cuando se carguen las reservas.
     */
    fun readPendingBoookings(userUID: String, callback: FirebaseCallback) {
        val query = reference.orderByChild("profUID").equalTo(userUID)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val bookings = ArrayList<Booking>()
                for (result in snapshot.children) {
                    val booking = result.getValue(Booking::class.java)
                    if (booking != null) {
                        booking.id = result.key.toString()
                        checkAppoDate(booking)
                        if (booking.status == "Pendiente") {
                            bookings.add(booking)
                        }
                    }
                }
                callback.onBookingsLoaded(bookings)
            }

            override fun onCancelled(error: DatabaseError) {
                // Cancelación de la operación de lectura de Firebase
            }
        })
    }

    /**
     * Lee el historial de reservas de un usuario desde la base de datos Firebase.
     * @param userUID El ID del usuario.
     * @param callback El callback que se invocará cuando se carguen las reservas.
     */
    fun readBookingsHistory(userUID: String, callback: FirebaseCallback) {
        val query = reference.orderByChild("profUID").equalTo(userUID)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val bookings = ArrayList<Booking>()
                for (result in snapshot.children) {
                    val booking = result.getValue(Booking::class.java)
                    if (booking != null) {
                        booking.id = result.key.toString()
                        bookings.add(booking)
                    }
                }
                callback.onBookingsLoaded(bookings)
            }

            override fun onCancelled(error: DatabaseError) {
                // Cancelación de la operación de lectura de Firebase
            }
        })
    }

    /**
     * Actualiza una reserva en la base de datos.
     * @param b La reserva actualizada.
     */
    fun editBooking(b: Booking) {
        val data: Map<String, Any> = mapOf(
            "date" to b.date,
            "hour" to b.hour,
            "site" to b.site,
            "descrip" to b.descrip
        )

        val query = reference.child(b.id)
        query.updateChildren(data)
            .addOnSuccessListener {
                // Éxito al editar la reserva
            }
            .addOnFailureListener { error ->
                // Error al editar la reserva
            }
    }

    /**
     * Elimina el historial de reservas de un usuario de la base de datos Firebase.
     * @param userUID El UID del usuario cuyo historial de reservas se eliminará.
     */
    fun deleteBookingsHistory(userUID: String) {
        // Crea una consulta para obtener las reservas del usuario con el UID proporcionado
        val query = reference.orderByChild("profUID").equalTo(userUID)
        // Añade un listener para obtener los datos una sola vez
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Itera sobre cada reserva obtenida
                for (result in snapshot.children) {
                    // Obtiene la reserva actual
                    val booking = result.getValue(Booking::class.java)
                    // Verifica si la reserva no es nula
                    if (booking != null) {
                        // Asigna el ID de la reserva
                        booking.id = result.key.toString()
                        // Verifica si la reserva está pendiente
                        if (booking.status == "Pendiente") { 
                          // No se elimina
                        } else {
                            // Si la reserva no está pendiente, se elimina
                            val bookingToDelete = reference.child(booking.id)
                            // Elimina la reserva de la base de datos
                            bookingToDelete.removeValue()
                                .addOnSuccessListener {
                                    // Éxito al eliminar la reserva
                                }
                                .addOnFailureListener { error ->
                                    // Error al eliminar la reserva
                                }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejo de errores en caso de que la operación sea cancelada
            }
        })
    }

    /**
     * Método que crea la reserva del usuario en la base de datos.
     * @param booking La reserva a crear.
     */
    fun crearDatos(booking: Booking) {
        // Crea un mapa de datos para la reserva
        val datosReserva: MutableMap<String, Any> = HashMap()
        datosReserva["date"] = booking.date
        datosReserva["descrip"] = booking.descrip
        datosReserva["hour"] = booking.hour
        datosReserva["profUID"] = booking.profUID
        datosReserva["site"] = booking.site
        datosReserva["status"] = "Pendiente"

        // Agrega la reserva a la base de datos
        reference.push().setValue(datosReserva)
    }

    /**
     * Método que comprueba si un día y una hora ya están reservados en la base de datos.
     * @param dia El día a comprobar.
     * @param h La hora a comprobar.
     * @param p El lugar a comprobar.
     * @param callback El callback que se invocará con el resultado de la comprobación.
     */
    fun checkDate(dia: String, h: String, p: String, callback: (Int) -> Unit) {
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var fechaHoraEncontrada = false
                snapshot.children.forEach { dataSnapshot ->
                    val booking: Booking? = dataSnapshot.getValue(Booking::class.java)
                    val fecha: String? = booking?.date
                    val hora: String? = booking?.hour
                    val sitio: String? = booking?.site

                    if (dia == fecha && h == hora) {
                        if (p == sitio) {
                            // Si la fecha y la hora están en la base de datos
                            fechaHoraEncontrada = true
                            return@forEach // Salimos del bucle
                        }
                    }
                }
                // Llamar al callback con 1 si se encuentra la fecha y la hora, 0 en caso contrario
                callback(if (fechaHoraEncontrada) 1 else 0)
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejo de errores
                // Llamar al callback con -1 para indicar que hubo un error
                callback(-1)
            }
        })
    }

    fun getOfferedHours(c : Context, date : String, turn : Int, site : String) : ArrayList<String>{
        val query = reference.orderByChild("date").equalTo(date)
        val list : ArrayList<String> = ArrayList()

        if (turn == 0){
            list.addAll(c.resources.getStringArray(R.array.hora_dia))
        }else if (turn == 1){
            list.addAll(c.resources.getStringArray(R.array.hora_tarde))
        }else{
            list.addAll(c.resources.getStringArray(R.array.hora_dia))
            list.addAll(c.resources.getStringArray(R.array.hora_tarde))
        }

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (result in snapshot.children) {
                    val booking = result.getValue(Booking::class.java)
                    if (booking != null) {
                        if (booking.site == site){
                            if (list.contains(booking.hour)) {
                                list.remove(booking.hour)
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Cancelación de la operación de lectura de Firebase
            }
        })
        return list
    }
}