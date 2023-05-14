package si.uni_lj.fri.pbd.gobar

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class RecyclerAdapter(private val shrooms: List<MushroomDetailsModel>?) : RecyclerView.Adapter<RecyclerAdapter.CardViewHolder?>() {




inner class CardViewHolder (itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
    var itemImage: ImageView? = null
    var itemTitle: TextView? = null
    var itemDetail: TextView? = null
    var itemCount: TextView? = null

    init {
        Log.d("mjauu", "helo")
        itemImage = itemView?.findViewById(R.id.item_image)
        itemTitle = itemView?.findViewById(R.id.item_title)
        itemDetail = itemView?.findViewById(R.id.item_detail)
        itemCount = itemView?.findViewById(R.id.item_count)
    }
}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        // have a CardViewHolder created when needed
        val view = LayoutInflater.from(parent.context).inflate(R.layout.shrooms_layout, parent, false)


        return CardViewHolder(view)
    }

    override fun getItemCount(): Int {
        return shrooms?.size ?: 0
    }

    override fun onBindViewHolder(viewHolder: CardViewHolder, i: Int) {
        Log.d("gobe", shrooms?.get(i)?.commonName.toString())

        if(shrooms == null) return
        if(shrooms[i].isDiscovered == 0) {
                viewHolder.itemView.setBackgroundColor(ContextCompat.getColor(viewHolder.itemView.context, R.color.disabled))
                viewHolder.itemView.isClickable = false
                viewHolder.itemView.alpha = 0.7f
            }
//        viewHolder.itemImage?.setImageResource(shrooms[i].image)
        Glide.with(viewHolder.itemView.context).load(shrooms[i].image).into(viewHolder.itemImage!!)
        viewHolder.itemTitle?.text = shrooms[i].commonName
        viewHolder.itemDetail?.text = shrooms[i].edibility
        viewHolder.itemCount?.text = shrooms[i].numFound.toString()
    }

}
