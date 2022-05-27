package com.velocitypulse.dicecustomrules.adapters

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.velocitypulse.dicecustomrules.core.LogManager

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
        try {
            notifyDataSetChanged()
            val layoutManager = recycler.layoutManager as LinearLayoutManager

            val currentPos = layoutManager.findFirstCompletelyVisibleItemPosition()
            val step =
                (Integer.MAX_VALUE / 2) - ((Integer.MAX_VALUE / 2) % getItemCountInfiniteLoop())

            if (currentPos < 2) {
                if (layoutManager.getChildAt(0) == null)
                    executeScroll(step, true)
            } else
                executeScroll(step, false)

        } catch (th: Throwable) {
            LogManager.error(th.stackTraceToString())
        }
    }

    fun executeScroll(position: Int, shouldPostOnRecycler: Boolean) {
        val layoutManager = recycler.layoutManager as LinearLayoutManager

        if (shouldPostOnRecycler) {
            recycler.post {
                layoutManager.scrollToPositionWithOffset(
                    position,
                    calculateDtToFit(layoutManager.getChildAt(0))
                )
            }
        } else {
            layoutManager.scrollToPositionWithOffset(
                position,
                calculateDtToFit(layoutManager.getChildAt(0))
            )
        }
    }

    fun calculateDtToFit(viewHolder: View?): Int {
        if (viewHolder == null)
            return 0

        return ((recycler.height - viewHolder.height) / 2) - (recycler.verticalFadingEdgeLength / 2)
    }

    abstract class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

}