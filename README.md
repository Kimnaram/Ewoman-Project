# ewoman
personal project <br />
- email address : snrneh3@naver.com <br />
<!-- - Demo Video :  <br /> -->
- blog : https://se0r1-tae27.tistory.com/ <br />

<!-- ## Introduction
This is an Android application for people who want to travel to Korea and have difficulty making travel plans.
1. It shows the hotel information of the desired travel area.
2. It shows the tourist attraction information of the desired travel area.
3. It shows various festival information.
4. You can write a review of the area you traveled to.
5. You can manage your budget while traveling. -->

## Development Environment
- MySQL @5.7.32
- Apache2 Server @2.4.29
- php @5.6.40
- Android Studio @3.5.3

## Application Version
- minSdkVersion : 21
- targetSdkVersion : 29

## APIs
- Naver Mobile Dynamic Map <br />
If you want to show the location of a particular place on the map, sign up to naver cloud platform and get own key. <br />
<!-- - Google Directions API <br />
If you want to get traffic information, sign up to google cloud platform and get own key. <br />
- Google Maps SDK for Android <br />
If you want to get locations of hotels and tourist attractions, sign up to google cloud platform and get own key. -->

<!-- ## Database table information
```
CREATE TABLE user (
email     varchar(100) PRIMARY KEY,
password  varchar(15) NOT NULL,
name      varchar(10) NOT NULL,
gender    varchar(6),
phone     varchar(11) NOT NULL
);

CREATE TABLE item (
item_no           int(12) PRIMARY KEY AUTO_INCREMENT,
category          varchar(10) NOT NULL, 
name              varchar(50) NOT NULL,
price             int(10) NOT NULL,
image             longblob NOT NULL,
inform            varchar(500),
deliv_method      varchar(20),
deliv_price       int(5),
deliv_inform      varchar(30),
minimum_quantity  int(5),
maximum_quantity  int(5)
);

CREATE TABLE class (
item_no          int(12) NOT NULL,
name             varchar(100) NOT NULL,
price            int(10) NOT NULL,
PRIMARY KEY(item_no, name),
FOREIGN KEY (item_no) REFERENCES item(item_no) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE cart (
item_no         int(12),
email           varchar(100),
count           int(5) NOT NULL,
date            date NOT NULL,
PRIMARY KEY(item_no, email),
FOREIGN KEY (item_no) REFERENCES item(item_no) ON DELETE CASCADE ON UPDATE CASCADE,
FOREIGN KEY (email) REFERENCES user(email) ON DELETE CASCADE ON UPDATE CASCADE
);
```
-->

<!-- ## screenshot
<p align="center">
<img src="https://user-images.githubusercontent.com/32188154/102354332-c59afd00-3fed-11eb-9b09-1d6102c82501.png" width="240px" height="440px" title="Main" alt="Main" margin="auto"></img></p>  

<img src="https://user-images.githubusercontent.com/32188154/102350837-ddbc4d80-3fe8-11eb-8de1-75eb1822aac0.png" width="180px" height="320px" title="Location" alt="Location"></img>
<img src="https://user-images.githubusercontent.com/32188154/102350878-ef9df080-3fe8-11eb-94a2-0fc7184cff91.png" width="180px" height="320px" title="Hotel" alt="Hotel"></img>
<img src="https://user-images.githubusercontent.com/32188154/102353553-96d05700-3fec-11eb-8613-52e720fa3fa9.jpg" width="180px" height="320px" title="Course" alt="Course"></img>
<img src="https://user-images.githubusercontent.com/32188154/102353874-0cd4be00-3fed-11eb-84bd-e1f4e7a7328d.png" width="180px" height="320px" title="Traffic" alt="Traffic"></img>
<img src="https://user-images.githubusercontent.com/32188154/102353181-11e53d80-3fec-11eb-8d93-ae025cb5e687.jpg" width="180px" height="320px" title="Review" alt="Review"></img>
<img src="https://user-images.githubusercontent.com/32188154/102352730-7a7fea80-3feb-11eb-8f45-e31744f540ea.png" width="180px" height="320px" title="Review Detail" alt="Review Detail"></img>
<img src="https://user-images.githubusercontent.com/32188154/102350110-bdd85a00-3fe7-11eb-8bee-8368ad4be007.png" width="180px" height="320px" title="Budget" alt="Budget"></img>
<img src="https://user-images.githubusercontent.com/32188154/102347144-51f3f280-3fe3-11eb-9c85-df0b9a40f342.png" width="180px" height="320px" title="Festival" alt="Festival"></img> -->

<!--
<img src="https://user-images.githubusercontent.com/32188154/102350886-f167b400-3fe8-11eb-91b1-bd9bb2f4463c.png" width="140px" height="260px" title="Hotel Room" alt="Hotel Room"></img>
<img src="https://user-images.githubusercontent.com/32188154/102353567-9c2da180-3fec-11eb-812b-dc17ec1eba3a.png" width="180px" height="320px" title="Plan" alt="Plan"></img>
-->

<!-- ## License
MoveItMovie is released under the MIT License. http://www.opensource.org/licenses/mit-license -->
