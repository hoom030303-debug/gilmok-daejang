# 길목대장

친구와 겨루는 안전운전 리그. 부드럽게 달린 사람이 길목의 대장이 돼요.

현대자동차그룹 차량용 앱 개발 공모전(Pleos Connect) 출품작이에요. 현대차·기아를 타는 친구들끼리 평소 운전 점수로 한 주 동안 겨루고, 자주 지나는 길(교차로, 스쿨존, 내리막)마다 그 길을 가장 부드럽게 지난 사람이 대장이 돼요. 주차하면 대장 코치가 "친구를 이기려면 이것 하나"를 소리로 알려 줘요.

## APK 위치

- `apk/길목대장_길목대장_release.apk` (서명된 release 버전)
- 같은 파일을 GitHub Releases에도 올려 두었어요: https://github.com/hoom030303-debug/gilmok-daejang/releases/tag/v1.0.0 (첨부 파일 이름은 GitHub 규칙상 영문 `gilmokdaejang_gilmokdaejang_release.apk`)

## 실행 방법

1. Android 기기나 에뮬레이터(API 28 이상)에 APK를 설치해요.
   ```
   adb install apk/길목대장_길목대장_release.apk
   ```
2. 앱을 열면 브랜드 화면 뒤에 홈이 나와요. 하단 탭(홈, 친구 지도, 운전 기록, 리그)으로 화면을 오가요.
3. 차에서 저절로 뜨는 화면(시동, 주차, 일요일 밤)은 **왼쪽 위 앱 아이콘을 길게 누르면** 나오는 시연 메뉴에서 바로 열 수 있어요.
4. 아래 대장 코치 바의 재생 버튼을 누르면 코치 문장을 한국어 음성(TTS)으로 읽어 줘요. 하단 탭 가운데 마이크 버튼은 음성 질문 화면이에요.

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
- 확인한 환경: Android 14(API 34) 에뮬레이터 태블릿, 화면 캡처 테스트(Robolectric + Roborazzi), 점수 계산 테스트(`DriveScorerTest`)
- 개발 환경: Android API 28 이상(minSdk 28, targetSdk 35), Gradle 8.10.2, Kotlin 2.0.21, Jetpack Compose, JDK 17
- Pleos SDK: Vehicle 2.3.2, NaviHelper 3.0.5 (Pleos Playground 공개 Maven 저장소, 신청 없이 사용)
- 권한: 차에서는 처음 실행할 때 차량 속도와 주행 정보 읽기 권한(`CAR_SPEED`, `pleos.car.permission.CAR_DRIVE`·`CAR_INFO`·`CAR_SAFETY`)을 물어요. 읽기 권한만 쓰고 차를 제어하는 권한은 쓰지 않아요.
- 음성 읽기는 기기의 한국어 TTS를 써요.

### 직접 빌드하기

```
./gradlew assembleDebug          # 디버그 APK
./gradlew testDebugUnitTest      # 모든 화면을 app/build/screens 에 PNG로 그려요
```

release 빌드는 `keystore.properties`(저장소에 올리지 않음)에 서명 키 정보가 있어야 해요.

## 차량 데이터 연결

Pleos Connect 차량에서는 `vehicle/PleosVehicleLink.kt`가 차량 신호를 받아 운전 한 번을 기록하고, P 기어를 넣는 순간 점수를 매겨 주차 코칭 화면을 띄워요. 차가 아닌 기기(휴대폰, 일반 에뮬레이터)에서는 연결이 실패하고 예시 운전 기록(`DemoVehicleDataSource`)으로 그대로 동작해요.

| 필요한 값 | 가져오는 곳 | 상태 |
| --- | --- | --- |
| 출발·주차 순간 | Vehicle SDK `Gear.registerGearSelection` | 연결함 |
| 급제동 판단 | Vehicle SDK `Brake.registerBrakePedalStatus` | 연결함 |
| 주행 거리 (10km 기준 감점) | Vehicle SDK `Odometer.registerTripCurrentDrivingDistance` | 연결함 |
| 속도, 급가속, 과속 | AAOS CarPropertyManager `PERF_VEHICLE_SPEED` | 연결함 |
| 제한 속도 | NaviHelper SDK `onCurrentRoadSpeedLimit` | 연결함 |
| 스쿨존, 사고 다발, 내리막 (2배 구간, 미리 감속) | NaviHelper SDK `onCautionPointInfo`, `onCameraAlert` | 연결함 |
| 위치, 길 나누기 | FusedLocation SDK | Pleos에서 준비 중 (2차 전 연결) |
| 코치 목소리, 음성 질문 | TextToSpeech SDK, SpeechToText SDK | Pleos에서 준비 중, 지금은 안드로이드 기본 TTS |

점수 계산은 `data/DriveScorer.kt`에 있고, 안드로이드에 의존하지 않아 테스트로 확인해요. 23.4km 출근길 예시(스쿨존 급제동 1번, 급가속 2번, 미리 감속 놓침 1곳, 미리 감속 성공 2곳)를 신호로 재생하면 `100 − 11.7 + 2.7 = 91점`이 나와요.

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
  data/                      점수 규칙, 운전 점수 계산기, 예시 데이터
  vehicle/                   Pleos Vehicle·NaviHelper SDK, 차량 속도 연결
  ui/GilmokApp.kt            화면 이동, 1700×1184 비율 맞춤, 시연 메뉴
  ui/components/             머리줄, 코치 바, 카드, 버튼
  ui/screens/                탭별 화면
app/src/test/                화면 캡처 테스트, 점수 계산 테스트
```

## 사용한 글꼴

Noto Sans KR, Fredoka (둘 다 SIL Open Font License)

