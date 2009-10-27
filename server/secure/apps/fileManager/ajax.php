<?
	//include_once 'include/image.php';

	$responseO = new Response($_POST['command']);

	switch($_POST['command']) {
		case 'deleteFile':
			$path1 = "upload/$userA[usr]/$_POST[filename]";
			$path2 = "upload/$userA[usr]/.$_POST[filename].jpg";

			if (!is_file($path1)) {
				$responseO->setStatus('ERR', "Can not find file $_POST[filename] for deleting.");
			} else {
				if(@unlink($path1)) {
					@unlink($path2);
					$responseO->setStatus('OK', "File $_POST[filename] deleted.");
					$responseO->set(array('filename' => $_POST['filename']));
				} else {
					$responseO->setStatus('ERR', "Could not delete $_POST[filename].");
				}
			}
			break;
			
		case 'uploadFile':
			if (!empty($_FILES['upfile']['error'])) {
				$errMsgA = Array (
					1 => 'The uploaded file exceeds the upload_max_filesize directive in php.ini',
					2 => 'The uploaded file exceeds the MAX_FILE_SIZE directive that was specified in the HTML form',
					3 => 'The uploaded file was only partially uploaded',
					4 => 'No file was uploaded.',
					6 => 'Missing a temporary folder',
					7 => 'Failed to write file to disk',
					8 => 'File upload stopped by extension'
				);
				if (isset($errMsgA[$_FILES['upfile']['error']])) {
					$responseO->setStatus('ERR', $errMsgA[$_FILES['upfile']['error']]);
				} else {
					$responseO->setStatus('ERR', 'No error code available');
				}
			} elseif (empty($_FILES['upfile']['tmp_name']) || $_FILES['upfile']['tmp_name'] == 'none') {
				$responseO->setStatus('ERR', 'No file was uploaded..');
			} else 	{
				// puts uploads in a different folder for each user
				$folder = "upload/$userA[usr]";
				if (!is_dir($folder)) {
					mkdir($folder, 0755);
				}

				$source_path = $_FILES['upfile']['tmp_name'];
				$target_path = "$folder/".$_FILES['upfile']['name'];

				if (move_uploaded_file($source_path, $target_path)) {
					$name = $_FILES['upfile']['name'];
					$size = $_FILES['upfile']['size'];
					$responseO->set(array(
						"name" => $name,
						"size" => $size,
						"html" => rawurlencode(getHTMLThumb($target_path))
					));
					
					$responseO->setStatus('OK', "File $name ($size bytes) happily saved.");
				} else {
					$responseO->setStatus('ERR', "Error saving file.");
				}
			}
			break;
	}

	print $responseO->get();
?>