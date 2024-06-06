package com.reservasayala.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.iamageo.library.BeautifulDialog
import com.iamageo.library.description
import com.iamageo.library.onNegative
import com.iamageo.library.onPositive
import com.iamageo.library.position
import com.iamageo.library.title
import com.iamageo.library.type
import com.reservasayala.R
import com.reservasayala.controllers.EditBookingActivity
import com.reservasayala.models.Booking
import com.reservasayala.utils.FirebaseRD

//Adaptador del RecyclerView de AppointmentsFragment
class RecyclerBookingsAdapter(bookings: ArrayList<Booking>) : RecyclerView.Adapter<RecyclerBookingsAdapter.ViewHolder>(){

    //Declaracion de variables
    val bookings: ArrayList<Booking>
    lateinit var context: Context

    //Constructor con 1 parametro requerido (Arraylist de citas)
    init {
        this.bookings = bookings
    }

    //Se crea nuestra vista personalizada card_layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        this.context = parent.context
        val v = LayoutInflater.from(context).inflate(R.layout.card_layout, parent, false)
        return ViewHolder(v)
    }

    // // Obtiene el número total de elementos en la lista de citas
    override fun getItemCount(): Int {
        return this.bookings.size
    }


    //Se asigna a cada componente del card_layout su valor correspondiente
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val booking : Booking = this.bookings[position]

        // Cambia el color del texto del estado según el estado de la reserva
        if (booking.status == "Pendiente") {
            holder.txtStatus.setTextColor(Color.GREEN)
        } else {
            holder.txtStatus.setTextColor(Color.WHITE)
        }
        holder.txtStatus.text = booking.status
        holder.txtSite.text = booking.site
        holder.txtDate.text = booking.date
        holder.txtHour.text = booking.hour
        holder.txtDescrip.text = booking.descrip
    }

    //Clase interna del adaptador que representa cada card_layout creado
    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), View.OnLongClickListener{
        //Declaracion de variables
        var txtStatus: TextView
        var txtSite: TextView
        var txtDate: TextView
        var txtHour: TextView
        var txtDescrip: TextView

        init {
            // Inicializa los componentes del card_layout
            txtStatus = itemView.findViewById(R.id.txtAppointStatus)
            txtSite = itemView.findViewById(R.id.txtAppointSite)
            txtDate = itemView.findViewById(R.id.txtAppointDate)
            txtHour = itemView.findViewById(R.id.txtAppointHour)
            txtDescrip = itemView.findViewById(R.id.txtAsunto)
            // Establece el listener para manejar los clics largos en los elementos del RecyclerView
            itemView.setOnLongClickListener(this)
        }

        // Maneja los clics largos en los elementos del RecyclerView
        override fun onLongClick(v: View?): Boolean {
            val position = absoluteAdapterPosition
            val booking = bookings[position]
            var isPendiente = false
            if (booking.status == "Pendiente"){
                isPendiente = true
            }
            // Muestra un menú emergente al realizar un clic largo en el elemento del RecyclerView
            val popup = PopupMenu(context, v)
            popup.inflate(R.menu.appo_context_menu)
            popup.setForceShowIcon(true)

            if (!isPendiente){
                popup.menu.removeItem(R.id.action_edit)
            }
            popup.setOnMenuItemClickListener {

                when(it.itemId){
                    // Si se selecciona la opción de edición, se envian los datos a la actividad de editar
                    R.id.action_edit->{

                        val intent = Intent(context, EditBookingActivity::class.java)
                        intent.putExtra("bid", booking.id)
                        intent.putExtra("bstatus", booking.status)
                        intent.putExtra("bsite", booking.site)
                        intent.putExtra("bprofuid", booking.profUID)
                        intent.putExtra("bdate", booking.date)
                        intent.putExtra("bhour", booking.hour)
                        intent.putExtra("bdescrip", booking.descrip)
                        context.startActivity(intent)
                        true
                    }
                    R.id.action_delete->{
                        //Se abre el dialogo de confirmación de borrado
                        BeautifulDialog.build(context as Activity)
                            .title(context.getString(R.string.delete_booking), titleColor = R.color.black)
                            .description(context.getString(R.string.are_you_sure), color = R.color.black)
                            .type(type = BeautifulDialog.TYPE.ALERT)
                            .position(BeautifulDialog.POSITIONS.CENTER)
                            .onPositive(text = context.getString(R.string.accept), shouldIDismissOnClick = true) {
                                bookings.removeAt(position)
                                FirebaseRD().deleteBooking(booking)
                                notifyItemRemoved(position)
                            }
                            .onNegative(text = context.getString(android.R.string.cancel)) {}
                        true
                    }
                    else-> true
                }
            }
            popup.show()
            return true
        }
    }
}