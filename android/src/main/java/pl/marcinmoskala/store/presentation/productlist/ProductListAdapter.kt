package pl.marcinmoskala.store.presentation.productlist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import pl.marcinmoskala.store.R
import pl.marcinmoskala.store.model.Product

class ProductListAdapter(private val context: Context, var products: List<Product> = listOf()) : BaseAdapter() {
    private val appearInterpolator = DecelerateInterpolator()

    val newItemsTable = (1..products.size).map { true }.toMutableList()

    override fun getCount(): Int
            = products.size ?: 0

    override fun getItem(position: Int) =
        products[position].toString()

    override fun getItemId(position: Int): Long =
        products[position].hashCode().toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View
        = convertView?: LayoutInflater.from(context).inflate(R.layout.product_list_item, parent, false)
    .apply {
        val product = products[position]
        (findViewById(R.id.productTitle) as TextView).text = product.name
        (findViewById(R.id.productPrice) as TextView).text = product.price.toString()

        val imageView = findViewById(R.id.productImage) as ImageView
        loadProductImage(imageView, product)

        if(newItemsTable[position]){
            newItemsTable[position] = false
            showAnimation(parent)
        }
    }

    private fun View.showAnimation(parent: ViewGroup) {
        val density = context.resources.displayMetrics.density
        translationY = 60 * density
        rotationX = 12f
        scaleX = 1.1f
        pivotY = 180 * density
        pivotX = (parent.width / 2).toFloat()
        animate()
                .translationY(0f)
                .rotationX(0f)
                .scaleX(1f)
                .setDuration(450)
                .setInterpolator(appearInterpolator).start()
    }

    private fun loadProductImage(imageView: ImageView, product: Product) =
        Glide.with(context)
                .load(product.imageUrl)
                .fitCenter()
                .into(imageView)

}