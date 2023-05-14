package si.uni_lj.fri.pbd.gobar

import android.Manifest
import android.R.attr
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.location.Location
import android.os.Bundle
import android.provider.MediaStore
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.parse.ParseObject
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import si.uni_lj.fri.pbd.gobar.databinding.FragmentCameraBinding
import java.io.ByteArrayOutputStream
import java.io.IOException


class CameraFragment : Fragment(){

    private lateinit var binding :FragmentCameraBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var dbHelper :DatabaseHelper? = null
    var lat :Double? = null
    var long :Double? = null
    var latinName :String? = null
    var comName :String? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        getPrem()
        getLoc()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCameraBinding.inflate(inflater, container, false)

        binding.buttonShare.setOnClickListener {
            share()
            binding.buttonShare.isEnabled = false
            val blurRadius = 6f
            val blurMaskFilter = BlurMaskFilter(blurRadius, BlurMaskFilter.Blur.NORMAL)
            binding.buttonShare.paint.maskFilter = blurMaskFilter
        }

        binding.buttonSave.setOnClickListener {
            save()
            binding.buttonSave.isEnabled = false
            val blurRadius = 6f
            val blurMaskFilter = BlurMaskFilter(blurRadius, BlurMaskFilter.Blur.NORMAL)
            binding.buttonSave.paint.maskFilter = blurMaskFilter
        }

