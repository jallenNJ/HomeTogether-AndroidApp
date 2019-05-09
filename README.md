# HomeTogether-AndroidApp

## Introduction
This is the Android front end for Home Together, which will be the user’s way of interacting with the server. The front end is written primarily in Java; however, the Shopping List activity is written in Kotlin. The app makes uses of gestures to allow for the users to swipe to interact with parts of the application. 

## Installation Instructions
1.	Put APK on the phone
2.	Tap the APK
3.	Allow it access to install

• Grant any permissions it asks for, which may include
  * INTERNET – To allow the app to communicate with the server

• If the installation is blocked due to being unsigned, the phone may need to be put into developer mode. If this step is required, and how to enable it, depends on the specific version of android. Consult the appropriate documentation for target device’s flavor of android for how to do that step. 

## Emulation Note
The app does not make use of the local file system so therefore it should run perfectly fine on an emulator. The only issue that I encountered with emulation is that the launcher icon of the app does not always displays, and instead uses the default. 
If an emulator is used though, the app does take a usability hit, as giving swipe gestures from a mouse is not as intuitive as using an actual smart screen, and therefore the given swipe gesture may be rejected.
