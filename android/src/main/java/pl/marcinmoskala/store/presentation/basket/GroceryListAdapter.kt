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
import pl.marcinmoskala.store.basket
import pl.marcinmoskala.store.views.SwipableListItem
import pl.marcinmoskala.store.views.ViewSwipeTouchListener

class GroceryListAdapter(private val context: Context) : BaseAdapter() {
    override fun getItemId(position: Int): Long = position.toLong()
    override fun getItem(position: Int): Any = basket.orders[position].toString()
    override fun getCount(): Int = basket.size()

    override fun getView(position: Int, view: View?, parent: ViewGroup): View
        = (view ?: inflateView(parent)).apply {
        val order = basket.orders[position]

        (findViewById(R.id.productTitle) as TextView).text = order.product.name
        (findViewById(R.id.productPrice) as TextView).text = order.product.price.toString()
        (findViewById(R.id.productSize) as TextView).text = order.size.toString()

        val orderImage = findViewById(R.id.productImage) as ImageView
        orderImage.setImageResource(R.drawable.product_image)
        Glide.with(context).load(order.product.imageUrls[0]).into(orderImage)
    }

    private fun inflateView(parent: ViewGroup): View {
        val view = LayoutInflater.from(context).inflate(R.layout.basket_item, parent, false)
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
        return view
    }
}