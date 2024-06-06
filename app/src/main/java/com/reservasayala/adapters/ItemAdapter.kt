package com.reservasayala.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.reservasayala.R

/**
 * Adaptador para mostrar una lista de elementos en un RecyclerView.
 * @property mList Lista de elementos a mostrar.
 */
class ItemAdapter(private var mList: ArrayList<String>) :
    RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    private lateinit var onItemClickListener: OnItemClickListener

    /**
     * Interfaz para manejar los clics en los elementos del RecyclerView.
     */
    interface OnItemClickListener {
        /**
         * Método llamado cuando se hace clic en un elemento del RecyclerView.
         * @param position La posición del elemento en la lista.
         */
        fun onItemClick(position: Int)
    }

    /**
     * Establece el listener para manejar los clics en los elementos del RecyclerView.
     * @param listener El listener para manejar los clics en los elementos.
     */
    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    /**
     * Crea una nueva instancia de ViewHolder.
     * @param parent El ViewGroup al que se añadirá la nueva vista.
     * @param viewType El tipo de la vista.
     * @return ViewHolder recién creado que contiene la vista inflada.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return ViewHolder(view)
    }

    /**
     * Vincula los datos del elemento en la posición especificada con la vista.
     * @param holder El ViewHolder que debe ser actualizado para representar el contenido del elemento.
     * @param position La posición del elemento dentro de los datos.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mList[position]
        holder.item.text = item
        // Establece el listener para manejar los clics en los elementos del RecyclerView
        holder.itemView.setOnClickListener {
            onItemClickListener.onItemClick(position)
        }
    }

    /**
     * Obtiene el número total de elementos en la lista.
     * @return El número total de elementos en la lista.
     */
    override fun getItemCount(): Int {
        return mList.size
    }

    /**
     * Clase ViewHolder que mantiene una referencia a la vista de cada elemento de la lista.
     * @param itemView La vista que representa cada elemento de la lista.
     */
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val item: TextView = itemView.findViewById(R.id.tvItem)
    }
}