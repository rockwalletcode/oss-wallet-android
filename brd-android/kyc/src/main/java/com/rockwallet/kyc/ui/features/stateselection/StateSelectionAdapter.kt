package com.rockwallet.kyc.ui.features.stateselection

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rockwallet.kyc.data.model.CountryState
import com.rockwallet.kyc.databinding.ListItemStateBinding

class StateSelectionAdapter(private val callback: (CountryState) -> Unit) :
    ListAdapter<CountryState, StateSelectionAdapter.ViewHolder>(
        CountryDiffCallback
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListItemStateBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(
            state = getItem(position),
            callback = callback
        )
    }

    class ViewHolder(val binding: ListItemStateBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(state: CountryState, callback: (CountryState) -> Unit) {
            with(binding) {
                tvTitle.text = state.name
                root.setOnClickListener { callback(state) }
            }
        }
    }

    object CountryDiffCallback : DiffUtil.ItemCallback<CountryState>() {
        override fun areItemsTheSame(oldItem: CountryState, newItem: CountryState) =
            oldItem.code == newItem.code

        override fun areContentsTheSame(oldItem: CountryState, newItem: CountryState) =
            oldItem.name == newItem.name
    }
}