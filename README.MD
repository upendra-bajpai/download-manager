# Downloading file via a url with resume/pause function
The app aims to demonstrate the power of a new paging library combined with data-binding and superior architecture in android app development.

## File Structure
![File Structure in Android Studio](https://i.postimg.cc/q7q8wSrh/Screenshot-from-2022-04-02-18-41-06.png)

To resume a file url header needs to know *Range* to skip over.
DownloadTaskManager.kt has a code of downloading where to resume we
``` connection?.setRequestProperty("Range", "bytes=" + model.size + "-")```
in headers.