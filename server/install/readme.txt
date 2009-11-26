* secure/

The folder "secure/" contains the files and folders to be placed inside a web server.
Some .htaccess files are found inside those folders. They are meant for an Apache web 
server.

* secure/sdcard/emocha/

Files inside this folder will be downloaded by the phones running eMOCHA.
There are three folders inside "secure/sdcard/emoca/training/. 

"courses/" and "lectures/" should contain pairs of files without spaces 
in the filenames in this format:
movie_about_something.mpg
movie_about_something.jpg
movie_about_something_else.mpg
movie_about_something_else.jpg
The jpg file is a thumbnail of the mp4 video.

This movies and thumbnails will be available in the phones.

"library/" should contain html files, with the "html" file extension. These
html files can not include links to css files or images, and ideally should
simple html files without much formatting (avoid wide tables for example)
so they render faster and correctly. The idea is to provide some reference
material for the phone user.

Currently all training materials can not be nested in subfolders (like
"library/treatments/a/some_treatment.html", but this will be soon fixed.

* make secure/sdcard/ writable by PHP
The "sdcard/" folder should be writable by the web server. PHP will attempt to
create there one folder for each phone user, and save inside that folder any
data sent by the phone. One way to make the folder writable is changing the 
owner of this folder (using "chown" so it's the same owner as the web server 
proccess). Making the folder writable by everyone (chmod 777) is another easy 
option, but it's not recommended because of security issues.

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