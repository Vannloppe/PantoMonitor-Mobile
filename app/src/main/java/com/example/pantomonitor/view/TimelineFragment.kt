package com.example.pantomonitor.view
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pantomonitor.databinding.FragmentTimelineBinding
import com.example.pantomonitor.viewmodel.BdMainViewModel
import com.example.pantomonitor.viewmodel.BdViewModelFactoy
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TimelineFragment : Fragment(),DatePickerDialog.OnDateSetListener {

    private lateinit var binding: FragmentTimelineBinding
    private lateinit var viewModel: BdMainViewModel
    private lateinit var Adapter: RecylerViewTimeline






    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTimelineBinding.inflate(inflater, container, false)  // Inflate using ViewBinding
        viewModel = ViewModelProvider(this, BdViewModelFactoy()).get(BdMainViewModel::class.java)


        Adapter = RecylerViewTimeline(emptyList())
        binding.detailsviewtl.setHasFixedSize(true)
        binding.detailsviewtl.adapter = Adapter
        binding.detailsviewtl.layoutManager = LinearLayoutManager(requireContext())


        viewModel.dataList.observe(viewLifecycleOwner) { newData ->
            // Update the adapter when the data changes
            Adapter = newData?.let { RecylerViewTimeline(it) }!!
            binding.detailsviewtl.adapter = Adapter

            Adapter.notifyDataSetChanged()
        }



        var btnDatePicker = binding.Monthdate
        binding.Monthdate.text = getCurrentMonthAsString()


        btnDatePicker.setOnClickListener {
            showDatePickerDialog()

        }

        return binding.root
    }


    fun getCurrentMonthAsString(): String {
        val calendar = Calendar.getInstance()
        val monthFormat = SimpleDateFormat("MMMM", Locale.getDefault())
        return monthFormat.format(calendar.time)
    }





    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireActivity(), this, year, month, day)
        datePickerDialog.show()
    }


    override fun onDateSet(view: android.widget.DatePicker?, year: Int, month: Int, dayOfMonth: Int) {

        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)
       calendar.set(Calendar.DAY_OF_MONTH, 0)


        var startDatemon = calendar.time.toString()
        var startmon = viewModel.getunixtimestamp(startDatemon)
        calendar.add(Calendar.MONTH, 1)
        calendar.add(Calendar.DAY_OF_MONTH, -1)

        var endDatemon = calendar.time.toString()
        var endmon = viewModel.getunixtimestamp(endDatemon)


        var monthh = SimpleDateFormat("MMMM", Locale.getDefault())
        var monthint = calendar.get(Calendar.MONTH) + 1
        var monthhh = monthh.format(calendar.time)


        binding.Monthdate.text = monthhh
        viewModel.updateQuery(startmon,endmon)
        Adapter.clearList()
    }

}


