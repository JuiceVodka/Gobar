package si.uni_lj.fri.pbd.gobar

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager


import androidx.recyclerview.widget.RecyclerView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CompendiumFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CompendiumFragment : Fragment() {

    interface detailsLst{
        fun gbl():List<MushroomDetailsModel>?
    }
    var detailsListener: detailsLst? = null
    private var recyclerView: RecyclerView? = null
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: RecyclerView.Adapter<*>? = null

    var mushroomDetailsList :List<MushroomDetailsModel>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            detailsListener = context as detailsLst
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement OnDataPass")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_compendium, container, false)
        mushroomDetailsList = detailsListener?.gbl()
        recyclerView = view?.findViewById(R.id.achievementsRecyclerView)
        layoutManager = LinearLayoutManager(view?.context)
        recyclerView?.layoutManager = layoutManager
        Log.d("CompendiumFragment", mushroomDetailsList.toString())
        adapter = RecyclerAdapter(mushroomDetailsList)
        recyclerView?.adapter = adapter
        return view

        // Inflate the layout for this fragment
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CompendiumFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CompendiumFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}