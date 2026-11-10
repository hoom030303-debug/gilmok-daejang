# 길목대장

친구와 겨루는 안전운전 리그. 부드럽게 달린 사람이 길목의 대장이 돼요.

현대자동차그룹 차량용 앱 개발 공모전(Pleos Connect) 출품작이에요. 현대차·기아를 타는 친구들끼리 평소 운전 점수로 한 주 동안 겨루고, 자주 지나는 길(교차로, 스쿨존, 내리막)마다 그 길을 가장 부드럽게 지난 사람이 대장이 돼요. 주차하면 대장 코치가 "친구를 이기려면 이것 하나"를 소리로 알려 줘요.

## APK 위치

- `apk/길목대장_길목대장_release.apk` (서명된 release 버전)
- 같은 파일을 GitHub Releases에도 올려 두었어요.

## 실행 방법

1. Android 기기나 에뮬레이터(API 28 이상)에 APK를 설치해요.
   ```
   adb install apk/길목대장_길목대장_release.apk
   ```
2. 앱을 열면 브랜드 화면 뒤에 홈이 나와요. 위쪽 탭(홈, 친구 지도, 운전 기록, 리그)으로 화면을 오가요.
3. 차에서 저절로 뜨는 화면(시동, 주차, 일요일 밤)은 **왼쪽 위 앱 아이콘을 길게 누르면** 나오는 시연 메뉴에서 바로 열 수 있어요.
4. 아래 대장 코치 바의 재생 버튼을 누르면 코치 문장을 한국어 음성(TTS)으로 읽어 줘요. 오른쪽 위 마이크 버튼은 음성 질문 화면이에요.

### 화면 흐름

| 탭 | 첫 화면 | 눌러서 들어가는 화면 | 저절로 뜨는 화면 |
| --- | --- | --- | --- |
| 홈 | 11 홈 | 10 이번 운전 상세, 14 길 상세, 12 성장 순위 | 07 운전자 확인(시동), 08 오늘의 목표(출발 전) |
| 친구 지도 | 13 친구 지도 | 14 길 상세 | |
| 운전 기록 | 15 운전 기록 | 10 이번 운전 상세 | 09 주차 코칭, 24 짧은 운전, 26 오프라인 |
| 리그 | 12 리그 순위 | 12 성장 순위, 05 리그 초대, 19 주간 리포트 | 19 주간 리포트(일요일 밤), 23 리그 없음 |
| 마이크 | 21 음성 질문 | | |

## 테스트 환경과 필요한 설정

- 화면 기준: Pleos Connect-S 17인치 앱 창 1700×1184 dp (가로). 다른 크기의 화면에서는 같은 비율로 줄이거나 늘려 보여 줘요.
- 확인한 환경: Android 14(API 34) 에뮬레이터 태블릿, 화면 캡처 테스트(Robolectric + Roborazzi)
- 개발 환경: Android API 28 이상(minSdk 28, targetSdk 35), Gradle 8.10.2, Kotlin 2.0.21, Jetpack Compose, JDK 17
- 따로 필요한 권한이나 계정은 없어요. 음성 읽기는 기기의 한국어 TTS를 써요.

### 직접 빌드하기

```
./gradlew assembleDebug          # 디버그 APK
./gradlew testDebugUnitTest      # 모든 화면을 app/build/screens 에 PNG로 그려요
```

release 빌드는 `keystore.properties`(저장소에 올리지 않음)에 서명 키 정보가 있어야 해요.

## 차량 데이터 연결

1차 제출 APK는 SDK 사용 승인 전이라 예시 운전 기록(`DemoVehicleDataSource`)으로 동작해요. 차에서는 `VehicleDataSource`를 아래 SDK로 채워요.

| 필요한 값 | 가져오는 곳 |
| --- | --- |
| 속도, 가속 | AAOS CarPropertyManager `PERF_VEHICLE_SPEED` |
| 브레이크, 핸들 각도, 주행 거리 | Vehicle SDK (Brake, SteeringWheel, Odometer) |
| 위치, 구간 진입 | FusedLocation SDK (`addProximityAlert`) |
| 스쿨존, 사고 다발 구간, 제한 속도 | NaviHelper SDK (`onCautionPointInfo`, `onCurrentRoadSpeedLimit`) |
| 코치 목소리, 음성 질문 | TextToSpeech SDK, SpeechToText SDK |

주행 중에는 화면을 띄우지 않고 소리로만 알려 주는 설계라 `distractionOptimized`를 넣지 않았어요.

## 점수 규칙 (`LeagueRules.kt`)

- 운전 점수 = 100 − 감점 + 가점. 감점은 달린 거리 10km 기준으로 맞춰요.
- 스쿨존·사고 다발 구간은 감점과 가점이 모두 2배예요.
- 길의 대장 = 최근 2주 동안 그 길을 2번 이상 지난 사람 중 그 길 평균 점수가 가장 높은 사람.
- 리그 순위 = 이번 주 거리 가중 평균 점수, 일주일 50km 이상 달린 사람만.
- 성장 순위 = 이번 주 평균 − 지난주 평균.
- 2km보다 짧은 운전은 점수만 남기고 순위에는 넣지 않아요.
- "다음에 몇 점 이상이면 대장"은 `scoreNeededToTake()`로 계산해요. 예) 은행나무길 내리막: 내 평균 86.9(6번), 대장 87.4 → 91점 이상.

## 폴더 구조

```
apk/                         제출용 release APK
app/src/main/java/io/gilmok/daejang/
  MainActivity.kt
  coach/CoachVoice.kt        대장 코치 목소리 (TTS)
  data/                      점수 규칙, 차량 데이터 연결, 예시 데이터
  ui/GilmokApp.kt            화면 이동, 1700×1184 비율 맞춤, 시연 메뉴
  ui/components/             머리줄, 코치 바, 카드, 버튼
  ui/screens/                탭별 화면
app/src/test/                화면 캡처 테스트
```

## 사용한 글꼴

Noto Sans KR, Fredoka (둘 다 SIL Open Font License)