        return binding.root
    }

    override fun onAttach(context: Context) {
        dbHelper = DatabaseHelper(context)
        super.onAttach(context)
    }


    fun takePhoto(){
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
//                    imageUri = getImageUri(context, imageFile)
//                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                startActivityForResult(takePictureIntent, 1)
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {

            val image = data?.extras?.get("data") as Bitmap
            Log.d(TAG, image.toString())

            image.apply {
                view?.findViewById<ImageView>(R.id.memoImage)?.setImageBitmap(this)
                // create rounded corners bitmap
                view?.findViewById<ImageView>(R.id.memoImage)?.setImageBitmap(toRoundedCorners(8F))
            }

            identify(image)

        }
    }

    fun Bitmap.toRoundedCorners(
        cornerRadius: Float = 25F
    ):Bitmap?{
        val bitmap = Bitmap.createBitmap(
            width, // width in pixels
            height, // height in pixels
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)

        // path to draw rounded corners bitmap
        val path = Path().apply {
            addRoundRect(
                RectF(0f,0f,width.toFloat(),height.toFloat()),
                cornerRadius,
                cornerRadius,
                Path.Direction.CCW
            )
        }
        canvas.clipPath(path)

        // draw the rounded corners bitmap on canvas
        canvas.drawBitmap(this,0f,0f,null)
        return bitmap
    }

    @Throws(IOException::class)
    fun identify(bitmap: Bitmap){
        Log.d(TAG, "IDENTIFIKACIJA")
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = createRequestBody(bitmap)
        val request = Request.Builder()
            .url("https://mushroom.mlapi.ai/api/v1/identification?details=common_names,gbif_id,taxonomy,rank,characteristic,edibility,psychoactive")
            .post(requestBody)
            .addHeader("Api-Key", "WVXUY2AzKt4AFWvVRwcDMd6NaTlZKgZcPokH19ZobPaR31GbJa")
            .build()
        val client = OkHttpClient()
        var res :Response? = null
        val response = client.newCall(request).enqueue(object :Callback{
            override fun onFailure(call: Call, e: IOException) {
                Log.d(TAG, "FAIL")
            }

            override fun onResponse(call: Call, response: Response) {
                Log.d(TAG, response.message)
                Log.d(TAG, response.toString())
                val bod = response.body?.string()
                Log.d(TAG, bod.toString())
                res = response
                val jsonResponse = JSONObject(bod)
                val name = (jsonResponse.getJSONObject("result").getJSONObject("classification").getJSONArray("suggestions")[0] as JSONObject).getString("name")
                Log.d(TAG, name.toString())


                val commonName = (jsonResponse.getJSONObject("result").getJSONObject("classification").getJSONArray("suggestions")[0] as JSONObject).getJSONObject("details").getJSONArray("common_names")[0] as String

                val edibility = (jsonResponse.getJSONObject("result").getJSONObject("classification").getJSONArray("suggestions")[0] as JSONObject).getJSONObject("details").getString("edibility")

                val psychoactive =  (jsonResponse.getJSONObject("result").getJSONObject("classification").getJSONArray("suggestions")[0] as JSONObject).getJSONObject("details").getString("psychoactive")
                Log.d(TAG, name)
                Log.d(TAG, commonName)
                Log.d(TAG, edibility)
                Log.d(TAG, psychoactive)


                latinName = name
                comName = commonName

                updateCompendium()


                requireActivity().runOnUiThread{

                    updateUI(name, commonName, edibility, psychoactive)
                }
            }

        })
        updateUI("name", "commonName", "edibility", "psychoactive")
    }

    @Throws(IOException::class)
    fun createRequestBody(bitmap: Bitmap): RequestBody {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("images", "image.jpg", byteArrayOutputStream.toByteArray().toRequestBody("image/jpg".toMediaType()))
            .addFormDataPart("latitude", "49.1951239")
            .addFormDataPart("longitude", "16.6077111")
            .addFormDataPart("similar_images", "true")
            .build()
        return requestBody
    }

    fun updateUI(latin :String, name :String, edibility :String, psyh :String){
        binding.scroll.visibility = View.VISIBLE
        binding.buttonSave.visibility = View.VISIBLE
        binding.buttonShare.visibility = View.VISIBLE

        if(psyh == "true" ){
            Toast.makeText(requireContext(), "Sine, ti si šel po gobe", Toast.LENGTH_LONG).show()
            val blurRadius = 7f
            val blurMaskFilter = BlurMaskFilter(blurRadius, BlurMaskFilter.Blur.NORMAL)
            binding.commonName.paint.maskFilter = blurMaskFilter
            binding.commonName.paint.maskFilter = blurMaskFilter
        }

        var sourceString = "Name: <b>$name" + "</b> "
        binding.commonName.text = Html.fromHtml(sourceString)
        binding.commonName.visibility = View.VISIBLE
        sourceString = "Latin: <i>$latin" + "</i> "
        binding.latinName.text = Html.fromHtml(sourceString)
        binding.latinName.visibility = View.VISIBLE
        binding.edible.text = edibility
        binding.edible.visibility = View.VISIBLE
        val rez = if(psyh=="true") "yes" else "no"
        sourceString = "Psychadelic: $rez"
        binding.psychadelic.text = Html.fromHtml(sourceString)
        binding.psychadelic.visibility = View.VISIBLE
    }


    fun share(){
        Log.d(TAG, "SHARING")
        val toBase = ParseObject("Mushroom")
        toBase.put("NameCommon", comName!!)
        toBase.put("NameLatin", latinName!!)


        getLoc()

        if(lat != null && long != null){
            toBase.put("Lat", lat!!)
            toBase.put("Long", long!!)
        }

        toBase.saveInBackground {
            if (it != null) {
                it.localizedMessage?.let { message -> Log.e("MainActivity", message) }
            } else {
                Log.d(TAG, toBase.toString())
                Log.d(TAG, "Object saved.")
            }
        }

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
                lat = location?.latitude
                long = location?.longitude
                // Got last known location. In some rare situations this can be null.
            }
    }

    fun updateCompendium(){
        var shroom = dbHelper?.returnDetailsByLatinName(latinName!!)
        Log.d(TAG, shroom?.numFound.toString())
        if(shroom != null){
            val values = ContentValues()
            values.put("NameCommon", comName)
            values.put("NameLatin", latinName)
            values.put("Edibility", shroom.edibility)
            values.put("Image", shroom.image)
            values.put("IsPsychoActive", shroom.isPsychoactive)
            values.put("IsDiscovered", 1)
            values.put("NumberFound", shroom.numFound + 1)

            dbHelper?.updateIfFound(values, latinName!!)
        }

        shroom = dbHelper?.returnDetailsByLatinName(latinName!!)
        Log.d(TAG, shroom?.numFound.toString())
    }

    fun save(){
        val values: ContentValues = ContentValues()
        values.put(DatabaseHelper.MUSHROOM_COMMON_NAME, comName);
        values.put(DatabaseHelper.MUSHROOM_LATIN_NAME, latinName);
        values.put(DatabaseHelper.MUSHROOM_LAT, (lat?.plus(0.2)).toString());
        values.put(DatabaseHelper.MUSHROOM_LONG, (long?.plus(0.2)).toString());

        dbHelper?.writableDatabase?.insert(DatabaseHelper.TABLE_MUSHROOM_LOCATION, null, values)
        values.clear()
    }

    companion object {
        const val TAG = "CAMERAFRAGMENT"
    }
}