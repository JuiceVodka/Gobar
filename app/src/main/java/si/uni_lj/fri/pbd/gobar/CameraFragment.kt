package si.uni_lj.fri.pbd.gobar

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import si.uni_lj.fri.pbd.gobar.databinding.FragmentCameraBinding
import java.io.ByteArrayOutputStream
import java.io.IOException


class CameraFragment : Fragment(){

    private lateinit var binding :FragmentCameraBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
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

            run(image)

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

    fun run(bitmap :Bitmap) {
        identify(bitmap)
    }

    @Throws(IOException::class)
    fun identify(bitmap: Bitmap){
        Log.d(TAG, "IDENTIFIKACIJA")
        /*val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = createRequestBody(bitmap)
        val request = Request.Builder()
            .url("https://mushroom.mlapi.ai/api/v1/identification?details=common_names,gbif_id,taxonomy,rank,characteristic,edibility,psychoactive")
            .post(requestBody)
            .addHeader("Api-Key", "Hei1oOTMvoeW2miXZ1eeUOT7IfIUn2QLSmwT89xPPCe8WVMGbh")
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
                */
                /*requireActivity().runOnUiThread{
                    updateUI(name, commonName, edibility, psychoactive)
                }
            }

        })*/
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

        binding.commonName.text = name
        binding.commonName.visibility = View.VISIBLE
        binding.latinName.text = latin
        binding.latinName.visibility = View.VISIBLE
        binding.edible.text = edibility
        binding.edible.visibility = View.VISIBLE
        binding.psychadelic.text = psyh
        binding.psychadelic.visibility = View.VISIBLE
    }


    companion object {
        const val TAG = "CAMERAFRAGMENT"
    }
}