package com.kotkot.usuario.noticiasrss

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.kotkot.usuario.noticiasrss.modelo.*

/**
 * Created by usuario on 15/02/18.
 */

class FeederViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener
{
    var tituloTV:TextView
    var fechaTV:TextView
    var contenidoTV:TextView

    private var itemClickListener : ItemClickListenerInterface?=null

    init
    {
        tituloTV = itemView.findViewById(R.id.tituloTV)
        fechaTV = itemView.findViewById(R.id.fechaTV)
        contenidoTV = itemView.findViewById(R.id.contenidoTV)

        itemView.setOnClickListener(this)
        itemView.setOnLongClickListener(this)
    }

    fun setItemClickListener(itemClickListenerInterface: ItemClickListenerInterface)
    {
        this.itemClickListener = itemClickListenerInterface
    }

    override fun onClick(p0: View?)
    {
        itemClickListener!!.onClick(p0, adapterPosition, false)
    }

    override fun onLongClick(p0: View?): Boolean
    {
        itemClickListener!!.onClick(p0, adapterPosition, true)
        return true
    }
}

class FeederRecyclerAdapter (private val rootObject: RootObject, private val contexto:Context) : RecyclerView.Adapter<FeederViewHolder>()
{
    private val layoutInflater:LayoutInflater

    init
    {
        layoutInflater = LayoutInflater.from(contexto)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): FeederViewHolder
    {
        val itemView = layoutInflater.inflate(R.layout.recycler_item, parent, false)
        return FeederViewHolder(itemView)
    }

    override fun getItemCount(): Int
    {
        return rootObject.items.size
    }

    override fun onBindViewHolder(holder: FeederViewHolder, position: Int)
    {
        holder.tituloTV.text = rootObject.items[position].title
        holder.fechaTV.text = rootObject.items[position].pubDate
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
            holder.contenidoTV.text = Html.fromHtml(rootObject.items[position].content, Html.FROM_HTML_MODE_LEGACY)
        else
            holder.contenidoTV.text = Html.fromHtml(rootObject.items[position].content)

        holder.setItemClickListener(ItemClickListenerInterface { view, pos, isLong ->
            if(isLong)
            {

            }
            else
            {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(rootObject.items[position].link))
                contexto.startActivity(intent)
            }
        })
    }

}
