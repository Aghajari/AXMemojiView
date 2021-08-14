# AXMemojiView
<img width="845" alt="AXMemojiView" src="https://user-images.githubusercontent.com/30867537/100551530-6cfdfd00-3296-11eb-8963-026ba0641d44.png">

[![Platform](https://img.shields.io/badge/platform-android-green.svg)](http://developer.android.com/index.html)
[![API](https://img.shields.io/badge/API-16%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=16)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.aghajari/AXMemojiView.svg?label=Maven%20Central)](https://search.maven.org/artifact/io.github.aghajari/AXMemojiView/1.1.0/aar)
[![Join the chat at https://gitter.im/Aghajari/community](https://badges.gitter.im/Aghajari/community.svg)](https://gitter.im/Aghajari/community?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

 AXMemojiView is a new page for [AXEmojiView](https://github.com/Aghajari/AXEmojiView) which shows memoji just like stickers

## Screenshots 
<img width="250" alt="AXMemojiView" src="https://user-images.githubusercontent.com/30867537/100551778-2a3d2480-3298-11eb-915d-a0b7ab9ef763.png"> <img width="250" alt="AXMemojiView" src="https://user-images.githubusercontent.com/30867537/100551817-6f615680-3298-11eb-8f56-c5e8e6adc23c.png"> <img width="250" alt="AXMemojiView" src="https://user-images.githubusercontent.com/30867537/100551853-a172b880-3298-11eb-87a0-7bcbc0bc7687.png">

## Table of Contents  
- [Installation](#installation)  
- [Usage](#usage)
- [Customization](#customization)
- [MemojiProvider](#memojiprovider)
- [+1500 memoji Pack](#1500-memoji-pack)
- [MemojiLoader](#memojiloader)
- [Author](#author)
- [License](#license)

## Installation
You have to work with [AXEmojiView](https://github.com/Aghajari/AXEmojiView) first!

<a href="https://github.com/Aghajari/AXEmojiView"><img align="center" src="https://github-readme-stats.vercel.app/api/pin?username=aghajari&repo=AXEmojiView" alt="AXEmojiView" /></a>

AXMemojiView is available in the `mavenCentral()`, so you just need to add it as a dependency (Module gradle)

Gradle
```gradle
implementation 'io.github.aghajari:AXMemojiView:1.1.0'
implementation 'io.github.aghajari:AXEmojiView:1.4.0'
```

Maven
```xml
<dependency>
  <groupId>io.github.aghajari</groupId>
  <artifactId>AXMemojiView</artifactId>
  <version>1.1.0</version>
  <type>aar</type>
</dependency>
```

## Usage

you just need to install AXMemojiView after installing EmojiView. there is two option to do that, using your own MemojiProvider or read a json file as the MemojiProvider.

```java
AXMemojiManager.install(this, MemojiProvider);
```

and now you can add AXMemojiView to your AXEmojiPager as a page 
or just show AXMemojiView in a EmojiPopupLayout

```java
AXMemojiView memojiView = new AXMemojiView(context);
emojiPager.addPage(memojiView, icon);
```

## Customization
Customize theme with AXMemojiTheme.

```java
AXMemojiManager.getMemojiViewTheme().setSelectedColor(0xffFF4081);
AXMemojiManager.getMemojiViewTheme().setCategoryColor(Color.WHITE);
AXMemojiManager.getMemojiViewTheme().setAlwaysShowDivider(true);
AXMemojiManager.getMemojiViewTheme().setBackgroundColor(Color.LTGRAY);
...
```

## MemojiProvider

I'm using "[+1500 memoji Pack](https://www.figma.com/community/file/913339145625776252)" as the default MemojiProvider.

but you can use your own provider or improve this one.

the json format of the MemojiProvider will be like this : 
```json
{
 "CategoryName": {
  "CharacterName": [
   {
    "images": [
     {
      "name": "memoji_1_1.png",
      "skin": "white"
     },
     {
      "name": "memoji_1_2.png",
      "skin": "black"
     }
    ],
    "emoji": "😄",
    "posture": "Happy"
   }
  ]
 }
}
```

## +1500 memoji Pack
**[Moein Rabti](https://instagram.com/remim.dsgn) :** [+1500 memoji Pack](https://www.figma.com/community/file/913339145625776252) includes 28 characters in white and black skin color and 29 different poses.

<img width="550" alt="+1500 memoji Pack | Characters" src="https://user-images.githubusercontent.com/30867537/100552501-d5e87380-329c-11eb-8c7c-2df005d8c6be.png">

[assets/Memoji](https://github.com/Aghajari/AXMemojiView/tree/v1.0.0-alpha/app/src/main/assets/Memoji)

## MemojiLoader

Since the size of the memoji files is very large, it is better to upload them to the your server and load them with glide by using MemojiLoader :

```java
new MemojiLoader() {
           @Override
           public void onLoadMemoji(AppCompatImageView view, Memoji memoji) {

               // Load memojis from server
               /**String serverURL = "https://..../Memoji/";
               String url = serverURL + memoji.getCategory()+"/"+memoji.getCharacter()+"/"+memoji.getImage();
               Glide.with(view)
                .load(url)
                .into(view);*/

               // Load memojis from assets
               Glide.with(view)
                       .load(Uri.parse("file:///android_asset/Memoji/"+memoji.getCategory()+"/"+memoji.getCharacter()+"/"+memoji.getImage()))
                       .into(view);
           }

           @Override
           public void onLoadMemojiCategory(AppCompatImageView view, Memoji memoji, boolean selected) {
               onLoadMemoji(view,memoji);
           }
       };
```

you can also load the MemojiProvider as a json and download it from your server, in this way you can add new memoji packs to your app when ever you want.

## Author 
- **Amir Hossein Aghajari**

License
=======

    Copyright 2020 Amir Hossein Aghajari
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.


<br><br>
<div align="center">
  <img width="64" alt="LCoders | AmirHosseinAghajari" src="https://user-images.githubusercontent.com/30867537/90538314-a0a79200-e193-11ea-8d90-0a3576e28a18.png">
  <br><a>Amir Hossein Aghajari</a> • <a href="mailto:amirhossein.aghajari.82@gmail.com">Email</a> • <a href="https://github.com/Aghajari">GitHub</a>
</div>
