* secure/

The folder "secure/" contains the files and folders to be placed inside a web server.
Some .htaccess files are found inside those folders. They are meant for an Apache web 
server.

* secure/sdcard/

Please create a folder "sdcard" inside the "secure/" folder. This folder should be
writable by the webserver. One way to achieve this is changing the owner of this folder
(using "chown" so it's the same owner as the web server proccess). Making the folder
writable by everyone (chmod 777) is another easy option, but it's not secure for live 
servers and not recommended.

* config/google_maps_key

Please add a file called "google_maps_key" inside the "config/" folder.
It should contain a key for Google Maps that allows you to display maps in the
domain you are using eMOCHA. The file should contain no spaces or line breaks,
just the string of characters. You can apply for a key here:
http://code.google.com/apis/maps/signup.html

* database/

The "database/" folder contains the SQL files, meant for a MySQL database.
They can be read using phpMyAdmin. 

* sys_user table
At least one user must be added to the "sys_user" table, to be able to log 
in into the website. The SQL files are early stage and probably will suffer 
big changes.