package si.uni_lj.fri.pbd.gobar

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import okhttp3.*
import okhttp3.Headers.Companion.toHeaders
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException


class CameraFragment : Fragment(){

    private val client = OkHttpClient()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_camera, container, false)
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
            }

            run(image)

        }
    }

    fun run(bitmap :Bitmap) {
        identify(bitmap)
    }

    @Throws(IOException::class)
    fun identify(bitmap: Bitmap){
        Log.d(TAG, "IDENTIFIKACIJA")
        val mediaType = "application/json; charset=utf-8".toMediaType()
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


                val commonName = (jsonResponse.getJSONObject("result").getJSONObject("classification").getJSONArray("suggestions")[0] as JSONObject).getJSONObject("details").getJSONArray("common_names")[0]

                val edibility = (jsonResponse.getJSONObject("result").getJSONObject("classification").getJSONArray("suggestions")[0] as JSONObject).getJSONObject("details").getString("edibility")

                val psychoactive =  (jsonResponse.getJSONObject("result").getJSONObject("classification").getJSONArray("suggestions")[0] as JSONObject).getJSONObject("details").getString("psychoactive")
                Log.d(TAG, name)
                Log.d(TAG, commonName.toString())
                Log.d(TAG, edibility)
                Log.d(TAG, psychoactive)
            }

        })
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

    fun updateUI(latin :String, name :String, edibility :String, poison :String){

    }


    companion object {
        const val TAG = "CAMERAFRAGMENT"
    }
}