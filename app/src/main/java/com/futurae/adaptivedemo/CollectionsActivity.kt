package com.futurae.adaptivedemo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.futurae.adaptivedemo.databinding.ActivityCollectionsBinding
import com.futurae.adaptivedemo.databinding.ItemCollectionBinding
import com.futurae.sdk.adaptive.AdaptiveDbHelper
import com.futurae.sdk.adaptive.model.AdaptiveCollection
import com.google.gson.GsonBuilder
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.Calendar

class CollectionsActivity : AppCompatActivity() {

    private val collectionsAdapter = CollectionsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityCollectionsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@CollectionsActivity)
            adapter = collectionsAdapter
        }

        lifecycleScope.launch {
            AdaptiveDbHelper.INSTANCE.allCollections.collect {
                collectionsAdapter.submitList(it.toList())
            }
        }

    }
}

class CollectionsAdapter : ListAdapter<AdaptiveCollection, CollectionViewHolder>(
    object : DiffUtil.ItemCallback<AdaptiveCollection>() {
        override fun areItemsTheSame(oldItem: AdaptiveCollection, newItem: AdaptiveCollection): Boolean {
            return oldItem.timestamp == newItem.timestamp
        }

        override fun areContentsTheSame(oldItem: AdaptiveCollection, newItem: AdaptiveCollection): Boolean {
            return oldItem == newItem
        }
    }
) {

    private val dateFormat = DateFormat.getDateInstance()
    private val gson = GsonBuilder().setLenient().create()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionViewHolder {
        return CollectionViewHolder(
            ItemCollectionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CollectionViewHolder, position: Int) {
        val collection = getItem(position)
        val calendar = Calendar.getInstance().apply {
            timeInMillis = collection.timestamp * 1000
        }
        holder.binding.titleTextView.text = dateFormat.format(calendar.time)
        holder.binding.subtitleTextView.text = gson.toJson(collection)
    }

}

class CollectionViewHolder(val binding: ItemCollectionBinding) : RecyclerView.ViewHolder(binding.root)