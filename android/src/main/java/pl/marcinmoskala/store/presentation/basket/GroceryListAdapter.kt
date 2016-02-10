package pl.marcinmoskala.store.presentation.basket

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import com.bumptech.glide.Glide
import pl.marcinmoskala.store.R
import pl.marcinmoskala.store.api.Rest
import pl.marcinmoskala.store.basket
import pl.marcinmoskala.store.model.Basket
import pl.marcinmoskala.store.views.SwipableListItem
import pl.marcinmoskala.store.views.ViewSwipeTouchListener

class GroceryListAdapter(private val context: Context) : BaseAdapter() {
    override fun getItemId(position: Int): Long = position.toLong()
    override fun getItem(position: Int): Any = basket.orders[position].toString()
    override fun getCount(): Int = basket.size()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val order = basket.orders[position]

        var view: View? = convertView // re-use an existing view, if one is available
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.basket_item, parent, false)
            val swipper = (view as SwipableListItem).swipeListener
            val finalView = view
            swipper!!.addEventListener(object : ViewSwipeTouchListener.EventListener {
                override fun onSwipeGestureBegin() {
                    parent.requestDisallowInterceptTouchEvent(true)
                }

                override fun onSwipeGestureEnd() {
                    parent.requestDisallowInterceptTouchEvent(false)
                }

                override fun onItemSwipped() {
                    // If view has already been processed, do nothing
                    if (finalView.getParent() == null) {
                        return
                    }
                    val p = (parent as ListView).getPositionForView(finalView)
                    basket.remove(p)
                    notifyDataSetChanged()
                }
            })
        }

        (view.findViewById(R.id.productTitle) as TextView).text = order.product.name
        (view.findViewById(R.id.productPrice) as TextView).text = order.product.price.toString()
        (view.findViewById(R.id.productSize) as TextView).text = order.size.toString()
        val orderImage = view.findViewById(R.id.productImage) as ImageView
        orderImage.setImageResource(R.drawable.product_image)

        Glide.with(context).load(order.product.imageUrls[0]).into(orderImage)
        return view
    }
}