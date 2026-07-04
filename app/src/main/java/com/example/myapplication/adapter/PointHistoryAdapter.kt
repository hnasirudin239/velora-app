package com.example.myapplication.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.model.PointHistory

class PointHistoryAdapter(
    private val histories: List<PointHistory>
) : RecyclerView.Adapter<PointHistoryAdapter.HistoryViewHolder>() {

    class HistoryViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        val flIcon: FrameLayout = itemView.findViewById(R.id.fl_icon)
        val ivIcon: ImageView = itemView.findViewById(R.id.iv_icon)
        val tvTitle: TextView = itemView.findViewById(R.id.tv_title)
        val tvDate: TextView = itemView.findViewById(R.id.tv_date)
        val tvPoints: TextView = itemView.findViewById(R.id.tv_points)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_point_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = histories[position]

        holder.tvTitle.text = item.title
        holder.tvDate.text = item.date

        // Set icon dan warna berdasarkan tipe
        when (item.type) {
            "earn" -> {
                holder.flIcon.background = holder.itemView.context.getDrawable(R.drawable.bg_history_earn)
                holder.ivIcon.setImageResource(R.drawable.ic_star)
                holder.ivIcon.setColorFilter(android.graphics.Color.parseColor("#047857"))
                holder.tvPoints.text = "+${item.points}"
                holder.tvPoints.setTextColor(android.graphics.Color.parseColor("#047857"))
            }
            "spend" -> {
                holder.flIcon.background = holder.itemView.context.getDrawable(R.drawable.bg_history_spend)
                holder.ivIcon.setImageResource(R.drawable.ic_shopping_cart)
                holder.ivIcon.setColorFilter(android.graphics.Color.parseColor("#DC2626"))
                holder.tvPoints.text = "-${kotlin.math.abs(item.points)}"
                holder.tvPoints.setTextColor(android.graphics.Color.parseColor("#6B7280"))
            }
            "expire" -> {
                holder.flIcon.background = holder.itemView.context.getDrawable(R.drawable.bg_history_expire)
                holder.ivIcon.setImageResource(R.drawable.ic_order_history)
                holder.ivIcon.setColorFilter(android.graphics.Color.parseColor("#B45309"))
                holder.tvPoints.text = "-${kotlin.math.abs(item.points)}"
                holder.tvPoints.setTextColor(android.graphics.Color.parseColor("#B45309"))
            }
        }
    }

    override fun getItemCount(): Int = histories.size
}