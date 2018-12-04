package eduardoflores.com.inappdemo

import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_row.view.*

class ListAdapter(private val dataset: List<AvailableItem>): RecyclerView.Adapter<ListAdapter.ViewHolder>() {

    class ViewHolder(private val row: ConstraintLayout): RecyclerView.ViewHolder(row) {
        val itemName = row.item_name_tv
        val itemDescription = row.item_description_tv
        val itemPrice = row.item_price
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListAdapter.ViewHolder {
        val row = LayoutInflater.from(parent.context).inflate(R.layout.item_row, parent, false)
                as ConstraintLayout

        return ViewHolder(row)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val availableItem = dataset[position]
        holder.apply {
            itemName.text = availableItem.title
            itemDescription.text = availableItem.description
            itemPrice.text = availableItem.price
        }
    }

    override fun getItemCount() = dataset.size
}