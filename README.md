

# Welcome to MovieTime!

Hi! This project (Popular Movies Stage Two) is meant for Android Developer Nanodegree provided by  **UDACITY**. If you want to try the project, you should get an API key from themoviedb.org.
If you want to watch trailers within the app, you will require *Youtube ApiKey* more information https://developers.google.com/youtube/android/player/


# Api Usage

Add the API key you get from TMDB to **gradle.properties (Project Properties).**
Replace the text

> YOUR_TMDB_API_KEY

 with your api key without quotes ("")and build the project.

 Add the API key you get from YOUTUBE API KEY to **gradle.properties (Project Properties).**
Replace the text

> YOUR_YOUTUBE_API_KEY

 with your api key without quotes ("")and build the project.

# Features

With the app, you can:
- Search Movies or Person
-   Discover different
- Settings Option to help you get you relevant details
- Watch Trailers in App or Youtube App
- Share the movie information with others via Social Media
- See the Cast And Detailed Movie Information
- Recommendations based on Movie Type
- Similar movies
- Mark a Favorite and the movie details is now available offline


# Libraries

* [Retrofit](https://github.com/square/retrofit)
* [Picasso](https://github.com/square/picasso)
* [Room](https://developer.android.com/topic/libraries/architecture/room)
* [Paging](https://developer.android.com/topic/libraries/architecture/paging/)
* [Lifecycle](https://developer.android.com/topic/libraries/architecture/lifecycle)



# Broadcast Receiver

Broadcast receiver example to manipulate UI on network changes,
Respond to broadcasts. Works on N+ devices too.

# Animations

Simple slide in-out animations have been used.

# Android Architecture Components
The use of AAC has made it easy for developers to develop apps without worrying of paging and memory leaks. Whether it be Network Calls or Database Calls, AAC has it covered for you. This project saves the data in Details activity and as soon as user navigates back to the Main Activity, the user sees the fresh data. Similarly gone are those days of handling device rotations and deal with activity **re-** creation.
View Model provides an efficient way for managing the Orientation Changes without fetching data again from Database or Network. The use of **Retrofit alongside ViewModel** helps us to overcome what was previously a nightmare for developers. Gone are the days of using EndlessRecyclerView to fetch network data. ViewModel handles it all.


# Video
Get an idea of the app.
***
![Look and Feel of App](https://thumbs.gfycat.com/ForsakenDefensiveIrukandjijellyfish-size_restricted.gif)

General Look and Feel of App
***
![Trailer MovieTime](https://thumbs.gfycat.com/FailingBreakableBordercollie-size_restricted.gif)

Watch Trailers within app provided YoutubeApiKey is provided.
***


![Trailer MovieTime](https://thumbs.gfycat.com/UnsungInsignificantCat-size_restricted.gif)

Find Movie or Person
*****
![Settings MovieTime](https://thumbs.gfycat.com/ZanyBogusDoctorfish-size_restricted.gif)

Settings which change the api parameters to match your need.
***
![Favorite MovieTime](https://thumbs.gfycat.com/ShrillFrailBovine-size_restricted.gif)

Favorites and ViewModels *marvellous performance*

*****

# License
This project was submitted by HRankit  as part of the Nanodegree At Udacity.

Copyright (c) 2018 HRankit.

```
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
`````
