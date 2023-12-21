package edu.arizona.cast.hannaarnold.glucosemonitor

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController

import androidx.recyclerview.widget.LinearLayoutManager
import edu.arizona.cast.hannaarnold.glucosemonitor.databinding.FragmentGlucoseListBinding
import kotlinx.coroutines.launch
import java.util.*

private const val TAG = "GlucoseListFragment"

class GlucoseListFragment : Fragment() {

    private var _binding: FragmentGlucoseListBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }
    private val glucoseListViewModel: GlucoseListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.new_glucose -> {
                showNewGlucose()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showNewGlucose() {
        viewLifecycleOwner.lifecycleScope.launch {
            val newGlucose = Glucose(
                date = Date(),
                fasting = 0,
                breakfast = 0,
                lunch = 0,
                dinner = 0,
            )
            glucoseListViewModel.addGlucose(newGlucose)
            findNavController().navigate(
                GlucoseListFragmentDirections.showGlucoseDetail(newGlucose.date)
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGlucoseListBinding.inflate(inflater, container, false)

        binding.glucoseRecyclerView.layoutManager = LinearLayoutManager(context)

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                glucoseListViewModel.glucoses.collect { glucoses ->
                    binding.glucoseRecyclerView.adapter =
                        GlucoseListAdapter(glucoses) { glucoseId ->
                            findNavController().navigate(
                                GlucoseListFragmentDirections.showGlucoseDetail(glucoseId)
                            )
                        }
                }
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_glucose_list, menu)
    }

}

