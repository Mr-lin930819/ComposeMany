# ComposeMany
使用jetpack compose构建的app

**项目仅供学习，不做商业用途。**



主页面实现的界面有两项：

<img src="https://github.com/Mr-lin930819/ComposeMany/blob/0f5ac3f8cbf3ab24882ec24b5bdf1aabc7c6f2fd/screenshots/main.png" width=240 alt="主界面" />

### 音乐

- 音乐功能借鉴了Flutter项目：[Flutter 版本的网易云音乐 ](https://github.com/fluttercandies/NeteaseCloudMusic)
- 音乐API使用： [Binaryify/NeteaseCloudMusicApi: 网易云音乐 Node.js API service (github.com)](https://github.com/Binaryify/NeteaseCloudMusicApi)，使用Vercel构建

<img src="https://github.com/Mr-lin930819/ComposeMany/raw/main/screenshots/music_main.jpg" width=240 alt="music_main" />  <img src="https://github.com/Mr-lin930819/ComposeMany/raw/main/screenshots/play_list.jpeg" width=240 alt="play_list" />  <img src="https://github.com/Mr-lin930819/ComposeMany/raw/main/screenshots/song_play.png" width=240 alt="song_play" />
<img src="https://github.com/Mr-lin930819/ComposeMany/raw/main/screenshots/comments.png" width=240 alt="comments" />  <img src="https://github.com/Mr-lin930819/ComposeMany/raw/main/screenshots/floor_comment.jpg" width=240 alt="floor_comment" />

#### 使用到的架构组件

- Hilt
  
  全应用的实例依赖管理
- ViewModel
  
  视图数据以及状态的管理
- Navigation

  页面跳转管理
- Room

  数据库访问
- Paging 3

  分页数据载入
- datastore

  参数保存
- splashscreen

  启动屏适配

#### 使用到的其他第三方库

- Accompanist

  提供Compose下的ViewPager、骨架屏、状态栏操作等
- Retrofit

  RESTful API接口通讯实现
- Coil

  kotlin图片加载框架
- toolbar-compose

  实现折叠工具栏，源地址：[onebone/compose-collapsing-toolbar](https://github.com/onebone/compose-collapsing-toolbar)


### 基金

基金页面仿支付宝基金功能，仅作练习，**无任何实际功能**

<img src="https://github.com/Mr-lin930819/ComposeMany/blob/0f5ac3f8cbf3ab24882ec24b5bdf1aabc7c6f2fd/screenshots/fund_main.png" width=240 alt="fund_main" />


