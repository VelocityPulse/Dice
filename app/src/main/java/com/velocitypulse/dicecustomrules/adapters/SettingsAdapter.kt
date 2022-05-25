package com.velocitypulse.dicecustomrules.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.velocitypulse.dicecustomrules.R
import com.velocitypulse.dicecustomrules.models.entity.SettingsProfile

class SettingsAdapter(
    val context: Context,
    var settingsProfileList: MutableList<SettingsProfile>,
    val cardViewClickListener: (SettingsProfile) -> Unit,
    val cardViewLongClickListener: (SettingsProfile) -> Boolean,
    val editButtonClickListener: (SettingsProfile) -> Unit
) : RecyclerView.Adapter<SettingsAdapter.ProfileViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_profile, parent, false)

        return ProfileViewHolder(itemView as CardView)
    }

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        holder.bind(
            settingsProfileList.get(position),
            cardViewClickListener, cardViewLongClickListener, editButtonClickListener
        )
    }

    override fun getItemCount(): Int {
        return settingsProfileList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setProfileList(profileList: MutableList<SettingsProfile>) {
        settingsProfileList = profileList
        notifyDataSetChanged()
    }

    fun insertProfile(profile: SettingsProfile, position: Int) {
        // TODO test implementation (position)
        settingsProfileList.add(position, profile)
        notifyItemInserted(position)
    }

    fun notifyItemChanged(profile: SettingsProfile) {
        notifyItemChanged(settingsProfileList.indexOf(profile))
    }

    fun notifyItemInserted(profile: SettingsProfile) {
        notifyItemInserted(settingsProfileList.indexOf(profile))
    }

    inner class ProfileViewHolder(val cardView: CardView) : RecyclerView.ViewHolder(cardView) {

        val textView: TextView = cardView.findViewById(R.id.title)
        val editButton: ImageButton = cardView.findViewById(R.id.edit_button)

        fun bind(
            profile: SettingsProfile,
            cardViewClickListener: (SettingsProfile) -> Unit,
            cardViewLongClickListener: (SettingsProfile) -> Boolean,
            editButtonClickListener: (SettingsProfile) -> Unit
        ) {
            textView.text = profile.title
            cardView.setOnClickListener { cardViewClickListener(profile) }
            cardView.setOnLongClickListener { cardViewLongClickListener(profile) }
            editButton.setOnClickListener { editButtonClickListener(profile) }

            var colorItems: Int
            var colorBackground: Int
            context.apply {
                if (profile.isSelected) {
                    colorItems = getColor(R.color.colorPrimaryDark)
                    colorBackground = getColor(R.color.colorPrimaryLight)
                } else {
                    colorItems = getColor(android.R.color.black)
                    colorBackground = getColor(android.R.color.white)
                }
            }
            editButton.backgroundTintList = ColorStateList.valueOf(colorItems)
            textView.setTextColor(colorItems)
            cardView.setCardBackgroundColor(ColorStateList.valueOf(colorBackground))
        }
    }
}