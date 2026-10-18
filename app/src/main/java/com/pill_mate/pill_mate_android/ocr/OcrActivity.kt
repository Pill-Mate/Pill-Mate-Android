package com.pill_mate.pill_mate_android.ocr

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import com.pill_mate.pill_mate_android.databinding.ActivityOcrBinding
import com.pill_mate.pill_mate_android.medicine_registration.MedicineRegistrationActivity
import com.pill_mate.pill_mate_android.medicine_registration.model.DataRepository
import com.pill_mate.pill_mate_android.medicine_registration.model.Medicine
import com.pill_mate.pill_mate_android.search.model.PillRepository
import com.pill_mate.pill_mate_android.search.model.SearchMedicineItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class OcrActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOcrBinding
    private var imageCapture: ImageCapture? = null

    private val repository = PillRepository()
    private val adapter = OcrMedicineAdapter { onMedicineSelected(it) }

    private val recognizer by lazy {
        TextRecognition.getClient(KoreanTextRecognizerOptions.Builder().build())
    }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                startCamera()
            } else {
                Toast.makeText(this, "카메라 권한이 필요해요", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOcrBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvMedicines.layoutManager = LinearLayoutManager(this)
        binding.rvMedicines.adapter = adapter

        binding.btnClose.setOnClickListener { finish() }
        binding.btnCapture.setOnClickListener { takePhoto() }
        binding.btnRetake.setOnClickListener { showResult(null) }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun startCamera() {
        val providerFuture = ProcessCameraProvider.getInstance(this)
        providerFuture.addListener({
            val provider = providerFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.previewView.surfaceProvider)
            }
            imageCapture = ImageCapture.Builder().build()

            provider.unbindAll()
            provider.bindToLifecycle(
                this, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageCapture
            )
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        val capture = imageCapture ?: return
        setLoading(true)

        val file = File(cacheDir, "ocr_${System.currentTimeMillis()}.jpg")
        val options = ImageCapture.OutputFileOptions.Builder(file).build()

        capture.takePicture(
            options,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    recognize(file)
                }

                override fun onError(exception: ImageCaptureException) {
                    setLoading(false)
                    Toast.makeText(this@OcrActivity, "촬영에 실패했어요", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    private fun recognize(file: File) {
        val image = try {
            InputImage.fromFilePath(this, Uri.fromFile(file))
        } catch (e: Exception) {
            setLoading(false)
            Toast.makeText(this, "이미지를 읽을 수 없어요", Toast.LENGTH_SHORT).show()
            return
        }

        recognizer.process(image)
            .addOnSuccessListener { result ->
                val candidates = OcrTextParser.extractCandidates(result.text)
                if (candidates.isEmpty()) {
                    setLoading(false)
                    showResult(emptyList()) // 약 이름을 못 찾음
                } else {
                    searchMedicines(candidates)
                }
            }
            .addOnFailureListener {
                setLoading(false)
                Toast.makeText(this, "인식에 실패했어요", Toast.LENGTH_SHORT).show()
            }
            .addOnCompleteListener { file.delete() }
    }

    // 후보 이름마다 기존 약 검색 API를 병렬 호출하고 결과를 합침
    private fun searchMedicines(candidates: List<String>) {
        lifecycleScope.launch {
            val results = withContext(Dispatchers.IO) {
                coroutineScope {
                    candidates
                        .map { name -> async { repository.getSearchMedicineResults(name).take(3) } }
                        .awaitAll()
                }.flatten().distinctBy { it.itemSeq }.take(10)
            }
            setLoading(false)
            showResult(results)
        }
    }

    // StepTwoPresenterImpl.convertToMedicine 과 동일한 변환
    private fun onMedicineSelected(item: SearchMedicineItem) {
        val medicine = Medicine(
            identify_number = item.itemSeq.toString(),
            medicine_name = item.itemName,
            ingredient = "",
            image = item.itemImage,
            classname = item.className,
            efficacy = "",
            side_effect = "",
            caution = "",
            storage = "",
            entp_name = item.entpName
        )
        DataRepository.saveMedicine(medicine)

        startActivity(
            Intent(this, MedicineRegistrationActivity::class.java)
                .putExtra("from_ocr", true) // 등록 화면에서 이 값으로 단계를 건너뛰도록 처리 필요
        )
        finish()
    }

    // items == null: 촬영 화면으로 복귀 / 빈 리스트: 결과 없음 안내
    private fun showResult(items: List<SearchMedicineItem>?) {
        val show = items != null
        adapter.submit(items.orEmpty())
        binding.tvEmpty.visibility = if (show && items.isNullOrEmpty()) View.VISIBLE else View.GONE
        binding.cardResult.visibility = if (show) View.VISIBLE else View.GONE
        binding.btnCapture.visibility = if (show) View.GONE else View.VISIBLE
        binding.tvGuide.visibility = if (show) View.GONE else View.VISIBLE
    }

    private fun setLoading(loading: Boolean) {
        binding.progress.visibility = if (loading) View.VISIBLE else View.GONE
        binding.btnCapture.isEnabled = !loading
    }

    override fun onDestroy() {
        super.onDestroy()
        recognizer.close()
    }
}