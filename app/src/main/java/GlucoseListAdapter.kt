package edu.arizona.cast.hannaarnold.glucosemonitor

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import edu.arizona.cast.hannaarnold.glucosemonitor.databinding.ListItemGlucoseBinding
import java.util.*


class GlucoseHolder(
    private val binding: ListItemGlucoseBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(glucose: Glucose, onGlucoseClicked: (glucoseId: Date) -> Unit) {

        fun averageGlucoseLevel(): Int {
            return (glucose.fasting + glucose.breakfast + glucose.lunch + glucose.dinner) / 4
        }

        fun isNormal(): Int {
            var count = 0

            if (glucose.fasting < 71 || glucose.fasting > 139) {
                count+=1
            }

            else if (glucose.breakfast > 140) {

                count += 1
            }
            else if (glucose.breakfast < 70) {
                count += 1
            }

            else if (glucose.lunch > 140) {
                count += 1
            }
            else if (glucose.lunch < 70) {
                count += 1
            }

            else if (glucose.dinner < 70) {
                count += 1
            }
            else if(glucose.dinner > 140) {
            count += 1
            }
            return count
        }

        if (isNormal() == 0) {
            binding.checkbox.isChecked=true
        }



        binding.glucoseDate.text = glucose.date.toString()
        binding.averageGlucoseLevel.text = averageGlucoseLevel().toString()


        binding.root.setOnClickListener {
            onGlucoseClicked(glucose.date)
        }
    }
}

class GlucoseListAdapter(
    private val glucoses: List<Glucose>,
    private val onGlucoseClicked: (glucoseId: Date) -> Unit
    ): RecyclerView.Adapter<GlucoseHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GlucoseHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemGlucoseBinding.inflate(inflater, parent, false)
        return GlucoseHolder(binding)
    }

    override fun onBindViewHolder(holder: GlucoseHolder, position: Int) {
        val glucose = glucoses[position]
        holder.bind(glucose, onGlucoseClicked)
    }

    override fun getItemCount() = glucoses.size

}