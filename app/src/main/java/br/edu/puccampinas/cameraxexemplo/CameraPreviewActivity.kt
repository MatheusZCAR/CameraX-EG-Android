package br.edu.puccampinas.cameraxexemplo

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import br.edu.puccampinas.cameraxexemplo.databinding.ActivityCameraPreviewBinding
import com.google.common.util.concurrent.ListenableFuture
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class CameraPreviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraPreviewBinding

    // processamento de imagem (não permitir ou controlar melhor o estado do driver da camera)
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>

    // selecionar se deseja a camera frontal ou traseira
    private lateinit var cameraSelector: CameraSelector

    // imagem capturada
    private var imageCapture: ImageCapture? = null

    // executor de thread separado
    private lateinit var imgCaptureExecutor: ExecutorService


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCameraPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        imgCaptureExecutor = Executors.newSingleThreadExecutor()


        // chamar o método startCamera()
        startCamera()

        // evento do clique no botão para chamar o método takePhoto
        binding.btnTakePhoto.setOnClickListener{
            takePhoto()
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                blinkPreview()
        }
    }

    private fun startCamera(){

        cameraProviderFuture.addListener({

            imageCapture = ImageCapture.Builder().build()

            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.cameraPreview.surfaceProvider)
            }

            try {

                // abrir o preview
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)

            }catch (e: Exception){
                Log.e("CameraPreview", "Falha ao abrir a camera")
            }
        }, ContextCompat.getMainExecutor((this)))

    }
    private fun takePhoto(){
        // código para tirar a foto
        imageCapture?.let {

            // nome do arquivo para gravar a foto
            val fileName = "JPEG_${System.currentTimeMillis()}"
            val file = File(externalMediaDirs[0], fileName)

            val outputFileOptions = ImageCapture.OutputFileOptions.Builder(file).build()

            it.takePicture(
                outputFileOptions,
                imgCaptureExecutor,
                object : ImageCapture.OnImageSavedCallback{  // tem que implementar pra mandar pro firestore a partir daqui
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        Log.i("CameraPreview", "A imagem foi salva no diretório: ${file.toURI()}")
                    }

                    override fun onError(exception: ImageCaptureException) {
                        Toast.makeText(binding.root.context, "Erro ao salvar foto", Toast.LENGTH_LONG).show()
                        Log.e("CameraPreview","Exceção ao gravar arquivo da foto: $exception")
                    }
                }

            )

        }


    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun blinkPreview(){
        binding.root.postDelayed({
            binding.root.foreground = ColorDrawable(Color.WHITE)
            binding.root.postDelayed({
                binding.root.foreground = null
            }, 50)
        }, 100)
    }

}