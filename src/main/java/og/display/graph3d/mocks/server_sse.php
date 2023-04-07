<?php
date_default_timezone_set("America/New_York");
header("Cache-Control: no-store");
header("Content-Type: text/event-stream");
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Headers: Origin, X-Requested-With, Content-Type, Accept');
header('Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS');



// $filename = "message.txt";
// $file = fopen($filename, "r");
// $filetext = fread($file, $filesize);
// fclose($file);
// echo $filetext;
// ob_end_flush();
// flush();

while(true){
    $myfile = fopen("message.txt", "r") or die("Unable to open file!");
echo fread($myfile,filesize("message.txt"));
fclose($myfile);
flush();    
sleep(1);
}

// $counter = rand(1, 10);
// while (true) {
//   // Every second, send a "ping" event.

//   echo "event: ping\n";
//   $curDate = date(DATE_ISO8601);
//   echo 'data: {"time": "' . $curDate . '"}';
//   echo "\n\n";

//   // Send a simple message at random intervals.

//   $counter--;

//   if (!$counter) {
//     echo 'data: This is a message at time ' . $curDate . "\n\n";
//     $counter = rand(1, 10);
//   }

//   ob_end_flush();
//   flush();

//   // Break the loop if the client aborted the connection (closed the page)

//   if (connection_aborted()) break;

//   sleep(1);
// }