package my.edu.tarc.ezbike_v1.adminSide.adaptor

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.ezbike_v1.adminSide.db.BikeData
import my.edu.tarc.ezbike_v1.databinding.BikeListBinding
import java.util.*
import kotlin.collections.ArrayList

private lateinit var binding: BikeListBinding

class bikeRVAdaptor(private val clickListener:(BikeData)->Unit):RecyclerView.Adapter<bikeViewHolder>(), Filterable {

    private val bikeList = ArrayList<BikeData>()
    private var bikeSearchList = ArrayList<BikeData>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): bikeViewHolder {
        //inflate view
        binding = BikeListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return bikeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: bikeViewHolder, position: Int) {
        holder.bind(bikeSearchList[position],clickListener)
    }

    override fun getItemCount(): Int {
        return bikeSearchList.size
    }


    fun setList(lists:List<BikeData>){
        bikeList.clear()
        bikeList.addAll(lists)
        bikeSearchList.addAll(lists)
        notifyDataSetChanged()
    }

    fun resetList() {
        bikeSearchList.clear()
        bikeSearchList.addAll(bikeList)
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return object: Filter(){
            override fun performFiltering(char: CharSequence?): FilterResults {
                val filterResult = FilterResults()

                if (char.isNullOrEmpty()){
//                    filterResult.count = searchList.size
                    filterResult.count = bikeSearchList.size
                    filterResult.values = bikeSearchList
                }else{
                    var charLowerCase = char.toString().lowercase()
                    val lists = ArrayList<BikeData>()

                    bikeSearchList.forEach {
                        if (it.bikeSerialNO.lowercase().contains(charLowerCase)){
                            lists.add(it)
                        }
                    }
                    filterResult.count = lists.size
                    filterResult.values = lists
                }
                return filterResult
            }

            override fun publishResults(p0: CharSequence?, filterResult: FilterResults?) {
                bikeSearchList = filterResult?.values as ArrayList<BikeData>
                Log.d("adaptor", "Location List size: ${bikeSearchList.size}")
                notifyDataSetChanged()
            }

        }
    }

}

class bikeViewHolder(private val binding: BikeListBinding):RecyclerView.ViewHolder(binding.root){
    fun bind(bikeData: BikeData, clickListener:(BikeData)->Unit){

        binding.tvDBBikeSerial.text = bikeData.bikeSerialNO
        binding.tvDBBikeAvailability.text = bikeData.bikeAvailability
        binding.editDetail.setOnClickListener {
            clickListener(bikeData)
        }
    }
}