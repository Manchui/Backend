# Backend

> ## 📝 목차
> 1. [서비스 소개](#-서비스-소개)
> 2. [페이지 구성 및 기능](#페이지-구성-및-기능)
> 3. [R&R](#-rr)
> 4. [프로젝트 일정](#프로젝트-일정)
> 5. [Discord 를 활용한 소통 및 PR 알림 봇](#discord를-활용한-소통-및-pr-알림-봇-)
> 6. [프로젝트 환경](#%EF%B8%8F-프로젝트-환경)
> 7. [기술 선택 이유](#-기술-선택-이유)
> 8. [서비스 아키텍처](#-서비스-아키텍처)
> 9. [API 명세서](#-api-명세서)
> 10. [ERD](#%EF%B8%8F-erd)
> 11. [트러블 슈팅](#-트러블-슈팅)
> 12. [고민한 흔적](#-고민한-흔적)
> 13. [디렉토리 구조](#%EF%B8%8F-디렉토리-구조)

<br/>


## 🍻 서비스 소개
  - 만취는 많은 사람들이 함께할 수 있는 취미가 같은 사람들의 자유로운 모임 플랫폼입니다.
  - 사용자는 9개의 카테고리(개발, 운동, 영화, 공부, 문화/예술, 게임, 여행, 맛집, 음악) 중에서 자신의 취향에 맞는 모임을 선택하고, 인기 모임 목록도 확인할 수 있습니다.
  - 각 모임의 상세 페이지에서는 해당 모임의 위치와 지도를 확인할 수 있습니다.
  - 실시간 채팅을 통해 모임장과 모임원들 간의 자유로운 소통이 가능하며, 알림 기능으로 모임 관련 업데이트를 확인할 수 있습니다.
  - 모임이 끝난 후, 사용자는 직접 별점과 후기를 남길 수 있습니다.

- [만취 바로가기](https://manchui.vercel.app/)

## 페이지 구성 및 기능
|랜딩 페이지|로그인 페이지|회원가입 페이지|
|:---:|:---:|:---:|
|![image](https://github.com/user-attachments/assets/f14b22b3-0bf6-442e-a701-2015d829a898)|![image](https://github.com/user-attachments/assets/a76558d8-1c7c-4ced-90de-825025e0ae53)|![image](https://github.com/user-attachments/assets/373ce440-944a-4e38-98b3-1767be6bae50)|

|메인 페이지|찜한 모임 페이지|모임 등록 페이지|
|:---:|:---:|:---:|
|![image](https://github.com/user-attachments/assets/f14b878e-92fb-4290-a0c9-796f3b567ced)|![image](https://github.com/user-attachments/assets/aed772ac-bb93-41a9-89b0-b0712d301a97)|![image](https://github.com/user-attachments/assets/b0a0e06b-e0ad-4ac6-a44d-bcbfd03f4837)<br/>![image](https://github.com/user-attachments/assets/f9231f04-ba09-4506-961d-a8cecdb3554c)|

|모든 리뷰 페이지|마이 페이지|모임 상세 페이지|
|:---:|:---:|:---:|
|![image](https://github.com/user-attachments/assets/b64aed22-8378-457d-bafd-e31ed868fbd0)|![image](https://github.com/user-attachments/assets/5bc132ab-a49c-4f3b-b597-c1d8a46ef47c)|![image](https://github.com/user-attachments/assets/42476c2c-ecac-439a-91e1-a8d011160005)|

|FAQ 및 고객지원 페이지|알림 기능|실시간 채팅 기능|
|:---:|:---:|:---:|
|![image](https://github.com/user-attachments/assets/ecb026b3-de6d-40c8-809d-a322cdf4522b)<br/>![image](https://github.com/user-attachments/assets/ecb45998-31b9-4776-9570-6c96c97ae55a)|![image](https://github.com/user-attachments/assets/d4006d11-4059-4126-b3b9-250fbb7428df)|![image](https://github.com/user-attachments/assets/b43a4854-ee15-49df-9b4b-9f9d5e51742f)|


## 주요 기능 소개
#### ⭐ 메인 페이지: 모임 검색 및 필터링
- 9개의 다양한 카테고리(개발, 운동, 영화, 공부, 문화/예술, 게임, 여행, 맛집, 음악)를 통해 원하는 모임을 찾을 수 있습니다.
- 지역, 날짜, 키워드 검색으로 원하는 모임을 쉽게 찾을 수 있습니다.

#### ⭐ 모임 생성 및 관리
- 모임 이름, 카테고리, 설명, 장소, 최소/최대 인원 등을 설정하여 새로운 모임을 만들 수 있습니다.
- 모임 일정과 장소를 지도를 통해 직관적으로 설정할 수 있습니다.

#### ⭐ 실시간 채팅 기능
- 모임원들과 실시간으로 소통할 수 있는 채팅방을 제공합니다.
- 모임 관련 문의사항이나 정보를 자유롭게 공유할 수 있습니다.
- 채팅방에서 모임 관련 중요 공지사항을 확인할 수 있습니다.

#### ⭐ 알림 시스템
- 새로운 참여자 알림, 모임 찜하기, 채팅 메시지 등 중요한 업데이트를 실시간으로 받아볼 수 있습니다.
- 모바일에서는 좌•우 슬라이드로 알림 삭제가 가능합니다.
- 모임 관련 주요 알림을 놓치지 않고 확인할 수 있습니다.

#### ⭐ 리뷰 시스템
- 모임 참여 후 별점과 상세 리뷰를 작성할 수 있습니다.
- 카테고리, 지역, 날짜별로 리뷰를 필터링하여 볼 수 있습니다.
- 다른 사용자들의 경험을 통해 모임의 퀄리티를 미리 확인할 수 있습니다.

#### ⭐ 찜하기 기능
- 관심 있는 모임을 찜하기로 저장할 수 있습니다.
- 찜한 모임 페이지에서 저장한 모임들을 한눈에 확인할 수 있습니다.
- 찜한 모임의 일정과 상태를 쉽게 확인할 수 있습니다.

#### ⭐ 간편한 회원가입/로그인
- 이메일 기반의 간단한 회원가입 절차를 제공합니다.
- 소셜 로그인을 통한 빠른 가입도 지원합니다.

#### ⭐ FAQ 및 고객지원
- 자주 묻는 질문들을 카테고리별로 정리하여 제공합니다.
- 이메일이나 구글 폼을 통한 추가 문의가 가능합니다.
- 서비스 이용에 대한 상세한 가이드를 제공합니다.

### 👩🏻‍💻 R&R
| 담당자                                      | 담당 업무                                                        |
|:-------------------------------------------:|------------------------------------------------------------------|
| [강병훈](https://github.com/yosong6729) | 사용자 기능 구현 (로그인, 회원가입, 회원정보 조회, 회원정보 수정), 마이페이지 기능(사용자 참여 모임 목록 조회,  사용자 리뷰 목록 조회, 사용자가 만든 모임 목록 조회), 1대다 채팅 기능 구현  |
| [오예령](https://github.com/ohyeryung)       | 모임, 후기 도메인 개발 |

<br>

### 프로젝트 일정
<details>
    <summary><b>프로젝트 과정 타임라인 🗓</b></summary><br>
    <img src="https://github.com/user-attachments/assets/42e3dbe5-8afc-4c37-be28-35544828fbff">

</details>

<br>

### Discord를 활용한 소통 및 PR 알림 봇 🤖 

<details>
<summary>소통 및 PR 알림 확인</summary>
<div markdown="1">
   <img src="https://github.com/user-attachments/assets/405e8325-89c7-42be-945f-413d0bcef42f">
    <img src="https://github.com/user-attachments/assets/52c361d5-c22a-4a17-978c-68c6119e8f16">
    <img src="https://github.com/user-attachments/assets/63000938-1e80-4c04-a501-2323dd937b80">

</div>
</details>

<br/>

## 🛠️ 프로젝트 환경

### 기술 스택
<img src="https://github.com/user-attachments/assets/54ca2bcb-b582-46d9-89b3-45feba2f574a" width="500" />

### 인프라
<img src="https://github.com/user-attachments/assets/e90a0687-7d3e-482f-a85f-97883ad23bc2" width="500" />

<br/>

### ✅ 기술 선택 이유

TODO : 위키로 정리 예정

<br/>

### 🎨 서비스 아키텍처
<img src="https://github.com/user-attachments/assets/5390ba70-72e1-44cc-96c2-b4ebbe3310c3" width="700" />

<br/>


### 🧾 API 명세서

 > 자세한 명세는 <a href="https://documenter.getpostman.com/view/39384426/2sAYBVhBdg">🔗여기</a> 를 클릭해주세요! `(Postman API)`

<br>

## ⛓️ ERD
<img width="1221" alt="image" src="https://github.com/user-attachments/assets/8ffc76a4-d372-4d98-bc32-eb68f68fecd0">

<br>


## 💥 트러블 슈팅


<br>

## 🤔 고민한 흔적

<br>

## 🗂️ 디렉토리 구조
<details><summary>직관적인 구조 파악과 관리를 위해 <b>도메인형 구조</b>를 채택하였습니다. <b>(더보기)</b></summary>

