package com.reservasayala.models

import android.icu.util.Calendar
import java.text.SimpleDateFormat

/**
     * Constructor secundario para inicializar una reserva con los parámetros proporcionados.
     * @param date Fecha de la reserva en formato dd/MM/yyyy.
     * @param descrip Descripción de la reserva.
     * @param hour Hora de la reserva en formato HH:mm.
     * @param uid Identificador único del profesional relacionado con la reserva.
     * @param site Ubicación de la reserva.
     * @param status Estado de la reserva.
     */
class Booking(){
    var id:String = ""
    var site:String = ""
    var status:String = ""
    var date:String = ""
    var hour:String = ""
    var profUID:String = ""
    var descrip:String = ""

    constructor(date:String, descrip:String, hour:String, uid:String, site:String, status:String) : this() {
        this.date = date
        this.descrip = descrip
        this.hour = hour
        this.profUID = uid
        this.site = site
        this.status = status
    }

    /**
     * Obtiene la fecha completa de la reserva en formato de calendario.
     * @return Objeto de calendario con la fecha y hora de la reserva.
     */
    fun getCompleteDate(): Calendar {
        val date = SimpleDateFormat("dd/MM/yyyy").parse(this.date)
        val dateHour = SimpleDateFormat("HH:mm").parse(this.hour)
        val calendar = Calendar.getInstance()
        calendar.setTime(date)

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        calendar.setTime(dateHour)

        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
        val minutes = calendar.get(Calendar.MINUTE)

        calendar.set(year, month, dayOfMonth, hourOfDay, minutes)
        return calendar
    }
}
