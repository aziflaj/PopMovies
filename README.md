# PopMovies
Popular Movies App: A task from Android Nanodegree @ Udacity

**PopMovies** is a movies app that will allow a user to discover popular movies. [Read here](https://docs.google.com/document/d/1gtXUu1nzLGWrGfVCD6tEA0YHoYA9UNyT2yByqjJemp8/pub?embedded=true) the project description.

## To Do
  - ~~**Use `Fragments` for showing views, `Activities` as `Fragment` containers**~~
  - ~~Write an `AsyncTask` class to fetch the movies from the cloud~~ ([693ddad](http://git.io/vOl7N))
  - ~~Show the data using an (custom-made) `Adapter`~~ ([2f50626](http://git.io/vOl5e))
  - ~~Show detailed view when choosing a movie~~ ([5a18028](http://git.io/vOl5I))
  - ~~Use preferences (from the Settings Activity) to sort the movies (possible bug)~~ ([5cd1326](http://git.io/vOl5m))
  - ~~Create the database with the needed table(s)~~ ([220e7ec](http://git.io/vOl5Z))
  - ~~Write a content provider and store movies into the DB~~ ([4bcc072](http://git.io/vOl5B))
  - ~~Use a `CursorAdapter` to show the data from the database~~ ([dfc6be0](http://git.io/vOl5g))
  - Make the app responsive (Tablet support)
    - Show the selected item
  - Add the magic: Make the app syncable on the background
  - Show notifications (for what?)


## Consider using
  - ~~[Fresco](http://frescolib.org/) for image loading~~
  - [Gson](https://github.com/google/gson) for JSON processing :white_check_mark:
  - Automate Content Provider generation with [ProviGen](https://github.com/TimotheeJeannin/ProviGen)
  - ORM instead of SQLite/ContentProvider:
    - [Realm](https://realm.io/docs/java)
    - [GreenDAO](http://greendao-orm.com/)
    - [Sugar ORM](http://satyan.github.io/sugar/index.html)
  - [OkHttp](http://square.github.io/okhttp/) as HTTP+SPDY Client
  - [Retrofit](http://square.github.io/retrofit/) as REST client :white_check_mark:
