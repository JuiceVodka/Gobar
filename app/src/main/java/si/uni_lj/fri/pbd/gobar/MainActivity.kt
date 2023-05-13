package si.uni_lj.fri.pbd.gobar

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.Image
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentTransaction
import si.uni_lj.fri.pbd.gobar.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), CompendiumFragment.detailsLst {
    private lateinit var binding : ActivityMainBinding;
    var dbHelper :DatabaseHelper? = null

    var detailsList: List<MushroomDetailsModel>? = null
    private var fragmentTransaction : FragmentTransaction? = null

    private var cameraActive :Boolean = true

    /** Helper to ask camera permission.  */
    object CameraPermissionHelper {
        private const val CAMERA_PERMISSION_CODE = 0
        private const val CAMERA_PERMISSION = Manifest.permission.CAMERA

        /** Check to see we have the necessary permissions for this app.  */
        fun hasCameraPermission(activity: Activity): Boolean {
            return ContextCompat.checkSelfPermission(activity, CAMERA_PERMISSION) == PackageManager.PERMISSION_GRANTED
        }

        /** Check to see we have the necessary permissions for this app, and ask for them if we don't.  */
        fun requestCameraPermission(activity: Activity) {
            ActivityCompat.requestPermissions(
                activity, arrayOf(CAMERA_PERMISSION), CAMERA_PERMISSION_CODE)
        }

        /** Check to see if we need to show the rationale for this permission.  */
        fun shouldShowRequestPermissionRationale(activity: Activity): Boolean {
            return ActivityCompat.shouldShowRequestPermissionRationale(activity, CAMERA_PERMISSION)
        }

        /** Launch Application Setting to grant permission.  */
        fun launchPermissionSettings(activity: Activity) {
            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            intent.data = Uri.fromParts("package", activity.packageName, null)
            activity.startActivity(intent)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (!CameraPermissionHelper.hasCameraPermission(this)) {
            Toast.makeText(this, "Camera permission is needed to run this application", Toast.LENGTH_LONG)
                .show()
            if (!CameraPermissionHelper.shouldShowRequestPermissionRationale(this)) {
                // Permission denied with checking "Do not ask again".
                CameraPermissionHelper.launchPermissionSettings(this)
            }
            finish()
        }

        recreate()
    }


    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!CameraPermissionHelper.hasCameraPermission(this)) {
            CameraPermissionHelper.requestCameraPermission(this)
        }

        dbHelper = DatabaseHelper(this)

        val prefs : SharedPreferences = getApplicationContext().getSharedPreferences("preferences", Context.MODE_PRIVATE)
        Log.d("DEBUG", prefs.all.toString())
        if(prefs.getBoolean("frstRun", true)){
            prefs.edit().putBoolean("frstRun", false).commit()
            Log.d("DEBUG", "FRST")
            fillDb()
        }else{
            Log.d("DEBUG", "SCND")
        }

        detailsList = dbHelper!!.returnDetails()

        fragmentTransaction = supportFragmentManager.beginTransaction()

//CAMERA:
        var camFragment :CameraFragment = CameraFragment()

        fragmentTransaction?.add(R.id.fragmentFrame, camFragment)
        fragmentTransaction?.commit()

        //todo set different icon


        binding.photo.setOnClickListener {
            if(cameraActive){
                camFragment.takePhoto()
            } else {
                fragmentTransaction = supportFragmentManager.beginTransaction()
                camFragment = CameraFragment()
                fragmentTransaction?.replace(R.id.fragmentFrame, camFragment)
                fragmentTransaction?.commit()
            }
        }

        binding.map.setOnClickListener {
            fragmentTransaction = supportFragmentManager.beginTransaction()
            var mapFragment = MapFragment()
            fragmentTransaction?.replace(R.id.fragmentFrame, mapFragment)
            fragmentTransaction?.commit()
            cameraActive = false
        }

        binding.compendium.setOnClickListener {
            fragmentTransaction = supportFragmentManager.beginTransaction()
            var compendiumFragment = CompendiumFragment()
            fragmentTransaction?.replace(R.id.fragmentFrame, compendiumFragment)
            fragmentTransaction?.commit()
            cameraActive = false
        }
    }

    fun fillDb(){
        val values: ContentValues = ContentValues()
        values.put(DatabaseHelper.MUSHROOM_COMMON_NAME, "Penny Bun");
        values.put(DatabaseHelper.MUSHROOM_LATIN_NAME, "Boletus edulis");
        values.put(DatabaseHelper.MUSHROOM_EDIBILITY, "edible");
        values.put(DatabaseHelper.MUSHROOM_IS_PSYCHOACTIVE, 0);
        values.put(DatabaseHelper.MUSHROOM_IS_DISCOVERED, 0);
        values.put(DatabaseHelper.MUSHROOM_IMAGE, "https://www.wildfooduk.com/wp-content/uploads/2017/06/Pence-4-720x540.jpg");
        values.put(DatabaseHelper.MUSHROOM_NUMBER_FOUND, 0);
        dbHelper?.writableDatabase?.insert(DatabaseHelper.TABLE_MUSHROOM_DETAILS, null, values)
        values.clear()

        values.put(DatabaseHelper.MUSHROOM_COMMON_NAME, "Girolle");
        values.put(DatabaseHelper.MUSHROOM_LATIN_NAME, "Cantharellus cibarius");
        values.put(DatabaseHelper.MUSHROOM_EDIBILITY, "edible");
        values.put(DatabaseHelper.MUSHROOM_IS_PSYCHOACTIVE, 0);
        values.put(DatabaseHelper.MUSHROOM_IS_DISCOVERED, 0);
        values.put(DatabaseHelper.MUSHROOM_IMAGE, "https://encrypted-tbn1.gstatic.com/licensed-image?q=tbn:ANd9GcQ5O7LHGpXAdrg4j0unGal2hTA1iIV2sWtqsrEBcUfjNrLhnU4gUMe7vN9uJwnKJMbBJY-mZVgS8WfS_oc");
        values.put(DatabaseHelper.MUSHROOM_NUMBER_FOUND, 0);
        dbHelper?.writableDatabase?.insert(DatabaseHelper.TABLE_MUSHROOM_DETAILS, null, values)
        values.clear()

        values.put(DatabaseHelper.MUSHROOM_COMMON_NAME, "Blusher");
        values.put(DatabaseHelper.MUSHROOM_LATIN_NAME, "Amanita rubescens");
        values.put(DatabaseHelper.MUSHROOM_EDIBILITY, "poisonous when raw, edible when cooked");
        values.put(DatabaseHelper.MUSHROOM_IS_PSYCHOACTIVE, 0);
        values.put(DatabaseHelper.MUSHROOM_IS_DISCOVERED, 0);
        values.put(DatabaseHelper.MUSHROOM_IMAGE, "hhttps://observation.org/media/photo/42803009.jpg");
        values.put(DatabaseHelper.MUSHROOM_NUMBER_FOUND, 0);
        dbHelper?.writableDatabase?.insert(DatabaseHelper.TABLE_MUSHROOM_DETAILS, null, values)
        values.clear()

        values.put(DatabaseHelper.MUSHROOM_COMMON_NAME, "Fly agaric");
        values.put(DatabaseHelper.MUSHROOM_LATIN_NAME, "Amanita muscaria");
        values.put(DatabaseHelper.MUSHROOM_EDIBILITY, "poisonous");
        values.put(DatabaseHelper.MUSHROOM_IS_PSYCHOACTIVE, 1);
        values.put(DatabaseHelper.MUSHROOM_IS_DISCOVERED, 0);
        values.put(DatabaseHelper.MUSHROOM_IMAGE, "https://upload.wikimedia.org/wikipedia/commons/3/32/Amanita_muscaria_3_vliegenzwammen_op_rij.jpg");
        values.put(DatabaseHelper.MUSHROOM_NUMBER_FOUND, 0);
        dbHelper?.writableDatabase?.insert(DatabaseHelper.TABLE_MUSHROOM_DETAILS, null, values)
        values.clear()

        values.put(DatabaseHelper.MUSHROOM_COMMON_NAME, "Penny Bun");
        values.put(DatabaseHelper.MUSHROOM_LATIN_NAME, "Boletus edulis");
        values.put(DatabaseHelper.MUSHROOM_EDIBILITY, "edible");
        values.put(DatabaseHelper.MUSHROOM_IS_PSYCHOACTIVE, 0);
        values.put(DatabaseHelper.MUSHROOM_IS_DISCOVERED, 0);
        values.put(DatabaseHelper.MUSHROOM_IMAGE, "https://www.wildfooduk.com/wp-content/uploads/2017/06/Pence-4-720x540.jpg");
        values.put(DatabaseHelper.MUSHROOM_NUMBER_FOUND, 0);
        dbHelper?.writableDatabase?.insert(DatabaseHelper.TABLE_MUSHROOM_DETAILS, null, values)
        values.clear()

        values.put(DatabaseHelper.MUSHROOM_COMMON_NAME, "charcoal burner");
        values.put(DatabaseHelper.MUSHROOM_LATIN_NAME, "Russula cyanoxantha");
        values.put(DatabaseHelper.MUSHROOM_EDIBILITY, "edible");
        values.put(DatabaseHelper.MUSHROOM_IS_PSYCHOACTIVE, 0);
        values.put(DatabaseHelper.MUSHROOM_IS_DISCOVERED, 0);
        values.put(DatabaseHelper.MUSHROOM_IMAGE, "https://www.fichasmicologicas.com/uploads/tx_txgastromicologia/Russula_cyanoxantha.jpg");
        values.put(DatabaseHelper.MUSHROOM_NUMBER_FOUND, 0);
        dbHelper?.writableDatabase?.insert(DatabaseHelper.TABLE_MUSHROOM_DETAILS, null, values)
        values.clear()

        values.put(DatabaseHelper.MUSHROOM_COMMON_NAME, "Birch bolete");
        values.put(DatabaseHelper.MUSHROOM_LATIN_NAME, "Leccinum scabrum");
        values.put(DatabaseHelper.MUSHROOM_EDIBILITY, "edible");
        values.put(DatabaseHelper.MUSHROOM_IS_PSYCHOACTIVE, 0);
        values.put(DatabaseHelper.MUSHROOM_IS_DISCOVERED, 0);
        values.put(DatabaseHelper.MUSHROOM_IMAGE, "https://www.first-nature.com/fungi/images/boletaceae/leccinum-scabrum10.jpg");
        values.put(DatabaseHelper.MUSHROOM_NUMBER_FOUND, 0);
        dbHelper?.writableDatabase?.insert(DatabaseHelper.TABLE_MUSHROOM_DETAILS, null, values)
        values.clear()

        values.put(DatabaseHelper.MUSHROOM_COMMON_NAME, "Salmon coral");
        values.put(DatabaseHelper.MUSHROOM_LATIN_NAME, "Ramaria formosa");
        values.put(DatabaseHelper.MUSHROOM_EDIBILITY, "poisonous");
        values.put(DatabaseHelper.MUSHROOM_IS_PSYCHOACTIVE, 0);
        values.put(DatabaseHelper.MUSHROOM_IS_DISCOVERED, 0);
        values.put(DatabaseHelper.MUSHROOM_IMAGE, "https://www.mykoweb.com/CAF/photos/large/Ramaria_formosa%28Exeter-2004-56a%29.jpg");
        values.put(DatabaseHelper.MUSHROOM_NUMBER_FOUND, 0);
        dbHelper?.writableDatabase?.insert(DatabaseHelper.TABLE_MUSHROOM_DETAILS, null, values)
        values.clear()

        values.put(DatabaseHelper.MUSHROOM_COMMON_NAME, "Laughing gym");
        values.put(DatabaseHelper.MUSHROOM_LATIN_NAME, "Gymnopilus junonius");
        values.put(DatabaseHelper.MUSHROOM_EDIBILITY, "not edible");
        values.put(DatabaseHelper.MUSHROOM_IS_PSYCHOACTIVE, 1);
        values.put(DatabaseHelper.MUSHROOM_IS_DISCOVERED, 0);
        values.put(DatabaseHelper.MUSHROOM_IMAGE, "https://encrypted-tbn2.gstatic.com/licensed-image?q=tbn:ANd9GcQOKyJY3gKJ5UynEpZ1C6i-TeGGVsv-eNXm7Z25bdSSlafhZ2U4B9bsorahvvrFd-j17Oy57m1IlEV0JJE");
        values.put(DatabaseHelper.MUSHROOM_NUMBER_FOUND, 0);
        dbHelper?.writableDatabase?.insert(DatabaseHelper.TABLE_MUSHROOM_DETAILS, null, values)
        values.clear()

        Log.d("DEBUG", "Baza napolnjena")
    }

    override fun gbl(): List<MushroomDetailsModel>? {
        return detailsList;
    }
}