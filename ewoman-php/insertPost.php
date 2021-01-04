<?php

    error_reporting(E_ALL);
    ini_set('display_errors',1);

    include('dbcon.php');

    $android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");


    if( (($_SERVER['REQUEST_METHOD'] == 'POST') && isset($_POST['submit'])) || $android ) {
	$title = $_POST['title'];
	$content = $_POST['content'];
	$email = $_POST['email'];
	$image = $_POST['image'];

	if(empty($title)) {
		$errMSG = "제목을 입력하세요.";
	}
	else if(empty($content)) {
		$errMSG = "내용을 입력하세요.";
	}
	 else if(empty($email)) {
		$errMSG = "로그인을 하셔야 합니다.";
	}

	if(!isset($errMSG)) {
		try {
			$stmt = $con->prepare('INSERT INTO post(title, content, email, image) VALUES(:title, :content, :email, :image)');
			$stmt->bindParam(':title', $title);
			$stmt->bindParam(':content', $content);
			$stmt->bindParam(':email', $email);
			$stmt->bindParam(':image', $image);

			if($stmt->execute()) {
				$successMSG = "새로운 리뷰를 추가했습니다.";
			} else {
				$errMSG = "리뷰 추가 에러";
			}

		} catch(PDOException $e) {
			die("Database error: " . $e->getMessage());
		}
	}

    }

?>


<?php
    if (isset($errMSG)) echo $errMSG;
    if (isset($successMSG)) echo $successMSG;

	$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

    if( !$android )
    {
?>
    <html>
       <body>

            <form action="<?php $_PHP_SELF ?>" method="POST">
                TITLE : <input type = "text" name = "title" />
                CONTENT : <input type = "text" name = "content" />
                EMAIL : <input type = "text" name = "email" />
                IMAGE : <input type = "text" name = "image" />
                <input type = "submit" name = "submit" />
            </form>

       </body>
    </html>

<?php
    }
?>
