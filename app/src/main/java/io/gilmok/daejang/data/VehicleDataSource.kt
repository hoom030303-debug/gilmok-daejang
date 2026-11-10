package io.gilmok.daejang.data

/**
 * 차에서 받아 오는 신호.
 *
 * 차량에서는 아래처럼 Pleos Connect SDK와 AAOS 속성으로 채워요.
 * - 속도, 가속: CarPropertyManager PERF_VEHICLE_SPEED (공식 FAQ 안내)
 * - 브레이크 페달, 핸들 각도, 주행 거리: Vehicle SDK (Brake, SteeringWheel, Odometer)
 * - 위치와 구간 진입: FusedLocation SDK (addProximityAlert)
 * - 스쿨존, 사고 다발 구간, 제한 속도: NaviHelper SDK (onCautionPointInfo, onCurrentRoadSpeedLimit)
 * - 코치 목소리와 음성 질문: TextToSpeech SDK, SpeechToText SDK
 *
 * 1차 제출 APK는 SDK 승인 전이라 [DemoVehicleDataSource]의 예시 운전 기록으로 동작해요.
 */
interface VehicleDataSource {
    fun me(): Person
    fun league(): List<LeagueMember>
    fun drivesThisWeek(): List<Drive>
    fun lastDrive(): Drive
    fun lastDriveItems(): List<ScoreItem>
    fun retakenRoad(): Road
    fun nextRoad(): Road
}
