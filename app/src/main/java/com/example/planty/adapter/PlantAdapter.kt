package com.example.planty.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.planty.data.Plant
import com.example.planty.databinding.ItemPlantCardBinding

class PlantAdapter(
    private val plants: List<Plant>,
    private val onPlantClick: (Plant) -> Unit
) : RecyclerView.Adapter<PlantAdapter.PlantViewHolder>() {

    inner class PlantViewHolder(private val binding: ItemPlantCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(plant: Plant) {
            binding.webPlantStream.settings.javaScriptEnabled = true
            binding.webPlantStream.settings.useWideViewPort = true
            binding.webPlantStream.settings.loadWithOverviewMode = true
            binding.webPlantStream.webViewClient = android.webkit.WebViewClient()
            binding.webPlantStream.loadUrl("https://planty.gaeun.xyz/image_raw") // TODO: Use plant-specific URL if available

            binding.tvPlantName.text = plant.name
            binding.tvPlantStatus.text = plant.status

            binding.root.setOnClickListener {
                onPlantClick(plant)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantViewHolder {
        val binding = ItemPlantCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PlantViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlantViewHolder, position: Int) {
        holder.bind(plants[position])
    }

    override fun getItemCount(): Int = plants.size
} 