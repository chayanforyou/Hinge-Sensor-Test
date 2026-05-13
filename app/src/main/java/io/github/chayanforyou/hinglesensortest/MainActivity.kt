package io.github.chayanforyou.hinglesensortest

import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var hingeSensor: Sensor? = null

    private lateinit var resultTextView: TextView
    private val resultBuilder = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        resultTextView = findViewById(R.id.resultTextView)

        detectUsingPackageManager()
        detectUsingHingeSensor()
        detectUsingWindowManager()
        detectUsingManufacturer()
        detectUsingKnownModels()
        detectUsingScreenRatio()
        detectUsingSmallestWidth()
    }

    // ------------------------------------------------------------------------
    // Helper
    // ------------------------------------------------------------------------

    private fun appendResult(title: String, value: Any?) {

        resultBuilder.appendLine("• $title : $value")

        runOnUiThread {
            resultTextView.text = resultBuilder.toString()
        }
    }

    // ------------------------------------------------------------------------
    // 1. PackageManager Feature
    // ------------------------------------------------------------------------

    private fun detectUsingPackageManager() {

        val hasFeature = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_HINGE_ANGLE)
        } else {
            false
        }

        appendResult(
            "FEATURE_SENSOR_HINGE_ANGLE",
            hasFeature
        )
    }

    // ------------------------------------------------------------------------
    // 2. Hinge Sensor
    // ------------------------------------------------------------------------

    private fun detectUsingHingeSensor() {

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            hingeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HINGE_ANGLE)
        }

        val hasSensor = hingeSensor != null

        appendResult(
            "TYPE_HINGE_ANGLE Sensor",
            hasSensor
        )

        if (hasSensor) {
            sensorManager.registerListener(
                this,
                hingeSensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {

        if (event?.sensor?.type == Sensor.TYPE_HINGE_ANGLE) {

            val angle = event.values[0]

            appendResult(
                "Current Hinge Angle",
                angle
            )
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

    // ------------------------------------------------------------------------
    // 3. WindowManager
    // ------------------------------------------------------------------------

    private fun detectUsingWindowManager() {

        lifecycleScope.launch {
            WindowInfoTracker.getOrCreate(this@MainActivity)
                .windowLayoutInfo(this@MainActivity)
                .collect { layoutInfo ->

                    val foldingFeature = layoutInfo.displayFeatures
                        .filterIsInstance<FoldingFeature>()
                        .firstOrNull()

                    if (foldingFeature != null) {

                        appendResult(
                            "WindowManager Foldable",
                            true
                        )

                        appendResult(
                            "Fold State",
                            foldingFeature.state
                        )

                        appendResult(
                            "Fold Orientation",
                            foldingFeature.orientation
                        )

                        appendResult(
                            "Is Separating",
                            foldingFeature.isSeparating
                        )

                    } else {

                        appendResult(
                            "WindowManager Foldable",
                            false
                        )
                    }
                }
        }
    }

    // ------------------------------------------------------------------------
    // 4. Manufacturer
    // ------------------------------------------------------------------------

    private fun detectUsingManufacturer() {

        appendResult(
            "Manufacturer",
            Build.MANUFACTURER
        )

        appendResult(
            "Brand",
            Build.BRAND
        )
    }

    // ------------------------------------------------------------------------
    // 5. Known Models
    // ------------------------------------------------------------------------

    private fun detectUsingKnownModels() {

        val knownFoldables = setOf(

            "SM-F926B",
            "SM-F936B",
            "SM-F946B",
            "SM-F956B",
            "Pixel Fold",
            "CPH2551"
        )

        val model = Build.MODEL

        appendResult(
            "Model",
            model
        )

        appendResult(
            "Known Foldable Model",
            model in knownFoldables
        )
    }

    // ------------------------------------------------------------------------
    // 6. Screen Ratio
    // ------------------------------------------------------------------------

    private fun detectUsingScreenRatio() {

        val metrics = resources.displayMetrics

        val width = metrics.widthPixels
        val height = metrics.heightPixels

        val ratio = width.toFloat() / height

        appendResult(
            "Screen Width",
            width
        )

        appendResult(
            "Screen Height",
            height
        )

        appendResult(
            "Aspect Ratio",
            ratio
        )

        appendResult(
            "Could Be Foldable",
            ratio in 0.75f..1.45f
        )
    }

    // ------------------------------------------------------------------------
    // 7. smallestScreenWidthDp
    // ------------------------------------------------------------------------

    private fun detectUsingSmallestWidth() {

        val swDp = resources.configuration.smallestScreenWidthDp

        appendResult(
            "smallestScreenWidthDp",
            swDp
        )

        appendResult(
            "Tablet/Foldable Layout",
            swDp >= 600
        )
    }

    // ------------------------------------------------------------------------
    // Cleanup
    // ------------------------------------------------------------------------

    override fun onDestroy() {
        super.onDestroy()

        sensorManager.unregisterListener(this)
    }
}