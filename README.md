# 감정 분석 일기 (Feeling Diary)

## 프로젝트 개요

### 선정 배경
현대 사람들은 자신의 감정을 정확히 인식하고 관리하는 데 어려움을 겪고 있습니다. 감정 이해 및 관리 능력이 중요하므로 이를 위한 적절한 지원 및 도구의 필요성이 대두되었습니다.

### 기획 의도
- 정서적 자기 인식 강화
- 감정 관리 능력 향상
- 증가하는 우울증 환자 지원

### 목적
사용자가 일상 속의 감정을 더 잘 이해하고 관리할 수 있도록 지원합니다.

### 대상 사용자
- 일상 속에 지친 현대인
- 직장 생활에 스트레스가 많은 직장인

### 목표
- 정서적 자기 인식 강화
- 감정 관리 능력 증대
- 개인화된 감정 분석 제공

## 프로젝트 수행 절차 및 방법

### 사전 기획
- 프로젝트 주제 선정 및 기획 문서 작성
- 라이브러리 탐색 (OpenVINO, DeepFace, Huggingface 등)
- 모델 성능 평가 및 선정

### 개발
- Kotlin을 활용한 앱 개발
- Flask를 활용한 서버 구성

### 활용 장비 및 리소스
- **프레임워크**: Android Studio, Python, Figma
- **서버 및 인증**: Flask, Firebase
- **협업 도구**: Notion, GitHub
- **AI 모델**: OpenVINO, Huggingface

## 프로젝트 수행 경과

### 모델 탐색 및 선정
- OpenVINO 활용 표정 감정 추론
- HuggingFace 파이프라인 활용 텍스트 감정 추론

### 데이터 탐색
- Kaggle의 얼굴 표정 데이터셋 EDA
https://www.kaggle.com/datasets/himanshuydv11/facial-emotion-dataset/data

### 모델 선정
- BERT, RoBERTa, DistilBERT, DistilRoBERTa 모델 비교 및 선정

### 앱 디자인 및 구현
- Figma를 활용한 UI/UX 디자인
- Navigator 및 Firebase Auth를 사용한 화면 전환 및 사용자 인증 기능 구현
- Naver 검색 API를 활용한 뉴스 제공 기능 추가
- MPAndroidChart 라이브러리를 사용한 그래프 및 차트 구현

### 서버 구축
- Flask를 활용한 텍스트 추론 서버 구축

## 부록
- 얼굴 표정 데이터 세트 추가 EDA
- 추가 기획: Feeling Diary 커뮤니티

---

## 설치 및 실행 방법

### 요구 사항
- Android Studio
- Python 3.x
- Flask
- Firebase

### 설치
1. 이 저장소를 클론합니다.
   ```bash
   git clone https://github.com/your-repo-url.git
