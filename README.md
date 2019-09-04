# DailyCloset(패션다이어리)
==============================
> ## MainActivity
----------------------
  > > ### TedPermission
  > >  - 위치권한, 카메라 권한, 파일접근 권한을 얻기 위해 권한 요청
  > > ### BottomNavigationView
  > >  - 프레그먼트 이동을 위한 네비게이션
  > > ### GeoCoder
  > >  - 위치 정보를 습득하고 기본 언어를 설정한다.
  > >  - WeatherFragment로 위치 정보 전달
  > > ### IntroMainActivity
  > >  - 애플리케이션을 처음 실행시킬 경우 사용법에 대한 설명을 가진 엑티비티를 호출한다.
  > >  - Animation

> ## WeatherFragment
  > > ### OpenWeatherAPI
  > >  - 위치 정보에 따른 날씨 정보를 얻기 위해 날씨 정보 요청
  > >  - 날씨에 따라서 출력되는 이미지 변경
  > >  - MainActivity의 BottomNavigationView에 날씨 이미지 변경 이벤트 송신\ 

> ## CalendarFragment
  > > ### PlannerView
  > > - 달력 생성을 위한 커스텀 뷰
  > >  - 달력의 날짜를 클릭할 경우 이벤트 수신
  > - 달 변경 등의 이벤트 수신 및 출력 담당
    #### TakePictureDialog
      > - 달력의 해당 날짜에 저장할 사진을 직접 찍는 경우 출력
      > - Preview객체를 통해 카메라 이벤트 송수신(Camera2 API)
      > - 촬영 시 사진 저장
      > - 이미 찍은 사진이 있는 경우 해당 날짜의 사진을 출력(ImageView)
    #### SelectDialog
      > - 달력의 해당 날짜에 저장할 사진을 갤러리에서 불러올지, 직접 찍을지 결정하는 Dialog
## GalleryFragment
  ### RecyclerView
    > - gallery_item(CustomView)를 바인드
    > - 최근을 기준으로 애플리케이션을 통해 등록한 사진을 출력
  > - 크게보기, 작게보기 기능 추가
  > - 즐겨찾기, 사진삭제, 사진 검색 기능
 
## SettingFragment
  > - 푸시 알림 허용
  > - 사용 언어 설정
