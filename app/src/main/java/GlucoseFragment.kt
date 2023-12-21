package edu.arizona.cast.hannaarnold.glucosemonitor

import android.content.Intent
import android.icu.text.MessageFormat.format
import android.os.Bundle
import android.provider.Settings.System.DATE_FORMAT
import android.text.format.DateFormat.format
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import edu.arizona.cast.hannaarnold.glucosemonitor.databinding.FragmentGlucoseBinding
import java.time.LocalDateTime
import android.widget.Button
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.launch
import java.lang.String.format
import android.text.format.DateFormat
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

private const val TAG = "GlucoseFragment"

class GlucoseFragment : Fragment() {
    private lateinit var dateButton: Button
    private lateinit var clearButton: Button
    private var _binding: FragmentGlucoseBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null."
        }


    private val args: GlucoseFragmentArgs by navArgs()
    private val glucoseViewModel: GlucoseViewModel by viewModels {
        GlucoseViewModelFactory(args.glucoseId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val glucose = Glucose(
            date = Date(),
            fasting = 0,
            breakfast = 0,
            lunch = 0,
            dinner = 0,
        )

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGlucoseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            this
            glucoseFasting.doOnTextChanged { text, _, _, _ ->
                if (glucoseFasting.text.toString() != "") {
                    glucoseViewModel.updateGlucose { oldGlucose ->
                        oldGlucose.copy(fasting = text.toString().toInt())
                    }
                }
            }
            glucoseBreakfast.doOnTextChanged { text, _, _, _ ->
                if (glucoseBreakfast.text.toString() != "") {
                    glucoseViewModel.updateGlucose { oldGlucose ->
                        oldGlucose.copy(breakfast = text.toString().toInt())

                    }
                }
            }
            glucoseLunch.doOnTextChanged { text, _, _, _ ->
                if (glucoseLunch.text.toString() != "") {
                    glucoseViewModel.updateGlucose { oldGlucose ->
                        oldGlucose.copy(lunch = text.toString().toInt())

                    }
                }
            }
            glucoseDinner.doOnTextChanged { text, _, _, _ ->
                if (glucoseDinner.text.toString() != "") {
                    glucoseViewModel.updateGlucose { oldGlucose ->
                        oldGlucose.copy(dinner = text.toString().toInt())
                    }
                }

            }
            fun getGlucoseReport(glucose: Glucose): String {

                var count = 0
                count = if (glucose.fasting in 71..139) {
                    0
                } else {
                    1
                }

                count = if(glucose.breakfast in 71..139) {
                    0
                }
                else
                {
                    1
                }

                count = if(glucose.lunch in 71..139) {
                    0
                }
                else
                {
                    1
                }

                count = if(glucose.dinner in 71..139) {
                    0
                }
                else
                {
                    1
                }

                val normalString = if (count == 0) {
                    getString(R.string.yes)
                }
                else
                {
                    getString(R.string.no)
                }

                val average = (glucose.fasting + glucose.breakfast + glucose.lunch + glucose.dinner) / 4



                return getString(
                    R.string.glucose_report,
                    glucose.date, glucose.fasting, glucose.breakfast, glucose.lunch, glucose.dinner, average, normalString
                )
            }

            fun updateUi(glucose: Glucose) {
                binding.apply {
                        glucoseFasting.setText(glucose.fasting.toString())
                        binding.fastingTextview.setText(R.string.fasting_text)

                        if (glucose.fasting in 71..139) {
                            binding.fastingResult.setText(R.string.normal_range)

                        } else {
                            binding.fastingResult.setText(R.string.abnormal_range)
                        }
                    glucoseBreakfast.setText(glucose.breakfast.toString())
                    binding.breakfastTextview.setText(R.string.breakfast_text)
                    if (glucose.breakfast > 140) {
                        binding.breakfastResult.setText(R.string.abnormal_range)
                    }
                    else if (glucose.breakfast < 70) {
                        binding.breakfastResult.setText(R.string.hypoglycemic_range)
                    }
                    else {
                        binding.breakfastResult.setText(R.string.normal_range)
                    }
                    glucoseLunch.setText(glucose.lunch.toString())
                    binding.lunchTextview.setText(R.string.lunch_text)
                    if (glucose.lunch > 140) {
                        binding.lunchResult.setText(R.string.abnormal_range)
                    }
                    else if (glucose.lunch < 70) {
                        binding.lunchResult.setText(R.string.hypoglycemic_range)
                    }
                    else {
                        binding.lunchResult.setText(R.string.normal_range)
                    }
                    glucoseDinner.setText(glucose.dinner.toString())
                    binding.dinnerTextview.setText(R.string.dinner_text)
                    if (glucose.dinner in 71..139) {
                        binding.dinnerResult.setText(R.string.normal_range)
                    }
                    else if (glucose.dinner < 70) {
                        binding.dinnerResult.setText(R.string.hypoglycemic_range)
                    }
                    else {
                        binding.dinnerResult.setText(R.string.abnormal_range)
                    }
                    dateButton.setText(glucose.date.toString())
                    dateButton.setOnClickListener {
                        findNavController().navigate(
                            GlucoseFragmentDirections.selectDate(glucose.date)
                        )
                    }
                    sendReportButton.setOnClickListener {
                        val reportIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, getGlucoseReport(glucose))
                        }

                        startActivity(reportIntent)
                    }

                }

                setFragmentResultListener(
                    DatePickerFragment.REQUEST_KEY_DATE
                ) { requestKey, bundle ->
                    val newDate =
                        bundle.getSerializable(DatePickerFragment.BUNDLE_KEY_DATE) as Date
                    findNavController().navigate(
                        DatePickerFragmentDirections.datePickerToDetail(newDate))
                }

            }
            historyButton.setOnClickListener {
                it.findNavController().popBackStack()
            }
            clearButton.setOnClickListener {
                val glucose = Glucose(
                    date = Date(),
                    fasting = 0,
                    breakfast = 0,
                    lunch = 0,
                    dinner = 0,
                )
                var clear: String = ""
                binding.glucoseFasting.setText(clear)
                binding.glucoseBreakfast.setText(clear)
                binding.glucoseLunch.setText(clear)
                binding.glucoseDinner.setText(clear)
                binding.fastingResult.setText(clear)
                binding.fastingTextview.setText(clear)
                binding.breakfastResult.setText(clear)
                binding.breakfastTextview.setText(clear)
                binding.lunchResult.setText(clear)
                binding.lunchTextview.setText(clear)
                binding.dinnerResult.setText(clear)
                binding.dinnerTextview.setText(clear)

            }


            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    glucoseViewModel.glucose.collect { glucose ->
                        glucose?.let { updateUi(it) }
                    }
                }
            }
            fun onDestroyView() {
                super.onDestroyView()
                _binding = null
            }


        }
    }
}





















