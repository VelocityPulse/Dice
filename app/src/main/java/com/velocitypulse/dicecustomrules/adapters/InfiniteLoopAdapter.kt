package com.velocitypulse.dicecustomrules.adapters

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class InfiniteLoopAdapter<VH : RecyclerView.ViewHolder>(
    val recycler: RecyclerView
) : RecyclerView.Adapter<VH>() {

    init {
        recycler.layoutManager?.scrollToPosition(Integer.MAX_VALUE / 2)
    }

    @Deprecated("Use InfiniteLoop methods")
    override fun onBindViewHolder(holder: VH, position: Int) {
        val itemPos = position % getItemCountInfiniteLoop()
        onBindViewHolderInfiniteLoop(holder, itemPos)
    }

    abstract fun onBindViewHolderInfiniteLoop(holder: VH, position: Int)

    @Deprecated("Use InfiniteLoop methods")
    override fun getItemCount(): Int {
        return if (getItemCountInfiniteLoop() > 0) Integer.MAX_VALUE else 0
    }

    abstract fun getItemCountInfiniteLoop(): Int

    fun notifyDataSetChangedInfiniteLoop() {
        if (getItemCountInfiniteLoop() < 1)
            return

        notifyDataSetChanged()
        val calculatedScroll =
            (Integer.MAX_VALUE / 2) - ((Integer.MAX_VALUE / 2) % getItemCountInfiniteLoop())

        if (recycler.layoutManager is LinearLayoutManager)
            (recycler.layoutManager as LinearLayoutManager)
                .scrollToPositionWithOffset(calculatedScroll, 0)
    }

    abstract class ViewHolder(view: View): RecyclerView.ViewHolder(view)

}