package uz.falconmobile.taxifood.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import uz.falconmobile.taxifood.R
import uz.falconmobile.taxifood.adapter.HistoryAdapter
import uz.falconmobile.taxifood.databinding.ActivityHistoryBinding
import uz.falconmobile.taxifood.db.models.history_model
import uz.falconmobile.taxifood.db.utilits.HisotiryDatabaseHelper

class HistoryActivity : AppCompatActivity() {

    private lateinit var historyAdapter: HistoryAdapter
    private var historyList = ArrayList<history_model>()
    private lateinit var history:HisotiryDatabaseHelper
    private lateinit var binding: ActivityHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        history = HisotiryDatabaseHelper(this)

        historyList=history.getAllHistory()
        setupRecyclerView()

        // Load your data here (example)
//        loadHistoryData(history.getAllHistory())
    }

    private fun setupRecyclerView() {
        historyAdapter = HistoryAdapter(this, historyList)
        binding.rvOrderHistory.layoutManager = LinearLayoutManager(this)
        binding.rvOrderHistory.adapter = historyAdapter
    }

    private fun loadHistoryData(list:ArrayList<history_model>) {
        // Replace with actual data loading logic

        historyList=list

        // Notify adapter of data change
        historyAdapter.updateList(historyList)
    }
}
