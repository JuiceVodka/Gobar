package si.uni_lj.fri.pbd.gobar

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService


class CameraFragment : Fragment(){

    private lateinit var cameraManager :CameraManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        startCameraSession()
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }


    fun takePhoto(){

    }

    private fun startCameraSession() {
        cameraManager = requireContext().getSystemService(Context.CAMERA_SERVICE) as CameraManager
        if (cameraManager.cameraIdList.isEmpty()) {
            // no cameras
            return
        }
        val firstCamera = cameraManager.cameraIdList[0]
        if (activity?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.CAMERA
                )
            } != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d(TAG, "NO Premissions")
            return
        }
        cameraManager.openCamera(firstCamera, object: CameraDevice.StateCallback() {
            override fun onDisconnected(p0: CameraDevice) {
                Log.d(TAG, "GHAJKDHGJSAGHJHK")
            }
            override fun onError(p0: CameraDevice, p1: Int) {
                Log.d(TAG, "GHAJKDHGJSAGHJHK")
            }

            override fun onOpened(cameraDevice: CameraDevice) {
                Log.d(TAG, "GHAJKDHGJSAGHJHK")
                // use the camera
                val cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraDevice.id)

                cameraCharacteristics[CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP]?.let { streamConfigurationMap ->
                    streamConfigurationMap.getOutputSizes(ImageFormat.YUV_420_888)?.let { yuvSizes ->
                        val previewSize = yuvSizes.last()

                        //val displayRotation = activity!!.windowManager.defaultDisplay.rotation
                        //val swappedDimensions = areDimensionsSwapped(displayRotation, cameraCharacteristics)
                        // swap width and height if needed
                        //val rotatedPreviewWidth = if (swappedDimensions) previewSize.height else previewSize.width
                        //val rotatedPreviewHeight = if (swappedDimensions) previewSize.width else previewSize.height

                        //view?.findViewById<SurfaceView>(R.id.surfaceView)?.holder?.setFixedSize(rotatedPreviewWidth, rotatedPreviewHeight)

                        val previewSurface = view?.findViewById<SurfaceView>(R.id.surfaceView)?.holder?.surface

                        val captureCallback = object : CameraCaptureSession.StateCallback()
                        {
                            override fun onConfigureFailed(session: CameraCaptureSession) {}

                            override fun onConfigured(session: CameraCaptureSession) {
                                Log.d(TAG, "CONFIGURED TEST")
                                // session configured
                                val previewRequestBuilder = cameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                                    .apply {
                                        if (previewSurface != null) {
                                            addTarget(previewSurface)
                                        }
                                    }
                                session.setRepeatingRequest(
                                    previewRequestBuilder.build(),
                                    object: CameraCaptureSession.CaptureCallback() {},
                                    Handler { true }
                                )
                            }
                        }

                        cameraDevice.createCaptureSession(mutableListOf(previewSurface), captureCallback, Handler { true })
                    }

                }
            }
        }, Handler { true })
    }


    private fun areDimensionsSwapped(displayRotation: Int, cameraCharacteristics: CameraCharacteristics): Boolean {
        var swappedDimensions = false
        when (displayRotation) {
            Surface.ROTATION_0, Surface.ROTATION_180 -> {
                if (cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) == 90 || cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) == 270) {
                    swappedDimensions = true
                }
            }
            Surface.ROTATION_90, Surface.ROTATION_270 -> {
                if (cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) == 0 || cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) == 180) {
                    swappedDimensions = true
                }
            }
            else -> {
                // invalid display rotation
            }
        }
        return swappedDimensions
    }


    companion object {
        const val TAG = "CAMERAFRAGMENT"
    }


}