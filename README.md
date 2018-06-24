# 基于Android的直播平台
## 简介
本科毕业设计。采用C/S架构，这里只给了安卓客户端的源代码。
<br>UI模仿Bilibili的Android端的一部分，很多实现也只是自己估摸着，效果其实和B站存在着一些差距。
<br>Email:fuyu273781076@gmail.com
## 功能
* 登录注册
* 摄像头推流（Yasea）
* rtmp拉流播放（ijkplayer播放器）
* 聊天室及弹幕（Openfire+smack库+烈焰弹幕使）
* 分类标签模块（界面模仿的新版的B站客户端2018-6）
* 我的直播间设置（可设置封面、房间名、标签、简介）
* 关注
* 搜索
* 广告横幅（使用Glide从服务器加载图片有Bug）
## 主要界面截图

## 项目演示的视频地址
* youtube 
* 优酷 
## 主要使用的开源项目
* Ijkplayer https://github.com/Bilibili/ijkplayer
* DanmakuFlameMaster https://github.com/Bilibili/DanmakuFlameMaster
* Openfire/smack  https://www.igniterealtime.org/projects/
* Yasea https://github.com/begeekmyfriend/yasea
* Glide https://github.com/bumptech/glide
* nginx-rtmp-module https://github.com/arut/nginx-rtmp-module
