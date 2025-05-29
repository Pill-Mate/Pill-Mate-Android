# 💊필메이트 – 나만의 복약 관리 & 약물 충돌 알림

![그래픽이미지_720x280](https://github.com/user-attachments/assets/0151bbb9-dd3a-42b1-b7c5-91fa937702ce)

- 배포 URL : [🔗 필메이트](https://play.google.com/store/apps/details?id=com.pill_mate.pill_mate_android&hl=ko)
<br>

## 1. 프로젝트 소개

나만의 복약 관리 & 약물 충돌 알림 필메이트는 다제약물 사용자와 복약이 필요한 모든 분들을 위한 스마트 복약 관리 앱입니다.

<br>

## 2. 팀원 구성

<div align="center">

| **강현정** | **김다희** |
| :------: |  :------: |
| [<img src="https://github.com/user-attachments/assets/b2fde1af-85e6-49cc-b46a-cb960c096130" height=150 width=150> <br/> @BanDalKang](https://github.com/BanDalKang) | [<img src="https://github.com/user-attachments/assets/a8c29331-5c90-4e82-bd9e-e5ab82334e1c" height=150 width=150> <br/> @kimdahee7](https://github.com/kimdahee7) | 

</div>

<br>

## 3. Key Features (주요 기능)
- **복약 스케줄 관리**:
  - 개인 생활 패턴에 맞춰 자동으로 복약 시간을 설정
  - 한눈에 보기 쉬운 체크리스트 제공
    
- **약물 충돌 감지 & 안내**:
  - 복용 중인 약물과 새로 등록한 약물 간 성분 충돌 여부 확인
  - 복약 오남용을 방지하고 안전한 복용을 지원
    
- **전문가 상담 연결**:
  - 약물 충돌 발생 시, 처방받은 약국·병원으로 바로 연결
  - 보다 정확한 상담을 통해 안전한 복약 결정
    
- **간편한 약물 등록**:
  - 병원·약국에서 처방받은 약을 쉽게 등록
  - 의약품 공공 데이터를 활용한 정확한 약물 정보 제공

<br/>
<br/>

## 4. Tasks & Responsibilities (작업 및 역할 분담)
### 🐋강현정
- **💊약물 등록 기능 (medicine_registration)**
  - 단계별 약물 등록 UI 및 스케줄 생성 기능 구현
  - 약국/병원 검색 & 일반/전문의약품 검색 기능 구현
  - 의약품 정보 및 기관 정보 API 연동

- **⚠️약물 충돌 확인 기능 (medicine_conflict)**
  - 성분 기반 병용금기 확인 및 결과 시각화
  - 약물 상세 정보 및 사용자 병원/약국 연락 기능 구현

- **✏️약물 수정 기능 (pilledit)**
  - 등록 약물의 복용 정보 수정 기능 구현
  - 시간 설정 BottomSheet 및 입력 유효성 검증 로직 적용
<br>
    
### 🌱김다희
- **✅ 복약 체크 기능 (pillcheck)**
  - 캘린더 기반 날짜 선택 및 일별 복약 일정 조회 기능 구현
  - 시간대별 약물 리스트 및 개별/전체 복약 체크 기능 구현 

- **🚀 온보딩 기능 (onboarding)**
  - 최초 사용자 진입 시 온보딩 슬라이드 UI 구성  
  - 기상/취침/식사 시간 등 사용자 루틴 정보 입력 flow 구현  

- **🔐 카카오 로그인 기능 (login)**
  - Kakao OAuth 연동을 통한 간편/자동 로그인 기능 구현  

- **👤 마이페이지 기능 (setting)**
  - 사용자 정보 및 루틴 변경 UI 구성


<br>

## 5. Technology Stack (기술 스택)
### 5.1 Language
|  |  |
|-----------------|-----------------|
| Kotlin    |<img src="https://github.com/user-attachments/assets/175f1129-6c24-4383-a9b8-f836b5410fa9" alt="Kotlin" width="80">| 

<br/>

### 5.2 Android
|  |  |  |
|-----------------|-----------------|-----------------|
| Android Studio    |  <img src="https://github.com/user-attachments/assets/caed7805-c667-4611-b0bf-a5143852d9a7" alt="Android Studio" width="100"> | Koala 2024.1.1 Patch 1    |

<br/>

### 5.3 Cooperation
|  |  |
|-----------------|-----------------|
| Git    |  <img src="https://github.com/user-attachments/assets/483abc38-ed4d-487c-b43a-3963b33430e6" alt="git" width="100">    |
| Git Kraken    |  <img src="https://github.com/user-attachments/assets/32c615cb-7bc0-45cd-91ea-0d1450bfc8a9" alt="git kraken" width="100">    |
| Jira    |  <img src="https://github.com/user-attachments/assets/f61a6e9e-0b52-4900-b5a4-9f43333de581" alt="Notion" width="100">    |
| Notion    |  <img src="https://github.com/user-attachments/assets/34141eb9-deca-416a-a83f-ff9543cc2f9a" alt="Notion" width="100">    |

<br/>

## 6. Project Structure (프로젝트 구조)
```plaintext
pill_mate_android/                            # 다제약물 복약 관리 앱 Android 프로젝트
├── login/                                    # 로그인 및 인증
│   ├── model/
│   └── view/
├── main/                                     # 메인 화면
│   ├── contract/
│   ├── presenter/
│   └── view/
├── medicine_conflict/                        # 약물 충돌 확인
│   └── model/
├── medicine_registration/                    # 약물 등록
│   ├── api/
│   ├── model/
│   └── presenter/
├── onboarding/                               # 초기 사용자 설정(온보딩)
│   ├── model/
│   └── view/
├── pillcheck/                                # 복약 체크 및 홈
│   ├── model/
│   ├── util/
│   └── view/
├── pilledit/                                 # 약물 관리
│   ├── api/
│   ├── model/
│   ├── util/
│   └── view/
├── schedule/                                 # 복약 스케줄 설정
├── search/                                   # 약물 검색
│   ├── api/
│   ├── model/
│   ├── presenter/
│   └── view/
├── setting/                                  # 마이페이지
│   ├── model/
│   ├── view/
│   │   ├── dialog/
│   │   └── ...
│   └── util/
├── util/                                     # 전역 유틸리티
│   ├── GlobalApplication.kt
│   ├── ServiceCreator.kt
│   └── TokenInterceptor.kt
├── .gitignore                                # Git 무시할 파일 설정
└── README.md                                 # 프로젝트 설명서
```

<br/>
<br/>

## 7. Development Workflow (개발 워크플로우)
### 브랜치 전략 (Branch Strategy)
브랜치 전략은 Git Flow를 기반으로 하며, 다음과 같은 브랜치를 사용합니다.

- **Main Branch**
  - 배포 가능한 상태의 코드를 유지합니다.
  - 실제 서비스 배포는 항상 이 브랜치에서 이루어집니다.

- **develop Branch**
  - 팀원들의 작업 내용을 통합하는 브랜치입니다.
  - feature 브랜치에서 완료된 작업은 이곳으로 병합됩니다.
 
- **release Branch**
  - 테스트를 위한 코드를 유지합니다.
  - 테스트를 위한 배포는 이 브랜치에서 이루어집니다.
  
- **feature/{num} Branch**
  - 각 기능 단위로 작업하는 팀원 개인 브랜치입니다.
  - 기능별로 새로운 브랜치를 생성합니다.

<br/>

## 8. Coding Convention
기본적으로 공식문서인 Kotlin Coding Conventions과 Kotlin style guide를 준수합니다.
