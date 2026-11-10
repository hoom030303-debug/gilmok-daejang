package io.gilmok.daejang

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import io.gilmok.daejang.coach.CoachVoice
import io.gilmok.daejang.ui.GilmokApp
import io.gilmok.daejang.vehicle.PleosVehicleLink

class MainActivity : ComponentActivity() {

    private lateinit var voice: CoachVoice
    private lateinit var vehicle: PleosVehicleLink

    /** 차량 신호를 읽는 권한. 차가 아닌 기기에서는 권한이 없어 그냥 넘어가요. */
    private val carPermissions = arrayOf(
        "android.car.permission.CAR_SPEED",
        "pleos.car.permission.CAR_DRIVE",
        "pleos.car.permission.CAR_INFO",
        "pleos.car.permission.CAR_SAFETY",
    )

    private val askPermissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        vehicle.start()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        io.gilmok.daejang.ui.theme.Fonts.init(this)
        voice = CoachVoice(this)
        vehicle = PleosVehicleLink(this)
        setContent { GilmokApp(voice, vehicle) }

        val missing = carPermissions.filter { checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED }
        val isCar = packageManager.hasSystemFeature(PackageManager.FEATURE_AUTOMOTIVE)
        if (isCar && missing.isNotEmpty()) askPermissions.launch(missing.toTypedArray()) else vehicle.start()
    }

    override fun onDestroy() {
        vehicle.release()
        voice.release()
        super.onDestroy()
    }
}
