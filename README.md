# Dicoding Story App
A simple social media app for Dicoding student to share their learning story and achievment (Implementing Room, Retrofit, MVVM Architecture, Dependency Injection using Dagger Hilt, Repository Pattern, Paging3, Unit Testing).

### App Features
* **Movie** - menampilkan daftar movie terbaru
* **TV Show** - menampilkan daftar tv show terbaru
* **Movie Favorite** - menampilkan daftar movie yang sudah ditambahkan sebagai favorite
* **TV Favorite** - menampilkan daftar tv yang sudah ditambahkan sebagai favorite
* **Search Movie & TV** - untuk melakukan pencarian movie & tv

### Screenshot
<span align="center">
 <hr>
 <p align="center"><img src="screenshot/banner.png" alt="Filmbase Screenshot" width="850" height="500"></p>
 <p align="center">Screenshot</p>
 <hr>
 </span>

### API
Api yang digunakan dalam project ini yaitu https://story-api.dicoding.dev/v1

Base URL yang digunakan adalah sebagai berikut
```
https://story-api.dicoding.dev/v1
```

#### Endpoint Used

|Method | Endpoint | Usage |
| ---- | ---- | --------------- |
|GET| `/register` | Register User.|
|GET| `/login` | Login User.| 
|GET| `/stories` | Get All Stories.| 
|GET| `/stories/:id` | Get Story Detail.| 
|POST| `/stories` | Add New Story.| 

