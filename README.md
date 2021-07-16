# An Android Mobile Application for Assisting Students with Disabilities

We developed an android mobile application with many functionalities dedicated to assisting students with visual, hearing and mobility impairment in overcoming barriers and accessing the university’s facilities and systems while they are in the university.The application directs students to their desired destination by audio guidance and captions, help students with hearing impairment communicate clearly, keep students notified of special events, and help them when they get in trouble by connecting them to volunteers through a video call.

* Note: the application is meant for university purpose, but it can be used for any purpose.
* Tools that are used : Android Studio, Firebase, Google Maps APIs, Speech Recognizer API, Google Duo.
* The application is inspired by: Lazarillo GPS for the blind, Connect by BeWarned, Be My Eyes.

# Class Diagram: 
![class](https://user-images.githubusercontent.com/67188835/103522902-1767d200-4e8c-11eb-868d-5b0929312729.PNG)

# Functional Requirements

## Maps Navigation
* The system should be able to display a map
* The user should be able to search for destination
* The user should be able to choose a location directly from the map
* The system should inform the user by his/her surrounding
* The system should provide a list of nearby places based on category around the user.
* The system should provide the details of the trip to the destination
* The system should be able to provide the fastest and best-fit route to the destination
* The system should direct the user by turn-by-turn instructions by voice and captions
* The system should be able to locate and provide the user location

## Dialog for the Hearing Impairment
* The system will receive a real-time speech or audio as input
* The system will display a text corresponding to the received speech or audio
* The user should be able to type text and display it on screen
* The user should be able to turn the written text into audio by clicking play button
* The system should be able to distinguish user A from user B by giving them different colors
* The user should be able to choose text from ready-to-use templates and display it on screen

## Emergency Video Call
* The system should be able to direct the user to make video call with assistant on Google Duo app

## Calendar
* The user should be able to add, update, delete special events dates
* The system should be able to notify the user based on voice, vibration, caption
* The system should display calendar with all added events

## Help Center Call
* The system should provide phone numbers and direct the user to conduct a call to help centers

## Real-Time Notification
* The administrator should be able to send a real-time notification to all users of the App
* The system should be able to notify the user based on voice, vibration, caption

## Feedback Form
* The user should be able to fill a form and submit it
* The system should be able to send the form to a particular email.

## Based on App
* The user should be able to register
* The user should be able to login
* The student should be able to add and update profile information

# Home Page
![Homepage EVC](https://user-images.githubusercontent.com/67188835/103524788-31ef7a80-4e8f-11eb-9b1a-b5eba8a52ca7.PNG)



# Maps Navigation: 
The maps page contains three essential buttons; each provides a specific function. 
## Explorer:
First, we have Explorer button, which uses Places API to retrieve nearby places in the range of 100 meters. When the user presses the explorer button the application retrieves nearby places and computes the distance from the user location to a particular nearby place. Moreover, it computes the direction to a place relative to the user orientation. Then it displays the information on screen one by one for the ease of reading it by a talkback screen reader, and it turns the text to speech while displaying the information.

![explorer](https://user-images.githubusercontent.com/67188835/103523160-87765800-4e8c-11eb-939f-01dfa856471d.PNG)

## My Location:
In addition, we have my location button, which displays the address of the user on screen for ease of reading by talkback screen reader

![my location png](https://user-images.githubusercontent.com/67188835/103529836-6b2be880-4e97-11eb-971f-a01b515081e2.png)

## Search:
Third, we have the search button, which enables the user to search for a location using a regular google maps view. The user can type a place in the search bar; the search bar supports voice search. The speech recognition language is based on the language of the application. When a place is given, a suggestions list will pop up, and the user can select the place he/she is looking for from the suggestions list. Once the user clicks on it, he/she will be directed to trip information page.

At the bottom, we have two buttons; the first is go to button, which enables the user to select any place on the map, then the application will direct the user directly to the turn-by-turn navigation in google maps based on walk mode or car mode.

The second button is University buildings button, which contains a list of places that the user can choose from. We can add any place of our choice to the list. Once the user clicks on it, he/she will be directed to trip information page.

![search](https://user-images.githubusercontent.com/67188835/103523550-0ec3cb80-4e8d-11eb-8283-09eb4f3a1437.PNG)

## Nearby Places Based on Categories:
Moreover, on the maps page, we have nearby places based on categories. It enables the user to look for nearby places in the range of 1500 meters based on the selected category. The application retrieves nearby places and computes the distance from the user location to a particular nearby place. Moreover, it computes the direction to a place relative to the user orientation. The nearby places will be displayed in the form of a list for ease of reading by talkback screen reader. When the user selects a place on the list, he/she will be directed to trip information page.

![nearby](https://user-images.githubusercontent.com/67188835/103523790-75e18000-4e8d-11eb-829a-7017e92be880.PNG)

## Trip Information Page:
The trip information page displays detailed information about a place. It displays the place's name, the type of the place, distance to the place, and direction to the place relative to user orientation. 

Once the user enters the trip information page, the application will turn the text to speech. The text to speech will repeat the distance and the direction every 15 seconds. The user can navigate turn-by-turn based on car and walk mode. Once the user chooses one of these modes, then the application will direct the user directly to the turn-by turn navigation in Google Maps application.

![trip](https://user-images.githubusercontent.com/67188835/103523957-cd7feb80-4e8d-11eb-90ad-2739c4331462.PNG)

# Sign Up:
Once the user opens the sign-up form, the user must enter email address, password, name, phone number. The password requirement to be no longer than 8 characters.

![sign up](https://user-images.githubusercontent.com/67188835/103524121-0e780000-4e8e-11eb-9a7a-3df5f2d6e2a1.PNG)

# Log In:
Once the user opens the application, the user has two options for him to use the application. The user can use the application without registration; the user can sign up with his/her own information. After the user has signed up, the next time, the user will login and use the application. For login, the user must enter the same information entered in the sign-up form. If the user used the application without registration, some of the features will not be available for him. 

![login](https://user-images.githubusercontent.com/67188835/103524225-3cf5db00-4e8e-11eb-816f-2b06f7a6eb1e.PNG)

# Personal Profile:
User can add more information and modify his/her profile and save it by pressing the save button. The information that he/she can add beside the sign-up information are student id, type of disability (hearing impairment, vision impairment, physical impairment, other), the degree of disability (light, average, strong), age, and the gender of the user.

![peronal png](https://user-images.githubusercontent.com/67188835/103526388-e4c0d800-4e91-11eb-8ec2-7f459be023e6.PNG)


# Dialog (Communicate):
Two users should use it. The first user types a text and displays it on screen, with the ability to convert the text to speech by pressing the voice button. The second user can speak and then the application will convert the speech to text and display it on screen; while the second user speaks the microphone button must be enabled. The first user's text is distinguished from the text of the second user by different colors and different alignments. The users can use the ready to use templates by pressing the template button on the left bottom corner, which basically is text to use without the need to type them again or speak them again.


![dialog](https://user-images.githubusercontent.com/67188835/103524608-e210b380-4e8e-11eb-9a6a-71bd0ab299b6.PNG)

#  Emergency Video Call:
The video call is conducted using the Google duo App. When the user has Google Duo account, the application will directly connect him to the person responsible for receiving video calls. 

![Homepage EVC](https://user-images.githubusercontent.com/67188835/103524788-31ef7a80-4e8f-11eb-9b1a-b5eba8a52ca7.PNG)

# Calendar:
User can add an event by choosing the date and add information regarding the event, then the event is added with a countdown, and it will be ordered by sooner to older. When that date has come, the user will receive a notification to remind him of the event. The event can be modified by pressing the event, and it can be deleted by swiping or pressing the event and pressing delete.


![Calendar png](https://user-images.githubusercontent.com/67188835/103526493-0fab2c00-4e92-11eb-80f7-c388d725ac45.PNG)


# Contact Us:
The user has two options to contact the support team, either by sending a feedback form or calling the support team using a regular phone call. For sending a form, the user should enter name, subject of the form, description of the form. Once the user fills the information and sends it, the feedback form will be reformed as an email message, and the user should click the send button to send it to the support team email address. For the regular phone call, the user will be directed to the phone dial with the support team phone number.

![Feedback png](https://user-images.githubusercontent.com/67188835/103526428-f5714e00-4e91-11eb-9574-b0fa04e87008.PNG)


# Real-Time Notifications:
The administrator can log in to the firebase site and send a real-time notification to all application users. To send a notification, first, the administrator should enter cloud messaging, then write the notification description, including notification title, notification text, notification image and notification name.

![firebase](https://user-images.githubusercontent.com/67188835/103525300-07ea8800-4e90-11eb-8559-95b9e3e3fe82.PNG)

# Additional Features:
The user can choose to use the application without registration. However, the user can only use maps, dialog, emergency calls, and contact us, including feedback form and regular phone call. In addition, at the beginning, the user can either choose to use the application in Arabic or English language. When the user signs up, the next time the user enters the application, he/she will be directed to the home page directly.

The application is created by: 
* Aymen Nacer
* Ahmed Alrazouq
* Naif Alobaid






  


