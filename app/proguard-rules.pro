# Pleos Connect SDK는 AIDL과 직렬화를 써서, 이름이 바뀌지 않게 그대로 둬요.
-keep class ai.pleos.** { *; }
-dontwarn ai.pleos.**
# AAOS 차량 라이브러리는 차에만 있어요.
-dontwarn android.car.**
