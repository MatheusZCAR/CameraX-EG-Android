package br.edu.puccampinas.cameraxexemplo

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import br.edu.puccampinas.cameraxexemplo.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // inflar a activity
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnOpenCamera.setOnClickListener{

            // solicitar permissão
            cameraProviderResult.launch(android.Manifest.permission.CAMERA)

        }

    }

    private val cameraProviderResult =
        registerForActivityResult(ActivityResultContracts.RequestPermission()){
            if(it){
                abrirTelaDePreview()
            }else{
                Snackbar.make(binding.root, "Você não concedeu permissões para usar a câmera", Snackbar.LENGTH_INDEFINITE).show()
            }
        }

    private fun abrirTelaDePreview(){
        // navegar para a outra activity
        val intentCameraPreview = Intent(this, CameraPreviewActivity::class.java)
        startActivity(intentCameraPreview)
    }
}