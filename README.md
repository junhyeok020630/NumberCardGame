# 🃏 숫자 카드 게임 (Number Card Game)

> **"당신의 기억력을 테스트하고 최고의 기록에 도전하세요!"**
> 안드로이드 기반의 직관적이고 재미있는 숫자 카드 맞추기 게임 애플리케이션입니다.

<div align=center>
  <img src="https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white"/>
  <img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white"/>
  <img src="https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white"/>
  <img src="https://img.shields.io/badge/Android_Studio-3DDC84?style=for-the-badge&logo=android-studio&logoColor=white"/>
</div>

---

## 📖 소개

**숫자 카드 게임**은 무작위로 배치된 카드들 중 같은 숫자를 찾아내는 클래식한 메모리 게임입니다. 깔끔한 UI와 실시간 사운드 피드백을 통해 몰입감 있는 게임 경험을 제공하며, 안드로이드의 커스텀 뷰와 다이얼로그 기능을 학습하기 위해 제작되었습니다.

- 🧠 **기억력 강화**: 짧은 시간 안에 카드의 위치를 기억하여 짝을 맞추는 두뇌 트레이닝 프로젝트입니다.
- 🎨 **커스텀 UI**: 안드로이드 `CustomView`를 직접 구현하여 독창적인 게임 인터페이스를 구축했습니다.
- 🔊 **생생한 피드백**: 정답과 오답 상황에 따른 효과음을 배치하여 게임의 재미를 더했습니다.

---

## ✨ 주요 기능

| 기능 | 상세 설명 |
|:---:|---|
| 🃏 **카드 매칭** | 숫자 0부터 9까지의 카드가 무작위로 배치되며, 같은 짝을 찾아 점수 획득 |
| 🔊 **사운드 효과** | 성공(`rightact`) 및 실패(`wrong`) 시 전용 MP3 효과음 재생으로 즉각적인 피드백 제공 |
| ⚙️ **게임 설정** | `SettingView`를 통해 게임 환경을 직접 조절하고 나만의 게임 플레이 구성 |
| 🏆 **결과 화면** | 게임 종료 시 `GameOverDialogFragment`를 통해 최종 점수 확인 및 재시작 기능 지원 |
| 🖼️ **테마 그래픽** | 'Dream' 시리즈 이미지를 활용한 세련된 카드 뒷면 디자인 적용 |

---

## 🚀 실행 가이드

이 프로젝트는 Android Studio 환경에서 빌드 및 실행이 가능합니다.

### 1️⃣ 프로젝트 가져오기
* GitHub 레포지토리를 클론하거나 압축 파일을 다운로드합니다.
* **Android Studio**를 실행하고 `Open` 버튼을 눌러 해당 프로젝트 폴더를 선택합니다.

### 2️⃣ 환경 설정 및 빌드
* **Gradle Sync**: 프로젝트가 열리면 자동으로 시작되는 Gradle Sync가 완료될 때까지 기다려 주세요.
* **SDK 확인**: 안드로이드 SDK 설정이 맞지 않을 경우, 알림창의 `Install missing platform(s)`를 클릭하여 필요한 버전을 설치합니다.

### 3️⃣ 실행하기
* 상단 메뉴의 **Run 'app'** (초록색 재생 버튼)을 클릭합니다.
* 연결된 안드로이드 에뮬레이터 또는 실제 기기를 선택하여 게임을 실행합니다.

---

## 📁 프로젝트 구조

```bash
NumberCardGame/
├── 📂 app/
│   ├── 📂 src/main/java/org/techtown/hello/
│   │   ├── 📄 MainActivity.java       # 게임 메인 로직 및 흐름 제어
│   │   ├── 📄 StartActivity.java      # 게임 시작 인트로 화면
│   │   ├── 📄 CustomView.java         # 게임 보드 및 카드 렌더링 커스텀 뷰
│   │   ├── 📄 SettingView.java        # 게임 설정 인터페이스
│   │   └── 📄 GameOverDialogFragment.java # 게임 종료 알림창
│   └── 📂 src/main/res/
│       ├── 📂 drawable/               # 숫자 카드 및 배경 그래픽 리소스
│       ├── 📂 raw/                    # 게임 효과음 (mp3)
│       └── 📂 layout/                 # XML 기반 레이아웃 설계 파일
└── build.gradle.kts                   # 프로젝트 빌드 및 의존성 설정
```

---

## 🛠️ 기술 스택 (Tech Stack)

### **Platform & Language**
![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)

### **Development Tools**
![Android Studio](https://img.shields.io/badge/Android_Studio-3DDC84?style=for-the-badge&logo=android-studio&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white)
![Git](https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=git&logoColor=white)

---
