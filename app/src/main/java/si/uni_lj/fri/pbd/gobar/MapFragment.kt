package si.uni_lj.fri.pbd.gobar

import android.Manifest
import android.R.attr.height
import android.R.attr.width
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.parse.ParseObject
import com.parse.ParseQuery
import si.uni_lj.fri.pbd.gobar.databinding.FragmentMapBinding


class MapFragment : Fragment() {

    private lateinit var binding :FragmentMapBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Initialize view
        binding = FragmentMapBinding.inflate(inflater, container, false)
        //val view = inflater.inflate(R.layout.fragment_map, container, false)
        val view = binding.root

        // Initialize map fragment
        val supportMapFragment =
            childFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment?

        // Async map
        supportMapFragment!!.getMapAsync { googleMap ->
            mMap = googleMap
            var success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireContext(), R.raw.style_json));
            // When map is loaded
            googleMap.setOnMapClickListener { latLng -> // When clicked on map
                // Initialize marker options
                val markerOptions = MarkerOptions()
                // Set position of marker
                markerOptions.position(latLng)
                // Set title of marker
                markerOptions.title(latLng.latitude.toString() + " : " + latLng.longitude)
                // Remove all marker
                googleMap.clear()
                // Animating to zoom the marker
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
            }
            addMarkersFromDB()
            addMarkersFromLocalDb()
        }
        getPrem()
        getLoc()
        //addMarkersFromDB()
        //addMarkersFromLocalDb()
        // Return view
        return view
    }

    fun getPrem(){
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission
                    .ACCESS_COARSE_LOCATION) || ActivityCompat.shouldShowRequestPermissionRationale (requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                Log.d("Debug", "snekbar")
                Snackbar.make(
                    binding.root,
                    R.string.permission_location_rationale,
                    Snackbar.LENGTH_INDEFINITE
                ).setAction(R.string.ok) {
                    ActivityCompat.requestPermissions(
                        requireActivity(), arrayOf(
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ),
                        8
                    )
                }.show()
            } else{
                ActivityCompat.requestPermissions(
                    requireActivity(), arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ),
                    8
                )
            }
            return
        }
    }

    @SuppressLint("MissingPermission")
    fun getLoc(){
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                Log.d(TAG, "LOCATION GOT")
                if(location!= null){
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), 15.0f))

                    val long: String = location.latitude.toString()
                    val lat: String = location.longitude.toString()
                    val title: String = "You are here"
                    Log.d("DebugLong", long.toString())
                    Log.d("DebugLat", lat.toString())
                    val sydney = LatLng(lat.toDouble() - 0.2, long.toDouble() - 0.2 )
                    val marker = mMap.addMarker(MarkerOptions().position(sydney).title(title))
                    val h = 150
                    val w = 150

                    val imageBitmap = BitmapFactory.decodeResource(
                        resources,
                        resources.getIdentifier("redridinhood", "drawable", requireActivity().packageName)
                    )
                    val btmp = Bitmap.createScaledBitmap(imageBitmap, h, w, false)

                    val icon: BitmapDescriptor =
                        BitmapDescriptorFactory.fromBitmap(btmp)
                    marker?.setIcon(icon)


                }

                // Got last known location. In some rare situations this can be null.
            }
    }

    private fun addMarkersFromDB(){
        Log.d(TAG, "MARKERS")
        val query: ParseQuery<ParseObject> = ParseQuery.getQuery("Mushroom")
        query.findInBackground { objects, e ->
            Log.d(TAG, objects.size.toString())
            if (e == null) {
                for (obj in objects) {
                    obj?.getNumber("Long")?.let { Log.d(TAG, it.toString()) }
                    obj?.getNumber("Lat")?.let { Log.d(TAG, it.toString()) }
                    val long: String? = obj?.getNumber("Long").toString()
                    val lat: String? = obj?.getNumber("Lat").toString()
                    val title: String? = obj?.getString("NameCommon")
                    val titleLatin: String? = obj?.getString("NameLatin")


                    if (long!=null && lat != null) {
                        val sydney = LatLng(lat.toDouble(), long.toDouble())
                        val marker = mMap.addMarker(MarkerOptions().position(sydney).title(title))
                        marker?.snippet = titleLatin
                        val h = 100
                        val w = 100

                        val imageBitmap = BitmapFactory.decodeResource(
                            resources,
                            resources.getIdentifier("mushroomtheir", "drawable", requireActivity().packageName)
                        )
                        val btmp = Bitmap.createScaledBitmap(imageBitmap, h, w, false)

                        val icon: BitmapDescriptor =
                            BitmapDescriptorFactory.fromBitmap(btmp)
                        marker?.setIcon(icon)

                        /*if(votes >= 50){
                            val icon:BitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.king)
                            marker.setIcon(icon)
                        }
                        else if(votes >= 30){
                            val icon:BitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.middle)
                            marker.setIcon(icon)
                        }
                        else{
                            val icon:BitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.bad)
                            marker.setIcon(icon)
                        }*/

                        Log.d("SNIPPET", titleLatin.toString())
                        //dataList.add(element)
                    }
                }
            }
        }
    }

    private fun addMarkersFromLocalDb(){
        Log.d(TAG, "ADDIN")
        val dbHelper = DatabaseHelper(requireContext())
        val myShrooms = dbHelper.returnLocations()
        for (shroom in myShrooms){
            Log.d(TAG, "SHROOOOOMSSS")
            val long: String? = shroom.long
            val lat: String? = shroom.lat
            val title: String? = shroom.commonName
            val titleLatin: String? = shroom.latinName


            if (long!=null && lat != null){
                val sydney = LatLng(lat.toDouble()+0.2, long.toDouble()+0.2)
                val marker = mMap.addMarker(MarkerOptions().position(sydney).title(title))
                marker?.snippet = titleLatin
                val h = 100
                val w = 100

                val imageBitmap = BitmapFactory.decodeResource(
                    resources,
                    resources.getIdentifier("mushroommy", "drawable", requireActivity().packageName)
                )
                val btmp = Bitmap.createScaledBitmap(imageBitmap, h, w, false)

                val icon: BitmapDescriptor =
                    BitmapDescriptorFactory.fromBitmap(btmp)
                marker?.setIcon(icon)
            }
        }
    }

    companion object{
        private const val TAG = "MAPFRAGMENT"
    }
}