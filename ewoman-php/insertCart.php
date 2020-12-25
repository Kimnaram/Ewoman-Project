<?php

    error_reporting(E_ALL);
    ini_set('display_errors',1);

    include('dbcon.php');

    $android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");


    if( (($_SERVER['REQUEST_METHOD'] == 'POST') && isset($_POST['submit'])) || $android )
    {
	$item_no = $_POST['item_no'];
	$email = $_POST['email'];
	$count = $_POST['count'];
	$date = $_POST['date'];
	
        	if(empty($item_no)) {
		$errMSG = "상품 번호 부재";
	} else if(empty($email)){
		$errMSG = "사용자 이메일 부재";
	} else if(empty($count)) {
		$errMSG = "상품 수량 부재";
	} else if(empty($date)) {
		$errMSG = "날짜 부재";
	}

	if(!isset($errMSG))	{
		try {
			$stmt = $con->prepare('INSERT INTO cart(item_no, email, count, date) VALUES(&item_no, :email, &count, :date)');
			$stmt->bindParam(':email', $email);
			$stmt->bindParam(':date', $date);

			if($stmt->execute()) {
				$successMSG = "상품을 카트에 추가했습니다.";
			} else {
				$errMSG = "카트 추가 에러";
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
                ITEM_NO : <input type = "text" name = "item_no" />
                EMAIL : <input type = "text" name = "email" />
                COUNT : <input type = "text" name = "count" />
                DATE : <input type = "text" name = "date" />
                <input type = "submit" name = "submit" />
            </form>


       </body>
    </html>

<?php
    }
?>
