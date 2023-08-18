package si.uni_lj.fri.pbd.gobar

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide


class RecyclerAdapter(private val shrooms: List<MushroomDetailsModel>?) : RecyclerView.Adapter<RecyclerAdapter.CardViewHolder?>() {




inner class CardViewHolder (itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
    var itemImage: ImageView? = null
    var itemTitle: TextView? = null
    var itemDetail: TextView? = null
    var itemCount: TextView? = null
    var countImg: ImageView? = null
    var edible: ImageView? = null
    var cardView :CardView? = null

    init {
        Log.d("mjauu", "helo")
        itemImage = itemView?.findViewById(R.id.item_image)
        itemTitle = itemView?.findViewById(R.id.item_title)
        itemDetail = itemView?.findViewById(R.id.item_detail)
        itemCount = itemView?.findViewById(R.id.item_count)
        countImg = itemView?.findViewById(R.id.count)
        edible = itemView?.findViewById(R.id.edible)
        cardView = itemView?.findViewById(R.id.card_view)
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
        Glide.with(viewHolder.itemView.context).load(shrooms[i].image).into(viewHolder.itemImage!!)

        viewHolder.edible?.setImageResource(R.drawable.mushrooms)
        if(shrooms[i].edibility == "poisonous") {
            viewHolder.countImg?.setImageResource(R.drawable.devil)
        }
        else if(shrooms[i].edibility == "edible") {
            viewHolder.countImg?.setImageResource(R.drawable.pica)
        }
        else {
            viewHolder.countImg?.setImageResource(R.drawable.silverware)
        }

        if(shrooms[i].isDiscovered == 0) {
            viewHolder.cardView?.setCardBackgroundColor(ContextCompat.getColor(viewHolder.itemView.context, R.color.disabled))

            viewHolder.itemView.isClickable = false
            viewHolder.itemView.alpha = 0.7f
            viewHolder.itemImage?.setBackgroundColor(ContextCompat.getColor(viewHolder.itemView.context, R.color.disabled))


            viewHolder.itemImage?.colorFilter = ColorMatrixColorFilter(ColorMatrix().apply { setSaturation(0f) })
            viewHolder.countImg?.colorFilter = ColorMatrixColorFilter(ColorMatrix().apply { setSaturation(0f) })
            viewHolder.edible?.colorFilter = ColorMatrixColorFilter(ColorMatrix().apply { setSaturation(0f) })


        }else{
            viewHolder.cardView?.setCardBackgroundColor(ContextCompat.getColor(viewHolder.itemView.context, R.color.bananaMania))
            viewHolder.itemView.isClickable = true
            viewHolder.itemView.alpha = 1.0f
            viewHolder.itemImage?.colorFilter = ColorMatrixColorFilter(ColorMatrix().apply { setSaturation(1f) })
            viewHolder.countImg?.colorFilter = ColorMatrixColorFilter(ColorMatrix().apply { setSaturation(1f) })
            viewHolder.edible?.colorFilter = ColorMatrixColorFilter(ColorMatrix().apply { setSaturation(1f) })
        }



        viewHolder.itemTitle?.text = shrooms[i].latinName
        viewHolder.itemDetail?.text = shrooms[i].edibility
        viewHolder.itemCount?.text = shrooms[i].numFound.toString()
    }


}
