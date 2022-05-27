package com.velocitypulse.dicecustomrules.adapters

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.velocitypulse.dicecustomrules.R

open class DiceDescriptionAdapter(
    val context: Context,
    recyclerView: RecyclerView,
    var descriptionMap: MutableMap<Int, String>,
    var listSize: Int,
    val onTextEdit: OnTextEditListener
) : InfiniteLoopAdapter<DiceDescriptionAdapter.DescriptionViewHolder>(recyclerView) {

    private val TAG: String = "DICE DESCRIPTION ADAPTER"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DescriptionViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dice_description, parent, false)

        return DescriptionViewHolder(itemView)
    }

    override fun onBindViewHolderInfiniteLoop(holder: DescriptionViewHolder, position: Int) {
        holder.apply {
            if (descriptionMap.containsKey(position)) {
                bind(descriptionMap[position]!!, position)
            } else {
                bind("", position)
            }
        }
    }

    override fun onViewRecycled(holder: DescriptionViewHolder) {
        holder.unBind()
        super.onViewRecycled(holder)
    }

    override fun getItemCountInfiniteLoop(): Int {
        return listSize
    }

    fun interface OnTextEditListener {
        fun onTextEdit(description: String, position: Int)
    }

    inner class DescriptionViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        val descriptionEditText: EditText = view.findViewById(R.id.description_edit_text)
        val diceNumber: TextView = view.findViewById(R.id.dice_number)
        var textWatcher: TextWatcher? = null

        fun bind(description: String, itemPos: Int) {
            descriptionEditText.setText(description)
            descriptionEditText.hint = "Description n°${itemPos + 1}"
            diceNumber.text = (itemPos + 1).toString()

            textWatcher = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, strt: Int, cnt: Int, af: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    descriptionMap[itemPos] = s.toString()
                    onTextEdit.onTextEdit(s.toString(), itemPos)
                }

                override fun afterTextChanged(s: Editable?) {}
            }
            descriptionEditText.addTextChangedListener(textWatcher)
        }

        fun unBind() {
            textWatcher?.let {
                descriptionEditText.removeTextChangedListener(textWatcher)
            }
        }
    }
}