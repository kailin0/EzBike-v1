package my.edu.tarc.ezbike_v1.adminSide.adaptor

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.ezbike_v1.adminSide.db.LocationData
import my.edu.tarc.ezbike_v1.databinding.LocationListBinding
import java.util.*
import kotlin.collections.ArrayList

private lateinit var binding: LocationListBinding

class locationRVAdaptor(private val clickListener:(LocationData)->Unit):RecyclerView.Adapter<locationViewHolder>(), Filterable {

    private val locationList = ArrayList<LocationData>()
    private var searchList = ArrayList<LocationData>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): locationViewHolder {
        //inflate view
        binding = LocationListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return locationViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return searchList.size
//        return locationList.size
    }

    override fun onBindViewHolder(holder: locationViewHolder, position: Int) {
        holder.bind(searchList[position],clickListener)
    }

    fun setList(lists:List<LocationData>){
        locationList.clear()
//        searchList.clear()
        locationList.addAll(lists)
        searchList.addAll(lists)
        notifyDataSetChanged()
    }

    fun resetList() {
        searchList.clear()
        searchList.addAll(locationList)
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return object: Filter(){
            override fun performFiltering(char: CharSequence?): FilterResults {
                val filterResult = FilterResults()

                if (char.isNullOrEmpty()){
//                    filterResult.count = searchList.size
                    filterResult.count = searchList.size
                    filterResult.values = searchList
                }else{
                    var charLowerCase = char.toString().lowercase()
                    val lists = ArrayList<LocationData>()

                    searchList.forEach {
                        if (it.locationName.lowercase().contains(charLowerCase)||it.locationAddress.lowercase().contains(charLowerCase)){
                            lists.add(it)
                        }
                    }
                    filterResult.count = lists.size
                    filterResult.values = lists
                }
                return filterResult
            }

            override fun publishResults(p0: CharSequence?, filterResult: FilterResults?) {
                searchList = filterResult?.values as ArrayList<LocationData>
                Log.d("adaptor", "Location List size: ${searchList.size}")
                notifyDataSetChanged()
            }

        }
    }

}

class locationViewHolder(private val binding: LocationListBinding):RecyclerView.ViewHolder(binding.root){
    fun bind(locationData: LocationData,clickListener:(LocationData)->Unit){

        binding.tvDBLocationName.text = locationData.locationName
        binding.tvDBLocationAddress.text = locationData.locationAddress
        binding.editDetail.setOnClickListener {
            clickListener(locationData)
        }
    }
}